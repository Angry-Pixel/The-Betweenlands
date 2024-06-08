package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> extends net.minecraftforge.registries.ForgeRegistryEntry<BlockStateProviderType<?>> {
   public static final BlockStateProviderType<SimpleStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleStateProvider.CODEC);
   public static final BlockStateProviderType<WeightedStateProvider> WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedStateProvider.CODEC);
   public static final BlockStateProviderType<NoiseThresholdProvider> NOISE_THRESHOLD_PROVIDER = register("noise_threshold_provider", NoiseThresholdProvider.CODEC);
   public static final BlockStateProviderType<NoiseProvider> NOISE_PROVIDER = register("noise_provider", NoiseProvider.CODEC);
   public static final BlockStateProviderType<DualNoiseProvider> DUAL_NOISE_PROVIDER = register("dual_noise_provider", DualNoiseProvider.CODEC);
   public static final BlockStateProviderType<RotatedBlockProvider> ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", RotatedBlockProvider.CODEC);
   public static final BlockStateProviderType<RandomizedIntStateProvider> RANDOMIZED_INT_STATE_PROVIDER = register("randomized_int_state_provider", RandomizedIntStateProvider.CODEC);
   private final Codec<P> codec;

   private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String p_68763_, Codec<P> p_68764_) {
      return Registry.register(Registry.BLOCKSTATE_PROVIDER_TYPES, p_68763_, new BlockStateProviderType<>(p_68764_));
   }

   public BlockStateProviderType(Codec<P> p_68760_) {
      this.codec = p_68760_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
