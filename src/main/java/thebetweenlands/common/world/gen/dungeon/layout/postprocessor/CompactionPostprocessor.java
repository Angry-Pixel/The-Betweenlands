package thebetweenlands.common.world.gen.dungeon.layout.postprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.MathHelper;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Cell;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Connector;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Tile;

public class CompactionPostprocessor extends Postprocessor<Object> {
	private final int maxIterations;
	
	private int thresholdCounter = 0;
	
	private Map<Connector, Set<Cell>> trees = new HashMap<>();
	private int prevTotalLinkLength;
	private int treeTagsStart;

	public CompactionPostprocessor(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	
	public CompactionPostprocessor() {
		this(50);
	}
	
	@Override
	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		super.init(grid, rng, tagSupplier);

		this.trees.clear();

		int numConnectors = 0;

		for(Cell cell : grid.getCells()) {
			cell.getTile().updateAccelerator(0, 0, 0, true);
			numConnectors += cell.getTile().getConnectors().size();
		}

		for(Link link : grid.getLinks()) {
			link.updateAccelerator(0, 0, 0, Collections.emptySet(), -1, true);
		}

		this.prevTotalLinkLength = 1000000000;

		this.treeTagsStart = tagSupplier.reserve(numConnectors);
	}

	@Override
	public boolean process() {
		int maxLinkLen = 1;
		for(Link l : this.grid.getLinks()) {
			maxLinkLen = Math.max(maxLinkLen, l.getApproximateLength());
		}

		int stepSize = Math.max(1, MathHelper.smallestEncompassingPowerOfTwo(maxLinkLen) / 2);

		for(int i = 0; i < this.maxIterations; i++) {
			if(!this.compactIteration(this.rng, stepSize) && stepSize <= 1) {
				break;
			}

			stepSize = Math.max(1, stepSize / 2);
		}

		for(Link link : this.grid.getLinks()) {
			if(link.isColliding(-1)) {
				return false;
			}
		}

		return true;
	}

	//TODO Exposed for debugging
	public boolean compactIteration(Random rng, int stepSize) {
		for(Link link : this.grid.getLinks()) {
			if(rng.nextBoolean()) {
				this.compactLink(rng, link, link.getStart(), link.getEnd(), stepSize);
			} else {
				this.compactLink(rng, link, link.getEnd(), link.getStart(), stepSize);
			}
		}

		int newTotalLinkLength = 0;

		for(Link link : this.grid.getLinks()) {
			int len = link.getApproximateLength();
			newTotalLinkLength += len;
		}

		if(this.prevTotalLinkLength - newTotalLinkLength < 2) {
			if(++this.thresholdCounter >= 3) {
				return false;
			}
		} else {
			this.thresholdCounter = 0;
		}
		
		this.prevTotalLinkLength = newTotalLinkLength;

		return true;
	}

