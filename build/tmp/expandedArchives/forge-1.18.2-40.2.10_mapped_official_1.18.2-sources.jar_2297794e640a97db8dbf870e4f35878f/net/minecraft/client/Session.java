package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Session implements GameSession {
   private final int players;
   private final boolean isRemoteServer;
   private final String difficulty;
   private final String gameMode;
   private final UUID id;

   public Session(ClientLevel p_92325_, LocalPlayer p_92326_, ClientPacketListener p_92327_) {
      this.players = p_92327_.getOnlinePlayers().size();
      this.isRemoteServer = !p_92327_.getConnection().isMemoryConnection();
      this.difficulty = p_92325_.getDifficulty().getKey();
      PlayerInfo playerinfo = p_92327_.getPlayerInfo(p_92326_.getUUID());
      if (playerinfo != null) {
         this.gameMode = playerinfo.getGameMode().getName();
      } else {
         this.gameMode = "unknown";
      }

      this.id = p_92327_.getId();
   }

   public int getPlayerCount() {
      return this.players;
   }

   public boolean isRemoteServer() {
      return this.isRemoteServer;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public String getGameMode() {
      return this.gameMode;
   }

   public UUID getSessionId() {
      return this.id;
   }
}