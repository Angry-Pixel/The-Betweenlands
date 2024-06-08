package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private final int radius = 12;
   @Nullable
   private ChunkDebugRenderer.ChunkData data;

   public ChunkDebugRenderer(Minecraft p_113368_) {
      this.minecraft = p_113368_;
   }

   public void render(PoseStack p_113370_, MultiBufferSource p_113371_, double p_113372_, double p_113373_, double p_113374_) {
      double d0 = (double)Util.getNanos();
      if (d0 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = d0;
         IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
         if (integratedserver != null) {
            this.data = new ChunkDebugRenderer.ChunkData(integratedserver, p_113372_, p_113374_);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.disableTexture();
         RenderSystem.depthMask(false);
         Map<ChunkPos, String> map = this.data.serverData.getNow((Map<ChunkPos, String>)null);
         double d1 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85D;

         for(Entry<ChunkPos, String> entry : this.data.clientData.entrySet()) {
            ChunkPos chunkpos = entry.getKey();
            String s = entry.getValue();
            if (map != null) {
               s = s + (String)map.get(chunkpos);
            }

            String[] astring = s.split("\n");
            int i = 0;

            for(String s1 : astring) {
               DebugRenderer.renderFloatingText(s1, (double)SectionPos.sectionToBlockCoord(chunkpos.x, 8), d1 + (double)i, (double)SectionPos.sectionToBlockCoord(chunkpos.z, 8), -1, 0.15F);
               i -= 2;
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }

   }

   @OnlyIn(Dist.CLIENT)
   final class ChunkData {
      final Map<ChunkPos, String> clientData;
      final CompletableFuture<Map<ChunkPos, String>> serverData;

      ChunkData(IntegratedServer p_113382_, double p_113383_, double p_113384_) {
         ClientLevel clientlevel = ChunkDebugRenderer.this.minecraft.level;
         ResourceKey<Level> resourcekey = clientlevel.dimension();
         int i = SectionPos.posToSectionCoord(p_113383_);
         int j = SectionPos.posToSectionCoord(p_113384_);
         Builder<ChunkPos, String> builder = ImmutableMap.builder();
         ClientChunkCache clientchunkcache = clientlevel.getChunkSource();

         for(int k = i - 12; k <= i + 12; ++k) {
            for(int l = j - 12; l <= j + 12; ++l) {
               ChunkPos chunkpos = new ChunkPos(k, l);
               String s = "";
               LevelChunk levelchunk = clientchunkcache.getChunk(k, l, false);
               s = s + "Client: ";
               if (levelchunk == null) {
                  s = s + "0n/a\n";
               } else {
                  s = s + (levelchunk.isEmpty() ? " E" : "");
                  s = s + "\n";
               }

               builder.put(chunkpos, s);
            }
         }

         this.clientData = builder.build();
         this.serverData = p_113382_.submit(() -> {
            ServerLevel serverlevel = p_113382_.getLevel(resourcekey);
            if (serverlevel == null) {
               return ImmutableMap.of();
            } else {
               Builder<ChunkPos, String> builder1 = ImmutableMap.builder();
               ServerChunkCache serverchunkcache = serverlevel.getChunkSource();

               for(int i1 = i - 12; i1 <= i + 12; ++i1) {
                  for(int j1 = j - 12; j1 <= j + 12; ++j1) {
                     ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                     builder1.put(chunkpos1, "Server: " + serverchunkcache.getChunkDebugData(chunkpos1));
                  }
               }

               return builder1.build();
            }
         });
      }
   }
}