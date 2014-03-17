package Semant;

import Translate.Exp;
import Types.Type;

public class Semant {
	Env							env;
	boolean						debug	= false;

	// Types to check against
	static final Types.VOID		VOID	= new Types.VOID();
	static final Types.INT		INT		= new Types.INT();
	static final Types.STRING	STRING	= new Types.STRING();
	static final Types.NIL		NIL		= new Types.NIL();

	public Semant(ErrorMsg.ErrorMsg err, boolean debug) {
		this(new Env(err));
		this.debug = debug;
	}

	Semant(Env e) {
		env = e;
	}

	/**
	 * Every project I've ever done for these classes benefitted enormously from this method. We pass a message and an object (for clarity.) If the messages
	 * comes from the containing class, we'll draw a big section marker with the message.
	 */
	private void debugPrint(Object sender, String message) {
		if (debug) {
			if (!(sender instanceof Semant)) {
				System.out.println(sender.getClass().toString().substring(6) + "-> " + message);
			}
			else {
				System.out.println("\n*************** " + message.toUpperCase() + " ***************");
			}
		}
	}

	/**
	 * Used from the Main class to begin Semantic analysis.
	 */
	public void transProg(Absyn.Exp exp) {
		debugPrint(this, "Begin!");
		// If we need to, this allows us the room to print the symbol table on error catching
		try {
			debugPrint(exp, "Start decent of expression.");
			transExp(exp);
			debugPrint(this, "Done!"); // eyes on the prize!
		}
		catch (Error e) {
			System.err.println(e);
			// Print the symbol table? -Not sure how to do.
		}
	}

	private void error(int pos, String msg) {
		env.errorMsg.error(pos, msg);
	}

	private Exp checkInt(ExpTy et, int pos) {
		if (!INT.coerceTo(et.ty))
			error(pos, "integer required");
		return et.exp;
	}

	/**
	 * The main switchboard for transExps. We take a general expression, discover its form, and pass it along.
	 */
	ExpTy transExp(Absyn.Exp e) {
		debugPrint(this, "Traversing General Expression " + e);
		debugPrint(e, "First, we determine the type of the expression.");
		ExpTy result = null;

		if (e == null) {
			debugPrint(e, "e is null.");
			return new ExpTy(null, VOID);
		}
		else if (e instanceof Absyn.OpExp) {
			debugPrint(e, "I'm an OpExp!");
			result = transExp((Absyn.OpExp) e);
		}
		else if (e instanceof Absyn.LetExp) {
			debugPrint(e, "I'm a LetExp!");
			result = transExp((Absyn.LetExp) e);
		}
		else if (e instanceof Absyn.ArrayExp) {
			debugPrint(e, "I'm an ArrayExp!");
			result = transExp((Absyn.ArrayExp) e);
		}
		else if (e instanceof Absyn.AssignExp) {
			debugPrint(e, "I'm an AssignExp!");
			result = transExp((Absyn.AssignExp) e);
		}
		else if (e instanceof Absyn.BreakExp) {
			debugPrint(e, "I'm a BreakExp!");
			result = transExp((Absyn.BreakExp) e);
		}
		else if (e instanceof Absyn.CallExp) {
			debugPrint(e, "I'm a CallExp!");
			result = transExp((Absyn.CallExp) e);
		}
		else if (e instanceof Absyn.ForExp) {
			debugPrint(e, "I'm a ForExp!");
			result = transExp((Absyn.ForExp) e);
		}
		else if (e instanceof Absyn.IfExp) {
			debugPrint(e, "I'm an IfExp!");
			result = transExp((Absyn.IfExp) e);
		}
		else if (e instanceof Absyn.IntExp) {
			debugPrint(e, "I'm an IntExp!");
			result = transExp((Absyn.IntExp) e);
		}
		else if (e instanceof Absyn.NilExp) {
			debugPrint(e, "I'm a NilExp!");
			result = transExp((Absyn.NilExp) e);
		}
		else if (e instanceof Absyn.RecordExp) {
			debugPrint(e, "I'm a RecordExp!");
			result = transExp((Absyn.RecordExp) e);
		}
		else if (e instanceof Absyn.SeqExp) {
			debugPrint(e, "I'm a SeqExp!");
			result = transExp((Absyn.SeqExp) e);
		}
		else if (e instanceof Absyn.StringExp) {
			debugPrint(e, "I'm a StringExp!");
			result = transExp((Absyn.StringExp) e);
		}
		else if (e instanceof Absyn.VarExp) {
			debugPrint(e, "I'm a VarExp!");
			result = transExp((Absyn.VarExp) e);
		}
		else if (e instanceof Absyn.WhileExp) {
			debugPrint(e, "I'm a WhileExp!");
			result = transExp((Absyn.WhileExp) e);
		}
		else
			error(((Absyn.Absyn) e).pos, "Unsure what type of expression we're dealing with here...");
		if (result == null) {
			debugPrint(e, "the result from this transExp is null. Likely that we haven't implemented it yet.");
		}
		e.type = result.ty;
		debugPrint(this, "expression " + e.toString() + " evaluated to " + result.toString());
		return result;
	}

