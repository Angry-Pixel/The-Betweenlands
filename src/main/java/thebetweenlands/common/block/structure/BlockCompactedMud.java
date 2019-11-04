package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.terrain.BlockMud;

public class BlockCompactedMud extends BlockMud { // Keeping this it's own class as we may need to make many moar spweshul
	public BlockCompactedMud() {
		setHardness(1F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setLightOpacity(255);
	}

	@Override
	public boolean canEntityWalkOnMud(Entity entity) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return true;
	}
}