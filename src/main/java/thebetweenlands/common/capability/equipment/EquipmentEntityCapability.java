package thebetweenlands.common.capability.equipment;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.inventory.InventoryEquipment;
import thebetweenlands.common.inventory.InventoryEquipmentAmulets;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EquipmentEntityCapability extends EntityCapability<EquipmentEntityCapability, IEquipmentCapability, EntityPlayer> implements IEquipmentCapability, ISerializableCapability {
	private Map<EnumEquipmentInventory, NonNullList<ItemStack>> allInventoryStacks = new EnumMap<>(EnumEquipmentInventory.class);
	private Map<EnumEquipmentInventory, IInventory> inventories = new EnumMap<>(EnumEquipmentInventory.class);
	private int amuletSlots = 1;

	public EquipmentEntityCapability() {
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.values()) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(inventory.maxSize, ItemStack.EMPTY));
		}
	}

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "equipment");
	}

	@Override
	protected Capability<IEquipmentCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_EQUIPMENT;
	}

	@Override
	protected Class<IEquipmentCapability> getCapabilityClass() {
		return IEquipmentCapability.class;
	}

	@Override
	protected EquipmentEntityCapability getDefaultCapabilityImplementation() {
		return new EquipmentEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityLivingBase;
	}

	@Override
	public boolean isPersistent(EntityPlayer oldPlayer, EntityPlayer newPlayer, boolean wasDead) {
		return !wasDead || this.getEntity().world.getGameRules().getBoolean("keepInventory");
	}

	@Override
	public IInventory getInventory(EnumEquipmentInventory inventoryType) {
		IInventory inventory = this.inventories.get(inventoryType);
		if(inventory == null) {
			switch (inventoryType) {
			case AMULET:
				inventory = new InventoryEquipmentAmulets(this, this.allInventoryStacks.get(inventoryType));
				break;
			default:
				inventory = new InventoryEquipment(this, this.allInventoryStacks.get(inventoryType));
				break;
			}
			this.inventories.put(inventoryType, inventory);
		}
		return inventory;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInt("amuletSlots", this.amuletSlots);
		NBTTagList inventoryList = new NBTTagList();
		for (EnumEquipmentInventory inventoryType : EnumEquipmentInventory.values()) {
			NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
			NBTTagCompound inventoryNbt = new NBTTagCompound();
			NBTTagList slotList = new NBTTagList();
			for (int c = 0; c < inventoryStacks.size(); c++) {
				ItemStack stack = inventoryStacks.get(c);
				if (!stack.isEmpty()) {
					NBTTagCompound slotNbt = new NBTTagCompound();
					slotNbt.setInt("slot", c);
					slotNbt.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
					slotList.add(slotNbt);
				}
			}
			if (slotList.size() > 0) {
				inventoryNbt.setInt("id", inventoryType.id);
				inventoryNbt.setTag("items", slotList);
				inventoryList.add(inventoryNbt);
			}
		}
		if (inventoryList.size() > 0)
			nbt.setTag("inventories", inventoryList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inventories.clear();
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.values()) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(inventory.maxSize, ItemStack.EMPTY));
		}
		if (nbt.contains("amuletSlots")) {
			this.amuletSlots = nbt.getInt("amuletSlots");
		}
		if (nbt.contains("inventories")) {
			NBTTagList inventoryList = nbt.getList("inventories", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < inventoryList.size(); i++) {
				NBTTagCompound inventoryNbt = inventoryList.getCompound(i);
				if (inventoryNbt.contains("items")) {
					int id = inventoryNbt.getInt("id");
					EnumEquipmentInventory inventoryType = EnumEquipmentInventory.fromID(id);
					if (inventoryType != null) {
						NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
						NBTTagList slotList = inventoryNbt.getList("items", Constants.NBT.TAG_COMPOUND);
						for (int c = 0; c < slotList.size(); c++) {
							NBTTagCompound slotNbt = slotList.getCompound(c);
							int slot = slotNbt.getInt("slot");
							if (slot < inventoryStacks.size()) {
								inventoryStacks.set(slot, new ItemStack(slotNbt.getCompound("stack")));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 10;
	}

	@Override
	public int getAmuletSlots() {
		return this.amuletSlots;
	}

	@Override
	public void setAmuletSlots(int slots) {
		this.amuletSlots = slots;
	}
}
