package thebetweenlands.creativetabs;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TabItems
        extends CreativeTabBetweenlands
{
    Comparator<ItemStack> sortedItems;

	public TabItems() {
		super("thebetweenlands.item");
	}

	@Override
	public Item getTabIconItem() {
		return BLItemRegistry.swampTalisman;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}

    @Override
    public void displayAllReleventItems(List tabItems) {
        super.displayAllReleventItems(tabItems);
        if( this.sortedItems == null ) {
            this.sortedItems = Ordering.explicit(BLItemRegistry.ITEMS).onResultOf(new Function<ItemStack, Item>() {
                                                                                         @Nullable
                                                                                         @Override
                                                                                         public Item apply(@Nullable ItemStack input) {
                                                                                             return input != null ? input.getItem() : null;
                                                                                         }
                                                                                     });
        }
        Collections.sort(tabItems, this.sortedItems);
    }
}
