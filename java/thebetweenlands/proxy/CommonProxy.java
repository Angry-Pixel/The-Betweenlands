package thebetweenlands.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.inventory.container.ContainerDruidAltar;
import thebetweenlands.inventory.gui.GuiDruidAltar;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler {
	
	public static final int GUI_DRUID_ALTAR = 1;
	
	public void registerTileEntities() {
		registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}

	public void registerRenderInformation() {
		// unused serverside see ClientProxy for implementation
	}
	
	public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ) {
		// unused serverside see ClientProxy for implementation
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_DRUID_ALTAR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityDruidAltar)
				return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_DRUID_ALTAR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityDruidAltar)
				return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
		}
		return null;
	}
}