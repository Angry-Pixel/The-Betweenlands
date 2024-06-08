package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatKillPacket implements Packet<ClientGamePacketListener> {
   private final int playerId;
   private final int killerId;
   private final Component message;

   public ClientboundPlayerCombatKillPacket(CombatTracker p_179066_, Component p_179067_) {
      this(p_179066_.getMob().getId(), p_179066_.getKillerId(), p_179067_);
   }

   public ClientboundPlayerCombatKillPacket(int p_179062_, int p_179063_, Component p_179064_) {
      this.playerId = p_179062_;
      this.killerId = p_179063_;
      this.message = p_179064_;
   }

   public ClientboundPlayerCombatKillPacket(FriendlyByteBuf p_179069_) {
      this.playerId = p_179069_.readVarInt();
      this.killerId = p_179069_.readInt();
      this.message = p_179069_.readComponent();
   }

   public void write(FriendlyByteBuf p_179072_) {
      p_179072_.writeVarInt(this.playerId);
      p_179072_.writeInt(this.killerId);
      p_179072_.writeComponent(this.message);
   }

   public void handle(ClientGamePacketListener p_179076_) {
      p_179076_.handlePlayerCombatKill(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public int getKillerId() {
      return this.killerId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public Component getMessage() {
      return this.message;
   }
}