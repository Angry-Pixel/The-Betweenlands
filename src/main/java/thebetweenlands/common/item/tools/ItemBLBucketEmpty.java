package thebetweenlands.common.item.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityInfuser;

public abstract class ItemBLBucketEmpty extends Item {
    public ItemBLBucketEmpty() {
        this.setCreativeTab(BLCreativeTabs.GEARS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStackIn, raytraceresult);
        if (ret != null) {
        	return ret;
        }

        if (raytraceresult == null) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        } else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        } else {
            BlockPos blockpos = raytraceresult.getBlockPos();

            if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
            } else {
                if (!playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemStackIn)) {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                } else {
                    IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    Block block = iblockstate.getBlock();

                    //TODO: Move this to infuser
                    if (!worldIn.isRemote && block == BlockRegistry.INFUSER && playerIn.isSneaking()) {
                        TileEntityInfuser tile = (TileEntityInfuser) worldIn.getTileEntity(blockpos);
                        if (tile != null && tile.hasInfusion() && tile.getWaterAmount() >= Fluid.BUCKET_VOLUME) {
                            ItemStack infusionBucket = new ItemStack(ItemRegistry.WEEDWOOD_BUCKET_INFUSION);
                            NBTTagCompound nbtCompound = new NBTTagCompound();
                            infusionBucket.setTagCompound(nbtCompound);
                            nbtCompound.setString("infused", "Infused");
                            NBTTagList nbtList = new NBTTagList();
                            for (int i = 0; i < tile.getSizeInventory() - 1; i++) {
                                ItemStack stackInSlot = tile.getStackInSlot(i);
                                if (stackInSlot != null) {
                                    nbtList.appendTag(stackInSlot.writeToNBT(new NBTTagCompound()));
                                }
                            }
                            nbtCompound.setTag("ingredients", nbtList);
                            nbtCompound.setInteger("infusionTime", tile.getInfusionTime());
                            tile.extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
                            if (itemStackIn.stackSize == 1)
                                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, infusionBucket);
                            else {
                                playerIn.dropItem(infusionBucket.copy(), false);
                                itemStackIn.stackSize--;
                                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
                            }
                        }
                    }

                    if (block instanceof IFluidBlock) {
                        Fluid fluid = ((IFluidBlock) block).getFluid();
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 11);
                        playerIn.addStat(StatList.getObjectUseStats(this));
                        playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, this.fillBucket(itemStackIn, playerIn, fluid));
                    } else {
                        TileEntity te = worldIn.getTileEntity(blockpos);
                        if (te != null && te instanceof IFluidHandler) {
                            IFluidHandler handler = (IFluidHandler) te;
                            FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME, false);
                            if (drained != null && drained.amount == Fluid.BUCKET_VOLUME) {
                                drained = handler.drain(Fluid.BUCKET_VOLUME, true);
                                playerIn.addStat(StatList.getObjectUseStats(this));
                                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, this.fillBucket(itemStackIn, playerIn, drained.getFluid()));
                            }
                        }
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                    }
                }
            }
        }
    }

    private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Fluid fluid) {
        ItemStack fullBucket = UniversalBucket.getFilledBucket(this.getFilledBucket(), fluid);
        if (player.capabilities.isCreativeMode) {
            return emptyBuckets;
        } else if (--emptyBuckets.stackSize <= 0) {
            return fullBucket.copy();
        } else {
            if (!player.inventory.addItemStackToInventory(fullBucket.copy())) {
                player.dropItem(fullBucket.copy(), false);
            }
            return emptyBuckets;
        }
    }

    protected abstract UniversalBucket getFilledBucket();


}
