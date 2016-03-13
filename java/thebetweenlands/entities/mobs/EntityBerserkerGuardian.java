package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityBerserkerGuardian extends EntityTempleGuardian implements IEntityBL {
	private int chargingTimer = 0;
	private int timeUntilCharge = 0;
	public ControlledAnimation chargeAnim = new ControlledAnimation(10);

	private static final int CHARGE_DURATION = 40;
	private static final int CHARGE_COOLDOWN = 120;

	public EntityBerserkerGuardian(World worldObj) {
		super(worldObj);
		this.setSize(1.1F, 2.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		//		float vel = (float) Math.sqrt(Math.pow(posX - prevPosX, 2) + Math.pow(posZ - prevPosZ, 2));
		//		if (getCharging() && chargingTimer < 30 && vel < 0.01) {
		//			playSound("thebetweenlands:templeGuardianBerserkerImpact", 1, 1);
		//			setCharging(false);
		//			chargingTimer = -1;
		//			chargeAnim.setTimer(0);
		//			float speed = 1f;
		//			float x = -speed * (float) Math.sin(-rotationYaw * Math.PI / 180);
		//			float z = -speed * (float) Math.cos(-rotationYaw * Math.PI / 180);
		//			moveEntity(x, 1, z);
		//		}

		if (getAttackTarget() != null && timeUntilCharge == 0 && getDistanceToEntity(getAttackTarget()) > 4 && active.getTimer() == 20) {
			setCharging(true);
			playSound("thebetweenlands:templeGuardianBerserkerCharge", 1, 1);
			chargingTimer = CHARGE_DURATION;
			timeUntilCharge = CHARGE_COOLDOWN;
		}
		if (chargingTimer >= 0) chargingTimer--;
		if (chargingTimer == 0) setCharging(false);
		if (timeUntilCharge > 0) timeUntilCharge--;

		if (getCharging()) {
			getNavigator().clearPathEntity();
			float speed = 0.4f;
			rotationYaw = prevRotationYaw;
			renderYawOffset = rotationYaw;
			float x = speed * (float) Math.sin(-rotationYaw * Math.PI / 180);
			float z = speed * (float) Math.cos(-rotationYaw * Math.PI / 180);
			moveEntity(x, motionY, z);
			chargeAnim.increaseTimer();
		}
		else chargeAnim.decreaseTimer();
	}

	@Override
	protected String getLivingSound() {
		if (!getActive()) return null;
		return "thebetweenlands:templeGuardianBerserkerLiving";
	}

	@Override
	public String pageName() {
		return "beserkerGuardian";
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(21, (byte) 0);
	}

	public boolean getCharging() {
		return dataWatcher.getWatchableObjectByte(21) == 1;
	}

	public void setCharging(boolean charging) {
		dataWatcher.updateObject(21, charging ? (byte) 1 : (byte) 0);
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		super.collideWithEntity(entity);
		if (getCharging() && entity instanceof EntityLivingBase) attackEntityAsMob(entity);
	}

	@Override
	public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
		if (getCharging()) return;
		super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_, p_70653_5_);
	}
}