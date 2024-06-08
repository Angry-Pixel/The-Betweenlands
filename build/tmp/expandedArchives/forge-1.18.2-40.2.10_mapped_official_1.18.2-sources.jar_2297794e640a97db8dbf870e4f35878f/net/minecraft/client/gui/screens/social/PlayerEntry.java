package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerEntry extends ContainerObjectSelectionList.Entry<PlayerEntry> {
   private static final int TOOLTIP_DELAY = 10;
   private static final int TOOLTIP_MAX_WIDTH = 150;
   private final Minecraft minecraft;
   private final List<AbstractWidget> children;
   private final UUID id;
   private final String playerName;
   private final Supplier<ResourceLocation> skinGetter;
   private boolean isRemoved;
   @Nullable
   private Button hideButton;
   @Nullable
   private Button showButton;
   final Component hideText;
   final Component showText;
   final List<FormattedCharSequence> hideTooltip;
   final List<FormattedCharSequence> showTooltip;
   float tooltipHoverTime;
   private static final Component HIDDEN = (new TranslatableComponent("gui.socialInteractions.status_hidden")).withStyle(ChatFormatting.ITALIC);
   private static final Component BLOCKED = (new TranslatableComponent("gui.socialInteractions.status_blocked")).withStyle(ChatFormatting.ITALIC);
   private static final Component OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_offline")).withStyle(ChatFormatting.ITALIC);
   private static final Component HIDDEN_OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_hidden_offline")).withStyle(ChatFormatting.ITALIC);
   private static final Component BLOCKED_OFFLINE = (new TranslatableComponent("gui.socialInteractions.status_blocked_offline")).withStyle(ChatFormatting.ITALIC);
   private static final int SKIN_SIZE = 24;
   private static final int PADDING = 4;
   private static final int CHAT_TOGGLE_ICON_SIZE = 20;
   private static final int CHAT_TOGGLE_ICON_X = 0;
   private static final int CHAT_TOGGLE_ICON_Y = 38;
   public static final int SKIN_SHADE = FastColor.ARGB32.color(190, 0, 0, 0);
   public static final int BG_FILL = FastColor.ARGB32.color(255, 74, 74, 74);
   public static final int BG_FILL_REMOVED = FastColor.ARGB32.color(255, 48, 48, 48);
   public static final int PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
   public static final int PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);

   public PlayerEntry(final Minecraft p_100552_, final SocialInteractionsScreen p_100553_, UUID p_100554_, String p_100555_, Supplier<ResourceLocation> p_100556_) {
      this.minecraft = p_100552_;
      this.id = p_100554_;
      this.playerName = p_100555_;
      this.skinGetter = p_100556_;
      this.hideText = new TranslatableComponent("gui.socialInteractions.tooltip.hide", p_100555_);
      this.showText = new TranslatableComponent("gui.socialInteractions.tooltip.show", p_100555_);
      this.hideTooltip = p_100552_.font.split(this.hideText, 150);
      this.showTooltip = p_100552_.font.split(this.showText, 150);
      PlayerSocialManager playersocialmanager = p_100552_.getPlayerSocialManager();
      if (!p_100552_.player.getGameProfile().getId().equals(p_100554_) && !playersocialmanager.isBlocked(p_100554_)) {
         this.hideButton = new ImageButton(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (p_100612_) -> {
            playersocialmanager.hidePlayer(p_100554_);
            this.onHiddenOrShown(true, new TranslatableComponent("gui.socialInteractions.hidden_in_chat", p_100555_));
         }, new Button.OnTooltip() {
            public void onTooltip(Button p_170090_, PoseStack p_170091_, int p_170092_, int p_170093_) {
               PlayerEntry.this.tooltipHoverTime += p_100552_.getDeltaFrameTime();
               if (PlayerEntry.this.tooltipHoverTime >= 10.0F) {
                  p_100553_.setPostRenderRunnable(() -> {
                     PlayerEntry.postRenderTooltip(p_100553_, p_170091_, PlayerEntry.this.hideTooltip, p_170092_, p_170093_);
                  });
               }

            }

            public void narrateTooltip(Consumer<Component> p_170088_) {
               p_170088_.accept(PlayerEntry.this.hideText);
            }
         }, new TranslatableComponent("gui.socialInteractions.hide")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton = new ImageButton(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (p_170074_) -> {
            playersocialmanager.showPlayer(p_100554_);
            this.onHiddenOrShown(false, new TranslatableComponent("gui.socialInteractions.shown_in_chat", p_100555_));
         }, new Button.OnTooltip() {
            public void onTooltip(Button p_170109_, PoseStack p_170110_, int p_170111_, int p_170112_) {
               PlayerEntry.this.tooltipHoverTime += p_100552_.getDeltaFrameTime();
               if (PlayerEntry.this.tooltipHoverTime >= 10.0F) {
                  p_100553_.setPostRenderRunnable(() -> {
                     PlayerEntry.postRenderTooltip(p_100553_, p_170110_, PlayerEntry.this.showTooltip, p_170111_, p_170112_);
                  });
               }

            }

            public void narrateTooltip(Consumer<Component> p_170107_) {
               p_170107_.accept(PlayerEntry.this.showText);
            }
         }, new TranslatableComponent("gui.socialInteractions.show")) {
            protected MutableComponent createNarrationMessage() {
               return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton.visible = playersocialmanager.isHidden(p_100554_);
         this.hideButton.visible = !this.showButton.visible;
         this.children = ImmutableList.of(this.hideButton, this.showButton);
      } else {
         this.children = ImmutableList.of();
      }

   }

   public void render(PoseStack p_100558_, int p_100559_, int p_100560_, int p_100561_, int p_100562_, int p_100563_, int p_100564_, int p_100565_, boolean p_100566_, float p_100567_) {
      int i = p_100561_ + 4;
      int j = p_100560_ + (p_100563_ - 24) / 2;
      int k = i + 24 + 4;
      Component component = this.getStatusComponent();
      int l;
      if (component == TextComponent.EMPTY) {
         GuiComponent.fill(p_100558_, p_100561_, p_100560_, p_100561_ + p_100562_, p_100560_ + p_100563_, BG_FILL);
         l = p_100560_ + (p_100563_ - 9) / 2;
      } else {
         GuiComponent.fill(p_100558_, p_100561_, p_100560_, p_100561_ + p_100562_, p_100560_ + p_100563_, BG_FILL_REMOVED);
         l = p_100560_ + (p_100563_ - (9 + 9)) / 2;
         this.minecraft.font.draw(p_100558_, component, (float)k, (float)(l + 12), PLAYER_STATUS_COLOR);
      }

      RenderSystem.setShaderTexture(0, this.skinGetter.get());
      GuiComponent.blit(p_100558_, i, j, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.enableBlend();
      GuiComponent.blit(p_100558_, i, j, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.disableBlend();
      this.minecraft.font.draw(p_100558_, this.playerName, (float)k, (float)l, PLAYERNAME_COLOR);
      if (this.isRemoved) {
         GuiComponent.fill(p_100558_, i, j, i + 24, j + 24, SKIN_SHADE);
      }

      if (this.hideButton != null && this.showButton != null) {
         float f = this.tooltipHoverTime;
         this.hideButton.x = p_100561_ + (p_100562_ - this.hideButton.getWidth() - 4);
         this.hideButton.y = p_100560_ + (p_100563_ - this.hideButton.getHeight()) / 2;
         this.hideButton.render(p_100558_, p_100564_, p_100565_, p_100567_);
         this.showButton.x = p_100561_ + (p_100562_ - this.showButton.getWidth() - 4);
         this.showButton.y = p_100560_ + (p_100563_ - this.showButton.getHeight()) / 2;
         this.showButton.render(p_100558_, p_100564_, p_100565_, p_100567_);
         if (f == this.tooltipHoverTime) {
            this.tooltipHoverTime = 0.0F;
         }
      }

   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public UUID getPlayerId() {
      return this.id;
   }

   public void setRemoved(boolean p_100620_) {
      this.isRemoved = p_100620_;
   }

   private void onHiddenOrShown(boolean p_100597_, Component p_100598_) {
      this.showButton.visible = p_100597_;
      this.hideButton.visible = !p_100597_;
      this.minecraft.gui.getChat().addMessage(p_100598_);
      NarratorChatListener.INSTANCE.sayNow(p_100598_);
   }

   MutableComponent getEntryNarationMessage(MutableComponent p_100595_) {
      Component component = this.getStatusComponent();
      return component == TextComponent.EMPTY ? (new TextComponent(this.playerName)).append(", ").append(p_100595_) : (new TextComponent(this.playerName)).append(", ").append(component).append(", ").append(p_100595_);
   }

   private Component getStatusComponent() {
      boolean flag = this.minecraft.getPlayerSocialManager().isHidden(this.id);
      boolean flag1 = this.minecraft.getPlayerSocialManager().isBlocked(this.id);
      if (flag1 && this.isRemoved) {
         return BLOCKED_OFFLINE;
      } else if (flag && this.isRemoved) {
         return HIDDEN_OFFLINE;
      } else if (flag1) {
         return BLOCKED;
      } else if (flag) {
         return HIDDEN;
      } else {
         return this.isRemoved ? OFFLINE : TextComponent.EMPTY;
      }
   }

   static void postRenderTooltip(SocialInteractionsScreen p_100589_, PoseStack p_100590_, List<FormattedCharSequence> p_100591_, int p_100592_, int p_100593_) {
      p_100589_.renderTooltip(p_100590_, p_100591_, p_100592_, p_100593_);
      p_100589_.setPostRenderRunnable((Runnable)null);
   }
}