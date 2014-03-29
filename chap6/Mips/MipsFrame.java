package Mips;

import java.util.Hashtable;
import Symbol.Symbol;
import Temp.Temp;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import Frame.AccessList;

public class MipsFrame extends Frame 
{
  //class fields
  private int count = 0;
  private int formalOffset;
  private static final int wordSize = 4;

  public Access allocLocal(boolean varEscaped) 
    {
      if (!varEscaped) 
        {
          //refer to a new temporary
          return new InReg(new Temp());
        }
      else
      {
        //found an escaper, so we'll use an inFrame instead
        formalOffset -= 4;
        return new InFrame(formalOffset);
      }
    }  

  //should be complete
  public Frame newFrame(Symbol name, Util.BoolList formals) 
  {
    Label label;

    if (name == null)             label = new Label();
    else if (this.name != null)   label = new Label(this.name + "." + name + "." + count++);
    else                          label = new Label(name);
    
    return new MipsFrame(label, formals);
  }

  public MipsFrame() 
  {
    count = 0;
    formalOffset = 0;
  }
  
  //fron Frame.java: formals is type AccessList
  private AccessList allocateFormalParameters(int offset, BoolList formals)
    {
        Access al;
        if(formals == null) return null;

        //nothing escaped
        if(formals.head)    al = new InFrame(offset);

        //escaped case
        else                al = new InReg(new Temp());

        return new AccessList(al, allocateFormalParameters(offset + 4, formals.tail));
    }


  private MipsFrame(Label n, Util.BoolList f) 
  {
    formalOffset = 0;
    name = n;
    count = 0;

    //allocate the formals in Frame
    super.formals = allocateFormalParameters(0, f);
  }

  
  
  public int wordSize() { return wordSize; }

}
