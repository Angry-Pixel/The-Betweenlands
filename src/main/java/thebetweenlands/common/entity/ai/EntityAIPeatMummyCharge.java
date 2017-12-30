package thebetweenlands.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

public class EntityAIPeatMummyCharge extends EntityAIBase {
	private final EntityPeatMummy mummy;

	private int chargingCooldown;
	private int chargingTime;

	public EntityAIPeatMummyCharge(EntityPeatMummy mummy) {
		this.mummy = mummy;
		this.chargingCooldown = mummy.getMaxChargingCooldown();
	}

	@Override
	public boolean shouldExecute() {
		return this.mummy.getAttackTarget() != null && this.mummy.getSpawningProgress() == 1;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.mummy.getAttackTarget() != null;
	}

	@Override
	public void updateTask() {
		if(!this.mummy.isCharging()) {
			if(this.chargingCooldown == 0) {
				//Peat mummy done charging
				this.resetTask();
			}

			if(this.chargingCooldown > 0 && this.mummy.getAttackTarget() != null) {
				this.chargingCooldown--;
			}
			if(this.chargingCooldown <= 0) {
				this.mummy.startCharging();
			}
		} else if(!this.mummy.isPreparing()) {
			this.chargingTime++;
			if(this.chargingTime >= this.mummy.getEntityAttribute(EntityPeatMummy.CHARGING_TIME_ATTRIB).getAttributeValue()) {
				this.mummy.stopCharging();
			}
		}
	}

	@Override
	public void resetTask() {
		this.chargingTime = 0;
		this.chargingCooldown = this.mummy.getMaxChargingCooldown() + this.mummy.world.rand.nextInt(this.mummy.getMaxChargingCooldown() / 2 + 1);
	}
}
