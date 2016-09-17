package thebetweenlands.common.block.misc;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockTrapDoorBetweenlands extends BlockTrapDoor {
	public BlockTrapDoorBetweenlands(Material material) {
		super(material);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public BlockTrapDoorBetweenlands setSoundType(SoundType type) {
		return (BlockTrapDoorBetweenlands) super.setSoundType(type);
	}
}