	ExpTy transExp(Absyn.OpExp e) {
		ExpTy left = transExp(e.left);
		ExpTy right = transExp(e.right);

		// TODO implement the other operations(?)
		switch (e.oper) {
			case Absyn.OpExp.PLUS:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
			default:
				throw new Error("unknown operator");
		}
	}

	/**
	 * I'm pretty sure there should only be 1 let statement. Should we check for that?
	 */
	ExpTy transExp(Absyn.LetExp e) {
		debugPrint(this, "Beginning traversal of a Let expr");
		env.venv.beginScope(); // venv should be value environment
		env.tenv.beginScope(); // tenc should be type environment
		debugPrint(e, "Began a new val env and type env.");

		// Iterate through the declist of this let expr
		for (Absyn.DecList d = e.decs; d != null; d = d.tail) {
			debugPrint(d, "Beginning new transDec of the head: " + d.head.toString());
			transDec(d.head);
			debugPrint(this, "continuing Let expr");
		}
		ExpTy body = transExp(e.body);
		env.venv.endScope();
		env.tenv.endScope();
		return new ExpTy(null, body.ty);
	}

	// TODO implement
	ExpTy transExp(Absyn.ArrayExp e) {
		debugPrint(this, "Traversing ArrayExp...");
		debugPrint(e, "Get the NAME of the expression from the type env.");

		Types.NAME name = (Types.NAME) env.tenv.get(e.typ);

		debugPrint(name, "Now we evalutate it to see if the NAME is an ARRAY.");
		if (name == null) {
			debugPrint(e, "The type of the array hasn't been declared yet. DUCK!!!");
			error(((Absyn.Absyn) (e)).pos, "undeclared type: " + e.typ);
		}
		else {
			debugPrint(name, "We found the name in the tenv. So far so good.");
		}

		debugPrint(name, "name is: " + name);
		debugPrint(e, "Get the size of the array by traversing the IntExpr.");
		ExpTy size = transExp(e.size);

		debugPrint(this, "continuing with transExp(ArrayExp).");
		debugPrint(size, "Let's make sure this is actually an int.");
		checkInt(size, ((Absyn.Absyn) e).pos);

		debugPrint(e, "now, we run the init part of the expression.");
		ExpTy init = transExp(e.init);
		debugPrint(this, "continuing with transExp(ArrayExp).");
		debugPrint(e, "Last, we check to be sure the array's type and the elements' type match up.");
		
		System.out.println(name.actual());
		Type actual = name.actual(); //This got explained in class.
		if (actual instanceof Types.ARRAY) {
			Types.ARRAY array = (Types.ARRAY) actual;
			if (!init.ty.coerceTo(array.element)) {
				debugPrint(init, "type of init and type of array don't match up.");
				error(((Absyn.Absyn) (e.init)).pos, "element type mismatch");
			}
			else {
				return new ExpTy(null, name);
			}
		}

		return new ExpTy(null, VOID);
	}

