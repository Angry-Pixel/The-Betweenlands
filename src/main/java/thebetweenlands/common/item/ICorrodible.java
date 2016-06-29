package thebetweenlands.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.corrosion.CorrosionHelper;

public interface ICorrodible {
	/**
	 * Returns a list of item variants that use a corroded texture
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	default ResourceLocation[] getCorrodibleVariants() {
		return CorrosionHelper.getVariantFromUnlocalizedName(((Item)this).getUnlocalizedName());
	}
}
