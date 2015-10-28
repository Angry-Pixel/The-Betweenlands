package thebetweenlands.proxy;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.imageio.ImageIO;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.event.AmbienceSoundPlayHandler;
import thebetweenlands.client.event.BLMusicHandler;
import thebetweenlands.client.event.CorrosionTextureStitchHandler;
import thebetweenlands.client.gui.GuiOverlay;
import thebetweenlands.client.input.WeedwoodRowboatHandler;
import thebetweenlands.client.render.TessellatorDebug;
import thebetweenlands.client.render.block.BlockBLLeverRenderer;
import thebetweenlands.client.render.block.BlockDoorRenderer;
import thebetweenlands.client.render.block.BlockDoublePlantRenderer;
import thebetweenlands.client.render.block.BlockHollowLogRenderer;
import thebetweenlands.client.render.block.BlockModelPlantRenderer;
import thebetweenlands.client.render.block.BlockMudFlowerPotRenderer;
import thebetweenlands.client.render.block.BlockRootRenderer;
import thebetweenlands.client.render.block.BlockRubberLogRenderer;
import thebetweenlands.client.render.block.BlockRubberTapRenderer;
import thebetweenlands.client.render.block.BlockSlopeRenderer;
import thebetweenlands.client.render.block.BlockStalactiteRenderer;
import thebetweenlands.client.render.block.BlockSwampReedRenderer;
import thebetweenlands.client.render.block.BlockSwampWaterRenderer;
import thebetweenlands.client.render.block.BlockWalkwayRenderer;
import thebetweenlands.client.render.block.BlockWeedWoodBushRenderer;
import thebetweenlands.client.render.entity.RenderAngler;
import thebetweenlands.client.render.entity.RenderAngryPebble;
import thebetweenlands.client.render.entity.RenderBLArrow;
import thebetweenlands.client.render.entity.RenderBLItemFrame;
import thebetweenlands.client.render.entity.RenderBerserkerGuardian;
import thebetweenlands.client.render.entity.RenderBlindCaveFish;
import thebetweenlands.client.render.entity.RenderBloodSnail;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.entity.RenderDragonFly;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.entity.RenderGecko;
import thebetweenlands.client.render.entity.RenderGiantToad;
import thebetweenlands.client.render.entity.RenderLeech;
import thebetweenlands.client.render.entity.RenderLurker;
import thebetweenlands.client.render.entity.RenderMeleeGuardian;
import thebetweenlands.client.render.entity.RenderMireSnail;
import thebetweenlands.client.render.entity.RenderMireSnailEgg;
import thebetweenlands.client.render.entity.RenderPeatMummy;
import thebetweenlands.client.render.entity.RenderSiltCrab;
import thebetweenlands.client.render.entity.RenderSludge;
import thebetweenlands.client.render.entity.RenderSnailPoisonJet;
import thebetweenlands.client.render.entity.RenderSporeling;
import thebetweenlands.client.render.entity.RenderSwampHag;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.entity.RenderTarminion;
import thebetweenlands.client.render.entity.RenderTermite;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.client.render.entity.RenderWight;
import thebetweenlands.client.render.item.ItemAlembicRenderer;
import thebetweenlands.client.render.item.ItemAnimatorRenderer;
import thebetweenlands.client.render.item.ItemCompostBinRenderer;
import thebetweenlands.client.render.item.ItemDruidAltarRenderer;
import thebetweenlands.client.render.item.ItemInfuserRenderer;
import thebetweenlands.client.render.item.ItemLootPot1Renderer;
import thebetweenlands.client.render.item.ItemLootPot2Renderer;
import thebetweenlands.client.render.item.ItemLootPot3Renderer;
import thebetweenlands.client.render.item.ItemPestleAndMortarRenderer;
import thebetweenlands.client.render.item.ItemPurifierRenderer;
import thebetweenlands.client.render.item.ItemAspectOverlayRenderer;
import thebetweenlands.client.render.item.ItemTarLootPot1Renderer;
import thebetweenlands.client.render.item.ItemTarLootPot2Renderer;
import thebetweenlands.client.render.item.ItemTarLootPot3Renderer;
import thebetweenlands.client.render.item.ItemTarminionRenderer;
import thebetweenlands.client.render.item.ItemVolarKiteRenderer;
import thebetweenlands.client.render.item.ItemWeedWoodChestRenderer;
import thebetweenlands.client.render.tileentity.TileEntityAlembicRenderer;
import thebetweenlands.client.render.tileentity.TileEntityAnimatorRenderer;
import thebetweenlands.client.render.tileentity.TileEntityBLSpawnerRenderer;
import thebetweenlands.client.render.tileentity.TileEntityBLWorkbenchRenderer;
import thebetweenlands.client.render.tileentity.TileEntityCompostBinRenderer;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.client.render.tileentity.TileEntityInfuserRenderer;
import thebetweenlands.client.render.tileentity.TileEntityItemShelfRenderer;
import thebetweenlands.client.render.tileentity.TileEntityLifeCrystalRenderer;
import thebetweenlands.client.render.tileentity.TileEntityLootPot1Renderer;
import thebetweenlands.client.render.tileentity.TileEntityLootPot2Renderer;
import thebetweenlands.client.render.tileentity.TileEntityLootPot3Renderer;
import thebetweenlands.client.render.tileentity.TileEntityPestleAndMortarRenderer;
import thebetweenlands.client.render.tileentity.TileEntityPurifierRenderer;
import thebetweenlands.client.render.tileentity.TileEntityTarBeastSpawnerRenderer;
import thebetweenlands.client.render.tileentity.TileEntityTarLootPot1Renderer;
import thebetweenlands.client.render.tileentity.TileEntityTarLootPot2Renderer;
import thebetweenlands.client.render.tileentity.TileEntityTarLootPot3Renderer;
import thebetweenlands.client.render.tileentity.TileEntityWeedWoodChestRenderer;
import thebetweenlands.client.render.tileentity.TileEntityWispRenderer;
import thebetweenlands.client.tooltips.HeldItemTooltipHandler;
import thebetweenlands.entities.EntityAngryPebble;
import thebetweenlands.entities.EntityBLArrow;
import thebetweenlands.entities.EntityBLItemFrame;
import thebetweenlands.entities.EntitySnailPoisonJet;
import thebetweenlands.entities.EntityThrownTarminion;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;
import thebetweenlands.entities.mobs.EntityBlindCaveFish;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.EntityDragonFly;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityGecko;
import thebetweenlands.entities.mobs.EntityGiantToad;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntityMeleeGuardian;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntityMireSnailEgg;
import thebetweenlands.entities.mobs.EntityPeatMummy;
import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityTarminion;
import thebetweenlands.entities.mobs.EntityTermite;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.particles.EntityThemFX;
import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.event.debugging.DebugHandlerClient;
import thebetweenlands.event.debugging.DebugHandlerCommon;
import thebetweenlands.event.elixirs.ElixirClientHandler;
import thebetweenlands.event.elixirs.ElixirCommonHandler;
import thebetweenlands.event.render.BrightnessHandler;
import thebetweenlands.event.render.FireflyHandler;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.event.render.GLUProjectionHandler;
import thebetweenlands.event.render.ItemTextureTicker;
import thebetweenlands.event.render.OverlayHandler;
import thebetweenlands.event.render.ScreenShakeHandler;
import thebetweenlands.event.render.ShaderHandler;
import thebetweenlands.event.render.WispHandler;
import thebetweenlands.event.world.ThemHandler;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.manager.TextureManager;
import thebetweenlands.network.handlers.ClientPacketHandler;
import thebetweenlands.tileentities.TileEntityAlembic;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;
import thebetweenlands.tileentities.TileEntityBLSpawner;
import thebetweenlands.tileentities.TileEntityCompostBin;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.tileentities.TileEntityInfuser;
import thebetweenlands.tileentities.TileEntityItemShelf;
import thebetweenlands.tileentities.TileEntityLifeCrystal;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.tileentities.TileEntityLootPot2;
import thebetweenlands.tileentities.TileEntityLootPot3;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import thebetweenlands.tileentities.TileEntityPurifier;
import thebetweenlands.tileentities.TileEntityTarBeastSpawner;
import thebetweenlands.tileentities.TileEntityTarLootPot1;
import thebetweenlands.tileentities.TileEntityTarLootPot2;
import thebetweenlands.tileentities.TileEntityTarLootPot3;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.utils.TimerDebug;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class ClientProxy extends CommonProxy {
	public enum BlockRenderIDs {
		DOUBLE_PLANTS,
		RUBBER_LOG,
		WEEDWOOD_BUSH,
		SWAMP_WATER,
		SWAMP_REED,
		STALACTITE,
		ROOT,
		MODEL_PLANT,
		GOLDEN_CLUB,
		BOG_BEAN,
		MARSH_MARIGOLD,
		WATER_WEEDS,
		DOOR,
		WALKWAY,
		RUBBER_TAP,
		LEVER,
		MUDFLOWERPOT,
		SLOPE,
		HOLLOW_LOG;

		private final int ID;

		BlockRenderIDs() {
			ID = RenderingRegistry.getNextAvailableRenderId();
		}

		public int id() {
			return ID;
		}
	}

	private static final ResourceLocation PLAYER_CORRUPTION_TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/player/playerCorruption.png");

	private BufferedImage playerCorruptionImg = null;

	public static RenderDragonFly dragonFlyRenderer;

	public static TimerDebug debugTimer;

	@Override
	public void init() {
		super.init();
		// Register packet handlers
		try {
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(ClientPacketHandler.class, Side.CLIENT);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(TileEntityAnimator.class, Side.CLIENT);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(TileEntityDruidAltar.class, Side.CLIENT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Mob Entity Renderer
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid());
		RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, new RenderAngler());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindCaveFish.class, new RenderBlindCaveFish());
		RenderingRegistry.registerEntityRenderingHandler(EntitySludge.class, new RenderSludge());
		RenderingRegistry.registerEntityRenderingHandler(EntitySwampHag.class, new RenderSwampHag());
		RenderingRegistry.registerEntityRenderingHandler(EntityTarBeast.class, new RenderTarBeast());
		RenderingRegistry.registerEntityRenderingHandler(EntityWight.class, new RenderWight());
		RenderingRegistry.registerEntityRenderingHandler(EntityFirefly.class, new RenderFirefly());
		RenderingRegistry.registerEntityRenderingHandler(EntityLeech.class, new RenderLeech());
		RenderingRegistry.registerEntityRenderingHandler(EntitySporeling.class, new RenderSporeling());
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonFly.class, dragonFlyRenderer = new RenderDragonFly());
		RenderingRegistry.registerEntityRenderingHandler(EntityBloodSnail.class, new RenderBloodSnail());
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnail.class, new RenderMireSnail());
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnailEgg.class, new RenderMireSnailEgg());
		RenderingRegistry.registerEntityRenderingHandler(EntityAngryPebble.class, new RenderAngryPebble());
		RenderingRegistry.registerEntityRenderingHandler(EntityBLArrow.class, new RenderBLArrow());
		RenderingRegistry.registerEntityRenderingHandler(EntitySiltCrab.class, new RenderSiltCrab());
		RenderingRegistry.registerEntityRenderingHandler(EntitySnailPoisonJet.class, new RenderSnailPoisonJet());
		RenderingRegistry.registerEntityRenderingHandler(EntityLurker.class, new RenderLurker());
		RenderingRegistry.registerEntityRenderingHandler(EntityGecko.class, new RenderGecko());
		RenderingRegistry.registerEntityRenderingHandler(EntityTermite.class, new RenderTermite());
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantToad.class, new RenderGiantToad());
		RenderingRegistry.registerEntityRenderingHandler(EntityMeleeGuardian.class, new RenderMeleeGuardian());
		RenderingRegistry.registerEntityRenderingHandler(EntityBerserkerGuardian.class, new RenderBerserkerGuardian());
		RenderingRegistry.registerEntityRenderingHandler(EntityBLItemFrame.class, new RenderBLItemFrame());
		RenderingRegistry.registerEntityRenderingHandler(EntityTarminion.class, new RenderTarminion());
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownTarminion.class, new ItemTarminionRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityWeedwoodRowboat.class, new RenderWeedwoodRowboat());
		RenderingRegistry.registerEntityRenderingHandler(EntityPeatMummy.class, new RenderPeatMummy());

		// Tile Entity Renderer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new TileEntityDruidAltarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedWoodChest.class, new TileEntityWeedWoodChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBLCraftingTable.class, new TileEntityBLWorkbenchRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWisp.class, new TileEntityWispRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAnimator.class, new TileEntityAnimatorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new TileEntityPurifierRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCompostBin.class, new TileEntityCompostBinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlembic.class, new TileEntityAlembicRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfuser.class, new TileEntityInfuserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPestleAndMortar.class, new TileEntityPestleAndMortarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLifeCrystal.class, new TileEntityLifeCrystalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot1.class, new TileEntityLootPot1Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot2.class, new TileEntityLootPot2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot3.class, new TileEntityLootPot3Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBLSpawner.class, new TileEntityBLSpawnerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarBeastSpawner.class, new TileEntityTarBeastSpawnerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot1.class, new TileEntityTarLootPot1Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot2.class, new TileEntityTarLootPot2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot3.class, new TileEntityTarLootPot3Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new TileEntityItemShelfRenderer());

		// Item Entity Renderer
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.druidAltar), new ItemDruidAltarRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.animator), new ItemAnimatorRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.weedwoodChest), new ItemWeedWoodChestRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.purifier), new ItemPurifierRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.compostBin), new ItemCompostBinRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.alembic), new ItemAlembicRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.infuser), new ItemInfuserRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.pestleAndMortar), new ItemPestleAndMortarRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.lootPot1), new ItemLootPot1Renderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.lootPot2), new ItemLootPot2Renderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.lootPot3), new ItemLootPot3Renderer());
		MinecraftForgeClient.registerItemRenderer(BLItemRegistry.tarminion, new ItemTarminionRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.tarLootPot1), new ItemTarLootPot1Renderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.tarLootPot2), new ItemTarLootPot2Renderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.tarLootPot3), new ItemTarLootPot3Renderer());
		MinecraftForgeClient.registerItemRenderer(BLItemRegistry.volarPad, new ItemVolarKiteRenderer());

		//Register custom item renderer for aspect overlays
		for(Item item : BLItemRegistry.ITEMS) {
			if(MinecraftForgeClient.getItemRenderer(new ItemStack(item), ItemRenderType.INVENTORY) == null) {
				this.registerItemRenderer(item);
			}
		}
		for(Block block : BLBlockRegistry.BLOCKS) {
			Item blockItem = Item.getItemFromBlock(block);
			if(MinecraftForgeClient.getItemRenderer(new ItemStack(blockItem), ItemRenderType.INVENTORY) == null) {
				this.registerItemRenderer(blockItem);
			}
		}

		// Block Renderer
		RenderingRegistry.registerBlockHandler(new BlockDoublePlantRenderer());
		RenderingRegistry.registerBlockHandler(new BlockRubberLogRenderer());
		RenderingRegistry.registerBlockHandler(new BlockWeedWoodBushRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSwampWaterRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSwampReedRenderer());
		RenderingRegistry.registerBlockHandler(new BlockStalactiteRenderer());
		RenderingRegistry.registerBlockHandler(new BlockRootRenderer());
		RenderingRegistry.registerBlockHandler(new BlockModelPlantRenderer());
		RenderingRegistry.registerBlockHandler(new BlockDoorRenderer());
		RenderingRegistry.registerBlockHandler(new BlockWalkwayRenderer());
		RenderingRegistry.registerBlockHandler(new BlockRubberTapRenderer());
		RenderingRegistry.registerBlockHandler(new BlockBLLeverRenderer());
		RenderingRegistry.registerBlockHandler(new BlockMudFlowerPotRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSlopeRenderer());
		RenderingRegistry.registerBlockHandler(new BlockHollowLogRenderer());

		// Events
		MinecraftForge.EVENT_BUS.register(new GuiOverlay());
		AmbienceSoundPlayHandler ambientHandler = new AmbienceSoundPlayHandler();
		FMLCommonHandler.instance().bus().register(ambientHandler);
		MinecraftForge.EVENT_BUS.register(ambientHandler);
		FMLCommonHandler.instance().bus().register(new BLMusicHandler());
		FMLCommonHandler.instance().bus().register(BrightnessHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(FogHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(WispHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FireflyHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ThemHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new CorrosionTextureStitchHandler());
		FMLCommonHandler.instance().bus().register(new HeldItemTooltipHandler());
		MinecraftForge.EVENT_BUS.register(GLUProjectionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(OverlayHandler.INSTANCE);
		WeedwoodRowboatHandler.INSTANCE.init();
		FMLCommonHandler.instance().bus().register(ScreenShakeHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ItemTextureTicker.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ElixirClientHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ElixirClientHandler.INSTANCE);

		if (ConfigHandler.DEBUG) {
			FMLCommonHandler.instance().bus().register(DebugHandlerClient.INSTANCE);
			MinecraftForge.EVENT_BUS.register(DebugHandlerClient.INSTANCE);
			Field tessellatorInstanceField = ReflectionHelper.findField(Tessellator.class, "instance", "field_78398_a", "a");
			try {
				ReflectionHelper.findField(Field.class, "modifiers").setInt(tessellatorInstanceField, tessellatorInstanceField.getModifiers() & ~Modifier.FINAL);
				tessellatorInstanceField.set(null, new TessellatorDebug());
			} catch (Exception e) {
				e.printStackTrace();
			}
			ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), debugTimer = new TimerDebug(20), "timer", "field_71428_T", "Q");
		}
	}



	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void corruptPlayerSkin(EntityPlayer player, int level) {
		AbstractClientPlayer entityPlayer = (AbstractClientPlayer) player;
		if (level == 0 || !DecayManager.enableDecay(entityPlayer)) {
			uncorruptPlayerSkin(entityPlayer);
			return;
		}
		if (!hasBackup(entityPlayer)) {
			backupPlayerSkin(entityPlayer);
		}
		BufferedImage skin = TextureManager.getPlayerSkin(entityPlayer);
		if (skin == null) {
			return;
		}
		BufferedImage corruption = getPlayerCorruptionTexture();
		BufferedImage corruptedSkin = new BufferedImage(skin.getWidth(), skin.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = corruptedSkin.createGraphics();
		g.drawImage(skin, 0, 0, null);
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8F * level / 10);
		g.setComposite(alphaComposite);
		g.drawImage(corruption, 0, 0, null);
		uploadPlayerSkin(entityPlayer, corruptedSkin);
	}

	private BufferedImage getPlayerCorruptionTexture() {
		if (playerCorruptionImg == null) {
			try {
				playerCorruptionImg = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(PLAYER_CORRUPTION_TEXTURE).getInputStream());
			} catch (IOException e) {
				playerCorruptionImg = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
				e.printStackTrace();
				// TODO: better logging
			}
		}
		return playerCorruptionImg;
	}

	@Override
	public void uncorruptPlayerSkin(EntityPlayer player) {
		AbstractClientPlayer entityPlayer = (AbstractClientPlayer) player;
		BufferedImage image = getOriginalPlayerSkin(entityPlayer);
		if (image != null) {
			uploadPlayerSkin(entityPlayer, image);
		}
	}

	public boolean hasBackup(AbstractClientPlayer player) {
		return new File("skinbackup" + File.separator + player.getCommandSenderName() + ".png").exists();
	}

	private void backupPlayerSkin(AbstractClientPlayer entityPlayer) {
		BufferedImage bufferedImage = TextureManager.getPlayerSkin(entityPlayer);

		File file = new File("skinbackup");
		file.mkdir();
		File skinFile = new File(file, entityPlayer.getCommandSenderName() + ".png");
		try {
			skinFile.createNewFile();
			if (bufferedImage != null) {
				ImageIO.write(bufferedImage, "PNG", skinFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void uploadPlayerSkin(AbstractClientPlayer player, BufferedImage bufferedImage) {
		ITextureObject textureObject = Minecraft.getMinecraft().renderEngine.getTexture(player.getLocationSkin());

		if (textureObject == null) {
			textureObject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(player.getCommandSenderName())), AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
			Minecraft.getMinecraft().renderEngine.loadTexture(player.getLocationSkin(), textureObject);
		}

		TextureManager.uploadTexture(textureObject, bufferedImage);
	}

	private BufferedImage getOriginalPlayerSkin(AbstractClientPlayer entityPlayer) {
		File file = new File("skinbackup" + File.separator + entityPlayer.getCommandSenderName() + ".png");
		BufferedImage bufferedImage = null;

		try {
			if (file.exists()) {
				bufferedImage = ImageIO.read(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bufferedImage;
	}

	@Override
	public void updateWispParticles(TileEntityWisp te) {
		Iterator<Object> i = te.particleList.iterator();
		while (i.hasNext()) {
			if (((EntityWispFX) i.next()).isDead) {
				i.remove();
			}
		}
		for (Object particle : te.particleList) {
			((EntityWispFX) particle).onUpdate();
		}
	}

	@Override
	public void spawnThem() {
		if (Minecraft.getMinecraft().thePlayer.dimension != ConfigHandler.DIMENSION_ID) {
			return;
		}
		if (FogHandler.INSTANCE.hasDenseFog() && FogHandler.INSTANCE.getCurrentFogEnd() < 80.0f) {
			int probability = (int) FogHandler.INSTANCE.getCurrentFogEnd() / 2 + 16;
			if (Minecraft.getMinecraft().theWorld.rand.nextInt(probability) == 0) {
				double xOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double zOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double sx = Minecraft.getMinecraft().renderViewEntity.posX + xOff;
				double sz = Minecraft.getMinecraft().renderViewEntity.posZ + zOff;
				double sy = Minecraft.getMinecraft().theWorld.getHeightValue((int) sx, (int) sz) + 1.0f + Minecraft.getMinecraft().theWorld.rand.nextFloat() * 2.5f;
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityThemFX(Minecraft.getMinecraft().theWorld, sx, sy, sz));
			}
		}
	}

	@Override
	public void playPortalSounds(Entity entity, int timer) {
		if (entity instanceof EntityPlayerSP) {
			EntityPlayerSP player = (EntityPlayerSP) entity;
			if (timer < 120) {
				player.closeScreen();
				if (timer == 119) {
					player.playSound("thebetweenlands:portalTrigger", 1.0F, 0.8F);
				}
				if (timer == 2) {
					player.playSound("thebetweenlands:portalTravel", 1.25f, 0.8f);
				}
			}
		}
	}

	@Override
	public DebugHandlerCommon getDebugHandler() {
		return DebugHandlerClient.INSTANCE;
	}

	@Override
	public void onPlayerEnterWeedwoodRowboat() {
		if (ConfigHandler.rowboatView) {
			WeedwoodRowboatHandler.WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE.switchTo();
		} else {
			WeedwoodRowboatHandler.WEEDWOOD_ROWBOAT_FIRST_PERSON_PERSPECTIVE.switchTo();
		}
	}

	@Override
	public void updateRiderYawInWeedwoodRowboat(EntityWeedwoodRowboat rowboat, EntityLivingBase rider) {
		if (rowboat.worldObj.isRemote && WeedwoodRowboatHandler.WEEDWOOD_ROWBOAT_THIRD_PERSON_PERSPECTIVE.isCurrentPerspective()) {
			rider.rotationYaw += ((rowboat.rotationYaw - rider.rotationYaw) % 180 - 90) * 0.2F;
			rider.rotationPitch *= 0.8F;
		}
	}

	private final ItemAspectOverlayRenderer itemAspectOverlayRenderer = new ItemAspectOverlayRenderer();
	@Override
	public void registerItemRenderer(Item item) { 
		MinecraftForgeClient.registerItemRenderer(item, this.itemAspectOverlayRenderer);
	}
}
