package Translate;

import Symbol.Symbol;
import Tree.BINOP;
import Tree.CJUMP;
import Temp.Temp;
import Temp.Label;

/*
 * The goal of this class is to take the semantically correct parse tree
 * we've been able to generate thus far and turn it into an intermediate
 * representation tree. Basically, that means we're one step away from
 * generating machine code that executes the given source.
 */
public class Translate {
	public Frame				frame;									// Represents the data frame from the last project
	private java.util.Hashtable	strings	= new java.util.Hashtable();	// Pre-included. Probably for helping with the strings that get declared in MIPS
	private Frag				frags;									// Frag is short for fragment.
	private boolean				debug	= false;						// Set to false before submit.

	public Translate(Frame f) {
		frame = f;
	}

	// debug tools
	private void debugPrint(Object sender, String message) {
		if (debug) {
			if (sender == null)
				System.out.println(": null -> " + message);
			if (!(sender instanceof Translate))
				System.out.println(": " + sender.getClass().toString().substring(6) + "-> " + message);
			else
				System.out.println("\n*************** " + message.toUpperCase() + " ***************");
		}
	}

	// Holy One Liners, batman!
	/* This stuff came pre-included, not sure why it's here yet. */
	public Frag getResult() {
		return frags;
	}

	private static Tree.Exp CONST(int value) {
		return new Tree.CONST(value);
	}

	private static Tree.Exp NAME(Label label) {
		return new Tree.NAME(label);
	}

	private static Tree.Exp TEMP(Temp temp) {
		return new Tree.TEMP(temp);
	}

	private static Tree.Exp MEM(Tree.Exp exp) {
		return new Tree.MEM(exp);
	}

	private static Tree.Stm UEXP(Tree.Exp exp) {
		return new Tree.UEXP(exp);
	}

	private static Tree.Stm JUMP(Label target) {
		return new Tree.JUMP(target);
	}

	private static Tree.Stm LABEL(Label label) {
		return new Tree.LABEL(label);
	}

