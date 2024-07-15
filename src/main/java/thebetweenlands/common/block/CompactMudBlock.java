package thebetweenlands.common.block;

import net.minecraft.world.entity.Entity;

public class CompactMudBlock extends MudBlock {
	public CompactMudBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canEntityWalkOnMud(Entity entity) {
		return true;
	}
}
