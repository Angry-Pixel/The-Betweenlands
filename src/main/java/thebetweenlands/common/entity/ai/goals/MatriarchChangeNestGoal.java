package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class MatriarchChangeNestGoal extends Goal {

	private final ChiromawMatriarch matriarch;

	protected float maxRangeSq = 180 * 180;

	private int checkTimer = 0;

	private int idleChangeCounter = 0;

	public MatriarchChangeNestGoal(ChiromawMatriarch matriarch) {
		this.matriarch = matriarch;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (!this.matriarch.isReturningToNest() && this.matriarch.getTarget() == null && this.checkTimer-- <= 0) {
			this.checkTimer = 10;

			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.getForLevelNullable(this.matriarch.level());

			if (worldStorage == null) return false;

			List<LocationStorage> priorityNests = new ArrayList<>();
			List<LocationStorage> otherNests = new ArrayList<>();

			for (ILocalStorage localStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {

				if (localStorage instanceof LocationStorage location && localStorage.getBoundingBox() != null) {

					Vec3 center = location.getBoundingBox().getCenter();

					if (center.distanceToSqr(this.matriarch.position()) < this.maxRangeSq && (location.getType() == EnumLocationType.FLOATING_ISLAND || location.getType() == EnumLocationType.CHIROMAW_MATRIARCH_NEST) &&
						(this.matriarch.getRandom().nextInt(15) == 0 || this.matriarch.level().getEntitiesOfClass(this.matriarch.getClass(), location.getBoundingBox()).isEmpty() /*Avoid already occupied places*/)) {
						if (location instanceof LocationChiromawMatriarchNest && this.matriarch.level().getNearestPlayer(center.x, center.y, center.z, 32, true) != null) {
							//Prioritise nests with players nearby
							priorityNests.add(location);
						} else {
							//Prioritise nests over islands
							if (location instanceof LocationChiromawMatriarchNest || this.matriarch.getRandom().nextInt(3) == 0) {
								otherNests.add(location);
							}
						}
					}
				}

			}

			LocationStorage nest = null;
			if (!priorityNests.isEmpty()) {
				Collections.shuffle(priorityNests);
				nest = priorityNests.getFirst();
				this.matriarch.returnFast = true;
			} else if (!otherNests.isEmpty() && this.idleChangeCounter++ >= 8) {
				this.idleChangeCounter = 0;
				Collections.shuffle(otherNests);
				nest = otherNests.getFirst();
				this.matriarch.returnFast = false;
			}

			if (nest != null) {
				BlockPos nestPos = null;
				if (nest instanceof LocationChiromawMatriarchNest nestLoc) {
					nestPos = nestLoc.getNestPosition();
					if (nestPos == null && nest.getBoundingBox() != null) {
						nestPos = BlockPos.containing(nest.getBoundingBox().getCenter());
					}
				} else {
					AABB locationBB = nest.getBoundingBox();
					if (locationBB != null) {
						nestPos = BlockPos.containing(locationBB.getCenter());
					}
				}

				if (nestPos != null) {
					nestPos = this.matriarch.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, nestPos);

					if (this.matriarch.getBoundOrigin() == null || !nestPos.equals(this.matriarch.getBoundOrigin().pos())) {
						this.matriarch.setBoundOrigin(GlobalPos.of(this.matriarch.level().dimension(), nestPos));
						this.matriarch.setNesting(false);
						this.matriarch.setReturnToNest(true);
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}
}
