package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatEnterPacket implements Packet<ClientGamePacketListener> {
   public ClientboundPlayerCombatEnterPacket() {
   }

   public ClientboundPlayerCombatEnterPacket(FriendlyByteBuf p_179051_) {
   }

   public void write(FriendlyByteBuf p_179053_) {
   }

   public void handle(ClientGamePacketListener p_179057_) {
      p_179057_.handlePlayerCombatEnter(this);
   }
}