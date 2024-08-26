package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class ElixirDecay extends ElixirEffect {
	public ElixirDecay(ResourceLocation icon) {
		super(icon);
	}

	@Override
	protected void performEffect(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide() && entity instanceof Player player) {
			DecayData cap = player.getData(AttachmentRegistry.DECAY);
			if(cap.isDecayEnabled(player)) {
				cap.addDecayAcceleration(player, 4.0F);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerDecay = 100 >> strength;
		return ticksPerDecay == 0 || ticks % ticksPerDecay == 0;
	}
}
