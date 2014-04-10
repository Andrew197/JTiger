package Translate;

import Tree.*;
import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp 
{

  private void debugPrint(int depth, Object sender, String message) 
  {
    if (debug) 
    {
      if (sender == null)                  System.out.println(Integer.toString(depth) + ": null -> " + message);
      if (!(sender instanceof FindEscape)) System.out.println(Integer.toString(depth) + ": " + sender.getClass().toString().substring(6) + "-> " + message);
      else                                 System.out.println("\n*************** " + message.toUpperCase() + " ***************");
    }
  }

  //globals
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();

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

    //what?
    //return new Tree.CONST(0);
  }

  Tree.Stm unNx() 
  {
    // You must implement this function
    return null;
  }
}
