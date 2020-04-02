package thebetweenlands.api.capability;

public class ProtectionShield {
	private boolean[] activeShields = new boolean[20];

	private int[] shieldAnimationTicks = new int[20];

	private int numActiveShields = 0;

	protected void updateNumActiveShields() {
		this.numActiveShields = 0;
		for(int i = 0; i <= 19; i++) {
			if(this.isActive(i))
				this.numActiveShields++;
		}
	}

	public void setActive(int index, boolean active) {
		this.activeShields[index] = active;
		this.updateNumActiveShields();
	}

	public boolean isActive(int index) {
		return this.activeShields[index];
	}

	public void setAnimationTicks(int index, int ticks) {
		this.shieldAnimationTicks[index] = ticks;
	}

	public int getAnimationTicks(int index) {
		return this.shieldAnimationTicks[index];
	}

	public boolean hasShield() {
		return this.numActiveShields > 0;
	}

	public int packActiveData() {
		int packedData = 0;
		for(int i = 0; i <= 19; i++) {
			packedData |= (this.activeShields[i] ? 1 : 0) << i;
		}
		return packedData;
	}

	public void unpackActiveData(int packedData) {
		for(int i = 0; i <= 19; i++) {
			this.activeShields[i] = ((packedData >> i) & 1) == 1 ? true : false;
		}
		this.updateNumActiveShields();
	}

	public float getYaw(float ticks) {
		return ticks * (1.0F + 6.0F / 20.0F * (20 - this.numActiveShields)) * 0.5f;
	}

	public float getPitch(float ticks) {
		return ticks * (1.4F + 8.0F / 20.0F * (20 - this.numActiveShields)) * 0.5f;
	}

	public float getRoll(float ticks) {
		return ticks * (1.6F + 10.0F / 20.0F * (20 - this.numActiveShields)) * 0.5f;
	}
}
