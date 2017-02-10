package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.audio.AnimatorSound;
import thebetweenlands.common.inventory.container.ContainerAnimator;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityAnimator extends TileEntityBasicInventory implements ITickable{
	//public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 20), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 30), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 40) };
	private int prevStackSize = 0;
	public ItemStack itemToAnimate = null;
	public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32, requiredLifeCount = 32;
	public boolean itemAnimated = false;
	private ItemStack prevItem;

	private boolean soundPlaying = false;

	public TileEntityAnimator() {
		super(3, "animator");
	}

	@Override
	public void update() {
		if(this.itemToAnimate != null) {
			IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(this.itemToAnimate);
			if(recipe != null) {
				this.requiredFuelCount = recipe.getRequiredFuel(this.itemToAnimate);
				this.requiredLifeCount = recipe.getRequiredLife(this.itemToAnimate);
			}
		}
		if(!worldObj.isRemote) {
			if (isCrystalInslot())
				lifeCrystalLife = 128 - getCrystalPower();
			if (!isSlotInUse(0) || !isSlotInUse(1) || !isSlotInUse(2)) {
				fuelBurnProgress = 0;
				fuelConsumed = 0;
			}
			if (isSlotInUse(0) && isCrystalInslot() && isSulfurInslot() && fuelConsumed < requiredFuelCount && isValidFocalItem()) {
				this.itemToAnimate = this.inventory[0];
				if (lifeCrystalLife >= this.requiredLifeCount) {
					fuelBurnProgress++;
					if (fuelBurnProgress >= 42) {
						fuelBurnProgress = 0;
						decrStackSize(2, 1);
						fuelConsumed++;
					}
					this.itemAnimated = false;
				}
			}
			if (isSlotInUse(2) && !this.itemAnimated) {
				if (!isSlotInUse(0) || !isSlotInUse(1)) {
					fuelBurnProgress = 0;
					fuelConsumed = 0;
				}
			}

			if (fuelConsumed >= requiredFuelCount && isSlotInUse(0) && isSlotInUse(1) && !this.itemAnimated) {
				IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(inventory[0]);
				ItemStack result = recipe.onAnimated(this.worldObj, getPos(), inventory[0]);
				if(result == null) result = recipe.getResult(inventory[0]);
				if(result != null) {
					setInventorySlotContents(0, result.copy());
					worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
				}
				inventory[1].setItemDamage(inventory[1].getItemDamage() + recipe.getRequiredLife(inventory[0]));
				this.itemAnimated = true;
			}
			if (prevStackSize != (isSlotInUse(0) ? inventory[0].stackSize : 0))
				worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			if (prevItem != (isSlotInUse(0) ? inventory[0] : null))
				worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			prevItem = isSlotInUse(0) ? inventory[0] : null;
			prevStackSize = isSlotInUse(0) ? inventory[0].stackSize : 0;
			updateContainingBlockInfo();
		} else {
			if(this.isRunning() && !this.soundPlaying) {
				this.playAnimatorSound();
				this.soundPlaying = true;
			} else if(!this.isRunning()) {
				this.soundPlaying = false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void playAnimatorSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(new AnimatorSound(SoundRegistry.ANIMATOR, SoundCategory.BLOCKS, this));
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isCrystalInslot() {
		return isSlotInUse(1) && inventory[1].getItem() == ItemRegistry.LIFE_CRYSTAL && inventory[1].getItemDamage() < inventory[1].getMaxDamage();
	}

	public int getCrystalPower() {
		if(isCrystalInslot())
			return inventory[1].getItemDamage();
		return 0;
	}

	public boolean isSulfurInslot() {
		return isSlotInUse(2) && inventory[2].getItem() == ItemRegistry.ITEMS_MISC && inventory[2].getItemDamage() == ItemMisc.EnumItemMisc.SULFUR.getID();
	}

	public boolean isSlotInUse(int slot) {
		return inventory[slot] != null;
	}

	public boolean isValidFocalItem() {
		return inventory[0] != null && AnimatorRecipe.getRecipe(inventory[0]) != null;
	}

	public void sendGUIData(ContainerAnimator animator, IContainerListener listener) {
		listener.sendProgressBarUpdate(animator, 0, fuelBurnProgress);
		listener.sendProgressBarUpdate(animator, 1, lifeCrystalLife);
		listener.sendProgressBarUpdate(animator, 2, itemAnimated ? 1 : 0);
		listener.sendProgressBarUpdate(animator, 3, fuelConsumed);
	}

	public void getGUIData(int id, int value) {
		switch (id) {
		case 0:
			fuelBurnProgress = value;
			break;
		case 1:
			lifeCrystalLife = value;
			break;
		case 2:
			itemAnimated = value == 1;
			break;
		case 3:
			fuelConsumed = value;
		}
	}

	public boolean isRunning() {
		return this.isSlotInUse(0) && this.isCrystalInslot() && this.isSulfurInslot() && this.fuelConsumed < this.requiredFuelCount && this.isValidFocalItem();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeNBT(nbt);
		return nbt;
	}

	protected void writeNBT(NBTTagCompound nbt) {
		nbt.setInteger("life", lifeCrystalLife);
		nbt.setInteger("progress", fuelBurnProgress);
		nbt.setInteger("itemsConsumed", fuelConsumed);
		nbt.setBoolean("lifeDepleted", itemAnimated);
		NBTTagCompound toAnimateCompound = new NBTTagCompound();
		if(this.itemToAnimate != null) {
			this.itemToAnimate.writeToNBT(toAnimateCompound);
		}
		nbt.setTag("toAnimate", toAnimateCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readNBT(nbt);
	}

	protected void readNBT(NBTTagCompound nbt) {
		lifeCrystalLife = nbt.getInteger("life");
		fuelBurnProgress = nbt.getInteger("progress");
		fuelConsumed = nbt.getInteger("itemsConsumed");
		itemAnimated = nbt.getBoolean("lifeDepleted");
		NBTTagCompound toAnimateStackCompound = nbt.getCompoundTag("toAnimate");
		if(toAnimateStackCompound.hasKey("id", Constants.NBT.TAG_STRING))
			this.itemToAnimate = ItemStack.loadItemStackFromNBT(toAnimateStackCompound);
		else
			this.itemToAnimate = null;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeNBT(nbt);
		this.writeInventoryNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		NBTTagCompound nbt = packet.getNbtCompound();
		this.readNBT(nbt);
		this.readInventoryNBT(nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeNBT(nbt);
		this.writeInventoryNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		this.readNBT(nbt);
		this.readInventoryNBT(nbt);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		if (slot == 1 && stack != null && stack.getItem().equals(ItemRegistry.LIFE_CRYSTAL))
			return true;
		else if (slot == 2 && stack != null && stack.getItem().equals(ItemRegistry.ITEMS_MISC) && stack.getItemDamage() == ItemMisc.EnumItemMisc.SULFUR.getID())
			return true;
		else if (slot == 0)
			return true;
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.DOWN || side == EnumFacing.UP)
			return new int[]{0};
		return new int[]{1, 2};

	}
}