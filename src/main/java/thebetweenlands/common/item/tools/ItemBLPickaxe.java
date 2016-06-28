package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.corrosion.CorrosionHelper;
import thebetweenlands.common.item.ICorrodible;

public class ItemBLPickaxe extends ItemPickaxe implements ICorrodible {
	public ItemBLPickaxe(ToolMaterial material) {
		super(material);

		//Adds the corrosion property overrides for model selection
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		//Add all corrodible pickaxe variants
		return new ResourceLocation[] {
				new ResourceLocation("thebetweenlands", "weedwood_pickaxe"),
				new ResourceLocation("thebetweenlands", "bone_pickaxe"),
				new ResourceLocation("thebetweenlands", "octine_pickaxe"),
				new ResourceLocation("thebetweenlands", "valonite_pickaxe")
		};
	}
}
