package thebetweenlands.client.proxy;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.gui.menu.GuiBLMainMenu;
import thebetweenlands.client.gui.menu.GuiDownloadTerrainBetweenlands;
import thebetweenlands.client.gui.GuiLorePage;
import thebetweenlands.client.gui.GuiPouchNaming;
import thebetweenlands.client.gui.inventory.GuiAnimator;
import thebetweenlands.client.gui.inventory.GuiBLDualFurnace;
import thebetweenlands.client.gui.inventory.GuiBLFurnace;
import thebetweenlands.client.gui.inventory.GuiDruidAltar;
import thebetweenlands.client.gui.inventory.GuiMortar;
import thebetweenlands.client.gui.inventory.GuiPouch;
import thebetweenlands.client.gui.inventory.GuiPurifier;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.client.handler.AmbienceSoundPlayHandler;
import thebetweenlands.client.handler.ArmSwingSpeedHandler;
import thebetweenlands.client.handler.BrightnessHandler;
import thebetweenlands.client.handler.CameraPositionHandler;
import thebetweenlands.client.handler.DebugHandlerClient;
import thebetweenlands.client.handler.DecayRenderHandler;
import thebetweenlands.client.handler.ElixirClientHandler;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.handler.InputHandler;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.client.handler.OverlayHandler;
import thebetweenlands.client.handler.ScreenRenderHandler;
import thebetweenlands.client.handler.ShaderHandler;
import thebetweenlands.client.handler.TextureStitchHandler;
import thebetweenlands.client.handler.TextureStitchHandler.TextureStitcher;
import thebetweenlands.client.handler.ThemHandler;
import thebetweenlands.client.handler.WeedwoodRowboatHandler;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.handler.equipment.RadialMenuHandler;
import thebetweenlands.client.render.entity.*;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.entity.ParticleWisp;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.client.render.tile.RenderAlembic;
import thebetweenlands.client.render.tile.RenderAnimator;
import thebetweenlands.client.render.tile.RenderAspectVial;
import thebetweenlands.client.render.tile.RenderAspectrusCrop;
import thebetweenlands.client.render.tile.RenderChestBetweenlands;
import thebetweenlands.client.render.tile.RenderCompostBin;
import thebetweenlands.client.render.tile.RenderDruidAltar;
import thebetweenlands.client.render.tile.RenderGeckoCage;
import thebetweenlands.client.render.tile.RenderInfuser;
import thebetweenlands.client.render.tile.RenderItemCage;
import thebetweenlands.client.render.tile.RenderItemShelf;
import thebetweenlands.client.render.tile.RenderLootPot;
import thebetweenlands.client.render.tile.RenderMudFlowerPot;
import thebetweenlands.client.render.tile.RenderPestleAndMortar;
import thebetweenlands.client.render.tile.RenderPossessedBlock;
import thebetweenlands.client.render.tile.RenderPurifier;
import thebetweenlands.client.render.tile.RenderRepeller;
import thebetweenlands.client.render.tile.RenderSpawnerBetweenlands;
import thebetweenlands.client.render.tile.RenderSpikeTrap;
import thebetweenlands.client.render.tile.RenderTarLootPot1;
import thebetweenlands.client.render.tile.RenderTarLootPot2;
import thebetweenlands.client.render.tile.RenderTarLootPot3;
import thebetweenlands.client.render.tile.RenderWeedwoodSign;
import thebetweenlands.client.render.tile.RenderWeedwoodWorkbench;
import thebetweenlands.client.render.tile.RenderWisp;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.HLEntryRegistry;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerPouch;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.item.shields.ItemSwatShield;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityTarLootPot1;
import thebetweenlands.common.tile.TileEntityTarLootPot2;
import thebetweenlands.common.tile.TileEntityTarLootPot3;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.common.world.event.EventSpoopy;
import thebetweenlands.common.world.event.EventWinter;
import thebetweenlands.util.GLUProjection;

