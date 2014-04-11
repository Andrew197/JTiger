package Translate;

import Symbol.Symbol;
import Tree.BINOP;
import Tree.CJUMP;
import Temp.Temp;
import Temp.Label;

import Absyn.OpExp;
import Frame.*;
import Tree.*;
import java.util.Hashtable;

public class Translate 
{
  public Frame.Frame frame;
  public Translate(Frame.Frame f) { frame = f; }
  private Frag frags;

  //debug tools
  private void debugPrint(int depth, Object sender, String message) 
  {
    if (debug) 
    {
      if (sender == null)                  System.out.println(": null -> " + message);
      if (!(sender instanceof Translate))  System.out.println(": " + sender.getClass().toString().substring(6) + "-> " + message);
      else                                 System.out.println("\n*************** " + message.toUpperCase() + " ***************");
    }
  }

  //Holy One Liners, batman!
  public Frag getResult() { return frags; }
  private static Tree.Exp CONST(int value)                                           { return new Tree.CONST(value); }
  private static Tree.Exp NAME(Label label)                                          { return new Tree.NAME(label);  }
  private static Tree.Exp TEMP(Temp temp)                                            { return new Tree.TEMP(temp);   }
  private static Tree.Exp MEM(Tree.Exp exp)                                          { return new Tree.MEM(exp);     }
  private static Tree.Stm UEXP(Tree.Exp exp)                                         { return new Tree.UEXP(exp);    }
  private static Tree.Stm JUMP(Label target)                                         { return new Tree.JUMP(target); }
  private static Tree.Stm LABEL(Label label)                                         { return new Tree.LABEL(label); }
  public  Exp NilExp()                                                               { return new Ex(CONST(0)); }
  public  Exp IntExp(int value)                                                      { return new Ex(CONST(value)); }
  public  Exp AssignExp(Exp lhs, Exp rhs)                                            { return new Nx(MOVE(lhs.unEx(), rhs.unEx())); }
  public  Exp BreakExp(Label done)                                                   { return new Nx(JUMP(done)); }
  public  Exp IfExp(Exp cc, Exp aa, Exp bb)                                          { return new IfThenElseExp(cc, aa, bb); }
  public  Exp TypeDec()                                                              { return new Nx(null); }
  public  Exp FunctionDec()                                                          { return new Nx(null); }
  public  Exp Error()                                                                { return new Ex(CONST(0)); }
  public  Exp FunExp(Symbol f, ExpList args, Level from)                             { return new Ex(CallExp(f, args, from)); }
  public  Exp FunExp(Level f, ExpList args, Level from)                              { return new Ex(CallExp(f, args, from)); }
  public  Exp ProcExp(Symbol f, ExpList args, Level from)                            { return new Nx(UEXP(CallExp(f, args, from)));}
  public  Exp ProcExp(Level f, ExpList args, Level from)                             { return new Nx(UEXP(CallExp(f, args, from))); }
  private Exp CallExp(Symbol f, ExpList args, Level from)                            { return frame.externalCall(f.toString(), ExpList(args)); }
  private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args)                     { return new Tree.CALL(func, args); }
  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src)                           { return new Tree.MOVE(dst, src); }
  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right)            { return new Tree.BINOP(binop, left, right); }
  private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) { return new Tree.CJUMP(relop, l, r, t, f); }
  private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail)              { return new Tree.ExpList(head, tail); }
  private static Tree.ExpList ExpList(Tree.Exp head)                                 { return ExpList(head, null); }

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
  
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) 
  {
    if (stm == null) return exp;
    else             return new Tree.ESEQ(stm, exp);
  }

  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) 
  {
    if      (left  == null)      return right;
    else if (right == null)      return left;
    else                         return new Tree.SEQ(left, right);
  }

  private static Tree.ExpList ExpList(ExpList exp) 
  {
    if (exp == null) return null;
    else             return ExpList(exp.head.unEx(), ExpList(exp.tail));
  }

  private java.util.Hashtable strings = new java.util.Hashtable();
  public Exp StringExp(String lit) 
  {
    String u = lit.intern();
    Label lab = (Label)strings.get(u);
    if (lab == null) 
    {
      lab = new Label();
      strings.put(u, lab);
      DataFrag frag = new DataFrag(frame.string(lab, u));
      frag.next = frags;
      frags = frag;
    }
    return new Ex(NAME(lab));
  }

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

  private Stm recordExp2(Temp temporary, Translate.ExpList recExp, int wordSize)
  {
      if(init != null)  return SEQ(MOVE(MEM(BINOP(0, TEMP(temporary), CONST(i))), recExp.head.unEx()), recordExp2(temporary, wordSize, recExp.tail, wordSize));
      else              return null;        
  }

  public Exp RecordExp(ExpList init)
  {
    int expSize = 0;
    for(Translate.ExpList exp = init; exp != null; exp = exp.tail) expSize++;

    Temp temporary = new Temp();
    return new Ex(ESEQ(SEQ(MOVE(TEMP(temporary), frame.externalCall("allocRecord", ExpList(CONST(expSize)))), recordExp2(temporary, init, frame.wordSize())), TEMP(temporary)));
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

  public Exp WhileExp(Exp test, Exp body, Label done) 
  {
    Label lab1 = new Label();
    Label lab2 = new Label();
    Nx retVal = new Nx(SEQ(SEQ(SEQ(LABEL(lab1), test.unCx(lab2, done)), SEQ(SEQ(LABEL(lab2), body.unNx()), JUMP(lab1))), LABEL(done)));  
    return retVal;
  }

  public Exp ArrayExp(Exp size, Exp init) 
  {
    Ex arrayEx;
    arrayEx = new Ex(frame.externalCall("initArray", ExpList(size.unEx(), ExpList(init.unEx()))));
    return arrayEx;
  }

  public Exp VarDec(Access a, Exp init) 
  {
    Nx vDec;
    vDec = new Nx(MOVE(a.acc.exp(TEMP(a.home.frame.FP())), init.unEx()));
    return vDec;
  }

  public Exp SimpleVar(Access access, Level level) 
  {
    Exp framePtr = TEMP(level.frame.FP());
    for(Level l = level; l != access.home; l = l.parent) framePtr = l.frame.formals.head.exp(framePtr);
    return new Ex(access.acc.exp(framePtr));
  }

  private Tree.Exp CallExp(Level f, ExpList args, Level from) 
  {
    Exp framePtr = TEMP(from.frame.FP());
    if(f.parent != from)
    {
      for(Level l = from; l != f.parent; l = l.parent) framePtr = l.frame.formals.head.exp(framePtr);
    }
    return CALL(NAME(f.frame.name), ExpList(framePtr, ExpList(args)));
  }

  public Exp SubscriptVar(Exp array, Exp index) 
  {
    int wordSize = frame.wordSize();
    Label badFrame = frame.badSub();

    Label lab1 = new Label();
    Label lab2 = new Label();

    //temporaries
    Temp t1 = new Temp();
    Temp t2 = new Temp();

    //return the IC
    Ex retCode;
    retCode = new Ex(ESEQ(SEQ(MOVE(TEMP(t1), array.unEx()), SEQ(MOVE(TEMP(t2), index.unEx()), SEQ(CJUMP(2, TEMP(t2), CONST(0), badFrame, lab2), SEQ(LABEL(lab2), SEQ(CJUMP(3, TEMP(t2), MEM(BINOP(0, TEMP(t1), CONST(-wordSize))), badFrame, lab1), LABEL(lab1)))))), MEM(BINOP(0, TEMP(t1), BINOP(2, TEMP(t2), CONST(wordSize))))));
    return retCode;
  }

  public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done) 
  {
    Temp temporary = new Temp();
    Temp framePtr = i.home.frame.FP();
    Label b = new Label();
    Label inc = new Label();
    
    Nx retVal;
    retVal = new Nx(SEQ(SEQ(SEQ(SEQ(MOVE(i.acc.exp(TEMP(framePtr)), lo.unEx()), MOVE(TEMP(temporary), hi.unEx())), CJUMP(4, i.acc.exp(TEMP(framePtr)), TEMP(temporary), b, done)), SEQ(SEQ(SEQ(LABEL(b), body.unNx()), CJUMP(2, i.acc.exp(TEMP(framePtr)), TEMP(temporary), inc, done)), SEQ(SEQ(LABEL(inc), MOVE(i.acc.exp(TEMP(framePtr)), BINOP(0, i.acc.exp(TEMP(framePtr)), CONST(1)))), JUMP(b)))), LABEL(done)));
    return retVal;
  }

  public Exp FieldVar(Exp record, int index) 
  {
    //adjust the index first
    index = index * frame.wordSize();

    //handle the good and bad ptrs
    Label badPtr = frame.badPtr();
    Label goodPtr = new Label();

    //new temporary
    Temp temporary = new Temp();

    //return it!
    Ex retVal;
    retVal = new Ex(ESEQ(SEQ(MOVE(TEMP(temporary), record.unEx()), SEQ(CJUMP(0, TEMP(temporary), CONST(0), badPtr, goodPtr), LABEL(goodPtr))), MEM(BINOP(0, TEMP(temporary), CONST(index)))));
    return retVal;
  }

  public Exp StrOpExp(int op, Exp left, Exp right) 
  {
    Exp cmp = frame.externalCall("strcmp", ExpList(left.unEx(), ExpList(right.unEx())));
    switch(op)
    {
      case 4: return new RelCx(0, cmp, CONST(0));
      case 5: return new RelCx(1, cmp, CONST(0));
      case 6: return new RelCx(2, cmp, CONST(0));
      case 7: return new RelCx(4, cmp, CONST(0));
      case 8: return new RelCx(3, cmp, CONST(0));
      case 9: return new RelCx(5, cmp, CONST(0));
    }
    throw new Error("Str Op Exp Error!!");
  }

  public Exp LetExp(ExpList lets, Exp body) 
  {
    Stm newStm = null;
    for(Translate.ExpList expList = lets; expList != null; expList = expList.tail) stm = SEQ(newStm, expList.head.unNx());

    Exp result = body.unEx();
    if(result != null) return new Ex(ESEQ(stm, result)); 
    else               return new Nx(SEQ(stm, body.unNx()));
  }
}