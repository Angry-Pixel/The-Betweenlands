package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.IntFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_SLOT_COUNT = 128;
   private final int containerId;
   private final int stateId;
   private final int slotNum;
   private final int buttonNum;
   private final ClickType clickType;
   private final ItemStack carriedItem;
   private final Int2ObjectMap<ItemStack> changedSlots;

   public ServerboundContainerClickPacket(int p_182734_, int p_182735_, int p_182736_, int p_182737_, ClickType p_182738_, ItemStack p_182739_, Int2ObjectMap<ItemStack> p_182740_) {
      this.containerId = p_182734_;
      this.stateId = p_182735_;
      this.slotNum = p_182736_;
      this.buttonNum = p_182737_;
      this.clickType = p_182738_;
      this.carriedItem = p_182739_;
      this.changedSlots = Int2ObjectMaps.unmodifiable(p_182740_);
   }

   public ServerboundContainerClickPacket(FriendlyByteBuf p_179578_) {
      this.containerId = p_179578_.readByte();
      this.stateId = p_179578_.readVarInt();
      this.slotNum = p_179578_.readShort();
      this.buttonNum = p_179578_.readByte();
      this.clickType = p_179578_.readEnum(ClickType.class);
      IntFunction<Int2ObjectOpenHashMap<ItemStack>> intfunction = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
      this.changedSlots = Int2ObjectMaps.unmodifiable(p_179578_.readMap(intfunction, (p_179580_) -> {
         return Integer.valueOf(p_179580_.readShort());
      }, FriendlyByteBuf::readItem));
      this.carriedItem = p_179578_.readItem();
   }

   public void write(FriendlyByteBuf p_133961_) {
      p_133961_.writeByte(this.containerId);
      p_133961_.writeVarInt(this.stateId);
      p_133961_.writeShort(this.slotNum);
      p_133961_.writeByte(this.buttonNum);
      p_133961_.writeEnum(this.clickType);
      p_133961_.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, FriendlyByteBuf::writeItem);
      p_133961_.writeItemStack(this.carriedItem, false); //Forge: Include full tag for C->S
   }

   public void handle(ServerGamePacketListener p_133958_) {
      p_133958_.handleContainerClick(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public int getButtonNum() {
      return this.buttonNum;
   }

   public ItemStack getCarriedItem() {
      return this.carriedItem;
   }

   public Int2ObjectMap<ItemStack> getChangedSlots() {
      return this.changedSlots;
   }

   public ClickType getClickType() {
      return this.clickType;
   }

   public int getStateId() {
      return this.stateId;
   }
}
