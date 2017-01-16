package thebetweenlands.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.network.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityDruidAltar extends TileEntityBasicInventory implements ITickable {
    public final static double FINAL_HEIGHT = 2.0D;
    // 14.25 seconds crafting time
    public static final int CRAFTING_TIME = 20 * 14 + 5;
    private static final float ROTATION_SPEED = 2.0F;
    public float rotation;
    public float prevRotation;
    public float renderYOffset;
    public float prevRenderYOffset;
    public int craftingProgress = 0;
    private boolean circleShouldRevert = true;

    public TileEntityDruidAltar() {
        super(5, "druid_altar");
    }

    @Override
    public void update() {
        if (!this.worldObj.isRemote && this.circleShouldRevert) {
            checkDruidCircleMeta(this.worldObj);
            this.circleShouldRevert = false;
        }
        if (this.worldObj.isRemote) {
            this.prevRotation = this.rotation;
            this.rotation += ROTATION_SPEED;
            if (this.rotation >= 360.0F) {
                this.rotation -= 360.0F;
                this.prevRotation -= 360.0F;
            }
            if (this.craftingProgress != 0) {
                this.craftingProgress++;
            }
            this.prevRenderYOffset = this.renderYOffset;
            this.renderYOffset = (float) ((double) this.craftingProgress / (double) TileEntityDruidAltar.CRAFTING_TIME * FINAL_HEIGHT + 1.0D);
        } else {
            if (this.craftingProgress != 0) {
                DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(this.inventory[1], this.inventory[2], this.inventory[3], this.inventory[4]);
                // Sync clients every second
                if (this.craftingProgress % 20 == 0 || this.craftingProgress == 1) {
                    sendCraftingProgressPacket();
                }
                this.craftingProgress++;
                if (recipe == null || this.inventory[0] != null) {
                    stopCraftingProcess();
                }
                if (this.craftingProgress >= CRAFTING_TIME && recipe != null) {
                    ItemStack stack = recipe.output;
                    stack.stackSize = 1;
                    setInventorySlotContents(1, null);
                    setInventorySlotContents(2, null);
                    setInventorySlotContents(3, null);
                    setInventorySlotContents(4, null);
                    setInventorySlotContents(0, stack);
                    stopCraftingProcess();
                    removeSpawner();
                }
            }
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    private void removeSpawner() {
        if (this.worldObj.getBlockState(this.pos.down()).getBlock() == BlockRegistry.MOB_SPAWNER) {
            this.worldObj.setBlockState(this.pos.down(), Blocks.GRASS.getDefaultState());
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(this.inventory[1], this.inventory[2], this.inventory[3], this.inventory[4]);
        if (!this.worldObj.isRemote && recipe != null && stack != null && this.inventory[0] == null && this.craftingProgress == 0) {
            startCraftingProcess();
        }
    }

    private void startCraftingProcess() {
        World world = this.worldObj;
        int dim = world.provider.getDimension();
        this.worldObj.setBlockState(this.pos, this.worldObj.getBlockState(this.pos).withProperty(BlockDruidAltar.ACTIVE, true), 3);
        this.craftingProgress = 1;
        // Packet to start sound
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -1), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
        // Sets client crafting progress to 1
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 1), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
        // Does the metadata stuff for the circle animated textures
        checkDruidCircleMeta(world);
    }

    private void stopCraftingProcess() {
        World world = this.worldObj;
        int dim = world.provider.getDimension();
        this.worldObj.setBlockState(this.pos, this.worldObj.getBlockState(this.pos).withProperty(BlockDruidAltar.ACTIVE, false), 3);
        this.craftingProgress = 0;
        // Packet to cancel sound
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, -2), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
        // Sets client crafting progress to 0
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this, 0), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
        // Does the metadata stuff for the circle animated textures
        checkDruidCircleMeta(world);
    }

    public void sendCraftingProgressPacket() {
        World world = this.worldObj;
        int dim = world.provider.getDimension();
        TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidAltarProgress(this), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
    }

    private void checkDruidCircleMeta(World world) {
        int baseRadius = 6;
        MutableBlockPos pos = new MutableBlockPos();
        int posX = this.pos.getX(), posY = this.pos.getY(), posZ = this.pos.getZ();
        for (int y = 0; y < 6; y++) {
            for (int x = -baseRadius; x <= baseRadius; x++) {
                for (int z = -baseRadius; z <= baseRadius; z++) {
                    int dSq = x * x + z * z;
                    if (Math.round(Math.sqrt(dSq)) == baseRadius) {
                        pos.setPos(posX + x, posY + y, posZ + z);
                        IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block instanceof BlockDruidStone) {
                            if ((this.craftingProgress == 0 || this.circleShouldRevert) && state.getValue(BlockDruidStone.ACTIVE)) {
                                world.setBlockState(pos, state.withProperty(BlockDruidStone.ACTIVE, false), 3);
                            } else if (this.craftingProgress == 1 && !state.getValue(BlockDruidStone.ACTIVE)) {
                                world.setBlockState(pos, state.withProperty(BlockDruidStone.ACTIVE, true), 3);
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
        return new AxisAlignedBB(this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1, this.pos.getX() + 2, this.pos.getY() + 3, this.pos.getZ() + 2);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("craftingProgress", this.craftingProgress);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.craftingProgress = nbt.getInteger("craftingProgress");
    }
}
