package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSword;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

public class ItemBLSword extends ItemSword implements ICorrodible {
	public ItemBLSword(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
		CircleGemHelper.addGemPropertyOverrides(this);
	}
}
