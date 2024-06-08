package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket implements Packet<ClientGamePacketListener> {
   private static final byte CONTINUE_MASK = -128;
   private final int entity;
   private final List<Pair<EquipmentSlot, ItemStack>> slots;

   public ClientboundSetEquipmentPacket(int p_133202_, List<Pair<EquipmentSlot, ItemStack>> p_133203_) {
      this.entity = p_133202_;
      this.slots = p_133203_;
   }

   public ClientboundSetEquipmentPacket(FriendlyByteBuf p_179297_) {
      this.entity = p_179297_.readVarInt();
      EquipmentSlot[] aequipmentslot = EquipmentSlot.values();
      this.slots = Lists.newArrayList();

      int i;
      do {
         i = p_179297_.readByte();
         EquipmentSlot equipmentslot = aequipmentslot[i & 127];
         ItemStack itemstack = p_179297_.readItem();
         this.slots.add(Pair.of(equipmentslot, itemstack));
      } while((i & -128) != 0);

   }

   public void write(FriendlyByteBuf p_133212_) {
      p_133212_.writeVarInt(this.entity);
      int i = this.slots.size();

      for(int j = 0; j < i; ++j) {
         Pair<EquipmentSlot, ItemStack> pair = this.slots.get(j);
         EquipmentSlot equipmentslot = pair.getFirst();
         boolean flag = j != i - 1;
         int k = equipmentslot.ordinal();
         p_133212_.writeByte(flag ? k | -128 : k);
         p_133212_.writeItem(pair.getSecond());
      }

   }

   public void handle(ClientGamePacketListener p_133209_) {
      p_133209_.handleSetEquipment(this);
   }

   public int getEntity() {
      return this.entity;
   }

   public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
      return this.slots;
   }
}