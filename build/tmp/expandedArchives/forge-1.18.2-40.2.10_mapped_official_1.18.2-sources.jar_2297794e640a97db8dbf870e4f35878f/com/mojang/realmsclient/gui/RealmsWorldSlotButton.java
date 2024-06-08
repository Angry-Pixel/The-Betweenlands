package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldSlotButton extends Button {
   public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
   private static final Component SLOT_ACTIVE_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip.active");
   private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip.minigame");
   private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = new TranslatableComponent("mco.configure.world.slot.tooltip");
   private final Supplier<RealmsServer> serverDataProvider;
   private final Consumer<Component> toolTipSetter;
   private final int slotIndex;
   private int animTick;
   @Nullable
   private RealmsWorldSlotButton.State state;

   public RealmsWorldSlotButton(int p_87929_, int p_87930_, int p_87931_, int p_87932_, Supplier<RealmsServer> p_87933_, Consumer<Component> p_87934_, int p_87935_, Button.OnPress p_87936_) {
      super(p_87929_, p_87930_, p_87931_, p_87932_, TextComponent.EMPTY, p_87936_);
      this.serverDataProvider = p_87933_;
      this.slotIndex = p_87935_;
      this.toolTipSetter = p_87934_;
   }

   @Nullable
   public RealmsWorldSlotButton.State getState() {
      return this.state;
   }

   public void tick() {
      ++this.animTick;
      RealmsServer realmsserver = this.serverDataProvider.get();
      if (realmsserver != null) {
         RealmsWorldOptions realmsworldoptions = realmsserver.slots.get(this.slotIndex);
         boolean flag2 = this.slotIndex == 4;
         boolean flag;
         String s;
         long i;
         String s1;
         boolean flag1;
         if (flag2) {
            flag = realmsserver.worldType == RealmsServer.WorldType.MINIGAME;
            s = "Minigame";
            i = (long)realmsserver.minigameId;
            s1 = realmsserver.minigameImage;
            flag1 = realmsserver.minigameId == -1;
         } else {
            flag = realmsserver.activeSlot == this.slotIndex && realmsserver.worldType != RealmsServer.WorldType.MINIGAME;
            s = realmsworldoptions.getSlotName(this.slotIndex);
            i = realmsworldoptions.templateId;
            s1 = realmsworldoptions.templateImage;
            flag1 = realmsworldoptions.empty;
         }

         RealmsWorldSlotButton.Action realmsworldslotbutton$action = getAction(realmsserver, flag, flag2);
         Pair<Component, Component> pair = this.getTooltipAndNarration(realmsserver, s, flag1, flag2, realmsworldslotbutton$action);
         this.state = new RealmsWorldSlotButton.State(flag, s, i, s1, flag1, flag2, realmsworldslotbutton$action, pair.getFirst());
         this.setMessage(pair.getSecond());
      }
   }

   private static RealmsWorldSlotButton.Action getAction(RealmsServer p_87960_, boolean p_87961_, boolean p_87962_) {
      if (p_87961_) {
         if (!p_87960_.expired && p_87960_.state != RealmsServer.State.UNINITIALIZED) {
            return RealmsWorldSlotButton.Action.JOIN;
         }
      } else {
         if (!p_87962_) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }

         if (!p_87960_.expired) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }
      }

      return RealmsWorldSlotButton.Action.NOTHING;
   }

   private Pair<Component, Component> getTooltipAndNarration(RealmsServer p_87954_, String p_87955_, boolean p_87956_, boolean p_87957_, RealmsWorldSlotButton.Action p_87958_) {
      if (p_87958_ == RealmsWorldSlotButton.Action.NOTHING) {
         return Pair.of((Component)null, new TextComponent(p_87955_));
      } else {
         Component component;
         if (p_87957_) {
            if (p_87956_) {
               component = TextComponent.EMPTY;
            } else {
               component = (new TextComponent(" ")).append(p_87955_).append(" ").append(p_87954_.minigameName);
            }
         } else {
            component = (new TextComponent(" ")).append(p_87955_);
         }

         Component component1;
         if (p_87958_ == RealmsWorldSlotButton.Action.JOIN) {
            component1 = SLOT_ACTIVE_TOOLTIP;
         } else {
            component1 = p_87957_ ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         }

         Component component2 = component1.copy().append(component);
         return Pair.of(component1, component2);
      }
   }

   public void renderButton(PoseStack p_87964_, int p_87965_, int p_87966_, float p_87967_) {
      if (this.state != null) {
         this.drawSlotFrame(p_87964_, this.x, this.y, p_87965_, p_87966_, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
      }
   }

   private void drawSlotFrame(PoseStack p_87939_, int p_87940_, int p_87941_, int p_87942_, int p_87943_, boolean p_87944_, String p_87945_, int p_87946_, long p_87947_, @Nullable String p_87948_, boolean p_87949_, boolean p_87950_, RealmsWorldSlotButton.Action p_87951_, @Nullable Component p_87952_) {
      boolean flag = this.isHoveredOrFocused();
      if (this.isMouseOver((double)p_87942_, (double)p_87943_) && p_87952_ != null) {
         this.toolTipSetter.accept(p_87952_);
      }

      Minecraft minecraft = Minecraft.getInstance();
      if (p_87950_) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_87947_), p_87948_);
      } else if (p_87949_) {
         RenderSystem.setShaderTexture(0, EMPTY_SLOT_LOCATION);
      } else if (p_87948_ != null && p_87947_ != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_87947_), p_87948_);
      } else if (p_87946_ == 1) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_1);
      } else if (p_87946_ == 2) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_2);
      } else if (p_87946_ == 3) {
         RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_3);
      }

      if (p_87944_) {
         float f = 0.85F + 0.15F * Mth.cos((float)this.animTick * 0.2F);
         RenderSystem.setShaderColor(f, f, f, 1.0F);
      } else {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      blit(p_87939_, p_87940_ + 3, p_87941_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
      boolean flag1 = flag && p_87951_ != RealmsWorldSlotButton.Action.NOTHING;
      if (flag1) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (p_87944_) {
         RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      blit(p_87939_, p_87940_, p_87941_, 0.0F, 0.0F, 80, 80, 80, 80);
      drawCenteredString(p_87939_, minecraft.font, p_87945_, p_87940_ + 40, p_87941_ + 66, 16777215);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;
   }

   @OnlyIn(Dist.CLIENT)
   public static class State {
      final boolean isCurrentlyActiveSlot;
      final String slotName;
      final long imageId;
      @Nullable
      final String image;
      public final boolean empty;
      public final boolean minigame;
      public final RealmsWorldSlotButton.Action action;
      @Nullable
      final Component actionPrompt;

      State(boolean p_87989_, String p_87990_, long p_87991_, @Nullable String p_87992_, boolean p_87993_, boolean p_87994_, RealmsWorldSlotButton.Action p_87995_, @Nullable Component p_87996_) {
         this.isCurrentlyActiveSlot = p_87989_;
         this.slotName = p_87990_;
         this.imageId = p_87991_;
         this.image = p_87992_;
         this.empty = p_87993_;
         this.minigame = p_87994_;
         this.action = p_87995_;
         this.actionPrompt = p_87996_;
      }
   }
}