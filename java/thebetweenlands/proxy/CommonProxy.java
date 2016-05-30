package thebetweenlands.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.container.BlockWeedWoodChest;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.event.debugging.DebugHandlerCommon;
import thebetweenlands.event.entity.RecruitmentRingHandler;
import thebetweenlands.event.input.RingInputHandler;
import thebetweenlands.event.item.ItemEquipmentHandler;
import thebetweenlands.event.player.RottenFoodHandler;
import thebetweenlands.event.world.AspectLoadHandler;
import thebetweenlands.event.world.PopulationHandler;
import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.inventory.container.ContainerBLCraftingTable;
import thebetweenlands.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.inventory.container.ContainerBLFurnace;
import thebetweenlands.inventory.container.ContainerDruidAltar;
import thebetweenlands.inventory.container.ContainerLurkerSkinPouch;
import thebetweenlands.inventory.container.ContainerPestleAndMortar;
import thebetweenlands.inventory.container.ContainerPurifier;
import thebetweenlands.inventory.container.ContainerWeedWoodChest;
import thebetweenlands.items.equipment.ItemAmulet;
import thebetweenlands.items.equipment.ItemBasicInventory;
import thebetweenlands.items.equipment.ItemLurkerSkinPouch;
import thebetweenlands.items.equipment.ItemRingOfFlight;
import thebetweenlands.tileentities.TileEntityAlembic;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.tileentities.TileEntityAspectVial;
import thebetweenlands.tileentities.TileEntityAspectrusCrop;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;
import thebetweenlands.tileentities.TileEntityBLDualFurnace;
import thebetweenlands.tileentities.TileEntityBLFurnace;
import thebetweenlands.tileentities.TileEntityBLSign;
import thebetweenlands.tileentities.TileEntityCompostBin;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.tileentities.TileEntityGeckoCage;
import thebetweenlands.tileentities.TileEntityInfuser;
import thebetweenlands.tileentities.TileEntityItemCage;
import thebetweenlands.tileentities.TileEntityItemShelf;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.tileentities.TileEntityLootPot2;
import thebetweenlands.tileentities.TileEntityLootPot3;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import thebetweenlands.tileentities.TileEntityPossessedBlock;
import thebetweenlands.tileentities.TileEntityPurifier;
import thebetweenlands.tileentities.TileEntityRepeller;
import thebetweenlands.tileentities.TileEntitySpikeTrap;
import thebetweenlands.tileentities.TileEntityTarBeastSpawner;
import thebetweenlands.tileentities.TileEntityTarLootPot1;
import thebetweenlands.tileentities.TileEntityTarLootPot2;
import thebetweenlands.tileentities.TileEntityTarLootPot3;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.tileentities.spawner.TileEntityBLSpawner;

public class CommonProxy implements IGuiHandler {
	public static final int GUI_DRUID_ALTAR = 1;

	public static final int GUI_WEEDWOOD_CRAFT = 2;

	public static final int GUI_WEEDWOOD_CHEST = 3;

	public static final int GUI_BL_FURNACE = 4;

	public static final int GUI_BL_DUAL_FURNACE = 5;

	public static final int GUI_ANIMATOR = 6;

	public static final int GUI_PURIFIER = 7;

	public static final int GUI_PESTLE_AND_MORTAR = 8;

	public static final int GUI_MANUAL = 9;

	public static final int GUI_HL = 10;

	public static final int GUI_LORE = 11;

	public static final int GUI_LURKER_POUCH = 12;

	public static final int GUI_LURKER_POUCH_NAMING = 13;

	public static final int GUI_LURKER_POUCH_KEYBIND = 14;