public class ClientProxy extends CommonProxy {
	public static Render<EntityDragonFly> dragonFlyRenderer;

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		switch (id) {
		case GUI_DRUID_ALTAR:
			if (tile instanceof TileEntityDruidAltar) {
				return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
			}
			break;

		case GUI_PURIFIER:
			if (tile instanceof TileEntityPurifier) {
				return new GuiPurifier(player.inventory, (TileEntityPurifier) tile);
			}
			break;

		case GUI_WEEDWOOD_CRAFT:
			if (tile instanceof TileEntityWeedwoodWorkbench) {
				return new GuiWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
			}
			break;

		case GUI_HL:
			EnumHand hand = x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
			ItemStack manual = player.getHeldItem(hand);
			if(!manual.isEmpty() && manual.getItem() == ItemRegistry.MANUAL_HL) {
				return new GuiManualHerblore(player, manual, hand);
			}
			break;

		case GUI_BL_FURNACE:
			if (tile instanceof TileEntityBLFurnace) {
				return new GuiBLFurnace(player.inventory, (TileEntityBLFurnace) tile);
			}
			break;

		case GUI_BL_DUAL_FURNACE:
			if (tile instanceof TileEntityBLDualFurnace) {
				return new GuiBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tile);
			}
			break;

		case GUI_PESTLE_AND_MORTAR:
			if (tile instanceof TileEntityMortar) {
				return new GuiMortar(player.inventory, (TileEntityMortar) tile);
			}
			break;

		case GUI_ANIMATOR:
			if (tile instanceof TileEntityAnimator) {
				return new GuiAnimator(player, (TileEntityAnimator) tile);
			}
			break;

