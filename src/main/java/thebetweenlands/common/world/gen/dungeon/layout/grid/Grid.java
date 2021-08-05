package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link.Constraint;

public class Grid extends MetaObject {
	private final Random rng;
	private final int stairsRotationDir;
	private final Accelerator accelerator;
	private final int maxIterations;

	private TLongObjectMap<Cell> cells;
	private List<Link> linkList;
	private List<Cell> cellList;

	private int nextConnectorIndex = 0;

	public Grid(Random rng, int regionSize, int maxIterations) {
		this.rng = rng;
		this.stairsRotationDir = rng.nextBoolean() ? -1 : 1;
		this.accelerator = new Accelerator(regionSize);
		this.maxIterations = maxIterations;
		this.reset();
	}

	public Grid(Random rng, int regionSize) {
		this(rng, regionSize, 100);
	}

	public void reset() {
		this.cells = new TLongObjectHashMap<>();
		this.linkList = new ArrayList<>();
		this.cellList = new ArrayList<>();
	}

	public Accelerator getAccelerator() {
		return this.accelerator;
	}

	public List<Link> getLinks() {
		return this.linkList;
	}

	public List<Cell> getCells() {
		return this.cellList;
	}

	public Cell getCell(int x, int y, int z) {
		return this.cells.get(Accelerator.xyzToKey(x, y, z));
	}

	public Cell setCell(int x, int y, int z, int tileSizeX, int tileSizeY, int tileSizeZ) {
		this.removeCell(x, y, z);
		Cell cell = new Cell(x, y, z, tileSizeX, tileSizeY, tileSizeZ, this.accelerator, () -> this.nextConnectorIndex++);
		this.cellList.add(cell);
		this.cells.put(Accelerator.xyzToKey(x, y, z), cell);
		return cell;
	}

	@Nullable
	public Cell removeCell(int x, int y, int z) {
		Cell cell = this.cells.remove(Accelerator.xyzToKey(x, y, z));
		if(cell != null) {
			this.cellList.remove(cell);
			for(Link link : cell.getLinks()) {
				this.linkList.remove(link);
				link.getStart().getCell().removeLink(link);
				link.getEnd().getCell().removeLink(link);
			}
		}
		return cell;
	}

	public Link connect(Connector start, Connector end, int xzPerY) {
		Link link = new Link(start, end, this.accelerator, xzPerY, this.stairsRotationDir);
		this.linkList.add(link);
		start.getCell().addLink(link);
		end.getCell().addLink(link);
		return link;
	}

	public void setCellSizes() {
		TIntIntMap sizesX = new TIntIntHashMap();
		TIntIntMap sizesY = new TIntIntHashMap();
		TIntIntMap sizesZ = new TIntIntHashMap();

		IntSortedSet xCoords = new IntRBTreeSet();
		IntSortedSet yCoords = new IntRBTreeSet();
		IntSortedSet zCoords = new IntRBTreeSet();

		for(Cell cell : this.cellList) {
			xCoords.add(cell.getX());
			sizesX.put(cell.getX(), Math.max(sizesX.get(cell.getX()), cell.getSizeX()));

			yCoords.add(cell.getY());
			sizesY.put(cell.getY(), Math.max(sizesY.get(cell.getY()), cell.getSizeY()));

			zCoords.add(cell.getZ());
			sizesZ.put(cell.getZ(), Math.max(sizesZ.get(cell.getZ()), cell.getSizeZ()));
		}

		TIntIntMap offsetsX = new TIntIntHashMap();
		TIntIntMap offsetsY = new TIntIntHashMap();
		TIntIntMap offsetsZ = new TIntIntHashMap();

		int offsetX = 0;
		int offsetY = 0;
		int offsetZ = 0;

		IntBidirectionalIterator xit = xCoords.iterator();
		while(xit.hasNext()) {
			int x = xit.nextInt();
			offsetsX.put(x, offsetX);
			offsetX += sizesX.get(x);
		}

		IntBidirectionalIterator yit = yCoords.iterator();
		while(yit.hasNext()) {
			int y = yit.nextInt();
			offsetsY.put(y, offsetY);
			offsetY += sizesY.get(y);
		}

		IntBidirectionalIterator zit = zCoords.iterator();
		while(zit.hasNext()) {
			int z = zit.nextInt();
			offsetsZ.put(z, offsetZ);
			offsetZ += sizesZ.get(z);
		}

		for(Cell cell : this.cellList) {
			int x = cell.getX();
			int y = cell.getY();
			int z = cell.getZ();
			cell.grow(sizesX.get(x) - cell.getSizeX(), sizesY.get(y) - cell.getSizeY(), sizesZ.get(z) - cell.getSizeZ(), this.rng.nextBoolean());
			cell.setCellTileCoord(offsetsX.get(x), offsetsY.get(y), offsetsZ.get(z));
		}
	}

