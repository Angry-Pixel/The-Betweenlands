package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerSocialManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final UserApiService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();
   private boolean onlineMode;
   private CompletableFuture<?> pendingBlockListRefresh = CompletableFuture.completedFuture((Object)null);

   public PlayerSocialManager(Minecraft p_194057_, UserApiService p_194058_) {
      this.minecraft = p_194057_;
      this.service = p_194058_;
   }

   public void hidePlayer(UUID p_100681_) {
      this.hiddenPlayers.add(p_100681_);
   }

   public void showPlayer(UUID p_100683_) {
      this.hiddenPlayers.remove(p_100683_);
   }

   public boolean shouldHideMessageFrom(UUID p_100685_) {
      return this.isHidden(p_100685_) || this.isBlocked(p_100685_);
   }

   public boolean isHidden(UUID p_100687_) {
      return this.hiddenPlayers.contains(p_100687_);
   }

   public void startOnlineMode() {
      this.onlineMode = true;
      this.pendingBlockListRefresh = this.pendingBlockListRefresh.thenRunAsync(this.service::refreshBlockList, Util.ioPool());
   }

   public void stopOnlineMode() {
      this.onlineMode = false;
   }

   public boolean isBlocked(UUID p_100689_) {
      if (!this.onlineMode) {
         return false;
      } else {
         this.pendingBlockListRefresh.join();
         return this.service.isBlockedPlayer(p_100689_);
      }
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(String p_100679_) {
      return this.discoveredNamesToUUID.getOrDefault(p_100679_, Util.NIL_UUID);
   }

   public void addPlayer(PlayerInfo p_100677_) {
      GameProfile gameprofile = p_100677_.getProfile();
      if (gameprofile.isComplete()) {
         this.discoveredNamesToUUID.put(gameprofile.getName(), gameprofile.getId());
      }

      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onAddPlayer(p_100677_);
      }

   }

   public void removePlayer(UUID p_100691_) {
      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onRemovePlayer(p_100691_);
      }

   }
}