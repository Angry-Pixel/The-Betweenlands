package thebetweenlands.common.entity.draeton;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.api.entity.IPullerEntity;

public class DraetonPhysicsPart {
	public static enum Type {
		PULLER, ANCHOR
	}
	
	public final Type type;
	
	public final EntityDraeton carriage;
	private Entity entity;
	
	public final int slot; //slot that this puller belongs to

	public final int id; //network ID of the puller, used for sync

	public double prevX, prevY, prevZ;
	public double x, y, z;
	public double motionX, motionY, motionZ;

	public int lerpSteps;
	public double lerpX;
	public double lerpY;
	public double lerpZ;

	public boolean isActive = true;
	
	public boolean grounded = false;

	public DraetonPhysicsPart(Type type, EntityDraeton carriage, int id, int slot) {
		this.type = type;
		this.carriage = carriage;
		this.id = id;
		this.slot = slot;
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
		return this.carriage.getAabb(this);
	}

	private void setPosToAabb(AxisAlignedBB aabb) {
		this.carriage.setPosToAabb(this, aabb);
	}
	
	public float getWidth() {
		return this.carriage.getWidth(this);
	}
	
	public float getHeight() {
		return this.carriage.getHeight(this);
	}

	public void move(double x, double y, double z) {
		if(!this.carriage.canCollide(this)) {
			this.grounded = false;
			this.setPosToAabb(this.getAabb().offset(x, y, z));
			return;
		}
		
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