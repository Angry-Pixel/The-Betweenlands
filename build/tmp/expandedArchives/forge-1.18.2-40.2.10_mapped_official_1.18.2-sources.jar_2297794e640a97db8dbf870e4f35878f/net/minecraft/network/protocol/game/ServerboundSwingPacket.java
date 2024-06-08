package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket implements Packet<ServerGamePacketListener> {
   private final InteractionHand hand;

   public ServerboundSwingPacket(InteractionHand p_134667_) {
      this.hand = p_134667_;
   }

   public ServerboundSwingPacket(FriendlyByteBuf p_179792_) {
      this.hand = p_179792_.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf p_134676_) {
      p_134676_.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener p_134673_) {
      p_134673_.handleAnimate(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}