package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ReparameterizedSpline;

//TODO Loot tables
public class EntityLargeSludgeWorm extends EntitySludgeWorm {
	private static final DataParameter<Float> EGG_SAC_PERCENTAGE = EntityDataManager.<Float>createKey(EntityLargeSludgeWorm.class, DataSerializers.FLOAT);

	public boolean segmentsAvailable = false;

	public ReparameterizedSpline spineySpliney;

	public final HullSegment[] segments;
	public final List<SpineBone> bones = new ArrayList<>();

	public Vec3d prevEggSacPosition = null;
	public Vec3d eggSacPosition = null;

	protected int eggSacMovementCooldown = 0;

	protected static final float HULL_OUTER_WIDTH = 0.58F;
	protected static final float HULL_INNER_WIDTH = 0.44F;
	protected static final float[][] HULL_CROSS_SECTION = new float[][] {
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
		private static final Vec3d WORLD_UP = new Vec3d(0, 1, 0);

		public Vec3d prevPos, pos;
		public float prevYaw, yaw;
		public final float[] offsetX, offsetY, offsetZ;

		public HullSegment() {
			this.offsetX = new float[HULL_CROSS_SECTION.length];
			this.offsetY = new float[HULL_CROSS_SECTION.length];
			this.offsetZ = new float[HULL_CROSS_SECTION.length];
		}

		public void update(Vec3d newPos, Vec3d splineDir) {
			this.prevPos = this.pos;
			this.pos = newPos;

			this.prevYaw = this.yaw;
			this.yaw = (float)Math.toDegrees(Math.atan2(splineDir.z, splineDir.x)) - 90;

			if(this.prevPos == null) {
				this.prevPos = this.pos;
				this.prevYaw = this.yaw;
			}

			while(this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while(this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}

			Vec3d right = splineDir.crossProduct(WORLD_UP).normalize();
			Vec3d up = right.crossProduct(splineDir).normalize();

			int i = 0;
			for(float[] hullCrossSection : HULL_CROSS_SECTION) {
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
		public Vec3d prevPos, pos;
		public float prevYaw, yaw;

		public void update(Vec3d newPos, Vec3d splineDir) {
			this.prevPos = this.pos;
			this.pos = newPos;

			this.prevYaw = this.yaw;
			this.yaw = -(float)Math.toDegrees(Math.atan2(splineDir.z, splineDir.x)) + 90;

			if(this.prevPos == null) {
				this.prevPos = this.pos;
				this.prevYaw = this.yaw;
			}

			while(this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while(this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}
		}
	}

	public EntityLargeSludgeWorm(World world) {
		super(world);
		setSize(0.8F, 0.8F);
		isImmuneToFire = true;
		maxHurtResistantTime = 40;

		this.parts = new MultiPartEntityPart[] {
				new MultiPartEntityPart(this, "part1", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part2", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part3", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part4", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part5", 0.8F, 0.8F)
		};

		final int numSegments = 3 * this.parts.length;

		this.segments = new HullSegment[numSegments];
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(EGG_SAC_PERCENTAGE, -1.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
		tasks.addTask(3, new EntityAIWander(this, 0.5D, 1));
		tasks.addTask(4, new AILayEggSac(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.LARGE_SLUDGE_WORM;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 0.5F;
	}
	
	@Override
	protected double getMaxPieceDistance() {
		return 0.95D;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setFloat("eggSacPercentage", this.getEggSacPercentage());
		nbt.setInteger("eggSacCooldown", this.eggSacMovementCooldown);
	}	

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if(nbt.hasKey("eggSacPercentage", Constants.NBT.TAG_FLOAT)) {
			this.setEggSacPercentage(nbt.getFloat("eggSacPercentage"));
		}
		if(nbt.hasKey("eggSacCooldown", Constants.NBT.TAG_INT)) {
			this.eggSacMovementCooldown = nbt.getInteger("eggSacCooldown");
		}
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.isEntityAlive()) {
				if(this.eggSacMovementCooldown > 0) {
					this.eggSacMovementCooldown--;
				} else if(this.getEggSacPercentage() >= 0) {
					float percentage = Math.max(this.getEggSacPercentage(), 0) + 0.001F;

					if(percentage >= 1.0F) {
						MultiPartEntityPart tailPart = this.parts[this.parts.length - 1];

						EntityTinyWormEggSac eggSac = new EntityTinyWormEggSac(this.world);
						eggSac.setLocationAndAngles(tailPart.posX, tailPart.posY, tailPart.posZ, 0, 0);

						this.world.spawnEntity(eggSac);

						this.setEggSacPercentage(-1);
					} else {
						this.setEggSacPercentage(percentage);
					}
				}
			}
		} else {
			this.updateSegmentPositions();

			float eggSackPercentage = this.getEggSacPercentage();

			if(eggSackPercentage >= 0) {
				this.prevEggSacPosition = this.eggSacPosition;
				this.eggSacPosition = this.spineySpliney.interpolate(eggSackPercentage);
			} else {
				this.prevEggSacPosition = this.eggSacPosition = null;
			}
		}
	}

	@Override
	protected boolean damageWorm(DamageSource source, float amount) {
		this.eggSacMovementCooldown = 120;

		if(!this.world.isRemote && source instanceof EntityDamageSource && this.world.rand.nextInt(6) == 0 && amount > 0.5F) {
			MultiPartEntityPart spawnPart = this.parts[this.rand.nextInt(this.parts.length)];

			EntitySmollSludge entity = new EntitySmollSludge(this.world);
			entity.setLocationAndAngles(spawnPart.posX, spawnPart.posY, spawnPart.posZ, this.rand.nextFloat() * 360.0F, 0);

			this.world.spawnEntity(entity);
		}

		return super.damageWorm(source, amount);
	}

	protected void updateSegmentPositions() {
		this.segmentsAvailable = true;

		Vec3d origin = this.getPositionVector();

		Vec3d[] points = new Vec3d[this.parts.length + 2];

		Vec3d partDir = null;
		MultiPartEntityPart prevPart = null;

		Vec3d look = this.getLookVec();

		points[0] = this.parts[0].getPositionVector().add(-origin.x + look.x, -origin.y + look.y, -origin.z + look.z);
		for(int i = 0; i < this.parts.length; i++) {
			MultiPartEntityPart part = this.parts[i];

			boolean isSamePos = false;

			if(prevPart != null) {
				Vec3d currPos = part.getPositionVector();
				Vec3d prevPos = prevPart.getPositionVector();
				Vec3d diff = currPos.subtract(prevPos);
				if(diff.lengthSquared() > 0.01D) {
					partDir = diff.normalize();
				} else {
					isSamePos = true;
				}
			}

			prevPart = part;

			Vec3d splineNode = part.getPositionVector().subtract(origin);

			if(isSamePos && partDir != null) {
				//Adds a slight offset in part dir such that the two positions
				//aren't the same
				splineNode = splineNode.add(partDir.scale(0.1D / this.parts.length * i));
			}

			points[i + 1] = splineNode;
		}

		Vec3d endPoint;
		if(partDir != null) {
			endPoint = this.parts[this.parts.length - 1].getPositionVector().add(-origin.x + partDir.x, -origin.y + partDir.y, -origin.z + partDir.z);
		} else {
			endPoint = this.parts[this.parts.length - 1].getPositionVector().add(-origin.x, -origin.y- 0.0001D, -origin.z);
		}
		points[this.parts.length + 1] = endPoint;

		this.spineySpliney = new ReparameterizedSpline(new CatmullRomSpline(points));
		this.spineySpliney.init(this.segments.length * 2, 3);

		for(int i = 0; i < this.segments.length; i++) {
			HullSegment segment = this.segments[i];
			if(segment == null) {
				this.segments[i] = segment = new HullSegment();
			}

			Vec3d pos = this.spineySpliney.interpolate(i / (float)(this.segments.length - 1));
			Vec3d dir = this.spineySpliney.derivative(i / (float)(this.segments.length - 1));

			segment.update(pos, dir);
		}

		int spineBones = MathHelper.ceil(this.spineySpliney.getArcLength() * 12);

		for(int i = spineBones - this.bones.size(); i > 0; i--) {
			this.bones.add(new SpineBone());
		}
		for(int i = this.bones.size() - spineBones; i > 0; i--) {
			this.bones.remove(this.bones.size() - 1);
		}

		for(int i = 0; i < this.bones.size(); i++) {
			SpineBone bone = this.bones.get(i);

			Vec3d pos = this.spineySpliney.interpolate(i / (float)(this.bones.size() - 1));
			Vec3d dir = this.spineySpliney.derivative(i / (float)(this.bones.size() - 1));

			bone.update(pos, dir);
		}
	}

	/**
	 * Sets the egg sac laying progress percentage.
	 * Use value < 0 if no egg sac.
	 * @param percentage
	 */
	public void setEggSacPercentage(float percentage) {
		this.getDataManager().set(EGG_SAC_PERCENTAGE, percentage);
	}

	/**
	 * Returns the egg sac laying progress percentage.
	 * Value < 0 means no egg sac.
	 * @return
	 */
	public float getEggSacPercentage() {
		return this.getDataManager().get(EGG_SAC_PERCENTAGE);
	}

	public void startLayingEggSac() {
		if(this.getEggSacPercentage() < 0) {
			this.setEggSacPercentage(0.00001F);
		}
	}

	public static class AILayEggSac extends EntityAIBase {
		protected final EntityLargeSludgeWorm entity;

		protected int cooldown = 30;

		public AILayEggSac(EntityLargeSludgeWorm entity) {
			this.entity = entity;
			this.setMutexBits(0);
		}

		@Override
		public boolean shouldExecute() {
			boolean canLay = this.entity.isEntityAlive() && this.entity.getAttackTarget() != null && this.entity.getEggSacPercentage() < 0;
			if(canLay) {
				if(this.cooldown-- <= 0) {
					List<EntityTinyWormEggSac> nearbyEggSacs = this.entity.world.getEntitiesWithinAABB(EntityTinyWormEggSac.class, this.entity.getEntityBoundingBox().grow(16.0D));
					List<EntityTinySludgeWorm> nearbyTinyWorms = this.entity.world.getEntitiesWithinAABB(EntityTinySludgeWorm.class, this.entity.getEntityBoundingBox().grow(16.0D));

					if(nearbyEggSacs.size() < 5 && nearbyTinyWorms.size() < 8) {
						this.cooldown = 133 + this.entity.rand.nextInt(80);
						return true;
					} else {
						this.cooldown = 40 + this.entity.rand.nextInt(30);
					}
				}
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.startLayingEggSac();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}
	}
}
