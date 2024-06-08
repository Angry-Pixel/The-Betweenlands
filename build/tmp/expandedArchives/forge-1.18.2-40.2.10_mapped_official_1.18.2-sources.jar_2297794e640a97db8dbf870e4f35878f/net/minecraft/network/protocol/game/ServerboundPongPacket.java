package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPongPacket implements Packet<ServerGamePacketListener> {
   private final int id;

   public ServerboundPongPacket(int p_179723_) {
      this.id = p_179723_;
   }

   public ServerboundPongPacket(FriendlyByteBuf p_179725_) {
      this.id = p_179725_.readInt();
   }

   public void write(FriendlyByteBuf p_179727_) {
      p_179727_.writeInt(this.id);
   }

   public void handle(ServerGamePacketListener p_179731_) {
      p_179731_.handlePong(this);
   }

   public int getId() {
      return this.id;
   }
}