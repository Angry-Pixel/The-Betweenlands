package thebetweenlands.api.rune.impl;

import java.util.List;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.IRuneChainUser;

public class CompoundRuneEffectModifier extends RuneEffectModifier {
	public static class CompoundRenderState extends RenderState {
		private RenderState[] states;

		private CompoundRenderState(RenderState state, int num) {
			this.states = new RenderState[num];
			for(int i = 0; i < num; i++) {
				this.states[i] = RenderState.with(state);
			}
		}

		@Override
		protected void tick() {
			for(RenderState state : this.states) {
				state.update();
			}
		}

		@Nullable
		public static RenderState get(@Nullable RenderState state, int num, int i) {
			if(state != null) {
				return state.get(CompoundRenderState.class, () -> new CompoundRenderState(state, num)).states[i];
			}
			return state;
		}
	}

	private final List<RuneEffectModifier> effects;

	public CompoundRuneEffectModifier(List<RuneEffectModifier> effects) {
		this.effects = effects;
	}

	@Override
	public boolean activate(AbstractRune<?> rune, IRuneChainUser user, RuneEffectModifier.Subject subject) {
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
	public int getColorModifier(RuneEffectModifier.Subject subject, int index) {
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
	public int getColorModifierCount(RuneEffectModifier.Subject subject) {
		int count = 0;
		for(RuneEffectModifier effect : this.effects) {
			count += effect.getColorModifierCount(subject);
		}
		return count;
	}

	@Override
	public void render(RuneEffectModifier.Subject subject, int index, RenderProperties properties, RenderState state, float partialTicks) {
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
	public int getRendererCount(RuneEffectModifier.Subject subject) {
		int count = 0;
		for(RuneEffectModifier effect : this.effects) {
			count += effect.getRendererCount(subject);
		}
		return count;
	}
}
