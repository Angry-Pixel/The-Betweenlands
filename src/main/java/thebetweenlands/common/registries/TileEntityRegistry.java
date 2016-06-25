package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class TileEntityRegistry {
	public void init() {
		registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
		registerTileEntity(TileEntityPurifier.class, "purifier");
		registerTileEntity(TileEntityWeedwoodWorkbench.class, "weedwoodWorkbench");
		registerTileEntity(TileEntityCompostBin.class, "compost_bin");
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}
}
