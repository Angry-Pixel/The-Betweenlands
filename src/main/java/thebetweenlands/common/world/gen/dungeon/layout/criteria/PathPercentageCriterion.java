package thebetweenlands.common.world.gen.dungeon.layout.criteria;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Cell;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link.Bound;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.Pathfinder;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.PathfinderLinkMeta;

public class PathPercentageCriterion extends LayoutCriterion<Pathfinder<?>> {
	private final float maxRatio;
	private final int maxRetries;
	private final boolean abortIfFailed;

	public PathPercentageCriterion(float maxRatio, int maxRetries, boolean abortIfFailed) {
		this.maxRatio = maxRatio;
		this.maxRetries = maxRetries;
		this.abortIfFailed = abortIfFailed;
	}

	@Override
	public void check(Pathfinder<?> phase, boolean result, int iteration) {
		if(iteration >= this.maxRetries) {
			if(this.abortIfFailed) {
				this.setAbort();
			}
		} else {
			int roomVolume = 0;
			for(Cell cell : phase.getGrid().getCells()) {
				roomVolume += cell.getTileSizeX() * cell.getTileSizeY() * cell.getTileSizeZ();
			}

			int pathVolume = 0;
			for(Link link : phase.getGrid().getLinks()) {
				PathfinderLinkMeta meta = null;

				if(PathfinderLinkMeta.class.isAssignableFrom(phase.getMetaClass())) {
					meta = (PathfinderLinkMeta) link.getMeta(phase);
				}

				if(meta == null || meta.pieces.isEmpty()) {
					for(Bound bound : link.getBounds()) {
						pathVolume += (bound.maxX + 1 - bound.minX) * (bound.maxY + 1 - bound.minY) * (bound.maxZ + 1 - bound.minZ);
					}
				} else {
					for(PathfinderLinkMeta.Piece piece : meta.pieces) {
						if(piece.shape == PathfinderLinkMeta.Shape.STAIRS) {
							pathVolume += 2 * piece.length;
						} else {
							pathVolume++;
						}
					}
				}
			}

			float ratio = (float)pathVolume / (float)(pathVolume + roomVolume);

			if(ratio > this.maxRatio) {
				this.setRetryFully();
			}
		}
	}
}
