package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SocialInteractionsScreen extends Screen {
   protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
   private static final Component TAB_ALL = new TranslatableComponent("gui.socialInteractions.tab_all");
   private static final Component TAB_HIDDEN = new TranslatableComponent("gui.socialInteractions.tab_hidden");
   private static final Component TAB_BLOCKED = new TranslatableComponent("gui.socialInteractions.tab_blocked");
   private static final Component TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
   private static final Component SEARCH_HINT = (new TranslatableComponent("gui.socialInteractions.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
   static final Component EMPTY_SEARCH = (new TranslatableComponent("gui.socialInteractions.search_empty")).withStyle(ChatFormatting.GRAY);
   private static final Component EMPTY_HIDDEN = (new TranslatableComponent("gui.socialInteractions.empty_hidden")).withStyle(ChatFormatting.GRAY);
   private static final Component EMPTY_BLOCKED = (new TranslatableComponent("gui.socialInteractions.empty_blocked")).withStyle(ChatFormatting.GRAY);
   private static final Component BLOCKING_HINT = new TranslatableComponent("gui.socialInteractions.blocking_hint");
   private static final String BLOCK_LINK = "https://aka.ms/javablocking";
   private static final int BG_BORDER_SIZE = 8;
   private static final int BG_UNITS = 16;
   private static final int BG_WIDTH = 236;
   private static final int SEARCH_HEIGHT = 16;
   private static final int MARGIN_Y = 64;
   public static final int LIST_START = 88;
   public static final int SEARCH_START = 78;
   private static final int IMAGE_WIDTH = 238;
   private static final int BUTTON_HEIGHT = 20;
   private static final int ITEM_HEIGHT = 36;
   SocialInteractionsPlayerList socialInteractionsPlayerList;
   EditBox searchBox;
   private String lastSearch = "";
   private SocialInteractionsScreen.Page page = SocialInteractionsScreen.Page.ALL;
   private Button allButton;
   private Button hiddenButton;
   private Button blockedButton;
   private Button blockingHintButton;
   @Nullable
   private Component serverLabel;
   private int playerCount;
   private boolean initialized;
   @Nullable
   private Runnable postRenderRunnable;

   public SocialInteractionsScreen() {
      super(new TranslatableComponent("gui.socialInteractions.title"));
      this.updateServerLabel(Minecraft.getInstance());
   }

   private int windowHeight() {
      return Math.max(52, this.height - 128 - 16);
   }

   private int backgroundUnits() {
      return this.windowHeight() / 16;
   }

   private int listEnd() {
      return 80 + this.backgroundUnits() * 16 - 8;
   }

   private int marginX() {
      return (this.width - 238) / 2;
   }

   public Component getNarrationMessage() {
      return (Component)(this.serverLabel != null ? CommonComponents.joinForNarration(super.getNarrationMessage(), this.serverLabel) : super.getNarrationMessage());
   }

   public void tick() {
      super.tick();
      this.searchBox.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      if (this.initialized) {
         this.socialInteractionsPlayerList.updateSize(this.width, this.height, 88, this.listEnd());
      } else {
         this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
      }

      int i = this.socialInteractionsPlayerList.getRowWidth() / 3;
      int j = this.socialInteractionsPlayerList.getRowLeft();
      int k = this.socialInteractionsPlayerList.getRowRight();
      int l = this.font.width(BLOCKING_HINT) + 40;
      int i1 = 64 + 16 * this.backgroundUnits();
      int j1 = (this.width - l) / 2;
      this.allButton = this.addRenderableWidget(new Button(j, 45, i, 20, TAB_ALL, (p_100796_) -> {
         this.showPage(SocialInteractionsScreen.Page.ALL);
      }));
      this.hiddenButton = this.addRenderableWidget(new Button((j + k - i) / 2 + 1, 45, i, 20, TAB_HIDDEN, (p_100791_) -> {
         this.showPage(SocialInteractionsScreen.Page.HIDDEN);
      }));
      this.blockedButton = this.addRenderableWidget(new Button(k - i + 1, 45, i, 20, TAB_BLOCKED, (p_100785_) -> {
         this.showPage(SocialInteractionsScreen.Page.BLOCKED);
      }));
      this.blockingHintButton = this.addRenderableWidget(new Button(j1, i1, l, 20, BLOCKING_HINT, (p_100770_) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((p_170143_) -> {
            if (p_170143_) {
               Util.getPlatform().openUri("https://aka.ms/javablocking");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/javablocking", true));
      }));
      String s = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_HINT) {
         protected MutableComponent createNarrationMessage() {
            return !SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty() ? super.createNarrationMessage().append(", ").append(SocialInteractionsScreen.EMPTY_SEARCH) : super.createNarrationMessage();
         }
      };
      this.searchBox.setMaxLength(16);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(s);
      this.searchBox.setResponder(this::checkSearchStringUpdate);
      this.addWidget(this.searchBox);
      this.addWidget(this.socialInteractionsPlayerList);
      this.initialized = true;
      this.showPage(this.page);
   }

   private void showPage(SocialInteractionsScreen.Page p_100772_) {
      this.page = p_100772_;
      this.allButton.setMessage(TAB_ALL);
      this.hiddenButton.setMessage(TAB_HIDDEN);
      this.blockedButton.setMessage(TAB_BLOCKED);
      Collection<UUID> collection;
      switch(p_100772_) {
      case ALL:
         this.allButton.setMessage(TAB_ALL_SELECTED);
         collection = this.minecraft.player.connection.getOnlinePlayerIds();
         break;
      case HIDDEN:
         this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
         collection = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
         break;
      case BLOCKED:
         this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
         PlayerSocialManager playersocialmanager = this.minecraft.getPlayerSocialManager();
         collection = this.minecraft.player.connection.getOnlinePlayerIds().stream().filter(playersocialmanager::isBlocked).collect(Collectors.toSet());
         break;
      default:
         collection = ImmutableList.of();
      }

      this.socialInteractionsPlayerList.updatePlayerList(collection, this.socialInteractionsPlayerList.getScrollAmount());
      if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
         NarratorChatListener.INSTANCE.sayNow(EMPTY_SEARCH);
      } else if (collection.isEmpty()) {
         if (p_100772_ == SocialInteractionsScreen.Page.HIDDEN) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_HIDDEN);
         } else if (p_100772_ == SocialInteractionsScreen.Page.BLOCKED) {
            NarratorChatListener.INSTANCE.sayNow(EMPTY_BLOCKED);
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void renderBackground(PoseStack p_100761_) {
      int i = this.marginX() + 3;
      super.renderBackground(p_100761_);
      RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_LOCATION);
      this.blit(p_100761_, i, 64, 1, 1, 236, 8);
      int j = this.backgroundUnits();

      for(int k = 0; k < j; ++k) {
         this.blit(p_100761_, i, 72 + 16 * k, 1, 10, 236, 16);
      }

      this.blit(p_100761_, i, 72 + 16 * j, 1, 27, 236, 8);
      this.blit(p_100761_, i + 10, 76, 243, 1, 12, 12);
   }

   public void render(PoseStack p_100763_, int p_100764_, int p_100765_, float p_100766_) {
      this.updateServerLabel(this.minecraft);
      this.renderBackground(p_100763_);
      if (this.serverLabel != null) {
         drawString(p_100763_, this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
      }

      if (!this.socialInteractionsPlayerList.isEmpty()) {
         this.socialInteractionsPlayerList.render(p_100763_, p_100764_, p_100765_, p_100766_);
      } else if (!this.searchBox.getValue().isEmpty()) {
         drawCenteredString(p_100763_, this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.HIDDEN) {
         drawCenteredString(p_100763_, this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (78 + this.listEnd()) / 2, -1);
      } else if (this.page == SocialInteractionsScreen.Page.BLOCKED) {
         drawCenteredString(p_100763_, this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (78 + this.listEnd()) / 2, -1);
      }

      if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
         drawString(p_100763_, this.minecraft.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
      } else {
         this.searchBox.render(p_100763_, p_100764_, p_100765_, p_100766_);
      }

      this.blockingHintButton.visible = this.page == SocialInteractionsScreen.Page.BLOCKED;
      super.render(p_100763_, p_100764_, p_100765_, p_100766_);
      if (this.postRenderRunnable != null) {
         this.postRenderRunnable.run();
      }

   }

   public boolean mouseClicked(double p_100753_, double p_100754_, int p_100755_) {
      if (this.searchBox.isFocused()) {
         this.searchBox.mouseClicked(p_100753_, p_100754_, p_100755_);
      }

      return super.mouseClicked(p_100753_, p_100754_, p_100755_) || this.socialInteractionsPlayerList.mouseClicked(p_100753_, p_100754_, p_100755_);
   }

   public boolean keyPressed(int p_100757_, int p_100758_, int p_100759_) {
      if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches(p_100757_, p_100758_)) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return super.keyPressed(p_100757_, p_100758_, p_100759_);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void checkSearchStringUpdate(String p_100789_) {
      p_100789_ = p_100789_.toLowerCase(Locale.ROOT);
      if (!p_100789_.equals(this.lastSearch)) {
         this.socialInteractionsPlayerList.setFilter(p_100789_);
         this.lastSearch = p_100789_;
         this.showPage(this.page);
      }

   }

   private void updateServerLabel(Minecraft p_100768_) {
      int i = p_100768_.getConnection().getOnlinePlayers().size();
      if (this.playerCount != i) {
         String s = "";
         ServerData serverdata = p_100768_.getCurrentServer();
         if (p_100768_.isLocalServer()) {
            s = p_100768_.getSingleplayerServer().getMotd();
         } else if (serverdata != null) {
            s = serverdata.name;
         }

         if (i > 1) {
            this.serverLabel = new TranslatableComponent("gui.socialInteractions.server_label.multiple", s, i);
         } else {
            this.serverLabel = new TranslatableComponent("gui.socialInteractions.server_label.single", s, i);
         }

         this.playerCount = i;
      }

   }

   public void onAddPlayer(PlayerInfo p_100776_) {
      this.socialInteractionsPlayerList.addPlayer(p_100776_, this.page);
   }

   public void onRemovePlayer(UUID p_100780_) {
      this.socialInteractionsPlayerList.removePlayer(p_100780_);
   }

   public void setPostRenderRunnable(@Nullable Runnable p_100778_) {
      this.postRenderRunnable = p_100778_;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Page {
      ALL,
      HIDDEN,
      BLOCKED;
   }
}