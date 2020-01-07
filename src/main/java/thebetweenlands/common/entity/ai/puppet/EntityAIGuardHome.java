package thebetweenlands.common.entity.ai.puppet;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAIGuardHome extends EntityAIBase {
	private final EntityCreature entity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private final double movementSpeed;
	private final int maxDist;

	public EntityAIGuardHome(EntityCreature entity, double speed, int maxDist) {
		this.entity = entity;
		this.movementSpeed = speed;
		this.maxDist = maxDist;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		IPuppetCapability cap = this.entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);

		if(cap != null && cap.hasPuppeteer() && cap.getGuard()) {
			BlockPos homePos = cap.getGuardHome();

			if(homePos == null || homePos.distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ) < this.maxDist * this.maxDist) {
				return false;
			} else {
				boolean hadHome = this.entity.hasHome();
				BlockPos prevHome = this.entity.getHomePosition();
				float prevRange = this.entity.getMaximumHomeDistance();

				this.entity.setHomePosAndDistance(homePos, this.maxDist);

				Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 7, new Vec3d(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D));

				if(hadHome) {
					this.entity.setHomePosAndDistance(prevHome, (int)prevRange);
				} else {
					this.entity.detachHome();
				}

				if (vec3d == null) {
					return false;
				} else {
					this.movePosX = vec3d.x;
					this.movePosY = vec3d.y;
					this.movePosZ = vec3d.z;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
	}
}