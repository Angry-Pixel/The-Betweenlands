package thebetweenlands.api.runechain.initiation;

import net.minecraft.entity.Entity;

public class InteractionInitiationPhase extends InitiationPhase {
	private final Entity target;

	public InteractionInitiationPhase(Entity target) {
		this.target = target;
	}

	public Entity getTarget() {
		return this.target;
	}
}