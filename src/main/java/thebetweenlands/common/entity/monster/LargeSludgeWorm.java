package thebetweenlands.common.entity.monster;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ReparameterizedSpline;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LargeSludgeWorm extends SludgeWorm {

	private static final EntityDataAccessor<Float> EGG_SAC_PERCENTAGE = SynchedEntityData.defineId(LargeSludgeWorm.class, EntityDataSerializers.FLOAT);

	public boolean segmentsAvailable = false;

	@Nullable
	public ReparameterizedSpline spineySpliney;

	public HullSegment[] segments;
	public final List<SpineBone> bones = new ArrayList<>();

	@Nullable
	public Vec3 prevEggSacPosition = null;
	@Nullable
	public Vec3 eggSacPosition = null;

	protected int eggSacMovementCooldown = 0;

	protected static final float HULL_OUTER_WIDTH = 0.58F;
	protected static final float HULL_INNER_WIDTH = 0.44F;
	protected static final float[][] HULL_CROSS_SECTION = new float[][]{
		{-HULL_OUTER_WIDTH, HULL_INNER_WIDTH},
		{-HULL_OUTER_WIDTH, -HULL_INNER_WIDTH},
		{-HULL_INNER_WIDTH, -HULL_INNER_WIDTH},
		{-HULL_INNER_WIDTH, -HULL_OUTER_WIDTH},
		{HULL_INNER_WIDTH, -HULL_OUTER_WIDTH},
		{HULL_INNER_WIDTH, -HULL_INNER_WIDTH},
		{HULL_OUTER_WIDTH, -HULL_INNER_WIDTH},
		{HULL_OUTER_WIDTH, HULL_INNER_WIDTH},
		{HULL_INNER_WIDTH, HULL_INNER_WIDTH},
		{HULL_INNER_WIDTH, HULL_OUTER_WIDTH},
		{-HULL_INNER_WIDTH, HULL_OUTER_WIDTH},
		{-HULL_INNER_WIDTH, HULL_INNER_WIDTH},
	};

	public static class HullSegment {
		private static final Vec3 WORLD_UP = new Vec3(0, 1, 0);

		@Nullable
		public Vec3 prevPos, pos;
		public float prevYaw, yaw;
		public final float[] offsetX, offsetY, offsetZ;

		public HullSegment() {
			this.offsetX = new float[HULL_CROSS_SECTION.length];
			this.offsetY = new float[HULL_CROSS_SECTION.length];
			this.offsetZ = new float[HULL_CROSS_SECTION.length];
		}

		public void update(Vec3 newPos, Vec3 splineDir) {
			this.prevPos = this.pos;
			this.pos = newPos;

			this.prevYaw = this.yaw;
			this.yaw = (float) Math.toDegrees(Math.atan2(splineDir.z, splineDir.x)) - 90;

			if (this.prevPos == null) {
				this.prevPos = this.pos;
				this.prevYaw = this.yaw;
			}

			while (this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while (this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}

			Vec3 right = splineDir.cross(WORLD_UP).normalize();
			Vec3 up = right.cross(splineDir).normalize();

			int i = 0;
			for (float[] hullCrossSection : HULL_CROSS_SECTION) {
				float hullX = hullCrossSection[0];
				float hullY = hullCrossSection[1];

				this.offsetX[i] = (float) (right.x * hullX + up.x * hullY);
				this.offsetY[i] = (float) (right.y * hullX + up.y * hullY);
				this.offsetZ[i] = (float) (right.z * hullX + up.z * hullY);

				i++;
			}
		}
	}

	public static class SpineBone {
		@Nullable
		public Vec3 prevPos, pos;
		public float prevYaw, yaw;

		public void update(Vec3 newPos, Vec3 splineDir) {
			this.prevPos = this.pos;
			this.pos = newPos;

			this.prevYaw = this.yaw;
			this.yaw = -(float) Math.toDegrees(Math.atan2(splineDir.z, splineDir.x)) + 90;

			if (this.prevPos == null) {
				this.prevPos = this.pos;
				this.prevYaw = this.yaw;
			}

			while (this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while (this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}
		}
	}

	public LargeSludgeWorm(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 10;
		final int numSegments = 3 * this.parts.length;

		this.segments = new HullSegment[numSegments];
	}

	@Override
	protected void assignParts() {
		this.parts = new SludgeWormMultipart[]{
			new SludgeWormMultipart(this, 0.8F, 0.8F),
			new SludgeWormMultipart(this, 0.8F, 0.8F),
			new SludgeWormMultipart(this, 0.8F, 0.8F),
			new SludgeWormMultipart(this, 0.8F, 0.8F)
		};
		this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(EGG_SAC_PERCENTAGE, -1.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D, 1));
		this.goalSelector.addGoal(4, new LayEggGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, entity -> !(entity instanceof Enemy)));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 60.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.19D)
			.add(Attributes.ATTACK_DAMAGE, 6.0D)
			.add(Attributes.FOLLOW_RANGE, 20.0D);
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 0.5F;
	}

	@Override
	protected double getMaxPieceDistance() {
		return 0.95D;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		tag.putFloat("egg_percentage", this.getEggSacPercentage());
		tag.putInt("egg_cooldown", this.eggSacMovementCooldown);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		this.setEggSacPercentage(tag.getFloat("egg_percentage"));
		this.eggSacMovementCooldown = tag.getInt("egg_cooldown");
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			if (this.isAlive() && !this.isNoAi()) {
				if (this.eggSacMovementCooldown > 0) {
					this.eggSacMovementCooldown--;
				} else if (this.getEggSacPercentage() >= 0) {
					float percentage = Math.max(this.getEggSacPercentage(), 0) + 0.005F;

					if (percentage >= 1.0F) {
						SludgeWormMultipart tailPart = this.parts[this.parts.length - 1];

						SludgeWormEggSac eggSac = new SludgeWormEggSac(EntityRegistry.SLUDGE_WORM_EGG_SAC.get(), this.level());
						eggSac.moveTo(tailPart.position(), 0, 0);

						this.level().addFreshEntity(eggSac);

						this.setEggSacPercentage(-1);
					} else {
						this.setEggSacPercentage(percentage);
					}
				}
			}
		} else {
			this.updateSegmentPositions();

			if (!this.isNoAi()) {
				float eggSackPercentage = this.getEggSacPercentage();

				if (eggSackPercentage >= 0) {
					this.prevEggSacPosition = this.eggSacPosition;
					this.eggSacPosition = this.spineySpliney.interpolate(eggSackPercentage);
				} else {
					this.prevEggSacPosition = this.eggSacPosition = null;
				}
			}
		}
	}

	@Override
	protected boolean damageWorm(DamageSource source, float amount) {
		this.eggSacMovementCooldown = 50;
		boolean hurt = super.damageWorm(source, amount);

		if (hurt && !this.level().isClientSide() && source.getEntity() != null && this.getRandom().nextInt(6) == 0 && amount > 0.5F) {
			SludgeWormMultipart spawnPart = this.parts[this.getRandom().nextInt(this.parts.length)];

			SmolSludge entity = new SmolSludge(EntityRegistry.SMOL_SLUDGE.get(), this.level());
			entity.moveTo(spawnPart.position(), this.getRandom().nextFloat() * 360.0F, 0);
			this.level().addFreshEntity(entity);
		}

		return hurt;
	}

	//TODO figure out why body segments are disconnected from the head. I suspect the positions are being set incorrectly in here
	protected void updateSegmentPositions() {
		this.segmentsAvailable = true;

		Vec3 look = this.getLookAngle();
		Vec3 origin = this.position();

		Vec3[] points = new Vec3[this.parts.length + 2];

		Vec3 partDir = null;
		SludgeWormMultipart prevPart = null;

		points[0] = this.parts[0].position().add(-origin.x + look.x, -origin.y + look.y, -origin.z + look.z);

		for (int i = 0; i < this.parts.length; i++) {
			SludgeWormMultipart part = this.parts[i];

			boolean isSamePos = false;

			if (prevPart != null) {
				Vec3 currPos = part.position();
				Vec3 prevPos = prevPart.position();
				Vec3 diff = currPos.subtract(prevPos);
				if (diff.lengthSqr() > 0.01D) {
					partDir = diff.normalize();
				} else {
					isSamePos = true;
				}
			}

			prevPart = part;

			Vec3 splineNode = part.position().subtract(origin);

			if (isSamePos && partDir != null) {
				//Adds a slight offset in part dir such that the two positions aren't the same
				splineNode = splineNode.add(partDir.scale(0.1D / this.parts.length * i));
			}

			points[i + 1] = splineNode;
		}

		Vec3 endPoint;
		if (partDir != null) {
			endPoint = this.parts[this.parts.length - 1].position().add(-origin.x + partDir.x, -origin.y + partDir.y, -origin.z + partDir.z);
		} else {
			endPoint = this.parts[this.parts.length - 1].position().add(-origin.x, -origin.y - 0.0001D, -origin.z);
		}
		points[this.parts.length + 1] = endPoint;

		this.spineySpliney = new ReparameterizedSpline(new CatmullRomSpline(points));
		this.spineySpliney.init(this.segments.length * 2, 3);

		for (int i = 0; i < this.segments.length; i++) {
			HullSegment segment = this.segments[i];
			if (segment == null) {
				this.segments[i] = segment = new HullSegment();
			}

			Vec3 pos = this.spineySpliney.interpolate(i / (float) (this.segments.length - 1));
			Vec3 dir = this.spineySpliney.derivative(i / (float) (this.segments.length - 1));

			segment.update(pos, dir);
		}

		int spineBones = Mth.ceil(this.spineySpliney.getArcLength() * 12);

		for (int i = spineBones - this.bones.size(); i > 0; i--) {
			this.bones.add(new SpineBone());
		}
		for (int i = this.bones.size() - spineBones; i > 0; i--) {
			this.bones.removeLast();
		}

		for (int i = 0; i < this.bones.size(); i++) {
			SpineBone bone = this.bones.get(i);

			Vec3 pos = this.spineySpliney.interpolate(i / (float) (this.bones.size() - 1));
			Vec3 dir = this.spineySpliney.derivative(i / (float) (this.bones.size() - 1));

			bone.update(pos, dir);
		}
	}

	/**
	 * Sets the egg sac laying progress percentage.
	 * Use value < 0 if no egg sac.
	 *
	 * @param percentage
	 */
	public void setEggSacPercentage(float percentage) {
		this.getEntityData().set(EGG_SAC_PERCENTAGE, percentage);
	}

	/**
	 * Returns the egg sac laying progress percentage.
	 * Value < 0 means no egg sac.
	 *
	 * @return
	 */
	public float getEggSacPercentage() {
		return this.getEntityData().get(EGG_SAC_PERCENTAGE);
	}

	public void startLayingEggSac() {
		if (this.getEggSacPercentage() < 0) {
			this.setEggSacPercentage(0.00001F);
		}
	}

	public static class LayEggGoal extends Goal {
		protected final LargeSludgeWorm entity;

		protected int cooldown = 30;

		public LayEggGoal(LargeSludgeWorm entity) {
			this.entity = entity;
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			boolean canLay = this.entity.isAlive() && this.entity.getTarget() != null && this.entity.getEggSacPercentage() < 0;
			if (canLay) {
				if (this.cooldown-- <= 0) {
					List<SludgeWormEggSac> nearbyEggSacs = this.entity.level().getEntitiesOfClass(SludgeWormEggSac.class, this.entity.getBoundingBox().inflate(16.0D));
					List<TinySludgeWorm> nearbyTinyWorms = this.entity.level().getEntitiesOfClass(TinySludgeWorm.class, this.entity.getBoundingBox().inflate(16.0D));

					if (nearbyEggSacs.size() < 5 && nearbyTinyWorms.size() < 8) {
						this.cooldown = 30 + this.entity.getRandom().nextInt(30);
						return true;
					} else {
						this.cooldown = 10 + this.entity.getRandom().nextInt(20);
					}
				}
			}
			return false;
		}

		@Override
		public void start() {
			this.entity.startLayingEggSac();
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}
	}
}
