package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.util.math.MathHelper;
import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.IExtent;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.Region;

public class Link extends GridObject {
	public static class Constraint {
		public int expandX, expandY, expandZ;
		public int pushX, pushY, pushZ;
		public int disperse;
	}

	public static class Bound {
		public final int minX, minY, minZ, maxX, maxY, maxZ;

		public Bound(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
	}

	public static interface IBoundConsumer {
		public boolean consume(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
	}

	private final Connector start, end;
	private final Accelerator accelerator;
	private final BitSet tags = new BitSet();
	private final int xzPerY; //Slope of stairs
	private final int stairsRotationDir;

	private Collection<Region> regions = Collections.emptyList();
	private BitSet regionTags = new BitSet();
	private Object[] meta = null;

	Link(Connector start, Connector end, Accelerator accelerator, int xzPerY, int stairsRotationDir) {
		this.start = start;
		this.end = end;
		this.accelerator = accelerator;
		this.xzPerY = xzPerY;
		this.stairsRotationDir = stairsRotationDir == 0 ? 1 : (int)Math.signum(stairsRotationDir);
	}

	public Connector getStart() {
		return this.start;
	}

	public Connector getEnd() {
		return this.end;
	}

	public int getApproximateLength() {
		Connector start = this.getStart();
		Connector end = this.getEnd();

		int sx = start.getTileX();
		int sy = start.getTileY();
		int sz = start.getTileZ();

		int ex = end.getTileX();
		int ey = end.getTileY();
		int ez = end.getTileZ();

		return Math.abs(sx - ex) + Math.abs(sy - ey) + Math.abs(sz - ez);
	}

	public int getXZPerY() {
		return this.xzPerY;
	}

