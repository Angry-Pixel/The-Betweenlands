package thebetweenlands.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.blocks.container.BlockWeedWoodChest;
import thebetweenlands.event.debugging.DebugHandlerCommon;
import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.inventory.container.ContainerBLCraftingTable;
import thebetweenlands.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.inventory.container.ContainerBLFurnace;
import thebetweenlands.inventory.container.ContainerDruidAltar;
import thebetweenlands.inventory.container.ContainerPestleAndMortar;
import thebetweenlands.inventory.container.ContainerPurifier;
import thebetweenlands.inventory.container.ContainerWeedWoodChest;
import thebetweenlands.inventory.gui.GuiAnimator;
import thebetweenlands.inventory.gui.GuiBLCrafting;
import thebetweenlands.inventory.gui.GuiBLDualFurnace;
import thebetweenlands.inventory.gui.GuiBLFurnace;
import thebetweenlands.inventory.gui.GuiDruidAltar;
import thebetweenlands.inventory.gui.GuiPestleAndMortar;
import thebetweenlands.inventory.gui.GuiPurifier;
import thebetweenlands.inventory.gui.GuiWeedWoodChest;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.tileentities.TileEntityAlembic;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;
import thebetweenlands.tileentities.TileEntityBLDualFurnace;
import thebetweenlands.tileentities.TileEntityBLFurnace;
import thebetweenlands.tileentities.TileEntityBLSpawner;
import thebetweenlands.tileentities.TileEntityBush;
import thebetweenlands.tileentities.TileEntityCompostBin;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.tileentities.TileEntityInfuser;
import thebetweenlands.tileentities.TileEntityLifeCrystal;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.tileentities.TileEntityLootPot2;
import thebetweenlands.tileentities.TileEntityLootPot3;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import thebetweenlands.tileentities.TileEntityPurifier;
import thebetweenlands.tileentities.TileEntityTarBeastSpawner;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.tileentities.TileEntityWisp;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy
        implements IGuiHandler
{
    public static final int GUI_DRUID_ALTAR = 1;
    public static final int GUI_WEEDWOOD_CRAFT = 2;
    public static final int GUI_WEEDWOOD_CHEST = 3;
    public static final int GUI_BL_FURNACE = 4;
    public static final int GUI_BL_DUAL_FURNACE = 5;
    public static final int GUI_ANIMATOR = 6;
    public static final int GUI_PURIFIER = 7;
    public static final int GUI_PESTLE_AND_MORTAR = 8;
    public static final int GUI_MANUAL = 9;

    public void registerTileEntities() {
        registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
        registerTileEntity(TileEntityWeedWoodChest.class, "weedWoodChest");
        registerTileEntity(TileEntityBLFurnace.class, "furnaceBL");
        registerTileEntity(TileEntityBLDualFurnace.class, "dualFurnaceBL");
        registerTileEntity(TileEntityBLCraftingTable.class, "crfTableBL");
        registerTileEntity(TileEntityWisp.class, "wisp");
        registerTileEntity(TileEntityBush.class, "weedWoodBush");
        registerTileEntity(TileEntityAnimator.class, "animator");
        registerTileEntity(TileEntityPurifier.class, "purifier");
        registerTileEntity(TileEntityCompostBin.class, "compostBin");
        registerTileEntity(TileEntityAlembic.class, "alembic");
        registerTileEntity(TileEntityInfuser.class, "infuser");
        registerTileEntity(TileEntityPestleAndMortar.class, "pestleAndMortar");
        registerTileEntity(TileEntityLifeCrystal.class, "lifeCrystalBlock");
        registerTileEntity(TileEntityLootPot1.class, "lootPot1");
        registerTileEntity(TileEntityLootPot2.class, "lootPot2");
        registerTileEntity(TileEntityLootPot3.class, "lootPot3");
        registerTileEntity(TileEntityBLSpawner.class, "blSpawner");
        registerTileEntity(TileEntityTarBeastSpawner.class, "tarBeastSpawner");
    }

    private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
        GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
    }

    public void preInit() {
        // unused serverside see ClientProxy for implementation
    }
    
    public void postInit() {
        // unused serverside see ClientProxy for implementation
    }

    /*public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, Object... data) {
        // unused serverside see ClientProxy for implementation
    }*/

    @Override
    //FIXME TODO use a switch block! PLEASE
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if( ID == GUI_DRUID_ALTAR ) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityDruidAltar ) {
                return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
            }
        }

		else if (ID == GUI_WEEDWOOD_CRAFT) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityBLCraftingTable ) {
                return new ContainerBLCraftingTable(player.inventory, (TileEntityBLCraftingTable) tileentity);
            }
        }

		else if (ID == GUI_WEEDWOOD_CHEST) {
        	IInventory inventory = BlockWeedWoodChest.getInventory(world, x, y, z);
        	return new ContainerWeedWoodChest(player.inventory, inventory);
        }

		else if (ID == GUI_BL_FURNACE) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLFurnace)
				return new ContainerBLFurnace(player.inventory, (TileEntityBLFurnace) tileentity);
		}

		else if (ID == GUI_BL_DUAL_FURNACE) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLDualFurnace)
				return new ContainerBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tileentity);
		}

		else if (ID == GUI_ANIMATOR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityAnimator)
				return new ContainerAnimator(player.inventory, (TileEntityAnimator) tileentity);
		}

		else if (ID == GUI_PURIFIER) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPurifier)
				return new ContainerPurifier(player.inventory, (TileEntityPurifier) tileentity);
		}

		else if (ID == GUI_PESTLE_AND_MORTAR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPestleAndMortar)
				return new ContainerPestleAndMortar(player.inventory, (TileEntityPestleAndMortar) tileentity);
		}

        return null;
    }

    @Override
    //FIXME TODO use a switch block! PLEASE
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if( ID == GUI_DRUID_ALTAR ) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityDruidAltar ) {
                return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
            }
        }

        else if (ID == GUI_WEEDWOOD_CRAFT) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if( tileentity instanceof TileEntityBLCraftingTable ) {
                return new GuiBLCrafting(player.inventory, (TileEntityBLCraftingTable) tileentity);
            }
        }

        else if (ID == GUI_WEEDWOOD_CHEST) {
        	IInventory inventory = BlockWeedWoodChest.getInventory(world, x, y, z);
        	return new GuiWeedWoodChest(player.inventory, inventory);
        }

		else if (ID == GUI_BL_FURNACE) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLFurnace)
				return new GuiBLFurnace(player.inventory, (TileEntityBLFurnace) tileentity);
		}

		else if (ID == GUI_BL_DUAL_FURNACE) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLDualFurnace)
				return new GuiBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tileentity);
		}

		else if (ID == GUI_ANIMATOR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityAnimator)
				return new GuiAnimator(player, (TileEntityAnimator) tileentity);
		}

		else if (ID == GUI_PURIFIER) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPurifier)
				return new GuiPurifier(player.inventory, (TileEntityPurifier) tileentity);
		}

		else if (ID == GUI_PESTLE_AND_MORTAR) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPestleAndMortar)
				return new GuiPestleAndMortar(player.inventory, (TileEntityPestleAndMortar) tileentity);
		}
        else if (ID == GUI_MANUAL) {
            return new GuiManualBase();
        }

        return null;
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }
    
	public World getClientWorld() {
		return null;
	}

    public void corruptPlayerSkin(EntityPlayer entityPlayer, int level) { }

    public void uncorruptPlayerSkin(EntityPlayer entityPlayer) { }

    public void updateWispParticles(TileEntityWisp te) { }

    public void spawnThem() { }
    
    public void playPortalSounds(Entity entity, int timer) { }

    public DebugHandlerCommon getDebugHandler() {
    	return DebugHandlerCommon.INSTANCE;
    }
}
