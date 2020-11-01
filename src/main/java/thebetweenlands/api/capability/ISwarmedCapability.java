package thebetweenlands.api.capability;

public interface ISwarmedCapability {
	public void setSwarmedStrength(float strength);

	public float getSwarmedStrength();

	public void setHurtTimer(int timer);

	public int getHurtTimer();

	public void setDamage(float damage);

	public float getDamage();

	public void setDamageTimer(int timer);

	public int getDamageTimer();

	public default void setLastRotations(float yaw, float pitch) {

	}

	public default float getLastYaw() {
		return 0;
	}

	public default float getLastPitch() {
		return 0;
	}

	public default void setLastRotationDeltas(float yaw, float pitch) {

	}

	public default float getLastYawDelta() {
		return 0;
	}

	public default float getLastPitchDelta() {
		return 0;
	}
}