	public boolean iterateBounds(IBoundConsumer consumer) {
		Connector start = this.getStart();
		Connector end = this.getEnd();

		Direction startDir = start.getDir();
		Direction endDir = end.getDir();

		int sx = start.getTileX();
		int sy = start.getTileY();
		int sz = start.getTileZ();

		int ex = end.getTileX();
		int ey = end.getTileY();
		int ez = end.getTileZ();

		if(sx + startDir.x == ex && sy + startDir.y == ey && sz + startDir.z == ez) {
			return false;
		}

		int dx = ex - sx;
		int dy = ey - sy;
		int dz = ez - sz;

		boolean hasStairs = sy != ey;

		if(!hasStairs) {
			if(Math.abs((int)Math.signum(dx)) == Math.abs(startDir.x) && Math.abs((int)Math.signum(dy)) == Math.abs(startDir.y) && Math.abs((int)Math.signum(dz)) == Math.abs(startDir.z)) {
				//Straight line

				int b1x = Math.min(sx + startDir.x, ex + endDir.x);
				int b1y = Math.min(sy + startDir.y, ey + endDir.y);
				int b1z = Math.min(sz + startDir.z, ez + endDir.z);
				int b2x = Math.max(sx + startDir.x, ex + endDir.x);
				int b2y = Math.max(sy + startDir.y, ey + endDir.y);
				int b2z = Math.max(sz + startDir.z, ez + endDir.z);

				if(consumer.consume(b1x, b1y, b1z, b2x, b2y, b2z)) return true;
			} else {
				//Z shape

				int midx = (sx + ex) / 2;
				int midy = (sy + ey) / 2;
				int midz = (sz + ez) / 2;

				int corner1x = sx + (midx - sx) * Math.abs(startDir.x);
				int corner1y = sy + (midy - sy) * Math.abs(startDir.y);
				int corner1z = sz + (midz - sz) * Math.abs(startDir.z);

				int corner2x = ex + (midx - ex) * Math.abs(startDir.x);
				int corner2y = ey + (midy - ey) * Math.abs(startDir.y);
				int corner2z = ez + (midz - ez) * Math.abs(startDir.z);

				//Corridor from start connector to middle corridor
				int b11x = Math.min(sx + startDir.x, corner1x - (corner1x == sx + startDir.x ? 0 : startDir.x));
				int b11y = Math.min(sy + startDir.y, corner1y - (corner1y == sy + startDir.y ? 0 : startDir.y));
				int b11z = Math.min(sz + startDir.z, corner1z - (corner1z == sz + startDir.z ? 0 : startDir.z));
				int b12x = Math.max(sx + startDir.x, corner1x - (corner1x == sx + startDir.x ? 0 : startDir.x));
				int b12y = Math.max(sy + startDir.y, corner1y - (corner1y == sy + startDir.y ? 0 : startDir.y));
				int b12z = Math.max(sz + startDir.z, corner1z - (corner1z == sz + startDir.z ? 0 : startDir.z));

				//Middle corridor
				int b21x = Math.min(corner1x, corner2x);
				int b21y = Math.min(corner1y, corner2y);
				int b21z = Math.min(corner1z, corner2z);
				int b22x = Math.max(corner1x, corner2x);
				int b22y = Math.max(corner1y, corner2y);
				int b22z = Math.max(corner1z, corner2z);

				//Corridor from end connector to middle corridor
				int b31x = Math.min(corner2x + (corner2x == ex + endDir.x ? 0 : startDir.x), ex + endDir.x);
				int b31y = Math.min(corner2y + (corner2y == ey + endDir.y ? 0 : startDir.y), ey + endDir.y);
				int b31z = Math.min(corner2z + (corner2z == ez + endDir.z ? 0 : startDir.z), ez + endDir.z);
				int b32x = Math.max(corner2x + (corner2x == ex + endDir.x ? 0 : startDir.x), ex + endDir.x);
				int b32y = Math.max(corner2y + (corner2y == ey + endDir.y ? 0 : startDir.y), ey + endDir.y);
				int b32z = Math.max(corner2z + (corner2z == ez + endDir.z ? 0 : startDir.z), ez + endDir.z);

				if(!((b11x == b21x || b11x == b22x) && (b11z == b21z || b11z == b22z))) {
					if(consumer.consume(b11x, b11y, b11z, b12x, b12y, b12z)) return true;
				}
				if(consumer.consume(b21x, b21y, b21z, b22x, b22y, b22z)) return true;
				if(!((b31x == b21x || b31x == b22x) && (b31z == b21z || b31z == b22z))) {
					if(consumer.consume(b31x, b31y, b31z, b32x, b32y, b32z)) return true;
				}
			}
		} else {
			if(startDir.y == 0 && Math.abs((int)Math.signum(dx)) == Math.abs(startDir.x) && Math.abs((int)Math.signum(dz)) == Math.abs(startDir.z)) {
				//Straight line

				int b1x = Math.min(sx + startDir.x, ex + endDir.x);
				int b1y = Math.min(sy + startDir.y, ey + endDir.y);
				int b1z = Math.min(sz + startDir.z, ez + endDir.z);
				int b2x = Math.max(sx + startDir.x, ex + endDir.x);
				int b2y = Math.max(sy + startDir.y, ey + endDir.y);
				int b2z = Math.max(sz + startDir.z, ez + endDir.z);

				if(consumer.consume(b1x, b1y, b1z, b2x, b2y, b2z)) return true;
			} else {
				if(startDir.y == 0 || start.getCell().getY() == end.getCell().getY()) {
					int midx = (sx + ex) / 2;
					int midz = (sz + ez) / 2;

					int corner1x = sx + (midx - sx) * Math.abs(startDir.x);
					int corner1y = sy;
					int corner1z = sz + (midz - sz) * Math.abs(startDir.z);

					int corner2x = ex + (midx - ex) * Math.abs(startDir.x);
					int corner2y = ey;
					int corner2z = ez + (midz - ez) * Math.abs(startDir.z);

					int cdx = corner2x - corner1x;
					int cdz = corner2z - corner1z;

					int cdxs = (int)Math.signum(cdx);
					int cdys = 0;
					int cdzs = (int)Math.signum(cdz);

					int height = Math.abs(ey - sy);

					int startHeight = Math.max(height / 2, height - height / 2);
					int endHeight = height - startHeight;

					int sdist = Math.abs(corner1x - sx) + Math.abs(corner1z - sz);
					int edist = Math.abs(corner2x - ex) + Math.abs(corner2z - ez);

					//If distance to mid segment is 1 then start segment must not ascend
					//because it needs to be a corner tile
					if(sdist > 1) {
						//Ascent/descend on start segment as much as possible
						int ascent = Math.min(startHeight, Math.max(0, (sdist - 1) / this.xzPerY));
						endHeight += (startHeight - ascent); //Give remainder to end segment
						startHeight -= ascent;
						corner1y = sy + (int)Math.signum(ey - sy) * ascent;
					}

					//If distance to mid segment is 1 then start segment must not ascend
					//because it needs to be a corner tile
					if(edist > 1) {
						//Ascent/descend on end segment as much as possible
						int ascent = Math.min(endHeight, Math.max(0, (edist - 1) / this.xzPerY));
						endHeight -= ascent;
						corner2y = ey + (int)Math.signum(sy - ey) * ascent;
					}

					int middst = Math.abs(corner1x - corner2x) + Math.abs(corner1z - corner2z);
					int midHeight = (middst - 1) / this.xzPerY - Math.abs(corner2y - corner1y);

					//Make mid segment cover remaining height or at least half of the
					//total height
					if(midHeight > 0) {
						int remainingStartHeight = height / 2 - startHeight;
						if(remainingStartHeight > 0 && midHeight > 1) {
							int ascent = Math.min(remainingStartHeight / 2, midHeight);
							midHeight -= ascent;
							corner1y -= (int)Math.signum(ey - sy) * ascent;
						}

						int remainingEndHeight = height - height / 2 - endHeight;
						if(remainingEndHeight > 0 && midHeight > 1) {
							int ascent = Math.min(remainingEndHeight / 2, midHeight);
							midHeight -= ascent;
							corner2y -= (int)Math.signum(sy - ey) * ascent;
						}
					}

					int minX = Math.min(sx + startDir.x, ex + endDir.x);
					int maxX = Math.max(sx + startDir.x, ex + endDir.x);
					int minY = Math.min(sy + startDir.y, ey + endDir.y);
					int maxY = Math.max(sy + startDir.y, ey + endDir.y);
					int minZ = Math.min(sz + startDir.z, ez + endDir.z);
					int maxZ = Math.max(sz + startDir.z, ez + endDir.z);

					//Corridor from start connector to middle corridor
					int b11x = Math.min(sx + startDir.x, MathHelper.clamp(corner1x - startDir.x, minX, maxX));
					int b11y = Math.min(sy + startDir.y, MathHelper.clamp(corner1y - startDir.y, minY, maxY));
					int b11z = Math.min(sz + startDir.z, MathHelper.clamp(corner1z - startDir.z, minZ, maxZ));
					int b12x = Math.max(sx + startDir.x, MathHelper.clamp(corner1x - startDir.x, minX, maxX));
					int b12y = Math.max(sy + startDir.y, MathHelper.clamp(corner1y - startDir.y, minY, maxY));
					int b12z = Math.max(sz + startDir.z, MathHelper.clamp(corner1z - startDir.z, minZ, maxZ));

					//Middle corridor
					int b21x = Math.min(corner1x + cdxs, corner2x - cdxs);
					int b21y = Math.min(corner1y + cdys, corner2y - cdys);
					int b21z = Math.min(corner1z + cdzs, corner2z - cdzs);
					int b22x = Math.max(corner1x + cdxs, corner2x - cdxs);
					int b22y = Math.max(corner1y + cdys, corner2y - cdys);
					int b22z = Math.max(corner1z + cdzs, corner2z - cdzs);

					//Corridor from end connector to middle corridor
					int b31x = Math.min(MathHelper.clamp(corner2x - endDir.x, minX, maxX), ex + endDir.x);
					int b31y = Math.min(MathHelper.clamp(corner2y - endDir.y, minY, maxY), ey + endDir.y);
					int b31z = Math.min(MathHelper.clamp(corner2z - endDir.z, minZ, maxZ), ez + endDir.z);
					int b32x = Math.max(MathHelper.clamp(corner2x - endDir.x, minX, maxX), ex + endDir.x);
					int b32y = Math.max(MathHelper.clamp(corner2y - endDir.y, minY, maxY), ey + endDir.y);
					int b32z = Math.max(MathHelper.clamp(corner2z - endDir.z, minZ, maxZ), ez + endDir.z);

					if(b11x != b12x + 1 && b11y != b12y + 1 && b11z != b12z + 1) {
						if(consumer.consume(b11x, b11y, b11z, b12x, b12y, b12z)) return true;
					}
					if((Math.abs(cdx) > 1 || Math.abs(cdz) > 1) && !((corner1x == b11x || corner1x == b12x) && (corner1z == b11z || corner1z == b12z))) {
						if(consumer.consume(corner1x, corner1y, corner1z, corner1x, corner1y, corner1z)) return true;
					}
					if(b21x != b22x + 1 && b21y != b22y + 1 && b21z != b22z + 1) {
						if(consumer.consume(b21x, b21y, b21z, b22x, b22y, b22z)) return true;
					}
					if((Math.abs(cdx) > 1 || Math.abs(cdz) > 1) && !((corner2x == b31x || corner2x == b32x) && (corner2z == b31z || corner2z == b32z))) {
						if(consumer.consume(corner2x, corner2y, corner2z, corner2x, corner2y, corner2z)) return true;
					}
					if(b31x != b32x + 1 && b31y != b32y + 1 && b31z != b32z + 1) {
						if(consumer.consume(b31x, b31y, b31z, b32x, b32y, b32z)) return true;
					}
				} else {
					int osx = sx + startDir.x;
					int osy = sy + startDir.y;
					int osz = sz + startDir.z;

					int oex = ex + endDir.x;
					int oey = ey + endDir.y;
					int oez = ez + endDir.z;

					int odx = oex - osx;
					int odz = oez - osz;

					int maindirx = 0;
					int maindirz = 0;

					if(Math.abs(odx) >= Math.abs(odz)) {
						maindirx = (int)Math.signum(odx);
						odx = 0;
					} else {
						maindirz = (int)Math.signum(odz);
						odz = 0;
					}

					int corner1x = osx + odx / 2;
					int corner1y = osy;
					int corner1z = osz + odz / 2;

					int corner4x = maindirx == 0 ? corner1x : oex - odx / 2;
					int corner4y = oey;
					int corner4z = maindirz == 0 ? corner1z : oez - odz / 2;

					int corner2x = corner1x + maindirx;
					int corner2y = corner1y;
					int corner2z = corner1z + maindirz;

					int corner3x = corner4x - maindirx;
					int corner3y = corner4y;
					int corner3z = corner4z - maindirz;

					//Corridor from start connector to stairs
					int b11x = Math.min(osx, corner1x);
					int b11y = Math.min(osy, corner1y);
					int b11z = Math.min(osz, corner1z);
					int b12x = Math.max(osx, corner1x);
					int b12y = Math.max(osy, corner1y);
					int b12z = Math.max(osz, corner1z);

					//Stairs
					int b21x = Math.min(corner2x, corner3x);
					int b21y = Math.min(corner2y, corner3y);
					int b21z = Math.min(corner2z, corner3z);
					int b22x = Math.max(corner2x, corner3x);
					int b22y = Math.max(corner2y, corner3y);
					int b22z = Math.max(corner2z, corner3z);

					//Corridor from end connector to stairs
					int b31x = Math.min(corner4x, oex);
					int b31y = Math.min(corner4y, oey);
					int b31z = Math.min(corner4z, oez);
					int b32x = Math.max(corner4x, oex);
					int b32y = Math.max(corner4y, oey);
					int b32z = Math.max(corner4z, oez);

					if(!((b11x == b21x || b11x == b22x) && (b11z == b21z || b11z == b22z))) {
						if(consumer.consume(b11x, b11y, b11z, b12x, b12y, b12z)) return true;
					}
					if(consumer.consume(b21x, b21y, b21z, b22x, b22y, b22z)) return true;
					if(!((b31x == b21x || b31x == b22x) && (b31z == b21z || b31z == b22z))) {
						if(consumer.consume(b31x, b31y, b31z, b32x, b32y, b32z)) return true;
					}
				}
			}
		}

		return false;
	}

