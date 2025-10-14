package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SludgeWorm extends Monster implements BLEntity {

	public SludgeWormMultipart[] parts;

	private AABB renderBoundingBox;
	private int wallInvulnerabilityTicks = 40;
	private final boolean doSpawningAnimation = true;

	public SludgeWorm(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, -10.0F);
		this.assignParts();
		this.renderBoundingBox = this.getBoundingBox();
	}

	protected void assignParts() {
		this.parts = new SludgeWormMultipart[]{
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F),
			new SludgeWormMultipart(this, 0.3125F, 0.3125F)
		};
		this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.parts.length; i++)
			this.parts[i].setId(id + i + 1);
	}

	@Override
	public PartEntity<?>[] getParts() {
		return parts;
	}

	/*
	 * Context for this function: in 1.12.2, there used to be one more subpart on every single
	 * sludge worm, and it would always share the exact same position and orientation as the head.
	 * This part would always be index 0 in the part array.
	 * In the port, that part has been removed, instead opting to use the actual parent entity in
	 * its place.
	 */
	public Entity getPartOrSelf(int i) {
		if(i-- == 0) return this;
		return this.parts[i];
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D, 1));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> !(entity instanceof Enemy)));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Animal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 10.0D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.21D)
			.add(Attributes.ATTACK_DAMAGE, 1.25D);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.setZza(0.2F);
		this.setHitBoxes();
	}

	protected float getHeadMotionYMultiplier() {
		return this.doSpawningAnimation && this.tickCount < 20 ? 0.65F : 1.0F;
	}

	protected float getTailMotionYMultiplier() {
		return this.doSpawningAnimation && this.tickCount < 20 ? 0.0F : 1.0F;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide() && this.tickCount % 10 == 0) {
			this.spawnParticles(this.level(), this.xo, this.yo, this.zo, this.getRandom());
		}

		if (this.wallInvulnerabilityTicks > 0) {
			this.wallInvulnerabilityTicks--;
		}

		Vec3 vec3 = this.getDeltaMovement();
		this.setDeltaMovement(vec3.multiply(1.0D, this.getHeadMotionYMultiplier(), 1.0D));

		this.renderBoundingBox = this.getBoundingBox();
		for(SludgeWormMultipart part : this.parts) {
			this.renderBoundingBox = this.renderBoundingBox.minmax(part.getBoundingBox());
		}
	}

	public void spawnParticles(Level level, double x, double y, double z, RandomSource rand) {
		for (int count = 0; count < 1 + level.getRandom().nextInt(4); ++count) {
			double a = Math.toRadians(this.yBodyRot);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), level , x + offSetX, y, z + offSetZ, ParticleFactory.ParticleArgs.get().withColor(0xFF694628));
		}
	}

	// can be set to any part(s) - dunno if we want this either
	public boolean hurtSegment(SludgeWormMultipart part, DamageSource source, float dmg) {
		this.damageWorm(source, dmg * 0.75F);
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.THORNS)) {
			return this.damageWorm(source, amount);
		} else if (source.is(DamageTypes.IN_WALL) && this.wallInvulnerabilityTicks > 0) {
			return false;
		}
		return this.damageWorm(source, amount);
	}

	protected boolean damageWorm(DamageSource source, float amount) {
		return super.hurt(source, amount);
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return !(type instanceof BLEntity) && type != EntityRegistry.SLUDGE_WORM_EGG_SAC.get();
	}

	private void setHitBoxes() {
		if (this.tickCount == 1) {
			for (SludgeWormMultipart part : this.parts) {
				part.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
			}
		}

		for (SludgeWormMultipart part : this.parts) {
			part.setOldPosAndRot();
			if (part.getY() < this.getY() && this.level().collidesWithSuffocatingBlock(part, part.getBoundingBox())) {
				part.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
				part.setDeltaMovement(part.getDeltaMovement().x(), 0.0D, part.getDeltaMovement().z());
			}

			part.move(MoverType.SELF, new Vec3(0.0D, part.getDeltaMovement().y(), 0.0D));

			part.setDeltaMovement(part.getDeltaMovement().subtract(0.0D, 0.08D, 0.0D).multiply(1.0D, 0.98D * this.getTailMotionYMultiplier(), 1.0D));
		}

		for (int i = 0; i < this.parts.length; i++) {
			this.movePiecePos(this, this.parts[i], i > 0 ? this.parts[i - 1] : this, 4.5F, 2F);
		}
	}

	protected double getMaxPieceDistance() {
		return 0.3D;
	}

	public void movePiecePos(SludgeWorm sludgeWorm, SludgeWormMultipart targetPart, Entity destinationPart, float speed, float yawSpeed) {
		//TODO make this better and use the parent entities motionY

		if (destinationPart.getY() - targetPart.getY() < -0.5D)
			speed = 1.5F;

		double movementTolerance = 0.05D;
		double maxDist = this.getMaxPieceDistance();
		boolean correctY = false;

		for (int i = 0; i < 5; i++) {
			Vec3 diff = destinationPart.position().subtract(targetPart.position());
			double len = diff.length();

			if (len > maxDist) {
				Vec3 correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.absMoveTo(targetPart.getX() + correction.x, targetPart.getY(), targetPart.getZ() + correction.z);

				double cy = targetPart.getY();
				targetPart.move(MoverType.SELF, new Vec3(0D, correction.y, 0D));

				if (Math.abs((targetPart.getY() - cy) - correction.y) <= movementTolerance) {
					correctY = true;
					break;
				}
			}
		}

		//Welp, failed to move smoothly along Y, just clip
		if (!correctY) {
			Vec3 diff = destinationPart.position().subtract(targetPart.position());
			double len = diff.lengthSqr();

			if (len > maxDist) {
				Vec3 correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.absMoveTo(targetPart.getX() + correction.x, targetPart.getY() + correction.y, targetPart.getZ() + correction.z);
			}
		}

		Vec3 diff = new Vec3(destinationPart.getX(), 0, destinationPart.getZ()).subtract(new Vec3(targetPart.getX(), 0, targetPart.getZ()));
		float destYaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90;
		float yawDiff = (destYaw - targetPart.getYRot()) % 360.0F;
		float yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		targetPart.setYRot(targetPart.getYRot() + (yawInterpolant / yawSpeed));
		targetPart.setXRot(0.0F);
		targetPart.absMoveTo(targetPart.getX(), targetPart.getY(), targetPart.getZ());
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WORM_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.WORM_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WORM_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundRegistry.WORM_LIVING.get(), 0.5F, 1.0F);
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.renderBoundingBox;
	}
}
