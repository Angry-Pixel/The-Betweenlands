package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.inventory.container.ContainerMortar;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityMortar extends TileEntityBasicInventory implements ITickable {

    public int progress;
    public boolean hasPestle;
    public boolean hasCrystal;
    public boolean manualGrinding = false;
    public float crystalVelocity;
    public float crystalRotation;
    public int itemBob;
    public boolean countUp = true;

    public TileEntityMortar() {
        super(4, "pestleAndMortar");
    }


    @Override
    public void update() {
        if (worldObj.isRemote) {
            if (hasCrystal) {
                crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
                crystalRotation += this.crystalVelocity;
                if (crystalRotation >= 360.0F)
                    crystalRotation -= 360.0F;
                else if (this.crystalRotation <= 360.0F)
                    this.crystalRotation += 360.0F;
                if (Math.abs(crystalVelocity) <= 1.0F && this.getWorld().rand.nextInt(15) == 0)
                    crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
                if (countUp && itemBob <= 20) {
                    itemBob++;
                    if (itemBob == 20)
                        countUp = false;
                }
                if (!countUp && itemBob >= 0) {
                    itemBob--;
                    if (itemBob == 0)
                        countUp = true;
                }
            }
            return;
        }
        ItemStack output = PestleAndMortarRecipe.getResult(inventory[0]);
        if (pestleInstalled() && !outputIsFull()) {

            if (isCrystalInstalled() && getStackInSlot(3).getItemDamage() < getStackInSlot(3).getMaxDamage() || manualGrinding) {
                if (output != null && inventory[2] == null || output != null && inventory[2] != null && inventory[2].isItemEqual(output)) {
                    progress++;
                    if (progress == 1)
                        worldObj.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundRegistry.GRIND, SoundCategory.BLOCKS, 1F, 1F);
                    if (progress == 64 || progress == 84) {
                        worldObj.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
                        worldObj.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
                    }
                    if (inventory[1] != null && !getStackInSlot(1).getTagCompound().getBoolean("active"))
                        getStackInSlot(1).getTagCompound().setBoolean("active", true);
                    if (progress > 84) {
                        if (inventory[0] != null)
                            if (--inventory[0].stackSize <= 0)
                                inventory[0] = null;
                        if (inventory[2] == null)
                            inventory[2] = output.copy();
                        else if (inventory[2].isItemEqual(output))
                            inventory[2].stackSize += output.stackSize;
                        inventory[1].setItemDamage(inventory[1].getItemDamage() + 1);
                        if (!manualGrinding)
                            inventory[3].setItemDamage(inventory[3].getItemDamage() + 1);
                        progress = 0;
                        manualGrinding = false;
                        if (inventory[1].getItemDamage() >= inventory[1].getMaxDamage()) {
                            inventory[1] = null;
                            hasPestle = false;
                        }
                        if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
                            getStackInSlot(1).getTagCompound().setBoolean("active", false);
                        markDirty();
                    }
                }
            }
        }
        if (progress > 0)
            markDirty();
        if (pestleInstalled()) {
            hasPestle = true;
            worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
        } else {
            hasPestle = false;
            worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
        }
        if (getStackInSlot(0) == null || getStackInSlot(1) == null || outputIsFull()) {
            if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
                getStackInSlot(1).getTagCompound().setBoolean("active", false);
            progress = 0;
            markDirty();
        }
        if (getStackInSlot(3) == null && progress > 0 && !manualGrinding) {
            if (inventory[1] != null && getStackInSlot(1).getTagCompound().getBoolean("active"))
                getStackInSlot(1).getTagCompound().setBoolean("active", false);
            progress = 0;
            markDirty();
        }
        if (isCrystalInstalled()) {
            hasCrystal = true;
            worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
        } else {
            hasCrystal = false;
            worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
        }
    }

    public boolean pestleInstalled() {
        return getStackInSlot(1) != null && getStackInSlot(1).getItem() == ItemRegistry.PESTLE;
    }

    public boolean isCrystalInstalled() {
        return getStackInSlot(3) != null && getStackInSlot(3).getItem() == ItemRegistry.LIFE_CRYSTAL && getStackInSlot(3).getItemDamage() <= getStackInSlot(3).getMaxDamage();
    }

    private boolean outputIsFull() {
        return getStackInSlot(2) != null && getStackInSlot(2).stackSize >= getInventoryStackLimit();
    }

    public void sendGUIData(ContainerMortar mortar, IContainerListener containerListener) {
        containerListener.sendProgressBarUpdate(mortar, 0, progress);
    }

    public void getGUIData(int id, int value) {
        switch (id) {
            case 0:
                progress = value;
                break;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("progress", progress);
        nbt.setBoolean("hasPestle", hasPestle);
        nbt.setBoolean("hasCrystal", hasCrystal);
        nbt.setBoolean("manualGrinding", manualGrinding);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        progress = nbt.getInteger("progress");
        hasPestle = nbt.getBoolean("hasPestle");
        hasCrystal = nbt.getBoolean("hasCrystal");
        manualGrinding = nbt.getBoolean("manualGrinding");
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("progress", progress);
        nbt.setBoolean("hasPestle", hasPestle);
        nbt.setBoolean("hasCrystal", hasCrystal);
        nbt.setBoolean("manualGrinding", manualGrinding);
        this.writeInventoryNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        progress = packet.getNbtCompound().getInteger("progress");
        hasPestle = packet.getNbtCompound().getBoolean("hasPestle");
        hasCrystal = packet.getNbtCompound().getBoolean("hasCrystal");
        manualGrinding = packet.getNbtCompound().getBoolean("manualGrinding");
        this.readInventoryNBT(packet.getNbtCompound());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN)
            return new int[]{2};
        if (side == EnumFacing.UP)
            return new int[]{0};
        return new int[]{};
    }

}