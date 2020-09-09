package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityFishTrimmingTable extends TileEntity implements IInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);

	public TileEntityFishTrimmingTable() {
		super();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

    public boolean hasAnadia() {
    	ItemStack stack = getItems().get(0);
    	return !stack.isEmpty() && stack.getItem() == ItemRegistry.ANADIA && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND);
    }
	private boolean isAnadiaRotten() {
		ItemStack stack = getItems().get(0);
		if (stack.getTagCompound().getCompoundTag("Entity").hasKey("rottingTime") && stack.getTagCompound().getCompoundTag("Entity").getByte("fishColour") != 0) {
			long rottingTime = stack.getTagCompound().getCompoundTag("Entity").getLong("rottingTime");
			if (rottingTime - getWorld().getTotalWorldTime() <= 0)
				return true;
		}
		return false;
	}

    public boolean hasChopper() {
    	ItemStack stack = getItems().get(5);
    	return !stack.isEmpty() && stack.getItem() == ItemRegistry.BONE_AXE;
    }
 

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        loadFromNbt(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return saveToNbt(nbt);
    }

	public void loadFromNbt(NBTTagCompound nbt) {
		inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (nbt.hasKey("Items", 9))
			ItemStackHelper.loadAllItems(nbt, inventory);
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		return nbt;
	}

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

	@Override
	public String getName() {
		return new TextComponentTranslation("tile.thebetweenlands.fish_trimming_table.name").getFormattedText();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventory)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Override
    public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		if (!itemstack.isEmpty())
			this.markDirty();
		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());
        this.markDirty();
    }

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}
/*
	//NYI
	public int getAnadiaMeatBuff() {
		int saturation = 0;
		if(hasAnadia()) {
			ItemStack stack = getItems().get(0);
			float size = stack.getTagCompound().getCompoundTag("Entity").getFloat("fishSize");
			byte head = stack.getTagCompound().getCompoundTag("Entity").getByte("headType");
			byte body = stack.getTagCompound().getCompoundTag("Entity").getByte("bodyType");
			byte tail = stack.getTagCompound().getCompoundTag("Entity").getByte("tailType");
			saturation = Math.round(getStrengthMods(size, head, body, tail) + getStaminaMods(size, head, body, tail));
		}
		return saturation;
	}

	public float getStrengthMods(float sizeIn, byte headIndex, byte bodyIndex, byte tailIndex) {
		float head = EnumAnadiaHeadParts.values()[headIndex].getStrengthModifier();
		float body = EnumAnadiaHeadParts.values()[bodyIndex].getStrengthModifier();
		float tail = EnumAnadiaHeadParts.values()[tailIndex].getStrengthModifier();
		return Math.round((sizeIn * 0.5F) * head + body + tail * 2F) / 2F;
	}

	public float getStaminaMods(float sizeIn, byte headIndex, byte bodyIndex, byte tailIndex) {
		float head = EnumAnadiaHeadParts.values()[headIndex].getStaminaModifier();
		float body = EnumAnadiaHeadParts.values()[bodyIndex].getStaminaModifier();
		float tail = EnumAnadiaHeadParts.values()[tailIndex].getStaminaModifier();
		return Math.round(sizeIn * head + body + tail * 2F) / 2F;
	}
*/
	public Entity getAndiaEntity() {
		Entity entity = null;
		if (getItems().get(0).getTagCompound() != null && getItems().get(0).getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			entity = EntityList.createEntityFromNBT(getItems().get(0).getTagCompound().getCompoundTag("Entity"), getWorld());
			entity.setPositionAndRotation(0D, 0D, 0D, 0F, 0F);
		}
		return entity;
	}

	public ItemStack getSlotresult(int slot) {
		if (hasAnadia()) {
			switch (slot) {
			case 0:
				return ItemStack.EMPTY;
			case 1:
				return !isAnadiaRotten() ? ((EntityAnadia) getAndiaEntity()).getHeadItem() : new ItemStack(ItemRegistry.ROTTEN_FOOD); // possible alt loot table?
			case 2:
				return !isAnadiaRotten() ? ((EntityAnadia) getAndiaEntity()).getBodyItem() : new ItemStack(ItemRegistry.ROTTEN_FOOD); // possible alt loot table?
			case 3:
				return !isAnadiaRotten() ? ((EntityAnadia) getAndiaEntity()).getTailItem() : new ItemStack(ItemRegistry.ROTTEN_FOOD); // possible alt loot table?
			case 4:
				return EnumItemMisc.ANADIA_REMAINS.create(3);
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean allResultSlotsEmpty() {
		return getItems().get(1).isEmpty() && getItems().get(2).isEmpty() && getItems().get(3).isEmpty() && getItems().get(4).isEmpty();
	}

}