	public void registerTileEntities() {
		registerTileEntity(TileEntityDruidAltar.class, "druidAltar");
		registerTileEntity(TileEntityWeedWoodChest.class, "weedWoodChest");
		registerTileEntity(TileEntityBLFurnace.class, "furnaceBL");
		registerTileEntity(TileEntityBLDualFurnace.class, "dualFurnaceBL");
		registerTileEntity(TileEntityBLCraftingTable.class, "crfTableBL");
		registerTileEntity(TileEntityWisp.class, "wisp");
		registerTileEntity(TileEntityAnimator.class, "animator");
		registerTileEntity(TileEntityPurifier.class, "purifier");
		registerTileEntity(TileEntityCompostBin.class, "compostBin");
		registerTileEntity(TileEntityAlembic.class, "alembic");
		registerTileEntity(TileEntityInfuser.class, "infuser");
		registerTileEntity(TileEntityPestleAndMortar.class, "pestleAndMortar");
		registerTileEntity(TileEntityLootPot1.class, "lootPot1");
		registerTileEntity(TileEntityLootPot2.class, "lootPot2");
		registerTileEntity(TileEntityLootPot3.class, "lootPot3");
		registerTileEntity(TileEntityBLSpawner.class, "blSpawner");
		registerTileEntity(TileEntityTarBeastSpawner.class, "tarBeastSpawner");
		registerTileEntity(TileEntityTarLootPot1.class, "tarLootPot1");
		registerTileEntity(TileEntityTarLootPot2.class, "tarLootPot2");
		registerTileEntity(TileEntityTarLootPot3.class, "tarLootPot3");
		registerTileEntity(TileEntityItemShelf.class, "itemShelf");
		registerTileEntity(TileEntityGeckoCage.class, "geckoCage");
		registerTileEntity(TileEntityAspectrusCrop.class, "aspectCrop");
		registerTileEntity(TileEntityBLSign.class, "signBL");
		registerTileEntity(TileEntityRepeller.class, "repeller");
		registerTileEntity(TileEntityPossessedBlock.class, "possessedBlock");
		registerTileEntity(TileEntitySpikeTrap.class, "spikeTrap");
		registerTileEntity(TileEntityItemCage.class, "itemCage");
		registerTileEntity(TileEntityAspectVial.class, "aspectVialBlock");
	}

	private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}

	public void init() {
		// Register packet handlers
		try {
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(ItemAmulet.class, Side.SERVER);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(ItemEquipmentHandler.class, Side.SERVER);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(RecruitmentRingHandler.class, Side.SERVER);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(ItemRingOfFlight.class, Side.SERVER);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(RingInputHandler.class, Side.SERVER);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MinecraftForge.EVENT_BUS.register(AspectLoadHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(PopulationHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RottenFoodHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(RottenFoodHandler.INSTANCE);
	}

	public void postInit() {
		// unused serverside see ClientProxy for implementation
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_DRUID_ALTAR: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityDruidAltar) {
				return new ContainerDruidAltar(player.inventory, (TileEntityDruidAltar) tileentity);
			}
			break;
		}
		case GUI_WEEDWOOD_CRAFT: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLCraftingTable) {
				return new ContainerBLCraftingTable(player.inventory, (TileEntityBLCraftingTable) tileentity);
			}
			break;
		}
		case GUI_WEEDWOOD_CHEST:
			IInventory inventory = BlockWeedWoodChest.getInventory(world, x, y, z);
			return new ContainerWeedWoodChest(player.inventory, inventory);
		case GUI_BL_FURNACE: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLFurnace) {
				return new ContainerBLFurnace(player.inventory, (TileEntityBLFurnace) tileentity);
			}
			break;
		}
		case GUI_BL_DUAL_FURNACE: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBLDualFurnace) {
				return new ContainerBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tileentity);
			}
			break;
		}
		case GUI_ANIMATOR: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityAnimator) {
				return new ContainerAnimator(player.inventory, (TileEntityAnimator) tileentity);
			}
			break;
		}
		case GUI_PURIFIER: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPurifier) {
				return new ContainerPurifier(player.inventory, (TileEntityPurifier) tileentity);
			}
			break;
		}
		case GUI_PESTLE_AND_MORTAR: {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityPestleAndMortar) {
				return new ContainerPestleAndMortar(player.inventory, (TileEntityPestleAndMortar) tileentity);
			}
			break;
		}
		case GUI_LURKER_POUCH:
			return new ContainerLurkerSkinPouch(player, player.inventory, new ItemBasicInventory(player.getHeldItem(), 9 + (x * 9), StatCollector.translateToLocal("container.lurkerSkinPouch")));
		case GUI_LURKER_POUCH_KEYBIND:
			ItemStack pouch = ItemLurkerSkinPouch.getFirstPouch(player);
			if(pouch != null)
				return new ContainerLurkerSkinPouch(player, player.inventory, new ItemBasicInventory(pouch, 9 + (x * 9), StatCollector.translateToLocal("container.lurkerSkinPouch")));
			break;
		case GUI_LURKER_POUCH_NAMING:
			return new ContainerLurkerSkinPouch(player, player.inventory, null);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public void updateWispParticles(TileEntityWisp te) {
	}

	public void spawnThem() {
	}

	public void playPortalSounds(Entity entity, int timer) {
	}

	public void onPlayerEnterWeedwoodRowboat() {
	}

	public void updateRiderYawInWeedwoodRowboat(EntityWeedwoodRowboat rowboat, EntityLivingBase rider) {
	}

	public DebugHandlerCommon getDebugHandler() {
		return DebugHandlerCommon.INSTANCE;
	}

	public void registerItemRenderer(Item item) {
	}

	@SideOnly(Side.CLIENT)
	public FontRenderer getCustomFontRenderer() {
		return null;
	}

	public ModelBiped getExplorersHatModel() {
		return null;
	}
}
