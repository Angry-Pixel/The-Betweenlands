package thebetweenlands.api.runechain.modifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.entity.Entity;
import thebetweenlands.api.entity.IRuneEffectModifierEntity;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.rune.AbstractRune;

/**
 * This rune effect modifier allows ({@link Subject}) entities that extend {@link IRuneEffectModifierEntity} to use a rune's {@link RuneEffectModifier}.
 * It does this by calling {@link IRuneEffectModifierEntity#setRuneEffectModifier(RuneEffectModifier, Subject)} on the subject's entity with the rune's {@link AbstractRune#getRuneEffectModifier()}. The entity
 * can then e.g. use that rune effect modifier for rendering.
 */
public class AttachedRuneEffectModifier extends RuneEffectModifier {
	private final boolean clientOnly;

	private List<Triple<IRuneEffectModifierEntity, AbstractRune<?>, Subject>> entries = new ArrayList<>();

	public AttachedRuneEffectModifier(boolean clientOnly) {
		this.clientOnly = clientOnly;
	}

	@Override
	public boolean activate(AbstractRune<?> rune, IRuneChainUser user, Subject subject) {
		if(subject != null) {
			Entity entity = subject.getEntity();

			if(entity instanceof IRuneEffectModifierEntity) {
				if(!this.clientOnly || user.getWorld().isRemote) {
					this.entries.add(Triple.of((IRuneEffectModifierEntity) entity, rune, subject));
				}

				return true;
			}
		}
		return false;
	}

	@Override
	public void update() {
		Iterator<Triple<IRuneEffectModifierEntity, AbstractRune<?>, Subject>> entriesIt = this.entries.iterator();

		while(entriesIt.hasNext()) {
			Triple<IRuneEffectModifierEntity, AbstractRune<?>, Subject> entry = entriesIt.next();

			IRuneEffectModifierEntity entity = entry.getLeft();
			RuneEffectModifier modifier = entry.getMiddle().getRuneEffectModifier();
			Subject subject = entry.getRight();

			if(modifier != null && subject.isActive()) {
				entity.setRuneEffectModifier(modifier, subject);
			} else {
				entity.clearRuneEffectModifier();
				entriesIt.remove();
			}
		}
	}
}
