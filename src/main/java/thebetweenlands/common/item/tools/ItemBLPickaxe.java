package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;
import thebetweenlands.common.corrosion.CorrosionHelper;
import thebetweenlands.common.item.ICorrodible;

public class ItemBLPickaxe extends ItemPickaxe implements ICorrodible {
	public ItemBLPickaxe(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}
}
