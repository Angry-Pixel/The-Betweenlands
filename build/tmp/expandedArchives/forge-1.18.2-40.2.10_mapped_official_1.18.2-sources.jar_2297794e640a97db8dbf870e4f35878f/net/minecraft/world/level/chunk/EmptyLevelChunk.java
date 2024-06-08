package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class EmptyLevelChunk extends LevelChunk {
   private final Holder<Biome> biome;

   public EmptyLevelChunk(Level p_204422_, ChunkPos p_204423_, Holder<Biome> p_204424_) {
      super(p_204422_, p_204423_);
      this.biome = p_204424_;
   }

   public BlockState getBlockState(BlockPos p_62625_) {
      return Blocks.VOID_AIR.defaultBlockState();
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_62605_, BlockState p_62606_, boolean p_62607_) {
      return null;
   }

   public FluidState getFluidState(BlockPos p_62621_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getLightEmission(BlockPos p_62628_) {
      return 0;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_62609_, LevelChunk.EntityCreationType p_62610_) {
      return null;
   }

   public void addAndRegisterBlockEntity(BlockEntity p_156346_) {
   }

   public void setBlockEntity(BlockEntity p_156344_) {
   }

   public void removeBlockEntity(BlockPos p_62623_) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isYSpaceEmpty(int p_62587_, int p_62588_) {
      return true;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return ChunkHolder.FullChunkStatus.BORDER;
   }

   public Holder<Biome> getNoiseBiome(int p_204426_, int p_204427_, int p_204428_) {
      return this.biome;
   }
}