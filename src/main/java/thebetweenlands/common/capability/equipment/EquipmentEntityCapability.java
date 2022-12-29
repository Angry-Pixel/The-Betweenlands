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
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.VALUES) {
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
		nbt.setInteger("amuletSlots", this.amuletSlots);
		NBTTagList inventoryList = new NBTTagList();
		for (EnumEquipmentInventory inventoryType : EnumEquipmentInventory.VALUES) {
			NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
			NBTTagCompound inventoryNbt = new NBTTagCompound();
			NBTTagList slotList = new NBTTagList();
			for (int c = 0; c < inventoryStacks.size(); c++) {
				ItemStack stack = inventoryStacks.get(c);
				if (!stack.isEmpty()) {
					NBTTagCompound slotNbt = new NBTTagCompound();
					slotNbt.setInteger("slot", c);
					slotNbt.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
					slotList.appendTag(slotNbt);
				}
			}
			if (slotList.tagCount() > 0) {
				inventoryNbt.setInteger("id", inventoryType.id);
				inventoryNbt.setTag("items", slotList);
				inventoryList.appendTag(inventoryNbt);
			}
		}
		if (inventoryList.tagCount() > 0)
			nbt.setTag("inventories", inventoryList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inventories.clear();
		for (EnumEquipmentInventory inventory : EnumEquipmentInventory.VALUES) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(inventory.maxSize, ItemStack.EMPTY));
		}
		if (nbt.hasKey("amuletSlots")) {
			this.amuletSlots = nbt.getInteger("amuletSlots");
		}
		if (nbt.hasKey("inventories")) {
			NBTTagList inventoryList = nbt.getTagList("inventories", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < inventoryList.tagCount(); i++) {
				NBTTagCompound inventoryNbt = inventoryList.getCompoundTagAt(i);
				if (inventoryNbt.hasKey("items")) {
					int id = inventoryNbt.getInteger("id");
					EnumEquipmentInventory inventoryType = EnumEquipmentInventory.fromID(id);
					if (inventoryType != null) {
						NonNullList<ItemStack> inventoryStacks = this.allInventoryStacks.get(inventoryType);
						NBTTagList slotList = inventoryNbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
						for (int c = 0; c < slotList.tagCount(); c++) {
							NBTTagCompound slotNbt = slotList.getCompoundTagAt(c);
							int slot = slotNbt.getInteger("slot");
							if (slot < inventoryStacks.size()) {
								inventoryStacks.set(slot, new ItemStack(slotNbt.getCompoundTag("stack")));
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
