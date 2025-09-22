package thebetweenlands.common.component.entity.equipment;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import javax.annotation.Nullable;

public class EquipmentDataSerializer implements IAttachmentSerializer<CompoundTag, EquipmentData>, AttachmentSyncHandler<EquipmentData> {

	@Override
	public @Nullable CompoundTag write(EquipmentData data, HolderLookup.Provider registries) {
		return serialize(data, registries);
	}

	@Override
	public EquipmentData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider registries) {
		return deserialize(tag, registries);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buf, EquipmentData data, boolean b) {
		buf.writeNbt(serialize(data, buf.registryAccess()));
	}

	@Override
	public @Nullable EquipmentData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable EquipmentData data) {
		return deserialize(buf.readNbt(), buf.registryAccess());
	}

	private static CompoundTag serialize(EquipmentData data, HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("amulet_slots", data.getAmuletSlots());
		tag.putInt("max_amulet_slots", data.maxAmuletSlots);
		tag.putInt("max_ring_slots", data.maxRingSlots);
		tag.putInt("max_misc_slots", data.maxMiscSlots);
		ListTag inventoryList = new ListTag();
		for (EquipmentInventoryType inventoryType : EquipmentInventoryType.values()) {
			NonNullList<ItemStack> inventoryStacks = data.allInventoryStacks.get(inventoryType);
			CompoundTag inventoryNbt = new CompoundTag();
			ListTag slotList = new ListTag();
			for (int c = 0; c < inventoryStacks.size(); c++) {
				ItemStack stack = inventoryStacks.get(c);
				if (!stack.isEmpty()) {
					CompoundTag slotNbt = new CompoundTag();
					slotNbt.putInt("slot", c);
					slotNbt.put("stack", stack.save(registries));
					slotList.add(slotNbt);
				}
			}
			if (!slotList.isEmpty()) {
				inventoryNbt.putInt("id", inventoryType.ordinal());
				inventoryNbt.put("items", slotList);
				inventoryList.add(inventoryNbt);
			}
		}
		if (!inventoryList.isEmpty())
			tag.put("inventories", inventoryList);
		return tag;
	}

	private static EquipmentData deserialize(CompoundTag tag, HolderLookup.Provider registries) {
		EquipmentData data = new EquipmentData();
		if (tag.contains("amulet_slots")) {
			data.setAmuletSlots(tag.getInt("amulet_slots"));
		}
		if (tag.contains("max_amulet_slots")) {
			data.maxAmuletSlots = tag.getInt("max_amulet_slots");
		}
		if (tag.contains("max_ring_slots")) {
			data.maxRingSlots = tag.getInt("max_ring_slots");
		}
		if (tag.contains("max_misc_slots")) {
			data.maxMiscSlots = tag.getInt("max_misc_slots");
		}
		if (tag.contains("inventories")) {
			ListTag inventoryList = tag.getList("inventories", Tag.TAG_COMPOUND);
			for (int i = 0; i < inventoryList.size(); i++) {
				CompoundTag inventoryNbt = inventoryList.getCompound(i);
				if (inventoryNbt.contains("items")) {
					int id = inventoryNbt.getInt("id");
					EquipmentInventoryType inventoryType = EquipmentInventoryType.fromID(id);
					if (inventoryType != null) {
						NonNullList<ItemStack> inventoryStacks = data.allInventoryStacks.get(inventoryType);
						ListTag slotList = inventoryNbt.getList("items", Tag.TAG_COMPOUND);
						for (int c = 0; c < slotList.size(); c++) {
							CompoundTag slotNbt = slotList.getCompound(c);
							int slot = slotNbt.getInt("slot");
							if (slot < inventoryStacks.size()) {
								inventoryStacks.set(slot, ItemStack.parseOptional(registries, slotNbt.getCompound("stack")));
							}
						}
					}
				}
			}
		}
		return data;
	}
}
