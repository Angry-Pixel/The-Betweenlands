package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundResourcePackPacket implements Packet<ServerGamePacketListener> {
   private final ServerboundResourcePackPacket.Action action;

   public ServerboundResourcePackPacket(ServerboundResourcePackPacket.Action p_134409_) {
      this.action = p_134409_;
   }

   public ServerboundResourcePackPacket(FriendlyByteBuf p_179740_) {
      this.action = p_179740_.readEnum(ServerboundResourcePackPacket.Action.class);
   }

   public void write(FriendlyByteBuf p_134417_) {
      p_134417_.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener p_134415_) {
      p_134415_.handleResourcePackResponse(this);
   }

   public ServerboundResourcePackPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}