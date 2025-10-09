package thebetweenlands.common.entity.projectile.spear;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;

public class AmphibiousFishingSpear extends FishingSpear {

	public AmphibiousFishingSpear(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}
}
