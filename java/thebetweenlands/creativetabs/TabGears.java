package thebetweenlands.creativetabs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabGears extends CreativeTabBetweenlands {

	public TabGears() {
		super("thebetweenlands.gear");
	}

	@Override
	public Item getTabIconItem() {
		return Items.diamond_sword;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}