package thebetweenlands.common.entity.movement;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public class ClimberMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	protected final EntityClimberBase climber;

	public ClimberMoveHelper(EntityClimberBase entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		double speed = this.climber.getMovementSpeed() * this.speed;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			this.action = EntityMoveHelper.Action.WAIT;

			Pair<EnumFacing, Vec3d> walkingSide = this.climber.getWalkingSide();
			Vec3d normal = new Vec3d(walkingSide.getLeft().getXOffset(), walkingSide.getLeft().getYOffset(), walkingSide.getLeft().getZOffset());

			double dx = this.posX - this.entity.posX;
			double dy = this.posY + 0.5f - (this.entity.posY + this.entity.height / 2.0f);
			double dz = this.posZ - this.entity.posZ;

			Vec3d dir = new Vec3d(dx, dy, dz);

			Vec3d targetDir = dir.subtract(normal.scale(dir.dotProduct(normal)));
			double targetDist = targetDir.length();
			targetDir = targetDir.normalize();

			if(targetDist < 0.0001D) {
				this.entity.setMoveForward(0);
			} else {
				EntityClimberBase.Orientation orientation = this.climber.getOrientation(1);

				float rx = (float)orientation.forward.dotProduct(targetDir);
				float ry = (float)orientation.right.dotProduct(targetDir);

				this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, 270.0f - (float)Math.toDegrees(Math.atan2(rx, ry)), 90.0f);

				this.entity.setAIMoveSpeed((float)speed);
			}
		} else if(this.action == EntityMoveHelper.Action.WAIT) {
			this.entity.setMoveForward(0);
		}
	}
}