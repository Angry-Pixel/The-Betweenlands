package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.ResettingGeneratedWorldTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Screen lastScreen;
   private final RealmsServer serverData;
   private Component subtitle = new TranslatableComponent("mco.reset.world.warning");
   private Component buttonTitle = CommonComponents.GUI_CANCEL;
   private int subtitleColor = 16711680;
   private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
   private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
   private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
   private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
   private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
   private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
   WorldTemplatePaginatedList templates;
   WorldTemplatePaginatedList adventuremaps;
   WorldTemplatePaginatedList experiences;
   WorldTemplatePaginatedList inspirations;
   public int slot = -1;
   private Component resetTitle = new TranslatableComponent("mco.reset.world.resetting.screen.title");
   private final Runnable resetWorldRunnable;
   private final Runnable callback;

   public RealmsResetWorldScreen(Screen p_167448_, RealmsServer p_167449_, Component p_167450_, Runnable p_167451_, Runnable p_167452_) {
      super(p_167450_);
      this.lastScreen = p_167448_;
      this.serverData = p_167449_;
      this.resetWorldRunnable = p_167451_;
      this.callback = p_167452_;
   }

   public RealmsResetWorldScreen(Screen p_89329_, RealmsServer p_89330_, Runnable p_89331_, Runnable p_89332_) {
      this(p_89329_, p_89330_, new TranslatableComponent("mco.reset.world.title"), p_89331_, p_89332_);
   }

   public RealmsResetWorldScreen(Screen p_89334_, RealmsServer p_89335_, Component p_89336_, Component p_89337_, int p_89338_, Component p_89339_, Runnable p_89340_, Runnable p_89341_) {
      this(p_89334_, p_89335_, p_89336_, p_89340_, p_89341_);
      this.subtitle = p_89337_;
      this.subtitleColor = p_89338_;
      this.buttonTitle = p_89339_;
   }

   public void setSlot(int p_89344_) {
      this.slot = p_89344_;
   }

   public void setResetTitle(Component p_89390_) {
      this.resetTitle = p_89390_;
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, (p_89419_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      (new Thread("Realms-reset-world-fetcher") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               WorldTemplatePaginatedList worldtemplatepaginatedlist = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
               WorldTemplatePaginatedList worldtemplatepaginatedlist1 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
               WorldTemplatePaginatedList worldtemplatepaginatedlist2 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
               WorldTemplatePaginatedList worldtemplatepaginatedlist3 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
               RealmsResetWorldScreen.this.minecraft.execute(() -> {
                  RealmsResetWorldScreen.this.templates = worldtemplatepaginatedlist;
                  RealmsResetWorldScreen.this.adventuremaps = worldtemplatepaginatedlist1;
                  RealmsResetWorldScreen.this.experiences = worldtemplatepaginatedlist2;
                  RealmsResetWorldScreen.this.inspirations = worldtemplatepaginatedlist3;
               });
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsserviceexception);
            }

         }
      }).start();
      this.addLabel(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(1), row(0) + 10, new TranslatableComponent("mco.reset.world.generate"), NEW_WORLD_LOCATION, (p_89417_) -> {
         this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this::generationSelectionCallback, this.title));
      }));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(2), row(0) + 10, new TranslatableComponent("mco.reset.world.upload"), UPLOAD_LOCATION, (p_89415_) -> {
         this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback));
      }));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(3), row(0) + 10, new TranslatableComponent("mco.reset.world.template"), SURVIVAL_SPAWN_LOCATION, (p_89412_) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.reset.world.template"), this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates));
      }));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(1), row(6) + 20, new TranslatableComponent("mco.reset.world.adventure"), ADVENTURE_MAP_LOCATION, (p_89407_) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.reset.world.adventure"), this::templateSelectionCallback, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps));
      }));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(2), row(6) + 20, new TranslatableComponent("mco.reset.world.experience"), EXPERIENCE_LOCATION, (p_89402_) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.reset.world.experience"), this::templateSelectionCallback, RealmsServer.WorldType.EXPERIENCE, this.experiences));
      }));
      this.addRenderableWidget(new RealmsResetWorldScreen.FrameButton(this.frame(3), row(6) + 20, new TranslatableComponent("mco.reset.world.inspiration"), INSPIRATION_LOCATION, (p_89381_) -> {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(new TranslatableComponent("mco.reset.world.inspiration"), this::templateSelectionCallback, RealmsServer.WorldType.INSPIRATION, this.inspirations));
      }));
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_89346_, int p_89347_, int p_89348_) {
      if (p_89346_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_89346_, p_89347_, p_89348_);
      }
   }

   private int frame(int p_89393_) {
      return this.width / 2 - 130 + (p_89393_ - 1) * 100;
   }

   public void render(PoseStack p_89350_, int p_89351_, int p_89352_, float p_89353_) {
      this.renderBackground(p_89350_);
      drawCenteredString(p_89350_, this.font, this.title, this.width / 2, 7, 16777215);
      super.render(p_89350_, p_89351_, p_89352_, p_89353_);
   }

   void drawFrame(PoseStack p_89355_, int p_89356_, int p_89357_, Component p_89358_, ResourceLocation p_89359_, boolean p_89360_, boolean p_89361_) {
      RenderSystem.setShaderTexture(0, p_89359_);
      if (p_89360_) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(p_89355_, p_89356_ + 2, p_89357_ + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
      if (p_89360_) {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GuiComponent.blit(p_89355_, p_89356_, p_89357_ + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      int i = p_89360_ ? 10526880 : 16777215;
      drawCenteredString(p_89355_, this.font, p_89358_, p_89356_ + 30, p_89357_, i);
   }

   private void startTask(LongRunningTask p_167458_) {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, p_167458_));
   }

   public void switchSlot(Runnable p_89383_) {
      this.startTask(new SwitchSlotTask(this.serverData.id, this.slot, () -> {
         this.minecraft.execute(p_89383_);
      }));
   }

   private void templateSelectionCallback(@Nullable WorldTemplate p_167454_) {
      this.minecraft.setScreen(this);
      if (p_167454_ != null) {
         this.resetWorld(() -> {
            this.startTask(new ResettingTemplateWorldTask(p_167454_, this.serverData.id, this.resetTitle, this.resetWorldRunnable));
         });
      }

   }

   private void generationSelectionCallback(@Nullable WorldGenerationInfo p_167456_) {
      this.minecraft.setScreen(this);
      if (p_167456_ != null) {
         this.resetWorld(() -> {
            this.startTask(new ResettingGeneratedWorldTask(p_167456_, this.serverData.id, this.resetTitle, this.resetWorldRunnable));
         });
      }

   }

   private void resetWorld(Runnable p_167465_) {
      if (this.slot == -1) {
         p_167465_.run();
      } else {
         this.switchSlot(p_167465_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   class FrameButton extends Button {
      private final ResourceLocation image;

      public FrameButton(int p_89439_, int p_89440_, Component p_89441_, ResourceLocation p_89442_, Button.OnPress p_89443_) {
         super(p_89439_, p_89440_, 60, 72, p_89441_, p_89443_);
         this.image = p_89442_;
      }

      public void renderButton(PoseStack p_89445_, int p_89446_, int p_89447_, float p_89448_) {
         RealmsResetWorldScreen.this.drawFrame(p_89445_, this.x, this.y, this.getMessage(), this.image, this.isHoveredOrFocused(), this.isMouseOver((double)p_89446_, (double)p_89447_));
      }
   }
}