	// TODO implement
	ExpTy transExp(Absyn.AssignExp e) {
		debugPrint(this, "Traversing AssignExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.BreakExp e) {
		debugPrint(this, "Traversing BreakExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.CallExp e) {
		debugPrint(this, "Traversing CallExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.IfExp e) {
		debugPrint(this, "Traversing IfExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.IntExp e) {
		debugPrint(this, "Traversing IntExp...");
		return new ExpTy(null, INT);
	}

	// TODO implement
	ExpTy transExp(Absyn.NilExp e) {
		debugPrint(this, "Traversing NilExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.RecordExp e) {
		debugPrint(this, "Traversing RecordExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.SeqExp e) {
		debugPrint(this, "Traversing IfExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.StringExp e) {
		debugPrint(this, "Traversing StringExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.VarExp e) {
		debugPrint(this, "Traversing VarExp...");
		return null;
	}

	// TODO implement
	ExpTy transExp(Absyn.WhileExp e) {
		debugPrint(this, "Traversing WhileExp...");
		return null;
	}

	/**
	 * This method exists to hand a general declaration. We figure out it's type, and then move to the appropriate method with a more specific decaraltion type.
	 */
	Exp transDec(Absyn.Dec d) {
		debugPrint(this, "Beginning transDec with Dec " + d.toString());
		debugPrint(d, "Let's determine the subtype.");
		if (d instanceof Absyn.VarDec) {
			debugPrint(d, "I'm a VarDec; traversing");
			return transDec((Absyn.VarDec) d);
		}
		else if (d instanceof Absyn.TypeDec) { // Added by Mitch.
			debugPrint(d, "I'm a TypeDec; traversing");
			return transDec((Absyn.TypeDec) d);
		}
		else if (d instanceof Absyn.FunctionDec) { // Added by Mitch.
			debugPrint(d, "I'm a FunctionDec; traversing");
			return transDec((Absyn.FunctionDec) d);
		}
		else
			throw new Error("Dec supplied is not actually a dec or isn't implemented");
	}

	// TODO implement
	Exp transDec(Absyn.TypeDec d) {
		debugPrint(this, "Traversing typedec");
		debugPrint(d, "Typedecs have many symbols. Let's build a list of them");
		java.util.ArrayList<Symbol.Symbol> symList = new java.util.ArrayList<Symbol.Symbol>();
		for (Absyn.TypeDec head = d; head != null; head = head.next) {
			// ArrayList.add() will return false if the element is already present in the list.
			if (symList.add(head.name)) {
				env.tenv.put(head.name, new Types.NAME(head.name));
				debugPrint(head, "added " + head.name + " to the types tables");
			}
			else {
				debugPrint(head, "duplicate name detected!");
				throw new Error("Duplicate name detected in type declaration.");
			}
		}

		return null;
	}

	// TODO implement
	Exp transDec(Absyn.FunctionDec d) {
		debugPrint(this, "Traversing functiondec");
		return null;
	}

	/**
	 * It appears we need a transDec method for each and every form of Absyn.Dec
	 */
	Exp transDec(Absyn.VarDec d) {
		debugPrint(d, "Beggining transDec");

		// Get the Expression type (a class composed of an expression, and it's resulting type.)
		ExpTy init = transExp(d.init);
		Type type; // we determine the type in the if statement below
		if (d.typ == null) {
			debugPrint(d, "my typ is null, pull my real type from the init data I hold.");
			type = init.ty; // If the VarDec doesn't have a type stored yet, pull it from itself.
			debugPrint(d, "That type is: " + type.toString());
		}
		else {
			type = VOID;
			throw new Error("unimplemented"); // Not sure what to implement here... It sounds like a complete logic flow to me -Mitch
												// Maybe we're this occurs when a varible has already been declared? -Mitch
		}

		// Store the resulting entry
		debugPrint(d, "Storing in the env table.");
		d.entry = new VarEntry(type);
		env.venv.put(d.name, d.entry);
		return null;
	}
}
