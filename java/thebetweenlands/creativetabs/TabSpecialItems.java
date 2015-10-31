package thebetweenlands.creativetabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import thebetweenlands.items.BLItemRegistry;

public class TabSpecialItems
        extends CreativeTabBetweenlands
{
    public TabSpecialItems() {
        super("thebetweenlands.special");
    }

    @Override
    public Item getTabIconItem() {
        return BLItemRegistry.shimmerStone;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int func_151243_f() {
        return 0;
    }
}
