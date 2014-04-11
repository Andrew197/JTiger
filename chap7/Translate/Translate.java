package Translate;

import Symbol.Symbol;
import Tree.BINOP;
import Tree.CJUMP;
import Temp.Temp;
import Temp.Label;

public class Translate 
{
  public Frame.Frame frame;
  public Translate(Frame.Frame f) { frame = f; }
  private Frag frags;

  public void procEntryExit(Level level, Exp body) 
  {
    Frame.Frame myframe = level.frame;
    Tree.Exp bodyExp = body.unEx();
    Tree.Stm bodyStm;

    if (bodyExp != null)  bodyStm = MOVE(TEMP(myframe.RV()), bodyExp);
    else                  bodyStm = body.unNx();

    ProcFrag frag = new ProcFrag(myframe.procEntryExit1(bodyStm), myframe);
    frag.next = frags;
    frags = frag;
  }

  public Frag getResult() { return frags; }

  private static Tree.Exp CONST(int value) 
  {
    return new Tree.CONST(value);
  }
  private static Tree.Exp NAME(Label label) {
    return new Tree.NAME(label);
  }
  private static Tree.Exp TEMP(Temp temp) {
    return new Tree.TEMP(temp);
  }

  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) 
  {
    return new Tree.BINOP(binop, left, right);
  }
  private static Tree.Exp MEM(Tree.Exp exp) {
    return new Tree.MEM(exp);
  }
  private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) { return new Tree.CALL(func, args); }
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
    if (stm == null)
      return exp;
    return new Tree.ESEQ(stm, exp);
  }

  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) { return new Tree.MOVE(dst, src); }
  private static Tree.Stm UEXP(Tree.Exp exp) { return new Tree.UEXP(exp); }
  private static Tree.Stm JUMP(Label target) { return new Tree.JUMP(target); }
  private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) { return new Tree.CJUMP(relop, l, r, t, f); }

  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) 
  {
    if      (left  == null)      return right;
    else if (right == null)      return left;
    else                         return new Tree.SEQ(left, right);
  }
  private static Tree.Stm LABEL(Label label) {
    return new Tree.LABEL(label);
  }

  private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
    return new Tree.ExpList(head, tail);
  }
  private static Tree.ExpList ExpList(Tree.Exp head) {
    return ExpList(head, null);
  }
  private static Tree.ExpList ExpList(ExpList exp) {
    if (exp == null)
      return null;
    return ExpList(exp.head.unEx(), ExpList(exp.tail));
  }

  public Exp Error() {
    return new Ex(CONST(0));
  }

  public Exp SimpleVar(Access access, Level level) {
    return Error();
  }

  public Exp FieldVar(Exp record, int index) 
  {
    return Error();
  }

  public Exp SubscriptVar(Exp array, Exp index) {
    return Error();
  }

  public Exp NilExp() { return new Ex(CONST(0)); }


  public Exp IntExp(int value) 
  {
    return new Ex(CONST(value));
  }

  private java.util.Hashtable strings = new java.util.Hashtable();
  public Exp StringExp(String lit) {
    String u = lit.intern();
    Label lab = (Label)strings.get(u);
    if (lab == null) {
      lab = new Label();
      strings.put(u, lab);
      DataFrag frag = new DataFrag(frame.string(lab, u));
      frag.next = frags;
      frags = frag;
    }
    return new Ex(NAME(lab));
  }

  private Tree.Exp CallExp(Symbol f, ExpList args, Level from) {
    return frame.externalCall(f.toString(), ExpList(args));
  }
  private Tree.Exp CallExp(Level f, ExpList args, Level from) {
    throw new Error("Translate.CallExp unimplemented");
  }

  public Exp FunExp(Symbol f, ExpList args, Level from) { return new Ex(CallExp(f, args, from)); }
  public Exp FunExp(Level f, ExpList args, Level from) { return new Ex(CallExp(f, args, from)); }
  public Exp ProcExp(Symbol f, ExpList args, Level from) { return new Nx(UEXP(CallExp(f, args, from)));}
  public Exp ProcExp(Level f, ExpList args, Level from) { return new Nx(UEXP(CallExp(f, args, from))); }
  
  public Exp OpExp(int op, Exp left, Exp right) 
  {
    switch(op)
    {
      case 0: return new Ex(BINOP(0, left.unEx(), right.unEx()));
      case 1: return new Ex(BINOP(1, left.unEx(), right.unEx()));
      case 2: return new Ex(BINOP(2, left.unEx(), right.unEx()));
      case 3: return new Ex(BINOP(3, left.unEx(), right.unEx()));
      case 4: return new RelCx(0,    left.unEx(), right.unEx());
      case 5: return new RelCx(1,    left.unEx(), right.unEx());
      case 6: return new RelCx(2,    left.unEx(), right.unEx());
      case 7: return new RelCx(4,    left.unEx(), right.unEx());
      case 8: return new RelCx(3,    left.unEx(), right.unEx());
      case 9: return new RelCx(5,    left.unEx(), right.unEx());
    }
  }

  public Exp StrOpExp(int op, Exp left, Exp right) {
    return Error();
  }

  public Exp RecordExp(ExpList init)
  {
    int expSize = 0;
    for(Translate.ExpList exp = init; exp != null; exp = exp.tail) expSize++;

    Temp temporary = new Temp();
    return new Ex(ESEQ(SEQ(MOVE(TEMP(temporary), frame.externalCall("allocRecord", ExpList(CONST(expSize)))), initRecord(temporary, 0, init, frame.wordSize())), TEMP(temporary)));
  }

  public Exp SeqExp(ExpList e) 
  {
    if (e != null)
    {
      Stm stm = null;
      for(; e.tail != null; e = e.tail) stm = SEQ(stm, e.head.unNx());

      Exp retVal = e.head.unEx();
      if(retVal != null) return new Ex(ESEQ(stm, retVal));
      else return new Nx(SEQ(stm, e.head.unNx()));
    }
    else return new Nx(null);
  }

  public Exp AssignExp(Exp lhs, Exp rhs) 
  {
    return new Nx(MOVE(lhs.unEx(), rhs.unEx()));
  }

  public Exp IfExp(Exp cc, Exp aa, Exp bb) 
  {
    return new IfThenElseExp(cc, aa, bb);
  }

  public Exp WhileExp(Exp test, Exp body, Label done) 
  {
    Label lab1 = new Label();
    Label lab2 = new Label();
    Nx retVal = new Nx(SEQ(SEQ(SEQ(LABEL(lab1), test.unCx(lab2, done)), SEQ(SEQ(LABEL(lab2), body.unNx()), JUMP(lab1))), LABEL(done)));  
    return retVal;
  }

  public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done) {
    return Error();
  }

  public Exp BreakExp(Label done) { return new Nx(JUMP(done)); }

  public Exp LetExp(ExpList lets, Exp body) {
    return Error();
  }

  public Exp ArrayExp(Exp size, Exp init) 
  {
    Ex arrayEx;
    arrayEx = new Ex(frame.externalCall("initArray", ExpList(size.unEx(), ExpList(init.unEx()))));
    return arrayEx;
  }

  public Exp VarDec(Access a, Exp init) {
    return Error();
  }

  public Exp TypeDec() {
    return new Nx(null);
  }

  public Exp FunctionDec() 
  {
    return new Nx(null);
  }
}
