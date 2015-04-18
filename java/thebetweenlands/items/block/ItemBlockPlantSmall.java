package thebetweenlands.items.block;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockPlantSmall extends ItemBlockGeneric {

	public ItemBlockPlantSmall(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return getBlock().getIcon(2, damage);
	}
}