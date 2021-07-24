package thebetweenlands.common.tile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityCrabPot extends TileEntity implements ITickable, IInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	public boolean active;
	public int fallCounter = 16;
	public int fallCounterPrev;
	public int horizontalIndex = 0;
	public float animationTicks;
	public boolean animate = false;
	public boolean hasUpdated = false;
	EntityPlayer placer;
	public String placerUUID;
	public TileEntityCrabPot() {
		super();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		if (world.isRemote) {
			fallCounterPrev = fallCounter;
			if (!hasBaitItem()) {
				if (fallCounter > 0)
					fallCounter--;
				if (fallCounter <= 0)
					fallCounter = 0;
			}
			if (hasBaitItem() && fallCounter != 32)
				fallCounter = 32;

			if(animate)
				animationTicks++;
			if(!animate && animationTicks > 0)
				animationTicks = 0;
		}

		if(!world.isRemote) {
			// because the player is always null unless the world is loaded but block NBT is loaded before grrrrr
			if(!hasUpdated && !placerUUID.isEmpty()) {
		        UUID id = UUID.fromString(placerUUID);
				EntityPlayer player = this.world.getPlayerEntityByUUID(id);
				if(player != null) {
					setPlacer(player);
					markForUpdate();
					hasUpdated = true;
				}
			}

			if(hasBaitItem() && !active)
				active = true;

			if(!hasBaitItem() && active)
				active = false;

			if(active && world.getTotalWorldTime()%20 == 0) {
				lureCloseCrab();
				if(checkCatch() != null) {
					ItemStack itemMob = ItemStack.EMPTY;
					if(checkCatch() instanceof EntitySiltCrab)
						itemMob = ((ItemMob) new ItemStack(ItemRegistry.SILT_CRAB).getItem()).capture(checkCatch());
					if(checkCatch()  instanceof EntityBubblerCrab)
						itemMob = ((ItemMob) new ItemStack(ItemRegistry.BUBBLER_CRAB).getItem()).capture(checkCatch());
					checkCatch().setDead();
					getItems().set(0, itemMob);
					markForUpdate();
			        if (hasSiltCrab() || hasBubblerCrab() && getPlacer() != null)
			        	if (getPlacer() instanceof EntityPlayerMP)
			        		AdvancementCriterionRegistry.CRAB_POT.trigger((EntityPlayerMP) getPlacer());
				}
			}
	        if (getWorld().getBlockState(pos.down()).getBlock() != BlockRegistry.CRAB_POT_FILTER && animate) {
	        	animate = false;
	        	markForUpdate();
	        }
		}
	}

	private EntityLiving checkCatch() {
		EntityLiving entity = null;
		List<EntityLiving> list = getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.up()).grow(-0.25D, 0F, -0.25D));
		if (!list.isEmpty())
			if(list.get(0) instanceof EntitySiltCrab || list.get(0) instanceof EntityBubblerCrab)
			entity = list.get(0);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public void lureCloseCrab() {
		List<EntityLiving> list = getWorld().getEntitiesWithinAABB(EntityLiving.class, extendRangeBox());
		for (Iterator<EntityLiving> iterator = list.iterator(); iterator.hasNext();) {
			EntityLiving entity = iterator.next();
			if (!entity.isInWater() || (!(entity instanceof EntitySiltCrab) && !(entity instanceof EntityBubblerCrab)))
				iterator.remove();
		}
		if (list.isEmpty())
			return;
		if (!list.isEmpty()) {
			EntityLiving foundCrab = list.get(0);
			Collections.shuffle(list);
			if(foundCrab instanceof EntitySiltCrab || foundCrab instanceof EntityBubblerCrab) {
				foundCrab = list.get(0);
				foundCrab.getNavigator().tryMoveToXYZ(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, 1D);
			}
		}
	}

	public AxisAlignedBB extendRangeBox() {
		return  new AxisAlignedBB(pos).grow(8D, 4D, 8D);
	}

	public boolean hasBaitItem() {
		ItemStack baitItem = getItems().get(0);
		return !baitItem.isEmpty() && baitItem.getItem() == EnumItemMisc.ANADIA_REMAINS.getItem();
	}

	public boolean hasSiltCrab() {
		ItemStack crabItem = getItems().get(0);
		return !crabItem.isEmpty() && crabItem.getItem() == ItemRegistry.SILT_CRAB;
	}

	public boolean hasBubblerCrab() {
		ItemStack crabItem = getItems().get(0);
		return !crabItem.isEmpty() && crabItem.getItem() == ItemRegistry.BUBBLER_CRAB;
	}

	public void markForUpdate() {
		IBlockState state = this.getWorld().getBlockState(this.getPos());
		this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
	}

	public Entity getEntity() {
		ItemStack stack = this.getItems().get(0);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemMob && ((ItemMob) stack.getItem()).hasEntityData(stack)) {
			return ((ItemMob) stack.getItem()).createCapturedEntity(this.world, 0, 0, 0, stack);
		}
		return null;
	}
	
	public void setRotation(int horizontalIndexIn) {
		horizontalIndex = horizontalIndexIn;
	}
	
	public int getRotation() {
		return horizontalIndex;
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
		active = nbt.getBoolean("active");
		animate = nbt.getBoolean("animate");
		setRotation(nbt.getInteger("horizontalIndex"));

        if (nbt.hasKey("OwnerUUID", 8))
        	placerUUID = nbt.getString("OwnerUUID");
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		nbt.setBoolean("active", active);
		nbt.setInteger("horizontalIndex", getRotation());
		nbt.setBoolean("animate", animate);

		if (getPlacer() != null)
			nbt.setString("OwnerUUID", getPlacer().getUniqueID().toString());
        else
        	nbt.setString("OwnerUUID", "");
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
		return new TextComponentTranslation("tile.thebetweenlands.crab_pot.name").getFormattedText();
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
		return 1;
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
	
	private EntityPlayer getPlacer() {
		return this.placer;
	}

	public void setPlacer(EntityPlayer player) {
		this.placer = player;
	}

}
