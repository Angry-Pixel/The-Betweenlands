package thebetweenlands.common.tile;

import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.misc.BlockLantern;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityMothHouse  extends TileEntityBasicInventory implements ITickable {
	public static final int SLOT_GRUBS = 0;
	public static final int SLOT_SILK = 1;
	
	protected static final int MAX_GRUBS = 6;
	protected static final int MAX_SILK_PER_GRUB = 3;
	protected static final int BASE_TICKS_PER_SILK = 160;
	
	public TileEntityMothHouse() {
        super("container.bl.moth_house", NonNullList.withSize(2, ItemStack.EMPTY), (te, inv) -> new ItemStackHandler(inv) {
    		@Override
    		public void setSize(int size) {
    			if(size != inv.size()) {
    				throw new UnsupportedOperationException("Can't resize this inventory");
    			}
    		}

    		@Override
    		protected void onContentsChanged(int slot) {
    			// Don't mark dirty while loading chunk!
    			if(te.hasWorld()) {
    				te.markDirty();
    			}
    		}
    		
    		@Override
    		public void setStackInSlot(int slot, ItemStack stack) {
    			if(slot == SLOT_SILK) {
    				ItemStack prevStack = this.getStackInSlot(slot).copy();
    				
    				super.setStackInSlot(slot, stack);
    				
    				ItemStack newStack = this.getStackInSlot(slot);
    				
    				if(newStack.getCount() < prevStack.getCount()) {
    					((TileEntityMothHouse) te).onSilkRemoved(prevStack.getCount() - newStack.getCount());
    				}
    			} else {
    				super.setStackInSlot(slot, stack);
    			}
    		}
    		
    		@Override
    		public ItemStack extractItem(int slot, int amount, boolean simulate) {
    			if(slot == SLOT_SILK && !simulate) {
    				ItemStack extracted = super.extractItem(slot, amount, false);
    				
    				if(extracted.getCount() > 0) {
    					((TileEntityMothHouse) te).onSilkRemoved(extracted.getCount());
    				}
    				
    				return extracted;
    			} else {
    				return super.extractItem(slot, amount, simulate);
    			}
    		}
    		
    		@Override
    		public int getSlotLimit(int slot) {
    			if(slot == SLOT_GRUBS) {
    				return MAX_GRUBS;
    			}
    			return super.getSlotLimit(slot);
    		}
    	});
    }

    private int productionTime = 0;
    private float productionEfficiency = 0;
    
    private boolean isWorking = false;
    
	private EntityPlayer placer;
	private UUID placerUUID;
	
	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		this.updatePlacerFromUUID();
	}

	private boolean updatePlacerFromUUID() {
		if(placerUUID != null) {
			EntityPlayer player = this.world.getPlayerEntityByUUID(placerUUID);
			if(player != null && player != getPlacer()) {
				setPlacer(player);
				return true;
			}
		}
		return false;
	}

    @Override
    public void update() {
        if(world.getTotalWorldTime() % 20 == 0) {
            if(isWorking) {
            	if(this.world.isRemote && this.world.rand.nextInt(3) == 0) {
            		double px = (double) pos.getX() + 0.5D;
                    double py = (double) pos.getY() + 0.3D;
                    double pz = (double) pos.getZ() + 0.5D;

                    spawnSilkMothParticle(px, py, pz);
            	}

                updateEfficiency();
            }
        }

        if (!this.world.isRemote) {
			// because the player is always null unless the world is loaded but block NBT is loaded before grrrrr
			if(placerUUID != null && getPlacer() == null && world.getTotalWorldTime() % 20 == 0) {
				if(updatePlacerFromUUID()) {
					markForUpdate();
				}
			}

            ItemStack grubs = this.getStackInSlot(SLOT_GRUBS);

            boolean wasWorking = this.isWorking;
            
            // don't work if no grubs are available or silk stack is full
            if(grubs == ItemStack.EMPTY || grubs.getCount() == 0 || this.getStackInSlot(SLOT_SILK).getCount() == this.getStackInSlot(SLOT_SILK).getMaxStackSize() || this.getStackInSlot(SLOT_SILK).getCount() >= this.getStackInSlot(SLOT_GRUBS).getCount() * MAX_SILK_PER_GRUB) {
                isWorking = false;
            } else {
            	if(!isWorking) {
            		this.updateEfficiency();
            		
            		this.resetProductionTime();
            	}
            	
            	productionTime--;
            	
                isWorking = true;

                if(productionTime <= 0) {
                	ItemStack silkStack = this.getStackInSlot(SLOT_SILK);

                    if(silkStack == ItemStack.EMPTY) {
                        silkStack = ItemMisc.EnumItemMisc.SILK_THREAD.create(1);
                        super.setInventorySlotContents(SLOT_SILK, silkStack);
                    } else if(ItemMisc.EnumItemMisc.SILK_THREAD.isItemOf(silkStack)) {
                    	silkStack.grow(1);
                    }

                    markForUpdate();

                    this.resetProductionTime();
                    
                    this.markDirty();
                }
            }
            
            if(wasWorking != this.isWorking) {
            	this.markForUpdate();
            	
            	this.markDirty();
            }
        }
    }

    protected void resetProductionTime() {
    	float efficiencyMultiplier = 1.0f / (0.1f + this.productionEfficiency * 0.9f);
        
    	// Triple production time with each stage
    	this.productionTime = (int)(BASE_TICKS_PER_SILK * Math.pow(3, this.getSilkProductionStage()) * efficiencyMultiplier);
    }
    
    public int getSilkProductionStage() {
    	// Silk stage starts at 0 and increments by 1 whenever all grubs have produced one more silk
    	ItemStack grubStack = this.getStackInSlot(SLOT_GRUBS);
    	ItemStack silkStack = this.getStackInSlot(SLOT_SILK);
    	return grubStack.getCount() > 0 ? silkStack.getCount() / grubStack.getCount() : 0;
    }
    
    public boolean isSilkProductionFinished() {
    	ItemStack grubStack = this.getStackInSlot(SLOT_GRUBS);
    	ItemStack silkStack = this.getStackInSlot(SLOT_SILK);
    	return silkStack.getCount() >= Math.min(MAX_GRUBS, grubStack.getCount()) * MAX_SILK_PER_GRUB;
    }
    
    public int getSilkRenderStage() {
    	ItemStack silkStack = this.getStackInSlot(SLOT_SILK);
    	return MathHelper.ceil(Math.min(1.0f, silkStack.getCount() / (float)Math.min(this.inventoryHandler.getSlotLimit(SLOT_SILK), MAX_GRUBS * MAX_SILK_PER_GRUB)) * 3);
    }

    private void updateEfficiency() {
        AxisAlignedBB axisalignedbb = extendRangeBox();
        int minX = MathHelper.floor(axisalignedbb.minX);
        int maxX = MathHelper.floor(axisalignedbb.maxX);
        int minY = MathHelper.floor(axisalignedbb.minY);
        int maxY = MathHelper.floor(axisalignedbb.maxY);
        int minZ = MathHelper.floor(axisalignedbb.minZ);
        int maxZ = MathHelper.floor(axisalignedbb.maxZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int lanternsNearby = 0;
        int maxLanterns = 3;

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    IBlockState state = getWorld().getBlockState(mutablePos.setPos(x, y, z));
                    if (lanternsNearby < maxLanterns && state.getBlock() instanceof BlockLantern) {
                        if (!world.isRemote) {
                            lanternsNearby++;
                            
                            if(this.placer instanceof EntityPlayerMP && lanternsNearby == maxLanterns) {
                            	AdvancementCriterionRegistry.MOTH_HOUSE_MAXED.trigger((EntityPlayerMP) placer);
                            }
                        } else if(this.world.rand.nextInt(16) == 0) {
                            double px = (double) mutablePos.getX() + 0.5D;
                            double py = (double) mutablePos.getY() + 0.7D;
                            double pz = (double) mutablePos.getZ() + 0.5D;

                            spawnSilkMothParticle(px, py, pz);
                        }
                    }

                    // add other boosters?
                }
            }
        }

        productionEfficiency = lanternsNearby / 3.0f;
    }

    @SideOnly(Side.CLIENT)
    protected void spawnSilkMothParticle(double x, double y, double z) {
    	BLParticles.SILK_MOTH.spawn(this.world, x, y, z);
    }

    public int addGrubs(ItemStack stack) {
    	int count = stack.getCount();
    	
        int grubsAdded = count - this.inventoryHandler.insertItem(SLOT_GRUBS, stack.copy(), false).getCount();

    	this.markDirty();
    	
        return grubsAdded;
    }

    protected void onSilkRemoved(int count) {
    	int grubsToRemove = MathHelper.ceil(count / (float)MAX_SILK_PER_GRUB);
    	
    	ItemStack grubsStack = this.getStackInSlot(SLOT_GRUBS);
    	
    	grubsStack.shrink(grubsToRemove);
    	
    	this.markDirty();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        markForUpdate();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

		if (nbt.hasKey("OwnerUUID", 8)) {
			placerUUID = nbt.getUniqueId("OwnerUUID");
		}

        productionTime = nbt.getInteger("productionTime");
        productionEfficiency = nbt.getFloat("productionEfficiency");
        isWorking = nbt.getBoolean("isWorking");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

		EntityPlayer placer = getPlacer();
		if (placer != null) {
			nbt.setUniqueId("OwnerUUID", placer.getUniqueID());
		}

        nbt.setInteger("productionTime", productionTime);
        nbt.setFloat("productionEfficiency", productionEfficiency);
        nbt.setBoolean("isWorking", this.isWorking);

        return nbt;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    public AxisAlignedBB extendRangeBox() {
        return  new AxisAlignedBB(pos).grow(3D, 3D, 3D);
    }

    public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 2);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return super.canInsertItem(slot, stack, side) && slot == SLOT_GRUBS && stack.getItem() == ItemRegistry.SILK_GRUB;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == SLOT_SILK;
    }

	public void setPlacer(EntityPlayer player) {
		this.placer = player;
	}

	private EntityPlayer getPlacer() {
		return this.placer;
	}
}
