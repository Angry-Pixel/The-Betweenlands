package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSword;
import thebetweenlands.common.gem.CircleGem;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

public class ItemBLSword extends ItemSword implements ICorrodible {
	public ItemBLSword(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
		CircleGem.addGemPropertyOverrides(this);
	}
}
