package thebetweenlands.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.api.entity.MusicPlayer;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.LootUrnBlock;
import thebetweenlands.common.block.container.MudBrickAlcoveBlock;
import thebetweenlands.common.block.entity.MudBrickAlcoveBlockEntity;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.BarrisheeBreakBlockGoal;
import thebetweenlands.common.entity.ai.goals.BarrisheeSlamAttackGoal;
import thebetweenlands.common.entity.ai.goals.BarrisheeSonicAttackGoal;
import thebetweenlands.common.entity.monster.AshSprite;
import thebetweenlands.common.entity.movement.BarrisheePathNavigation;
import thebetweenlands.common.entity.movement.climb.PathObstructionAwareEntity;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.location.LocationGuarded;

import javax.annotation.Nullable;
import java.util.List;

public class Barrishee extends Monster implements ScreenShaker, BLEntity, PathObstructionAwareEntity, MusicPlayer {

	private static final EntityDataAccessor<Boolean> AMBUSH_SPAWNED = SynchedEntityData.defineId(Barrishee.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SCREAM = SynchedEntityData.defineId(Barrishee.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SCREAM_TIMER = SynchedEntityData.defineId(Barrishee.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> SCREAM_BEAM = SynchedEntityData.defineId(Barrishee.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SLAMMING_ANIMATION = SynchedEntityData.defineId(Barrishee.class, EntityDataSerializers.BOOLEAN);

	public float standingAngle, prevStandingAngle;

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	public int screamTimer;

	//Adjust to length of screaming sound
	private final int screamingTimerMax = 50;

	protected int actionCooldown = 0;

	public Barrishee(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 150;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(AMBUSH_SPAWNED, false);
		builder.define(SCREAM, false);
		builder.define(SCREAM_TIMER, 50);
		builder.define(SCREAM_BEAM, false);
		builder.define(SLAMMING_ANIMATION, false);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new BarrisheeBreakBlockGoal(this, 60, 100));
		this.goalSelector.addGoal(3, new BarrisheeSonicAttackGoal(this, 32, 50));
		this.goalSelector.addGoal(4, new BarrisheeSlamAttackGoal(this, 22, 40));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.8D, 50));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, null).setUnseenMemoryTicks(1200));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 200.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.24D)
			.add(Attributes.ATTACK_DAMAGE, 7.0D)
			.add(Attributes.FOLLOW_RANGE, 24.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.75D);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new BarrisheePathNavigation(this, level);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.BARRISHEE_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.BARRISHEE_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.BARRISHEE_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundRegistry.BARRISHEE_STEP.get(), 0.5F, 1F);
	}

	public SoundEvent getScreamSound() {
		return SoundRegistry.BARRISHEE_SCREAM.get();
	}

	public boolean isAmbushSpawn() {
		return this.getEntityData().get(AMBUSH_SPAWNED);
	}

	public void setIsAmbushSpawn(boolean is_ambush) {
		this.getEntityData().set(AMBUSH_SPAWNED, is_ambush);
	}

	public void setIsScreaming(boolean scream) {
		this.getEntityData().set(SCREAM, scream);
	}

	public boolean isScreaming() {
		return this.getEntityData().get(SCREAM);
	}

	public void setScreamTimer(int scream_timer) {
		this.getEntityData().set(SCREAM_TIMER, scream_timer);
	}

	public int getScreamTimer() {
		return this.getEntityData().get(SCREAM_TIMER);
	}

	public void setIsScreamingBeam(boolean scream_beam) {
		this.getEntityData().set(SCREAM_BEAM, scream_beam);
	}

	public boolean isScreamingBeam() {
		return this.getEntityData().get(SCREAM_BEAM);
	}

	public void setIsSlamming(boolean slamming) {
		this.getEntityData().set(SLAMMING_ANIMATION, slamming);
	}

	public boolean isSlamming() {
		return this.getEntityData().get(SLAMMING_ANIMATION);
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader level) {
		return !level.containsAnyLiquid(this.getBoundingBox()) && level.getEntityCollisions(this, this.getBoundingBox()).isEmpty() && this.level().isUnobstructed(this);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	public float getSmoothedStandingAngle(float partialTicks) {
		return this.prevStandingAngle + (this.standingAngle - this.prevStandingAngle) * partialTicks;
	}

	@Override
	public void aiStep() {
		if (this.level().isClientSide() && !this.isSlamming()) {
			this.prevStandingAngle = this.standingAngle;

			if (this.standingAngle <= 0.1F)
				this.standingAngle += this.isAmbushSpawn() ? 0.01F : 0.2F;
			if (this.standingAngle > 0.1F && this.standingAngle <= 1F)
				this.standingAngle += this.isAmbushSpawn() ? 0.1F : 0.2F;

			if (this.standingAngle > 1F) {
				this.standingAngle = 1F;

				if (this.isAmbushSpawn())
					this.setIsAmbushSpawn(false);
			}
		}

		if (this.level().isClientSide() && this.isSlamming()) {
			this.prevStandingAngle = this.standingAngle;

			if (this.standingAngle >= 0F && this.standingAngle <= 1F)
				this.standingAngle -= 0.2F;

			if (this.standingAngle < 0F) {
				this.setIsSlamming(false);
				this.standingAngle = 0F;
			}
		}

		this.prevScreamTimer = this.getScreamTimer();
		if (!this.level().isClientSide()) {
			if (this.getScreamTimer() == 0) {
				this.setIsScreaming(true);
				this.setScreamTimer(1);
			}

			if (this.getScreamTimer() == 1) {
				this.playSound(this.getScreamSound(), 0.75F, 0.5F);
			}

			if (this.getScreamTimer() > 0 && this.getScreamTimer() <= this.screamingTimerMax) {
				this.setScreamTimer(this.getScreamTimer() + 1);
			}

			this.setIsScreaming(this.getScreamTimer() < this.screamingTimerMax);

			if (this.getTarget() != null) {
				this.getLookControl().setLookAt(this.getTarget(), 100F, 100F);

				if (this.isScreaming()) {
					this.getNavigation().moveTo(this.getTarget(), 0);
				}
			}
		}

		if (this.level().isClientSide() && this.isScreamingBeam()) {
			this.spawnScreamParticles();
		}

		if (!this.level().isClientSide() && this.isScreaming() && !this.isScreamingBeam() && this.getScreamTimer() >= 25) {
			this.breakBlocksForAOEScream(this.getAOEScreamBounds());
		}

		if (!this.level().isClientSide()) {
			if (this.isDoingSpecialAttack()) {
				this.actionCooldown = 60;
			} else if (this.actionCooldown > 0) {
				this.actionCooldown--;
			}
		}

		super.aiStep();
	}

	@Override
	public void tick() {
		super.tick();

		this.pushEntitiesAway();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Nullable
	public AABB getCollisionBox(Entity entityIn) {
		if (entityIn.isPickable()) {
			return entityIn.getBoundingBox();
		}

		return null;
	}

	protected void pushEntitiesAway() {
		AABB collisionAABB = this.getBoundingBox().move(this.getDeltaMovement());
		if (collisionAABB != null) {
			List<Entity> entities = this.level().getEntities(this, collisionAABB);

			for (Entity entity : entities) {
				if (entity.canBeCollidedWith() && entity.isPushable()) {
					AABB entityAABB = this.getCollisionBox(entity);

					if (entityAABB != null) {
						double dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
						double dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

						if (Math.abs(dz) < Math.abs(dx)) {
							entity.move(MoverType.PISTON, new Vec3(0, 0, (dz - 0.005D) * Math.signum(this.getZ() - entity.getZ())));
						} else {
							entity.move(MoverType.PISTON, new Vec3((dx - 0.005D) * Math.signum(this.getX() - entity.getX()), 0, 0));
						}

						//Move slightly towards ground to update onGround state etc.
						entity.move(MoverType.PISTON, new Vec3(0, -0.01D, 0));
					}
				}
			}
		}
	}

	private void breakBlocksForAOEScream(AABB aoeScreamBounds) {
		if (EventHooks.canEntityGrief(this.level(), this)) {
			int minX = Mth.floor(aoeScreamBounds.minX);
			int minY = Mth.floor(aoeScreamBounds.minY);
			int minZ = Mth.floor(aoeScreamBounds.minZ);
			int maxX = Mth.floor(aoeScreamBounds.maxX);
			int maxY = Mth.floor(aoeScreamBounds.maxY);
			int maxZ = Mth.floor(aoeScreamBounds.maxZ);

			for (int sizeX = minX; sizeX <= maxX; ++sizeX) {
				for (int sizeZ = minZ; sizeZ <= maxZ; ++sizeZ) {
					for (int sizeY = minY; sizeY <= maxY; ++sizeY) {
						BlockPos pos = new BlockPos(sizeX, sizeY, sizeZ);
						BlockState state = this.level().getBlockState(pos);

						if (state.getBlock() instanceof MudBrickAlcoveBlock && this.checkAlcoveForUrn(state)) {
							this.setAlcoveUrnEmpty(this.level(), pos, state);
						}

						if (state.getDestroySpeed(this.level(), pos) >= 0 && state.canEntityDestroy(this.level(), pos, this)) {
							if (state.getBlock() instanceof LootUrnBlock) {
								this.spawnAshSpriteMinion(this.level(), pos, state);
								this.level().destroyBlock(pos, true);
								//TODO add back once LocalStorage or some other alternative exists
							} else if (/*!LocationGuarded.isLocationGuarded(this.level(), this, pos) &&*/ EventHooks.onEntityDestroyBlock(this, pos, state)) {
								this.level().destroyBlock(pos, true);
							}
						}
					}
				}
			}
		}
	}

	private void setAlcoveUrnEmpty(Level level, BlockPos pos, BlockState state) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof MudBrickAlcoveBlockEntity alcove) {
			Direction facing = state.getValue(MudBrickAlcoveBlock.FACING);
			Containers.dropContents(level, pos.relative(facing), alcove);
			this.spawnAshSpriteMinion(level, pos, state);
			level.levelEvent(null, 2001, pos, Block.getId(state));
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}

	private void spawnAshSpriteMinion(Level level, BlockPos pos, BlockState state) {
		BlockPos offsetPos = pos;
		if (state.getBlock() instanceof MudBrickAlcoveBlock) {
			Direction facing = state.getValue(MudBrickAlcoveBlock.FACING);
			offsetPos = pos.relative(facing);
		}
		AshSprite entity = new AshSprite(EntityRegistry.ASH_SPRITE.get(), level);
		entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
		entity.setBoundOrigin(offsetPos);
		level.addFreshEntity(entity);
	}

	private boolean checkAlcoveForUrn(BlockState state) {
		return state.getValue(MudBrickAlcoveBlock.HAS_URN);
	}

	protected void spawnScreamParticles() {
		Vec3 look = this.getViewVector(1.0F).normalize();
		float speed = 0.6f;
		TheBetweenlands.createParticle(ParticleRegistry.SONIC_SCREAM.get(), this.level(), this.getX(), this.getY() + (this.getScreamTimer() < 25 ? 0.8 + (this.getScreamTimer() * 0.0125F) : 1.25 - (25 - this.getScreamTimer()) * 0.025F), this.getZ(),
			ParticleFactory.ParticleArgs.get().withMotion(look.x * speed, look.y * speed, look.z * speed).withScale(10).withData(30, Mth.floor(this.tickCount * 3.3f))
				.withColor(1.0f, 0.9f, 0.8f, 1.0f));
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING, particle);
	}

	public boolean isLookingAtAttackTarget(Entity entity) {
		Vec3 Vec3 = this.getViewVector(1.0F).normalize();
		Vec3 Vec31 = new Vec3(entity.getX() - this.getX(), entity.getBoundingBox().minY + (double) entity.getEyeHeight() - (this.getY() + (double) this.getEyeHeight()), entity.getZ() - this.getZ());
		double d0 = Vec31.length();
		Vec31 = Vec31.normalize();
		double d1 = Vec3.dot(Vec31);
		return d1 > 1.0D - 0.025D / d0 && this.hasLineOfSight(entity);
	}

	public float getScreamingProgress() {
		return 1.0F / this.screamingTimerMax * (this.prevScreamTimer + (this.screamTimer - this.prevScreamTimer));
	}

	public AABB getAOEScreamBounds() {
		float boxsizeUnit = 0.0275F * getScreamTimer();
		AABB bounds = this.getBoundingBox();
		return bounds.inflate(boxsizeUnit, 0, boxsizeUnit).expandTowards(0, 1, 0);
	}

	public boolean isDoingSpecialAttack() {
		return this.isScreaming() || this.isScreamingBeam() || this.isSlamming();
	}

	public boolean isReadyForSpecialAttack() {
		if (this.isDoingSpecialAttack()) {
			return false;
		} else {
			return this.actionCooldown <= 0;
		}
	}

	/**
	 * Called by barrishee path navigator if a path is obstructed and the barrishee becomes stuck
	 */
	@Override
	public void onPathingObstructed(Direction facing) {
		if (this.getTarget() != null && this.isReadyForSpecialAttack()) {
			this.setScreamTimer(0);
		}
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (isScreaming()) {
			double dist = this.distanceTo(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if (dist >= 30.0F) {
				return 0.0F;
			}
			return (Mth.sin(this.getScreamingProgress() * Mth.PI) + 0.1F) * 0.15F * screamMult;
		} else {
			return 0.0F;
		}
	}

	@Override
	public SoundEvent getMusicFile(Player listener) {
		return SoundRegistry.BARRISHEE_THEME.get();
	}

	@Override
	public double getMusicRange(Player listener) {
		return 32.0D;
	}

	@Override
	public boolean isMusicActive(Player listener) {
		return this.isAlive();
	}

	@Override
	public int getMusicLayer(Player listener) {
		return EntityMusicLayers.BOSS;
	}
}
