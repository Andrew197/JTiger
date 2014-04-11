package Translate;

import Tree.*;
import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp 
{
  //globals
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();

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
    JUMP jump = (JUMP)stm;
    if(jump.exp instanceof NAME)
    {
      NAME name = (NAME)jump.exp;
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
      ESEQ retVal;
      retVal = new ESEQ(new SEQ(new SEQ(cond.unCx(t, f), new SEQ(new SEQ(new LABEL(t), new SEQ(new MOVE(new TEMP(r), aExp), new JUMP(join))), new SEQ(new LABEL(f), new SEQ(new MOVE(new TEMP(r), bExp), new JUMP(join))))), new LABEL(join)), new TEMP(r));
      return retVal;
    }
  }

  Tree.Stm unNx() 
  {
    Stm aStm, bStm;

    aStm = a.unNx();
    bStm = b.unNx();

    if (aStm == null) t = join;
    else aStm = new SEQ(new SEQ(new LABEL(t), aStm), new JUMP(join));

    if  (bStm == null) f = join;
    else bStm = new SEQ(new SEQ(new LABEL(f), bStm), new JUMP(join));
  
    if(aStm == null && bStm == null) return cond.unNx();
    else
    {
      Stm stm = cond.unCx(t, f);
      if(aStm == null)       return new SEQ(new SEQ(stm, bStm), new LABEL(join));
      else if(bStm == null)  return new SEQ(new SEQ(stm, aStm), new LABEL(join));
      else                   return new SEQ(new SEQ(stm, new SEQ(aStm, bStm)), new LABEL(join));
    }
  }
}
