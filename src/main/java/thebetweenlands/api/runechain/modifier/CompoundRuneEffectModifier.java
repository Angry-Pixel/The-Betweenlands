package thebetweenlands.api.runechain.modifier;

import java.util.List;

import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.rune.AbstractRune;

/**
 * This rune effect modifier combines an arbitrary number of other rune effect modifiers together.
 */
public class CompoundRuneEffectModifier extends RuneEffectModifier {
	private final List<RuneEffectModifier> effects;

	public CompoundRuneEffectModifier(List<RuneEffectModifier> effects) {
		this.effects = effects;
	}

	@Override
	public boolean activate(AbstractRune<?> rune, IRuneChainUser user, Subject subject) {
		boolean activated = false;
		for(RuneEffectModifier effect : this.effects) {
			activated |= effect.activate(rune, user, subject);
		}
		return activated;
	}

	@Override
	public void update() {
		for(RuneEffectModifier effect : this.effects) {
			effect.update();
		}
	}

	@Override
	public int getColorModifier(Subject subject, int index) {
		if(index < 0 || index > this.effects.size()) {
			return 0xFFFFFFFF;
		}

		int accumulated = 0;

		for(RuneEffectModifier effect : this.effects) {
			int count = effect.getColorModifierCount(subject);

			if(index >= accumulated && index < accumulated + count) {
				return effect.getColorModifier(subject, index - accumulated);
			}

			accumulated += count;
		}

		return 0xFFFFFFFF;
	}

	@Override
	public int getColorModifierCount(Subject subject) {
		int count = 0;
		for(RuneEffectModifier effect : this.effects) {
			count += effect.getColorModifierCount(subject);
		}
		return count;
	}

	@Override
	public void render(Subject subject, int index, RenderProperties properties, RenderState state, float partialTicks) {
		int accumulated = 0;
		int i = 0;

		for(RuneEffectModifier effect : this.effects) {
			int count = effect.getRendererCount(subject);

			if(index >= accumulated && index < accumulated + count) {
				effect.render(subject, index - accumulated, properties, CompoundRenderState.get(state, this.effects.size(), i), partialTicks);
				return;
			}

			accumulated += count;
			i++;
		}
	}

	@Override
	public int getRendererCount(Subject subject) {
		int count = 0;
		for(RuneEffectModifier effect : this.effects) {
			count += effect.getRendererCount(subject);
		}
		return count;
	}
}
