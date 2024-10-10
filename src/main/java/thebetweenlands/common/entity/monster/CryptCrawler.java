package thebetweenlands.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class CryptCrawler extends Monster implements BLEntity {

	private static final EntityDataAccessor<Boolean> IS_STANDING = SynchedEntityData.defineId(CryptCrawler.class, EntityDataSerializers.BOOLEAN);

	public float standingAngle, prevStandingAngle;

	public CryptCrawler(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 5;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 20.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.31D)
			.add(Attributes.ATTACK_DAMAGE, 1.75D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_STANDING, false);
	}

	public boolean isStanding() {
		return this.getEntityData().get(IS_STANDING);
	}

	private void setIsStanding(boolean standing) {
		this.getEntityData().set(IS_STANDING, standing);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(CryptCrawler.class, BipedCryptCrawler.class, ChiefCryptCrawler.class));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CRYPT_CRAWLER_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRYPT_CRAWLER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRYPT_CRAWLER_DEATH.get();
	}

	@Override
	public void aiStep() {
		if (!this.level().isClientSide()) {
			if (this.getTarget() != null) {
				double distance = this.distanceTo(this.getTarget());

				this.setIsStanding(distance <= 3.0D);
			}

			if (this.getTarget() == null) {
				this.setIsStanding(false);
			}
		} else {
			this.prevStandingAngle = this.standingAngle;

			if (this.standingAngle > 0 && !this.isStanding()) {
				this.standingAngle -= 0.1F;
			}

			if (this.isStanding() && this.standingAngle <= 1.0F) {
				this.standingAngle += 0.1F;
			}

			if (this.standingAngle < 0 && !this.isStanding()) {
				this.standingAngle = 0F;
			}

			if (this.isStanding() && this.standingAngle > 1.0F) {
				this.standingAngle = 1F;
			}

			this.standingAngle = Mth.clamp(this.standingAngle, 0, 1);

			if (this.prevStandingAngle != this.standingAngle) {
				this.refreshDimensions();
			}
		}

		super.aiStep();
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return EntityDimensions.scalable(0.95F - this.standingAngle * 0.2F, 1F + this.standingAngle * 0.75F);
	}

	public float smoothedStandingAngle(float partialTicks) {
		return this.prevStandingAngle + (this.standingAngle - this.prevStandingAngle) * partialTicks;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.tickCount < 40 && source.is(DamageTypes.IN_WALL)) {
			return false;
		}

		return super.hurt(source, amount);
	}
}
