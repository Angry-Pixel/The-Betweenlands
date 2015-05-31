package thebetweenlands.creativetabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import thebetweenlands.items.BLItemRegistry;

public class TabGears
        extends CreativeTabBetweenlands
{
	public TabGears() {
		super("thebetweenlands.gear");
	}

	@Override
	public Item getTabIconItem() {
		return BLItemRegistry.valonitePickaxe;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
}
