package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
   private final ServerboundClientCommandPacket.Action action;

   public ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action p_133843_) {
      this.action = p_133843_;
   }

   public ServerboundClientCommandPacket(FriendlyByteBuf p_179547_) {
      this.action = p_179547_.readEnum(ServerboundClientCommandPacket.Action.class);
   }

   public void write(FriendlyByteBuf p_133852_) {
      p_133852_.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener p_133849_) {
      p_133849_.handleClientCommand(this);
   }

   public ServerboundClientCommandPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS;
   }
}