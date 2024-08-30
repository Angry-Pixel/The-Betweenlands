package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ElixirSwiftarm extends ElixirEffect {
	public ElixirSwiftarm(ResourceLocation icon) {
		super(icon);
	}

	@Override
	public int getColor() {
		return 0xFFe8fc5b;
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if(entity instanceof Player player) {
			if(player.swingTime >= 20) player.swing(InteractionHand.MAIN_HAND);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
