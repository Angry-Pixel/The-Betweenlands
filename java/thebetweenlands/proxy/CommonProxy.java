package thebetweenlands.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.blocks.BlockWeedWoodChest;
import thebetweenlands.inventory.container.ContainerBLCraftingTable;
import thebetweenlands.inventory.container.ContainerDruidAltar;
import thebetweenlands.inventory.container.ContainerWeedWoodChest;
import thebetweenlands.inventory.gui.GuiBLCrafting;
import thebetweenlands.inventory.gui.GuiDruidAltar;
import thebetweenlands.inventory.gui.GuiWeedWoodChest;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
        implements IGuiHandler
{
    public static final int GUI_DRUID_ALTAR = 1;
    public static final int GUI_WEEDWOOD_CRAFT = 2;
    public static final int GUI_WEEDWOOD_CHEST= 3;

    public void registerTileEntities() {
        registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
        registerTileEntity(TileEntityWeedWoodChest.class, "weedWoodChest");
    }

    private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
        GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
    }

    public void registerRenderInformation() {
        // unused serverside see ClientProxy for implementation
    }

    public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, Object... data) {
        // unused serverside see ClientProxy for implementation
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if( ID == GUI_DRUID_ALTAR ) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityDruidAltar ) {
                return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
            }
        }
        
		else if (ID == GUI_WEEDWOOD_CRAFT)
			return new ContainerBLCraftingTable(player.inventory, world, x, y, z);
        
		else if (ID == GUI_WEEDWOOD_CHEST) {
        	IInventory inventory = BlockWeedWoodChest.getInventory(world, x, y, z);
        	return new ContainerWeedWoodChest(player.inventory, inventory);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if( ID == GUI_DRUID_ALTAR ) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityDruidAltar ) {
                return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
            }
        }
        
        else if (ID == GUI_WEEDWOOD_CRAFT)
			return new GuiBLCrafting(player.inventory, world, x, y, z);
        
        else if (ID == GUI_WEEDWOOD_CHEST) {
        	IInventory inventory = BlockWeedWoodChest.getInventory(world, x, y, z);
        	return new GuiWeedWoodChest(player.inventory, inventory);
        }

        return null;
    }
}
