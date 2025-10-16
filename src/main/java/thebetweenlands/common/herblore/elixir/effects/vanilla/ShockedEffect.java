package thebetweenlands.common.herblore.elixir.effects.vanilla;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.ShockParticlePacket;

import java.util.Set;

public class ShockedEffect extends MobEffect {

	public ShockedEffect() {
		super(MobEffectCategory.HARMFUL, 0xFFFFFF00);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if(!entity.level().isClientSide() && entity.level().getRandom().nextInt(20) == 0) {
			PacketDistributor.sendToPlayersTrackingEntity(entity, new ShockParticlePacket(entity.getId()));
		}
		return super.applyEffectTick(entity, amplifier);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
		cures.clear();
	}
}
