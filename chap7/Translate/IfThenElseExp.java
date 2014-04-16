package Translate;

import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp 
{
  //globals
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();
  private boolean debug = false;

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

  //constructor
  IfThenElseExp(Exp cc, Exp aa, Exp bb) 
  {
    cond = cc; 
    a = aa; 
    b = bb;
  }

  Tree.Stm fixJump(Tree.Stm stm, int tb)
  {
    Tree.JUMP jump = (Tree.JUMP)stm;
    if(jump.exp instanceof Tree.NAME)
    {
      Tree.NAME name = (Tree.NAME)jump.exp;
      stm = null;
      if (tb == 1) t = name.label;
      else         f = name.label;
    }
    return stm;  
  }

  Tree.Stm unCx(Label tt, Label ff) 
  {
    Tree.Stm aStm = a.unCx(tt, ff);
    Tree.Stm bStm = b.unCx(tt, ff);
    if(aStm instanceof JUMP) aStm = fixJump(aStm, 1);
    if(bStm instanceof JUMP) bStm = fixJump(bStm, 0);

    Tree.Stm condStm = cond.unCx(t, f);
    if (aStm == null && bStm == null) return condStm;
    if (aStm == null)                 return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(f), bStm));
    if (bStm == null)                 return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(t), aStm));
    return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm), new Tree.SEQ(new Tree.LABEL(f), bStm)));
  }

  Tree.Exp unEx() 
  {
    Exp aExp = a.unEx();
    if(aExp == null) return null;

    Exp bExp = b.unEx();
    if(bExp == null) return null;
    else 
    {
      //nothing's null, we're good here.
      Temp temporary;
      Tree.ESEQ retVal;
      retVal = new Tree.ESEQ(new Tree.SEQ(new Tree.SEQ(cond.unCx(t, f), new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(temporary), aExp), new Tree.JUMP(join))), new Tree.SEQ(new Tree.LABEL(f), new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(temporary), bExp), new Tree.JUMP(join))))), new Tree.LABEL(join)), new Tree.TEMP(temporary));
      return retVal;
    }
  }

  Tree.Stm unNx() 
  {
    Tree.Stm aStm, bStm;

    aStm = a.unNx();
    bStm = b.unNx();

    if (aStm == null) t = join;
    else aStm = new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm), new Tree.JUMP(join));

    if  (bStm == null) f = join;
    else bStm = new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(f), bStm), new Tree.JUMP(join));
  
    if(aStm == null && bStm == null) return cond.unNx();
    else
    {
      Tree.Stm stm = cond.unCx(t, f);
      if(aStm == null)       return new Tree.SEQ(new Tree.SEQ(stm, bStm), new Tree.LABEL(join));
      else if(bStm == null)  return new Tree.SEQ(new Tree.SEQ(stm, aStm), new Tree.LABEL(join));
      else                   return new Tree.SEQ(new Tree.SEQ(stm, new Tree.SEQ(aStm, bStm)), new Tree.LABEL(join));
    }
  }
}
