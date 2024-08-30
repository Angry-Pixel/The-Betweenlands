package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ElixirHealing extends ElixirEffect {
	public ElixirHealing(ResourceLocation icon) {
		super(icon);
	}

	@Override
	public int getColor() {
		return 0xFF1cd67d;
	}

	@Override
	protected void performEffect(LivingEntity entity, int ampifier) {
		if(!entity.level().isClientSide() && entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(1.0F);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerHeal = 50 >> strength;
		return ticksPerHeal == 0 || ticks % ticksPerHeal == 0;
	}
}
