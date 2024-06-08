package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunkRegion implements BlockAndTintGetter {
   private final int centerX;
   private final int centerZ;
   protected final RenderChunk[][] chunks;
   protected final Level level;

   RenderChunkRegion(Level p_200456_, int p_200457_, int p_200458_, RenderChunk[][] p_200459_) {
      this.level = p_200456_;
      this.centerX = p_200457_;
      this.centerZ = p_200458_;
      this.chunks = p_200459_;
   }

   public BlockState getBlockState(BlockPos p_112947_) {
      int i = SectionPos.blockToSectionCoord(p_112947_.getX()) - this.centerX;
      int j = SectionPos.blockToSectionCoord(p_112947_.getZ()) - this.centerZ;
      return this.chunks[i][j].getBlockState(p_112947_);
   }

   public FluidState getFluidState(BlockPos p_112943_) {
      int i = SectionPos.blockToSectionCoord(p_112943_.getX()) - this.centerX;
      int j = SectionPos.blockToSectionCoord(p_112943_.getZ()) - this.centerZ;
      return this.chunks[i][j].getBlockState(p_112943_).getFluidState();
   }

   public float getShade(Direction p_112940_, boolean p_112941_) {
      return this.level.getShade(p_112940_, p_112941_);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_112945_) {
      int i = SectionPos.blockToSectionCoord(p_112945_.getX()) - this.centerX;
      int j = SectionPos.blockToSectionCoord(p_112945_.getZ()) - this.centerZ;
      return this.chunks[i][j].getBlockEntity(p_112945_);
   }

   public int getBlockTint(BlockPos p_112937_, ColorResolver p_112938_) {
      return this.level.getBlockTint(p_112937_, p_112938_);
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }
}