package thebetweenlands.common.proxy;

import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thebetweenlands.client.render.sky.RiftVariant;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerAnimator;
import thebetweenlands.common.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.common.inventory.container.ContainerBLFurnace;
import thebetweenlands.common.inventory.container.ContainerBarrel;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.inventory.container.ContainerDraetonBurner;
import thebetweenlands.common.inventory.container.ContainerDraetonFurnace;
import thebetweenlands.common.inventory.container.ContainerDraetonPouch;
import thebetweenlands.common.inventory.container.ContainerDraetonUpgrades;
import thebetweenlands.common.inventory.container.ContainerDraetonWorkbench;
import thebetweenlands.common.inventory.container.ContainerDruidAltar;
import thebetweenlands.common.inventory.container.ContainerItemNaming;
import thebetweenlands.common.inventory.container.ContainerMortar;
import thebetweenlands.common.inventory.container.ContainerPouch;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityBarrel;
import thebetweenlands.common.tile.TileEntityCenser;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

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
	public static final int GUI_ITEM_RENAMING = 13;
	public static final int GUI_LURKER_POUCH_KEYBIND = 14;
	public static final int GUI_CENSER = 15;
	public static final int GUI_BARREL = 16;
	public static final int GUI_DRAETON_POUCH = 17;
	public static final int GUI_DRAETON_BURNER = 18;
	public static final int GUI_DRAETON_CRAFTING = 19;
	public static final int GUI_DRAETON_FURNACE = 20;
	public static final int GUI_DRAETON_UPGRADES = 21;
	public static final int GUI_RUNE_CHAIN_ALTAR = 22;
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		Entity entity = null;
		
		switch (id) {
		case GUI_DRUID_ALTAR:
			if (tile instanceof TileEntityDruidAltar) {
				return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
			}
			break;

		case GUI_PURIFIER:
			if (tile instanceof TileEntityPurifier) {
				return new ContainerPurifier(player.inventory, (TileEntityPurifier) tile);
			}
			break;

		case GUI_WEEDWOOD_CRAFT:
			if (tile instanceof TileEntityWeedwoodWorkbench) {
				return new ContainerWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
			}
			break;

		case GUI_BL_FURNACE:
			if (tile instanceof TileEntityBLFurnace) {
				return new ContainerBLFurnace(player.inventory, (TileEntityBLFurnace) tile);
			}
			break;

		case GUI_BL_DUAL_FURNACE:
			if (tile instanceof TileEntityBLDualFurnace) {
				return new ContainerBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tile);
			}
			break;

		case GUI_PESTLE_AND_MORTAR:
			if (tile instanceof TileEntityMortar) {
				return new ContainerMortar(player.inventory, (TileEntityMortar) tile);
			}
			break;

		case GUI_ANIMATOR:
			if (tile instanceof TileEntityAnimator) {
				return new ContainerAnimator(player.inventory, (TileEntityAnimator) tile);
			}
			break;

		case GUI_LURKER_POUCH: {
			ItemStack item = player.getHeldItemMainhand();
			if(item.isEmpty() || !(item.getItem() instanceof ItemLurkerSkinPouch)) {
				item = player.getHeldItemOffhand();
			}
			if(!item.isEmpty() && item.getItem() instanceof ItemLurkerSkinPouch) {
				String name = item.hasDisplayName() ? item.getDisplayName() : "container.bl.lurker_skin_pouch";
				return new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (item.getItemDamage() * 9), name));
			}
			break;
		}

		case GUI_LURKER_POUCH_KEYBIND: {
			ItemStack item = ItemLurkerSkinPouch.getFirstPouch(player);
			if(item != null) {
				String name = item.hasDisplayName() ? item.getDisplayName() : "container.bl.lurker_skin_pouch";
				return new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (item.getItemDamage() * 9), name));
			}
		}

		case GUI_ITEM_RENAMING:
			return new ContainerItemNaming();

		case GUI_CENSER:
			if (tile instanceof TileEntityCenser) {
				return new ContainerCenser(player.inventory, (TileEntityCenser) tile);
			}
			break;
			
		case GUI_BARREL:
			if (tile instanceof TileEntityBarrel) {
				return new ContainerBarrel(player.inventory, (TileEntityBarrel) tile);
			}
			break;

		case GUI_RUNE_CHAIN_ALTAR:
			if (tile instanceof TileEntityRuneChainAltar) {
				return new ContainerRuneChainAltar(player, (TileEntityRuneChainAltar) tile);
			}
			break;
			
		case GUI_DRAETON_POUCH:
			entity = world.getEntityByID(x);
			if (entity instanceof EntityDraeton) {
				IInventory upgrades = ((EntityDraeton) entity).getUpgradesInventory();
				if(y >= 0 && y < 4) {
					ItemStack stack = upgrades.getStackInSlot(y);
					if(!stack.isEmpty() && ((EntityDraeton) entity).isStorageUpgrade(stack)) {
						String name = stack.hasDisplayName() ? stack.getDisplayName(): "container.bl.draeton_storage";
						return new ContainerDraetonPouch(player, player.inventory, new InventoryItem(stack, 9 + (stack.getItemDamage() * 9), name), (EntityDraeton)entity, y);
					}
				}
			}
			break;
			
		case GUI_DRAETON_CRAFTING:
			entity = world.getEntityByID(x);
			if (entity instanceof EntityDraeton) {
				IInventory upgrades = ((EntityDraeton) entity).getUpgradesInventory();
				if(y >= 0 && y < 4) {
					ItemStack stack = upgrades.getStackInSlot(y);
					if(!stack.isEmpty() && ((EntityDraeton) entity).isCraftingUpgrade(stack)) {
						return new ContainerDraetonWorkbench(player.inventory, (EntityDraeton) entity, y);
					}
				}
			}
			break;
			
		case GUI_DRAETON_FURNACE:
			entity = world.getEntityByID(x);
			if (entity instanceof EntityDraeton) {
				IInventory upgrades = ((EntityDraeton) entity).getUpgradesInventory();
				if(y >= 0 && y < 4) {
					ItemStack stack = upgrades.getStackInSlot(y);
					if(!stack.isEmpty() && ((EntityDraeton) entity).isFurnaceUpgrade(stack)) {
						return new ContainerDraetonFurnace(player, player.inventory, ((EntityDraeton) entity).getFurnace(y), (EntityDraeton)entity, y);
					}
				}
			}
			break;
			
		case GUI_DRAETON_BURNER:
			entity = world.getEntityByID(x);
			if (entity instanceof EntityDraeton)
				return new ContainerDraetonBurner(player.inventory, ((EntityDraeton)entity).getBurnerInventory(), (EntityDraeton)entity);
			break;
			
		case GUI_DRAETON_UPGRADES:
			entity = world.getEntityByID(x);
			if (entity instanceof EntityDraeton)
				return new ContainerDraetonUpgrades(player.inventory, (EntityDraeton)entity);
			break;
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

	public void  registerEventHandlersPreInit(){

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

	public Map<Integer, ResourceLocation> getItemModelMap(Item item) {
		return Collections.emptyMap();
	}
	
	public void registerItemAndBlockRenderers() {

	}

	public void setCustomStateMap(Block block, StateMap stateMap) {

	}

	public FontRenderer getCustomFontRenderer() {
		return null;
	}

    public void onPilotEnterWeedwoodRowboat(Entity pilot) {

    }

    public void onPilotExitWeedwoodRowboat(EntityWeedwoodRowboat rowboat, Entity pilot) {

    }
    
    public boolean isSingleplayer() {
    	return false;
    }
    
    @Nullable
    public Proxy getNetProxy() {
    	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    	if(server != null) {
    		return server.getServerProxy();
    	}
    	return null;
    }
    
    public List<RiftVariant> getRiftVariants() {
    	return Collections.emptyList();
    }
}
