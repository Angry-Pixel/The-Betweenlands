package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;

//TODO Loot tables
public class EntitySludgeMenace extends EntityWallLivingRoot implements IEntityScreenShake {
	protected static final byte EVENT_SLAM_HIT = 90;

	public int renderedFrame = -1;

	protected static final DataParameter<Integer> ACTION_STATE = EntityDataManager.createKey(EntitySludgeMenace.class, DataSerializers.VARINT);

	public static enum ActionState {
		IDLE, SLAM, POKE, SWING;
	}

	private EntityMultipartDummy[] dummies;

	private AxisAlignedBB renderBoundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	protected int actionTimer = 0;
	protected ActionState actionState = ActionState.IDLE;

	protected Vec3d actionTargetPos = null;

	protected int screenShakeTimer = 0;

	public EntitySludgeMenace(World world) {
		super(world);
		this.dummies = new EntityMultipartDummy[this.getParts().length];
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ACTION_STATE, ActionState.IDLE.ordinal());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(MAX_ARM_LENGTH).setBaseValue(8);
	}

	@Override
	protected float[][] getArmCrossSection() {
		float sqrt2 = 1.41f;
		float width = this.getFullArmWidth();
		return new float[][] {
			{-width, width},
			{-width * sqrt2, 0},
			{-width, -width},
			{0, -width * sqrt2},
			{width, -width},
			{width * sqrt2, 0},
			{width, width},
			{0, width * sqrt2},
		};
	}

	@Override
	protected float getNodeSize(int node) {
		return 0.2f + node / (float)this.getNumSegments() * 0.75f;
	}

	@Override
	protected int getNumSegments() {
		return 16;
	}

	@Override
	protected float getFullArmWidth() {
		return 0.45F;
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote) {
			if(this.actionState == ActionState.IDLE) {
				//TODO Do this from AIs
				//this.startAction(ActionState.values()[this.world.rand.nextInt(ActionState.values().length)]);
				this.startAction(ActionState.SLAM);
			}

			this.dataManager.set(ACTION_STATE, this.actionState.ordinal());
		} else {
			this.actionState = ActionState.values()[this.dataManager.get(ACTION_STATE)];
		}

		//TODO Debug
		this.setHealth(this.getMaxHealth());

		if(this.actionState != ActionState.IDLE) {
			this.actionTimer++;
		} else {
			this.actionTimer = 0;
		}

		float armGravity = 0.5f;

		if(this.actionState == ActionState.SLAM) {
			armGravity = 0;
		} else if(this.actionState == ActionState.POKE) {
			armGravity = 0.8f;
		}

		int segmentIndex = 0;
		for(ArmSegment segment : this.armSegments) {
			segment.motion = new Vec3d(0, armGravity * (1 - segmentIndex / (float)this.armSegments.size()), 0);
			segmentIndex++;
		}

		if(this.screenShakeTimer > 0) {
			this.screenShakeTimer--;
		}

		super.onUpdate();

		Entity[] parts = this.getParts();

		if(this.world.isRemote) {
			for(int i = 0; i < parts.length; i++) {
				if(i == 0) {
					this.renderBoundingBox = parts[i].getEntityBoundingBox();
				} else {
					this.renderBoundingBox = this.renderBoundingBox.union(parts[i].getEntityBoundingBox());
				}
			}

			this.spawnTipParticles();
		}

		if(!this.world.isRemote && this.isEntityAlive()) {
			for(int i = 0; i < this.dummies.length; i++) {
				EntityMultipartDummy dummy = this.dummies[i];

				if(dummy == null || !dummy.isEntityAlive()) {
					Entity multipart = parts[i];

					if(multipart instanceof MultiPartEntityPart) {
						this.dummies[i] = dummy = new EntityMultipartDummy(this.world, (MultiPartEntityPart) multipart);
						this.world.spawnEntity(dummy);
					}
				} else {
					dummy.updatePositioning();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnTipParticles() {
		Entity[] parts = this.getParts();

		if(parts.length > 2) {
			Entity lastPart = parts[parts.length - 1];
			Entity secondLastPart = parts[parts.length - 2];

			Vec3d dir = lastPart.getPositionVector().subtract(secondLastPart.getPositionVector()).normalize().add(this.rand.nextFloat() - 0.5f, 0, this.rand.nextFloat() - 0.5f).scale(0.1D);

			double x = lastPart.posX;
			double y = lastPart.posY + lastPart.height / 2;
			double z = lastPart.posZ;

			double mx = dir.x;
			double my = dir.y;
			double mz = dir.z;

			int[] color = {100, 70, 0, 255};

			ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(null)
							.withMotion(mx, my, mz)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
							.withScale(8f));

			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);

			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(null)
							.withMotion(mx, my, mz)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
							.withScale(4f));

			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
		}
	}

	@Override
	protected Vec3d updateTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		switch(this.actionState) {
		default:
		case IDLE:
			return this.updateIdleTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);
		case SLAM:
			return this.updateSlamTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);
		case POKE:
			return this.updatePokeTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);
		case SWING:
			return this.updateSwingTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);
		}
	}

	protected void startAction(ActionState action) {
		this.actionState = action;
		this.actionTimer = 0;
	}

	protected Vec3d updateSwingTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		float drive = this.actionTimer * 0.05f * (1.0f + this.actionTimer / 180.0f * 2.0f);

		if(this.actionTimer >= 180) {
			this.actionState = ActionState.IDLE;
		}

		float swingLength = maxArmLength + 0.2f;

		return armStartWorld.add(Math.cos(drive) * swingLength, 1 + (Math.sin(drive * 0.4f) + 1), Math.sin(drive) * swingLength);
	}

	protected Vec3d updatePokeTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		float drive = this.actionTimer;

		if(drive < 42 && this.getAttackTarget() != null) {
			this.actionTargetPos = this.getAttackTarget().getPositionVector();
		}

		if(drive < 40) {
			Vec3d bendPos = armStartWorld.add(dirFwd.scale(maxArmLength));

			Vec3d targetDir = new Vec3d(3, 0, 0);

			if(this.getAttackTarget() != null) {
				targetDir = this.getAttackTarget().getPositionVector().subtract(bendPos).normalize().scale(5);
			}

			return bendPos.add(targetDir);
		} else if(drive < 48) {
			float speed = (drive - 40) / 8.0f * 4.0f;

			Vec3d targetTipPos = armStartWorld.add(dirFwd.scale(maxArmLength));

			if(this.actionTargetPos != null) {
				targetTipPos = this.actionTargetPos.add(0, this.getAttackTarget() != null ? this.getAttackTarget().height / 2 : 0, 0);
			}

			Vec3d tipPos = this.rootTip.getPositionVector();

			Vec3d tipDiff = targetTipPos.subtract(tipPos);
			targetTipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), speed)));

			return targetTipPos;
		}

		if(this.actionTimer >= 70) {
			this.actionState = ActionState.IDLE;
		}

		return this.rootTip.getPositionVector();
	}

	protected Vec3d updateSlamTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		float drive = this.actionTimer;

		if(drive < 40 && this.getAttackTarget() != null) {
			this.actionTargetPos = this.getAttackTarget().getPositionVector();
		}

		float rot;
		if(drive < 20) {
			rot = (float)Math.PI / 2;
		} else if(drive < 60) {
			float slap = (drive - 20) / 40.0f;
			float s = 6;
			float a = 6;
			rot = (float)Math.pow(slap, a + slap * s) * (float)(Math.PI / 2) + (float)Math.PI / 2;
		} else {
			rot = (float)Math.PI;
		}

		Vec3d targetDir = new Vec3d(maxArmLength + 1, 0, 0);

		if(this.actionTargetPos != null) {
			targetDir = armStartWorld.subtract(this.actionTargetPos).normalize().scale(maxArmLength + 1.0F);
		}

		if(this.actionTimer >= 90) {
			this.actionState = ActionState.IDLE;
		}

		if(!this.world.isRemote && this.actionTimer == 63) {
			this.world.setEntityState(this, EVENT_SLAM_HIT);
			this.startSlamScreenShake();

			for(Entity part : this.getParts()) {
				BlockPos pos = new BlockPos(part).down();

				if(!this.world.isAirBlock(pos)) {
					EntitySludgeJet jet = new EntitySludgeJet(this.world);
					jet.setLocationAndAngles(part.posX + this.rand.nextFloat() - 0.5f, pos.getY() + 0.5D, part.posZ + this.rand.nextFloat() - 0.5f, 0, 0);
					this.world.spawnEntity(jet);
				}
			}
		}

		return armStartWorld.add(targetDir.x * Math.cos(rot), Math.sin(rot) * targetDir.length(), targetDir.z * Math.cos(rot));
	}

	protected Vec3d updateIdleTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		float flailingStrength = this.isSwingInProgress ? (1 - this.swingProgress) : this.hurtTime > 0 ? (this.hurtTime / (float)this.maxHurtTime) * 0.5F : 0.0f;

		this.armMovementTicks += 1 + (int)(flailingStrength * 10);

		float idleX = MathHelper.cos(this.armMovementTicks / 9.0f) * 0.75F;
		float idleY = MathHelper.sin(this.armMovementTicks / 7.0f) * 0.75F;
		float idleZ = (MathHelper.cos(this.armMovementTicks / 15.0f) + 1) * 0.25f;

		Vec3d targetTipPos = armStartWorld.add(dirFwd.scale(maxArmLength));

		EntityLivingBase target = this.getAttackTarget();
		if(target != null) {
			targetTipPos = target.getPositionVector().add(0, target.height / 2, 0);
		}

		float forwardPos = (float) dirFwd.dotProduct(targetTipPos.subtract(armStartWorld));
		float offsetZ = 0.0f;
		if(forwardPos < 1.0F) {
			offsetZ = 1.0F - forwardPos;
		}

		//Idle movement
		targetTipPos = targetTipPos.add(dirUp.scale(idleY)).add(dirFwd.crossProduct(dirUp).scale(idleX)).add(dirFwd.scale(offsetZ - idleZ));

		Vec3d tipPos = this.rootTip.getPositionVector();

		Vec3d tipDiff = targetTipPos.subtract(tipPos);
		targetTipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), 0.1D + flailingStrength * 0.9D)));

		return targetTipPos;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.renderBoundingBox;
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(this.screenShakeTimer > 0) {
			float dstMul = MathHelper.clamp(1.0f - this.getDistance(viewer) / 24.0f, 0, 1);
			return dstMul * (this.screenShakeTimer - partialTicks) / 10.0f * 0.2f;
		}
		return 0;
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_SLAM_HIT) {
			this.startSlamScreenShake();
		}
	}

	protected void startSlamScreenShake() {
		this.screenShakeTimer = 10;
	}
}
