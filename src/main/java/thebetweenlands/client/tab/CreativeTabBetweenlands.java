package thebetweenlands.client.tab;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabBetweenlands extends CreativeTabs {
	public CreativeTabBetweenlands(String label) {
		super(label);
	}

	public void setTab(Block... blocks) {
		for( Block block : blocks ) {
			if( block != null ) {
				block.setCreativeTab(this);
			}
		}
	}

	public void setTab(Item... items) {
		for( Item item : items ) {
			if( item != null ) {
				item.setCreativeTab(this);
			}
		}
	}
}
