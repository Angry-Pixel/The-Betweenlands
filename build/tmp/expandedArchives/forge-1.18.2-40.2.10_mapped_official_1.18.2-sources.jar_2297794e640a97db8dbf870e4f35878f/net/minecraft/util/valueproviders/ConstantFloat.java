package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class ConstantFloat extends FloatProvider {
   public static final ConstantFloat ZERO = new ConstantFloat(0.0F);
   public static final Codec<ConstantFloat> CODEC = Codec.either(Codec.FLOAT, RecordCodecBuilder.<ConstantFloat>create((p_146465_) -> {
      return p_146465_.group(Codec.FLOAT.fieldOf("value").forGetter((p_146473_) -> {
         return p_146473_.value;
      })).apply(p_146465_, ConstantFloat::new);
   })).xmap((p_146463_) -> {
      return p_146463_.map(ConstantFloat::of, (p_146470_) -> {
         return p_146470_;
      });
   }, (p_146461_) -> {
      return Either.left(p_146461_.value);
   });
   private final float value;

   public static ConstantFloat of(float p_146459_) {
      return p_146459_ == 0.0F ? ZERO : new ConstantFloat(p_146459_);
   }

   private ConstantFloat(float p_146456_) {
      this.value = p_146456_;
   }

   public float getValue() {
      return this.value;
   }

   public float sample(Random p_146467_) {
      return this.value;
   }

   public float getMinValue() {
      return this.value;
   }

   public float getMaxValue() {
      return this.value + 1.0F;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.CONSTANT;
   }

   public String toString() {
      return Float.toString(this.value);
   }
}