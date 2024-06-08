package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket implements Packet<ClientGamePacketListener> {
   public static final int CARRIED_ITEM = -1;
   public static final int PLAYER_INVENTORY = -2;
   private final int containerId;
   private final int stateId;
   private final int slot;
   private final ItemStack itemStack;

   public ClientboundContainerSetSlotPacket(int p_131982_, int p_182713_, int p_131983_, ItemStack p_131984_) {
      this.containerId = p_131982_;
      this.stateId = p_182713_;
      this.slot = p_131983_;
      this.itemStack = p_131984_.copy();
   }

   public ClientboundContainerSetSlotPacket(FriendlyByteBuf p_178829_) {
      this.containerId = p_178829_.readByte();
      this.stateId = p_178829_.readVarInt();
      this.slot = p_178829_.readShort();
      this.itemStack = p_178829_.readItem();
   }

   public void write(FriendlyByteBuf p_131993_) {
      p_131993_.writeByte(this.containerId);
      p_131993_.writeVarInt(this.stateId);
      p_131993_.writeShort(this.slot);
      p_131993_.writeItem(this.itemStack);
   }

   public void handle(ClientGamePacketListener p_131990_) {
      p_131990_.handleContainerSetSlot(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public int getStateId() {
      return this.stateId;
   }
}