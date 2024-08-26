package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ElixirFeasting extends ElixirEffect {
	public ElixirFeasting(ResourceLocation icon) {
		super(icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide() && entity instanceof Player player) {
			if(player.getFoodData().needsFood()) {
				player.getFoodData().eat(1, 0.5F);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerFood = 100 >> strength;
		return ticksPerFood == 0 || ticks % ticksPerFood == 0;
	}
}
