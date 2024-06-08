package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientChunkCache extends ChunkSource {
   static final Logger LOGGER = LogUtils.getLogger();
   private final LevelChunk emptyChunk;
   public LevelLightEngine lightEngine;
   volatile ClientChunkCache.Storage storage;
   final ClientLevel level;

   public ClientChunkCache(ClientLevel p_104414_, int p_104415_) {
      this.level = p_104414_;
      this.emptyChunk = new EmptyLevelChunk(p_104414_, new ChunkPos(0, 0), p_104414_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(Biomes.PLAINS));
      this.lightEngine = new LevelLightEngine(this, true, p_104414_.dimensionType().hasSkyLight());
      this.storage = new ClientChunkCache.Storage(calculateStorageRange(p_104415_));
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   private static boolean isValidChunk(@Nullable LevelChunk p_104439_, int p_104440_, int p_104441_) {
      if (p_104439_ == null) {
         return false;
      } else {
         ChunkPos chunkpos = p_104439_.getPos();
         return chunkpos.x == p_104440_ && chunkpos.z == p_104441_;
      }
   }

   public void drop(int p_104456_, int p_104457_) {
      if (this.storage.inRange(p_104456_, p_104457_)) {
         int i = this.storage.getIndex(p_104456_, p_104457_);
         LevelChunk levelchunk = this.storage.getChunk(i);
         if (isValidChunk(levelchunk, p_104456_, p_104457_)) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(levelchunk));
            this.storage.replace(i, levelchunk, (LevelChunk)null);
         }

      }
   }

   @Nullable
   public LevelChunk getChunk(int p_104451_, int p_104452_, ChunkStatus p_104453_, boolean p_104454_) {
      if (this.storage.inRange(p_104451_, p_104452_)) {
         LevelChunk levelchunk = this.storage.getChunk(this.storage.getIndex(p_104451_, p_104452_));
         if (isValidChunk(levelchunk, p_104451_, p_104452_)) {
            return levelchunk;
         }
      }

      return p_104454_ ? this.emptyChunk : null;
   }

   public BlockGetter getLevel() {
      return this.level;
   }

   @Nullable
   public LevelChunk replaceWithPacketData(int p_194117_, int p_194118_, FriendlyByteBuf p_194119_, CompoundTag p_194120_, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> p_194121_) {
      if (!this.storage.inRange(p_194117_, p_194118_)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", p_194117_, p_194118_);
         return null;
      } else {
         int i = this.storage.getIndex(p_194117_, p_194118_);
         LevelChunk levelchunk = this.storage.chunks.get(i);
         ChunkPos chunkpos = new ChunkPos(p_194117_, p_194118_);
         if (!isValidChunk(levelchunk, p_194117_, p_194118_)) {
            levelchunk = new LevelChunk(this.level, chunkpos);
            levelchunk.replaceWithPacketData(p_194119_, p_194120_, p_194121_);
            this.storage.replace(i, levelchunk);
         } else {
            levelchunk.replaceWithPacketData(p_194119_, p_194120_, p_194121_);
         }

         this.level.onChunkLoaded(chunkpos);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(levelchunk));
         return levelchunk;
      }
   }

   public void tick(BooleanSupplier p_202421_, boolean p_202422_) {
   }

   public void updateViewCenter(int p_104460_, int p_104461_) {
      this.storage.viewCenterX = p_104460_;
      this.storage.viewCenterZ = p_104461_;
   }

   public void updateViewRadius(int p_104417_) {
      int i = this.storage.chunkRadius;
      int j = calculateStorageRange(p_104417_);
      if (i != j) {
         ClientChunkCache.Storage clientchunkcache$storage = new ClientChunkCache.Storage(j);
         clientchunkcache$storage.viewCenterX = this.storage.viewCenterX;
         clientchunkcache$storage.viewCenterZ = this.storage.viewCenterZ;

         for(int k = 0; k < this.storage.chunks.length(); ++k) {
            LevelChunk levelchunk = this.storage.chunks.get(k);
            if (levelchunk != null) {
               ChunkPos chunkpos = levelchunk.getPos();
               if (clientchunkcache$storage.inRange(chunkpos.x, chunkpos.z)) {
                  clientchunkcache$storage.replace(clientchunkcache$storage.getIndex(chunkpos.x, chunkpos.z), levelchunk);
               }
            }
         }

         this.storage = clientchunkcache$storage;
      }

   }

   private static int calculateStorageRange(int p_104449_) {
      return Math.max(2, p_104449_) + 3;
   }

   public String gatherStats() {
      return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   public void onLightUpdate(LightLayer p_104436_, SectionPos p_104437_) {
      Minecraft.getInstance().levelRenderer.setSectionDirty(p_104437_.x(), p_104437_.y(), p_104437_.z());
   }

   @OnlyIn(Dist.CLIENT)
   final class Storage {
      final AtomicReferenceArray<LevelChunk> chunks;
      final int chunkRadius;
      private final int viewRange;
      volatile int viewCenterX;
      volatile int viewCenterZ;
      int chunkCount;

      Storage(int p_104474_) {
         this.chunkRadius = p_104474_;
         this.viewRange = p_104474_ * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.viewRange * this.viewRange);
      }

      int getIndex(int p_104482_, int p_104483_) {
         return Math.floorMod(p_104483_, this.viewRange) * this.viewRange + Math.floorMod(p_104482_, this.viewRange);
      }

      protected void replace(int p_104485_, @Nullable LevelChunk p_104486_) {
         LevelChunk levelchunk = this.chunks.getAndSet(p_104485_, p_104486_);
         if (levelchunk != null) {
            --this.chunkCount;
            ClientChunkCache.this.level.unload(levelchunk);
         }

         if (p_104486_ != null) {
            ++this.chunkCount;
         }

      }

      protected LevelChunk replace(int p_104488_, LevelChunk p_104489_, @Nullable LevelChunk p_104490_) {
         if (this.chunks.compareAndSet(p_104488_, p_104489_, p_104490_) && p_104490_ == null) {
            --this.chunkCount;
         }

         ClientChunkCache.this.level.unload(p_104489_);
         return p_104489_;
      }

      boolean inRange(int p_104501_, int p_104502_) {
         return Math.abs(p_104501_ - this.viewCenterX) <= this.chunkRadius && Math.abs(p_104502_ - this.viewCenterZ) <= this.chunkRadius;
      }

      @Nullable
      protected LevelChunk getChunk(int p_104480_) {
         return this.chunks.get(p_104480_);
      }

      private void dumpChunks(String p_171623_) {
         try {
            FileOutputStream fileoutputstream = new FileOutputStream(p_171623_);

            try {
               int i = ClientChunkCache.this.storage.chunkRadius;

               for(int j = this.viewCenterZ - i; j <= this.viewCenterZ + i; ++j) {
                  for(int k = this.viewCenterX - i; k <= this.viewCenterX + i; ++k) {
                     LevelChunk levelchunk = ClientChunkCache.this.storage.chunks.get(ClientChunkCache.this.storage.getIndex(k, j));
                     if (levelchunk != null) {
                        ChunkPos chunkpos = levelchunk.getPos();
                        fileoutputstream.write((chunkpos.x + "\t" + chunkpos.z + "\t" + levelchunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                     }
                  }
               }
            } catch (Throwable throwable1) {
               try {
                  fileoutputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }

               throw throwable1;
            }

            fileoutputstream.close();
         } catch (IOException ioexception) {
            ClientChunkCache.LOGGER.error("Failed to dump chunks to file {}", p_171623_, ioexception);
         }

      }
   }
}
