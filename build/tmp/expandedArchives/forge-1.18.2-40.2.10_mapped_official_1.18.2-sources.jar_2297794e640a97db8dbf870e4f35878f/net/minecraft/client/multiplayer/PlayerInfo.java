package net.minecraft.client.multiplayer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerInfo {
   private final GameProfile profile;
   private final Map<Type, ResourceLocation> textureLocations = Maps.newEnumMap(Type.class);
   private GameType gameMode;
   private int latency;
   private boolean pendingTextures;
   @Nullable
   private String skinModel;
   @Nullable
   private Component tabListDisplayName;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private long renderVisibilityId;

   public PlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate p_105311_) {
      this.profile = p_105311_.getProfile();
      this.gameMode = p_105311_.getGameMode();
      this.latency = p_105311_.getLatency();
      this.tabListDisplayName = p_105311_.getDisplayName();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   @Nullable
   public GameType getGameMode() {
      return this.gameMode;
   }

   protected void setGameMode(GameType p_105318_) {
      net.minecraftforge.client.ForgeHooksClient.onClientChangeGameType(this, this.gameMode, p_105318_);
      this.gameMode = p_105318_;
   }

   public int getLatency() {
      return this.latency;
   }

   protected void setLatency(int p_105314_) {
      this.latency = p_105314_;
   }

   public boolean isCapeLoaded() {
      return this.getCapeLocation() != null;
   }

   public boolean isSkinLoaded() {
      return this.getSkinLocation() != null;
   }

   public String getModelName() {
      return this.skinModel == null ? DefaultPlayerSkin.getSkinModelName(this.profile.getId()) : this.skinModel;
   }

   public ResourceLocation getSkinLocation() {
      this.registerTextures();
      return MoreObjects.firstNonNull(this.textureLocations.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
   }

   @Nullable
   public ResourceLocation getCapeLocation() {
      this.registerTextures();
      return this.textureLocations.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation getElytraLocation() {
      this.registerTextures();
      return this.textureLocations.get(Type.ELYTRA);
   }

   @Nullable
   public PlayerTeam getTeam() {
      return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
   }

   protected void registerTextures() {
      synchronized(this) {
         if (!this.pendingTextures) {
            this.pendingTextures = true;
            Minecraft.getInstance().getSkinManager().registerSkins(this.profile, (p_105320_, p_105321_, p_105322_) -> {
               this.textureLocations.put(p_105320_, p_105321_);
               if (p_105320_ == Type.SKIN) {
                  this.skinModel = p_105322_.getMetadata("model");
                  if (this.skinModel == null) {
                     this.skinModel = "default";
                  }
               }

            }, true);
         }

      }
   }

   public void setTabListDisplayName(@Nullable Component p_105324_) {
      this.tabListDisplayName = p_105324_;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return this.tabListDisplayName;
   }

   public int getLastHealth() {
      return this.lastHealth;
   }

   public void setLastHealth(int p_105327_) {
      this.lastHealth = p_105327_;
   }

   public int getDisplayHealth() {
      return this.displayHealth;
   }

   public void setDisplayHealth(int p_105332_) {
      this.displayHealth = p_105332_;
   }

   public long getLastHealthTime() {
      return this.lastHealthTime;
   }

   public void setLastHealthTime(long p_105316_) {
      this.lastHealthTime = p_105316_;
   }

   public long getHealthBlinkTime() {
      return this.healthBlinkTime;
   }

   public void setHealthBlinkTime(long p_105329_) {
      this.healthBlinkTime = p_105329_;
   }

   public long getRenderVisibilityId() {
      return this.renderVisibilityId;
   }

   public void setRenderVisibilityId(long p_105334_) {
      this.renderVisibilityId = p_105334_;
   }
}
