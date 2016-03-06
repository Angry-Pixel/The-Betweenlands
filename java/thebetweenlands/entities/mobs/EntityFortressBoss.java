package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFortressBoss extends EntityMob implements IEntityBL {
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

	private boolean[] activeShields = new boolean[20];
	private float shieldExplosion = 0.2F;

	public final AxisAlignedBB coreBoundingBox;

	public EntityFortressBoss(World world) {
		super(world);
		float width = 2.0F;
		float height = 2.0F;
		this.setSize(width, height);
		float coreWidth = 1.0F;
		float coreHeight = 1.0F;
		this.coreBoundingBox = AxisAlignedBB.getBoundingBox(-coreWidth/2.0F, 0F + height / 4.0F, -coreWidth/2.0F, coreWidth/2.0F, coreHeight + height / 4.0F, coreWidth/2.0F);
		for(int i = 0; i < 20; i++) {
			this.activeShields[i] = true;
		}
	}

	@Override
	public String pageName() {
		return "fortressBoss";
	}

	public float getShieldExplosion() {
		return this.shieldExplosion;
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

	public int rayTraceShield(Vec3 pos, Vec3 ray, boolean back) {
		int shield = -1;
		double centroidX = 0;
		double centroidY = 0;
		double centroidZ = 0;

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
			Vec3 vec1 = Vec3.createVectorHelper(v1[0]+centerX/len*this.getShieldExplosion(), v1[1]+centerY/len*this.getShieldExplosion(), v1[2]+centerZ/len*this.getShieldExplosion());
			Vec3 vec2 = Vec3.createVectorHelper(v2[0]+centerX/len*this.getShieldExplosion(), v2[1]+centerY/len*this.getShieldExplosion(), v2[2]+centerZ/len*this.getShieldExplosion());
			Vec3 vec3 = Vec3.createVectorHelper(v3[0]+centerX/len*this.getShieldExplosion(), v3[1]+centerY/len*this.getShieldExplosion(), v3[2]+centerZ/len*this.getShieldExplosion());
			vec1 = vec1.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vec2 = vec2.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vec3 = vec3.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			Vec3 normal = vec2.subtract(vec1).crossProduct(vec3.subtract(vec1));
			centerX += this.posX + SHIELD_OFFSET_X;
			centerY += this.posY + SHIELD_OFFSET_Y;
			centerZ += this.posZ + SHIELD_OFFSET_Z;
			if(this.rayTraceTriangle(pos, ray, vec1, vec2, vec3) && (back || normal.dotProduct(ray) < Math.toRadians(45))) {
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
				Vec3 pos = Vec3.createVectorHelper(living.posX, living.posY + living.getEyeHeight(), living.posZ);
				if(this.hasShield()) {
					int shieldHit = this.rayTraceShield(pos, ray, false);
					if(shieldHit >= 0) {
						this.setShieldActive(shieldHit, false);
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
		return super.attackEntityFrom(source, damage);
	}
}
