package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseProvider extends NoiseBasedStateProvider {
   public static final Codec<NoiseProvider> CODEC = RecordCodecBuilder.create((p_191462_) -> {
      return noiseProviderCodec(p_191462_).apply(p_191462_, NoiseProvider::new);
   });
   protected final List<BlockState> states;

   protected static <P extends NoiseProvider> P4<Mu<P>, Long, NormalNoise.NoiseParameters, Float, List<BlockState>> noiseProviderCodec(Instance<P> p_191460_) {
      return noiseCodec(p_191460_).and(Codec.list(BlockState.CODEC).fieldOf("states").forGetter((p_191448_) -> {
         return p_191448_.states;
      }));
   }

   public NoiseProvider(long p_191442_, NormalNoise.NoiseParameters p_191443_, float p_191444_, List<BlockState> p_191445_) {
      super(p_191442_, p_191443_, p_191444_);
      this.states = p_191445_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.NOISE_PROVIDER;
   }

   public BlockState getState(Random p_191457_, BlockPos p_191458_) {
      return this.getRandomState(this.states, p_191458_, (double)this.scale);
   }

   protected BlockState getRandomState(List<BlockState> p_191453_, BlockPos p_191454_, double p_191455_) {
      double d0 = this.getNoiseValue(p_191454_, p_191455_);
      return this.getRandomState(p_191453_, d0);
   }

   protected BlockState getRandomState(List<BlockState> p_191450_, double p_191451_) {
      double d0 = Mth.clamp((1.0D + p_191451_) / 2.0D, 0.0D, 0.9999D);
      return p_191450_.get((int)(d0 * (double)p_191450_.size()));
   }
}