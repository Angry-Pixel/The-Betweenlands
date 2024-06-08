package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SocialInteractionsPlayerList extends ContainerObjectSelectionList<PlayerEntry> {
   private final SocialInteractionsScreen socialInteractionsScreen;
   private final List<PlayerEntry> players = Lists.newArrayList();
   @Nullable
   private String filter;

   public SocialInteractionsPlayerList(SocialInteractionsScreen p_100697_, Minecraft p_100698_, int p_100699_, int p_100700_, int p_100701_, int p_100702_, int p_100703_) {
      super(p_100698_, p_100699_, p_100700_, p_100701_, p_100702_, p_100703_);
      this.socialInteractionsScreen = p_100697_;
      this.setRenderBackground(false);
      this.setRenderTopAndBottom(false);
   }

   public void render(PoseStack p_100705_, int p_100706_, int p_100707_, float p_100708_) {
      double d0 = this.minecraft.getWindow().getGuiScale();
      RenderSystem.enableScissor((int)((double)this.getRowLeft() * d0), (int)((double)(this.height - this.y1) * d0), (int)((double)(this.getScrollbarPosition() + 6) * d0), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d0));
      super.render(p_100705_, p_100706_, p_100707_, p_100708_);
      RenderSystem.disableScissor();
   }

   public void updatePlayerList(Collection<UUID> p_100720_, double p_100721_) {
      this.players.clear();

      for(UUID uuid : p_100720_) {
         PlayerInfo playerinfo = this.minecraft.player.connection.getPlayerInfo(uuid);
         if (playerinfo != null) {
            this.players.add(new PlayerEntry(this.minecraft, this.socialInteractionsScreen, playerinfo.getProfile().getId(), playerinfo.getProfile().getName(), playerinfo::getSkinLocation));
         }
      }

      this.updateFilteredPlayers();
      this.players.sort((p_100712_, p_100713_) -> {
         return p_100712_.getPlayerName().compareToIgnoreCase(p_100713_.getPlayerName());
      });
      this.replaceEntries(this.players);
      this.setScrollAmount(p_100721_);
   }

   private void updateFilteredPlayers() {
      if (this.filter != null) {
         this.players.removeIf((p_100710_) -> {
            return !p_100710_.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter);
         });
         this.replaceEntries(this.players);
      }

   }

   public void setFilter(String p_100718_) {
      this.filter = p_100718_;
   }

   public boolean isEmpty() {
      return this.players.isEmpty();
   }

   public void addPlayer(PlayerInfo p_100715_, SocialInteractionsScreen.Page p_100716_) {
      UUID uuid = p_100715_.getProfile().getId();

      for(PlayerEntry playerentry : this.players) {
         if (playerentry.getPlayerId().equals(uuid)) {
            playerentry.setRemoved(false);
            return;
         }
      }

      if ((p_100716_ == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(uuid)) && (Strings.isNullOrEmpty(this.filter) || p_100715_.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
         PlayerEntry playerentry1 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, p_100715_.getProfile().getId(), p_100715_.getProfile().getName(), p_100715_::getSkinLocation);
         this.addEntry(playerentry1);
         this.players.add(playerentry1);
      }

   }

   public void removePlayer(UUID p_100723_) {
      for(PlayerEntry playerentry : this.players) {
         if (playerentry.getPlayerId().equals(p_100723_)) {
            playerentry.setRemoved(true);
            return;
         }
      }

   }
}