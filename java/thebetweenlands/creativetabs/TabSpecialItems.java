package thebetweenlands.creativetabs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabSpecialItems extends CreativeTabBetweenlands {

	public TabSpecialItems() {
		super("thebetweenlands.special");
	}

	@Override
	public Item getTabIconItem() {
		return Items.enchanted_book;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}