package thebetweenlands.common.entity.draeton;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.entity.IPullerEntity;

public class DraetonPhysicsPart {
	public static enum Type {
		PULLER, ANCHOR
	}
	
	public final Type type;
	
	public final EntityDraeton carriage;
	private Entity entity;

	public final int id; //network ID of the puller, used for sync

	public double prevX, prevY, prevZ;
	public double x, y, z;
	public double motionX, motionY, motionZ;

	public int lerpSteps;
	public double lerpX;
	public double lerpY;
	public double lerpZ;

	public final float width = 0.5f;
	public final float height = 0.5f;

	public boolean isActive = true;
	
	public boolean grounded = false;

	public DraetonPhysicsPart(Type type, EntityDraeton carriage, int id) {
		this.type = type;
		this.carriage = carriage;
		this.id = id;
	}

	public void setEntity(IPullerEntity entity) {
		if(entity instanceof Entity) {
			this.entity = (Entity) entity;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends Entity & IPullerEntity> T getEntity() {
		return (T) this.entity;
	}

	public AxisAlignedBB getAabb() {
		return new AxisAlignedBB(this.x - this.width / 2, this.y, this.z - this.width / 2, this.x + this.width / 2, this.y + this.height, this.z + this.width / 2);
	}

	private void setPosToAabb(AxisAlignedBB aabb) {
		this.x = aabb.minX + this.width / 2;
		this.y = aabb.minY;
		this.z = aabb.minZ + this.width / 2;
	}

	public void move(double x, double y, double z) {
		double ty = y;
		
		List<AxisAlignedBB> collisionBoxes = this.carriage.world.getCollisionBoxes(null, this.getAabb().expand(x, y, z));

		if (y != 0.0D) {
			int k = 0;

			for (int l = collisionBoxes.size(); k < l; ++k) {
				y = ((AxisAlignedBB)collisionBoxes.get(k)).calculateYOffset(this.getAabb(), y);
			}

			this.setPosToAabb(this.getAabb().offset(0.0D, y, 0.0D));
		}

		if (x != 0.0D) {
			int j5 = 0;

			for (int l5 = collisionBoxes.size(); j5 < l5; ++j5) {
				x = ((AxisAlignedBB)collisionBoxes.get(j5)).calculateXOffset(this.getAabb(), x);
			}

			if (x != 0.0D) {
				this.setPosToAabb(this.getAabb().offset(x, 0.0D, 0.0D));
			}
		}

		if (z != 0.0D) {
			int k5 = 0;

			for (int i6 = collisionBoxes.size(); k5 < i6; ++k5) {
				z = ((AxisAlignedBB)collisionBoxes.get(k5)).calculateZOffset(this.getAabb(), z);
			}

			if (z != 0.0D) {
				this.setPosToAabb(this.getAabb().offset(0.0D, 0.0D, z));
			}
		}
		
		if(Math.abs(ty - y) > 0.0001f) {
			this.grounded = true;
		} else {
			this.grounded = false;
		}
	}

	public void tickLerp() {
		if (this.lerpSteps > 0) {
			this.x = this.x + (this.lerpX - this.x) / (double)this.lerpSteps;
			this.y = this.y + (this.lerpY - this.y) / (double)this.lerpSteps;
			this.z = this.z + (this.lerpZ - this.z) / (double)this.lerpSteps;
			--this.lerpSteps;
		}
	}
}