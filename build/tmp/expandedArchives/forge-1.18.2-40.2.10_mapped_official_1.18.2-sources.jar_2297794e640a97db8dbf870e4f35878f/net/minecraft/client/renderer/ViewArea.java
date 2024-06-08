package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewArea {
   protected final LevelRenderer levelRenderer;
   protected final Level level;
   protected int chunkGridSizeY;
   protected int chunkGridSizeX;
   protected int chunkGridSizeZ;
   public ChunkRenderDispatcher.RenderChunk[] chunks;

   public ViewArea(ChunkRenderDispatcher p_110845_, Level p_110846_, int p_110847_, LevelRenderer p_110848_) {
      this.levelRenderer = p_110848_;
      this.level = p_110846_;
      this.setViewDistance(p_110847_);
      this.createChunks(p_110845_);
   }

   protected void createChunks(ChunkRenderDispatcher p_110865_) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("createChunks called from wrong thread: " + Thread.currentThread().getName());
      } else {
         int i = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
         this.chunks = new ChunkRenderDispatcher.RenderChunk[i];

         for(int j = 0; j < this.chunkGridSizeX; ++j) {
            for(int k = 0; k < this.chunkGridSizeY; ++k) {
               for(int l = 0; l < this.chunkGridSizeZ; ++l) {
                  int i1 = this.getChunkIndex(j, k, l);
                  this.chunks[i1] = p_110865_.new RenderChunk(i1, j * 16, k * 16, l * 16);
               }
            }
         }

      }
   }

   public void releaseAllBuffers() {
      for(ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk : this.chunks) {
         chunkrenderdispatcher$renderchunk.releaseBuffers();
      }

   }

   private int getChunkIndex(int p_110856_, int p_110857_, int p_110858_) {
      return (p_110858_ * this.chunkGridSizeY + p_110857_) * this.chunkGridSizeX + p_110856_;
   }

   protected void setViewDistance(int p_110854_) {
      int i = p_110854_ * 2 + 1;
      this.chunkGridSizeX = i;
      this.chunkGridSizeY = this.level.getSectionsCount();
      this.chunkGridSizeZ = i;
   }

   public void repositionCamera(double p_110851_, double p_110852_) {
      int i = Mth.ceil(p_110851_);
      int j = Mth.ceil(p_110852_);

      for(int k = 0; k < this.chunkGridSizeX; ++k) {
         int l = this.chunkGridSizeX * 16;
         int i1 = i - 8 - l / 2;
         int j1 = i1 + Math.floorMod(k * 16 - i1, l);

         for(int k1 = 0; k1 < this.chunkGridSizeZ; ++k1) {
            int l1 = this.chunkGridSizeZ * 16;
            int i2 = j - 8 - l1 / 2;
            int j2 = i2 + Math.floorMod(k1 * 16 - i2, l1);

            for(int k2 = 0; k2 < this.chunkGridSizeY; ++k2) {
               int l2 = this.level.getMinBuildHeight() + k2 * 16;
               ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = this.chunks[this.getChunkIndex(k, k2, k1)];
               BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
               if (j1 != blockpos.getX() || l2 != blockpos.getY() || j2 != blockpos.getZ()) {
                  chunkrenderdispatcher$renderchunk.setOrigin(j1, l2, j2);
               }
            }
         }
      }

   }

   public void setDirty(int p_110860_, int p_110861_, int p_110862_, boolean p_110863_) {
      int i = Math.floorMod(p_110860_, this.chunkGridSizeX);
      int j = Math.floorMod(p_110861_ - this.level.getMinSection(), this.chunkGridSizeY);
      int k = Math.floorMod(p_110862_, this.chunkGridSizeZ);
      ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = this.chunks[this.getChunkIndex(i, j, k)];
      chunkrenderdispatcher$renderchunk.setDirty(p_110863_);
   }

   @Nullable
   protected ChunkRenderDispatcher.RenderChunk getRenderChunkAt(BlockPos p_110867_) {
      int i = Mth.intFloorDiv(p_110867_.getX(), 16);
      int j = Mth.intFloorDiv(p_110867_.getY() - this.level.getMinBuildHeight(), 16);
      int k = Mth.intFloorDiv(p_110867_.getZ(), 16);
      if (j >= 0 && j < this.chunkGridSizeY) {
         i = Mth.positiveModulo(i, this.chunkGridSizeX);
         k = Mth.positiveModulo(k, this.chunkGridSizeZ);
         return this.chunks[this.getChunkIndex(i, j, k)];
      } else {
         return null;
      }
   }
}