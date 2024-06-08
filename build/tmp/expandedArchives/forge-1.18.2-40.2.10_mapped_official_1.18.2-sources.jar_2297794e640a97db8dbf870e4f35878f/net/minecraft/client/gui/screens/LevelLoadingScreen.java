package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LevelLoadingScreen extends Screen {
   private static final long NARRATION_DELAY_MS = 2000L;
   private final StoringChunkProgressListener progressListener;
   private long lastNarration = -1L;
   private boolean done;
   private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), (p_96157_) -> {
      p_96157_.defaultReturnValue(0);
      p_96157_.put(ChunkStatus.EMPTY, 5526612);
      p_96157_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      p_96157_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      p_96157_.put(ChunkStatus.BIOMES, 8434258);
      p_96157_.put(ChunkStatus.NOISE, 13750737);
      p_96157_.put(ChunkStatus.SURFACE, 7497737);
      p_96157_.put(ChunkStatus.CARVERS, 7169628);
      p_96157_.put(ChunkStatus.LIQUID_CARVERS, 3159410);
      p_96157_.put(ChunkStatus.FEATURES, 2213376);
      p_96157_.put(ChunkStatus.LIGHT, 13421772);
      p_96157_.put(ChunkStatus.SPAWN, 15884384);
      p_96157_.put(ChunkStatus.HEIGHTMAPS, 15658734);
      p_96157_.put(ChunkStatus.FULL, 16777215);
   });

   public LevelLoadingScreen(StoringChunkProgressListener p_96143_) {
      super(NarratorChatListener.NO_TITLE);
      this.progressListener = p_96143_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      this.done = true;
      this.triggerImmediateNarration(true);
   }

   protected void updateNarratedWidget(NarrationElementOutput p_169312_) {
      if (this.done) {
         p_169312_.add(NarratedElementType.TITLE, new TranslatableComponent("narrator.loading.done"));
      } else {
         String s = this.getFormattedProgress();
         p_169312_.add(NarratedElementType.TITLE, s);
      }

   }

   private String getFormattedProgress() {
      return Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
   }

   public void render(PoseStack p_96145_, int p_96146_, int p_96147_, float p_96148_) {
      this.renderBackground(p_96145_);
      long i = Util.getMillis();
      if (i - this.lastNarration > 2000L) {
         this.lastNarration = i;
         this.triggerImmediateNarration(true);
      }

      int j = this.width / 2;
      int k = this.height / 2;
      int l = 30;
      renderChunks(p_96145_, this.progressListener, j, k + 30, 2, 0);
      drawCenteredString(p_96145_, this.font, this.getFormattedProgress(), j, k - 9 / 2 - 30, 16777215);
   }

   public static void renderChunks(PoseStack p_96150_, StoringChunkProgressListener p_96151_, int p_96152_, int p_96153_, int p_96154_, int p_96155_) {
      int i = p_96154_ + p_96155_;
      int j = p_96151_.getFullDiameter();
      int k = j * i - p_96155_;
      int l = p_96151_.getDiameter();
      int i1 = l * i - p_96155_;
      int j1 = p_96152_ - i1 / 2;
      int k1 = p_96153_ - i1 / 2;
      int l1 = k / 2 + 1;
      int i2 = -16772609;
      if (p_96155_ != 0) {
         fill(p_96150_, p_96152_ - l1, p_96153_ - l1, p_96152_ - l1 + 1, p_96153_ + l1, -16772609);
         fill(p_96150_, p_96152_ + l1 - 1, p_96153_ - l1, p_96152_ + l1, p_96153_ + l1, -16772609);
         fill(p_96150_, p_96152_ - l1, p_96153_ - l1, p_96152_ + l1, p_96153_ - l1 + 1, -16772609);
         fill(p_96150_, p_96152_ - l1, p_96153_ + l1 - 1, p_96152_ + l1, p_96153_ + l1, -16772609);
      }

      for(int j2 = 0; j2 < l; ++j2) {
         for(int k2 = 0; k2 < l; ++k2) {
            ChunkStatus chunkstatus = p_96151_.getStatus(j2, k2);
            int l2 = j1 + j2 * i;
            int i3 = k1 + k2 * i;
            fill(p_96150_, l2, i3, l2 + p_96154_, i3 + p_96154_, COLORS.getInt(chunkstatus) | -16777216);
         }
      }

   }
}