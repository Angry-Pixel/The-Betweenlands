package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface FloatProviderType<P extends FloatProvider> {
   FloatProviderType<ConstantFloat> CONSTANT = register("constant", ConstantFloat.CODEC);
   FloatProviderType<UniformFloat> UNIFORM = register("uniform", UniformFloat.CODEC);
   FloatProviderType<ClampedNormalFloat> CLAMPED_NORMAL = register("clamped_normal", ClampedNormalFloat.CODEC);
   FloatProviderType<TrapezoidFloat> TRAPEZOID = register("trapezoid", TrapezoidFloat.CODEC);

   Codec<P> codec();

   static <P extends FloatProvider> FloatProviderType<P> register(String p_146527_, Codec<P> p_146528_) {
      return Registry.register(Registry.FLOAT_PROVIDER_TYPES, p_146527_, () -> {
         return p_146528_;
      });
   }
}