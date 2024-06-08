package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossHealthOverlay extends GuiComponent {
   private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
   private static final int BAR_WIDTH = 182;
   private static final int BAR_HEIGHT = 5;
   private static final int OVERLAY_OFFSET = 80;
   private final Minecraft minecraft;
   final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

   public BossHealthOverlay(Minecraft p_93702_) {
      this.minecraft = p_93702_;
   }

   public void render(PoseStack p_93705_) {
      if (!this.events.isEmpty()) {
         int i = this.minecraft.getWindow().getGuiScaledWidth();
         int j = 12;

         for(LerpingBossEvent lerpingbossevent : this.events.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
               net.minecraftforge.client.ForgeHooksClient.renderBossEventPre(p_93705_, this.minecraft.getWindow(), lerpingbossevent, k, j, 10 + this.minecraft.font.lineHeight);
            if (!event.isCanceled()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
            this.drawBar(p_93705_, k, j, lerpingbossevent);
            Component component = lerpingbossevent.getName();
            int l = this.minecraft.font.width(component);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.minecraft.font.drawShadow(p_93705_, component, (float)i1, (float)j1, 16777215);
            }
            j += event.getIncrement();
            net.minecraftforge.client.ForgeHooksClient.renderBossEventPost(p_93705_, this.minecraft.getWindow());
            if (j >= this.minecraft.getWindow().getGuiScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void drawBar(PoseStack p_93707_, int p_93708_, int p_93709_, BossEvent p_93710_) {
      this.blit(p_93707_, p_93708_, p_93709_, 0, p_93710_.getColor().ordinal() * 5 * 2, 182, 5);
      if (p_93710_.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
         this.blit(p_93707_, p_93708_, p_93709_, 0, 80 + (p_93710_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(p_93710_.getProgress() * 183.0F);
      if (i > 0) {
         this.blit(p_93707_, p_93708_, p_93709_, 0, p_93710_.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (p_93710_.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(p_93707_, p_93708_, p_93709_, 0, 80 + (p_93710_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void update(ClientboundBossEventPacket p_93712_) {
      p_93712_.dispatch(new ClientboundBossEventPacket.Handler() {
         public void add(UUID p_168824_, Component p_168825_, float p_168826_, BossEvent.BossBarColor p_168827_, BossEvent.BossBarOverlay p_168828_, boolean p_168829_, boolean p_168830_, boolean p_168831_) {
            BossHealthOverlay.this.events.put(p_168824_, new LerpingBossEvent(p_168824_, p_168825_, p_168826_, p_168827_, p_168828_, p_168829_, p_168830_, p_168831_));
         }

         public void remove(UUID p_168812_) {
            BossHealthOverlay.this.events.remove(p_168812_);
         }

         public void updateProgress(UUID p_168814_, float p_168815_) {
            BossHealthOverlay.this.events.get(p_168814_).setProgress(p_168815_);
         }

         public void updateName(UUID p_168821_, Component p_168822_) {
            BossHealthOverlay.this.events.get(p_168821_).setName(p_168822_);
         }

         public void updateStyle(UUID p_168817_, BossEvent.BossBarColor p_168818_, BossEvent.BossBarOverlay p_168819_) {
            LerpingBossEvent lerpingbossevent = BossHealthOverlay.this.events.get(p_168817_);
            lerpingbossevent.setColor(p_168818_);
            lerpingbossevent.setOverlay(p_168819_);
         }

         public void updateProperties(UUID p_168833_, boolean p_168834_, boolean p_168835_, boolean p_168836_) {
            LerpingBossEvent lerpingbossevent = BossHealthOverlay.this.events.get(p_168833_);
            lerpingbossevent.setDarkenScreen(p_168834_);
            lerpingbossevent.setPlayBossMusic(p_168835_);
            lerpingbossevent.setCreateWorldFog(p_168836_);
         }
      });
   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if (!this.events.isEmpty()) {
         for(BossEvent bossevent : this.events.values()) {
            if (bossevent.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if (!this.events.isEmpty()) {
         for(BossEvent bossevent : this.events.values()) {
            if (bossevent.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if (!this.events.isEmpty()) {
         for(BossEvent bossevent : this.events.values()) {
            if (bossevent.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
