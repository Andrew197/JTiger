package Semant;
class LoopSemant extends Semant{
    public LoopSemant(Env env){
		super(env);
	}
    public ExpTy transExp(Absyn.BreakExp e){
		//In a loop Semant, Break is allowed, and doesn't really do anything
		return new ExpTy(null,VOID);
	}
}
