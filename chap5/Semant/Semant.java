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
			if(sender == null){
				System.out.println("null -> " + message); //Null ptr exeception protection.
			}
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
		debugPrint(this, "translating OpExp...");
		debugPrint(e, "translate left side");
		ExpTy left = transExp(e.left);
		debugPrint(this, "OpExp>>>translate right side.");
		ExpTy right = transExp(e.right);

		// TODO implement the other operations(?)
		switch (e.oper) {
			case Absyn.OpExp.PLUS:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
			case Absyn.OpExp.MINUS:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
			case Absyn.OpExp.MUL:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
			case Absyn.OpExp.DIV:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
				// We will need EQ, NE, LT, LE, GT, GE as well
			case Absyn.OpExp.EQ:
				debugPrint(e, "Checking = expr.");
				Type a = left.ty.actual();
				Type b = right.ty.actual();
				debugPrint(e, "Is the left an appropriate type?");
				if (!(a instanceof Types.INT) && !(a instanceof Types.STRING) && !(a instanceof Types.NIL)
						&& !(a instanceof Types.RECORD) && !(a instanceof Types.ARRAY)) {
					error(((Absyn.Absyn) e).pos,
							"Cannot use EQ with something other than int, str, nil, record or array.");
				}
				debugPrint(e, "Is the right an appropriate type?");
				if (!(b instanceof Types.INT) && !(b instanceof Types.STRING) && !(b instanceof Types.NIL)
						&& !(b instanceof Types.RECORD) && !(b instanceof Types.ARRAY)) {
					error(((Absyn.Absyn) e).pos,
							"Cannot use EQ with something other than int, str, nil, record or array.");
				}
				debugPrint(e, "Are the types the same?");
				if (left.ty.coerceTo(right.ty) && right.ty.coerceTo(left.ty)) {
					debugPrint(e, "Everything looks good. Returning new ExpTy.");
				}
				else {
					debugPrint(e, "Nope. Type A:" + a.toString() + " Type B: " + b.toString());
					error(((Absyn.Absyn) e).pos, "Types not compatable in EQ operator.");
				}
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

		Type actual = name.actual(); // This got explained in class.
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

	// TODO fix
	ExpTy transExp(Absyn.AssignExp e) {
		debugPrint(this, "Traversing AssignExp...");
		debugPrint(e, "Translating the var");
		ExpTy var = transVar(e.var, true);
		debugPrint(this, "transExp(AssignExp): Translating the expression: " + e.exp);
		ExpTy exp = transExp(e.exp);
		debugPrint(this, "transExp(AssignExp): Checking to see if the types of the expression and var match");
		if (!exp.ty.coerceTo(var.ty)) {
			debugPrint(exp, "Nope. DUCK!!!");
			error(((Absyn.Absyn) (e)).pos, "assignment var and expression types don't match");
		}
		else {
			debugPrint(exp, "Yep.");
		}
		return new ExpTy(null, VOID);
	}

	// TODO test
	ExpTy transExp(Absyn.BreakExp e) {
		debugPrint(this, "Break expressions not allowed under normal circumstances.");
		error(((Absyn.Absyn) (e)).pos, "Break expressions not allowed under normal circumstances.");
		return new ExpTy(null, VOID);
	}

	ExpTy transExp(Absyn.CallExp e) {
		debugPrint(this, "Traversing CallExp...");
		// Here, we call a function of some type. We first need to look it up
		Entry function = (Entry) env.venv.get(e.func);
		debugPrint(function, "After looking up, here's the function: " + function.toString());
		if (function instanceof FunEntry) {
			FunEntry true_function = (FunEntry) function;
			debugPrint(true_function,"Here's the function entry with result: " + true_function.result);
			return new ExpTy(null, true_function.result);
		}
		else {
			debugPrint(e, "Funtion not found in val environment. DUCK!!!");
			error(((Absyn.Absyn) (e)).pos, "Funtion not found in val environment: " + e.func);
			return new ExpTy(null, VOID);
		}
	}

	// TODO fix
	ExpTy transExp(Absyn.IfExp e) {
		debugPrint(this, "Traversing IfExp...");
		debugPrint(e, "If statements first have a test. Let's Pull it out and make sure it evalueates to true/false");
		ExpTy condition = transExp(e.test);
		checkInt(condition, ((Absyn.Absyn) e).pos); // This makes sure we're dealing with a boolean.
		debugPrint(condition, "Condition is good to go.");
		debugPrint(e, "Next, we check the then and else clauses.");
		debugPrint(this, "then clause");
		ExpTy thenclause = transExp(e.thenclause);
		debugPrint(this, "else clause");
		ExpTy elseclause = transExp(e.elseclause);

		debugPrint(this, "Returning from then and else checks.");
		return new ExpTy(null, elseclause.ty);
	}

	ExpTy transExp(Absyn.IntExp e) {
		debugPrint(this, "Traversing IntExp...");
		// Not much to do for Ints
		return new ExpTy(null, INT);
	}

	ExpTy transExp(Absyn.NilExp e) {
		debugPrint(this, "Traversing NilExp...");
		// Defintely nothing to do with NilExp
		return new ExpTy(null, NIL);
	}

	// TODO fix
	ExpTy transExp(Absyn.RecordExp e) {
		debugPrint(this, "Traversing RecordExp...");
		debugPrint(e, "Pull this expression from the types environment to see if it already exists.");
		Types.NAME result = (Types.NAME) env.tenv.get(e.typ);
		if (result != null) {
			debugPrint(e, "I exist in the types table! But am I a Record type?");
			Types.Type actual = result.actual();
			if (actual instanceof Types.RECORD) {
				debugPrint(actual, "yep.");
				Types.RECORD r = (Types.RECORD) actual;
				return new ExpTy(null, result);
			}
			else {
				debugPrint(actual, "nope.");
			}
			error(((Absyn.Absyn) (e)).pos, "record type required");
		}
		else {
			error(((Absyn.Absyn) (e)).pos, "undeclared type: " + e.typ);
		}
		return new ExpTy(null, VOID);
	}

	// TODO fix
	ExpTy transExp(Absyn.SeqExp e) {
		debugPrint(this, "Traversing SeqExp...");
		Types.Type type = VOID;
		debugPrint(e, "Getting SeqExp list.");
		Absyn.ExpList exp = e.list;
		debugPrint(exp, "looping around the sequence.");
		while (exp != null) {
			ExpTy headExp = transExp(exp.head);
			type = headExp.ty;
			exp = exp.tail;
		}
		debugPrint(e, "Returning with type " + type);
		return new ExpTy(null, type);
	}

	ExpTy transExp(Absyn.StringExp e) {
		debugPrint(this, "Traversing StringExp...");
		// Not much to do for String expressions
		return new ExpTy(null, STRING);
	}

	// TODO implement
	ExpTy transExp(Absyn.VarExp e) {
		debugPrint(this, "Traversing VarExp...");
		ExpTy result = transVar(e.var);
		debugPrint(this, "result of VarExp = " + result.toString());
		return result;
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
				Types.NAME name = new Types.NAME(head.name);
				head.entry = name;
				env.tenv.put(head.name, name);
				debugPrint(head, "added " + head.name + " to the types tables");
			}
			else {
				debugPrint(head, "duplicate name detected!");
				error(((Absyn.Absyn) d).pos, "Duplicate name detected in type declaration.");
			}
		}
		// Bind the symbols we just stored to thier types.
		// We do this now becuase of mutally recursive types
		debugPrint(d, "Next we need to bind all of the symbols to their types");
		for (Absyn.TypeDec head = d; head != null; head = head.next) {
			if (head.entry == null) {
				debugPrint(head, "entry is null");
				return null;
			}
			Types.NAME name = head.entry;
			Type headType = transTy(head.ty);
			name.bind(headType);
		}
		return null;
	}

	// TODO taken from pg 125. Build to handle multiple function declarations, void returning functions, and recursive functions
	Exp transDec(Absyn.FunctionDec fd) {
		/*
		 * Hashtable hash = new Hashtable(); for(FunctionDec f = d; f != null; f = f.next) { if(hash.put(f.name, f.name) != null) error(((Absyn) (f)).pos,
		 * "function redeclared"); RECORD fields = transTypeFields(new Hashtable(), f.params); Type type = transTy(f.result); f.entry = new FunEntry(fields,
		 * type); env.venv.put(f.name, f.entry); } for(FunctionDec f = d; f != null; f = f.next) { env.venv.beginScope(); putTypeFields(f.entry.formals); Semant
		 * fun = new Semant(env); ExpTy body = fun.transExp(f.body); if(!body.ty.coerceTo(f.entry.result)) error(((Absyn) (f.body)).pos,
		 * "result type mismatch"); env.venv.endScope(); }
		 */
		Absyn.FunctionDec temp = fd;
		while (temp != null) {
			debugPrint(this, "Traversing functiondec");
			debugPrint(fd, "Let's get the result of this function");
			Types.Type result = transTy(fd.result);

			debugPrint(this, "return to functiondec. here's the result.");
			debugPrint(fd, "Now, let's have a look at the formals.");
			Types.RECORD formals = transTypeFields(new java.util.Hashtable(), fd.params);

			debugPrint(formals, "Here they are.");
			FunEntry fe = new FunEntry(formals, result);
			temp.entry = fe;
			env.venv.put(fd.name, fe);
			temp = temp.next;
		}
		temp = fd;
		while (temp != null) {
			env.venv.beginScope(); // handle the params and function-scope vars

			for (Absyn.FieldList p = fd.params; p != null; p = p.tail) {
				env.venv.put(p.name, new VarEntry((Types.Type) env.tenv.get(p.typ)));
			}
			ExpTy result = transExp(fd.body);
			if (!result.ty.coerceTo(fd.entry.result))
				error(((Absyn.Absyn) (fd.body)).pos, "Result type and function return type don't match.");
			env.venv.endScope();
			temp = temp.next;
		}
		return null;
	}

	/**
	 * This function comes from the suggestion of the book pg 125 It reads: "...transTypeFields on each formal parameter; this yeilds a 'record type,'
	 * (a,t_a),(b,t_b) where t_a is the NAME type found by looking up ta in the type environment.
	 */
	// TODO fix
	private void transFields(int epos, Types.RECORD f, Absyn.FieldExpList exp) {
		if (f == null) {
			if (exp != null)
				error(((Absyn.Absyn) (exp)).pos, "too many expressions");
			return;
		}
		if (exp == null) {
			error(epos, "missing expression for " + f.fieldName);
			return;
		}
		ExpTy e = transExp(exp.init);
		if (exp.name != f.fieldName)
			error(((Absyn.Absyn) (exp)).pos, "field name mismatch");
		if (!e.ty.coerceTo(f.fieldType))
			error(((Absyn.Absyn) (exp)).pos, "field type mismatch");
		transFields(epos, f.tail, exp.tail);
	}

	Exp transDec(Absyn.VarDec d) {
		debugPrint(this, "Beggining transDec");
		debugPrint(d, "translate the init expression.");
		// Get the Expression type (a class composed of an expression, and it's resulting type.)
		ExpTy init = transExp(d.init);
		debugPrint(this, "continuing transDec");
		Type type; // we determine the type in the if statement below
		if (d.typ == null) {
			debugPrint(d, "my typ is null, pull my real type from the init data I hold.");
			type = init.ty; // If the VarDec doesn't have a type stored yet, pull it from itself.
			debugPrint(d, "That type is: " + type.toString());
		}
		else {
			debugPrint(d, "my typ is not null, check to be sure there's no type mismatch.");
			type = transTy(d.typ);
			if (!init.ty.coerceTo(type)) {
				debugPrint(d, "inializer type and var type don't match.");
				error(((Absyn.Absyn) (d)).pos, "assignment type and var type do not match up");
			}
		}

		// Store the resulting entry
		debugPrint(d, "Storing in the env table.");
		d.entry = new VarEntry(type);
		env.venv.put(d.name, d.entry);
		return null;
	}

	// TODO fix
	ExpTy transVar(Absyn.Var v) {
		debugPrint(this, "translating var");
		return transVar(v, false);
	}

	// TODO fix
	ExpTy transVar(Absyn.Var v, boolean lhs) {
		debugPrint(v, "route through type");
		if (v instanceof Absyn.SimpleVar)
			return transVar((Absyn.SimpleVar) v, lhs);
		if (v instanceof Absyn.FieldVar)
			return transVar((Absyn.FieldVar) v);
		if (v instanceof Absyn.SubscriptVar)
			return transVar((Absyn.SubscriptVar) v);
		else
			throw new Error("Not a valid Var");
	}
	
	ExpTy transVar(Absyn.FieldVar fv) {
		ExpTy var = transVar(fv.var);
		Type actual = var.ty.actual();
		if (!(actual instanceof Types.RECORD)) {
			error(((Absyn.Absyn) (fv.var)).pos, "record required");
		}
		else {
			Types.RECORD field = (Types.RECORD) actual;
			while (field != null) {
				if (field.fieldName == fv.field) { return new ExpTy(null, field.fieldType); }
				field = field.tail;
			}
			error(((Absyn.Absyn) (fv)).pos, "undeclared field: " + fv.field);
		}
		return new ExpTy(null, VOID);
	}

	// TODO fix
	ExpTy transVar(Absyn.SimpleVar v, boolean lhs) {
		debugPrint(v,"Translating simple var.");
		debugPrint(v,"Get entry from symbol table.");
		Entry x = (Entry) env.venv.get(v.name);
		debugPrint(x,"Here it is.");
		if (x instanceof VarEntry) {
			VarEntry ent = (VarEntry) x;
			if (lhs && (ent instanceof LoopVarEntry))
				error(((Absyn.Absyn) (v)).pos, "assignment to loop index");
			return new ExpTy(null, ent.ty);
		}
		else {
			error(((Absyn.Absyn) (v)).pos, "undeclared variable: " + v.name);
			return new ExpTy(null, VOID);
		}
	}

	// TODO fix
	ExpTy transVar(Absyn.SubscriptVar v) {
		ExpTy var = transVar(v.var);
		ExpTy index = transExp(v.index);
		checkInt(index, ((Absyn.Absyn) (v.index)).pos);
		Type actual = var.ty.actual();
		if (actual instanceof Types.ARRAY) {
			Types.ARRAY array = (Types.ARRAY) actual;
			return new ExpTy(null, array.element);
		}
		else {
			error(((Absyn.Absyn) (v.var)).pos, "array required");
			return new ExpTy(null, VOID);
		}
	}

	/**
	 * This series of methods comes from the book. It reads: "The transTy function translates type expressions as found in the abstract syntax (Absyn.Ty) to the
	 * digested type descriptions that we will put into environments (Types.Type). This translation is done by recurring over the structure of an Absyn.Type,
	 * turning Absyn.RecordTy into Types.RECORD, etc. While Translating, transTy looks up any symbols it finds in the type environment tenv.
	 */
	Type transTy(Absyn.Ty t) {
		if (t instanceof Absyn.NameTy)
			return transTy((Absyn.NameTy) t);
		if (t instanceof Absyn.RecordTy)
			return transTy((Absyn.RecordTy) t);
		if (t instanceof Absyn.ArrayTy)
			return transTy((Absyn.ArrayTy) t);
		else
			throw new Error("Not a valid subclass of Ty");
	}

	Type transTy(Absyn.ArrayTy t) {
		Types.NAME name = (Types.NAME) env.tenv.get(t.typ);
		if (name != null) {
			return new Types.ARRAY(name);
		}
		else {
			error(((Absyn.Absyn) (t)).pos, "undeclared type: " + t.typ);
			return VOID;
		}
	}

	// TODO fix
	Type transTy(Absyn.NameTy t) {
		if (t == null)
			return VOID;
		Types.NAME name = (Types.NAME) env.tenv.get(t.name);
		if (name != null) {
			return name;
		}
		else {
			error(((Absyn.Absyn) (t)).pos, "undeclared type: " + t.name);
			return VOID;
		}
	}

	// TODO fix
	Type transTy(Absyn.RecordTy t) {
		Types.RECORD type = transTypeFields(new java.util.Hashtable(), t.fields);
		if (type != null)
			return type;
		else
			return VOID;
	}

	// TODO fix
	private Types.RECORD transTypeFields(java.util.Hashtable hash, Absyn.FieldList f) {
		if (f == null)
			return null;
		Types.NAME name = (Types.NAME) env.tenv.get(f.typ);
		if (name == null)
			error(((Absyn.Absyn) (f)).pos, "undeclared type: " + f.typ);
		if (hash.put(f.name, f.name) != null)
			error(((Absyn.Absyn) (f)).pos, "function parameter/record field redeclared: " + f.name);
		return new Types.RECORD(f.name, name, transTypeFields(hash, f.tail));
	}
}
