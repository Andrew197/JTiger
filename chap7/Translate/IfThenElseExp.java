package Translate;

import Tree.*;
import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp 
{
  /**
   * Every project I've ever done for these classes benefitted enormously from this method. We pass a message and an object (for clarity.) If the messages
   * comes from the containing class, we'll draw a big section marker with the message.
   */
  private void debugPrint(int depth, Object sender, String message) 
  {
    if (debug) 
    {
      if (sender == null)                  System.out.println(Integer.toString(depth) + ": null -> " + message);
      if (!(sender instanceof FindEscape)) System.out.println(Integer.toString(depth) + ": " + sender.getClass().toString().substring(6) + "-> " + message);
      else                                 System.out.println("\n*************** " + message.toUpperCase() + " ***************");
    }
  }


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

  Tree.Stm unCx(Label tt, Label ff) 
  {
    Tree.Stm aStm = a.unCx(tt, ff);
    Tree.Stm bStm = b.unCx(tt, ff);
    Tree.Stm condStm = cond.unCx(t, f);

    if(aStm instanceof JUMP)
    {
        JUMP aJump = (JUMP)aStm;
        if(aJump.exp instanceof NAME)
        {
            NAME aName = (NAME)aJump.exp;
            aStm = null;
            t = aName.label;
        }
    }

    if (aStm == null && bStm == null) return condStm;
    if (aStm == null)                 return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(f), bStm));
    if (bStm == null)                 return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(t), aStm));
    return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm), new Tree.SEQ(new Tree.LABEL(f), bStm)));
  }

  Tree.Exp unEx() 
  {
    // You must implement this function
    return new Tree.CONST(0);
  }

  Tree.Stm unNx() 
  {
    // You must implement this function
    return null;
  }
}
