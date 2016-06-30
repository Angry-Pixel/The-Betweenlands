package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSpade;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

public class ItemBLShovel extends ItemSpade implements ICorrodible {
	public ItemBLShovel(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}
}
