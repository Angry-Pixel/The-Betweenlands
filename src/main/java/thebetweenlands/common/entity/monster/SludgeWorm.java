package thebetweenlands.common.entity.monster;


import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class SludgeWorm extends PathfinderMob implements BLEntity {

	public SludgeWormMultipart[] parts;

	private int wallInvulnerabilityTicks = 40;
	private boolean doSpawningAnimation = true;

	public SludgeWorm(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		setPathfindingMalus(PathType.WATER, -10F);
		parts = new SludgeWormMultipart[] {
				new SludgeWormMultipart(this, "body_part_1", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_2", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_3", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_4", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_5", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_6", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "body_part_7", 0.3125F, 0.3125F),
				new SludgeWormMultipart(this, "tail", 0.3125F, 0.3125F) };
		setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}
	
/*	public SludgeWorm(EntityType<? extends PathfinderMob> type, Level level, boolean doSpawningAnimation) {
		this(type, level);
		this.doSpawningAnimation = doSpawningAnimation;
	}
*/

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++)
            this.parts[i].setId(id + i + 1);
    }

	@Override
	public @Nullable PartEntity<?>[] getParts() {
		return parts;
	}

    public SludgeWormMultipart[] getSubEntities() {
        return this.parts;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
		goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D, 1));
		targetSelector.addGoal(0, new HurtByTargetGoal(this));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> entity instanceof Monster == false));
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
		setHitBoxes();
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
		
		if(this.level().isClientSide() && this.tickCount % 10 == 0) {
	//TODO		this.spawnParticles(this.level(), this.xo, this.yo, this.zo, this.rand);
		}

		if(this.wallInvulnerabilityTicks > 0) {
			this.wallInvulnerabilityTicks--;
		}
		
		Vec3 vec3 = this.getDeltaMovement();
		this.setDeltaMovement(vec3.multiply(1.0D, this.getHeadMotionYMultiplier(), 1.0D));
	 
	}
/*
	//TODO Particles 
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles(Level level, double x, double y, double z, Random rand) {
		for (int count = 0; count < 1 + level.rand.nextInt(4); ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			BLParticles.TAR_BEAST_DRIP.spawn(level , x + offSetX, y, z + offSetZ).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		}
	}
*/
	// can be set to any part(s) - dunno if we want this either
	public boolean hurt(SludgeWormMultipart part, DamageSource source, float dmg) {
		damageWorm(source, dmg * 0.75F);
		return true;
	}

    @SuppressWarnings("unlikely-arg-type")
	@Override
    public boolean hurt(DamageSource source, float amount) {
		if (source.type().equals(DamageTypes.FELL_OUT_OF_WORLD) || source.type().equals(DamageTypes.THORNS))
			damageWorm(source, amount);
		else if(source.type().equals(DamageTypes.IN_WALL) && this.wallInvulnerabilityTicks > 0)
			return false;
		else
			damageWorm(source, amount);
		return true;
	}

	@Override
	public boolean canAttackType(EntityType<?> typeIn) {
		return !(typeIn instanceof BLEntity); // && typeIn != EntityRegistry.TINY_WORM_EGG_SAC.get());
	}

	protected boolean damageWorm(DamageSource source, float amount) {
		return super.hurt(source, amount);
	}

	private void setHitBoxes() {
		for (SludgeWormMultipart part : this.parts) {
			part.yRotO = part.getYRot();
			part.xRotO = part.getXRot();
			part.xOld = part.xo;
			part.yOld = part.yo;
			part.zOld = part.zo;
			Vec3 vec3 = part.getDeltaMovement();
			if (part.yo < this.yo && level().collidesWithSuffocatingBlock(part, part.getBoundingBox()))
				part.setDeltaMovement(vec3.add(0.0D, 0.1D, 0.0D));

			double motionY = vec3.y;
			motionY -= 0.08D;
			part.setDeltaMovement(vec3.add(0D, motionY, 0D));
			motionY *= 0.98D * this.getTailMotionYMultiplier();
			part.setDeltaMovement(vec3.add(0D, motionY, 0D));
		}

		for(int i = 0; i < this.parts.length; i++) {
			this.movePiecePos(this, this.parts[i], i > 0 ? this.parts[i - 1] : this, 4.5F, 2F);
		}
	}

	protected double getMaxPieceDistance() {
		return 0.3D;
	}

	public void movePiecePos(SludgeWorm sludgeWorm, SludgeWormMultipart targetPart, Entity destinationPart, float speed, float yawSpeed) {
		//TODO make this better and use the parent entities motionY 

		if (destinationPart.yo - targetPart.yo < -0.5D)
			speed = 1.5F;

		double movementTolerance = 0.05D;
		double maxDist = this.getMaxPieceDistance();
		boolean correctY = false;

		for(int i = 0; i < 5; i++) {
			Vec3 diff = destinationPart.position().subtract(targetPart.position());
			double len = diff.length();

			if(len > maxDist) {
				Vec3 correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.xo += correction.x;
				targetPart.yo += correction.y; // this?
				targetPart.zo += correction.z;
				targetPart.setPos(targetPart.xo, targetPart.yo, targetPart.zo);

				double cy = targetPart.yo;
				Vec3 vec3 = targetPart.getDeltaMovement();
				targetPart.setDeltaMovement(vec3.add(0D, correction.y, 0D));

				if(Math.abs((targetPart.yo - cy) - correction.y) <= movementTolerance) {
					correctY = true;
					break;
				}
			}
		}

		//Welp, failed to move smoothly along Y, just clip
		if(!correctY) {
			Vec3 diff = destinationPart.position().subtract(targetPart.position());
			double len = diff.lengthSqr();

			if(len > maxDist) {
				Vec3 correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.xo += correction.x;
				targetPart.yo += correction.y;
				targetPart.zo += correction.z;
			}
		}

		Vec3 diff = new Vec3(destinationPart.xo, 0, destinationPart.zo).subtract(new Vec3(targetPart.xo, 0, targetPart.zo));
		float destYaw = (float)Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90;
		double yawDiff = (destYaw - targetPart.getYRot()) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float rotationYaw = targetPart.getYRot();
		rotationYaw += yawInterpolant / yawSpeed;
		targetPart.setYRot(rotationYaw);
		targetPart.setXRot(0F);
		targetPart.setPos(targetPart.xo, targetPart.yo, targetPart.zo);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WORM_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.WORM_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WORM_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundRegistry.WORM_LIVING.get(), 0.5F, 1F);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		for (int i = 0; i < this.parts.length; i++) {
			this.parts[i].setPos(xo, yo, zo);
			this.parts[i].setYRot(getYRot());
		}
		return spawnGroupData;
	}

}
