package thebetweenlands.common.item.misc;

import net.minecraft.item.Item;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;

public class ItemDentrothystShard extends Item {
	public final EnumDentrothyst type;

	public ItemDentrothystShard(EnumDentrothyst type) {
		this.type = type;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}
}
