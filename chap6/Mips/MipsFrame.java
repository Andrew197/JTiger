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
  private static final int wordSize = 4;
  
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
  }
  
  private MipsFrame(Label n, Util.BoolList f) 
  {
    name = n;
    count = 0;
  }

  
  
  public int wordSize() { return wordSize; }
  public Access allocLocal(boolean escape) { return null; }
}
