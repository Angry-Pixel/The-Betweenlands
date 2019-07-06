package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ReparameterizedSpline;

public class EntityLargeSludgeWorm extends EntitySludgeWorm {
	public boolean segmentsAvailable = false;

	public ReparameterizedSpline spineySpliney;

	public final Vec3d[] prevSegmentPositions;
	public final Vec3d[] segmentPositions;
	public final Vec3d[] prevSegmentDirs;
	public final Vec3d[] segmentDirs;

	public final List<Vec3d> prevSpinePositions = new ArrayList<>();
	public final List<Vec3d> spinePositions = new ArrayList<>();

	public final List<Vec3d> prevSpineDirs = new ArrayList<>();
	public final List<Vec3d> spineDirs = new ArrayList<>();

	public EntityLargeSludgeWorm(World world) {
		super(world);
		setSize(0.8F, 0.8F);
		isImmuneToFire = true;
		maxHurtResistantTime = 40;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));

		this.parts = new MultiPartEntityPart[] {
				new MultiPartEntityPart(this, "part1", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part2", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part3", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part4", 0.8F, 0.8F),
				new MultiPartEntityPart(this, "part5", 0.8F, 0.8F)
		};

		final int numSegments = 3 * this.parts.length;

		this.prevSegmentPositions = new Vec3d[numSegments];
		this.segmentPositions = new Vec3d[numSegments];
		this.prevSegmentDirs = new Vec3d[numSegments];
		this.segmentDirs = new Vec3d[numSegments];

		// tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWander(this, 0.5D, 1));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.1D);
	}

	@Override
	protected double getMaxPieceDistance() {
		return 0.95D;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.isRemote) {
			this.updateSegmentPositions();
		}
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
		this.spineySpliney.init(this.segmentPositions.length * 2, 3);

		for(int i = 0; i < this.segmentPositions.length; i++) {
			Vec3d pos = this.spineySpliney.interpolate(i / (float)(this.segmentPositions.length - 1));
			Vec3d dir = this.spineySpliney.derivative(i / (float)(this.segmentPositions.length - 1));

			this.prevSegmentPositions[i] = this.segmentPositions[i];
			this.segmentPositions[i] = pos;
			if(this.prevSegmentPositions[i] == null) {
				this.prevSegmentPositions[i] = pos;
			}

			this.prevSegmentDirs[i] = this.segmentDirs[i];
			this.segmentDirs[i] = dir;
			if(this.prevSegmentDirs[i] == null) {
				this.prevSegmentDirs[i] = dir;
			}
		}

		this.prevSpinePositions.clear();
		this.prevSpinePositions.addAll(this.spinePositions);

		this.prevSpineDirs.clear();
		this.prevSpineDirs.addAll(this.spineDirs);

		this.spinePositions.clear();
		this.spineDirs.clear();

		int spineBones = MathHelper.ceil(this.spineySpliney.getArcLength() * 12);

		for(int i = 0; i < spineBones; i++) {
			Vec3d bonePos = this.spineySpliney.interpolate(i / (float)(spineBones - 1));
			Vec3d boneDir = this.spineySpliney.derivative(i / (float)(spineBones - 1));

			this.spinePositions.add(bonePos);
			this.spineDirs.add(boneDir);
		}
	}
}
