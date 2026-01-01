package thebetweenlands.common.entity.creature;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.LightningArcParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.ElectricShock;
import thebetweenlands.common.registries.SoundRegistry;

public class CaveJellyfish extends Jellyfish implements Enemy {
	protected static final byte EVENT_SPARK = 80;

	public CaveJellyfish(EntityType<? extends WaterAnimal> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.5D, false) {
			@Override
    		protected void checkAndPerformAttack(LivingEntity enemy) {
				//No melee attacks
			}
		});
		this.goalSelector.addGoal(0, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 0.5D, 20));
		targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, null));
	}

	@Override
	public void tick() {
		super.tick();

		if(this.level().getDifficulty() != Difficulty.PEACEFUL) {
			LivingEntity target = this.getTarget();

			if(target != null) {
				double dst = this.distanceTo(target);

				if(dst < 6.0f && this.level().random.nextInt(20) == 0) {
					this.level().broadcastEntityEvent(this, EVENT_SPARK);
				}

				if(dst < 3.0f && this.tickCount % 20 == 0) {
					if(hasLineOfSight(target) && target.isInWater()) {
						this.level().addFreshEntity(new ElectricShock(this.level(), this, target, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75f, true));
						level().playSound(null, blockPosition(), SoundRegistry.JELLYFISH_ZAP.get(), SoundSource.HOSTILE, 1F, 0.8F + random.nextFloat());
					}
				}
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if(id == EVENT_SPARK) {
			this.spawnLightningArcs();
		}
	}

	private void spawnLightningArcs() {
		Entity view = Minecraft.getInstance().getCameraEntity();
		if(view != null && view.distanceTo(this) < 16) {
			float ox = this.level().getRandom().nextFloat() - 0.5f + (float)this.getDeltaMovement().x();
			float oy = this.level().getRandom().nextFloat() - 0.5f + (float)this.getDeltaMovement().y();
			float oz = this.level().getRandom().nextFloat() - 0.5f + (float)this.getDeltaMovement().z();

			TheBetweenlands.createParticle(LightningArcParticleOptions.defaultArc(), this.level(), this.getX(), this.getY() + this.getBbHeight() * 0.5f, this.getZ(),
					ParticleFactory.ParticleArgs.get()
					.withMotion(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z())
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vec3(this.getX() + ox, this.getY() + this.getBbHeight() * 0.5f + oy, this.getZ() + oz)));
		}
	}
}
