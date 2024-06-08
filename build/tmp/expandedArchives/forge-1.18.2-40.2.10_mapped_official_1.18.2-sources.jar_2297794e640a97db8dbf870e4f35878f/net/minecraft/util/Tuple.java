package net.minecraft.util;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(A p_14416_, B p_14417_) {
      this.a = p_14416_;
      this.b = p_14417_;
   }

   public A getA() {
      return this.a;
   }

   public void setA(A p_145024_) {
      this.a = p_145024_;
   }

   public B getB() {
      return this.b;
   }

   public void setB(B p_145026_) {
      this.b = p_145026_;
   }
}