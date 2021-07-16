package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class Cell {
	private final int x, y, z;
	private final Tile tile;
	private final List<Link> links = new ArrayList<>();
	private final Supplier<Integer> nextConnectorIndex;

	private int cellTileX, cellTileY, cellTileZ;
	private int[] padding = new int[6];
	private int[] immutablePadding = new int[6];

	Cell(int x, int y, int z, int tileSizeX, int tileSizeY, int tileSizeZ, Accelerator accelerator, Supplier<Integer> nextConnectorIndex) {
		this.cellTileX = this.x = x;
		this.cellTileY = this.y = y;
		this.cellTileZ = this.z = z;
		this.tile = new Tile(this, tileSizeX, tileSizeY, tileSizeZ, accelerator);
		this.nextConnectorIndex = nextConnectorIndex;
	}

	public void reset() {
		for(int i = 0; i < 6; i++) {
			this.padding[i] = 0;
		}
		for(int i = 0; i < 6; i++) {
			this.immutablePadding[i] = 0;
		}
	}

	public Tile getTile() {
		return this.tile;
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

	public int getSizeX() {
		return this.padding[0] + this.immutablePadding[0] + this.tile.getSizeX() + this.padding[1] + this.immutablePadding[1];
	}

	public int getSizeY() {
		return this.padding[2] + this.immutablePadding[2] + this.tile.getSizeY() + this.padding[3] + this.immutablePadding[3];
	}

	public int getSizeZ() {
		return this.padding[4] + this.immutablePadding[4] + this.tile.getSizeZ() + this.padding[5] + this.immutablePadding[5];
	}

	public int getTileSizeX() {
		return this.tile.getSizeX();
	}

	public int getTileSizeY() {
		return this.tile.getSizeY();
	}

	public int getTileSizeZ() {
		return this.tile.getSizeZ();
	}

	public int getTileX() {
		return this.tile.getX();
	}

	public int getTileY() {
		return this.tile.getY();
	}

	public int getTileZ() {
		return this.tile.getZ();
	}

	public void moveTile(int x, int y, int z, boolean immutable) {
		if(immutable) {
			if(x < 0) {
				if(this.padding[1] > 0) {
					this.padding[1] -= Math.min(this.padding[1], -x);
				}
				this.immutablePadding[0] -= x;
			} else {
				if(this.padding[0] > 0) {
					this.padding[0] -= Math.min(this.padding[0], x);
				}
				this.immutablePadding[1] += x;
			}
			if(y < 0) {
				if(this.padding[3] > 0) {
					this.padding[3] -= Math.min(this.padding[3], -y);
				}
				this.immutablePadding[2] -= y;
			} else {
				if(this.padding[2] > 0) {
					this.padding[2] -= Math.min(this.padding[2], y);
				}
				this.immutablePadding[3] += y;
			}
			if(z < 0) {
				if(this.padding[5] > 0) {
					this.padding[5] -= Math.min(this.padding[5], -z);
				}
				this.immutablePadding[4] -= z;
			} else {
				if(this.padding[4] > 0) {
					this.padding[4] -= Math.min(this.padding[4], z);
				}
				this.immutablePadding[5] += z;
			}
		} else {
			if(x < 0) {
				if(this.padding[1] > 0) {
					int contraction = Math.min(this.padding[1], -x);
					x += contraction;
					this.padding[1] -= contraction;
					this.padding[0] += contraction;
				}
				this.padding[0] -= x;
			} else {
				if(this.padding[0] > 0) {
					int contraction = Math.min(this.padding[0], x);
					x -= contraction;
					this.padding[0] -= contraction;
					this.padding[1] += contraction;
				}
				this.padding[1] += x;
			}
			if(y < 0) {
				if(this.padding[3] > 0) {
					int contraction = Math.min(this.padding[3], -y);
					y += contraction;
					this.padding[3] -= contraction;
					this.padding[2] += contraction;
				}
				this.padding[2] -= y;
			} else {
				if(this.padding[2] > 0) {
					int contraction = Math.min(this.padding[2], y);
					y -= contraction;
					this.padding[2] -= contraction;
					this.padding[3] += contraction;
				}
				this.padding[3] += y;
			}
			if(z < 0) {
				if(this.padding[5] > 0) {
					int contraction = Math.min(this.padding[5], -z);
					z += contraction;
					this.padding[5] -= contraction;
					this.padding[4] += contraction;
				}
				this.padding[4] -= z;
			} else {
				if(this.padding[4] > 0) {
					int contraction = Math.min(this.padding[4], z);
					z -= contraction;
					this.padding[4] -= contraction;
					this.padding[5] += contraction;
				}
				this.padding[5] += z;
			}
		}

		this.resetTilePos();
	}

	public void grow(int x, int y, int z, boolean preferLow) {
		this.grow(0, x, preferLow);
		this.grow(2, y, preferLow);
		this.grow(4, z, preferLow);
	}

	private void grow(int idx, int amount, boolean preferLow) {
		int lowerHalf = amount / 2;
		if(preferLow) {
			this.padding[idx] += amount - lowerHalf;
			this.padding[idx + 1] += lowerHalf;
		} else {
			this.padding[idx] += lowerHalf;
			this.padding[idx + 1] += amount - lowerHalf;
		}

		this.resetTilePos();
	}

	public void setCellTileCoord(int x, int y, int z) {
		this.cellTileX = x;
		this.cellTileY = y;
		this.cellTileZ = z;

		this.resetTilePos();
	}

	private void resetTilePos() {
		this.tile.setPosWithoutUpdate(this.cellTileX + this.padding[0] + this.immutablePadding[0], this.cellTileY + this.padding[2] + this.immutablePadding[2], this.cellTileZ + this.padding[4] + this.immutablePadding[4]);
	}

	public void setTentativeTilePos(int x, int y, int z) {
		this.tile.setTentativePos(x, y, z);
	}

	public void cancelTentativeTilePos() {
		this.tile.cancelTentativePos();
	}

	public void confirmTentativeTilePos() {
		this.tile.confirmTentativePos();
	}

	public void confirmTentativeTilePos(Set<Cell> tree, int tag) {
		this.tile.confirmTentativePos(tree, tag);
	}

	public int getCellTileX() {
		return this.cellTileX;
	}

	public int getCellTileY() {
		return this.cellTileY;
	}

	public int getCellTileZ() {
		return this.cellTileZ;
	}

	public boolean isTileDetached() {
		return this.getTileX() != this.cellTileX + this.padding[0] + this.immutablePadding[0] || this.getTileY() != this.cellTileY + this.padding[2] + this.immutablePadding[2] || this.getTileZ() != this.cellTileZ + this.padding[4] + this.immutablePadding[4];
	}

	public Connector addTileConnector(int x, int y, int z, Direction dir) {
		return this.tile.addConnector(new Connector(this, x, y, z, dir, this.nextConnectorIndex.get()));
	}

	void addLink(Link link) {
		this.links.add(link);
	}
	
	void removeLink(Link link) {
		this.links.remove(link);
	}

	public List<Link> getLinks() {
		return this.links;
	}
}
