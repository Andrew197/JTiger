package Semant;
class LoopSemant extends Semant{
    public LoopSemant(Env env){
		super(env);
	}
    public LoopSemant(Env env, boolean isDebug){
    	super(env,isDebug);
    }
    public ExpTy transExp(Absyn.BreakExp e){
		//In a loop Semant, Break is allowed, and doesn't really do anything
		return new ExpTy(null,VOID);
	}
}
