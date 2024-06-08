package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ServerboundUseItemPacket implements Packet<ServerGamePacketListener> {
   private final InteractionHand hand;

   public ServerboundUseItemPacket(InteractionHand p_134710_) {
      this.hand = p_134710_;
   }

   public ServerboundUseItemPacket(FriendlyByteBuf p_179798_) {
      this.hand = p_179798_.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf p_134719_) {
      p_134719_.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener p_134716_) {
      p_134716_.handleUseItem(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}