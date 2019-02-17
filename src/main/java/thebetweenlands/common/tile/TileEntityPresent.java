package thebetweenlands.common.tile;

import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityPresent extends TileEntityLootInventory {
	public TileEntityPresent() {
		super(TileEntityRegistry.PRESENT, 3, "container.present");
	}
}
