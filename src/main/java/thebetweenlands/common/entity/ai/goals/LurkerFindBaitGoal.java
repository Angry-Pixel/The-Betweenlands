package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import thebetweenlands.common.entity.creature.Lurker;
import thebetweenlands.common.entity.fishing.FishBait;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class LurkerFindBaitGoal extends Goal {

	private final Lurker lurker;
	private final double searchRange;
	@Nullable
	public FishBait bait = null;

	public LurkerFindBaitGoal(Lurker lurker, double searchRange) {
		this.lurker = lurker;
		this.searchRange = searchRange;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return this.lurker.huntingTimer <= 0 && this.bait == null;
	}

	@Override
	public void start() {
		if (this.bait == null)
			this.bait = this.getClosestBait(this.searchRange);
	}

	@Override
	public boolean canContinueToUse() {
		return this.lurker.huntingTimer <= 0 && this.bait != null && this.bait.isAlive();
	}

	@Override
	public void tick() {
		if (!this.lurker.level().isClientSide() && this.canContinueToUse()) {

			if (this.bait != null) {
				float distance = this.bait.distanceTo(this.lurker);
				double x = this.bait.getX();
				double y = this.bait.getY();
				double z = this.bait.getZ();

				if (this.bait.hasPickUpDelay()) {
					if (distance >= 1.0F) {
						this.lurker.getLookControl().setLookAt(x, y, z, 20.0F, 8.0F);
						this.moveToItem(this.bait);
					}

					if (distance <= 3.0F) {
						this.lurker.getMoveControl().setWantedPosition(x, y, z, this.lurker.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2D);
						this.bait.getItem().shrink(1);
						if (this.bait.getItem().getCount() <= 0)
							this.bait.discard();
						this.lurker.setHuntingTimer(2400);
						this.stop();
					}
				}
			}
		}
	}

	@Override
	public void stop() {
		this.bait = null;
	}

	@Nullable
	public FishBait getClosestBait(double distance) {
		List<FishBait> list = this.lurker.level().getEntitiesOfClass(FishBait.class, this.lurker.getBoundingBox().inflate(distance), bait -> bait.tickCount < bait.lifespan && bait.isInWater() && bait.isCurrentlyGlowing());
		if (!list.isEmpty()) {
			Collections.shuffle(list);
			return list.getFirst();
		}
		return null;
	}

	public void moveToItem(FishBait bait) {
		Path pathentity = this.lurker.getNavigation().getPath();
		if (pathentity != null) {
			this.lurker.getNavigation().moveTo(bait, this.lurker.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2D);
		}
	}
}
