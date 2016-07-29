package thebetweenlands.common.item.corrosion;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICorrodible {
	/**
	 * Returns a list of item variants that use a corroded texture
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	default ResourceLocation[] getCorrodibleVariants() {
		return null;
	}

	public static <I extends Item & ICorrodible> ResourceLocation[] getItemCorrodibleVariants(I item) {
		ResourceLocation[] variants = item.getCorrodibleVariants();
		if (variants == null) {
			return new ResourceLocation[] { item.getRegistryName() };
		}
		return variants;
	}
}
