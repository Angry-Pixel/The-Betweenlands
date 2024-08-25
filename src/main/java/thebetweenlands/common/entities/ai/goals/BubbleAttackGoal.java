package thebetweenlands.common.entities.ai.goals;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entities.fishing.BubblerCrab;
import thebetweenlands.common.registries.SoundRegistry;

public class BubbleAttackGoal extends Goal {
	private final BubblerCrab crab;
	private int attackStep;
	private int attackTime;

	public BubbleAttackGoal(BubblerCrab crab) {
		this.crab = crab;
	}
	@Override
	public boolean canUse() {
		LivingEntity entitylivingbase = this.crab.getTarget();
		return entitylivingbase != null && entitylivingbase.isAlive();
	}

	@Override
	public void start() {
		this.attackStep = 0;
	}

	@Override
	public void tick() {
		this.attackTime--;
		LivingEntity entitylivingbase = crab.getTarget();
		if (entitylivingbase != null && entitylivingbase.isAlive()) {
			double d0 = this.crab.distanceToSqr(entitylivingbase);
			if (d0 < 25D) {
				double d1 = entitylivingbase.getX() - this.crab.getX();
				double d2 = entitylivingbase.getY() - this.crab.getY();
				double d3 = entitylivingbase.getZ() - this.crab.getZ();
				if (this.attackTime <= 0) {
					this.attackStep++;
					if (this.attackStep == 1) {
						this.attackTime = 40;
					} else {
						this.attackTime = 40;
						this.attackStep = 0;
					}
					if (this.attackStep == 1) {
//						EntityBubblerCrabBubble entityBubble = new BubblerCrabBubble(this.crab.level(), this.crab);
//						entityBubble.setPos(this.crab.getX(), this.crab.getY() + this.crab.getBbHeight() + 0.5D, this.crab.getZ());
//						entityBubble.shoot(d1, d2, d3, 0.5F, 0F);
//						this.crab.level().addFreshEntity(entityBubble);
						this.crab.level().playSound(null, this.crab.blockPosition(), SoundRegistry.BUBBLER_SPIT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
						this.crab.aggroCooldown = 0;
					}
				}
			}
		}
	}
}
