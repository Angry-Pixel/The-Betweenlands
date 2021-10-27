package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.client.audio.AnimatorSound;
import thebetweenlands.common.inventory.container.ContainerAnimator;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityAnimator extends TileEntityBasicInventory implements ITickable {
    public ItemStack itemToAnimate = ItemStack.EMPTY;
    public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32, requiredLifeCount = 32;
    public boolean itemAnimated = false;
    //public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(new ItemStack(BLItemRegistry.lifeCrystal), 10), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD), 20), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.OCTINE_INGOT), 30), new WeightedRandomItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 40) };
    private int prevStackSize = 0;
    private ItemStack prevItem = ItemStack.EMPTY;

    private boolean running = false;
    
    private boolean soundPlaying = false;

    public TileEntityAnimator() {
        super(3, "container.bl.animator");
    }

    @Override
    public void update() {
        if (isSlotInUse(0) && isValidFocalItem()) {
            this.itemToAnimate = this.inventory.get(0);
            if(!this.world.isRemote) {
	            IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(this.itemToAnimate);
	            if (recipe != null) {
	                this.requiredFuelCount = recipe.getRequiredFuel(this.itemToAnimate);
	                this.requiredLifeCount = recipe.getRequiredLife(this.itemToAnimate);
	            }
            }
        } else {
            this.itemToAnimate = ItemStack.EMPTY;
        }
        if (!world.isRemote) {
            if (isCrystalInslot())
                lifeCrystalLife = getCrystalPower();
            if (!isSlotInUse(0) || !isSlotInUse(1) || !isSlotInUse(2)) {
                fuelBurnProgress = 0;
                fuelConsumed = 0;
            }

            if (!this.itemToAnimate.isEmpty() && isCrystalInslot() && isSulfurInSlot() && fuelConsumed < requiredFuelCount && isValidFocalItem()) {
                if (lifeCrystalLife >= this.requiredLifeCount) {
                    fuelBurnProgress++;
                    if (fuelBurnProgress >= 42) {
                        fuelBurnProgress = 0;
                        decrStackSize(2, 1);
                        fuelConsumed++;
                        markDirty();
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
                IAnimatorRecipe recipe = AnimatorRecipe.getRecipe(inventory.get(0));
                if(recipe != null) {
                	ItemStack input = inventory.get(0).copy();
	                ItemStack result = recipe.onAnimated(this.world, getPos(), inventory.get(0));
	                if (result.isEmpty()) result = recipe.getResult(inventory.get(0));
	                if (!result.isEmpty()) {
	                    setInventorySlotContents(0, result.copy());
	                    
	                    AxisAlignedBB aabb = new AxisAlignedBB(this.getPos()).grow(12);
	                    for(EntityPlayerMP player : this.world.getEntitiesWithinAABB(EntityPlayerMP.class, aabb, EntitySelectors.NOT_SPECTATING)) {
	                    	if(player.getDistanceSq(this.getPos()) <= 144) {
	                    		AdvancementCriterionRegistry.ANIMATE.trigger(input, result.copy(), player);
	                    	}
	                    }
	                }
                }
                inventory.get(1).setItemDamage(inventory.get(1).getItemDamage() + this.requiredLifeCount);
                markDirty();
                this.itemAnimated = true;
            }
            if (prevStackSize != (isSlotInUse(0) ? inventory.get(0).getCount() : 0))
                markDirty();
            if (prevItem != (isSlotInUse(0) ? inventory.get(0) : ItemStack.EMPTY))
                markDirty();
            prevItem = isSlotInUse(0) ? inventory.get(0) : ItemStack.EMPTY;
            prevStackSize = isSlotInUse(0) ? inventory.get(0).getCount() : 0;
            
            boolean shouldBeRunning = this.isSlotInUse(0) && this.isCrystalInslot() && this.isSulfurInSlot() && this.fuelConsumed < this.requiredFuelCount && lifeCrystalLife >= this.requiredLifeCount && this.isValidFocalItem();
            if(this.running != shouldBeRunning) {
            	this.running = shouldBeRunning;
            	this.markDirty();
            }
            
            updateContainingBlockInfo();
        } else {
            if (this.isRunning() && !this.soundPlaying) {
                this.playAnimatorSound();
                this.soundPlaying = true;
            } else if (!this.isRunning()) {
                this.soundPlaying = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void playAnimatorSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(new AnimatorSound(SoundRegistry.ANIMATOR, SoundCategory.BLOCKS, this));
    }

    @Override
    public boolean isEmpty() {
        return inventory.size() <= 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null)
            world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public boolean isCrystalInslot() {
        return isSlotInUse(1) && inventory.get(1).getItem() instanceof ItemLifeCrystal && inventory.get(1).getItemDamage() < inventory.get(1).getMaxDamage();
    }

    public int getCrystalPower() {
        if (isCrystalInslot())
            return inventory.get(1).getMaxDamage() - inventory.get(1).getItemDamage();
        return 0;
    }

    public boolean isSulfurInSlot() {
        return isSlotInUse(2) && inventory.get(2).getItem() == ItemRegistry.ITEMS_MISC && inventory.get(2).getItemDamage() == ItemMisc.EnumItemMisc.SULFUR.getID();
    }

    public boolean isSlotInUse(int slot) {
        return !inventory.get(slot).isEmpty();
    }

    public boolean isValidFocalItem() {
        return !inventory.get(0).isEmpty() && AnimatorRecipe.getRecipe(inventory.get(0)) != null;
    }

    public void sendGUIData(ContainerAnimator animator, IContainerListener listener) {
        listener.sendWindowProperty(animator, 0, fuelBurnProgress);
        listener.sendWindowProperty(animator, 1, lifeCrystalLife);
        listener.sendWindowProperty(animator, 2, itemAnimated ? 1 : 0);
        listener.sendWindowProperty(animator, 3, fuelConsumed);
        listener.sendWindowProperty(animator, 4, requiredFuelCount);
        listener.sendWindowProperty(animator, 5, requiredLifeCount);
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
                break;
            case 4:
            	requiredFuelCount = value;
            	break;
            case 5:
            	requiredLifeCount = value;
            	break;
        }
    }

    public boolean isRunning() {
        return this.running;
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
        if (!this.itemToAnimate.isEmpty()) {
            this.itemToAnimate.writeToNBT(toAnimateCompound);
        }
        nbt.setTag("toAnimate", toAnimateCompound);
        nbt.setBoolean("running", running);
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
        if (toAnimateStackCompound.hasKey("id", Constants.NBT.TAG_STRING))
            this.itemToAnimate = new ItemStack(toAnimateStackCompound);
        else
            this.itemToAnimate = ItemStack.EMPTY;
        running = nbt.getBoolean("running");
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
        if (slot == 1 && !stack.isEmpty() && stack.getItem() instanceof ItemLifeCrystal)
            return true;
        else if (slot == 2 && !stack.isEmpty() && stack.getItem().equals(ItemRegistry.ITEMS_MISC) && stack.getItemDamage() == ItemMisc.EnumItemMisc.SULFUR.getID())
            return true;
        else if (slot == 0 && inventory.get(0).isEmpty())
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