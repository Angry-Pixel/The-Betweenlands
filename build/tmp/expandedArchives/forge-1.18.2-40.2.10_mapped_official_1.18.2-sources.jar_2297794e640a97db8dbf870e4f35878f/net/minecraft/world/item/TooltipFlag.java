package net.minecraft.world.item;

public interface TooltipFlag {
   boolean isAdvanced();

   public static enum Default implements TooltipFlag {
      NORMAL(false),
      ADVANCED(true);

      private final boolean advanced;

      private Default(boolean p_43374_) {
         this.advanced = p_43374_;
      }

      public boolean isAdvanced() {
         return this.advanced;
      }
   }
}