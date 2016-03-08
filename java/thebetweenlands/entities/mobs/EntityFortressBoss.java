package thebetweenlands.entities.mobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.utils.RotationMatrix;

public class EntityFortressBoss extends EntityMob implements IEntityBL {
	public static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	public static final int SHIELD_DW = 20;
	public static final int SHIELD_ROTATION_DW = 21;

	public static final double SHIELD_OFFSET_X = 0.0D;
	public static final double SHIELD_OFFSET_Y = 1D;
	public static final double SHIELD_OFFSET_Z = 0.0D;

	private static final double ICOSAHEDRON_X = 0.525731112119133606D;
	private static final double ICOSAHEDRON_Z = 0.850650808352039932D;
	public static final double[][] ICOSAHEDRON_VERTICES = new double[][] {{-ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {-ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z},
		{0.0, ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, ICOSAHEDRON_Z, -ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, -ICOSAHEDRON_X},
		{ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}
	};
	public static final int[][] ICOSAHEDRON_INDICES = new int[][] {
		{0,4,1}, {0,9,4}, {9,5,4}, {4,5,8}, {4,8,1},
		{8,10,1}, {8,3,10},{5,3,8}, {5,2,3}, {2,7,3},
		{7,10,3}, {7,6,10}, {7,11,6}, {11,0,6}, {0,1,6},
		{6,1,10}, {9,0,11}, {9,11,2}, {9,2,5}, {7,2,11}
	};
	public static final float SHIELD_EXPLOSION = 0.2F;

	private boolean[] activeShields = new boolean[20];

	@SideOnly(Side.CLIENT)
	public int[] shieldAnimationTicks = new int[20];

	public final AxisAlignedBB coreBoundingBox;

	private double anchorX, anchorY, anchorZ, anchorRadius;

	public float shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, lastShieldRotationYaw, lastShieldRotationPitch, lastShieldRotationRoll;

	public EntityFortressBoss(World world) {
		super(world);
		float width = 1.9F;
		float height = 1.9F;
		this.setSize(width, height);
		float coreWidth = 1.0F;
		float coreHeight = 1.0F;
		this.coreBoundingBox = AxisAlignedBB.getBoundingBox(-coreWidth/2.0F, 0F + height / 4.0F, -coreWidth/2.0F, coreWidth/2.0F, coreHeight + height / 4.0F, coreWidth/2.0F);
		for(int i = 0; i < 20; i++) {
			this.activeShields[i] = true;
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(SHIELD_DW, (int) 0);
		this.dataWatcher.addObject(SHIELD_ROTATION_DW, 0.0F);
	}

	@Override
	public String pageName() {
		return "fortressBoss";
	}

	public void setAnchor(double x, double y, double z, double radius) {
		this.anchorX = x;
		this.anchorY = y;
		this.anchorZ = z;
		this.anchorRadius = radius;
	}

	public double getAnchorX() {
		return this.anchorX;
	}

	public double getAnchorY() {
		return this.anchorY;
	}

	public double getAnchorZ() {
		return this.anchorZ;
	}

	public double getAnchorRadius() {
		return this.anchorRadius;
	}

	private int packShieldData() {
		int packedData = 0;
		for(int i = 0; i <= 19; i++) {
			packedData |= (this.activeShields[i] ? 1 : 0) << i;
		}
		return packedData;
	}

	private void unpackShieldData(int packedData) {
		for(int i = 0; i <= 19; i++) {
			this.activeShields[i] = ((packedData >> i) & 1) == 1 ? true : false;
		}
	}

	public boolean hasShield() {
		for(int i = 0; i <= 19; i++) {
			if(this.activeShields[i])
				return true;
		}
		return false;
	}

	public boolean isShieldActive(int shield) {
		return this.activeShields[shield];
	}

	public void setShieldActive(int shield, boolean active) {
		this.activeShields[shield] = active;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationYaw(float partialTicks) {
		return this.lastShieldRotationYaw + (this.shieldRotationYaw - this.lastShieldRotationYaw) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationPitch(float partialTicks) {
		return this.lastShieldRotationPitch + (this.shieldRotationPitch - this.lastShieldRotationPitch) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationRoll(float partialTicks) {
		return this.lastShieldRotationRoll + (this.shieldRotationRoll - this.lastShieldRotationRoll) * partialTicks;
	}

	public int rayTraceShield(Vec3 pos, Vec3 ray, boolean back) {
		int shield = -1;
		double centroidX = 0;
		double centroidY = 0;
		double centroidZ = 0;

		ROTATION_MATRIX.setRotations((float)Math.toRadians(-this.shieldRotationPitch), (float)Math.toRadians(-this.shieldRotationYaw), (float)Math.toRadians(-this.shieldRotationRoll));

		Vec3 centerPos = Vec3.createVectorHelper(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);

		//Transform position and ray to local space
		pos = ROTATION_MATRIX.transformVec(pos, centerPos);
		ray = ROTATION_MATRIX.transformVec(ray, Vec3.createVectorHelper(0, 0, 0));

		for(int i = 0; i <= 19; i++) {
			if(!this.isShieldActive(i))
				continue;
			double v3[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][0]];
			double v2[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][1]];
			double v1[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			double a = len + SHIELD_EXPLOSION;
			Vec3 center = Vec3.createVectorHelper(centerX, centerY, centerZ);
			centerX += this.posX + SHIELD_OFFSET_X;
			centerY += this.posY + SHIELD_OFFSET_Y;
			centerZ += this.posZ + SHIELD_OFFSET_Z;
			Vec3 vert1Exploded = Vec3.createVectorHelper(v1[0], v1[1], v1[2]);
			double b = vert1Exploded.dotProduct(center);
			double d = a * Math.tan(b);
			double vertexExplode = Math.sqrt(a*a + d*d) - 1;
			Vec3 v1Normalized = Vec3.createVectorHelper(v1[0], v1[1], v1[2]).normalize();
			Vec3 v2Normalized = Vec3.createVectorHelper(v2[0], v2[1], v2[2]).normalize();
			Vec3 v3Normalized = Vec3.createVectorHelper(v3[0], v3[1], v3[2]).normalize();
			Vec3 vert1 = Vec3.createVectorHelper(v1[0]+v1Normalized.xCoord*vertexExplode, v1[1]+v1Normalized.yCoord*vertexExplode, v1[2]+v1Normalized.zCoord*vertexExplode);
			Vec3 vert2 = Vec3.createVectorHelper(v2[0]+v2Normalized.xCoord*vertexExplode, v2[1]+v2Normalized.yCoord*vertexExplode, v2[2]+v2Normalized.zCoord*vertexExplode);
			Vec3 vert3 = Vec3.createVectorHelper(v3[0]+v3Normalized.xCoord*vertexExplode, v3[1]+v3Normalized.yCoord*vertexExplode, v3[2]+v3Normalized.zCoord*vertexExplode);
			vert1 = vert1.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vert2 = vert2.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vert3 = vert3.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			Vec3 normal = vert2.subtract(vert1).crossProduct(vert3.subtract(vert1));
			if(this.rayTraceTriangle(pos, ray, vert1, vert2, vert3) && (back || normal.normalize().dotProduct(ray.normalize()) < Math.cos(Math.toRadians(90)))) {
				double dx = centerX - pos.xCoord;
				double dy = centerY - pos.yCoord;
				double dz = centerZ - pos.zCoord;
				double pdx = centroidX - pos.xCoord;
				double pdy = centroidY - pos.yCoord;
				double pdz = centroidZ - pos.zCoord;
				if(shield == -1 || (Math.sqrt(dx*dx+dy*dy+dz*dz) < Math.sqrt(pdx*pdx+pdy*pdy+pdz*pdz))) {
					shield = i;
					centroidX = centerX;
					centroidY = centerY;
					centroidZ = centerZ;
				}
			}
		}

		return shield;
	}

	private boolean rayTraceTriangle(Vec3 pos, Vec3 ray, Vec3 v0, Vec3 v1, Vec3 v2) {
		final double epsilon = 0.00001;
		Vec3 diff1 = v1.subtract(v0);
		Vec3 diff2 = v2.subtract(v0);
		Vec3 rayCross = ray.crossProduct(diff2);
		double angleDifference = diff1.dotProduct(rayCross);
		if (angleDifference > -epsilon && angleDifference < epsilon)
			return false;
		double f = 1.0D / angleDifference;
		Vec3 s = pos.subtract(v0);
		double u = f * (s.dotProduct(rayCross));
		if (u < 0.0 || u > 1.0) {
			return false;
		}
		Vec3 q = s.crossProduct(diff1);
		double v = f * ray.dotProduct(q);
		if (v < 0.0 || u + v > 1.0) {
			return false;
		}
		double t = -f * diff2.dotProduct(q);
		if (t > epsilon) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			if(((EntityDamageSource)source).getEntity() instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) ((EntityDamageSource)source).getEntity();
				Vec3 ray = living.getLookVec();
				ray.xCoord = ray.xCoord * 640.0D;
				ray.yCoord = ray.yCoord * 640.0D;
				ray.zCoord = ray.zCoord * 640.0D;
				Vec3 pos = Vec3.createVectorHelper(living.posX, living.posY + living.getEyeHeight() + (living instanceof EntityPlayer && ((EntityPlayer)living).isSneaking() ? -0.08D : 0.0D), living.posZ);
				if(this.hasShield()) {
					int shieldHit = this.rayTraceShield(pos, ray, false);
					if(shieldHit >= 0) {
						if(!this.worldObj.isRemote && living.isSneaking())
							this.setShieldActive(shieldHit, false);
						if(this.worldObj.isRemote) {
							this.shieldAnimationTicks[shieldHit] = 20;
							this.worldObj.playSound(this.posX, this.posY, this.posZ, "random.anvil_land", 1.0F, 1.0F, false);
						}
						double dx = living.posX - this.posX;
						double dy = living.posY - this.posY;
						double dz = living.posZ - this.posZ;
						double len = Math.sqrt(dx*dx+dy*dy+dz*dz);
						living.motionX = dx / len * 0.8F;
						living.motionY = dy / len * 0.8F;
						living.motionZ = dz / len * 0.8F;
						living.attackEntityFrom(DamageSource.magic, 2);
						return false;
					}
				}
				MovingObjectPosition result = this.coreBoundingBox.getOffsetBoundingBox(this.posX, this.posY, this.posZ).calculateIntercept(pos, ray.addVector(pos.xCoord, pos.yCoord, pos.zCoord));
				if(result != null) {
					return super.attackEntityFrom(source, damage);
				} else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("shields", this.packShieldData());
		nbt.setDouble("anchorX", this.anchorX);
		nbt.setDouble("anchorY", this.anchorY);
		nbt.setDouble("anchorZ", this.anchorZ);
		nbt.setDouble("anchorRadius", this.anchorRadius);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.unpackShieldData(nbt.getInteger("shields"));
		this.anchorX = nbt.getDouble("anchorX");
		this.anchorY = nbt.getDouble("anchorY");
		this.anchorZ = nbt.getDouble("anchorZ");
		this.anchorRadius = nbt.getDouble("anchorRadius");
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		super.onSpawnWithEgg(data);
		this.anchorX = this.posX;
		this.anchorY = this.posY;
		this.anchorZ = this.posZ;
		this.anchorRadius = 10.0D;
		return data;
	}

	@Override
	public void onUpdate() {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		
		super.onUpdate();
		this.lastShieldRotationYaw = this.shieldRotationYaw;
		this.lastShieldRotationPitch = this.shieldRotationPitch;
		this.lastShieldRotationRoll = this.shieldRotationRoll;


		float shieldRotation;
		if(this.worldObj.isRemote) {
			shieldRotation = this.dataWatcher.getWatchableObjectFloat(SHIELD_ROTATION_DW);
		} else {
			shieldRotation = this.ticksExisted;
			this.dataWatcher.updateObject(SHIELD_ROTATION_DW, shieldRotation);
		}
		this.shieldRotationYaw = shieldRotation * 2.0F;
		this.shieldRotationPitch = shieldRotation * 3.0F;
		this.shieldRotationRoll = shieldRotation * 5.0F;

		if(!this.worldObj.isRemote) {
			this.dataWatcher.updateObject(SHIELD_DW, this.packShieldData());

			if(this.getDistance(this.anchorX, this.anchorY, this.anchorZ) > this.anchorRadius) {
				this.setPosition(this.anchorX, this.anchorY, this.anchorZ);
			}
		} else {
			this.unpackShieldData(this.dataWatcher.getWatchableObjectInt(SHIELD_DW));
			for(int i = 0; i <= 19; i++) {
				if(this.shieldAnimationTicks[i] == 0 && this.worldObj.rand.nextInt(50) == 0)
					this.shieldAnimationTicks[i] = 40;
				if(this.shieldAnimationTicks[i] > 0) {
					this.shieldAnimationTicks[i]--;
					if(this.shieldAnimationTicks[i] == 20)
						this.shieldAnimationTicks[i] = 0;
				}
			}
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if (this.isInWater()) {
			this.moveFlying(strafe, forward, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.handleLavaMovement()) {
			this.moveFlying(strafe, forward, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveFlying(strafe, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.posX - this.prevPosX;
		double dz = this.posZ - this.prevPosZ;
		float distanceMoved = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}
}
