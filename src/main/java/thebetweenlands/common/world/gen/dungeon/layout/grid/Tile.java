package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.IExtent;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.Region;

public class Tile extends GridObject {
	private final Cell cell;
	private final int sizeX, sizeY, sizeZ;
	private final BitSet tags = new BitSet();
	private final Accelerator accelerator;

	private int x, y, z;
	private Map<Direction, Connector> connectors = new HashMap<>();
	private Collection<Region> regions = Collections.emptyList();
	private BitSet regionTags = new BitSet();

	private boolean tentative;
	private int prevX, prevY, prevZ;

	Tile(Cell cell, int sizeX, int sizeY, int sizeZ, Accelerator accelerator) {
		this.cell = cell;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.accelerator = accelerator;
	}

	Connector addConnector(Connector connector) {
		this.connectors.put(connector.getDir(), connector);
		return connector;
	}

	public Cell getCell() {
		return this.cell;
	}

	public Collection<Connector> getConnectors() {
		return this.connectors.values();
	}

	public int getSizeX() {
		return this.sizeX;
	}

	public int getSizeY() {
		return this.sizeY;
	}

	public int getSizeZ() {
		return this.sizeZ;
	}

	public void setPosWithoutUpdate(int x, int y, int z) {
		if(this.tentative) {
			throw new IllegalStateException("Cannot set position before confirming or cancelling the current tentative position");
		}
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setPos(int x, int y, int z) {
		if(this.tentative) {
			throw new IllegalStateException("Cannot set position before confirming or cancelling the current tentative position");
		}

		int dx = x - this.x;
		int dy = y - this.y;
		int dz = z - this.z;

		this.x = x;
		this.y = y;
		this.z = z;

		this.updateAccelerator(dx, dy, dz, false);

		for(Link l : this.cell.getLinks()) {
			l.updateAccelerator(dx, dy, dz, null, -1, false);
		}
	}

	public void setTentativePos(int x, int y, int z) {
		if(this.tentative) {
			throw new IllegalStateException("Cannot set tentative position before confirming or cancelling the current tentative position");
		}
		this.tentative = true;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void cancelTentativePos() {
		if(!this.tentative) {
			throw new IllegalStateException("Cannot cancel non-tentative position");
		}
		this.tentative = false;
		this.x = this.prevX;
		this.y = this.prevY;
		this.z = this.prevZ;
	}

	public void confirmTentativePos() {
		this.confirmTentativePos(null, -1);
	}

	public static boolean checkLinkDuplicate(Link link, Cell cell, @Nullable Set<Cell> tree, int tag) {
		if(tag < 0) {
			if(tree == null) {
				return false;
			}
			Cell startCell = link.getStart().getCell();
			return cell != startCell && tree.contains(startCell);
		} else {
			Cell startCell = link.getStart().getCell();
			return cell != startCell && !link.getTag(tag);
		}
	}

	public void confirmTentativePos(Set<Cell> tree, int tag) {
		if(!this.tentative) {
			throw new IllegalStateException("Cannot confirm non-tentative position");
		}
		this.tentative = false;

		int dx = this.x - this.prevX;
		int dy = this.y - this.prevY;
		int dz = this.z - this.prevZ;

		this.updateAccelerator(dx, dy, dz, false);

		if(tag < 0 && tree == null) {
			for(Link l : this.cell.getLinks()) {
				l.updateAccelerator(dx, dy, dz, null, -1, false);
			}
		} else {
			for(Link l : this.cell.getLinks()) {
				if(!Tile.checkLinkDuplicate(l, this.cell, tree, tag)) {
					l.updateAccelerator(dx, dy, dz, tree, tag, false);
				}
			}
		}
	}

	public void updateAccelerator(int dx, int dy, int dz, boolean force) {
		if(force) {
			this.accelerator.update(this);
		} else {
			int regionSize = this.accelerator.getRegionSize();

			if(this.x / regionSize != (this.x - dx) / regionSize ||
					this.y / regionSize != (this.y - dy) / regionSize ||
					this.z / regionSize != (this.z - dz) / regionSize ||
					(this.x + this.sizeX) / regionSize != (this.x + this.sizeX - dx) / regionSize ||
					(this.y + this.sizeY) / regionSize != (this.y + this.sizeY - dy) / regionSize ||
					(this.z + this.sizeZ) / regionSize != (this.z + this.sizeZ - dz) / regionSize) {
				this.accelerator.update(this);
			}
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public boolean isColliding(int tag) {
		final int regionSize = this.accelerator.getRegionSize();

		final int minX = this.x;
		final int minY = this.y;
		final int minZ = this.z;
		final int maxX = this.x + this.sizeX;
		final int maxY = this.y + this.sizeY;
		final int maxZ = this.z + this.sizeZ;

		int sx = Math.floorDiv(minX, regionSize);
		int sy = Math.floorDiv(minY, regionSize);
		int sz = Math.floorDiv(minZ, regionSize);
		int ex = Math.floorDiv(maxX, regionSize);
		int ey = Math.floorDiv(maxY, regionSize);
		int ez = Math.floorDiv(maxZ, regionSize);

		if(tag < 0) {
			for(int x = sx; x <= ex; x++) {
				for(int y = sy; y <= ey; y++) {
					for(int z = sz; z <= ez; z++) {
						Region region = this.accelerator.get(x, y, z);

						for(GridObject obj : region.get()) {
							if(obj instanceof Link) {
								Link link = (Link) obj;

								if(link.iterateBounds((minX2, minY2, minZ2, maxX2, maxY2, maxZ2) -> {
									if(minX2 < maxX && maxX2 >= minX &&
											minY2 < maxY && maxY2 >= minY &&
											minZ2 < maxZ && maxZ2 >= minZ) {
										return true;
									}
									return false;
								})) {
									return true;
								}
							} else if(obj instanceof Tile && obj != this) {
								Tile tile = (Tile) obj;

								int tileX = tile.getX();
								int tileY = tile.getY();
								int tileZ = tile.getZ();

								if(minX < tileX + tile.getSizeX() && maxX > tileX &&
										minY < tileY + tile.getSizeY() && maxY > tileY &&
										minZ < tileZ + tile.getSizeZ() && maxZ > tileZ) {
									return true;
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
								if(accelerated instanceof Link) {
									Link link = (Link) accelerated;

									if(!link.getTag(tag)) {
										continue;
									}

									if(link.iterateBounds((minX2, minY2, minZ2, maxX2, maxY2, maxZ2) -> {
										if(minX2 < maxX && maxX2 >= minX &&
												minY2 < maxY && maxY2 >= minY &&
												minZ2 < maxZ && maxZ2 >= minZ) {
											return true;
										}
										return false;
									})) {
										return true;
									}
								} else if(accelerated instanceof Tile && accelerated != this) {
									Tile tile = (Tile) accelerated;

									if(!tile.getTag(tag)) {
										continue;
									}

									int tileX = tile.getX();
									int tileY = tile.getY();
									int tileZ = tile.getZ();

									if(minX < tileX + tile.getSizeX() && maxX > tileX &&
											minY < tileY + tile.getSizeY() && maxY > tileY &&
											minZ < tileZ + tile.getSizeZ() && maxZ > tileZ) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	final void getExtent(IExtent extent, int regionSize) {
		int sx = Math.floorDiv(this.x, regionSize);
		int sy = Math.floorDiv(this.y, regionSize);
		int sz = Math.floorDiv(this.z, regionSize);
		int ex = Math.floorDiv(this.x + this.sizeX, regionSize);
		int ey = Math.floorDiv(this.y + this.sizeY, regionSize);
		int ez = Math.floorDiv(this.z + this.sizeZ, regionSize);
		for(int x = sx; x <= ex; x++) {
			for(int y = sy; y <= ey; y++) {
				for(int z = sz; z <= ez; z++) {
					extent.set(x, y, z);
				}
			}
		}
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
	public Tile setTag(int tag) {
		return (Tile) super.setTag(tag);
	}

	@Override
	public Tile setTag(int tag, boolean value) {
		return (Tile) super.setTag(tag, value);
	}

	@Override
	public final Tile setTagWithoutUpdate(int tag, boolean value) {
		this.tags.set(tag, value);
		return this;
	}

	@Override
	public final Tile clearTags(LayoutPhase<?> phase) {
		this.tags.andNot(phase.getTagsMask());
		return this;
	}

	@Override
	public <TMeta> Tile updateOrCreateMeta(LayoutPhase<TMeta> phase, Consumer<TMeta> update) {
		return (Tile) super.updateOrCreateMeta(phase, update);
	}

	@Override
	public <TMeta> Tile setMeta(LayoutPhase<TMeta> phase, @Nullable TMeta meta) {
		return (Tile) super.setMeta(phase, meta);
	}
}
