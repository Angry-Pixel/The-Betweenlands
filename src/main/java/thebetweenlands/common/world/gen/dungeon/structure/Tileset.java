package thebetweenlands.common.world.gen.dungeon.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thebetweenlands.api.entity.spawning.IWeightProvider;
import thebetweenlands.util.WeightedList;

public class Tileset {
	public static class Entry implements IWeightProvider {
		public final TileInfo tile;
		private final short weight;

		private Entry(TileInfo info, short weight) {
			this.tile = info;
			this.weight = weight;
		}

		@Override
		public short getWeight() {
			return this.weight;
		}
	}

	private List<Entry> entries = new ArrayList<>();

	private boolean isPathTileset;

	public Tileset addTile(TileInfo tile, short weight) {
		this.entries.add(new Entry(tile, weight));
		this.categorize();
		return this;
	}

	private void categorize() {
		Map<TileInfo.Shape, Set<Integer>> shapeRotations = new HashMap<>();
		for(TileInfo.Shape shape : TileInfo.Shape.values()) {
			shapeRotations.put(shape, new HashSet<>());
		}

		for(Entry entry : this.entries) {
			TileInfo tile = entry.tile;
			shapeRotations.get(tile.getShape()).addAll(tile.getRotations());
		}

		this.isPathTileset = true;
		for(TileInfo.Shape shape : TileInfo.Shape.CORRIDOR_SHAPES) {
			if(shapeRotations.get(shape).size() != 4) {
				this.isPathTileset = false;
			}
		}
	}

	public boolean isPathTileset() {
		return this.isPathTileset;
	}

	public WeightedList<Entry> getEntries() {
		WeightedList<Entry> list = new WeightedList<Entry>();
		list.addAll(this.entries);
		return list;
	}

	public WeightedList<Entry> getEntries(TileInfo.Shape shape) {
		return this.getEntries(shape, -1);
	}

	public WeightedList<Entry> getEntries(TileInfo.Shape shape, int rotation) {
		WeightedList<Entry> list = new WeightedList<Entry>();
		for(Entry entry : this.entries) {
			if(entry.tile.getShape() == shape && (rotation < 0 || entry.tile.getRotations().contains(rotation))) {
				list.add(entry);
			}
		}
		return list;
	}
}
