package thebetweenlands.common.item;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICorrodible {
	/**
	 * Returns a list of item variants that use a corroded texture
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	ResourceLocation[] getCorrodibleVariants();
}
