package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.render.render.item.TileEntityShield;
import thebetweenlands.common.tileentity.TileEntityDruidAltar;
import thebetweenlands.common.tileentity.TileEntityPurifier;

public class TileEntityRegistry {
	public void init() {
		registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
		registerTileEntity(TileEntityShield.class, "shield");
		registerTileEntity(TileEntityPurifier.class, "purifier");
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}
}
