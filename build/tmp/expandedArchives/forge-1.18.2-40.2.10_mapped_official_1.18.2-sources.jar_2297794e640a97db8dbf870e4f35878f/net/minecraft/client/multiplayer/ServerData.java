package net.minecraft.client.multiplayer;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = new TextComponent(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<Component> playerList = Collections.emptyList();
   private ServerData.ServerPackStatus packStatus = ServerData.ServerPackStatus.PROMPT;
   @Nullable
   private String iconB64;
   private boolean lan;
   public net.minecraftforge.client.ExtendedServerListData forgeData = null;

   public ServerData(String p_105375_, String p_105376_, boolean p_105377_) {
      this.name = p_105375_;
      this.ip = p_105376_;
      this.lan = p_105377_;
   }

   public CompoundTag write() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("name", this.name);
      compoundtag.putString("ip", this.ip);
      if (this.iconB64 != null) {
         compoundtag.putString("icon", this.iconB64);
      }

      if (this.packStatus == ServerData.ServerPackStatus.ENABLED) {
         compoundtag.putBoolean("acceptTextures", true);
      } else if (this.packStatus == ServerData.ServerPackStatus.DISABLED) {
         compoundtag.putBoolean("acceptTextures", false);
      }

      return compoundtag;
   }

   public ServerData.ServerPackStatus getResourcePackStatus() {
      return this.packStatus;
   }

   public void setResourcePackStatus(ServerData.ServerPackStatus p_105380_) {
      this.packStatus = p_105380_;
   }

   public static ServerData read(CompoundTag p_105386_) {
      ServerData serverdata = new ServerData(p_105386_.getString("name"), p_105386_.getString("ip"), false);
      if (p_105386_.contains("icon", 8)) {
         serverdata.setIconB64(p_105386_.getString("icon"));
      }

      if (p_105386_.contains("acceptTextures", 1)) {
         if (p_105386_.getBoolean("acceptTextures")) {
            serverdata.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
         } else {
            serverdata.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
         }
      } else {
         serverdata.setResourcePackStatus(ServerData.ServerPackStatus.PROMPT);
      }

      return serverdata;
   }

   @Nullable
   public String getIconB64() {
      return this.iconB64;
   }

   public void setIconB64(@Nullable String p_105384_) {
      this.iconB64 = p_105384_;
   }

   public boolean isLan() {
      return this.lan;
   }

   public void copyFrom(ServerData p_105382_) {
      this.ip = p_105382_.ip;
      this.name = p_105382_.name;
      this.setResourcePackStatus(p_105382_.getResourcePackStatus());
      this.iconB64 = p_105382_.iconB64;
      this.lan = p_105382_.lan;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerPackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Component name;

      private ServerPackStatus(String p_105399_) {
         this.name = new TranslatableComponent("addServer.resourcePack." + p_105399_);
      }

      public Component getName() {
         return this.name;
      }
   }
}
