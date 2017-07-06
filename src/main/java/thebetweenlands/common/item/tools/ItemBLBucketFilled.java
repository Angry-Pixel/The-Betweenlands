package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		String key = this.getEmpty().getUnlocalizedName() + "." + (fluidStack == null ? "unknown" : fluidStack.getFluid().getUnlocalizedName(fluidStack));
		return key;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		FluidStack fluidStack = this.getFluid(stack);
		String fluidLocalizedStr = (fluidStack == null ? "unknown" : fluidStack.getLocalizedName()); //Localized fluid name
		String fullUnlocalizedFluidBucketKey = this.getEmpty().getUnlocalizedName() + "." + (fluidStack == null ? "unknown" : fluidStack.getUnlocalizedName()); //Unlocalized full bucket name
		//Try to find localization for this specific fluid bucket, if not found use a generic name with the fluid passed in as %s
		return net.minecraft.util.text.translation.I18n.canTranslate(fullUnlocalizedFluidBucketKey + ".name") ? 
				net.minecraft.util.text.translation.I18n.translateToLocal(fullUnlocalizedFluidBucketKey + ".name").trim() : 
					net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this.getEmpty().getUnlocalizedName() + ".full.name", fluidLocalizedStr);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid != FluidRegistry.WATER && fluid != FluidRegistry.LAVA && !fluid.getName().equals("milk") && !ItemSpecificBucket.hasSpecificBucket(this.getEmpty().getItem(), fluid)) {
				// add all fluids that the bucket can be filled  with
				FluidStack fs = new FluidStack(fluid, getCapacity());
				ItemStack stack = new ItemStack(this);
				UniversalBucket.getFilledBucket(this, fluid);
				if (this.getFluid(stack) != null && this.getFluid(stack).amount == fs.amount) { // probably broke something
					subItems.add(stack);
				}
			}
		}
	}
	
	 public int fill(ItemStack container, FluidStack resource, boolean doFill) {
	        // has to be exactly 1, must be handled from the caller
	        if (container.getCount() != 1) {
	            return 0;
	        }

	        // can only fill exact capacity
	        if (resource == null || resource.amount < getCapacity()) {
	            return 0;
	        }

	        // already contains fluid?
	        if (getFluid(container) != null) {
	            return 0;
	        }

	        // registered in the registry?
	        if (FluidRegistry.getBucketFluids().contains(resource.getFluid())) {
	            // fill the container
	            if (doFill) {
	                NBTTagCompound tag = container.getTagCompound();
	                if (tag == null)
	                {
	                    tag = new NBTTagCompound();
	                }
	                resource.writeToNBT(tag);
	                container.setTagCompound(tag);
	            }
	            return getCapacity();
	        }
	        else if (resource.getFluid() == FluidRegistry.WATER) {
	            if (doFill) {
	                container.deserializeNBT(new ItemStack(Items.WATER_BUCKET).serializeNBT());
	            }
	            return getCapacity();
	        }
	        else if (resource.getFluid() == FluidRegistry.LAVA) {
	            if (doFill) {
	                container.deserializeNBT(new ItemStack(Items.LAVA_BUCKET).serializeNBT());
	            }
	            return getCapacity();
	        }

	        return 0;
	}
}
