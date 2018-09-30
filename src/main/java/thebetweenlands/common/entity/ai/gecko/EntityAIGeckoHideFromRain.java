package thebetweenlands.common.entity.ai.gecko;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class EntityAIGeckoHideFromRain extends EntityAIGeckoHide {
	public EntityAIGeckoHideFromRain(EntityGecko gecko, double speed) {
		super(gecko, speed, speed);
	}

	@Override
	protected boolean shouldFlee() {
		return this.gecko.world.isRainingAt(new BlockPos(this.gecko));
	}

	@Override
	protected Vec3d getFleeingCausePosition() {
		return null;
	}
}