	public void clearTags(LayoutPhase<?> phase) {
		this.accelerator.iterateRegions(region -> {
			region.clearTags(phase);
			return false;
		});

		for(Cell cell : this.cellList) {
			cell.getTile().clearTags(phase);
		}

		for(Link link : this.linkList) {
			link.clearTags(phase);
		}
	}

	public void clearMeta(LayoutPhase<?> phase) {
		if(phase.getMeta() != null) {
			this.setMeta(phase, null);

			for(Cell cell : this.cellList) {
				cell.getTile().setMeta(phase, null);
			}

			for(Link link : this.linkList) {
				link.setMeta(phase, null);
			}
		}
	}

	public void resolve() {
		this.accelerator.clear();

		for(Cell cell : this.cellList) {
			cell.reset();
		}

		this.setCellSizes();

		for(int i = 0; i < this.maxIterations; i++) {
			this.resolveIteration();

			boolean valid = true;

			for(Link link : this.linkList) {
				if(link.getConstraint(null)) {
					valid = false;
					break;
				}
			}

			if(valid) {
				break;
			}
		}
	}

	//TODO Exposed for debugging
	public boolean resolveIteration() {
		class Correction {
			private int expandNegativeX, expandNegativeY, expandNegativeZ, expandPositiveX, expandPositiveY, expandPositiveZ;
			private int pushNegativeX, pushNegativeY, pushNegativez, pushPositiveX, pushPositiveY, pushPositiveZ;
			private int disperse;
		}

		Map<Cell, Correction> cellCorrections = new HashMap<>();

		Constraint constraint = new Constraint();

		for(Link link : this.linkList) {
			if(link.getConstraint(constraint)) {
				Cell start = link.getStart().getCell();
				Cell end = link.getEnd().getCell();

				Cell correctionCell = null;
				Correction cellCorrection = cellCorrections.get(start);
				if(cellCorrection != null) {
					correctionCell = start;
				} else {
					cellCorrection = cellCorrections.get(end);
					if(cellCorrection != null) {
						correctionCell = end;
					}
				}

				if(cellCorrection == null) {
					if(this.rng.nextBoolean()) {
						cellCorrections.put(start, cellCorrection = new Correction());
						correctionCell = start;
					} else {
						cellCorrections.put(end, cellCorrection = new Correction());
						correctionCell = end;
					}
				}

				if(correctionCell != null) {
					if(correctionCell == start) {
						cellCorrection.expandNegativeX = Math.min(cellCorrection.expandNegativeX, constraint.expandX);
						cellCorrection.expandNegativeY = Math.min(cellCorrection.expandNegativeY, constraint.expandY);
						cellCorrection.expandNegativeZ = Math.min(cellCorrection.expandNegativeZ, constraint.expandZ);
						cellCorrection.expandPositiveX = Math.max(cellCorrection.expandPositiveX, constraint.expandX);
						cellCorrection.expandPositiveY = Math.max(cellCorrection.expandPositiveY, constraint.expandY);
						cellCorrection.expandPositiveZ = Math.max(cellCorrection.expandPositiveZ, constraint.expandZ);
						cellCorrection.pushNegativeX = Math.min(cellCorrection.pushNegativeX, constraint.pushX);
						cellCorrection.pushNegativeY = Math.min(cellCorrection.pushNegativeY, constraint.pushY);
						cellCorrection.pushNegativez = Math.min(cellCorrection.pushNegativez, constraint.pushZ);
						cellCorrection.pushPositiveX = Math.max(cellCorrection.pushPositiveX, constraint.pushX);
						cellCorrection.pushPositiveY = Math.max(cellCorrection.pushPositiveY, constraint.pushY);
					} else {
						cellCorrection.expandNegativeX = Math.min(cellCorrection.expandNegativeX, -constraint.expandX);
						cellCorrection.expandNegativeY = Math.min(cellCorrection.expandNegativeY, -constraint.expandY);
						cellCorrection.expandNegativeZ = Math.min(cellCorrection.expandNegativeZ, -constraint.expandZ);
						cellCorrection.expandPositiveX = Math.max(cellCorrection.expandPositiveX, -constraint.expandX);
						cellCorrection.expandPositiveY = Math.max(cellCorrection.expandPositiveY, -constraint.expandY);
						cellCorrection.expandPositiveZ = Math.max(cellCorrection.expandPositiveZ, -constraint.expandZ);
						cellCorrection.pushNegativeX = Math.min(cellCorrection.pushNegativeX, -constraint.pushX);
						cellCorrection.pushNegativeY = Math.min(cellCorrection.pushNegativeY, -constraint.pushY);
						cellCorrection.pushNegativez = Math.min(cellCorrection.pushNegativez, -constraint.pushZ);
						cellCorrection.pushPositiveX = Math.max(cellCorrection.pushPositiveX, -constraint.pushX);
						cellCorrection.pushPositiveY = Math.max(cellCorrection.pushPositiveY, -constraint.pushY);
						cellCorrection.pushPositiveZ = Math.max(cellCorrection.pushPositiveZ, -constraint.pushZ);
					}
					cellCorrection.disperse = Math.max(cellCorrection.disperse, constraint.disperse);
				} else {
					if(this.rng.nextBoolean()) {
						cellCorrections.put(start, new Correction());
					} else {
						cellCorrections.put(end, new Correction());
					}
				}
			}
		}

		for(Entry<Cell, Correction> corrections : cellCorrections.entrySet()) {
			Cell cell = corrections.getKey();
			Correction correction = corrections.getValue();

			cell.moveTile(correction.expandPositiveX, correction.expandPositiveY, correction.expandPositiveZ, false);
			cell.moveTile(correction.expandNegativeX, correction.expandNegativeY, correction.expandNegativeZ, false);

			cell.moveTile(correction.disperse, 0, correction.disperse, true);
			cell.moveTile(-correction.disperse, 0, -correction.disperse, true);

			if(correction.disperse != 0) {
				for(Link link : cell.getLinks()) {
					Connector otherConnector;
					if(link.getStart().getCell() == cell) {
						otherConnector = link.getEnd();
					} else {
						otherConnector = link.getStart();
					}
					Cell otherCell = otherConnector.getCell();

					if(cell.getY() != otherCell.getY()) {
						//Move the two tiles in opposite directions to move them apart
						otherCell.moveTile(correction.disperse * otherConnector.getDir().x, 0, correction.disperse * otherConnector.getDir().z, true);
						cell.moveTile(correction.disperse * -otherConnector.getDir().x, 0, correction.disperse * -otherConnector.getDir().z, true);
					}
				}
			}

			if(correction.pushNegativeX != 0) {
				for(int yo = -1; yo < 1; yo++) {
					Cell pushCell = this.getCell(cell.getX() - 1, cell.getY() + yo, cell.getZ());
					if(pushCell != null) {
						pushCell.moveTile(-correction.pushNegativeX, 0, 0, true);
					}
				}
			}

			if(correction.pushNegativeY != 0) {
				Cell pushCell = this.getCell(cell.getX(), cell.getY() - 1, cell.getZ());
				if(pushCell != null) {
					pushCell.moveTile(0, -correction.pushNegativeY, 0, true);
				}
			}

			if(correction.pushNegativez != 0) {
				for(int yo = -1; yo < 1; yo++) {
					Cell pushCell = this.getCell(cell.getX(), cell.getY() + yo, cell.getZ() - 1);
					if(pushCell != null) {
						pushCell.moveTile(0, 0, -correction.pushNegativez, true);
					}
				}
			}

			if(correction.pushPositiveX != 0) {
				for(int yo = -1; yo < 1; yo++) {
					Cell pushCell = this.getCell(cell.getX() + 1, cell.getY() + yo, cell.getZ());
					if(pushCell != null) {
						pushCell.moveTile(-correction.pushPositiveX, 0, 0, true);
					}
				}
			}

			if(correction.pushPositiveY != 0) {
				Cell pushCell = this.getCell(cell.getX(), cell.getY() + 1, cell.getZ());
				if(pushCell != null) {
					pushCell.moveTile(0, -correction.pushPositiveY, 0, true);
				}
			}

			if(correction.pushPositiveZ != 0) {
				for(int yo = -1; yo < 1; yo++) {
					Cell pushCell = this.getCell(cell.getX(), cell.getY() + yo, cell.getZ() + 1);
					if(pushCell != null) {
						pushCell.moveTile(0, 0, -correction.pushPositiveZ, true);
					}
				}
			}
		}

		this.setCellSizes();

		return !cellCorrections.isEmpty();
	}

	@Override
	public <TMeta> Grid updateOrCreateMeta(LayoutPhase<TMeta> phase, Consumer<TMeta> update) {
		return (Grid) super.updateOrCreateMeta(phase, update);
	}

	@Override
	public <TMeta> Grid setMeta(LayoutPhase<TMeta> phase, @Nullable TMeta meta) {
		return (Grid) super.setMeta(phase, meta);
	}
}
