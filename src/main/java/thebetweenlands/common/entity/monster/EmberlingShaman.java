package thebetweenlands.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.GenericPartEntity;
import thebetweenlands.common.entity.ai.goals.ShamanFireColumnGoal;
import thebetweenlands.common.entity.ai.goals.ShamanHoverSpinAttackGoal;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class EmberlingShaman extends Monster {

	public final PartEntity<?> tailPart;
	private static final EntityDataAccessor<Boolean> IS_CASTING_SPELL = SynchedEntityData.defineId(EmberlingShaman.class, EntityDataSerializers.BOOLEAN);
	public float animationTicks, prevAnimationTicks;

	public EmberlingShaman(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.tailPart = new GenericPartEntity<>(this, 0.5F, 0.5F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.5D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D)
			.add(Attributes.STEP_HEIGHT, 1.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ShamanHoverSpinAttackGoal(this, 0.6F));
		this.goalSelector.addGoal(2, new ShamanFireColumnGoal(this));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.65D, false));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.4D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_CASTING_SPELL, false);
	}

	@Override
	public PartEntity<?>[] getParts() {
		return new PartEntity<?>[]{this.tailPart};
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	public void setCastingSpell(boolean spell) {
		this.getEntityData().set(IS_CASTING_SPELL, spell);
	}

	public boolean isCastingSpell() {
		return this.getEntityData().get(IS_CASTING_SPELL);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.EMBERLING_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.EMBERLING_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.EMBERLING_DEATH.get();
	}

	@Override
	public void tick() {
		super.tick();
		this.yBodyRot = this.getYRot();
		double a = Math.toRadians(this.getYRot());
		double offSetX = -Math.sin(a) * 1.5D;
		double offSetZ = Math.cos(a) * 1.5D;
		this.tailPart.moveTo(this.getX() - offSetX, this.getY(), this.getZ() - offSetZ, 0.0F, 0.0F);

		if (this.level().getGameTime() % 5 == 0)
			if (this.level().isClientSide())
				this.flameParticles(this.level(), this.tailPart.getX(), this.tailPart.getY() + 0.25, this.tailPart.getZ(), this.getRandom());

		this.checkCollision();

		if (this.level().isClientSide()) {
			if (this.isCastingSpell()) {
				this.prevAnimationTicks = this.animationTicks;

				if (this.animationTicks <= 1F)
					this.animationTicks += 0.1F;

			} else {
				this.prevAnimationTicks = this.animationTicks;

				if (this.animationTicks >= 0.1F)
					this.animationTicks -= 0.1F;

				if (this.animationTicks < 0F)
					this.animationTicks = 0F;
			}
		}
		if (!this.level().isClientSide() && this.lastHurtByPlayerTime > 40)
			if (this.isCastingSpell())
				this.setCastingSpell(false);

		if (this.level().isClientSide()) {
			if (this.getRandom().nextInt(4) == 0) {
				ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				args.withColor(1F, 1F, 1F, 1F);
				args.withScale(0.75F + this.getRandom().nextFloat() * 0.75F);
				TheBetweenlands.createParticle(new EntitySwirlParticleOptions(ParticleRegistry.EMBER_SWIRL.get(), new Vec3(0, -0.6D, 0), Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, 4.0F, false), this.level(), this.getX(), this.getY(), this.getZ(), args);
			}
		}
	}

	public float smoothedAngle(float partialTicks) {
		return this.prevAnimationTicks + (this.animationTicks - this.prevAnimationTicks) * partialTicks;
	}

	public void flameParticles(Level level, double x, double y, double z, RandomSource rand) {
		for (int count = 0; count < 3; ++count) {
			int motionX = rand.nextBoolean() ? 1 : -1;
			int motionZ = rand.nextBoolean() ? 1 : -1;
			double velY = rand.nextFloat() * 0.05D;
			double velZ = rand.nextFloat() * 0.025D * motionZ;
			double velX = rand.nextFloat() * 0.025D * motionX;
			if (this.tailPart.isUnderWater()) {
				level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE, x, y, z, velX, velY, velZ);
				level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, x, y, z, velX, velY, velZ);
			} else {
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, x, y, z, velX, velY, velZ);
			}
		}
	}

	protected void checkCollision() {
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.tailPart.getBoundingBox());
		for (LivingEntity entity : list) {
			if (entity != null && entity == this.getTarget()) {
				this.doHurtTarget(entity);
				entity.push(-Mth.sin(this.tailPart.getYRot() * Mth.DEG_TO_RAD) * 0.5D, 0.3D, Mth.cos(this.tailPart.getYRot() * Mth.DEG_TO_RAD) * 0.5D);
				entity.igniteForSeconds(5); // randomise or something?
			}
		}
	}
}
