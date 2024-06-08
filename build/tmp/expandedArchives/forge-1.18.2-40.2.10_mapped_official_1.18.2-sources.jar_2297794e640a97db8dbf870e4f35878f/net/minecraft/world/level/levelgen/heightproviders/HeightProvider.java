package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public abstract class HeightProvider {
   private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(VerticalAnchor.CODEC, Registry.HEIGHT_PROVIDER_TYPES.byNameCodec().dispatch(HeightProvider::getType, HeightProviderType::codec));
   public static final Codec<HeightProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((p_161974_) -> {
      return p_161974_.map(ConstantHeight::of, (p_161980_) -> {
         return p_161980_;
      });
   }, (p_161976_) -> {
      return p_161976_.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeight)p_161976_).getValue()) : Either.right(p_161976_);
   });

   public abstract int sample(Random p_161977_, WorldGenerationContext p_161978_);

   public abstract HeightProviderType<?> getType();
}