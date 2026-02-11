package thebetweenlands.common.entity.monster.wall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import thebetweenlands.common.entity.multipart.GenericPartEntity;
import thebetweenlands.common.entity.multipart.ShamblerTongueMultipart;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WallRoot extends AbstractMovingWallCreature implements Enemy {

	public static class ArmSegment {
		public Vec3 motion = Vec3.ZERO;

		@Nullable
		public Vec3 prevPos, pos;

		public final float[] offsetX, offsetY, offsetZ;

		private final float[][] armCrossSection;

		public ArmSegment(WallRoot root) {
			this.armCrossSection = root.getArmCrossSection();
			this.offsetX = new float[this.armCrossSection.length];
			this.offsetY = new float[this.armCrossSection.length];
			this.offsetZ = new float[this.armCrossSection.length];
		}

		public void updatePrev() {
			this.prevPos = Objects.requireNonNullElse(this.pos, Vec3.ZERO);
		}

		public void update(Vec3 quadUp, Vec3 pos, Vec3 dir) {
			this.pos = pos;

			Vec3 right = dir.cross(quadUp).normalize();
			Vec3 up = right.cross(dir).normalize();

			int i = 0;
			for (float[] hullCrossSection : this.armCrossSection) {
				float hullX = hullCrossSection[0];
				float hullY = hullCrossSection[1];

				this.offsetX[i] = (float) (right.x * hullX + up.x * hullY);
				this.offsetY[i] = (float) (right.y * hullX + up.y * hullY);
				this.offsetZ[i] = (float) (right.z * hullX + up.z * hullY);

				i++;
			}
		}
	}

	private static final EntityDataAccessor<Vector3f> REL_TIP = SynchedEntityData.defineId(WallRoot.class, EntityDataSerializers.VECTOR3);

	private boolean rootTipPositionSet = false;

	public GenericPartEntity<?> rootTip;
	protected GenericPartEntity<?>[] parts;

	private Direction segmentsFacing = Direction.NORTH;

	public List<ArmSegment> armSegments = new ArrayList<>();

	public final WallSpriteInfo info = new WallSpriteInfo();

	protected int armMovementTicks;

	public WallRoot(EntityType<? extends AbstractMovingWallCreature> type, Level level) {
		super(type, level);

		this.lookMoveSpeedMultiplier = 8.0F;
		this.xpReward = 7;
		this.createParts();
	}

	public float getArmLength() {
		return 2.5F;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.parts.length; i++)
			this.parts[i].setId(id + i + 1);
	}

	protected void createParts() {
		this.parts = new GenericPartEntity<?>[this.getNumSegments() + 1];
		this.parts[0] = this.rootTip = new GenericPartEntity<>(this, this.getNodeSize(0), this.getNodeSize(0));
		for (int i = 0; i < this.getNumSegments(); i++) {
			this.parts[i + 1] = new GenericPartEntity<>(this, this.getNodeSize(this.getNumSegments() - i + 1), this.getNodeSize(this.getNumSegments() - i + 1));
		}
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.parts;
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	protected float getNodeSize(int node) {
		return 0.3F;
	}

	protected float[][] getArmCrossSection() {
		float width = this.getFullArmWidth();
		return new float[][]{
			{-width, width},
			{-width, -width},
			{width, -width},
			{width, width},
		};
	}

	protected int getNumSegments() {
		return 8;
	}

	protected float getFullArmWidth() {
		return 0.2F;
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.armMovementTicks = level.getRandom().nextInt(10000);
		for (PartEntity<?> part : this.getParts()) {
			part.setPos(this.getX(), this.getY(), this.getZ());
			part.setYRot(this.getYRot());
		}
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(REL_TIP, new Vector3f());
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new TrackTargetGoal<>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
		this.goalSelector.addGoal(1, new SwingArmGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this) {
			@Override
			protected double getFollowDistance() {
				return 8.0D;
			}
		});
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, null).setUnseenMemoryTicks(120));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.08D)
			.add(Attributes.ATTACK_DAMAGE, 2.5D);
	}

	public Vec3 getTipPos() {
		Vector3f tipData = this.getEntityData().get(REL_TIP);
		return new Vec3(this.getX() + tipData.x() / 512.0f, this.getY() + tipData.y() / 512.0f, this.getZ() + tipData.z() / 512.0f);
	}

	public void setTipPos(Vec3 pos) {
		this.getEntityData().set(REL_TIP, new Vector3f((float) ((pos.x - this.getX()) * 512.0F), (float) ((pos.y - this.getY()) * 512.0F), (float) ((pos.z - this.getZ()) * 512.0F)));
	}

	protected Vec3 updateTargetTipPos(Vec3 armStartWorld, float maxArmLength, Vec3 dirFwd, Vec3 dirUp) {
		float flailingStrength = this.swinging ? (1 - this.attackAnim) : this.hurtTime > 0 ? (this.hurtTime / (float) this.hurtDuration) * 0.5F : 0.0f;

		this.armMovementTicks += 1 + (int) (flailingStrength * 10);

		float idleX = Mth.cos(this.armMovementTicks / 9.0f) * 0.75F;
		float idleY = Mth.sin(this.armMovementTicks / 7.0f) * 0.75F;
		float idleZ = (Mth.cos(this.armMovementTicks / 15.0f) + 1) * 0.25f;

		Vec3 targetTipPos = armStartWorld.add(dirFwd.scale(maxArmLength));

		LivingEntity target = this.getTarget();
		if (target != null) {
			targetTipPos = target.position().add(0, target.getBbHeight() / 2, 0);
		}

		float forwardPos = (float) dirFwd.dot(targetTipPos.subtract(armStartWorld));
		float offsetZ = 0.0f;
		if (forwardPos < 1.0F) {
			offsetZ = 1.0F - forwardPos;
		}

		//Idle movement
		targetTipPos = targetTipPos.add(dirUp.scale(idleY)).add(dirFwd.cross(dirUp).scale(idleX)).add(dirFwd.scale(offsetZ - idleZ));

		Vec3 tipPos = this.rootTip.position();

		Vec3 tipDiff = targetTipPos.subtract(tipPos);
		targetTipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), 0.1D + flailingStrength * 0.9D)));

		return targetTipPos;
	}

	protected float getArmLengthSlack() {
		return 0.0f;
	}

	@Override
	public void tick() {
		for (GenericPartEntity<?> part : this.parts) part.setOldPosAndRot();
		super.tick();

		float maxArmLength = this.getArmLength() * this.getArmSize(1);

		float segmentLength = maxArmLength / (float) (this.getNumSegments() - 2);

		Vec3 dirFwd = new Vec3(this.getFacing().step());
		Vec3 dirUp = new Vec3(this.getFacingUp().step());

		Vec3 armStart = new Vec3(0, this.getBbHeight() / 2, 0).add(-dirFwd.x * (this.getBbWidth() / 2 - 0.1f), -dirFwd.y * (this.getBbHeight() / 2 - 0.1f), -dirFwd.z * (this.getBbWidth() / 2 - 0.1f));
		Vec3 ikArmStart = new Vec3(0, this.getBbHeight() / 2, 0).add(dirFwd.scale(0.1f));

		if (!this.rootTipPositionSet) {
			Vec3 tipPos = this.position().add(armStart.add(dirFwd.scale(maxArmLength)).add(0, -this.rootTip.getBbHeight() / 2, 0));
			this.setTipPos(tipPos);
			this.rootTip.setPos(tipPos.x, tipPos.y, tipPos.z);
			this.rootTipPositionSet = true;
		}

		Vec3 armEnd = this.rootTip.position().add(0, this.rootTip.getBbHeight() / 2, 0).subtract(this.position());

		if (!this.level().isClientSide()) {
			Vec3 armStartWorld = this.position().add(ikArmStart);

			Vec3 tipPos = this.updateTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);

			//Clamp to max reach sphere
			tipPos = armStartWorld.add(tipPos.subtract(armStartWorld).normalize().scale(Math.min(tipPos.subtract(armStartWorld).length(), maxArmLength + this.getArmLengthSlack())));

			this.setTipPos(tipPos);
			this.rootTip.setPos(tipPos.x, tipPos.y, tipPos.z);
		} else {
			Vec3 tipPos = this.getTipPos();
			this.rootTip.setPos(tipPos.x, tipPos.y, tipPos.z);

			this.info.updateWallSprite(this);
		}

		if (this.armSegments.size() != this.getNumSegments() || this.getFacing() != this.segmentsFacing) {
			this.armSegments.clear();

			for (int i = 0; i < this.getNumSegments(); i++) {
				ArmSegment segment = new ArmSegment(this);
				float dist = maxArmLength / (float) (this.getNumSegments() - 1) * i;
				segment.update(dirUp, ikArmStart.add(dirFwd.x * dist, dirFwd.y * dist, dirFwd.z * dist), dirFwd);
				this.armSegments.add(segment);
			}

			this.segmentsFacing = this.getFacing();
		}

		for (ArmSegment segment : this.armSegments) {
			segment.updatePrev();

			segment.pos = segment.pos.add(segment.motion);
		}

		for(int i = this.getNumSegments() - 2; i >= 2; i--) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3 target;
			if(i == this.getNumSegments() - 2) {
				target = armEnd;
			} else {
				target = this.armSegments.get(i + 1).pos;
			}

			Vec3 dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		for(int i = 2; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3 target;
			if(i == 0) {
				target = ikArmStart;
			} else {
				target = this.armSegments.get(i - 1).pos;
			}

			Vec3 dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		ArmSegment startSegment = this.armSegments.getFirst();
		startSegment.update(dirUp, armStart, new Vec3(-dirFwd.x, -dirFwd.y, -dirFwd.z));

		ArmSegment startSegment2 = this.armSegments.get(1);
		startSegment2.update(dirUp, ikArmStart, new Vec3(-dirFwd.x, -dirFwd.y, -dirFwd.z));
//
		ArmSegment endSegment = this.armSegments.getLast();
		endSegment.update(dirUp, armEnd, this.armSegments.get(this.armSegments.size() - 2).pos.subtract(armEnd).normalize());

		for (int i = 0; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);
			Vec3 pos = segment.pos;
			this.parts[i + 1].setPos(this.getX() + pos.x, this.getY() + pos.y - this.parts[i + 1].getBbHeight() / 2.0f, this.getZ() + pos.z);
		}
	}

	@Override
	protected void updateMovement() {
		if (!this.level().isClientSide() && this.isMoving() && this.getMoveReason() != MoveReason.LOOK) {
			boolean wasFirstHalf = this.getMovementProgress(1) < 0.5F;

			super.updateMovement();

			if (this.getMovementProgress(1) >= 0.5F && wasFirstHalf) {
				this.level().playSound(null, this.blockPosition(), SoundRegistry.WALL_LIVING_ROOT_EMERGE.get(), SoundSource.HOSTILE, 1, 1);
			}
		} else {
			super.updateMovement();
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.armMovementTicks = tag.getInt("arm_ticks");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("arm_ticks", this.armMovementTicks);
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	protected @Nullable SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.LIVING_ROOT_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LIVING_ROOT_DEATH.get();
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.isValidBlockForMovement(pos, this.level().getBlockState(pos));
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return state.isCollisionShapeFullBlock(this.level(), pos) && state.getDestroySpeed(this.level(), pos) > 0;
	}

	@Override
	public Vec3 getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getArmSize(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.getWeaponItem() != null && source.getWeaponItem().is(ItemTags.AXES)) {
			amount *= 2.0F;
		}
		return super.hurt(source, amount);
	}

	protected static class SwingArmGoal extends Goal {
		protected final WallRoot entity;
		protected int attackTicks;

		public SwingArmGoal(WallRoot entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			return this.entity.getTarget() != null;
		}

		@Override
		public void tick() {
			Entity target = this.entity.getTarget();

			if (this.attackTicks > 0) {
				this.attackTicks--;
			} else if (target != null && target.getBoundingBox().intersects(this.entity.rootTip.getBoundingBox())) {
				this.entity.doHurtTarget(target);
				this.entity.swing(InteractionHand.MAIN_HAND);
				this.attackTicks = 20;
			}
		}
	}
}
