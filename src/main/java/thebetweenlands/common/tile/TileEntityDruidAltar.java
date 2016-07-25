package thebetweenlands.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityDruidAltar extends TileEntityBasicInventory implements ITickable {
    @SideOnly(Side.CLIENT)
    public final static double FINAL_HEIGHT = 2.0D;
    //14.25 seconds crafting time
    public static final int CRAFTING_TIME = 20 * 14 + 5;
    @SideOnly(Side.CLIENT)
    private static final float ROTATION_SPEED = 2.0F;
    @SideOnly(Side.CLIENT)
    public float rotation;
    @SideOnly(Side.CLIENT)
    public float prevRotation;
    @SideOnly(Side.CLIENT)
    public float renderYOffset;
    @SideOnly(Side.CLIENT)
    public float prevRenderYOffset;
    public int craftingProgress = 0;
    private boolean circleShouldRevert = true;

    public TileEntityDruidAltar() {
        super(5, "druid_altar");
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && circleShouldRevert) {
            checkDruidCircleMeta(worldObj);
            circleShouldRevert = false;
        }

        if (worldObj.isRemote) {
            prevRotation = rotation;
            rotation += ROTATION_SPEED;
            if (rotation >= 360.0F) {
                rotation -= 360.0F;
                prevRotation -= 360.0F;
            }
            if (craftingProgress != 0) {
                ++craftingProgress;
            }
            prevRenderYOffset = renderYOffset;
            renderYOffset = (float) ((double) this.craftingProgress / (double) TileEntityDruidAltar.CRAFTING_TIME * FINAL_HEIGHT + 1.0D);
        } else {
            if (craftingProgress != 0) {
                DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(inventory[1], inventory[2], inventory[3], inventory[4]);
                //Sync clients every second
                if (this.craftingProgress % 20 == 0 || this.craftingProgress == 1) {
                    sendCraftingProgressPacket();
                }
                ++craftingProgress;

                //updateDamageValues();
                if (recipe == null || inventory[0] != null) {
                    if (this.craftingProgress != 0) {
                        this.stopCraftingProcess();
                    }
                }

                if (craftingProgress >= CRAFTING_TIME && recipe != null) {
                    ItemStack stack = recipe.output;
                    stack.stackSize = 1;
                    setInventorySlotContents(1, null);
                    setInventorySlotContents(2, null);
                    setInventorySlotContents(3, null);
                    setInventorySlotContents(4, null);
                    setInventorySlotContents(0, stack);
                    this.stopCraftingProcess();

                    this.removeSpawner();
                }
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    private void removeSpawner() {
        if (worldObj.getBlockState(pos.down()) == BlockRegistry.DRUID_SPAWNER)
            worldObj.setBlockState(pos.down(), Blocks.GRASS.getDefaultState());
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack is) {
        inventory[slot] = is;
        if (is != null && is.stackSize > getInventoryStackLimit()) {
            is.stackSize = getInventoryStackLimit();
        }
        //updateDamageValues();
        DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(inventory[1], inventory[2], inventory[3], inventory[4]);
        if (recipe != null && is != null) {
            if (inventory[0] == null) {
                if (!worldObj.isRemote) {
                    if (craftingProgress == 0) {
                        this.startCraftingProcess();
                    }
                }
            }
        }
    }

    private void startCraftingProcess() {
        World world = worldObj;
        int dim = 0;
        if (world instanceof WorldServer) {
            dim = ((WorldServer) world).provider.getDimension();
        }


        worldObj.setBlockState(pos, worldObj.getBlockState(pos).getBlock().getStateFromMeta(1), 2);
        ((TileEntityDruidAltar) worldObj.getTileEntity(pos)).inventory = inventory;
        ((TileEntityDruidAltar) worldObj.getTileEntity(pos)).craftingProgress = 1;
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), world.getBlockState(pos), 2);
        //Packet to start sound
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -1), new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64D));
        //Sets client crafting progress to 1
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 1), new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64D));
        //Does the metadata stuff for the circle animated textures
        checkDruidCircleMeta(world);
    }

    private void stopCraftingProcess() {
        World world = worldObj;
        int dim = 0;
        if (world instanceof WorldServer) {
            dim = ((WorldServer) world).provider.getDimension();
        }
        worldObj.setBlockState(pos, worldObj.getBlockState(pos).getBlock().getStateFromMeta(0), 2);
        ((TileEntityDruidAltar) worldObj.getTileEntity(pos)).inventory = inventory;
        ((TileEntityDruidAltar) worldObj.getTileEntity(pos)).craftingProgress = 0;
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), world.getBlockState(pos), 2);
        //Packet to cancel sound
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -2), new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64D));
        //Sets client crafting progress to 0
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 0), new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64D));
        //Does the metadata stuff for the circle animated textures
        checkDruidCircleMeta(world);
    }

    public void sendCraftingProgressPacket() {
        World world = worldObj;
        int dim = 0;
        if (world instanceof WorldServer) {
            dim = ((WorldServer) world).provider.getDimension();
        }
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this), new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 64D));
    }

    private void checkDruidCircleMeta(World world) {
        int baseRadius = 6;
        for (int y = 0; y < 6; y++) {
            for (int x = baseRadius * -1; x <= baseRadius; ++x) {
                for (int z = baseRadius * -1; z <= baseRadius; ++z) {
                    double dSq = x * x + z * z;
                    if (Math.round(Math.sqrt(dSq)) == baseRadius) {
                        BlockPos pos1 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        Block block = world.getBlockState(pos1).getBlock();
                        if (block == BlockRegistry.DRUID_STONE_1
                                || block == BlockRegistry.DRUID_STONE_2
                                || block == BlockRegistry.DRUID_STONE_3
                                || block == BlockRegistry.DRUID_STONE_4
                                || block == BlockRegistry.DRUID_STONE_5) {
                            IBlockState state = world.getBlockState(pos1);
                            int meta = state.getBlock().getMetaFromState(state);
                            if (craftingProgress == 0 && meta >= 4 || circleShouldRevert && meta >= 4) {
                                world.setBlockState(pos1, state.getBlock().getStateFromMeta(meta - 4), 3);
                            } else if (craftingProgress == 1 && meta < 4) {
                                world.setBlockState(pos1, state.getBlock().getStateFromMeta(meta + 4), 3);
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("craftingProgress", craftingProgress);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        craftingProgress = nbt.getInteger("craftingProgress");
    }


}
