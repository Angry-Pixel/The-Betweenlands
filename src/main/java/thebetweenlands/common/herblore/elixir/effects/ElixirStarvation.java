package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ElixirStarvation extends ElixirEffect {
	public ElixirStarvation(ResourceLocation icon) {
		super(icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if (!entity.level().isClientSide() && entity instanceof Player player) {
			if (player.getFoodData().getFoodLevel() > 0) {
				player.getFoodData().addExhaustion(4);
			} else {
				player.getFoodData().setFoodLevel(0);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerStarve = 80 >> strength;
		return ticksPerStarve > 0 ? ticks % ticksPerStarve == 0 : true;
	}
}
