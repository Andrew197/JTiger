package FindEscape;

import Absyn.*;

/**
 * From the paper Escape Analysis in the Context of Dynamic Compilation and Deoptimization, a function or value is said to escape if "the method or thread in
 * which it was created if it can also be accessed by other methods or threads." This is what this class is built to determine.
 */
public class FindEscape {
	Symbol.Table				escEnv		= new Symbol.Table();
	private FunctionDec			currentFun	= null;						// We have to store the current function we're examining from the global sense
	privates static final boolean	debug		= true;					// we'll make this public so that it's visible from everywhere.

	public FindEscape(Exp e) {
		debugPrint(0, this, "begin!");
		traverseExp(0, e);
	}

	/**
	 * Every project I've ever done for these classes benefitted enormously from this method. We pass a message and an object (for clarity.) If the messages
	 * comes from the containing class, we'll draw a big section marker with the message.
	 */
	private void debugPrint(int depth, Object sender, String message) {
		if (debug) {
			if (sender == null) {
				System.out.println(Integer.toString(depth) + ": null -> " + message); // Null ptr exeception protection.
			}
			if (!(sender instanceof FindEscape)) {
				System.out.println(Integer.toString(depth) + ": " + sender.getClass().toString().substring(6) + "-> "
						+ message);
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
		debugPrint(depth, this, "beginning traverseVar.");
		if (v instanceof SimpleVar)
			traverseVar(depth, (SimpleVar) v);
		else if (v instanceof FieldVar)
			traverseVar(depth, (FieldVar) v);
		else if (v instanceof SubscriptVar)
			traverseVar(depth, (SubscriptVar) v);
		else
			{
				debugPrint(depth, v, "Is not a subclass of Var, no method to handle.");
				throw new Error("Var given is not a valid subclass. What have you done???");
			}
	}

	void traverseVar(int depth, SimpleVar v) {
		debugPrint(depth, v, "beginning traverseVar(SimpleVar)");
		debugPrint(depth, v, "Looking up the var in Symbol table");
		Escape esc = (Escape)escEnv.get(v.name);
		debugPrint(depth, esc, "the result. Checking to see if it exists at a shallower depth");
        if(esc != null && esc.depth < depth){
        	esc.setEscape();
        	debugPrint(depth, esc, "Setting escape");
        }
        else{
        	debugPrint(depth, esc, "Nope. It doesn't escape!");
        }
	}
	
	/**
	 * For FieldVars, we are really just concerned with the var field.
	 */
	void traverseVar(int depth, FieldVar v) {
		debugPrint(depth, v, "beginning traverseVar(FieldVar)");
		traverseVar(depth, v.var);
	}
	
	/**
	 * Similar to the FieldVar variant, but we are also concerend with the index field. We'll need to traverse that Expression as well.
	 */
	void traverseVar(int depth, SubscriptVar v) {
		debugPrint(depth, v, "beginning traverseVar(SubscriptVar) with depth = ." + depth);
		traverseVar(depth, v.var);
	    traverseExp(depth, v.index);
	}

	/**
	 * acts as a switch statement for subclasses of Absyn.Exp
	 */
	void traverseExp(int depth, Exp e) {
		debugPrint(depth, this, "beginning traverseExp.");
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
			debugPrint(depth, e, "Is not a subclass of Exp, no method to handle.");
	}

	void traverseExp(int depth, VarExp e) {
		debugPrint(depth, e, "beginning traverseExp(VarExp) with depth = " + depth);
	}

	void traverseExp(int depth, CallExp e) {
		debugPrint(depth, e, "beginning traverseExp(CallExp) with depth = " + depth);
	}
	
	//TODO Test
	void traverseExp(int depth, OpExp e) {
		debugPrint(depth, e, "beginning traverseExp(OpExp) with depth = " + depth);
		//check both left and right operands
		traverseExp(depth, e.left);
		traverseExp(depth, e.right);
	}

	void traverseExp(int depth, RecordExp e) {
		debugPrint(depth, e, "beginning traverseExp(RecordExp) with depth = " + depth);
	}

	void traverseExp(int depth, SeqExp e) {
		debugPrint(depth, e, "beginning traverseExp(SeqExp) with depth = " + depth);
	}

	void traverseExp(int depth, AssignExp e) {
		debugPrint(depth, e, "beginning traverseExp(AssignExp) with depth = " + depth);
	}
	
	/**
	 * We're really just concerned with the test,then, and else clauses here. Traverse them.
	 */
	void traverseExp(int depth, IfExp e) {
		debugPrint(depth, e, "beginning traverseExp(IfExp)");
		debugPrint(depth, e.test, "Traversing test");
		traverseExp(depth, e.test);
		debugPrint(depth, e.thenclause, "Traversing then");
        traverseExp(depth, e.thenclause);
        debugPrint(depth, e.elseclause, "Traversing else");
        traverseExp(depth, e.elseclause);
	}

	void traverseExp(int depth, WhileExp e) {
		debugPrint(depth, e, "beginning traverseExp(WhileExp) with depth = " + depth);
	}

	void traverseExp(int depth, ForExp e) {
		debugPrint(depth, e, "beginning traverseExp(ForExp) with depth = " + depth);
	}

	void traverseExp(int depth, LetExp e) {
		debugPrint(depth, e, "beginning traverseExp(LetExp) with depth = " + depth);
	}
	
	//TODO Fix
	void traverseExp(int depth, ArrayExp e) {
		debugPrint(depth, e, "beginning traverseExp(AeeayExp) with depth = " + depth);
		traverseExp(depth, e.size);
        traverseExp(depth, e.init);
	}

	/**
	 * acts as a switch statement for subclasses of Absyn.Dec
	 */
	void traverseDec(int depth, Dec d) {
		debugPrint(depth, this, "Beginning traverseDec");
		if (d instanceof VarDec)
			traverseDec(depth, (VarDec) d);
		else if (d instanceof FunctionDec)
			traverseDec(depth, (FunctionDec) d);
		else
			debugPrint(depth, d, "Is not a subclass of Dec, no method to handle.");
	}

	/**
	 * traveseDec(VarDec) is pretty much just going to poke around the function that declares it and store the varible name for later reference.
	 */
	void traverseDec(int depth, VarDec d) {
		debugPrint(depth, d, "It's a VarDec. Let's store the variable for later reference.");
		traverseExp(depth, d.init);
		escEnv.put(d.name, new VarEscape(depth, d));
	}

	/**
	 * traverseDec(FunctionDec) is a bit more complex. We're going to have to run through all the functions that are a part of this functiondec. For each one,
	 * store the parameters in a new scope, and then traverse the expression at an increased depth so we can mark the escaping varibles.
	 */
	void traverseDec(int depth, FunctionDec d) {
		debugPrint(depth, d,
				"It's a FunctionDec. we'll be going through the function and, at each point, traversing the body.");
		FunctionDec prevFun = currentFun; // Switch the current function
		
		FunctionDec fun = d;
		while (fun != null) {
			escEnv.beginScope();
			currentFun = fun;

			// Store the params of the function
			FieldList paramsList = fun.params;
			debugPrint(depth,paramsList,"Storing the parameters of the function.");
			while (paramsList != null) {
				debugPrint(depth, paramsList, "Storing new parameters list.");
				escEnv.put(paramsList.name, new FormalEscape(depth + 1, paramsList));
				paramsList = paramsList.tail;
			}

			traverseExp(depth + 1, fun.body);
			escEnv.endScope();
			fun = fun.next;
		}
		currentFun = prevFun;
	}
}