package thebetweenlands.common.herblore.elixir.effects.vanilla;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class BlessedEffect extends MobEffect {
	public BlessedEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xFF0000FF);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
		cures.clear();
	}
}
