package thebetweenlands.common.item.tools;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.registries.ItemRegistry;

/**
 * A bucket that can only contain the specified fluid. When a fluid is picked up
 * with the empty bucket it is automatically converted to this item.
 */
public class ItemSpecificBucket extends ItemBLBucket {
    private static final Map<Fluid, ItemSpecificBucket> BUCKETS = new HashMap<>();
    protected final FluidStack fluidStack;

    public ItemSpecificBucket(Fluid fluid) {
        super(ItemRegistry.BL_BUCKET);
        this.setMaxStackSize(1);
        this.fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
        MinecraftForge.EVENT_BUS.register(this);
        BUCKETS.put(fluid, this);
    }

    public static boolean hasSpecificBucket(Fluid fluid) {
        return BUCKETS.containsKey(fluid);
    }
    
    @Nullable
    public static ItemSpecificBucket getSpecificBucket(Fluid fluid) {
    	return BUCKETS.get(fluid);
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return this.fluidStack.copy();
    }

    @Override
    public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < 2; i++)
                subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    @SubscribeEvent
    public void onFillBucket(FillBucketEvent event) {
        // not for us to handle
        ItemStack emptyBucket = event.getEmptyBucket();
        if (emptyBucket.isEmpty() || !emptyBucket.isItemEqualIgnoreDurability(getEmpty(emptyBucket))) {
            return;
        }

        // needs to target a block
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }


        World world = event.getWorld();
        BlockPos pos = target.getBlockPos();

        ItemStack singleBucket = emptyBucket.copy();
        singleBucket.setCount(1);

        IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, pos, target.sideHit);
        if (targetFluidHandler != null) {
            FluidStack fluidStack = targetFluidHandler.drain(this.fluidStack, false);
            if (fluidStack != null && fluidStack.amount == this.fluidStack.amount && fluidStack.getFluid() == this.fluidStack.getFluid()) {
                fluidStack = targetFluidHandler.drain(this.fluidStack, true);
                world.playSound(null, pos, fluidStack.getFluid().getFillSound(fluidStack), SoundCategory.BLOCKS, 1.0F, 1.0F);

                //Replace with specific bucket
                event.setResult(Result.ALLOW);
                event.setFilledBucket(new ItemStack(this, 1, emptyBucket.getMetadata()));
                event.setCanceled(false);
            }
        }
    }
}
