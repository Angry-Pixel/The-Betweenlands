package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final int id;
   private final int value;

   public ClientboundContainerSetDataPacket(int p_131963_, int p_131964_, int p_131965_) {
      this.containerId = p_131963_;
      this.id = p_131964_;
      this.value = p_131965_;
   }

   public ClientboundContainerSetDataPacket(FriendlyByteBuf p_178825_) {
      this.containerId = p_178825_.readUnsignedByte();
      this.id = p_178825_.readShort();
      this.value = p_178825_.readShort();
   }

   public void write(FriendlyByteBuf p_131974_) {
      p_131974_.writeByte(this.containerId);
      p_131974_.writeShort(this.id);
      p_131974_.writeShort(this.value);
   }

   public void handle(ClientGamePacketListener p_131971_) {
      p_131971_.handleContainerSetData(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getId() {
      return this.id;
   }

   public int getValue() {
      return this.value;
   }
}