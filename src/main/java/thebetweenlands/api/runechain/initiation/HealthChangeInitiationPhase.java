package thebetweenlands.api.runechain.initiation;

public class HealthChangeInitiationPhase extends InitiationPhase {
	private final float oldHealth, newHealth;

	public HealthChangeInitiationPhase(float oldHealth, float newHealth) {
		this.oldHealth = oldHealth;
		this.newHealth = newHealth;
	}

	public float getOldHealth() {
		return this.oldHealth;
	}

	public float getNewHealth() {
		return this.newHealth;
	}
}
