package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Registry;

public abstract class IntProvider {
   private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, Registry.INT_PROVIDER_TYPES.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
   public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((p_146543_) -> {
      return p_146543_.map(ConstantInt::of, (p_146549_) -> {
         return p_146549_;
      });
   }, (p_146541_) -> {
      return p_146541_.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt)p_146541_).getValue()) : Either.right(p_146541_);
   });
   public static final Codec<IntProvider> NON_NEGATIVE_CODEC = codec(0, Integer.MAX_VALUE);
   public static final Codec<IntProvider> POSITIVE_CODEC = codec(1, Integer.MAX_VALUE);

   public static Codec<IntProvider> codec(int p_146546_, int p_146547_) {
      Function<IntProvider, DataResult<IntProvider>> function = (p_146539_) -> {
         if (p_146539_.getMinValue() < p_146546_) {
            return DataResult.error("Value provider too low: " + p_146546_ + " [" + p_146539_.getMinValue() + "-" + p_146539_.getMaxValue() + "]");
         } else {
            return p_146539_.getMaxValue() > p_146547_ ? DataResult.error("Value provider too high: " + p_146547_ + " [" + p_146539_.getMinValue() + "-" + p_146539_.getMaxValue() + "]") : DataResult.success(p_146539_);
         }
      };
      return CODEC.flatXmap(function, function);
   }

   public abstract int sample(Random p_146544_);

   public abstract int getMinValue();

   public abstract int getMaxValue();

   public abstract IntProviderType<?> getType();
}