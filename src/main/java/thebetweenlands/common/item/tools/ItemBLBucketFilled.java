package thebetweenlands.common.item.tools;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemBLBucketFilled extends UniversalBucket {
	private static final class FluidBucketWrapperFixed extends FluidBucketWrapper {
		public FluidBucketWrapperFixed(ItemStack container) {
			super(container);
		}

		@Override
		@Nullable
		public FluidStack getFluid() {
			Item item = container.getItem();
			if (item == Items.WATER_BUCKET) {
				return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
			} else if (item == Items.LAVA_BUCKET) {
				return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
			} else if (item == Items.MILK_BUCKET) {
				return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
			} else if(item instanceof UniversalBucket) {
				UniversalBucket bucket = (UniversalBucket)item;
				return bucket.getFluid(container);
			} else {
				return null;
			}
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			if (fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA || fluid.getFluid().getName().equals("milk")) {
				return true;
			}
			return FluidRegistry.getBucketFluids().contains(fluid.getFluid());
		}

		@Override
		protected void setFluid(Fluid fluid) {
			if(fluid == null && this.container.getItem() instanceof UniversalBucket) {
				this.container.deserializeNBT(((UniversalBucket)this.container.getItem()).getEmpty().writeToNBT(new NBTTagCompound()));
			} else if (FluidRegistry.getBucketFluids().contains(fluid) && this.container.getItem() instanceof UniversalBucket) {
				ItemStack filledBucket = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
				this.container.deserializeNBT(filledBucket.serializeNBT());
			} else {
				super.setFluid(fluid);
			}
		}
	}

	public ItemBLBucketFilled(Item empty) {
		super(Fluid.BUCKET_VOLUME, new ItemStack(empty), false);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FluidBucketWrapperFixed(stack);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		FluidStack fluidStack = this.getFluid(stack);
		return super.getUnlocalizedName() + "." + (fluidStack == null ? "unknown" : fluidStack.getFluid().getUnlocalizedName(fluidStack));
	}
}
