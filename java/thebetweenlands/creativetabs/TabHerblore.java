package thebetweenlands.creativetabs;

import thebetweenlands.items.BLItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TabHerblore extends CreativeTabBetweenlands
{
	public TabHerblore() {
		super("thebetweenlands.herbLore");
	}

	@Override
	public Item getTabIconItem() {
		return BLItemRegistry.pestle;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}
