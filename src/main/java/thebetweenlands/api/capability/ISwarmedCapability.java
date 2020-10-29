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
}
