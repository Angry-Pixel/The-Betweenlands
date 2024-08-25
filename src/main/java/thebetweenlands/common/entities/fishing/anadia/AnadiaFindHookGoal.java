package thebetweenlands.common.entities.fishing.anadia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import thebetweenlands.common.entities.fishing.BLFishHook;

import java.util.Collections;
import java.util.List;

public class AnadiaFindHookGoal extends Goal {

	private final Anadia anadia;
	private final double searchRange;
	public BLFishHook hook = null;

	public AnadiaFindHookGoal(Anadia anadia, double searchRange) {
		this.anadia = anadia;
		this.searchRange = searchRange;
	}

	@Override
	public boolean canUse() {
		return this.hook == null;
//		return this.anadia.getHungerCooldown() <= 0 && hook == null;
	}

	@Override
	public void start() {
		if (this.hook == null)
			this.hook = this.getClosestHook(this.searchRange);
	}

	@Override
	public boolean canContinueToUse() {
		return this.hook != null && !this.hook.isRemoved() && this.hook.getBaited() || this.hook != null && !this.hook.isRemoved() && (this.anadia.level().getRandom().nextInt(50) == 0 && !this.hook.getBaited());
//		return anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead && hook.getBaited() ? true : anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead && (anadia.getEntityWorld().rand.nextInt(50) == 0 && !hook.getBaited());
	}

	@Override
	public void tick() {
		if (!this.anadia.level().isClientSide() && this.canContinueToUse()) {
			if (this.hook != null && this.hook.getHookedIn() == null) {
				float distance = this.hook.distanceTo(this.anadia);
				BlockPos hookPos = this.hook.blockPosition();

				if (distance >= 1F) {
					this.anadia.getLookControl().setLookAt(hookPos.getX(), hookPos.getY(), hookPos.getZ(), 20.0F, 8.0F);
					this.moveToEntity(this.hook);
				}

				if (distance <= 2F)
					if (this.anadia.isInWater() && this.anadia.level().isEmptyBlock(hookPos.above()) && this.anadia.hasLineOfSight(this.hook))
						this.anadia.leapAtTarget(hookPos.getX(), hookPos.getZ());

				if (distance <= 1F) {
					this.anadia.getMoveControl().setWantedPosition(hookPos.getX(), hookPos.getY(), hookPos.getZ(), this.anadia.getAttributeValue(Attributes.MOVEMENT_SPEED));
					//	anadia.setHungerCooldown(600);
					this.anadia.setIsLeaping(false);
					this.hook.setHookedEntity(this.anadia);
					this.anadia.randomiseObstructionOrder();
					this.hook.startRiding(this.anadia, true);
					this.hook.setBaited(false);
					//resetTask();
				}
			}
		}
	}

	@Override
	public void stop() {
		this.hook = null;
	}

	public BLFishHook getClosestHook(double distance) {
		List<BLFishHook> list = this.anadia.level().getEntitiesOfClass(BLFishHook.class, this.anadia.getBoundingBox().inflate(distance), Entity::isInWater);

		if (list.isEmpty())
			return null;
		Collections.shuffle(list);
		return list.getFirst();
	}

	public void moveToEntity(BLFishHook hook) {
		Path pathentity = this.anadia.getNavigation().getPath();
		if (pathentity != null) {
			//entity.getNavigator().setPath(pathentity, 0.5D);
			this.anadia.getNavigation().moveTo(hook.getX(), hook.getY(), hook.getZ(), this.anadia.getAttributeValue(Attributes.MOVEMENT_SPEED));
		}
	}
}
