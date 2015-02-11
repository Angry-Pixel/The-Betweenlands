package thebetweenlands.creativetabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabBlocks extends CreativeTabBetweenlands {

	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(Blocks.stone);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}