	public List<Bound> getBounds() {
		List<Bound> bounds = new ArrayList<>();
		this.iterateBounds((minX, minY, minZ, maxX, maxY, maxZ) -> {
			bounds.add(new Bound(minX, minY, minZ, maxX, maxY, maxZ));
			return false;
		});
		return bounds;
	}

	public boolean getConstraint(@Nullable Constraint constraint) {
		Connector start = this.getStart();
		Connector end = this.getEnd();

		Direction startDir = start.getDir();
		Direction endDir = end.getDir();

		Cell startCell = start.getCell();
		Cell endCell = end.getCell();

		if(startDir != endDir.opposite()) {
			throw new RuntimeException();
		}

		int sx = start.getTileX();
		int sy = start.getTileY();
		int sz = start.getTileZ();

		int ex = end.getTileX();
		int ey = end.getTileY();
		int ez = end.getTileZ();

		if(sx + startDir.x == ex && sy + startDir.y == ey && sz + startDir.z == ez) {
			return false;
		}

		int spacingX = 0;
		int spacingY = 0;
		int spacingZ = 0;

		if(startDir == Direction.POS_X) {
			if(ez != sz) {
				if(ex - sx < 2) {
					//Need at least +1 X space
					spacingX = 1;
				}
			}
		} else if(startDir == Direction.NEG_X) {
			if(ez != sz) {
				if(sx - ex < 2) {
					//Need at least -1 X space
					spacingX = -1;
				}
			}
		} else if(startDir == Direction.POS_Z) {
			if(ex != sx) {
				if(ez - sz < 2) {
					//Need at least +1 Z space
					spacingZ = 1;
				}
			}
		} else if(startDir == Direction.NEG_Z) {
			if(ex != sx) {
				if(sz - ez < 2) {
					//Need at least -1 Z space
					spacingZ = -1;
				}
			}
		} else if(startDir == Direction.POS_Y && ey - sy < this.xzPerY + 1) {
			//Need at least +1 Y space
			spacingY = 1;
		} else if(startDir == Direction.NEG_Y && sy - ey < this.xzPerY + 1) {
			//Need at least -1 Y space
			spacingY = -1;
		}

		int expandX = spacingX;
		int expandZ = spacingZ;

		int pushX = 0;
		int pushZ = 0;

		int disperse = 0;

		if(sy != ey) {
			boolean isVerticalConnection = startDir.y != 0 && startCell.getY() != endCell.getY();

			//Need enough straight space for stairs
			int minHorizontalDst = (Math.abs(ey - sy) - (isVerticalConnection ? 2 : 0)) * this.xzPerY;

			if(sx == ex && startDir.y == 0) {
				//Stairs need to fit into line path
				int expand = Math.max(0, minHorizontalDst - (Math.abs(ez - sz) - 1));
				expandZ += expand * (int)Math.signum(ez - sz);
			} else if(sz == ez && startDir.y == 0) {
				//Stairs need to fit into line path
				int expand = Math.max(0, minHorizontalDst - (Math.abs(ex - sx) - 1));
				expandX += expand * (int)Math.signum(ex - sx);
			} else {
				//Stairs need to fit into Z path
				int dstX = Math.abs(ex - sx);
				int dstZ = Math.abs(ez - sz);
				int stairTiles = 0;

				if(startDir.y == 0 || startCell.getY() == endCell.getY()) {
					//Number of available straight tiles. Minus 1 because of the
					//perpendicular/mid segment
					int straightTiles = Math.max(0, dstX * Math.abs(startDir.x) + dstZ * Math.abs(startDir.z) - 1 - 1);

					//When the perpendicular distance between the two connectors is
					//1 then both straight segments must be a corner
					if(!isVerticalConnection && dstX * (1 - Math.abs(startDir.x)) + dstZ * (1 - Math.abs(startDir.z)) == 1) {
						straightTiles = Math.max(straightTiles - 1, 0);
					}

					//Parallel start and end segments
					int straightTilesStart = Math.max(0, straightTiles / 2);
					int straightTilesEnd = Math.max(0, straightTiles - straightTilesStart);

					//Perpendicular/mid segment
					int perpendicularTiles = dstX * (1 - Math.abs(startDir.x)) + dstZ * (1 - Math.abs(startDir.z)) - 1;

					stairTiles += straightTilesStart / this.xzPerY + straightTilesEnd / this.xzPerY;
					stairTiles += perpendicularTiles / this.xzPerY;
				} else {
					stairTiles += Math.max(Math.max(dstX - 1, dstZ - 1), 0) / this.xzPerY;
				}

				int expand = Math.max(0, minHorizontalDst - stairTiles * this.xzPerY);

				if(startCell.getY() != endCell.getY() && startDir.y == 0) {
					//Globally move side stairs counter-/clockwise to
					//avoid unresolvable conflicts with two side stairs
					expandX += this.stairsRotationDir * expand * startDir.z;
					expandZ += this.stairsRotationDir * expand * startDir.x;
				} else {
					//For non side stairs any direction can be picked, but
					//axis with maximum distance is preferred
					if(dstX >= dstZ) {
						int dirX = (int)Math.signum(ex - sx);
						if(dirX == 0) {
							expandX++;
						} else {
							expandX += expand * dirX;
						}
					} else {
						int dirZ = (int)Math.signum(ez - sz);
						if(dirZ == 0) {
							expandZ++;
						} else {
							expandZ += expand * dirZ;
						}
					}
				}
			}

			//Top stairs may collide with corridors that are straight.
			//Push room to side to bend top stairs.
			//TODO Examples: https://i.imgur.com/Q81CSts.png, https://i.imgur.com/gByIGjx.png
			/*if(startDir.y != 0) {
				for(Connector c : startCell.getTile().getConnectors()) {
					if(c == start || c == end) {
						continue;
					}

					Direction cDir = c.getDir();

					if(Math.abs(cDir.x) != 0 || Math.abs(cDir.z) != 0) {
						disperse++;
					}
				}

				for(Connector c : endCell.getTile().getConnectors()) {
					if(c == start || c == end) {
						continue;
					}

					Direction cDir = c.getDir();

					if(Math.abs(cDir.x) != 0 || Math.abs(cDir.z) != 0) {
						disperse++;
					}
				}
			}*/

			//Side stairs may collide with corridors. Push away in direction of connector
			//and spread +/- Y connected rooms apart to resolve.
			if(startDir.x != 0 || startDir.z != 0) {
				for(Link link : startCell.getLinks()) {
					Connector c;
					Connector otherc;
					if(link.getStart().getCell() == startCell) {
						c = link.getStart();
						otherc = link.getEnd();
					} else {
						c = link.getEnd();
						otherc = link.getStart();
					}

					if(c == start || c == end) {
						continue;
					}

					Direction cDir = c.getDir();

					//Connectors that point in same or opposite direction can be ignored
					if(cDir.y == 0 && Math.abs(cDir.x) != Math.abs(startDir.x) && Math.abs(cDir.z) != Math.abs(startDir.z)) {
						if(otherc == start || otherc == end) {
							continue;
						}

						if(startDir.x != 0 && ((startDir.x < 0 && otherc.getTileX() < Math.max(sx, ex)) || (startDir.x > 0 && otherc.getTileX() > Math.min(sx, ex)))) {
							//Check for space along Z
							int dz = otherc.getTileZ() - c.getTileZ();
							if(dz > 0 && (otherc.getTileZ() + c.getTileZ()) / 2 - 1 < Math.max(sz, ez) && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ())) {
								if(startDir.x > 0 && (sx + ex) / 2 - 1 < Math.max(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = -1;
								} else if(startDir.x < 0 && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = -1;
								}
							} else if(dz < 0 && (otherc.getTileZ() + c.getTileZ()) / 2 + 1 > Math.min(sz, ez) && (sz + ez) / 2 - 1 > Math.min(otherc.getTileZ(), c.getTileZ())) {
								if(startDir.x > 0 && (sx + ex) / 2 - 1 < Math.max(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = 1;
								} else if(startDir.x < 0 && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = 1;
								}
							}
						} else if(startDir.z != 0 && ((startDir.z < 0 && otherc.getTileZ() < Math.max(sz, ez)) || (startDir.z > 0 && otherc.getTileZ() > Math.min(sz, ez)))) {
							//Check for space along X
							int dx = otherc.getTileX() - c.getTileX();
							if(dx > 0 && (otherc.getTileX() + c.getTileX()) / 2 - 1 < Math.max(sx, ex)) {
								if(startDir.z > 0 && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = -1;
								} else if(startDir.z < 0 && (sz + ez) / 2 + 1 > Math.min(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = -1;
								}
							} else if(dx < 0 && (otherc.getTileX() + c.getTileX()) / 2 + 1 > Math.min(sx, ex) && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX())) {
								if(startDir.z > 0 && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = 1;
								} else if(startDir.z < 0 && (sz + ez) / 2 + 1 > Math.min(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = 1;
								}
							}
						}
					}
				}

				for(Link link : endCell.getLinks()) {
					Connector c;
					Connector otherc;
					if(link.getStart().getCell() == endCell) {
						c = link.getStart();
						otherc = link.getEnd();
					} else {
						c = link.getEnd();
						otherc = link.getStart();
					}

					if(c == start || c == end) {
						continue;
					}

					Direction cDir = c.getDir();

					//Connectors that point in same or opposite direction can be ignored
					if(cDir.y == 0 && Math.abs(cDir.x) != Math.abs(endDir.x) && Math.abs(cDir.z) != Math.abs(endDir.z)) {
						if(otherc == start || otherc == end) {
							continue;
						}

						if(endDir.x != 0 && ((endDir.x < 0 && otherc.getTileX() < Math.max(sx, ex)) || (endDir.x > 0 && otherc.getTileX() > Math.min(sx, ex)))) {
							//Check for space along Z
							int dz = otherc.getTileZ() - c.getTileZ();
							if(dz > 0 && (otherc.getTileZ() + c.getTileZ()) / 2 - 1 < Math.max(sz, ez) && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ())) {
								if(endDir.x > 0 && (sx + ex) / 2 - 1 < Math.max(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = 1;
								} else if(endDir.x < 0 && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = 1;
								}
							} else if(dz < 0 && (otherc.getTileZ() + c.getTileZ()) / 2 + 1 > Math.min(sz, ez) && (sz + ez) / 2 - 1 > Math.min(otherc.getTileZ(), c.getTileZ())) {
								if(endDir.x > 0 && (sx + ex) / 2 - 1 < Math.max(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = -1;
								} else if(endDir.x < 0 && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX()) && this.isColliding(link)) {
									disperse++;
									pushZ = -1;
								}
							}
						} else if(endDir.z != 0 && ((endDir.z < 0 && otherc.getTileZ() < Math.max(sz, ez)) || (endDir.z > 0 && otherc.getTileZ() > Math.min(sz, ez)))) {
							//Check for space along X
							int dx = otherc.getTileX() - c.getTileX();
							if(dx > 0 && (otherc.getTileX() + c.getTileX()) / 2 - 1 < Math.max(sx, ex)) {
								if(endDir.z > 0 && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = 1;
								} else if(endDir.z < 0 && (sz + ez) / 2 + 1 > Math.min(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = 1;
								}
							} else if(dx < 0 && (otherc.getTileX() + c.getTileX()) / 2 + 1 > Math.min(sx, ex) && (sx + ex) / 2 + 1 > Math.min(otherc.getTileX(), c.getTileX())) {
								if(endDir.z > 0 && (sz + ez) / 2 - 1 < Math.max(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = -1;
								} else if(endDir.z < 0 && (sz + ez) / 2 + 1 > Math.min(otherc.getTileZ(), c.getTileZ()) && this.isColliding(link)) {
									disperse++;
									pushX = -1;
								}
							}
						}
					}
				}
			}

			//Make sure rooms are on opposite sides
			if(startDir.x != 0 && (int)Math.signum(ex - sx) != startDir.x) {
				if(startDir.x > 0) {
					expandX = Math.max(expandX, Math.abs(ex - sx) + 1);
				} else {
					expandX = Math.min(expandX, -(Math.abs(ex - sx) + 1));
				}
			}
			if(startDir.z != 0 && (int)Math.signum(ez - sz) != startDir.z) {
				if(startDir.z > 0) {
					expandZ = Math.max(expandZ, Math.abs(ez - sz) + 1);
				} else {
					expandZ = Math.min(expandZ, -(Math.abs(ez - sz) + 1));
				}
			}
		}

		if(expandX != 0 || spacingY != 0 || expandZ != 0 || pushX != 0 || pushZ != 0 || disperse != 0) {
			if(constraint != null) {
				constraint.expandX = expandX;
				constraint.expandY = spacingY;
				constraint.expandZ = expandZ;
				constraint.pushX = pushX;
				constraint.pushY = 0;
				constraint.pushZ = pushZ;
				constraint.disperse = disperse;
			}
			return true;
		}

		return false;
	}

	public void updateAccelerator(int dx, int dy, int dz, Set<Cell> tree, int tag, boolean force) {
		//if(force) {
		this.accelerator.update(this);

		//TODO Quick check not working yet
		/*} else {
			Connector start = this.getStart();
			Connector end = this.getEnd();

			Direction startDir = start.getDir();
			Direction endDir = end.getDir();

			int regionSize = this.accelerator.getRegionSize();

			int startdx = dx;
			int startdy = dy;
			int startdz = dz;
			if(!moved.contains(start.getCell())) {
				startdx = 0;
				startdy = 0;
				startdz = 0;
			}

			int enddx = dx;
			int enddy = dy;
			int enddz = dz;
			if(!moved.contains(end.getCell())) {
				enddx = 0;
				enddy = 0;
				enddz = 0;
			}

			int currMinX = Math.min(start.getTileX() + startDir.x, end.getTileX() + endDir.x);
			int currMinY = Math.min(start.getTileY() + startDir.y, end.getTileY() + endDir.y);
			int currMinZ = Math.min(start.getTileZ() + startDir.z, end.getTileZ() + endDir.z);
			int currMaxX = Math.max(start.getTileX() + startDir.x, end.getTileX() + endDir.x) + 1;
			int currMaxY = Math.max(start.getTileY() + startDir.y, end.getTileY() + endDir.y) + 1;
			int currMaxZ = Math.max(start.getTileZ() + startDir.z, end.getTileZ() + endDir.z) + 1;

			int prevMinX = Math.min(start.getTileX() + startDir.x - startdx, end.getTileX() + endDir.x - enddx);
			int prevMinY = Math.min(start.getTileY() + startDir.y - startdy, end.getTileY() + endDir.y - enddy);
			int prevMinZ = Math.min(start.getTileZ() + startDir.z - startdz, end.getTileZ() + endDir.z - enddz);
			int prevMaxX = Math.max(start.getTileX() + startDir.x - startdx, end.getTileX() + endDir.x - enddx) + 1;
			int prevMaxY = Math.max(start.getTileY() + startDir.y - startdy, end.getTileY() + endDir.y - enddy) + 1;
			int prevMaxZ = Math.max(start.getTileZ() + startDir.z - startdz, end.getTileZ() + endDir.z - enddz) + 1;

			int minX = Math.min(currMinX, prevMinX);
			int minY = Math.min(currMinY, prevMinY);
			int minZ = Math.min(currMinZ, prevMinZ);
			int maxX = Math.max(currMaxX, prevMaxX);
			int maxY = Math.max(currMaxY, prevMaxY);
			int maxZ = Math.max(currMaxZ, prevMaxZ);

			AtomicInteger realMinX = new AtomicInteger(1000000);
			AtomicInteger realMinY = new AtomicInteger(1000000);
			AtomicInteger realMinZ = new AtomicInteger(1000000);
			AtomicInteger realMaxX = new AtomicInteger(0);
			AtomicInteger realMaxY = new AtomicInteger(0);
			AtomicInteger realMaxZ = new AtomicInteger(0);

			this.iterateBounds((rminx, rminy, rminz, rmaxx, rmaxy, rmaxz) -> {
				realMinX.set(Math.min(realMinX.get(), rminx));
				realMinY.set(Math.min(realMinY.get(), rminy));
				realMinZ.set(Math.min(realMinZ.get(), rminz));
				realMaxX.set(Math.max(realMaxX.get(), rmaxx));
				realMaxY.set(Math.max(realMaxY.get(), rmaxy));
				realMaxZ.set(Math.max(realMaxZ.get(), rmaxz));
				return false;
			});

			if(
					maxX - minX >= regionSize ||
					maxY - minY >= regionSize ||
					maxZ - minZ >= regionSize ||
					currMinX / regionSize != prevMinX / regionSize ||
					currMinY / regionSize != prevMinY / regionSize ||
					currMinZ / regionSize != prevMinZ / regionSize ||
					currMaxX / regionSize != prevMaxX / regionSize ||
					currMaxY / regionSize != prevMaxY / regionSize ||
					currMaxZ / regionSize != prevMaxZ / regionSize) {
				this.accelerator.update(this);
			} else {
				if(this.accelerator.update(this)) {
					//TODO This shouldn't happen!!
					System.out.println("Missed accelerator update: " + this);
					System.out.println((maxX - minX) + " " + (maxY - minY) + " " + (maxZ - minZ));
					System.out.println(realMinX.get() + " " + realMinY.get() + " " + realMinZ.get() + " " + realMaxX.get() + " " + realMaxY.get() + " " + realMaxZ.get());
					System.out.println(currMinX + " " + currMinY + " " + currMinZ + " " + currMaxX + " " + currMaxY + " " + currMaxZ);
					System.out.println(startdx + " " + startdy + " " + startdz + " " + enddx + " " + enddy + " " + enddz);
					Test.invalidLink = this;
					throw new RuntimeException("test");
				}
			}
		}*/
	}

	public boolean isColliding(int tag) {
		Connector start = this.getStart();
		Connector end = this.getEnd();

		Tile startTile = start.getCell().getTile();
		Tile endTile = end.getCell().getTile();

		final int regionSize = this.accelerator.getRegionSize();

		//TODO All the Connector.checkCollision(Connector) checks must be removed because they can
		//cause collisions to occur because link collisions are skipped

		return this.iterateBounds((minX, minY, minZ, maxX, maxY, maxZ) -> {
			//Need to check at least the connected tiles because those could always potentially
			//collide and may not yet be in the correct accelerator regions because the
			//accelerator update is done after all the checks have passed
			if(minX < startTile.getX() + startTile.getSizeX() && maxX >= startTile.getX() &&
					minY < startTile.getY() + startTile.getSizeY() && maxY >= startTile.getY() &&
					minZ < startTile.getZ() + startTile.getSizeZ() && maxZ >= startTile.getZ()) {
				return true;
			}
			if(minX < endTile.getX() + endTile.getSizeX() && maxX >= endTile.getX() &&
					minY < endTile.getY() + endTile.getSizeY() && maxY >= endTile.getY() &&
					minZ < endTile.getZ() + endTile.getSizeZ() && maxZ >= endTile.getZ()) {
				return true;
			}
			//Same for the links connected to the start and/or end tile, if they have moved
			if(tag < 0 || !startTile.getTag(tag)) {
				for(Link link : startTile.getCell().getLinks()) {
					Connector start2 = link.getStart();
					Connector end2 = link.getEnd();
					if(link != this && this.isColliding(link) && (start.getDir() != start2.getDir() || !start.checkCollision(start2)) && (start.getDir() != end2.getDir() || !start.checkCollision(end2)) && (end.getDir() != start2.getDir() || !end.checkCollision(start2)) && (end.getDir() != end2.getDir() || !end.checkCollision(end2))) {
						return true;
					}
				}
			}
			if(tag < 0 || !endTile.getTag(tag)) {
				for(Link link : endTile.getCell().getLinks()) {
					Connector start2 = link.getStart();
					Connector end2 = link.getEnd();
					if(link != this && this.isColliding(link) && (start.getDir() != start2.getDir() || !start.checkCollision(start2)) && (start.getDir() != end2.getDir() || !start.checkCollision(end2)) && (end.getDir() != start2.getDir() || !end.checkCollision(start2)) && (end.getDir() != end2.getDir() || !end.checkCollision(end2))) {
						return true;
					}
				}
			}

			int sx = Math.floorDiv(minX,  regionSize);
			int sy = Math.floorDiv(minY,  regionSize);
			int sz = Math.floorDiv(minZ,  regionSize);
			int ex = Math.floorDiv(maxX,  regionSize);
			int ey = Math.floorDiv(maxY,  regionSize);
			int ez = Math.floorDiv(maxZ,  regionSize);

			if(tag < 0) {
				for(int x = sx; x <= ex; x++) {
					for(int y = sy; y <= ey; y++) {
						for(int z = sz; z <= ez; z++) {
							Region region = this.accelerator.get(x, y, z);

							for(GridObject obj : region.get()) {
								if(obj instanceof Tile) {
									Tile tile = (Tile) obj;

									int tileX = tile.getX();
									int tileY = tile.getY();
									int tileZ = tile.getZ();

									if(minX < tileX + tile.getSizeX() && maxX >= tileX &&
											minY < tileY + tile.getSizeY() && maxY >= tileY &&
											minZ < tileZ + tile.getSizeZ() && maxZ >= tileZ) {
										return true;
									}
								} else if(obj instanceof Link && obj != this) {
									Link link = (Link) obj;

									Connector start2 = link.getStart();
									Connector end2 = link.getEnd();

									if((start.getDir() != start2.getDir() || !start.checkCollision(start2)) && (start.getDir() != end2.getDir() || !start.checkCollision(end2)) && (end.getDir() != start2.getDir() || !end.checkCollision(start2)) && (end.getDir() != end2.getDir() || !end.checkCollision(end2))) {
										if(link.iterateBounds((minX2, minY2, minZ2, maxX2, maxY2, maxZ2) -> {
											if(minX <= maxX2 && maxX >= minX2 &&
													minY <= maxY2 && maxY >= minY2 &&
													minZ <= maxZ2 && maxZ >= minZ2) {
												return true;
											}
											return false;
										})) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			} else {
				for(int x = sx; x <= ex; x++) {
					for(int y = sy; y <= ey; y++) {
						for(int z = sz; z <= ez; z++) {
							Region region = this.accelerator.get(x, y, z);

							if(region.getTag(tag)) {
								for(GridObject accelerated : region.get()) {
									if(accelerated instanceof Tile) {
										Tile tile = (Tile) accelerated;

										if(!tile.getTag(tag)) {
											continue;
										}

										int tileX = tile.getX();
										int tileY = tile.getY();
										int tileZ = tile.getZ();

										if(minX < tileX + tile.getSizeX() && maxX >= tileX &&
												minY < tileY + tile.getSizeY() && maxY >= tileY &&
												minZ < tileZ + tile.getSizeZ() && maxZ >= tileZ) {
											return true;
										}
									} else if(accelerated instanceof Link && accelerated != this) {
										Link link = (Link) accelerated;

										if(!link.getTag(tag)) {
											continue;
										}

										Connector start2 = link.getStart();
										Connector end2 = link.getEnd();

										if((start.getDir() != start2.getDir() || !start.checkCollision(start2)) && (start.getDir() != end2.getDir() || !start.checkCollision(end2)) && (end.getDir() != start2.getDir() || !end.checkCollision(start2)) && (end.getDir() != end2.getDir() || !end.checkCollision(end2))) {
											if(link.iterateBounds((minX2, minY2, minZ2, maxX2, maxY2, maxZ2) -> {
												if(minX <= maxX2 && maxX >= minX2 &&
														minY <= maxY2 && maxY >= minY2 &&
														minZ <= maxZ2 && maxZ >= minZ2) {
													return true;
												}
												return false;
											})) {
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return false;
		});
	}

	public boolean isColliding(Link link) {
		return this.iterateBounds((minX, minY, minZ, maxX, maxY, maxZ) -> {
			if(link.iterateBounds((minX2, minY2, minZ2, maxX2, maxY2, maxZ2) -> {
				if(minX <= maxX2 && maxX >= minX2 &&
						minY <= maxY2 && maxY >= minY2 &&
						minZ <= maxZ2 && maxZ >= minZ2) {
					return true;
				}
				return false;
			})) {
				return true;
			}
			return false;
		});
	}

	@Override
	final void getExtent(IExtent extent, int regionSize) {
		this.iterateBounds((minX, minY, minZ, maxX, maxY, maxZ) -> {
			int sx = Math.floorDiv(minX, regionSize);
			int sy = Math.floorDiv(minY, regionSize);
			int sz = Math.floorDiv(minZ, regionSize);
			int ex = Math.floorDiv(maxX + 1, regionSize);
			int ey = Math.floorDiv(maxY + 1, regionSize);
			int ez = Math.floorDiv(maxZ + 1, regionSize);
			for(int x = sx; x <= ex; x++) {
				for(int y = sy; y <= ey; y++) {
					for(int z = sz; z <= ez; z++) {
						extent.set(x, y, z);
					}
				}
			}
			return false;
		});
	}

	@Override
	public final Accelerator getAccelerator() {
		return this.accelerator;
	}

	@Override
	final void setRegions(Collection<Region> regions) {
		this.regions = regions;
	}

	@Override
	public final Collection<Region> getRegions() {
		return this.regions;
	}

	@Override
	public final void applyTags(BitSet set) {
		set.or(this.tags);
	}

	@Override
	public final boolean getTag(int tag) {
		return this.tags.get(tag);
	}

	@Override
	public Link setTag(int tag) {
		super.setTag(tag);
		return this;
	}

	@Override
	public Link setTag(int tag, boolean value) {
		super.setTag(tag, value);
		return this;
	}

	@Override
	public final Link setTagWithoutUpdate(int tag, boolean value) {
		this.tags.set(tag, value);
		return this;
	}

	@Override
	public final Link clearTags(LayoutPhase<?> phase) {
		this.tags.andNot(phase.getTagsMask());
		return this;
	}

	private void ensureMetaCapacity(int id) {
		if(this.meta == null) {
			this.meta = new Object[id + 1];
		} else if(id >= this.meta.length) {
			Object[] newMeta = new Object[id + 1];
			System.arraycopy(this.meta, 0, newMeta, 0, this.meta.length);
			this.meta = newMeta;
		}
	}

	@Override
	public <TMeta> Link updateOrCreateMeta(LayoutPhase<TMeta> phase, Consumer<TMeta> update) {
		super.updateOrCreateMeta(phase, update);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <TMeta> TMeta getMeta(LayoutPhase<TMeta> phase) {
		int id = phase.getMetaId();
		if(id < 0 || this.meta == null || id >= this.meta.length) {
			return null;
		}
		return (TMeta) this.meta[id];
	}

	@Override
	public <TMeta> Link setMeta(LayoutPhase<TMeta> phase, @Nullable TMeta meta) {
		int id = phase.getMetaId();
		if(id < 0) {
			return this;
		}
		this.ensureMetaCapacity(id);
		this.meta[id] = meta;
		return this;
	}
}