	private boolean compactLink(Random rng, Link link, Connector connector, Connector other, int stepSize) {
		//TODO Add option to preserve floors, i.e. not to pull rooms together
		//on the Y axis unless they're on the same floor

		int dx = connector.getTileX() - other.getTileX();
		int dy = connector.getTileY() - other.getTileY();
		int dz = connector.getTileZ() - other.getTileZ();

		int mx1 = 0;
		int my1 = 0;
		int mz1 = 0;

		int mx2 = 0;
		int my2 = 0;
		int mz2 = 0;

		if(Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= Math.abs(dz)) {
			mx1 = (int)Math.signum(dx) * stepSize;
			if(Math.abs(dy) >= Math.abs(dz)) {
				my2 = (int)Math.signum(dy) * stepSize;
			} else {
				mz2 = (int)Math.signum(dz) * stepSize;
			}
		} else if(Math.abs(dy) > Math.abs(dx) && Math.abs(dy) >= Math.abs(dz)) {
			my1 = (int)Math.signum(dy) * stepSize;
			if(Math.abs(dx) >= Math.abs(dz)) {
				mx2 = (int)Math.signum(dx) * stepSize;
			} else {
				mz2 = (int)Math.signum(dz) * stepSize;
			}
		} else if(Math.abs(dz) > Math.abs(dx) && Math.abs(dz) > Math.abs(dy)) {
			mz1 = (int)Math.signum(dz) * stepSize;
			if(Math.abs(dx) >= Math.abs(dy)) {
				mx2 = (int)Math.signum(dx) * stepSize;
			} else {
				my2 = (int)Math.signum(dy) * stepSize;
			}
		}

		mx1 = MathHelper.clamp(mx1, -Math.abs(dx), Math.abs(dx));
		my1 = MathHelper.clamp(my1, -Math.abs(dy), Math.abs(dy));
		mz1 = MathHelper.clamp(mz1, -Math.abs(dz), Math.abs(dz));

		mx2 = MathHelper.clamp(mx2, -Math.abs(dx), Math.abs(dx));
		my2 = MathHelper.clamp(my2, -Math.abs(dy), Math.abs(dy));
		mz2 = MathHelper.clamp(mz2, -Math.abs(dz), Math.abs(dz));

		if(mx1 == 0 && my1 == 0 && mz1 == 0) {
			return false;
		}

		Cell cell = connector.getCell();

		int treeTag = this.treeTagsStart + connector.getIndex();
		Set<Cell> tree = this.getTree(connector, treeTag);
		/*if(tree.size() <= this.cellList.size() / 2) {
			treeTag = -1;
		}*/

		boolean isValid = true;

		//Try moving cell and subtree along major axis
		{
			for(Cell c : tree) {
				c.setTentativeTilePos(c.getTileX() - mx1, c.getTileY() - my1, c.getTileZ() - mz1);
			}

			for(Cell c : tree) {
				if(c.getTile().isColliding(treeTag)) {
					isValid = false;
					break;
				}

				for(Link l : c.getLinks()) {
					if(!Tile.checkLinkDuplicate(l, c, tree, treeTag) && (l.getConstraint(null) || l.isColliding(treeTag))) {
						isValid = false;
						break;
					}
				}
			}

			if(!isValid) {
				for(Cell c : tree) {
					c.cancelTentativeTilePos();
				}
			} else {
				for(Cell c : tree) {
					c.confirmTentativeTilePos(tree, treeTag);
				}
				return true;
			}
		}

		isValid = true;

		//Try moving single cell only along major axis
		{
			cell.setTentativeTilePos(cell.getTileX() - mx1, cell.getTileY() - my1, cell.getTileZ() - mz1);

			if(cell.getTile().isColliding(-1)) {
				isValid = false;
			}

			if(isValid) {
				for(Link l : cell.getLinks()) {
					if(l.getConstraint(null) || l.isColliding(-1)) {
						isValid = false;
						break;
					}
				}
			}

			if(!isValid) {
				cell.cancelTentativeTilePos();
			} else {
				cell.confirmTentativeTilePos();
				return true;
			}
		}

		isValid = true;

		//Try moving cell with subtree along second major axis
		{
			for(Cell c : tree) {
				c.setTentativeTilePos(c.getTileX() - mx2, c.getTileY() - my2, c.getTileZ() - mz2);
			}

			for(Cell c : tree) {
				if(c.getTile().isColliding(treeTag)) {
					isValid = false;
					break;
				}

				for(Link l : c.getLinks()) {
					if(!Tile.checkLinkDuplicate(l, c, tree, treeTag) && (l.getConstraint(null) || l.isColliding(treeTag))) {
						isValid = false;
						break;
					}
				}
			}

			if(!isValid) {
				for(Cell c : tree) {
					c.cancelTentativeTilePos();
				}
			} else {
				for(Cell c : tree) {
					c.confirmTentativeTilePos(tree, treeTag);
				}
				return true;
			}
		}

		isValid = true;

		//Try moving single cell only along second major axis
		{
			cell.setTentativeTilePos(cell.getTileX() - mx2, cell.getTileY() - my2, cell.getTileZ() - mz2);

			if(cell.getTile().isColliding(-1)) {
				isValid = false;
			}

			if(isValid) {
				for(Link l : cell.getLinks()) {
					if(l.getConstraint(null) || l.isColliding(-1)) {
						isValid = false;
						break;
					}
				}
			}

			if(!isValid) {
				cell.cancelTentativeTilePos();
			} else {
				cell.confirmTentativeTilePos();
				return true;
			}
		}

		return false;
	}

	private Set<Cell> getTree(Connector connector, int tag) {
		Set<Cell> result = this.trees.get(connector);
		if(result != null) {
			return result;
		}

		result = new HashSet<>();

		List<Cell> pending = new ArrayList<>();
		Set<Cell> visited = new HashSet<>();

		result.add(connector.getCell());
		pending.add(connector.getCell());

		Direction dir = connector.getDir();

		while(!pending.isEmpty()) {
			Cell current = pending.remove(pending.size() - 1);

			for(Link link : current.getLinks()) {
				Connector end;
				if(link.getStart().getCell() == current) {
					end = link.getEnd();
				} else {
					end = link.getStart();
				}

				int dx = end.getTileX() - connector.getTileX();
				int dy = end.getTileY() - connector.getTileY();
				int dz = end.getTileZ() - connector.getTileZ();

				//Avoid loops
				if((dir.x == 0 || dir.x == -(int)Math.signum(dx)) && (dir.y == 0 || dir.y == -(int)Math.signum(dy)) && (dir.z == 0 || dir.z == -(int)Math.signum(dz))) {
					if(visited.add(end.getCell())) {
						result.add(end.getCell());
						pending.add(end.getCell());
					}
				} else {
					/*if(visited.add(end.getCell())) {
							pending.add(end.getCell());
						}*/
				}
			}
		}

		if(tag >= 0) {
			for(Cell c : this.grid.getCells()) {
				if(!result.contains(c)) {
					Tile tile = c.getTile();
					tile.setTag(tag);

					for(Link link : c.getLinks()) {
						link.setTag(tag);
					}
				}
			}
		}

		this.trees.put(connector, result);

		return result;
	}
}
