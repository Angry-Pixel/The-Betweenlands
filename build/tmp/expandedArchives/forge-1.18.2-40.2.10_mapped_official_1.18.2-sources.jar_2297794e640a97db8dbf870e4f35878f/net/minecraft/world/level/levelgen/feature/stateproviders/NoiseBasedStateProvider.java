package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public abstract class NoiseBasedStateProvider extends BlockStateProvider {
   protected final long seed;
   protected final NormalNoise.NoiseParameters parameters;
   protected final float scale;
   protected final NormalNoise noise;

   protected static <P extends NoiseBasedStateProvider> P3<Mu<P>, Long, NormalNoise.NoiseParameters, Float> noiseCodec(Instance<P> p_191426_) {
      return p_191426_.group(Codec.LONG.fieldOf("seed").forGetter((p_191435_) -> {
         return p_191435_.seed;
      }), NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("noise").forGetter((p_191433_) -> {
         return p_191433_.parameters;
      }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter((p_191428_) -> {
         return p_191428_.scale;
      }));
   }

   protected NoiseBasedStateProvider(long p_191422_, NormalNoise.NoiseParameters p_191423_, float p_191424_) {
      this.seed = p_191422_;
      this.parameters = p_191423_;
      this.scale = p_191424_;
      this.noise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(p_191422_)), p_191423_);
   }

   protected double getNoiseValue(BlockPos p_191430_, double p_191431_) {
      return this.noise.getValue((double)p_191430_.getX() * p_191431_, (double)p_191430_.getY() * p_191431_, (double)p_191430_.getZ() * p_191431_);
   }
}