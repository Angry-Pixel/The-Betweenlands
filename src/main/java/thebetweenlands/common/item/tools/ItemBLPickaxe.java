package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.ICorrodible;

public class ItemBLPickaxe extends ItemPickaxe implements ICorrodible {
	public ItemBLPickaxe(ToolMaterial material) {
		super(material);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		//Add all corrodible pickaxe variants
		return new ResourceLocation[] {
				new ResourceLocation("thebetweenlands", "octine_pickaxe")
		};
	}
}
