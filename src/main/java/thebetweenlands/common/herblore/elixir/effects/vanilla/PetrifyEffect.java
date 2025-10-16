package thebetweenlands.common.herblore.elixir.effects.vanilla;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class PetrifyEffect extends MobEffect {
	public PetrifyEffect() {
		super(MobEffectCategory.HARMFUL, 0xFF71C230);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		CompoundTag nbt = entity.getPersistentData();

		if(nbt.getInt("thebetweenlands.petrify.ticks") != entity.tickCount - 1) {
			nbt.putFloat("thebetweenlands.petrify.yaw", entity.getYRot());
			nbt.putFloat("thebetweenlands.petrify.yawHead", entity.getYHeadRot());
			nbt.putFloat("thebetweenlands.petrify.pitch", entity.getXRot());
		}
		nbt.putInt("thebetweenlands.petrify.ticks", entity.tickCount);

		entity.absRotateTo(nbt.getFloat("thebetweenlands.petrify.yaw"), nbt.getFloat("thebetweenlands.petrify.pitch"));
		entity.setYHeadRot(nbt.getFloat("thebetweenlands.petrify.yawHead"));
		return super.applyEffectTick(entity, amplifier);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
		cures.clear();
	}
}
