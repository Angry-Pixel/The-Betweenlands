package thebetweenlands.common.entity.fishing.anadia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.fishing.FishBait;

import java.util.Collections;
import java.util.List;

public class AnadiaFindBaitGoal extends Goal {

	private final Anadia anadia;
	private final double searchRange;
	@Nullable
	public FishBait bait = null;

	public AnadiaFindBaitGoal(Anadia anadia, double searchRange) {
		this.anadia = anadia;
		this.searchRange = searchRange;
	}

	@Override
	public boolean canUse() {
		return this.bait == null;
		//return anadia.getHungerCooldown() <= 0 && bait == null;
	}

	@Override
	public void start() {
		if (this.bait == null)
			this.bait = this.getClosestBait(this.searchRange);
	}

	@Override
	public boolean canContinueToUse() {
		return this.bait != null && !this.bait.isRemoved();
		//	return anadia.getHungerCooldown() <= 0 && bait != null && !bait.isDead;
	}

	@Override
	public void tick() {
		if (!this.anadia.level().isClientSide() && this.canContinueToUse()) {

			if (this.bait != null) {
				float distance = this.bait.distanceTo(this.anadia);
				BlockPos hookPos = this.bait.blockPosition();
				if (this.bait.hasPickUpDelay()) {

					if (distance >= 1F) {
						this.anadia.getLookControl().setLookAt(hookPos.getX(), hookPos.getY(), hookPos.getZ(), 20.0F, 8.0F);
						this.moveToItem(this.bait);
					}

					if (distance <= 2F)
						if (this.anadia.isInWater() && this.anadia.level().isEmptyBlock(hookPos.above()) && this.anadia.hasLineOfSight(this.bait))
							this.anadia.leapAtTarget(hookPos.getX(), hookPos.getZ());

					if (distance <= 1F) {
						this.anadia.getMoveControl().setWantedPosition(hookPos.getX(), hookPos.getY(), hookPos.getZ(), this.anadia.getAttributeValue(Attributes.MOVEMENT_SPEED));
						//anadia.setHungerCooldown(bait.getBaitSaturation());
						this.bait.getItem().shrink(1);
						if (this.bait.getItem().getCount() <= 0)
							this.bait.discard();
						this.anadia.setIsLeaping(false);
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
		List<FishBait> list = this.anadia.level().getEntitiesOfClass(FishBait.class, this.anadia.getBoundingBox().inflate(distance), bait -> bait.isInWater() && bait.tickCount < bait.lifespan);
		if (list.isEmpty())
			return null;
		Collections.shuffle(list);
		return list.getFirst();
	}

	public void moveToItem(FishBait bait) {
		Path pathentity = this.anadia.getNavigation().getPath();
		if (pathentity != null) {
			//entity.getNavigator().setPath(pathentity, 0.5D);
			this.anadia.getNavigation().moveTo(bait.getX(), bait.getY(), bait.getZ(), this.anadia.getAttributeValue(Attributes.MOVEMENT_SPEED));
		}
	}
}
