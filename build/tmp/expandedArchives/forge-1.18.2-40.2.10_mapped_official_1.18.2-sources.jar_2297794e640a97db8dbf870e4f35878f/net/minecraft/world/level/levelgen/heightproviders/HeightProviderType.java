package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface HeightProviderType<P extends HeightProvider> {
   HeightProviderType<ConstantHeight> CONSTANT = register("constant", ConstantHeight.CODEC);
   HeightProviderType<UniformHeight> UNIFORM = register("uniform", UniformHeight.CODEC);
   HeightProviderType<BiasedToBottomHeight> BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomHeight.CODEC);
   HeightProviderType<VeryBiasedToBottomHeight> VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", VeryBiasedToBottomHeight.CODEC);
   HeightProviderType<TrapezoidHeight> TRAPEZOID = register("trapezoid", TrapezoidHeight.CODEC);
   HeightProviderType<WeightedListHeight> WEIGHTED_LIST = register("weighted_list", WeightedListHeight.CODEC);

   Codec<P> codec();

   private static <P extends HeightProvider> HeightProviderType<P> register(String p_161990_, Codec<P> p_161991_) {
      return Registry.register(Registry.HEIGHT_PROVIDER_TYPES, p_161990_, () -> {
         return p_161991_;
      });
   }
}