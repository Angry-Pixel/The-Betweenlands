package thebetweenlands.creativetabs;

import net.minecraft.item.Item;
import thebetweenlands.blocks.BLBlockRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabBlocks
        extends CreativeTabBetweenlands
{
	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(BLBlockRegistry.betweenstone);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}
