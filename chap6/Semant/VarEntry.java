package Semant;

public class VarEntry extends Entry {
  Translate.Access access;
  public Types.Type ty;
  VarEntry(Translate.Access a, Types.Type t) {
    ty = t;
    access = a;
  }
}
