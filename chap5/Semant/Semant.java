package Semant;

import Translate.Exp;
import Types.Type;

public class Semant {
	Env		env;
	boolean	debug	= false;

	public Semant(ErrorMsg.ErrorMsg err, boolean debug) {
		this(new Env(err)); // This is lazy but should work. I couldn't call Semant(ErrorMsg.ErrorMsg) for whatever reason
		this.debug = debug;
	}

	// public Semant(ErrorMsg.ErrorMsg err) {
	// this(new Env(err));
	// }

	Semant(Env e) {
		env = e;
	}

	/**
	 * Every project I've ever done for these classes benefitted enormously from this method.
	 */
	public void debugPrint(Object sender, String message) {
		if (debug)
			{
				System.out.println(sender.getClass().toString() + "-> " + message);
			}
	}

	public void transProg(Absyn.Exp exp) {
		debugPrint(this, "Beginning parsing with exp: " + exp.toString());
		try
			// If we need to, this allows us the room to print the symbol table on error catching
			{
				transExp(exp);
			} catch (Error e)
			{
				System.err.println(e);
				// Print the symbol table?
			}
	}

	private void error(int pos, String msg) {
		env.errorMsg.error(pos, msg);
	}

	static final Types.VOID		VOID	= new Types.VOID();
	static final Types.INT		INT		= new Types.INT();
	static final Types.STRING	STRING	= new Types.STRING();
	static final Types.NIL		NIL		= new Types.NIL();

	private Exp checkInt(ExpTy et, int pos) {
		if (!INT.coerceTo(et.ty))
			error(pos, "integer required");
		return et.exp;
	}

	ExpTy transExp(Absyn.Exp e) {
		ExpTy result;

		if (e == null)
			return new ExpTy(null, VOID);
		else if (e instanceof Absyn.OpExp)
			{
				result = transExp((Absyn.OpExp) e);
				debugPrint(e, "I'm an OpExp!");
			} else if (e instanceof Absyn.LetExp)
			{
				result = transExp((Absyn.LetExp) e);
				debugPrint(e, "I'm an LetExp!");
			} else
			throw new Error("First expression must be an opexp or letexp");
		e.type = result.ty;
		return result;
	}

	ExpTy transExp(Absyn.OpExp e) {
		ExpTy left = transExp(e.left);
		ExpTy right = transExp(e.right);

		switch (e.oper) {
			case Absyn.OpExp.PLUS:
				checkInt(left, e.left.pos);
				checkInt(right, e.right.pos);
				return new ExpTy(null, INT);
			default:
				throw new Error("unknown operator");
		}
	}

	ExpTy transExp(Absyn.LetExp e) {
		debugPrint(e, "Beginning traversal of a Let expr");
		env.venv.beginScope();
		env.tenv.beginScope();
		for (Absyn.DecList d = e.decs; d != null; d = d.tail)
			{
				transDec(d.head);
			}
		ExpTy body = transExp(e.body);
		env.venv.endScope();
		env.tenv.endScope();
		return new ExpTy(null, body.ty);
	}

	Exp transDec(Absyn.Dec d) {
		debugPrint(this, "Beginning transDec with Dec " + d.toString());
		if (d instanceof Absyn.VarDec)
			{
				Absyn.VarDec dee = (Absyn.VarDec) d;
				debugPrint(dee, "I'm a VarDec with type:" + dee.typ.toString());
				/* From the book, pg 124 */
				VarEntry ve = new VarEntry(transExp(dee.init).ty);
				env.venv.put(dee.name, ve);
				debugPrint(d, "Added a new var to the table: " + ve.toString());
				return transDec(dee);
				/* end book code */
				//return transDec((Absyn.VarDec) d);
			}
		else if(d instanceof Absyn.TypeDec){ //Added by Mitch.
			//env.tenv.put(d,)
			return null; //TODO fix this!
		} //</Mitch>
		throw new Error("Dec supplied is not a varDec.");
	}

	/**
	 * This is the meat of the program. Given a variable declaration like: a =: 2 determine the type O.o
	 */
	Exp transDec(Absyn.VarDec d) {
		// NOTE: THIS IMPLEMENTATION IS INCOMPLETE
		// It is here to show you the general form of the transDec methods
		ExpTy init = transExp(d.init);
		Type type;
		if (d.typ == null)
			{
				type = init.ty;
			}
		// else if(d.typ instanceof Something){debugPrint(d,"I'm a Something");}
		else
			{
				type = VOID;
				throw new Error("unimplemented");
			}
		d.entry = new VarEntry(type);
		env.venv.put(d.name, d.entry);
		return null;
	}
}
