package thebetweenlands.common.world.gen.dungeon.layout.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Connector;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link.Bound;

public class SimplePathfinder extends Pathfinder<PathfinderLinkMeta> {

	@Override
	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		super.init(grid, rng, tagSupplier);

		this.setMeta(PathfinderLinkMeta.class, PathfinderLinkMeta::new, true);
	}

	@Override
	public boolean process() {
		for(Link link : this.grid.getLinks()) {
			List<Bound> bounds = link.getBounds();

			if(bounds.size() > 0) {
				PathfinderLinkMeta meta = link.getOrCreateMeta(this);

				if(meta.pieces.isEmpty()) {
					List<PathfinderLinkMeta.Piece> pieces = new ArrayList<>();

					Connector bottom;
					Connector top;

					if(link.getStart().getTileY() > link.getEnd().getTileY()) {
						Collections.reverse(bounds);
						bottom = link.getEnd();
						top = link.getStart();
					} else {
						bottom = link.getStart();
						top = link.getEnd();
					}

					int prevEndX = bottom.getTileX();
					int prevEndY = bottom.getTileY();
					int prevEndZ = bottom.getTileZ();

					for(int i = 0; i < bounds.size(); i++) {
						Bound bound = bounds.get(i);

						int startX;
						int endX;
						if(Math.abs(bound.minX - prevEndX) < Math.abs(bound.maxX - prevEndX)) {
							startX = bound.minX;
							endX = bound.maxX;
						} else {
							startX = bound.maxX;
							endX = bound.minX;
						}

						int startY;
						int endY;
						if(Math.abs(bound.minY - prevEndY) < Math.abs(bound.maxY - prevEndY)) {
							startY = bound.minY;
							endY = bound.maxY;
						} else {
							startY = bound.maxY;
							endY = bound.minY;
						}

						int startZ;
						int endZ;
						if(Math.abs(bound.minZ - prevEndZ) < Math.abs(bound.maxZ - prevEndZ)) {
							startZ = bound.minZ;
							endZ = bound.maxZ;
						} else {
							startZ = bound.maxZ;
							endZ = bound.minZ;
						}

						int indx = startX - prevEndX;
						int indy = startY - prevEndY;
						int indz = startZ - prevEndZ;

						int outdx;
						int outdy;
						int outdz;

						if(i == bounds.size() - 1) {
							outdx = (int)Math.signum(top.getTileX() - endX);
							outdy = (int)Math.signum(top.getTileY() - endY);
							outdz = (int)Math.signum(top.getTileZ() - endZ);
						} else {
							Bound next = bounds.get(i + 1);

							if(Math.abs(next.minX - endX) < Math.abs(next.maxX - endX)) {
								outdx = next.minX - endX;
							} else {
								outdx = next.maxX - endX;
							}

							if(Math.abs(next.minY - endY) < Math.abs(next.maxY - endY)) {
								outdy = next.minY - endY;
							} else {
								outdy = next.maxY - endY;
							}

							if(Math.abs(next.minZ - endZ) < Math.abs(next.maxZ - endZ)) {
								outdz = next.minZ - endZ;
							} else {
								outdz = next.maxZ - endZ;
							}
						}

						if(!this.placePieces(link, bound, startX, startY, startZ, endX, endY, endZ, indx, indy, indz, outdx, outdy, outdz, pieces)) {
							return false;
						}

						prevEndX = endX;
						prevEndY = endY;
						prevEndZ = endZ;
					}

					meta.pieces.addAll(pieces);
				}
			}
		}

		return true;
	}

	private boolean placePieces(Link link, Bound bound,
			int startX, int startY, int startZ, int endX, int endY, int endZ,
			int indx, int indy, int indz, int outdx, int outdy, int outdz,
			List<PathfinderLinkMeta.Piece> pieces) {

		int ascent = bound.maxY - bound.minY;

		if(ascent == 0) {
			if(indy > 0) {
				//Stairs top piece

				int dx = (int)Math.signum(endX - startX);
				int dz = (int)Math.signum(endZ - startZ);

				int corridorRotation;
				int corridordx;
				int corridordz;
				if(dx == 0 && dz == 0) {
					corridorRotation = getRotation(outdx, outdz);
					corridordx = outdx;
					corridordz = outdz;
				} else {
					corridorRotation = getRotation(dx, dz);
					corridordx = dx;
					corridordz = dz;
				}

				int endRotation;
				int enddx;
				int enddy;
				int enddz;
				PathfinderLinkMeta.Shape endShape;
				if(outdy > 0) {
					endRotation = corridorRotation;
					enddx = outdx;
					enddy = outdy;
					enddz = outdz;
					endShape = PathfinderLinkMeta.Shape.STAIRS_BOTTOM;
				} else if(Math.abs(dx - outdx) + Math.abs(dz - outdz) == 2) {
					endRotation = getRotation(dx, dz, outdx, outdz);
					enddx = outdx;
					enddy = 0;
					enddz = outdz;
					endShape = PathfinderLinkMeta.Shape.CORNER;
				} else {
					endRotation = getRotation(dx, dz);
					enddx = dx;
					enddy = 0;
					enddz = dz;
					endShape = PathfinderLinkMeta.Shape.STRAIGHT;
				}

				for(int x = bound.minX; x <= bound.maxX; x++) {
					for(int y = bound.minY; y <= bound.maxY; y++) {
						for(int z = bound.minZ; z <= bound.maxZ; z++) {
							if(x == startX && y == startY && z == startZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, 0, indy, 0, corridordx, 0, corridordz, corridorRotation, 1, PathfinderLinkMeta.Shape.STAIRS_TOP));
							} else if(x == endX && y == endY && z == endZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, corridordx, 0, corridordz, enddx, enddy, enddz, endRotation, 1, endShape));
							} else {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, corridordx, 0, corridordz, corridordx, 0, corridordz, corridorRotation, 1, PathfinderLinkMeta.Shape.STRAIGHT));
							}
						}
					}
				}
			} else if(outdy > 0) {
				//Stairs bottom piece

				int dx = (int)Math.signum(endX - startX);
				int dz = (int)Math.signum(endZ - startZ);

				int corridorRotation;
				int corridordx;
				int corridordz;
				if(dx == 0 && dz == 0) {
					corridorRotation = getRotation(indx, indz);
					corridordx = indx;
					corridordz = indz;
				} else {
					corridorRotation = getRotation(dx, dz);
					corridordx = dx;
					corridordz = dz;
				}

				int startRotation;
				int startdx;
				int startdy;
				int startdz;
				PathfinderLinkMeta.Shape startShape;
				if(indy > 0) {
					startRotation = corridorRotation;
					startdx = indx;
					startdy = indy;
					startdz = indz;
					startShape = PathfinderLinkMeta.Shape.STAIRS_TOP;
				} else if(Math.abs(indx - dx) + Math.abs(indz - dz) == 2) {
					startRotation = getRotation(indx, indz, dx, dz);
					startdx = indx;
					startdy = 0;
					startdz = indz;
					startShape = PathfinderLinkMeta.Shape.CORNER;
				} else {
					startRotation = getRotation(dx, dz);
					startdx = dx;
					startdy = 0;
					startdz = dz;
					startShape = PathfinderLinkMeta.Shape.STRAIGHT;
				}

				for(int x = bound.minX; x <= bound.maxX; x++) {
					for(int y = bound.minY; y <= bound.maxY; y++) {
						for(int z = bound.minZ; z <= bound.maxZ; z++) {
							if(x == endX && y == endY && z == endZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, corridordx, 0, corridordz, 0, outdy, 0, corridorRotation, 1, PathfinderLinkMeta.Shape.STAIRS_BOTTOM));
							} else if(x == startX && y == startY && z == startZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, startdx, startdy, startdz, corridordx, 0, corridordz, startRotation, 1, startShape));
							} else {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, corridordx, 0, corridordz, corridordx, 0, corridordz, corridorRotation, 1, PathfinderLinkMeta.Shape.STRAIGHT));
							}
						}
					}
				}
			} else {
				//Corridor with potential start and end L shapes

				int dx = (int)Math.signum(endX - startX);
				int dz = (int)Math.signum(endZ - startZ);

				int corridorRotation = getRotation(dx, dz);

				int startRotation;
				int startindx;
				int startindz;
				int startoutdx;
				int startoutdz;
				PathfinderLinkMeta.Shape startShape;

				int endRotation;
				int endindx;
				int endindz;
				int endoutdx;
				int endoutdz;
				PathfinderLinkMeta.Shape endShape;

				if(dx == 0 && dz == 0) {
					if(Math.abs(indx - outdx) + Math.abs(indz - outdz) == 2) {
						endRotation = startRotation = getRotation(indx, indz, outdx, outdz);
						endindx = startindx = indx;
						endindz = startindz = indz;
						endoutdx = startoutdx = outdx;
						endoutdz = startoutdz = outdz;
						endShape = startShape = PathfinderLinkMeta.Shape.CORNER;
					} else {
						endRotation = startRotation = getRotation(indx, indx);
						endindx = startindx = indx;
						endindz = startindz = indz;
						endoutdx = startoutdx = indx;
						endoutdz = startoutdz = indz;
						endShape = startShape = PathfinderLinkMeta.Shape.STRAIGHT;
					}
				} else {
					if(Math.abs(indx - dx) + Math.abs(indz - dz) == 2) {
						startRotation = getRotation(indx, indz, dx, dz);
						startindx = indx;
						startindz = indz;
						startoutdx = dx;
						startoutdz = dz;
						startShape = PathfinderLinkMeta.Shape.CORNER;
					} else {
						startRotation = getRotation(dx, dz);
						startindx = dx;
						startindz = dz;
						startoutdx = dx;
						startoutdz = dz;
						startShape = PathfinderLinkMeta.Shape.STRAIGHT;
					}

					if(Math.abs(dx - outdx) + Math.abs(dz - outdz) == 2) {
						endRotation = getRotation(dx, dz, outdx, outdz);
						endindx = dx;
						endindz = dz;
						endoutdx = outdx;
						endoutdz = outdz;
						endShape = PathfinderLinkMeta.Shape.CORNER;
					} else {
						endRotation = getRotation(dx, dz);
						endindx = dx;
						endindz = dz;
						endoutdx = dx;
						endoutdz = dz;
						endShape = PathfinderLinkMeta.Shape.STRAIGHT;
					}
				}

				for(int x = bound.minX; x <= bound.maxX; x++) {
					for(int y = bound.minY; y <= bound.maxY; y++) {
						for(int z = bound.minZ; z <= bound.maxZ; z++) {
							if(x == startX && y == startY && z == startZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, startindx, 0, startindz, startoutdx, 0, startoutdz, startRotation, 1, startShape));
							} else if(x == endX && y == endY && z == endZ) {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, endindx, 0, endindz, endoutdx, 0, endoutdz, endRotation, 1, endShape));
							} else {
								pieces.add(new PathfinderLinkMeta.Piece(x, y, z, dx, 0, dz, dx, 0, dz, corridorRotation, 1, PathfinderLinkMeta.Shape.STRAIGHT));
							}
						}
					}
				}
			}
		} else {
			//Stairs with potential corridors at the end

			if(indx != outdx || indz != outdz) {
				return false;
			}

			int rotation = getRotation(indx, indz);

			int currentX = startX;
			int currentY = startY;
			int currentZ = startZ;

			int xzPerY = link.getXZPerY();

			if(indx != 0) {
				int len = Math.abs(endX + outdx - startX);
				int numPieces = Math.min(len / xzPerY, ascent);

				for(int i = 0; i < numPieces; i++) {
					pieces.add(new PathfinderLinkMeta.Piece(currentX, currentY, currentZ, indx, 0, indz, outdx, 0, outdz, rotation, xzPerY, PathfinderLinkMeta.Shape.STAIRS));
					currentY++;
					currentX += indx * xzPerY;
				}

				int remainingCorridors = Math.abs(endX + outdx - currentX);
				for(int i = 0; i < remainingCorridors; i++) {
					pieces.add(new PathfinderLinkMeta.Piece(currentX, currentY, currentZ, indx, 0, indz, outdx, 0, outdz, rotation, 1, PathfinderLinkMeta.Shape.STRAIGHT));
					currentX += indx;
				}
			} else {
				int len = Math.abs(endZ + outdz - startZ);
				int numPieces = Math.min(len / xzPerY, ascent);

				for(int i = 0; i < numPieces; i++) {
					pieces.add(new PathfinderLinkMeta.Piece(currentX, currentY, currentZ, indx, 0, indz, outdx, 0, outdz, rotation, xzPerY, PathfinderLinkMeta.Shape.STAIRS));
					currentY++;
					currentZ += indz * xzPerY;
				}

				int remainingCorridors = Math.abs(endZ + outdz - currentZ);
				for(int i = 0; i < remainingCorridors; i++) {
					pieces.add(new PathfinderLinkMeta.Piece(currentX, currentY, currentZ, indx, 0, indz, outdx, 0, outdz, rotation, 1, PathfinderLinkMeta.Shape.STRAIGHT));
					currentZ += indz;
				}
			}
		}

		return true;
	}

	private static int getRotation(int dx, int dz) {
		if(dx == 1) {
			return 0;
		} else if(dz == -1) {
			return 1;
		} else if(dx == -1) {
			return 2;
		} else if(dz == 1) {
			return 3;
		}
		return 0;
	}

	private static int getRotation(int dx, int dz, int dx2, int dz2) {
		if(dx == 1) {
			if(dz2 == 1) {
				return 3;
			} else {
				return 2;
			}
		} else if(dz == -1) {
			if(dx2 == 1) {
				return 0;
			} else {
				return 3;
			}
		} else if(dx == -1) {
			if(dz2 == 1) {
				return 0;
			} else {
				return 1;
			}
		} else if(dz == 1) {
			if(dx2 == 1) {
				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}
}
