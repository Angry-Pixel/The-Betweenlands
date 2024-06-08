package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
   private final int killerId;
   private final int duration;

   public ClientboundPlayerCombatEndPacket(CombatTracker p_179040_) {
      this(p_179040_.getKillerId(), p_179040_.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(int p_179037_, int p_179038_) {
      this.killerId = p_179037_;
      this.duration = p_179038_;
   }

   public ClientboundPlayerCombatEndPacket(FriendlyByteBuf p_179042_) {
      this.duration = p_179042_.readVarInt();
      this.killerId = p_179042_.readInt();
   }

   public void write(FriendlyByteBuf p_179044_) {
      p_179044_.writeVarInt(this.duration);
      p_179044_.writeInt(this.killerId);
   }

   public void handle(ClientGamePacketListener p_179048_) {
      p_179048_.handlePlayerCombatEnd(this);
   }
}