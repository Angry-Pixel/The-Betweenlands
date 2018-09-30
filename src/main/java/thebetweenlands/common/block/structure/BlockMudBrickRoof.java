package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudBrickRoof extends BlockSlanted {
	public BlockMudBrickRoof() {
		super(BlockRegistry.MUD_BRICKS.getDefaultState());
		this.setSoundType(SoundType.STONE).setHardness(1.5F).setResistance(10.0F);
	}
}
