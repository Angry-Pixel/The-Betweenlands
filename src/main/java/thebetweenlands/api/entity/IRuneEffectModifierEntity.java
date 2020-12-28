package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneEffectModifier.Subject;

public interface IRuneEffectModifierEntity {
	public void setRuneEffectModifier(RuneEffectModifier modifier, Subject subject);

	@Nullable
	public RuneEffectModifier getRuneEffectModifier();

	@Nullable
	public Subject getRuneEffectModifierSubject();

	public void clearRuneEffectModifier();
}
