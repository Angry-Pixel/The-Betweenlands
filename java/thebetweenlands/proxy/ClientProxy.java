package thebetweenlands.proxy;

import com.google.common.base.Throwables;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.lanterns.BlockConnectionFastener;
import thebetweenlands.client.audio.SuperbSoundRegistry;
import thebetweenlands.client.event.AmbienceSoundPlayHandler;
import thebetweenlands.client.event.BLMusicHandler;
import thebetweenlands.client.event.CorrosionTextureStitchHandler;
import thebetweenlands.client.gui.GuiOverlay;
import thebetweenlands.client.input.KeyBindingsBL;
import thebetweenlands.client.input.WeedwoodRowboatHandler;
import thebetweenlands.client.model.block.crops.*;
import thebetweenlands.client.model.item.ModelExplorersHat;
import thebetweenlands.client.render.ConnectionRenderer;
import thebetweenlands.client.render.block.*;
import thebetweenlands.client.render.block.crops.BlockBLGenericCropRenderer;
import thebetweenlands.client.render.entity.*;
import thebetweenlands.client.render.entity.projectile.RenderAngryPebble;
import thebetweenlands.client.render.entity.projectile.RenderBLArrow;
import thebetweenlands.client.render.entity.projectile.RenderElixir;
import thebetweenlands.client.render.entity.projectile.RenderSnailPoisonJet;
import thebetweenlands.client.render.item.*;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.tileentity.*;
import thebetweenlands.entities.EntityBLItemFrame;
import thebetweenlands.entities.EntityRopeNode;
import thebetweenlands.entities.mobs.*;
import thebetweenlands.entities.particles.EntityThemFX;
import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.entities.projectiles.*;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.event.debugging.DebugHandlerClient;
import thebetweenlands.event.debugging.DebugHandlerCommon;
import thebetweenlands.event.elixirs.ElixirClientHandler;
import thebetweenlands.event.entity.AttackDamageHandler;
import thebetweenlands.event.item.ItemNBTExclusionHandler;
import thebetweenlands.event.item.ItemTooltipHandler;
import thebetweenlands.event.render.*;
import thebetweenlands.event.world.ThemHandler;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.manual.GuideBookEntryRegistry;
import thebetweenlands.manual.HLEntryRegistry;
import thebetweenlands.network.handlers.ClientPacketHandler;
import thebetweenlands.tileentities.*;
import thebetweenlands.tileentities.connection.Connection;
import thebetweenlands.utils.TimerDebug;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.utils.vectormath.Point3f;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

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
		HOLLOW_LOG,
		MOSS_BED,
		CROP,
		FARMED_DIRT,
		HOPPER;

		private final int ID;

		BlockRenderIDs() {
			ID = RenderingRegistry.getNextAvailableRenderId();
		}

		public int id() {
			return ID;
		}
	}

	private static final ResourceLocation PLAYER_CORRUPTION_TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/player/playerCorruption.png");

	private static final ModelExplorersHat EXPLORERS_HAT_MODEL = new ModelExplorersHat();

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
			TheBetweenlands.sidedPacketHandler.registerPacketHandler(AttackDamageHandler.class, Side.CLIENT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Key bindings
		KeyBindingsBL.init();

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
		RenderingRegistry.registerEntityRenderingHandler(EntityElixir.class, new RenderElixir());
		RenderingRegistry.registerEntityRenderingHandler(EntityRopeNode.class, new RenderRopeNode());
		RenderingRegistry.registerEntityRenderingHandler(EntityDreadfulMummy.class, new RenderDreadfulMummy());
		RenderingRegistry.registerEntityRenderingHandler(EntityVolatileSoul.class, new RenderVolatileSoul());
		RenderingRegistry.registerEntityRenderingHandler(EntitySludgeBall.class, new RenderSludgeBall());

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot1.class, new TileEntityLootPot1Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot2.class, new TileEntityLootPot2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot3.class, new TileEntityLootPot3Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBLSpawner.class, new TileEntityBLSpawnerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarBeastSpawner.class, new TileEntityTarBeastSpawnerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot1.class, new TileEntityTarLootPot1Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot2.class, new TileEntityTarLootPot2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarLootPot3.class, new TileEntityTarLootPot3Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new TileEntityItemShelfRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGeckoCage.class, new TileEntityGeckoCageRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAspectrusCrop.class, new TileEntityAspectrusCropRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBLSign.class, new TileEntityBLSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRepeller.class, new TileEntityRepellerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWraithPusher.class, new TileEntityWraithPusherRenderer());

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
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.geckoCage), new ItemGeckoCageRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.repeller), new ItemRepellerRenderer());

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
		RenderingRegistry.registerBlockHandler(new BlockMossBedRenderer());
		RenderingRegistry.registerBlockHandler(new BlockSlopeRenderer());
		RenderingRegistry.registerBlockHandler(new BlockHollowLogRenderer());
		RenderingRegistry.registerBlockHandler(new BlockBLGenericCropRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFarmedDirtRenderer());
		RenderingRegistry.registerBlockHandler(new BlockBLHopperRenderer());

		// Events
		MinecraftForge.EVENT_BUS.register(GuiOverlay.INSTANCE);
		FMLCommonHandler.instance().bus().register(GuiOverlay.INSTANCE);
		AmbienceSoundPlayHandler ambientHandler = new AmbienceSoundPlayHandler();
		FMLCommonHandler.instance().bus().register(ambientHandler);
		MinecraftForge.EVENT_BUS.register(ambientHandler);
		FMLCommonHandler.instance().bus().register(new BLMusicHandler());
		FMLCommonHandler.instance().bus().register(BrightnessHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(FogHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(WorldRenderHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ThemHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new CorrosionTextureStitchHandler());
		FMLCommonHandler.instance().bus().register(ItemTooltipHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ItemTooltipHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(GLUProjectionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(OverlayHandler.INSTANCE);
		WeedwoodRowboatHandler.INSTANCE.init();
		FMLCommonHandler.instance().bus().register(ScreenShakeHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ItemTextureTicker.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ElixirClientHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ElixirClientHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FovHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(DecayRenderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(AspectItemOverlayHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ItemNBTExclusionHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ItemNBTExclusionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new ConnectionRenderer());

		// Crop renderers
		BLBlockRegistry.fungusCrop.setCropModels(
				new ModelBase[]{
						new ModelCropFungus1(), 
						new ModelCropFungus2(), 
						new ModelCropFungus3(), 
						new ModelCropFungus4(), 
						new ModelCropFungus5()}, 
				new int[] {
						32, 32,
						32, 32,
						64, 64,
						64, 64,
						64, 64
				});

		replaceSoundRegistry();

		//Add listener to reload shader resources
		IResourceManager resourceMgr = Minecraft.getMinecraft().getResourceManager();
		if(resourceMgr instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)resourceMgr).registerReloadListener(ShaderHelper.INSTANCE);
		}

		if (ConfigHandler.DEBUG) {
			FMLCommonHandler.instance().bus().register(DebugHandlerClient.INSTANCE);
			MinecraftForge.EVENT_BUS.register(DebugHandlerClient.INSTANCE);
			Field tessellatorInstanceField = ReflectionHelper.findField(Tessellator.class, "instance", "field_78398_a", "a");
			unfinalize(tessellatorInstanceField);
			ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), debugTimer = new TimerDebug(20), "timer", "field_71428_T", "Q");
		}
	}

	private void replaceSoundRegistry() {
		SoundHandler mcSoundHandler = Minecraft.getMinecraft().getSoundHandler();
		Field sndRegistry = ReflectionHelper.findField(SoundHandler.class, "sndRegistry", "field_147697_e", "e");
		unfinalize(sndRegistry);
		try {
			sndRegistry.set(mcSoundHandler, new SuperbSoundRegistry());
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	private void unfinalize(Field field) {
		try {
			ReflectionHelper.findField(Field.class, "modifiers").setInt(field, field.getModifiers() & ~Modifier.FINAL);
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public void postInit() {
		//Font
		pixelLove = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("thebetweenlands:textures/gui/manual/fontAtlas.png"), Minecraft.getMinecraft().renderEngine, false);
		if (Minecraft.getMinecraft().gameSettings.language != null) {
			pixelLove.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(pixelLove);

		// Init manual
		GuideBookEntryRegistry.init();
		HLEntryRegistry.init();
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
			int probability = Math.max((int) FogHandler.INSTANCE.getCurrentFogEnd() / 2 + 16, 10);
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
			WeedwoodRowboatHandler.THIRD_PERSON_PERSPECTIVE.switchTo();
		} else {
			WeedwoodRowboatHandler.FIRST_PERSON_PERSPECTIVE.switchTo();
		}
	}

	@Override
	public void updateRiderYawInWeedwoodRowboat(EntityWeedwoodRowboat rowboat, EntityLivingBase rider) {
		if (rowboat.worldObj.isRemote && WeedwoodRowboatHandler.THIRD_PERSON_PERSPECTIVE.isCurrentPerspective()) {
			rider.rotationYaw += ((rowboat.rotationYaw - rider.rotationYaw) % 180 - 90) * 0.2F;
			rider.rotationPitch *= 0.8F;
		}
	}

	private static FontRenderer pixelLove;

	@Override
	public FontRenderer getCustomFontRenderer() {
		return pixelLove; 
	}

	@Override
	public ModelBiped getExplorersHatModel() {
		return EXPLORERS_HAT_MODEL;
	}

	@Override
	public float getCatenaryOffset(EntityPlayer player) {
		if (player == Minecraft.getMinecraft().thePlayer) {
			return -player.height * 0.4F;
		} else {
			return player.height - player.height * 0.4F;
		}
	}

	@Override
	public ItemStack getFairyLightsFastenerPickBlock(MovingObjectPosition target, World world, int x, int y, int z, BlockConnectionFastener block) {
		ItemStack itemStack = new ItemStack(block.getItem(world, x, y, z));
		NBTTagCompound tagCompound = new NBTTagCompound();
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		double playerX = Minecraft.getMinecraft().thePlayer.posX, playerY = Minecraft.getMinecraft().thePlayer.posY, playerZ = Minecraft.getMinecraft().thePlayer.posZ;
		if (tileEntity instanceof TileEntityConnectionFastener) {
			TileEntityConnectionFastener lightsFastener = (TileEntityConnectionFastener) tileEntity;
			Connection closetConnection = null;
			float smallestDistance = Float.MAX_VALUE;
			for (Connection connection : lightsFastener.getConnections()) {
				Point3f to = connection.getTo();
				if (to == null) {
					continue;
				}
				float tx = to.x, ty = to.y, tz = to.z;
				Point3f offset = ((BlockConnectionFastener) lightsFastener.getBlockType()).getOffsetForData(lightsFastener.getBlockMetadata(), 0.125F);
				float dist = (float) Math.abs((offset.x + tx) * (offset.x + tx) - playerX * playerX + (offset.y + ty) * (offset.y + ty) - playerY * playerY + (offset.z + tz) * (offset.z + tz) - playerZ * playerZ);
				if (dist < smallestDistance) {
					smallestDistance = dist;
					closetConnection = connection;
				}
			}
			if (closetConnection != null) {
				closetConnection.writeDetailsToNBT(tagCompound);
				itemStack.func_150996_a(closetConnection.getType().getItem());
			}
		}
		if (!tagCompound.hasNoTags()) {
			itemStack.setTagCompound(tagCompound);
		}
		return itemStack;
	}
}
