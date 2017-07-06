package thebetweenlands.common.capability.equipment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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



	//private List<ItemStack>[] inventories;
	private List<List<ItemStack>> inventories;
	private int amuletSlots = 1;

	public EquipmentEntityCapability() {
		//this.inventories = (ArrayList<ItemStack>[])new ArrayList[EnumEquipmentInventory.values().length]; // this is fug.  arraylist of arraylist probably better
		this.inventories = new ArrayList<List<ItemStack>>(EnumEquipmentInventory.values().length); 
		//this.inventories = new ItemStack[EnumEquipmentInventory.values().length][];
		for(EnumEquipmentInventory inventory : EnumEquipmentInventory.values()) {
			this.inventories.add(inventory.id, new ArrayList<ItemStack>(Collections.nCopies(inventory.maxSize, ItemStack.EMPTY)));
		}
	}
	
	@Override
	public IInventory getInventory(EnumEquipmentInventory inventory) {
		switch(inventory) {
		case AMULET:
			return new InventoryEquipmentAmulets(this, this.inventories.get(inventory.id));
		default:
			return new InventoryEquipment(this, this.inventories.get(inventory.id));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("amuletSlots", this.amuletSlots);
		NBTTagList inventoryList = new NBTTagList();
		for(int i = 0; i < this.inventories.size(); i++) {
			NBTTagCompound inventoryNbt = new NBTTagCompound();
			NBTTagList slotList = new NBTTagList();
			for(int c = 0; c < this.inventories.get(i).size(); c++) {
				ItemStack stack = this.inventories.get(i).get(c);
				if(stack != null) {
					NBTTagCompound slotNbt = new NBTTagCompound();
					slotNbt.setInteger("slot", c);
					slotNbt.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
					slotList.appendTag(slotNbt);
				}
			}
			if(slotList.tagCount() > 0) {
				inventoryNbt.setInteger("id", i);
				inventoryNbt.setTag("items", slotList);
				inventoryList.appendTag(inventoryNbt);
			}
		}
		if(inventoryList.tagCount() > 0)
			nbt.setTag("inventories", inventoryList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for(EnumEquipmentInventory inventory : EnumEquipmentInventory.values()) {
			this.inventories.set(inventory.id, new ArrayList<ItemStack>(Collections.nCopies(inventory.maxSize, ItemStack.EMPTY)));
		}
		if(nbt.hasKey("amuletSlots")) {
			this.amuletSlots = nbt.getInteger("amuletSlots");
		}
		if(nbt.hasKey("inventories")) {
			NBTTagList inventoryList = nbt.getTagList("inventories", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < inventoryList.tagCount(); i++) {
				NBTTagCompound inventoryNbt = inventoryList.getCompoundTagAt(i);
				if(inventoryNbt.hasKey("items")) {
					int id = inventoryNbt.getInteger("id");
					if(id < this.inventories.size()) {
						List<ItemStack> inventoryStacks = this.inventories.get(id);
						NBTTagList slotList = inventoryNbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
						for(int c = 0; c < slotList.tagCount(); c++) {
							NBTTagCompound slotNbt = slotList.getCompoundTagAt(c);
							int slot = slotNbt.getInteger("slot");
							if(slot < inventoryStacks.size()) {
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
		return 0;
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
