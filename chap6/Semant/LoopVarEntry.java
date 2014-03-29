package Semant;

class LoopVarEntry extends VarEntry {
  Translate.Access access;
  LoopVarEntry(Translate.Access a, Types.Type t) {
    super(a, t);
  }
}
