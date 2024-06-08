package net.minecraft.world;

public class InteractionResultHolder<T> {
   private final InteractionResult result;
   private final T object;

   public InteractionResultHolder(InteractionResult p_19087_, T p_19088_) {
      this.result = p_19087_;
      this.object = p_19088_;
   }

   public InteractionResult getResult() {
      return this.result;
   }

   public T getObject() {
      return this.object;
   }

   public static <T> InteractionResultHolder<T> success(T p_19091_) {
      return new InteractionResultHolder<>(InteractionResult.SUCCESS, p_19091_);
   }

   public static <T> InteractionResultHolder<T> consume(T p_19097_) {
      return new InteractionResultHolder<>(InteractionResult.CONSUME, p_19097_);
   }

   public static <T> InteractionResultHolder<T> pass(T p_19099_) {
      return new InteractionResultHolder<>(InteractionResult.PASS, p_19099_);
   }

   public static <T> InteractionResultHolder<T> fail(T p_19101_) {
      return new InteractionResultHolder<>(InteractionResult.FAIL, p_19101_);
   }

   public static <T> InteractionResultHolder<T> sidedSuccess(T p_19093_, boolean p_19094_) {
      return p_19094_ ? success(p_19093_) : consume(p_19093_);
   }
}