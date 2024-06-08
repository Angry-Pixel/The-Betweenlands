package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
   public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create((p_48644_) -> {
      return p_48644_.group(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter((p_151890_) -> {
         return null;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((p_151888_) -> {
         return p_151888_.seed;
      })).apply(p_48644_, p_48644_.stable(TheEndBiomeSource::new));
   });
   private static final float ISLAND_THRESHOLD = -0.9F;
   public static final int ISLAND_CHUNK_DISTANCE = 64;
   private static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
   private final SimplexNoise islandNoise;
   private final long seed;
   private final Holder<Biome> end;
   private final Holder<Biome> highlands;
   private final Holder<Biome> midlands;
   private final Holder<Biome> islands;
   private final Holder<Biome> barrens;

   public TheEndBiomeSource(Registry<Biome> p_48628_, long p_48629_) {
      this(p_48629_, p_48628_.getOrCreateHolder(Biomes.THE_END), p_48628_.getOrCreateHolder(Biomes.END_HIGHLANDS), p_48628_.getOrCreateHolder(Biomes.END_MIDLANDS), p_48628_.getOrCreateHolder(Biomes.SMALL_END_ISLANDS), p_48628_.getOrCreateHolder(Biomes.END_BARRENS));
   }

   private TheEndBiomeSource(long p_204285_, Holder<Biome> p_204286_, Holder<Biome> p_204287_, Holder<Biome> p_204288_, Holder<Biome> p_204289_, Holder<Biome> p_204290_) {
      super(ImmutableList.of(p_204286_, p_204287_, p_204288_, p_204289_, p_204290_));
      this.seed = p_204285_;
      this.end = p_204286_;
      this.highlands = p_204287_;
      this.midlands = p_204288_;
      this.islands = p_204289_;
      this.barrens = p_204290_;
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(p_204285_));
      worldgenrandom.consumeCount(17292);
      this.islandNoise = new SimplexNoise(worldgenrandom);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long p_48640_) {
      return new TheEndBiomeSource(p_48640_, this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   public Holder<Biome> getNoiseBiome(int p_204292_, int p_204293_, int p_204294_, Climate.Sampler p_204295_) {
      int i = p_204292_ >> 2;
      int j = p_204294_ >> 2;
      if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
         return this.end;
      } else {
         float f = getHeightValue(this.islandNoise, i * 2 + 1, j * 2 + 1);
         if (f > 40.0F) {
            return this.highlands;
         } else if (f >= 0.0F) {
            return this.midlands;
         } else {
            return f < -20.0F ? this.islands : this.barrens;
         }
      }
   }

   public boolean stable(long p_48654_) {
      return this.seed == p_48654_;
   }

   public static float getHeightValue(SimplexNoise p_48646_, int p_48647_, int p_48648_) {
      int i = p_48647_ / 2;
      int j = p_48648_ / 2;
      int k = p_48647_ % 2;
      int l = p_48648_ % 2;
      float f = 100.0F - Mth.sqrt((float)(p_48647_ * p_48647_ + p_48648_ * p_48648_)) * 8.0F;
      f = Mth.clamp(f, -100.0F, 80.0F);

      for(int i1 = -12; i1 <= 12; ++i1) {
         for(int j1 = -12; j1 <= 12; ++j1) {
            long k1 = (long)(i + i1);
            long l1 = (long)(j + j1);
            if (k1 * k1 + l1 * l1 > 4096L && p_48646_.getValue((double)k1, (double)l1) < (double)-0.9F) {
               float f1 = (Mth.abs((float)k1) * 3439.0F + Mth.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
               float f2 = (float)(k - i1 * 2);
               float f3 = (float)(l - j1 * 2);
               float f4 = 100.0F - Mth.sqrt(f2 * f2 + f3 * f3) * f1;
               f4 = Mth.clamp(f4, -100.0F, 80.0F);
               f = Math.max(f, f4);
            }
         }
      }

      return f;
   }
}