package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockLeverBetweenlands extends BlockLever {
	public BlockLeverBetweenlands() {
		super();
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.5F);
		this.setSoundType(SoundType.WOOD);
	}
}
