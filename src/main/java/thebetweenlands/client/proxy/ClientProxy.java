package thebetweenlands.client.proxy;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.event.ShaderHandler;
import thebetweenlands.client.event.TextureStitchHandler;
import thebetweenlands.client.gui.inventory.GuiDruidAltar;
import thebetweenlands.client.gui.inventory.GuiPurifier;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.client.particle.entity.ParticleWisp;
import thebetweenlands.client.render.entity.*;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.tile.RenderDruidAltar;
import thebetweenlands.client.render.tile.RenderLootPot;
import thebetweenlands.client.render.tile.RenderPurifier;
import thebetweenlands.client.render.tile.RenderSpawnerBetweenlands;
import thebetweenlands.client.render.tile.RenderWeedwoodWorkbench;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.HLEntryRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.util.config.ConfigHandler;

public class ClientProxy extends CommonProxy {

	//Please turn this off again after using
	private static final boolean createJSONFile = false;

	public static Render<EntityDragonFly> dragonFlyRenderer;

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		switch (id) {
		case GUI_DRUID_ALTAR: {
			if (tile instanceof TileEntityDruidAltar)
				return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
			break;
		}
		case GUI_PURIFIER: {
			if (tile instanceof TileEntityPurifier) {
				return new GuiPurifier(player.inventory, (TileEntityPurifier) tile);
			}
			break;
		}
		case GUI_WEEDWOOD_CRAFT: {
			if (tile instanceof TileEntityWeedwoodWorkbench) {
				return new GuiWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
			}
			break;
		}
		case GUI_HL:{
			return new GuiManualHerblore(player);
		}
		}
		return null;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public void registerItemAndBlockRenderers() {
		CustomModelManager.INSTANCE.registerLoader();
		//TODO ItemRegistry.registerRenderers();
		BlockRegistry.registerRenderers();
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
                        if (ConfigHandler.debug && createJSONFile)
                            JsonRenderGenerator.createJSONForBlock(block, models.get(i));
                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
                    }
            } else {
                String name = block.getRegistryName().toString().replace("thebetweenlands:", "");
                if (ConfigHandler.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForBlock(block, name);
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name, "inventory"));
            }
        }
	 */
	@Override
	public void registerDefaultItemRenderer(Item item) {
		if (item instanceof ItemRegistry.ISubItemsItem) {
			Set<Entry<Integer, ResourceLocation>> models = ((ItemRegistry.ISubItemsItem) item).getModels().entrySet();
			Iterator<Entry<Integer, ResourceLocation>> modelsIT = ((ItemRegistry.ISubItemsItem) item).getModels().entrySet().iterator();
			while(modelsIT.hasNext()) {
				Entry<Integer, ResourceLocation> model = modelsIT.next();
				if (ConfigHandler.debug && createJSONFile)
					JsonRenderGenerator.createJSONForItem(item, model.getValue().getResourcePath());
				ModelLoader.setCustomModelResourceLocation(item, model.getKey(), new ModelResourceLocation(model.getValue(), "inventory"));
			}
		} else if (item instanceof ItemRegistry.ISingleJsonSubItems) {
			List<String> types = ((ItemRegistry.ISingleJsonSubItems) item).getTypes();
			for (int i = 0; i < types.size(); i++) {
				//if (ConfigHandler.debug && createJSONFile)
				//JsonRenderGenerator.createJSONForItem(item, types.get(i)); //TODO: Make this work. Tomorrow, (hopefully), so don't panic
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + item.getRegistryName().getResourcePath(), types.get(i)));
			}
		} else {
			String itemName = item.getRegistryName().toString().replace("thebetweenlands:", "");
			if (ConfigHandler.debug && createJSONFile)
				JsonRenderGenerator.createJSONForItem(item, itemName);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
		}
	}


	//Probably will only be used while updating
	@Override
	public void changeFileNames() {
		File textures = new File(TheBetweenlands.sourceFile, "assets/thebetweenlands/sounds");
		if (textures.listFiles() != null)
			for (File file : textures.listFiles()) {
				if (file.getName().contains(".ogg")) {
					CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

					String text = file.getName();
					for (int i = 0; i < sequence.length(); i++) {
						text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
					}
					File newFile = new File(file.getPath().replace(file.getName(), "") + text);
					System.out.println(file.renameTo(newFile));
				} else
					for (File file2 : file.listFiles()) {
						if (file2.getName().contains(".ogg")) {
							CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

							String text = file2.getName();
							for (int i = 0; i < sequence.length(); i++) {
								text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
							}
							File newFile = new File(file2.getPath().replace(file2.getName(), "") + text);
							System.out.println(file2.renameTo(newFile));
						}
					}
			}
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
	}

	@Override
	public void postInit() {
		dragonFlyRenderer = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityDragonFly.class);
		//Tile entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new RenderPurifier());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new RenderDruidAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodWorkbench.class, new RenderWeedwoodWorkbench());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot.class, new RenderLootPot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawnerBetweenlands.class, new RenderSpawnerBetweenlands());

		//item models
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_ALTAR), 0, TileEntityDruidAltar.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.COMPOST_BIN), 0, TileEntityCompostBin.class);
		//ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_SPAWNER), 0, TileEntityDruidSpawner.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.PURIFIER), 0, TileEntityPurifier.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_1.getMetadata(EnumFacing.NORTH), TileEntityLootPot.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_2.getMetadata(EnumFacing.NORTH), TileEntityLootPot.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_3.getMetadata(EnumFacing.NORTH), TileEntityLootPot.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.MOB_SPAWNER), 0, TileEntityMobSpawnerBetweenlands.class);

		pixelLove = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("thebetweenlands:textures/gui/manual/font_atlas.png"), Minecraft.getMinecraft().renderEngine, false);
		if (Minecraft.getMinecraft().gameSettings.language != null) {
			pixelLove.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(pixelLove);
		HLEntryRegistry.init();
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(TextureStitchHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
	}

	@Override
	public void updateWispParticles(TileEntityWisp te) {
		Iterator<Object> i = te.particleList.iterator();
		while (i.hasNext()) {
			if (!((ParticleWisp) i.next()).isAlive()) {
				i.remove();
			}
		}
		for (Object particle : te.particleList) {
			((ParticleWisp) particle).onUpdate();
		}
	}

	private static FontRenderer pixelLove;

	@Override
	public FontRenderer getCustomFontRenderer() {
		return pixelLove;
	}
}
