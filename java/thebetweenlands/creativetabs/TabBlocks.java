package thebetweenlands.creativetabs;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TabBlocks
        extends CreativeTabBetweenlands
{
    Comparator<ItemStack> sortedBlocks;

	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(BLBlockRegistry.swampGrass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return 0;
	}
/*
    @Override
    public void displayAllReleventItems(List tabItems) {
        super.displayAllReleventItems(tabItems);
        if( this.sortedBlocks == null ) {
            this.sortedBlocks = Ordering.explicit(BLBlockRegistry.BLOCKS).onResultOf(new Function<ItemStack, Block>() {
                                                                                         @Nullable
                                                                                         @Override
                                                                                         public Block apply(@Nullable ItemStack input) {
                                                                                             return input != null ? Block.getBlockFromItem(input.getItem()) : null;
                                                                                         }
                                                                                     });
        }
        Collections.sort(tabItems, this.sortedBlocks);
    }*/
}
