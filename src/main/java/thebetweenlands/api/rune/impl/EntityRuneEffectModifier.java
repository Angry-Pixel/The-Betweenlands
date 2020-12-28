package thebetweenlands.api.rune.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.entity.Entity;
import thebetweenlands.api.entity.IRuneEffectModifierEntity;
import thebetweenlands.api.rune.IRuneChainUser;

public class EntityRuneEffectModifier extends RuneEffectModifier {
	private final boolean clientOnly;

	private List<Triple<IRuneEffectModifierEntity, AbstractRune<?>, Subject>> entries = new ArrayList<>();

	public EntityRuneEffectModifier(boolean clientOnly) {
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
