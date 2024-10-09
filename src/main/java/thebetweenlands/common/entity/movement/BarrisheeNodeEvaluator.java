package thebetweenlands.common.entity.movement;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;

public class BarrisheeNodeEvaluator extends WalkNodeEvaluator {
	private final Barrishee barrishee;

	public BarrisheeNodeEvaluator(Barrishee barrishee) {
		this.barrishee = barrishee;
	}

	@Override
	public PathType getPathType(PathfindingContext context, int x, int y, int z) {
		PathType type = super.getPathType(context, x, y, z);

		if (this.barrishee == null) return type;

		if(type == PathType.OPEN || type == PathType.WALKABLE) {
			return type;
		}

		BlockPos pos = new BlockPos(x, y, z);

		var storage = BetweenlandsWorldStorage.get(this.barrishee.level());

		if (storage != null) {
			List<LocationSludgeWormDungeon> locations = storage.getLocalStorageHandler().getLocalStorages(LocationSludgeWormDungeon.class, new AABB(pos), location -> location.getGuard() != null);

			if (!locations.isEmpty()) {
				boolean isWalkable = true;

				for (LocationGuarded location : locations) {
					if (location.getGuard().isGuarded(this.barrishee.level(), this.barrishee, pos)) {
						isWalkable = false;
						break;
					} else {
						boolean allNeighborsProtected = true;

						for (Direction offset : Direction.Plane.HORIZONTAL) {
							if (!location.getGuard().isGuarded(this.barrishee.level(), this.barrishee, pos.relative(offset))) {
								allNeighborsProtected = false;
								break;
							}
						}

						if (allNeighborsProtected) {
							return type;
						}
					}
				}

				return isWalkable ? PathType.OPEN : PathType.BLOCKED;
			}
		}
		return type;
	}
}