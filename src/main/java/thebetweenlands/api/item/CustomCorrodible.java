package thebetweenlands.api.item;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.item.CorrosionData;
import thebetweenlands.common.registries.DataComponentRegistry;

public interface CustomCorrodible {

//	/**
//	 * Returns an array of item variants that use a corroded texture
//	 * @return
//	 */
//	@Nullable
//	default ResourceLocation[] getCorrodibleVariants() {
//		return null;
//	}
//
//	/**
//	 * Returns an array of item variants that use a corroded texture. If the item
//	 * doesn't specify any the default variant is returned
//	 * @param item
//	 * @return
//	 */
//	public static <I extends Item & ICustomCorrodible> ResourceLocation[] getItemCorrodibleVariants(I item) {
//		ResourceLocation[] variants = item.getCorrodibleVariants();
//		if (variants == null) {
//			return new ResourceLocation[] { item.getRegistryName() };
//		}
//		return variants;
//	}

	/**
	 * Returns the maximum amount of coating for the specified item stack
	 * @param stack
	 * @return
	 */
	default int getMaxCoating(ItemStack stack) {
		return CorrosionHelper.MAX_CORROSION;
	}

	/**
	 * Returns the maximum amount of corrosion for the specified item stack
	 * @param stack
	 * @return
	 */
	default int getMaxCorrosion(ItemStack stack) {
		return CorrosionHelper.MAX_COATING;
	}

	/**
	 * Returns the amount of coating on the specified item stack
	 * @param stack
	 * @return
	 */
	default int getCoating(ItemStack stack) {
		if(stack.has(DataComponentRegistry.CORROSION)) {
			return stack.get(DataComponentRegistry.CORROSION).coating();
		}
		return 0;
	}

	/**
	 * Returns the amount of corrosion on the specified item stack
	 * @param stack
	 * @return
	 */
	default int getCorrosion(ItemStack stack) {
		if(stack.has(DataComponentRegistry.CORROSION)) {
			return stack.get(DataComponentRegistry.CORROSION).corrosion();
		}
		return 0;
	}

	/**
	 * Sets the amount of coating of the specified item stack
	 * @param itemStack
	 * @param coating
	 */
	default void setCoating(ItemStack stack, int coating) {
		if(stack.has(DataComponentRegistry.CORROSION)) {
			CorrosionData data = stack.get(DataComponentRegistry.CORROSION);
			stack.set(DataComponentRegistry.CORROSION, data.withCoating(Math.clamp(coating, 0, this.getMaxCoating(stack))));
		}
	}

	/**
	 * Sets the amount of corrosion of the specified item stack
	 * @param stack
	 * @param corrosion
	 */
	default void setCorrosion(ItemStack stack, int corrosion) {
		if(stack.has(DataComponentRegistry.CORROSION)) {
			CorrosionData data = stack.get(DataComponentRegistry.CORROSION);
			stack.set(DataComponentRegistry.CORROSION, data.withCorrosion(Math.clamp(corrosion, 0, this.getMaxCorrosion(stack))));
		}
	}

}
