package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntity;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityWisp extends TileEntity {
	public long lastSpawn = 0;
	
	public TileEntityWisp() {
		super(TileEntityRegistry.WISP);
	}
}
