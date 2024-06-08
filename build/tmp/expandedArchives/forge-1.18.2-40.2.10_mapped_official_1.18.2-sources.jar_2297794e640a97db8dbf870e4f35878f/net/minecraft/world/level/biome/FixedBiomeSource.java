package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

public class FixedBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final Codec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (p_204259_) -> {
      return p_204259_.biome;
   }).stable().codec();
   private final Holder<Biome> biome;

   public FixedBiomeSource(Holder<Biome> p_204257_) {
      super(ImmutableList.of(p_204257_));
      this.biome = p_204257_;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long p_48274_) {
      return this;
   }

   public Holder<Biome> getNoiseBiome(int p_204265_, int p_204266_, int p_204267_, Climate.Sampler p_204268_) {
      return this.biome;
   }

   public Holder<Biome> getNoiseBiome(int p_204261_, int p_204262_, int p_204263_) {
      return this.biome;
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int p_207885_, int p_207886_, int p_207887_, int p_207888_, int p_207889_, Predicate<Holder<Biome>> p_207890_, Random p_207891_, boolean p_207892_, Climate.Sampler p_207893_) {
      if (p_207890_.test(this.biome)) {
         return p_207892_ ? Pair.of(new BlockPos(p_207885_, p_207886_, p_207887_), this.biome) : Pair.of(new BlockPos(p_207885_ - p_207888_ + p_207891_.nextInt(p_207888_ * 2 + 1), p_207886_, p_207887_ - p_207888_ + p_207891_.nextInt(p_207888_ * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   public Set<Holder<Biome>> getBiomesWithin(int p_187038_, int p_187039_, int p_187040_, int p_187041_, Climate.Sampler p_187042_) {
      return Sets.newHashSet(Set.of(this.biome));
   }
}