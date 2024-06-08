package thebetweenlands.common.entitys.ai.goals;

import java.util.Random;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entitys.movement.FlightMoveHelper;

public class EntityAIFlyRandomly<T extends Mob> extends Goal {

	protected final T entity;
	protected double distanceMultiplier = 1.0D;
	
		public EntityAIFlyRandomly(T entity) {
			this.entity = entity;
		}

		@Override
		public boolean canUse() {
			MoveControl entitymovehelper = this.entity.getMoveControl();

			if (!entitymovehelper.hasWanted()) {
				return true;
			} else {
				double dx = entitymovehelper.getWantedX() - this.entity.xo;
				double dy = entitymovehelper.getWantedY() - this.entity.yo;
				double dz = entitymovehelper.getWantedZ() - this.entity.zo;

				double distSq = dx * dx + dy * dy + dz * dz;

				return distSq < 1.0D || distSq > 3600.0D;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void start() {
			Random random = this.entity.getRandom();
			this.entity.getMoveControl().setWantedPosition(this.getTargetX(random, this.distanceMultiplier), this.getTargetY(random, this.distanceMultiplier), this.getTargetZ(random, this.distanceMultiplier), this.getFlightSpeed());
		}

		@Override
		public void tick() {
			MoveControl moveHelper = this.entity.getMoveControl();
			if(moveHelper instanceof FlightMoveHelper) {
				FlightMoveHelper flightMoveHelper = (FlightMoveHelper) moveHelper;

				if(flightMoveHelper.isBlocked()) {
					this.distanceMultiplier = Math.max(this.distanceMultiplier -= 0.04F, 0.1D);
				} else {
					this.distanceMultiplier = Math.min(this.distanceMultiplier += 0.15F, 1.0D);
				}
			}
		}

		/**
		 * Returns the flight speed
		 * @return
		 */
		protected double getFlightSpeed() {
			return 1.0D;
		}

		/**
		 * Returns the random position in the X direction
		 * @param rand Random
		 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
		 * @return
		 */
		protected double getTargetX(Random rand, double distanceMultiplier) {
			return this.entity.xo + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
		}

		/**
		 * Returns the random position in the Y direction
		 * @param rand Random
		 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
		 * @return
		 */
		protected double getTargetY(Random rand, double distanceMultiplier) {
			return this.entity.yo + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
		}

		/**
		 * Returns the random position in the Z direction
		 * @param rand Random
		 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
		 * @return
		 */
		protected double getTargetZ(Random rand, double distanceMultiplier) {
			return this.entity.zo + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
		}
}
