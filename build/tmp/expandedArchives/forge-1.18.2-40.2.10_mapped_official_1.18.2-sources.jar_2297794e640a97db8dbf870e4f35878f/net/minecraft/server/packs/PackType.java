package net.minecraft.server.packs;

import com.mojang.bridge.game.GameVersion;

public enum PackType {
   CLIENT_RESOURCES("assets", com.mojang.bridge.game.PackType.RESOURCE),
   SERVER_DATA("data", com.mojang.bridge.game.PackType.DATA);

   private final String directory;
   public final com.mojang.bridge.game.PackType bridgeType;

   private PackType(String p_143754_, com.mojang.bridge.game.PackType p_143755_) {
      this.directory = p_143754_;
      this.bridgeType = p_143755_;
   }

   public String getDirectory() {
      return this.directory;
   }

   public int getVersion(GameVersion p_143757_) {
      return p_143757_.getPackVersion(this.bridgeType);
   }
}