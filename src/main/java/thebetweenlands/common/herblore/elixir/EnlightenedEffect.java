package thebetweenlands.common.herblore.elixir;

import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.common.EffectCure;

public class EnlightenedEffect extends MobEffect {
	public EnlightenedEffect() {
		super(MobEffectCategory.BENEFICIAL, 5926017);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		cures.clear();
	}
}
