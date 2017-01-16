package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A bucket that can only contain the specified fluid. When a fluid is picked up
 * with the empty bucket it is automatically converted to this item.
 */
public class ItemSpecificBucket extends ItemBLBucketFilled {
	protected final FluidStack fluidStack;

	private static final Map<Item, List<Fluid>> BUCKETS = new HashMap<>();

	public ItemSpecificBucket(Item empty, Fluid fluid) {
		super(empty);
		this.fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
		MinecraftForge.EVENT_BUS.register(this);

		List<Fluid> lst = BUCKETS.get(empty);
		if(lst == null) {
			BUCKETS.put(empty, lst = new ArrayList<>());
		}
		lst.add(fluid);
	}

	public static boolean hasSpecificBucket(Item empty, Fluid fluid) {
		List<Fluid> lst = BUCKETS.get(empty);
		if(lst != null) {
			return lst.contains(fluid);
		}
		return false;
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		return this.fluidStack.copy();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(this));
	}

	@Override
	@SubscribeEvent
	public void onFillBucket(FillBucketEvent event) {
		// not for us to handle
		ItemStack emptyBucket = event.getEmptyBucket();
		if (emptyBucket == null ||
				!emptyBucket.isItemEqual(getEmpty()) ||
				(isNbtSensitive() && ItemStack.areItemStackTagsEqual(emptyBucket, getEmpty()))) {
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
		singleBucket.stackSize = 1;

		IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, pos, target.sideHit);
		if (targetFluidHandler != null) {
			FluidStack fluidStack = targetFluidHandler.drain(this.fluidStack, false);
			if(fluidStack != null && fluidStack.amount == this.fluidStack.amount && fluidStack.getFluid() == this.fluidStack.getFluid()) {
				fluidStack = targetFluidHandler.drain(this.fluidStack, true);

				//Replace with specific bucket
				event.setResult(Result.ALLOW);
				event.setFilledBucket(new ItemStack(this));
				event.setCanceled(false);
			}
		}
	}
}
