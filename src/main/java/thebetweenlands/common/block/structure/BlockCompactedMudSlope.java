package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockCompactedMudSlope extends BlockSlanted {
	public BlockCompactedMudSlope() {
		super(BlockRegistry.COMPACTED_MUD.getDefaultState());
		this.setSoundType(SoundType.GROUND).setHardness(1F).setResistance(10.0F);
	}
}
