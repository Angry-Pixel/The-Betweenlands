package net.minecraft.world.item;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;

public class ServerItemCooldowns extends ItemCooldowns {
   private final ServerPlayer player;

   public ServerItemCooldowns(ServerPlayer p_43067_) {
      this.player = p_43067_;
   }

   protected void onCooldownStarted(Item p_43069_, int p_43070_) {
      super.onCooldownStarted(p_43069_, p_43070_);
      this.player.connection.send(new ClientboundCooldownPacket(p_43069_, p_43070_));
   }

   protected void onCooldownEnded(Item p_43072_) {
      super.onCooldownEnded(p_43072_);
      this.player.connection.send(new ClientboundCooldownPacket(p_43072_, 0));
   }
}