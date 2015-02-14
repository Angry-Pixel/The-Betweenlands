package thebetweenlands.proxy;

import net.minecraft.tileentity.TileEntity;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void registerTileEntities() {
		registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}

	public void registerRenderInformation() {
		// unused serverside see ClientProxy for implementation
	}
}