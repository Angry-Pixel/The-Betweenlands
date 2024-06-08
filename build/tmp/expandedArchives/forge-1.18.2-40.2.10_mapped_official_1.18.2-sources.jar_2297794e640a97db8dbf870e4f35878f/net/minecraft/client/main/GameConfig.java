package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.client.resources.DirectAssetIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfig {
   public final GameConfig.UserData user;
   public final DisplayData display;
   public final GameConfig.FolderData location;
   public final GameConfig.GameData game;
   public final GameConfig.ServerData server;

   public GameConfig(GameConfig.UserData p_101911_, DisplayData p_101912_, GameConfig.FolderData p_101913_, GameConfig.GameData p_101914_, GameConfig.ServerData p_101915_) {
      this.user = p_101911_;
      this.display = p_101912_;
      this.location = p_101913_;
      this.game = p_101914_;
      this.server = p_101915_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderData {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      @Nullable
      public final String assetIndex;

      public FolderData(File p_101921_, File p_101922_, File p_101923_, @Nullable String p_101924_) {
         this.gameDirectory = p_101921_;
         this.resourcePackDirectory = p_101922_;
         this.assetDirectory = p_101923_;
         this.assetIndex = p_101924_;
      }

      public AssetIndex getAssetIndex() {
         return (AssetIndex)(this.assetIndex == null ? new DirectAssetIndex(this.assetDirectory) : new AssetIndex(this.assetDirectory, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameData {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;

      public GameData(boolean p_101932_, String p_101933_, String p_101934_, boolean p_101935_, boolean p_101936_) {
         this.demo = p_101932_;
         this.launchVersion = p_101933_;
         this.versionType = p_101934_;
         this.disableMultiplayer = p_101935_;
         this.disableChat = p_101936_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerData {
      @Nullable
      public final String hostname;
      public final int port;

      public ServerData(@Nullable String p_101940_, int p_101941_) {
         this.hostname = p_101940_;
         this.port = p_101941_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserData {
      public final User user;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserData(User p_101947_, PropertyMap p_101948_, PropertyMap p_101949_, Proxy p_101950_) {
         this.user = p_101947_;
         this.userProperties = p_101948_;
         this.profileProperties = p_101949_;
         this.proxy = p_101950_;
      }
   }
}