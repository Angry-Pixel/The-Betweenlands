package thebetweenlands.common.block.misc;

import net.minecraft.world.entity.Entity;
import thebetweenlands.common.block.terrain.MudBlock;

public class CompactMudBlock extends MudBlock {
	public CompactMudBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canEntityWalkOnMud(Entity entity) {
		return true;
	}
}
