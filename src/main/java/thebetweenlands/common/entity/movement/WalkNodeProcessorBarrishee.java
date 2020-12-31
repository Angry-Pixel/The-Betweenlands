package thebetweenlands.common.entity.movement;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;

public class WalkNodeProcessorBarrishee extends WalkNodeProcessor {
	protected EntityBarrishee barrishee;

	@Override
	public void init(IBlockAccess sourceIn, EntityLiving mob) {
		this.barrishee = (EntityBarrishee) mob;

		super.init(sourceIn, mob);
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess world, int x, int y, int z, int xSize, int ySize, int zSize,
			boolean canOpenDoorsIn, boolean canEnterDoorsIn, EnumSet<PathNodeType> nodeTypeSet,
			PathNodeType nodeTypeOrigin, BlockPos entityPos) {

		for (int i = 0; i < xSize; ++i) {
			for (int j = 0; j < ySize; ++j) {
				for (int k = 0; k < zSize; ++k) {
					int xo = i + x;
					int yo = j + y;
					int zo = k + z;

					PathNodeType nodeType = this.getPathNodeType(world, xo, yo, zo);

					if (nodeType == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoorsIn && canEnterDoorsIn) {
						nodeType = PathNodeType.WALKABLE;
					}

					if (nodeType == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
						nodeType = PathNodeType.BLOCKED;
					}

					if (nodeType == PathNodeType.RAIL && !(world.getBlockState(entityPos).getBlock() instanceof BlockRailBase) && !(world.getBlockState(entityPos.down()).getBlock() instanceof BlockRailBase)) {
						nodeType = PathNodeType.FENCE;
					}

					if (i == 0 && j == 0 && k == 0) {
						nodeTypeOrigin = nodeType;
					}

					nodeTypeSet.add(nodeType);
				}
			}
		}

		return nodeTypeOrigin;
	}

	@Override
	protected PathNodeType getPathNodeTypeRaw(IBlockAccess world, int x, int y, int z) {
		PathNodeType type = super.getPathNodeTypeRaw(world, x, y, z);

		if(type == PathNodeType.OPEN || type == PathNodeType.WALKABLE) {
			return type;
		}

		BlockPos pos = new BlockPos(x, y, z);

		List<LocationSludgeWormDungeon> locations = BetweenlandsWorldStorage.forWorld(this.barrishee.world).getLocalStorageHandler().getLocalStorages(LocationSludgeWormDungeon.class, new AxisAlignedBB(pos), location -> location.getGuard() != null);

		if(!locations.isEmpty()) {
			boolean isWalkable = true;

			for(LocationGuarded location : locations) {
				if(location.getGuard().isGuarded(this.barrishee.world, barrishee, pos)) {
					isWalkable = false;
					break;
				} else {
					boolean allNeighborsProtected = true;

					for(EnumFacing offset : EnumFacing.HORIZONTALS) {
						if(!location.getGuard().isGuarded(this.barrishee.world, barrishee, pos.offset(offset))) {
							allNeighborsProtected = false;
							break;
						}
					}

					if(allNeighborsProtected) {
						return type;
					}
				}
			}

			return isWalkable ? PathNodeType.OPEN : PathNodeType.BLOCKED;
		}

		return type;
	}
}