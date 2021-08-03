package thebetweenlands.common.world.gen.dungeon.layout.grid;

public class Connector {
	private final Cell cell;
	private final int offsetX, offsetY, offsetZ;
	private final Direction dir;
	private final int index;

	Connector(Cell cell, int x, int y, int z, Direction dir, int index) {
		this.cell = cell;
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		this.dir = dir;
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public Cell getCell() {
		return this.cell;
	}

	public int getOffsetX() {
		return this.offsetX;
	}

	public int getTileX() {
		return this.cell.getTileX() + this.offsetX;
	}

	public int getOffsetY() {
		return this.offsetY;
	}

	public int getTileY() {
		return this.cell.getTileY() + this.offsetY;
	}

	public int getOffsetZ() {
		return this.offsetZ;
	}

	public int getTileZ() {
		return this.cell.getTileZ() + this.offsetZ;
	}

	public Direction getDir() {
		return this.dir;
	}
}
