package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockLadderBetweenlands extends BlockLadder {
	public BlockLadderBetweenlands() {
		super();
		this.setSoundType(SoundType.LADDER);
		this.setHardness(0.4F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}
}
