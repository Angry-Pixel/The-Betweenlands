package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSpade;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.corrosion.CorrosionHelper;
import thebetweenlands.common.item.ICorrodible;

public class ItemBLShovel extends ItemSpade implements ICorrodible {
	public ItemBLShovel(ToolMaterial material) {
		super(material);
		
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		return CorrosionHelper.getVariantFromUnlocalizedName(this.getUnlocalizedName());
	}
}
