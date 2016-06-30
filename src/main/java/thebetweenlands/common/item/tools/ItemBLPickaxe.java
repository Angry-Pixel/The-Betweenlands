package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

public class ItemBLPickaxe extends ItemPickaxe implements ICorrodible {
	public ItemBLPickaxe(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}
}
