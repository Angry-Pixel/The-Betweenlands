package thebetweenlands.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.event.AmbienceSoundPlayHandler;
import thebetweenlands.client.event.BLMusicHandler;
import thebetweenlands.client.event.DecayTextureStitchHandler;
import thebetweenlands.client.gui.GuiOverlay;
import thebetweenlands.client.render.TessellatorDebug;
import thebetweenlands.client.render.block.*;
import thebetweenlands.client.render.entity.*;
import thebetweenlands.client.render.item.*;
import thebetweenlands.client.render.tileentity.*;
import thebetweenlands.client.tooltips.HeldItemTooltipHandler;
import thebetweenlands.entities.EntityAngryPebble;
import thebetweenlands.entities.EntityBLArrow;
import thebetweenlands.entities.EntityBLItemFrame;
import thebetweenlands.entities.EntitySnailPoisonJet;
import thebetweenlands.entities.mobs.*;
import thebetweenlands.entities.particles.*;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.event.render.*;
import thebetweenlands.event.world.ThemHandler;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.manager.TextureManager;
import thebetweenlands.network.handlers.ClientPacketHandler;
import thebetweenlands.tileentities.*;
import thebetweenlands.utils.TimerDebug;
import thebetweenlands.utils.confighandler.ConfigHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class ClientProxy extends CommonProxy {
	public enum BlockRenderIDs {
		DOUBLE_PLANTS, RUBBER_LOG, WEEDWOOD_BUSH, SWAMP_WATER, SWAMP_REED, STALACTITE, ROOT, MODEL_PLANT, GOLDEN_CLUB, BOG_BEAN, MARSH_MARIGOLD, WATER_WEEDS, DOOR, WALKWAY, RUBBER_TAP, LEVER, MUDFLOWERPOT, SLOPE;

		private final int ID;

		BlockRenderIDs() {
			ID = RenderingRegistry.getNextAvailableRenderId();
		}

		public int id() {
			return ID;
		}
	}

	public static RenderDragonFly dragonFlyRenderer;

	public static TimerDebug debugTimer;

	@Override
	public void preInit() {
		//Register packet handlers
		try {
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(ClientPacketHandler.class, Side.CLIENT);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(TileEntityAnimator.class, Side.CLIENT);
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(TileEntityDruidAltar.class, Side.CLIENT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Mob Entity Renderer
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid());
		RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, new RenderAngler());
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

		//Tile Entity Renderer
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
		
		//Item Entity Renderer
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.druidAltar), new ItemDruidAltarRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.animator), new ItemAnimatorRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.weedwoodChest), new ItemWeedWoodChestRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.purifier), new ItemPurifierRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.compostBin), new ItemCompostBinRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.alembic), new ItemAlembicRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.infuser), new ItemInfuserRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.pestleAndMortar), new ItemPestleAndMortarRenderer());

		//Block Renderer
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
		
		//Events
		MinecraftForge.EVENT_BUS.register(new GuiOverlay());
		AmbienceSoundPlayHandler ambientHandler = new AmbienceSoundPlayHandler();
		FMLCommonHandler.instance().bus().register(ambientHandler);
		MinecraftForge.EVENT_BUS.register(ambientHandler);
		FMLCommonHandler.instance().bus().register(new BLMusicHandler());
		FMLCommonHandler.instance().bus().register(BrightnessHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(WispHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FireflyHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ThemHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new DecayTextureStitchHandler());
		FMLCommonHandler.instance().bus().register(new HeldItemTooltipHandler());

		if (ConfigHandler.DEBUG) {
			FMLCommonHandler.instance().bus().register(DebugHandler.INSTANCE);
			MinecraftForge.EVENT_BUS.register(DebugHandler.INSTANCE);
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
	public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, Object... data) {
		EntityFX fx = null;

		if( particleName.equals("druidmagic") ) {
			fx = new EntityDruidCastingFX(world, x, y, z, vecX, vecY, vecZ, scale);
		}

		if( particleName.equals("druidmagicbig") ) {
			fx = new EntityDruidCastingFX(world, x, y, z, vecX, vecY, vecZ, scale);
			fx.setRBGColorF(0F, 1F, 1F);
		}

		if( particleName.equals("altarcrafting") ) {
			fx = new EntityAltarCraftingFX(world, x, y, z, scale, (TileEntityDruidAltar)data[0]);
		}

		if( particleName.equals("smoke") ) {
			fx = new EntitySmokeFX(world, x, y, z, vecX, vecY, vecZ);
		}

		if( particleName.equals("flame") ) {
			fx = new EntityFlameFX(world, x, y, z, vecX, vecY, vecZ);
		}

		if( particleName.equals("sulfurTorch") ) {
			fx = new EntitySmokeFX(world, x, y, z, 0F, 0F, 0F);
			fx.setRBGColorF(1F, 0.9294F, 0F);
		}

		if (particleName.equals("sulfurOre")) {
			fx = new EntitySpellParticleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(1F, 0.9294F, 0F);
		}

		if (particleName.equals("snailPoison")) {
			fx = new EntitySpellParticleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(1F, 0F, 0F);
		}

		if (particleName.equals("dirtDecay")) {
			fx = new EntitySpellParticleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(0.306F, 0.576F, 0.192F);
		}

		if (particleName.equals("bubblePurifier")) {
			fx = new EntityBLBubbleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(0.306F, 0.576F, 0.192F);
		}
		
		if (particleName.equals("bubbleInfusion")) {
			fx = new EntityBLBubbleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(0.5F, 0F, 0.125F);
			fx.setAlphaF(0.5F);
		}

		if( particleName.equals("steamPurifier") ) {
			fx = new EntitySmokeFX(world, x, y, z, 0F, 0F, 0F);
			fx.setRBGColorF(1F, 1F, 1F);
		}

		if( particleName.equals("portal") ) {
			fx = new EntityPortalFX(world, x, y, z, vecX, vecY, vecZ, 20, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, new ResourceLocation("thebetweenlands:textures/particle/portal.png"), 6);
		}

		if(particleName.equals("moth")) {
			if(world.rand.nextBoolean()) {
				fx = new EntityBugFX(world, x, y, z, 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, false, new ResourceLocation("thebetweenlands:textures/particle/moth1.png"), 2);
			} else {
				fx = new EntityBugFX(world, x, y, z, 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, false, new ResourceLocation("thebetweenlands:textures/particle/moth2.png"), 2);
			}
		}

		if(particleName.equals("fish")) {
			int fishParticle = world.rand.nextInt(3);
			if(fishParticle  == 0) {
				fx = new EntityBugFX(world, x, y, z, 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, true, new ResourceLocation("thebetweenlands:textures/particle/fish1.png"), 1);
			} else if(fishParticle == 1) {
				fx = new EntityBugFX(world, x, y, z, 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, true, new ResourceLocation("thebetweenlands:textures/particle/fish2.png"), 1);
			} else {
				fx = new EntityBugFX(world, x, y, z, 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, true, new ResourceLocation("thebetweenlands:textures/particle/fish3.png"), 1);
			}
		}

		if(particleName.equals("fly")) {
			fx = new EntityBugFX(world, x, y, z, 400, 0.05F, 0.025F, 0.06F * world.rand.nextFloat(), 0xFFFFFFFF, false, new ResourceLocation("thebetweenlands:textures/particle/fly.png"), 2);
		}

		if(particleName.equals("mosquito")) {
			fx = new EntityBugFX(world, x, y, z, 400, 0.05F, 0.025F, 0.1F * world.rand.nextFloat(), 0xFFFFFFFF, false, new ResourceLocation("thebetweenlands:textures/particle/mosquito.png"), 2);
		}

		if(particleName.equals("waterBug")) {
			fx = new EntityBugFX(world, x, y, z, 400, 0.03F, 0.02F, 0.2F * world.rand.nextFloat(), 0xFFFFFFFF, true, new ResourceLocation("thebetweenlands:textures/particle/waterbug.png"), 2);
		}

		if(particleName.equals("leaf")) {
			fx = new EntityLeafFX(world, x, y, z, 400, 0.12F * world.rand.nextFloat() + 0.03F, 0xFFFFFFFF, new ResourceLocation("thebetweenlands:textures/particle/leaf.png"), 5);
		}

		if (particleName.equals("splash")) {
			fx = new EntitySplashFX(world, x, y, z, vecX, vecY, vecZ, data.length == 0 || !(data[0] instanceof Integer) ? 0xFFFFFF : (int) data[0]);
		}

		if (fx != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
	public void corruptPlayerSkin(EntityPlayer player, int level)
	{
		AbstractClientPlayer entityPlayer = (AbstractClientPlayer) player;
		if (level == 0 || !DecayManager.enableDecay(entityPlayer))
		{
			uncorruptPlayerSkin(entityPlayer);
			return;
		}

		try
		{
			if (!hasBackup(entityPlayer)) backupPlayerSkin(entityPlayer);

			BufferedImage skin = TextureManager.getPlayerSkin(entityPlayer);
			BufferedImage corruption = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("thebetweenlands:textures/player/playerCorruption.png")).getInputStream());

			if (skin == null) return;
			BufferedImage corruptedSkin = new BufferedImage(skin.getWidth(), skin.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = corruptedSkin.getGraphics();
			g.drawImage(skin, 0, 0, null);
			for (int i = 0; i < level - 1; i++) g.drawImage(corruption, 0, 0, null);

			uploadPlayerSkin(entityPlayer, corruptedSkin);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void uncorruptPlayerSkin(EntityPlayer player)
	{
		AbstractClientPlayer entityPlayer = (AbstractClientPlayer) player;
		BufferedImage image = getOriginalPlayerSkin(entityPlayer);
		if (image != null) uploadPlayerSkin(entityPlayer, image);
	}

	public boolean hasBackup(AbstractClientPlayer player)
	{
		return new File("skinbackup" + File.separator + player.getCommandSenderName() + ".png").exists();
	}

	private void backupPlayerSkin(AbstractClientPlayer entityPlayer)
	{
		BufferedImage bufferedImage = TextureManager.getPlayerSkin(entityPlayer);

		File file = new File("skinbackup");
		file.mkdir();
		File skinFile = new File(file, entityPlayer.getCommandSenderName() + ".png");
		try
		{
			skinFile.createNewFile();
			if (bufferedImage != null) ImageIO.write(bufferedImage, "PNG", skinFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void uploadPlayerSkin(AbstractClientPlayer player, BufferedImage bufferedImage)
	{
		ITextureObject textureObject = Minecraft.getMinecraft().renderEngine.getTexture(player.getLocationSkin());

		if (textureObject == null)
		{
			textureObject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(player.getCommandSenderName())), AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
			Minecraft.getMinecraft().renderEngine.loadTexture(player.getLocationSkin(), textureObject);
		}

		TextureManager.uploadTexture(textureObject, bufferedImage);
	}

	private BufferedImage getOriginalPlayerSkin(AbstractClientPlayer entityPlayer)
	{
		File file = new File("skinbackup" + File.separator + entityPlayer.getCommandSenderName() + ".png");
		BufferedImage bufferedImage = null;

		try
		{
			if (file.exists()) bufferedImage = ImageIO.read(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return bufferedImage;
	}

	@Override
	public void updateWispParticles(TileEntityWisp te) {
		Iterator<Object> i = te.particleList.iterator();
		while (i.hasNext()) {
			if(((EntityWispFX)i.next()).isDead) {
				i.remove();
			}
		}
		for(Object particle : te.particleList) {
			((EntityWispFX)particle).onUpdate();
		}
	}

	@Override
	public void spawnThem() {
		if(Minecraft.getMinecraft().thePlayer.dimension != ConfigHandler.DIMENSION_ID) return;
		if(FogHandler.INSTANCE.hasDenseFog() && FogHandler.INSTANCE.getCurrentFogEnd() < 80.0f) {
			int probability = (int)(FogHandler.INSTANCE.getCurrentFogEnd()) / 2 + 16;
			if(Minecraft.getMinecraft().theWorld.rand.nextInt(probability) == 0) {
				double xOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double zOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double sx = Minecraft.getMinecraft().renderViewEntity.posX + xOff;
				double sz = Minecraft.getMinecraft().renderViewEntity.posZ + zOff;
				double sy = Minecraft.getMinecraft().theWorld.getHeightValue((int)sx, (int)sz) + 1.0f + Minecraft.getMinecraft().theWorld.rand.nextFloat() * 2.5f;
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityThemFX(
						Minecraft.getMinecraft().theWorld, sx, sy, sz));
			}
		}
	}

	@Override
	public void playPortalSounds(Entity entity, int timer) { 
		if(entity instanceof EntityPlayerSP){
			EntityPlayerSP player = (EntityPlayerSP)entity;
			if(timer < 120) {
				player.closeScreen();
				if (timer == 119)
					player.playSound("thebetweenlands:portalTrigger", 1.0F, 0.8F);
				if(timer == 2)
					player.playSound("thebetweenlands:portalTravel", 1.25f, 0.8f);
			}
		}
	}
}