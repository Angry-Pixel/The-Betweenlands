package thebetweenlands.proxy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.event.AmbienceSoundPlayHandler;
import thebetweenlands.client.event.BLMusicHandler;
import thebetweenlands.client.gui.GuiOverlay;
import thebetweenlands.client.render.block.BlockDoublePlantRender;
import thebetweenlands.client.render.block.BlockRubberLogRender;
import thebetweenlands.client.render.entity.RenderAngler;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.entity.RenderLeech;
import thebetweenlands.client.render.entity.RenderSludge;
import thebetweenlands.client.render.entity.RenderSporeling;
import thebetweenlands.client.render.entity.RenderSwampHag;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.entity.RenderWight;
import thebetweenlands.client.render.item.ItemDruidAltarRenderer;
import thebetweenlands.client.render.item.ItemWeedWoodChestRenderer;
import thebetweenlands.client.render.tileentity.TileEntityBLWorkbenchRenderer;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.client.render.tileentity.TileEntityWeedWoodChestRenderer;
import thebetweenlands.client.render.tileentity.TileEntityWispRenderer;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.particles.EntityAltarCraftingFX;
import thebetweenlands.entities.particles.EntityDruidCastingFX;
import thebetweenlands.entities.particles.EntityThemFX;
import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.manager.TextureManager;
import thebetweenlands.tileentities.TileEntityBLCraftingTable;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

	public enum BlockRenderIDs {

		DOUBLE_PLANTS, RUBBER_LOG;

		private final int ID;

		BlockRenderIDs() {
			ID = RenderingRegistry.getNextAvailableRenderId();
		}

		public int id() {
			return ID;
		}
	}

	@Override
	public void preInit() {
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
		
		//Tile Entity Renderer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new TileEntityDruidAltarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedWoodChest.class, new TileEntityWeedWoodChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBLCraftingTable.class, new TileEntityBLWorkbenchRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWisp.class, new TileEntityWispRenderer());

		//Item Entity Renderer
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.druidAltar), new ItemDruidAltarRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.weedWoodChest), new ItemWeedWoodChestRenderer());

		//Block Renderer
		RenderingRegistry.registerBlockHandler(new BlockDoublePlantRender());
		RenderingRegistry.registerBlockHandler(new BlockRubberLogRender());

        //Events
        MinecraftForge.EVENT_BUS.register(new GuiOverlay());
        AmbienceSoundPlayHandler ambientHandler = new AmbienceSoundPlayHandler();
        FMLCommonHandler.instance().bus().register(ambientHandler);
        MinecraftForge.EVENT_BUS.register(ambientHandler);
        FMLCommonHandler.instance().bus().register(new BLMusicHandler());
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
			fx = new EntityAltarCraftingFX(world, x, y, z, vecX, vecY, vecZ, scale, (TileEntityDruidAltar)data[0]);
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

		if (fx != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int level)
	{
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

	public void uncorruptPlayerSkin(AbstractClientPlayer entityPlayer)
	{
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
}
