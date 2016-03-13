package thebetweenlands.tileentities;

public class TileEntityTarLootPot1 extends TileEntityBasicInventory {
	public TileEntityTarLootPot1() {
		super(3, "container.tarLootPot");
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
}