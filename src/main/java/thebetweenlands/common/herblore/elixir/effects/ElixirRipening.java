package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class ElixirRipening extends ElixirEffect {
	public ElixirRipening(ResourceLocation icon) {
		super(icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide() && entity instanceof Player player) {
			DecayData cap = player.getData(AttachmentRegistry.DECAY);
			if(cap.isDecayEnabled(player)) {
				cap.addStats(player, -1, 0.6F);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerHeal = 100 >> strength;
		return ticksPerHeal == 0 || ticks % ticksPerHeal == 0;
	}
}
