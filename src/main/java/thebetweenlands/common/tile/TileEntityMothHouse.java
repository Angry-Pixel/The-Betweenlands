package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.misc.BlockLantern;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityMothHouse  extends TileEntityBasicInventory implements ITickable {
    public TileEntityMothHouse() {
        super(2, "container.bl.moth_house");
    }

    private int productionTime = 0;
    private int productionEfficiency = 0;
    private boolean isWorking = false;


    @Override
    public void update() {
        if(world.getTotalWorldTime() % 20 == 0) {
            if(isWorking) {
                double px = (double) pos.getX() + 0.5D;
                double py = (double) pos.getY() + 0.3D;
                double pz = (double) pos.getZ() + 0.5D;

                BLParticles.SILK_MOTH.spawn(world, px, py, pz);

                checkEfficiency();
            }
        }

        if (!this.world.isRemote) {
            ItemStack grubs = super.getStackInSlot(0);

            // don't work if no grubs are available or silk stack is full
            if(grubs == ItemStack.EMPTY || grubs.getCount() == 0 || super.getStackInSlot(1).getCount() == super.getStackInSlot(1).getMaxStackSize()) {
                isWorking = false;
                return;
            }

            productionTime--;

            isWorking = true;

            if(productionTime <= 0) {
                grubs.shrink(1);

                if(productionEfficiency != 0) {
                    int randomChance = world.rand.nextInt(4 - productionEfficiency);

                    if(randomChance == 0) {
                        ItemStack silkStack = super.getStackInSlot(1);

                        if(silkStack == ItemStack.EMPTY) {
                            silkStack = ItemMisc.EnumItemMisc.SILK_THREAD.create(1);
                            super.setInventorySlotContents(1, silkStack);
                        } else {
                            silkStack.grow(1);
                        }

                        markForUpdate();
                    }
                }

                productionTime = 20;
            }
        }
    }


    public int getSilkCount() {
        ItemStack silkStack = super.getStackInSlot(1);

        if(silkStack == ItemStack.EMPTY)
            return 0;

        float percent = (float)silkStack.getCount() / (float)silkStack.getMaxStackSize();
        return Math.round(percent * 3);
    }


    private void checkEfficiency() {
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
                        }

                        if (world.isRemote) {
                            double px = (double) mutablePos.getX() + 0.5D;
                            double py = (double) mutablePos.getY() + 0.3D;
                            double pz = (double) mutablePos.getZ() + 0.5D;

                            BLParticles.SILK_MOTH.spawn(world, px, py, pz);
                        }
                    }

                    // add other boosters?
                }
            }
        }

        productionEfficiency = lanternsNearby;
    }


    public int addGrubs(ItemStack stack) {
        int grubsAdded = Math.min(this.inventory.get(0).getMaxStackSize() - this.inventory.get(0).getCount(), stack.getCount());

        if(grubsAdded <= 0) {
            return 0;
        }

        ItemStack copy = stack.copy();
        copy.setCount(this.inventory.get(0).getCount() + grubsAdded);
        super.setInventorySlotContents(0, copy);

        markForUpdate();

        return grubsAdded;
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

        productionTime = nbt.getInteger("productionTime");
        productionEfficiency = nbt.getInteger("productionEfficiency");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("productionTime", productionTime);
        nbt.setInteger("productionEfficiency", productionEfficiency);

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
        return slot == 0 && stack.getItem() == ItemRegistry.SILK_GRUB;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == 1;
    }
}
