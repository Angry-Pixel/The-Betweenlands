package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class ConstantInt extends IntProvider {
   public static final ConstantInt ZERO = new ConstantInt(0);
   public static final Codec<ConstantInt> CODEC = Codec.either(Codec.INT, RecordCodecBuilder.<ConstantInt>create((p_146490_) -> {
      return p_146490_.group(Codec.INT.fieldOf("value").forGetter((p_146498_) -> {
         return p_146498_.value;
      })).apply(p_146490_, ConstantInt::new);
   })).xmap((p_146488_) -> {
      return p_146488_.map(ConstantInt::of, (p_146495_) -> {
         return p_146495_;
      });
   }, (p_146486_) -> {
      return Either.left(p_146486_.value);
   });
   private final int value;

   public static ConstantInt of(int p_146484_) {
      return p_146484_ == 0 ? ZERO : new ConstantInt(p_146484_);
   }

   private ConstantInt(int p_146481_) {
      this.value = p_146481_;
   }

   public int getValue() {
      return this.value;
   }

   public int sample(Random p_146492_) {
      return this.value;
   }

   public int getMinValue() {
      return this.value;
   }

   public int getMaxValue() {
      return this.value;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CONSTANT;
   }

   public String toString() {
      return Integer.toString(this.value);
   }
}