package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ElixirMasking extends ElixirEffect {
	public ElixirMasking(ResourceLocation icon) {
		super(icon);
	}

	@Override
	public int getColor() {
		return 0xFF28ccd5;
	}

	public boolean canEntityBeSeenBy(LivingEntity target, LivingEntity watcher) {
		if(this.isActive(target)) {
			int strength = this.getStrength(target);
			double minDist = 28.0D - Math.min(20.0D / 4.0D * strength, 21.0D);
			return target.distanceTo(watcher) < minDist;
		}
		return true;
	}
}
