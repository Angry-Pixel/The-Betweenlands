package thebetweenlands.creativetabs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabItems extends CreativeTabBetweenlands {

	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public Item getTabIconItem() {
		return Items.bucket;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}