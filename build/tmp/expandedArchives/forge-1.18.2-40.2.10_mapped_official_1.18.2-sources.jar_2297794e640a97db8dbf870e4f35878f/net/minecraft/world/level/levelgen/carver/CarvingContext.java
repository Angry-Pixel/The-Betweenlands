package net.minecraft.world.level.levelgen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class CarvingContext extends WorldGenerationContext {
   private final NoiseBasedChunkGenerator generator;
   private final RegistryAccess registryAccess;
   private final NoiseChunk noiseChunk;

   public CarvingContext(NoiseBasedChunkGenerator p_190642_, RegistryAccess p_190643_, LevelHeightAccessor p_190644_, NoiseChunk p_190645_) {
      super(p_190642_, p_190644_);
      this.generator = p_190642_;
      this.registryAccess = p_190643_;
      this.noiseChunk = p_190645_;
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(Function<BlockPos, Holder<Biome>> p_190647_, ChunkAccess p_190648_, BlockPos p_190649_, boolean p_190650_) {
      return this.generator.topMaterial(this, p_190647_, p_190648_, this.noiseChunk, p_190649_, p_190650_);
   }

   /** @deprecated */
   @Deprecated
   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }
}