package thebetweenlands.proxy;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void registerTileEntities() {
		//registerTileEntity(TileEntityName.class, "tileEntityName"); **EXAMPLE**
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}

	public void registerRenderInformation() {
		// unused serverside see ClientProxy for implementation
	}
}