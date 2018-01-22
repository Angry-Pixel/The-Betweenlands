package thebetweenlands.common.block.structure;

import net.minecraft.block.SoundType;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockThatchRoof extends BlockSlanted {
	public BlockThatchRoof() {
		super(BlockRegistry.THATCH.getDefaultState());
		this.setHardness(0.5F);
		this.setSoundType(SoundType.PLANT);
	}
}
