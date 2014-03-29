package FindEscape;

import Absyn.*;

public class FindEscape 
{
	Symbol.Table	escEnv	= new Symbol.Table();	// escEnv maps Symbol to Escape
	static final boolean debug = true;

	public FindEscape(Exp e) {
		traverseExp(0, e);
	}

	/**
	 * Every project I've ever done for these classes benefitted enormously from this method. We pass a message and an object (for clarity.) If the messages
	 * comes from the containing class, we'll draw a big section marker with the message.
	 */
	private void debugPrint(Object sender, String message) 
	{
		if (debug) {
			if (sender == null) {
				System.out.println("null -> " + message); // Null ptr exeception protection.
			}
			if (!(sender instanceof FindEscape)) {
				System.out.println(sender.getClass().toString().substring(6) + "-> " + message);
			}
			else {
				System.out.println("\n*************** " + message.toUpperCase() + " ***************");
			}
		}
	}

	/**
	 * acts as a switch statement for subclasses of Absyn.Var
	 */
	void traverseVar(int depth, Var v) {
		if (v instanceof SimpleVar)
			traverseVar(depth, (SimpleVar) v);
		else if (v instanceof FieldVar)
			traverseVar(depth, (FieldVar) v);
		else if (v instanceof SubscriptVar)
			traverseVar(depth, (SubscriptVar) v);
		else
			debugPrint(v, "Is not a subclass of Var, no method to handle.");
	}
	
	void traverseVar(int depth, SimpleVar v) {}
	void traverseVar(int depth, FieldVar v) {}
	void traverseVar(int depth, SubscriptVar v) {}

	/**
	 * acts as a switch statement for subclasses of Absyn.Exp
	 */
	void traverseExp(int depth, Exp e) {
		if (e instanceof VarExp)
			traverseExp(depth, (VarExp) e);
		else if (e instanceof CallExp)
			traverseExp(depth, (CallExp) e);
		else if (e instanceof OpExp)
			traverseExp(depth, (OpExp) e);
		else if (e instanceof RecordExp)
			traverseExp(depth, (RecordExp) e);
		else if (e instanceof SeqExp)
			traverseExp(depth, (SeqExp) e);
		else if (e instanceof AssignExp)
			traverseExp(depth, (AssignExp) e);
		else if (e instanceof IfExp)
			traverseExp(depth, (IfExp) e);
		else if (e instanceof WhileExp)
			traverseExp(depth, (WhileExp) e);
		else if (e instanceof ForExp)
			traverseExp(depth, (ForExp) e);
		else if (e instanceof LetExp)
			traverseExp(depth, (LetExp) e);
		else if (e instanceof ArrayExp)
			traverseExp(depth, (ArrayExp) e);
		else
			debugPrint(e, "Is not a subclass of Exp, no method to handle.");
	}
	
	void traverseExp(int depth, VarExp e) 
	{
		debugPrint(e, "I'm a VarExp!");
		traverseVar(depth, e.var);
	}
	void traverseExp(int depth, CallExp e) {}

	void traverseExp(int depth, OpExp e) 
	{
		//check both left and right operands
		traverseExp(depth, e.left);
        traverseExp(depth, e.right);
	}

	void traverseExp(int depth, RecordExp e) {}
	void traverseExp(int depth, SeqExp e) {}
	void traverseExp(int depth, AssignExp e) {}
	
	void traverseExp(int depth, IfExp e) 
	{
		debugPrint(this, "traversing an ifexp");

		traverseExp(depth, e.test);
        traverseExp(depth, e.thenclause);
        traverseExp(depth, e.elseclause);
        debugPrint(this, "done traversing an ifexp");
	}

	void traverseExp(int depth, WhileExp e) {}

	void traverseExp(int depth, ForExp e) 
	{
	}
	void traverseExp(int depth, LetExp e) {}
	void traverseExp(int depth, ArrayExp e) 
	{
		//something seems off with this
        traverseExp(depth, e.size);
	}
	

	/**
	 * acts as a switch statement for subclasses of Absyn.Dec
	 */
	void traverseDec(int depth, Dec d) {
		//what type of dec is this?
		if (d instanceof VarDec)
			traverseDec(depth, (VarDec) d);
		else if (d instanceof FunctionDec)
			traverseDec(depth, (FunctionDec) d);
		else
			debugPrint(d, "Is not a subclass of Dec, no method to handle.");
	}
	
	void traverseExp(int depth, VarDec d) {}
	void traverseExp(int depth, FunctionDec d) {}
}