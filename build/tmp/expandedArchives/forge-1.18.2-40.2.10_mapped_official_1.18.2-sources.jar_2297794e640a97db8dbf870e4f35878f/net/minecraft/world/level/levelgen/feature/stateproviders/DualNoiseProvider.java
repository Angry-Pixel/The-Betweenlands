package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class DualNoiseProvider extends NoiseProvider {
   public static final Codec<DualNoiseProvider> CODEC = RecordCodecBuilder.create((p_191414_) -> {
      return p_191414_.group(InclusiveRange.codec(Codec.INT, 1, 64).fieldOf("variety").forGetter((p_191416_) -> {
         return p_191416_.variety;
      }), NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("slow_noise").forGetter((p_191412_) -> {
         return p_191412_.slowNoiseParameters;
      }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter((p_191405_) -> {
         return p_191405_.slowScale;
      })).and(noiseProviderCodec(p_191414_)).apply(p_191414_, DualNoiseProvider::new);
   });
   private final InclusiveRange<Integer> variety;
   private final NormalNoise.NoiseParameters slowNoiseParameters;
   private final float slowScale;
   private final NormalNoise slowNoise;

   public DualNoiseProvider(InclusiveRange<Integer> p_191396_, NormalNoise.NoiseParameters p_191397_, float p_191398_, long p_191399_, NormalNoise.NoiseParameters p_191400_, float p_191401_, List<BlockState> p_191402_) {
      super(p_191399_, p_191400_, p_191401_, p_191402_);
      this.variety = p_191396_;
      this.slowNoiseParameters = p_191397_;
      this.slowScale = p_191398_;
      this.slowNoise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(p_191399_)), p_191397_);
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.DUAL_NOISE_PROVIDER;
   }

   public BlockState getState(Random p_191409_, BlockPos p_191410_) {
      double d0 = this.getSlowNoiseValue(p_191410_);
      int i = (int)Mth.clampedMap(d0, -1.0D, 1.0D, (double)this.variety.minInclusive().intValue(), (double)(this.variety.maxInclusive() + 1));
      List<BlockState> list = Lists.newArrayListWithCapacity(i);

      for(int j = 0; j < i; ++j) {
         list.add(this.getRandomState(this.states, this.getSlowNoiseValue(p_191410_.offset(j * '\ud511', 0, j * '\u85ba'))));
      }

      return this.getRandomState(list, p_191410_, (double)this.scale);
   }

   protected double getSlowNoiseValue(BlockPos p_191407_) {
      return this.slowNoise.getValue((double)((float)p_191407_.getX() * this.slowScale), (double)((float)p_191407_.getY() * this.slowScale), (double)((float)p_191407_.getZ() * this.slowScale));
   }
}