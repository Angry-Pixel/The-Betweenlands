package thebetweenlands.common.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerBLFurnace;
import thebetweenlands.common.inventory.container.ContainerDruidAltar;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;

public class CommonProxy implements IGuiHandler {
    public static final int GUI_DRUID_ALTAR = 1;
    public static final int GUI_WEEDWOOD_CRAFT = 2;
    public static final int GUI_WEEDWOOD_CHEST = 3;
    public static final int GUI_BL_FURNACE = 4;
    public static final int GUI_BL_DUAL_FURNACE = 5;
    public static final int GUI_ANIMATOR = 6;
    public static final int GUI_PURIFIER = 7;
    public static final int GUI_PESTLE_AND_MORTAR = 8;
    public static final int GUI_HL = 9;
    public static final int GUI_LORE = 10;
    public static final int GUI_LURKER_POUCH = 11;
    public static final int GUI_LURKER_POUCH_NAMING = 12;
    public static final int GUI_LURKER_POUCH_KEYBIND = 13;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (id) {
            case GUI_DRUID_ALTAR: {
                if (tile instanceof TileEntityDruidAltar)
                    return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
                break;
            }
            case GUI_PURIFIER: {
                if (tile instanceof TileEntityPurifier) {
                    return new ContainerPurifier(player.inventory, (TileEntityPurifier) tile);
                }
                break;
            }
            case GUI_WEEDWOOD_CRAFT: {
                if (tile instanceof TileEntityWeedwoodWorkbench) {
                    return new ContainerWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
                }
                break;
            }
            case GUI_BL_FURNACE: {
                if (tile instanceof TileEntityBLFurnace) {
                    return new ContainerBLFurnace(player.inventory, (TileEntityBLFurnace) tile);
                }
                break;
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void preInit() {

    }

    public void init() {

    }

    public void postInit() {

    }

    public void registerEventHandlers() {

    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }

    public void registerDefaultBlockItemRenderer(Block block) {

    }

    public void registerDefaultItemRenderer(Item item) {

    }

    public void changeFileNames() {

    }

    public void registerItemAndBlockRenderers() {

    }

    public void setCustomStateMap(Block block, StateMap stateMap) {

    }
    public void updateWispParticles(TileEntityWisp te) {
    }

    @SideOnly(Side.CLIENT)
    public FontRenderer getCustomFontRenderer() {
        return null;
    }
}