		case GUI_LURKER_POUCH: {
			ItemStack item = player.getHeldItemMainhand();
			if(item.isEmpty() || !(item.getItem() instanceof ItemLurkerSkinPouch)) {
				item = player.getHeldItemOffhand();
			}
			if(!item.isEmpty() && item.getItem() instanceof ItemLurkerSkinPouch) {
				String name = item.hasDisplayName() ? item.getDisplayName(): I18n.format("container.lurker_skin_pouch");
				return new GuiPouch(new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (x * 9), name)));
			}
			break;
		}

		case GUI_LURKER_POUCH_KEYBIND: {
			ItemStack item = ItemLurkerSkinPouch.getFirstPouch(player);
			if(!item.isEmpty()) {
				String name = item.hasDisplayName() ? item.getDisplayName(): I18n.format("container.lurker_skin_pouch");
				return new GuiPouch(new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (x * 9), name)));
			}
		}

		case GUI_LURKER_POUCH_NAMING:
			if(!player.getHeldItemMainhand().isEmpty()) {
				return new GuiPouchNaming(player, x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
			}
			break;

		case GUI_LORE:
			return new GuiLorePage(player.getHeldItem(x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
		}

		return null;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public void registerItemAndBlockRenderers() {
		CustomModelManager.INSTANCE.registerLoader();
	}



	@Override
	public void setCustomStateMap(Block block, StateMap stateMap) {
		ModelLoader.setCustomStateMapper(block, stateMap);
	}

	/*
        @Override
        public void registerDefaultBlockItemRenderer(Block block) {
            if (block instanceof BlockRegistry.ISubBlocksBlock) {
                List<String> models = ((BlockRegistry.ISubBlocksBlock) block).getModels();
                if (block instanceof BlockDruidStone) {
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(0), "inventory"));
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 4, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(1), "inventory"));
                } else
                    for (int i = 0; i < models.size(); i++) {
                        if (ConfigHandler.DEBUG.debug && createJSONFile)
                            JsonRenderGenerator.createJSONForBlock(block, models.get(i));
                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
                    }
            } else {
                String name = block.getRegistryName().toString().replace("thebetweenlands:", "");
                if (ConfigHandler.DEBUG.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForBlock(block, name);
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name, "inventory"));
            }
        }
	 */
	@Override
	public void registerDefaultItemRenderer(Item item) {
		Map<Integer, ResourceLocation> map = this.getItemModelMap(item);
		for(Entry<Integer, ResourceLocation> entry : map.entrySet()) {
			ModelLoader.setCustomModelResourceLocation(item, entry.getKey(), (ModelResourceLocation) entry.getValue());
		}
	}
	
	@Override
	public Map<Integer, ResourceLocation> getItemModelMap(Item item) {
		Map<Integer, ResourceLocation> map = new HashMap<>();
		if (item instanceof ItemRegistry.IMultipleItemModelDefinition) {
			for (Entry<Integer, ResourceLocation> model : ((ItemRegistry.IMultipleItemModelDefinition) item).getModels().entrySet()) {
				map.put(model.getKey(), new ModelResourceLocation(model.getValue(), "inventory"));
			}
		} else if (item instanceof ItemRegistry.IBlockStateItemModelDefinition) {
			for (Entry<Integer, String> variant : ((ItemRegistry.IBlockStateItemModelDefinition) item).getVariants().entrySet()) {
				map.put(variant.getKey(), new ModelResourceLocation(item.getRegistryName().toString(), variant.getValue()));
			}
		} else {
			map.put(0, new ModelResourceLocation(item.getRegistryName().toString(), "inventory"));
		}
		return map;
	}

	@Override
	public void preInit() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, RenderAngler::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindCaveFish.class, RenderBlindCaveFish::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnail.class, RenderMireSnail::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnailEgg.class, RenderMireSnailEgg::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBloodSnail.class, RenderBloodSnail::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySnailPoisonJet.class, RenderSnailPoisonJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySwampHag.class, RenderSwampHag::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityChiromaw.class, RenderChiromaw::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonFly.class, RenderDragonFly::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLurker.class, RenderLurker::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, RenderFrog::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantToad.class, RenderGiantToad::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySporeling.class, RenderSporeling::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTermite.class, RenderTermite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLeech.class, RenderLeech::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySwordEnergy.class, RenderSwordEnergy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShockwaveBlock.class, RenderShockwaveBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGecko.class, RenderGecko::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWight.class, RenderWight::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShockwaveSwordItem.class, manager -> new RenderShockwaveSwordItem(manager, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFirefly.class, RenderFirefly::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGasCloud.class, RenderGasCloud::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySludge.class, RenderSludge::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBLArrow.class, RenderBLArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, RenderDarkDruid::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVolatileSoul.class, RenderVolatileSoul::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTarBeast.class, RenderTarBeast::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySiltCrab.class, RenderSiltCrab::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPyrad.class, RenderPyrad::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPyradFlame.class, RenderPyradFlame::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPeatMummy.class, RenderPeatMummy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTarminion.class, RenderTarminion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownTarminion.class, RenderThrownTarminion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRopeNode.class, RenderRopeNode::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMummyArm.class, RenderMummyArm::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAngryPebble.class, manager -> new RenderAngryPebble(manager, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBoss.class, RenderFortressBoss::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBossSpawner.class, RenderFortressBossSpawner::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBossBlockade.class, RenderFortressBossBlockade::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBossProjectile.class, RenderFortressBossProjectile::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBossTurret.class, RenderFortressBossTurret::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFortressBossTeleporter.class, RenderFortressBossTeleporter::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWeedwoodRowboat.class, RenderWeedwoodRowboat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityElixir.class, RenderElixir::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDreadfulMummy.class, RenderDreadfulMummy::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySludgeBall.class, RenderSludgeBall::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkLight.class, RenderDarkLight::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySmollSludge.class, RenderSmollSludge::new);
 
		IReloadableResourceManager resourceManager = ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager());
		resourceManager.registerReloadListener(ShaderHelper.INSTANCE);
		resourceManager.registerReloadListener(new FoodSickness.ResourceReloadListener());

		//Register particle stitchers
		BLParticles[] particles = BLParticles.values();
		for(BLParticles particle : particles) {
			ParticleTextureStitcher<?> stitcher = particle.getFactory().getStitcher();
			if(stitcher != null) {
				TextureStitchHandler.INSTANCE.registerTextureStitcher(new TextureStitcher((splitter) -> {
					stitcher.setFrames(splitter.getFrames());
				}, stitcher.getTextures()).setSplitFrames(stitcher.shouldSplitAnimations()));
			}
		}

		registerEventHandlersPreInit();
	}

	@Override
	public void init() {
		KeyBindRegistry.init();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void postInit() {
	    RenderManager mgr = Minecraft.getMinecraft().getRenderManager();
		dragonFlyRenderer = mgr.getEntityClassRenderObject(EntityDragonFly.class);
        MinecraftForge.EVENT_BUS.register(mgr.getEntityClassRenderObject(EntityWeedwoodRowboat.class));

		//Tile entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new RenderPurifier());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new RenderDruidAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodWorkbench.class, new RenderWeedwoodWorkbench());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot.class, new RenderLootPot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawnerBetweenlands.class, new RenderSpawnerBetweenlands());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChestBetweenlands.class, new RenderChestBetweenlands());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpikeTrap.class, new RenderSpikeTrap());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPossessedBlock.class, new RenderPossessedBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemCage.class, new RenderItemCage());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodSign.class, new RenderWeedwoodSign());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMudFlowerPot.class, new RenderMudFlowerPot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGeckoCage.class, new RenderGeckoCage());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWisp.class, new RenderWisp());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfuser.class, new RenderInfuser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMortar.class, new RenderPestleAndMortar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAnimator.class, new RenderAnimator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlembic.class, new RenderAlembic());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCompostBin.class, new RenderCompostBin());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new RenderItemShelf());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot1.class, new RenderTarLootPot1());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot2.class, new RenderTarLootPot2());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot3.class, new RenderTarLootPot3());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAspectVial.class, new RenderAspectVial());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAspectrusCrop.class, new RenderAspectrusCrop());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRepeller.class, new RenderRepeller());
		
		//item models
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_ALTAR), 0, TileEntityDruidAltar.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.COMPOST_BIN), 0, TileEntityCompostBin.class);
		//ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_SPAWNER), 0, TileEntityDruidSpawner.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.PURIFIER), 0, TileEntityPurifier.class);
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_1.getMetadata(facing), TileEntityLootPot.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_2.getMetadata(facing), TileEntityLootPot.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_3.getMetadata(facing), TileEntityLootPot.class);
		}
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.MOB_SPAWNER), 0, TileEntityMobSpawnerBetweenlands.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.SPIKE_TRAP), 0, TileEntitySpikeTrap.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.POSSESSED_BLOCK), 0, TileEntityPossessedBlock.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ITEM_CAGE), 0, TileEntityItemCage.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.GECKO_CAGE), 0, TileEntityGeckoCage.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.INFUSER), 0, TileEntityInfuser.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.MORTAR), 0, TileEntityMortar.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ANIMATOR), 0, TileEntityAnimator.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ALEMBIC), 0, TileEntityAlembic.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ITEM_SHELF), 0, TileEntityItemShelf.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.REPELLER), 0, TileEntityRepeller.class);
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.TAR_LOOT_POT), EnumLootPot.POT_1.getMetadata(facing), TileEntityTarLootPot1.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.TAR_LOOT_POT), EnumLootPot.POT_2.getMetadata(facing), TileEntityTarLootPot2.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.TAR_LOOT_POT), EnumLootPot.POT_3.getMetadata(facing), TileEntityTarLootPot3.class);
		}
		
		//Block colors
		for (Block block : BlockRegistry.BLOCKS) {
			if (block instanceof ITintedBlock) {
				final ITintedBlock tintedBlock = (ITintedBlock) block;
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> tintedBlock.getColorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex), block);
				Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(tintedBlock::getColorMultiplier, block);
			}
		}

		//Item colors
		for (Item item : ItemRegistry.ITEMS) {
			if (item instanceof ITintedItem) {
				final ITintedItem tintedItem = (ITintedItem) item;
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler(tintedItem::getColorMultiplier, item);
			}
		}

		pixelLove = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("thebetweenlands:textures/gui/manual/font_atlas.png"), Minecraft.getMinecraft().renderEngine, false);
		if (Minecraft.getMinecraft().gameSettings.language != null) {
			pixelLove.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(pixelLove);
		HLEntryRegistry.init();

		WeedwoodRowboatHandler.INSTANCE.init();
		//Turn dirt background in menus into temple bricks
		//Disabled for now as it was test, could be used if it's suitable
		/*
		if (ConfigHandler.blMainMenu) {
			Field background = ReflectionHelper.findField(Gui.class, "OPTIONS_BACKGROUND");
			try {
				setFinalStatic(background, new ResourceLocation(ModInfo.ID, "textures/blocks/temple_bricks.png"));
			} catch (IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	private static void setFinalStatic(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException {
		field.setAccessible(true);
		Field modifiers = field.getClass().getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newValue);*/
	}

	@Override
	public void registerEventHandlersPreInit(){
		MinecraftForge.EVENT_BUS.register(TextureStitchHandler.INSTANCE);
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.class);
		MinecraftForge.EVENT_BUS.register(AmbienceSoundPlayHandler.class);
		MinecraftForge.EVENT_BUS.register(GLUProjection.getInstance());
		MinecraftForge.EVENT_BUS.register(WorldRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(ScreenRenderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(DecayRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(CameraPositionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(MusicHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ThemHandler.class);
		MinecraftForge.EVENT_BUS.register(RadialMenuHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ItemAmulet.class);
		MinecraftForge.EVENT_BUS.register(InputHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemLurkerSkinPouch.class);
		MinecraftForge.EVENT_BUS.register(BrightnessHandler.class);
		MinecraftForge.EVENT_BUS.register(DebugHandlerClient.class);
		MinecraftForge.EVENT_BUS.register(ItemTooltipHandler.class);
		MinecraftForge.EVENT_BUS.register(GuiBLMainMenu.class);
        MinecraftForge.EVENT_BUS.register(WeedwoodRowboatHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(OverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(ElixirClientHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GuiDownloadTerrainBetweenlands.class);
        MinecraftForge.EVENT_BUS.register(ItemBLBow.class);
        MinecraftForge.EVENT_BUS.register(ItemSwatShield.class);
        MinecraftForge.EVENT_BUS.register(EventWinter.class);
        MinecraftForge.EVENT_BUS.register(EventSpoopy.class);
        MinecraftForge.EVENT_BUS.register(ArmSwingSpeedHandler.class);
        MinecraftForge.EVENT_BUS.register(BLSkyRenderer.class);
	}

	@Override
	public void updateWispParticles(TileEntityWisp te) {
		Iterator<Object> it = te.particleList.iterator();
		while (it.hasNext()) {
			ParticleWisp wisp = (ParticleWisp) it.next();
			if (!wisp.isAlive()) {
				it.remove();
			} else {
				wisp.onUpdate();
			}
		}
	}

	private static FontRenderer pixelLove;

	@Override
	public FontRenderer getCustomFontRenderer() {
		return pixelLove;
	}

	@Override
	public void onPilotEnterWeedwoodRowboat(Entity pilot) {
        WeedwoodRowboatHandler.INSTANCE.onPilotEnterWeedwoodRowboat(pilot);
	}

    @Override
    public void onPilotExitWeedwoodRowboat(EntityWeedwoodRowboat rowboat, Entity pilot) {
        WeedwoodRowboatHandler.INSTANCE.onPilotExitWeedwoodRowboat(rowboat, pilot);
    }
    
    @Override
    public Proxy getNetProxy() {
    	return Minecraft.getMinecraft().getProxy();
    }
    
    @Override
    public boolean isSingleplayer() {
    	return Minecraft.getMinecraft().isSingleplayer();
    }
}
