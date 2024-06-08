package net.minecraft.server.players;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SleepStatus {
   private int activePlayers;
   private int sleepingPlayers;

   public boolean areEnoughSleeping(int p_144003_) {
      return this.sleepingPlayers >= this.sleepersNeeded(p_144003_);
   }

   public boolean areEnoughDeepSleeping(int p_144005_, List<ServerPlayer> p_144006_) {
      int i = (int)p_144006_.stream().filter(Player::isSleepingLongEnough).count();
      return i >= this.sleepersNeeded(p_144005_);
   }

   public int sleepersNeeded(int p_144011_) {
      return Math.max(1, Mth.ceil((float)(this.activePlayers * p_144011_) / 100.0F));
   }

   public void removeAllSleepers() {
      this.sleepingPlayers = 0;
   }

   public int amountSleeping() {
      return this.sleepingPlayers;
   }

   public boolean update(List<ServerPlayer> p_144008_) {
      int i = this.activePlayers;
      int j = this.sleepingPlayers;
      this.activePlayers = 0;
      this.sleepingPlayers = 0;

      for(ServerPlayer serverplayer : p_144008_) {
         if (!serverplayer.isSpectator()) {
            ++this.activePlayers;
            if (serverplayer.isSleeping()) {
               ++this.sleepingPlayers;
            }
         }
      }

      return (j > 0 || this.sleepingPlayers > 0) && (i != this.activePlayers || j != this.sleepingPlayers);
   }
}