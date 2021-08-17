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
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.inventory.container.ContainerMortar;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

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
        super(4, "container.bl.mortar");
    }


    @Override
    public void update() {
        if (world.isRemote) {
            if (hasCrystal) {
                crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
                crystalRotation += this.crystalVelocity;
                if (crystalRotation >= 360.0F)
                    crystalRotation -= 360.0F;
                else if (this.crystalRotation <= 360.0F)
                    this.crystalRotation += 360.0F;
                if (Math.abs(crystalVelocity) <= 1.0F && this.getWorld().rand.nextInt(15) == 0)
                    crystalVelocity = this.world.rand.nextFloat() * 18.0F - 9.0F;
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
            
            if(this.progress > 0 && this.progress < 84) {
            	this.progress++;
            }
            
            return;
        }
        
        boolean validRecipe = false;
        boolean outputFull = outputIsFull();
        
        if (pestleInstalled()) {
            IPestleAndMortarRecipe recipe = PestleAndMortarRecipe.getRecipe(inventory.get(0), inventory.get(2), false);
            
            if(recipe != null) {
	            ItemStack output = recipe.getOutput(inventory.get(0), inventory.get(2).copy());
	            boolean replacesOutput = recipe.replacesOutput();
	            
	            outputFull &= !replacesOutput;
	            
	            if ((isCrystalInstalled() && getStackInSlot(3).getItemDamage() < getStackInSlot(3).getMaxDamage()) || manualGrinding) {
	                if (!output.isEmpty() && (replacesOutput || inventory.get(2).isEmpty() || (inventory.get(2).isItemEqual(output) && inventory.get(2).getCount() + output.getCount() <= output.getMaxStackSize()))) {
	                	validRecipe = true;
	                	
	                	progress++;
	                    
	                    if (progress == 1) {
	                        world.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundRegistry.GRIND, SoundCategory.BLOCKS, 1F, 1F);
	                    
	                        //Makes sure client knows that new grinding cycle has started
	                        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
	                    }
	                    
	                    if (progress == 64 || progress == 84) {
	                        world.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
	                        world.playSound(null, getPos().getX() + 0.5F, getPos().getY() + 0.5F, getPos().getZ() + 0.5F, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.3F, 1F);
	                    }
	                    
	                    if (!inventory.get(1).isEmpty())
	                    	NBTHelper.getStackNBTSafe(getStackInSlot(1)).setBoolean("active", true);
	                    
	                    if (progress > 84) {
	                        if (!inventory.get(0).isEmpty())
	                            if (inventory.get(0).getCount() - 1 <= 0)
	                                inventory.set(0, ItemStack.EMPTY);
	                            else
	                                inventory.get(0).shrink(1);
	                        
	                        if (replacesOutput || inventory.get(2).isEmpty())
	                            inventory.set(2, output.copy());
	                        else if (inventory.get(2).isItemEqual(output))
	                            inventory.get(2).grow(output.getCount());
	                        
	                        inventory.get(1).setItemDamage(inventory.get(1).getItemDamage() + 1);
	                        
	                        if (!manualGrinding)
	                            inventory.get(3).setItemDamage(inventory.get(3).getItemDamage() + 1);
	                        
	                        progress = 0;
	                        manualGrinding = false;
	                        
	                        if (inventory.get(1).getItemDamage() >= inventory.get(1).getMaxDamage()) {
	                            inventory.set(1, ItemStack.EMPTY);
	                            hasPestle = false;
	                        }
	                        
	                        if (!inventory.get(1).isEmpty())
	                            NBTHelper.getStackNBTSafe(getStackInSlot(1)).setBoolean("active", false);
	                        
	                        markDirty();
	                    }
	                }
	            }
            }
        }
        if (progress > 0) {
        	markDirty();
        }
        if (pestleInstalled()) {
        	if(!hasPestle) {
        		hasPestle = true;
        		world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
        	}
        } else {
        	if(hasPestle) {
        		hasPestle = false;
        		world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
        	}
        }
        if (!validRecipe || getStackInSlot(0).isEmpty() || getStackInSlot(1).isEmpty() || outputFull) {
            if (!inventory.get(1).isEmpty())
                NBTHelper.getStackNBTSafe(getStackInSlot(1)).setBoolean("active", false);
            
            if(progress > 0) {
            	progress = 0;
            	world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
            	markDirty();
            }
        }
        if (getStackInSlot(3).isEmpty() && progress > 0 && !manualGrinding) {
            if (!inventory.get(1).isEmpty())
                NBTHelper.getStackNBTSafe(getStackInSlot(1)).setBoolean("active", false);
            progress = 0;
            markDirty();
            world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
        }
        if (isCrystalInstalled()) {
        	if(!hasCrystal) {
        		hasCrystal = true;
        		world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
        	}
        } else {
        	if(hasCrystal) {
        		hasCrystal = false;
        		world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 2);
        	}
        }
    }

    public boolean pestleInstalled() {
        return !getStackInSlot(1).isEmpty() && getStackInSlot(1).getItem() == ItemRegistry.PESTLE;
    }

    public boolean isCrystalInstalled() {
        return !getStackInSlot(3).isEmpty() && getStackInSlot(3).getItem() instanceof ItemLifeCrystal && getStackInSlot(3).getItemDamage() <= getStackInSlot(3).getMaxDamage();
    }

    private boolean outputIsFull() {
        return !getStackInSlot(2).isEmpty() && getStackInSlot(2).getCount() >= getInventoryStackLimit();
    }

    public void sendGUIData(ContainerMortar mortar, IContainerListener containerListener) {
        containerListener.sendWindowProperty(mortar, 0, progress);
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
        switch(side) {
            case DOWN:
                return new int[]{2};
            case UP:
                return new int[]{0};
            case NORTH:
            case EAST:
            case SOUTH:
            case WEST:
                return new int[]{0, 1, 3};
        }
        return new int[]{};
    }


    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return slot == 1 && itemstack.getItem() == ItemRegistry.PESTLE || slot == 3 && itemstack.getItem() instanceof ItemLifeCrystal || slot != 1 && itemstack.getItem() != ItemRegistry.PESTLE && slot != 3 && itemstack.getItem() != ItemRegistry.LIFE_CRYSTAL;
    }

}