	private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) {
		return new Tree.CALL(func, args);
	}

	private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
		return new Tree.MOVE(dst, src);
	}

	private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
		return new Tree.BINOP(binop, left, right);
	}

	private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) {
		return new Tree.CJUMP(relop, l, r, t, f);
	}

	private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
		return new Tree.ExpList(head, tail);
	}

	private static Tree.ExpList ExpList(Tree.Exp head) {
		return ExpList(head, null);
	}

	private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
		if (stm == null)
			return exp;
		else
			return new Tree.ESEQ(stm, exp);
	}

	private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
		if (left == null)
			return right;
		else if (right == null)
			return left;
		else
			return new Tree.SEQ(left, right);
	}

	private static Tree.ExpList ExpList(ExpList exp) {
		if (exp == null)
			return null;
		else
			return ExpList(exp.head.unEx(), ExpList(exp.tail));
	}

	// </pre-written stuff>

	// Nil in tiger really means 0, so just return that
	public Exp NilExp() {
		return new Ex(CONST(0));
	}

	// IntExp's are just CONST nodes with the value passed. Easy.
	public Exp IntExp(int value) {
		return new Ex(CONST(value));
	}

	// No result given. We are simply moving something into memory here.
	public Exp AssignExp(Exp lhs, Exp rhs) {
		// Un-ex both the left and right hand sides
		Exp l = lhs.unEx();
		Exp r = rhs.unEx();
		Nx moveX = new Nx(MOVE(l, r));
		return moveX;
	}

	// When we break, we just move back to the expression we started at.
	public Exp BreakExp(Label done) {
		return new Nx(JUMP(done));
	}

	// From here, we just launch a new IfThenElseExp. This is talked about heavily in
	// the book, and it's the only other class file we do for this project.
	public Exp IfExp(Exp cc, Exp aa, Exp bb) {
		return new IfThenElseExp(cc, aa, bb);
	}

	// Pre-included but obvious.
	public Exp TypeDec() {
		return new Nx(null);
	}

	// Pre-included but obvious.
	public Exp FunctionDec() {
		return new Nx(null);
	}

	// Interesting Error function...
	// It just keeps us rolling along, so I added a way for us to know we were hitting an error...
	public Exp Error() {
		debugPrint(this, "Function not implemented yet!");
		return new Ex(CONST(0));
	}

	// Pre-included.
	public Exp FunExp(Symbol f, ExpList args, Level from) {
		return new Ex(CallExp(f, args, from));
	}

	// Pre-included
	public Exp FunExp(Level f, ExpList args, Level from) {
		return new Ex(CallExp(f, args, from));
	}

	// Pre-included
	public Exp ProcExp(Symbol f, ExpList args, Level from) {
		return new Nx(UEXP(CallExp(f, args, from)));
	}

	// Pre-included
	public Exp ProcExp(Level f, ExpList args, Level from) {
		return new Nx(UEXP(CallExp(f, args, from)));
	}

	// Pre-included, and pretty confusing... TODO understand!
	private Exp CallExp(Symbol f, ExpList args, Level from) {
		return frame.externalCall(f.toString(), ExpList(args));
	}

	/**
	 * This method is the point of entry to begin the process of translating. We take out the data frame, and start by unEx()ing the whole body. This returns a
	 * Tree.Exp. If that Exp is not null, then the bodyStm will simply move the bodyExp into temporary memory. Not sure why it will do this, but my guess is
	 * that we'll find out soon enough.
	 */
	public void procEntryExit(Level level, Exp body) {
		Frame.Frame myframe = level.frame;
		Tree.Exp bodyExp = body.unEx();
		Tree.Stm bodyStm;

		if (bodyExp != null)
			bodyStm = MOVE(TEMP(myframe.RV()), bodyExp);
		else
			bodyStm = body.unNx();

		ProcFrag frag = new ProcFrag(myframe.procEntryExit1(bodyStm), myframe);
		frag.next = frags;
		frags = frag;
	}
	
	//Pre-included, no changes appear to be in need. 
	public Exp StringExp(String lit) {
		String u = lit.intern(); //'u' is not a great var name...
		Label lab = (Label) strings.get(u);
		if (lab == null) {
			lab = new Label();
			strings.put(u, lab);
			DataFrag frag = new DataFrag(frame.string(lab, u));
			frag.next = frags;
			frags = frag;
		}
		return new Ex(NAME(lab));
	}
	
	/*
	 * These are the operations we can do with the BINOP node. This includes
	 * Add, sub, mul, div, and, or, shifts, and xor.
	 * With add, sub, mul and div, we know there will be an EX node.
	 * For the conditionals AND,OR,XOR nodes, we will need a relative context node,
	 * as called for in the book. 
	 */
	public Exp OpExp(int op, Exp left, Exp right) {
		switch (op) {
			case BINOP.PLUS:
				return new Ex(BINOP(0, left.unEx(), right.unEx()));
			case BINOP.MINUS:
				return new Ex(BINOP(1, left.unEx(), right.unEx()));
			case BINOP.MUL:
				return new Ex(BINOP(2, left.unEx(), right.unEx()));
			case BINOP.DIV:
				return new Ex(BINOP(3, left.unEx(), right.unEx()));
			case BINOP.AND:
				return new RelCx(4, left.unEx(), right.unEx());
			case BINOP.OR:
				return new RelCx(5, left.unEx(), right.unEx());
			case BINOP.LSHIFT:
				return new RelCx(6, left.unEx(), right.unEx());
			case BINOP.RSHIFT:
				return new RelCx(7, left.unEx(), right.unEx());
			case BINOP.ARSHIFT:
				return new RelCx(8, left.unEx(), right.unEx());
			case BINOP.XOR:
				return new RelCx(9, left.unEx(), right.unEx());
		}
	}

	/*
	 * This one's tricky. See page 151 of the textbook
	 */
	public Exp RecordExp(ExpList init) {
		
		//Count the amount of expressions
		int expLength = 0;
		ExpList exp = init;
		while(exp!=null){
			expLength++;
			exp=exp.tail;
		}
		
		//Create a new temporary reg
		Temp temporary = new Temp();
		return new Ex(ESEQ(
				SEQ(MOVE(TEMP(temporary), frame.externalCall("allocRecord", ExpList(CONST(expLength)))),
						RecordExpRest(temporary, init, frame.wordSize())), TEMP(temporary)));
	}
	
	//TODO incorporate this function
	private Stm RecordExpRest(Temp temporary, ExpList recExp, int wordSize) {
		if (recExp != null)
			return SEQ(MOVE(MEM(BINOP(0, TEMP(temporary), CONST(0))), recExp.head.unEx()),
					recordExp2(temporary, wordSize, recExp.tail, wordSize));
		else
			return null;
	}
	
	//TODO
	public Exp SeqExp(ExpList e) {
		if (e != null) {
			Tree.Stm stm = null;
			for (; e.tail != null; e = e.tail)
				stm = SEQ(stm, e.head.unNx());

			Exp retVal = e.head.unEx();
			if (retVal != null)
				return new Ex(ESEQ(stm, retVal));
			else
				return new Nx(SEQ(stm, e.head.unNx()));
		}
		else
			return new Nx(null);
	}
	
	//TODO
	public Exp WhileExp(Exp test, Exp body, Label done) {
		Label lab1 = new Label();
		Label lab2 = new Label();
		Nx retVal = new Nx(SEQ(
				SEQ(SEQ(LABEL(lab1), test.unCx(lab2, done)), SEQ(SEQ(LABEL(lab2), body.unNx()), JUMP(lab1))),
				LABEL(done)));
		return retVal;
	}
	
	//TODO
	public Exp ArrayExp(Exp size, Exp init) {
		Ex arrayEx;
		arrayEx = new Ex(frame.externalCall("initArray", ExpList(size.unEx(), ExpList(init.unEx()))));
		return arrayEx;
	}
	
	//TODO
	public Exp VarDec(Access a, Exp init) {
		Nx vDec;
		vDec = new Nx(MOVE(a.acc.exp(TEMP(a.home.frame.FP())), init.unEx()));
		return vDec;
	}

	//TODO
	public Exp SimpleVar(Access access, Level level) {
		Exp framePtr = TEMP(level.frame.FP());
		for (Level l = level; l != access.home; l = l.parent)
			framePtr = l.frame.formals.head.exp(framePtr);
		return new Ex(access.acc.exp(framePtr));
	}

	//TODO
	private Tree.Exp CallExp(Level f, ExpList args, Level from) {
		Exp framePtr = TEMP(from.frame.FP());
		if (f.parent != from) {
			for (Level l = from; l != f.parent; l = l.parent)
				framePtr = l.frame.formals.head.exp(framePtr);
		}
		return CALL(NAME(f.frame.name), ExpList(framePtr, ExpList(args)));
	}

	//TODO
	public Exp SubscriptVar(Exp array, Exp index) {
		int wordSize = frame.wordSize();
		Label badFrame = frame.badSub();

		Label lab1 = new Label();
		Label lab2 = new Label();

		// temporaries
		Temp t1 = new Temp();
		Temp t2 = new Temp();

		// return the IC
		Ex retCode;
		retCode = new Ex(ESEQ(
				SEQ(MOVE(TEMP(t1), array.unEx()),
						SEQ(MOVE(TEMP(t2), index.unEx()),
								SEQ(CJUMP(2, TEMP(t2), CONST(0), badFrame, lab2),
										SEQ(LABEL(lab2),
												SEQ(CJUMP(3, TEMP(t2), MEM(BINOP(0, TEMP(t1), CONST(-wordSize))),
														badFrame, lab1), LABEL(lab1)))))),
				MEM(BINOP(0, TEMP(t1), BINOP(2, TEMP(t2), CONST(wordSize))))));
		return retCode;
	}

	//TODO
	public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done) {
		Temp temporary = new Temp();
		Temp framePtr = i.home.frame.FP();
		Label b = new Label();
		Label inc = new Label();

		Nx retVal;
		retVal = new Nx(
				SEQ(SEQ(SEQ(SEQ(MOVE(i.acc.exp(TEMP(framePtr)), lo.unEx()), MOVE(TEMP(temporary), hi.unEx())),
						CJUMP(4, i.acc.exp(TEMP(framePtr)), TEMP(temporary), b, done)),
						SEQ(SEQ(SEQ(LABEL(b), body.unNx()),
								CJUMP(2, i.acc.exp(TEMP(framePtr)), TEMP(temporary), inc, done)),
								SEQ(SEQ(LABEL(inc),
										MOVE(i.acc.exp(TEMP(framePtr)), BINOP(0, i.acc.exp(TEMP(framePtr)), CONST(1)))),
										JUMP(b)))), LABEL(done)));
		return retVal;
	}

	//TODO
	public Exp FieldVar(Exp record, int index) {
		// adjust the index first
		index = index * frame.wordSize();

		// handle the good and bad ptrs
		Label badPtr = frame.badPtr();
		Label goodPtr = new Label();

		// new temporary
		Temp temporary = new Temp();

		// return it!
		Ex retVal;
		retVal = new Ex(ESEQ(
				SEQ(MOVE(TEMP(temporary), record.unEx()),
						SEQ(CJUMP(0, TEMP(temporary), CONST(0), badPtr, goodPtr), LABEL(goodPtr))),
				MEM(BINOP(0, TEMP(temporary), CONST(index)))));
		return retVal;
	}

	//TODO
	public Exp StrOpExp(int op, Exp left, Exp right) {
		Exp cmp = frame.externalCall("strcmp", ExpList(left.unEx(), ExpList(right.unEx())));
		switch (op) {
			case 4:
				return new RelCx(0, cmp, CONST(0));
			case 5:
				return new RelCx(1, cmp, CONST(0));
			case 6:
				return new RelCx(2, cmp, CONST(0));
			case 7:
				return new RelCx(4, cmp, CONST(0));
			case 8:
				return new RelCx(3, cmp, CONST(0));
			case 9:
				return new RelCx(5, cmp, CONST(0));
		}
		throw new Error("Str Op Exp Error!!");
	}

	//TODO
	public Exp LetExp(ExpList lets, Exp body) {
		Tree.Stm newStm = null;
		for (ExpList expList = lets; expList != null; expList = expList.tail)
			newStm = SEQ(newStm, expList.head.unNx());

		Exp result = body.unEx();
		if (result != null)
			return new Ex(ESEQ(newStm, result));
		else
			return new Nx(SEQ(newStm, body.unNx()));
	}
}
