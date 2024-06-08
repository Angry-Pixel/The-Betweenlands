package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final Codec<ConstantHeight> CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.<ConstantHeight>create((p_161955_) -> {
      return p_161955_.group(VerticalAnchor.CODEC.fieldOf("value").forGetter((p_161967_) -> {
         return p_161967_.value;
      })).apply(p_161955_, ConstantHeight::new);
   })).xmap((p_161953_) -> {
      return p_161953_.map(ConstantHeight::of, (p_161965_) -> {
         return p_161965_;
      });
   }, (p_161959_) -> {
      return Either.left(p_161959_.value);
   });
   private final VerticalAnchor value;

   public static ConstantHeight of(VerticalAnchor p_161957_) {
      return new ConstantHeight(p_161957_);
   }

   private ConstantHeight(VerticalAnchor p_161950_) {
      this.value = p_161950_;
   }

   public VerticalAnchor getValue() {
      return this.value;
   }

   public int sample(Random p_161961_, WorldGenerationContext p_161962_) {
      return this.value.resolveY(p_161962_);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.CONSTANT;
   }

   public String toString() {
      return this.value.toString();
   }
}