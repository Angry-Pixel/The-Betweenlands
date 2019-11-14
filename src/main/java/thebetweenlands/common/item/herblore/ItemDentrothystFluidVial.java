package thebetweenlands.common.item.herblore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemDentrothystFluidVial extends UniversalBucket implements ItemRegistry.IMultipleItemModelDefinition {
	private static final class DentrothystFluidVialFluidHandler extends FluidHandlerItemStackSimple {
		public DentrothystFluidVialFluidHandler(final ItemStack container, final int capacity) {
			super(container, capacity);
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			return ((ItemDentrothystFluidVial) container.getItem()).canFillWith(container, fluid);
		}

		@Nullable
		@Override
		public FluidStack getFluid() {
			return container.getItem() != ItemRegistry.DENTROTHYST_VIAL ? ((ItemDentrothystFluidVial) container.getItem()).getFluid(container): null;
		}

		@Override
		protected void setContainerToEmpty() {
			container = ((ItemDentrothystFluidVial) container.getItem()).getEmpty(container).copy();
		}
	}
	
	public ItemDentrothystFluidVial() {
		super(Fluid.BUCKET_VOLUME, ItemStack.EMPTY, true);
		this.setHasSubtypes(true);
		this.setMaxStackSize(16);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.HERBLORE);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setTag(FluidHandlerItemStackSimple.FLUID_NBT_KEY, new NBTTagCompound());
		stack.setTagCompound(nbt);
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		models.put(0, new ResourceLocation(getRegistryName().toString() + "_green"));
		models.put(1, new ResourceLocation(getRegistryName().toString() + "_orange"));
		return models;
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		FluidStack fluidStack = getFluid(stack);
		if (fluidStack != null)
			return 1;
		return super.getItemStackLimit(stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		return new DentrothystFluidVialFluidHandler(stack, getCapacity());
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() >= 2)
			return getTranslationKey() + ".unknown";
		return getTranslationKey() + (stack.getMetadata() == 0 ? "_green": "_orange");
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final FluidStack fluidStack = getFluid(stack);
		final String unlocName = getEmpty(stack).getTranslationKey();

		if (fluidStack == null)
			return I18n.translateToLocal(unlocName + ".name").trim();

		String fluidUnlocKey = unlocName + "." + fluidStack.getUnlocalizedName() + ".name"; //Unlocalized full bucket name
		//Try to find localization for this specific fluid bucket, if not found use a generic name with the fluid passed in as %s
		if (I18n.canTranslate(fluidUnlocKey))
			return I18n.translateToLocal(fluidUnlocKey).trim();

		return I18n.translateToLocalFormatted(getEmpty(stack).getTranslationKey() + ".filled.name", fluidStack.getFluid().getRarity(fluidStack).color + fluidStack.getLocalizedName() + TextFormatting.WHITE);
	}

	@Override
	public void getSubItems(@Nullable final CreativeTabs tab, final NonNullList<ItemStack> subItems) {
		if (!this.isInCreativeTab(tab)) return;

		for (int i = 0; i < 2; i++) {
			for (final Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
				// Add all fluids that the bucket can be filled with
				final FluidStack fs = new FluidStack(fluid, getCapacity());
				final ItemStack stack = new ItemStack(this, 1, i);
				final IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (fluidHandler != null && fluidHandler.fill(fs, true) == fs.amount) {
					final ItemStack filled = fluidHandler.getContainer();
					subItems.add(filled);
				}
			}
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return !getEmpty(stack).isEmpty();
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
		if (!getEmpty(itemStack).isEmpty()) {
			return getEmpty(itemStack).copy();
		}
		return super.getContainerItem(itemStack);
	}

	@Nullable
	@Override
	public FluidStack getFluid(final ItemStack container) {
		NBTTagCompound tagCompound = container.getTagCompound();
		if (tagCompound == null || !tagCompound.hasKey(FluidHandlerItemStackSimple.FLUID_NBT_KEY))
		{
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FluidHandlerItemStackSimple.FLUID_NBT_KEY));
	}

	public ItemStack getEmpty(ItemStack stack) {
		return stack.getMetadata() == 1 ? new ItemStack(ItemRegistry.DENTROTHYST_VIAL, stack.getCount(), 2) : new ItemStack(ItemRegistry.DENTROTHYST_VIAL, stack.getCount(), 0);
	}

	public ItemStack withFluid(int meta, Fluid fluid) {
		final FluidStack fs = new FluidStack(fluid, getCapacity());
		final ItemStack stack = new ItemStack(this, 1, meta);
		final IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		fluidHandler.fill(fs, true);
		return fluidHandler.getContainer();
	}

	public boolean canFillWith(ItemStack stack, FluidStack fluid) {
		return fluid.getFluid() == thebetweenlands.common.registries.FluidRegistry.FOG;
	}
}
