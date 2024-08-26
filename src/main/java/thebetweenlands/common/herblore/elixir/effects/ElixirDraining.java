package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ElixirDraining extends ElixirEffect {
	public ElixirDraining(ResourceLocation icon) {
		super(icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide()) {
			entity.hurt(entity.damageSources().magic(), 1);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerDamage = 50 >> strength;
		return ticksPerDamage == 0 || ticks % ticksPerDamage == 0;
	}
}
