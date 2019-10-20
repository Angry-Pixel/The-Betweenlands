package thebetweenlands.client.proxy;

import java.io.File;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.gui.GuiItemNaming;
import thebetweenlands.client.gui.GuiLorePage;
import thebetweenlands.client.gui.inventory.GuiAnimator;
import thebetweenlands.client.gui.inventory.GuiBLDualFurnace;
import thebetweenlands.client.gui.inventory.GuiBLFurnace;
import thebetweenlands.client.gui.inventory.GuiCenser;
import thebetweenlands.client.gui.inventory.GuiDruidAltar;
import thebetweenlands.client.gui.inventory.GuiMortar;
import thebetweenlands.client.gui.inventory.GuiPouch;
import thebetweenlands.client.gui.inventory.GuiPurifier;
import thebetweenlands.client.gui.inventory.GuiTarBarrel;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.client.gui.menu.GuiBLMainMenu;
import thebetweenlands.client.gui.menu.GuiDownloadTerrainBetweenlands;
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
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.client.render.entity.RenderAngler;
import thebetweenlands.client.render.entity.RenderAngryPebble;
import thebetweenlands.client.render.entity.RenderAshSprite;
import thebetweenlands.client.render.entity.RenderBLArrow;
import thebetweenlands.client.render.entity.RenderBarrishee;
import thebetweenlands.client.render.entity.RenderBlindCaveFish;
import thebetweenlands.client.render.entity.RenderBloodSnail;
import thebetweenlands.client.render.entity.RenderBoulderSprite;
import thebetweenlands.client.render.entity.RenderCCGroundSpawner;
import thebetweenlands.client.render.entity.RenderChiromaw;
import thebetweenlands.client.render.entity.RenderCryptCrawler;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.entity.RenderDarkLight;
import thebetweenlands.client.render.entity.RenderDecayPitTarget;
import thebetweenlands.client.render.entity.RenderDragonFly;
import thebetweenlands.client.render.entity.RenderDreadfulMummy;
import thebetweenlands.client.render.entity.RenderElixir;
import thebetweenlands.client.render.entity.RenderEmberlingShaman;
import thebetweenlands.client.render.entity.RenderEmberling;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.entity.RenderFlameJet;
import thebetweenlands.client.render.entity.RenderFortressBoss;
import thebetweenlands.client.render.entity.RenderFortressBossBlockade;
import thebetweenlands.client.render.entity.RenderFortressBossProjectile;
import thebetweenlands.client.render.entity.RenderFortressBossSpawner;
import thebetweenlands.client.render.entity.RenderFortressBossTeleporter;
import thebetweenlands.client.render.entity.RenderFortressBossTurret;
import thebetweenlands.client.render.entity.RenderFrog;
import thebetweenlands.client.render.entity.RenderGalleryFrame;
import thebetweenlands.client.render.entity.RenderGasCloud;
import thebetweenlands.client.render.entity.RenderGecko;
import thebetweenlands.client.render.entity.RenderGiantToad;
import thebetweenlands.client.render.entity.RenderGrapplingHookNode;
import thebetweenlands.client.render.entity.RenderGreebling;
import thebetweenlands.client.render.entity.RenderLargeSludgeWorm;
import thebetweenlands.client.render.entity.RenderLeech;
import thebetweenlands.client.render.entity.RenderLurker;
import thebetweenlands.client.render.entity.RenderLurkerSkinRaft;
import thebetweenlands.client.render.entity.RenderMireSnail;
import thebetweenlands.client.render.entity.RenderMireSnailEgg;
import thebetweenlands.client.render.entity.RenderMovingSpawnerHole;
import thebetweenlands.client.render.entity.RenderMovingWall;
import thebetweenlands.client.render.entity.RenderMummyArm;
import thebetweenlands.client.render.entity.RenderPeatMummy;
import thebetweenlands.client.render.entity.RenderPredatorArrowGuide;
import thebetweenlands.client.render.entity.RenderPyrad;
import thebetweenlands.client.render.entity.RenderPyradFlame;
import thebetweenlands.client.render.entity.RenderRootGrabber;
import thebetweenlands.client.render.entity.RenderRootSprite;
import thebetweenlands.client.render.entity.RenderRopeNode;
import thebetweenlands.client.render.entity.RenderSapSpit;
import thebetweenlands.client.render.entity.RenderShambler;
import thebetweenlands.client.render.entity.RenderShockwaveBlock;
import thebetweenlands.client.render.entity.RenderShockwaveSwordItem;
import thebetweenlands.client.render.entity.RenderSiltCrab;
import thebetweenlands.client.render.entity.RenderSludge;
import thebetweenlands.client.render.entity.RenderSludgeBall;
import thebetweenlands.client.render.entity.RenderSludgeJet;
import thebetweenlands.client.render.entity.RenderSludgeWallJet;
import thebetweenlands.client.render.entity.RenderSludgeWorm;
import thebetweenlands.client.render.entity.RenderSmollSludge;
import thebetweenlands.client.render.entity.RenderSnailPoisonJet;
import thebetweenlands.client.render.entity.RenderSpikeWave;
import thebetweenlands.client.render.entity.RenderSpiritTreeFaceLarge;
import thebetweenlands.client.render.entity.RenderSpiritTreeFaceMask;
import thebetweenlands.client.render.entity.RenderSpiritTreeFaceSmall;
import thebetweenlands.client.render.entity.RenderSplodeshroom;
import thebetweenlands.client.render.entity.RenderSporeJet;
import thebetweenlands.client.render.entity.RenderSporeling;
import thebetweenlands.client.render.entity.RenderSwampHag;
import thebetweenlands.client.render.entity.RenderSwordEnergy;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.entity.RenderTarminion;
import thebetweenlands.client.render.entity.RenderTermite;
import thebetweenlands.client.render.entity.RenderThrownTarminion;
import thebetweenlands.client.render.entity.RenderTinySludgeWorm;
import thebetweenlands.client.render.entity.RenderTinyWormEggSac;
import thebetweenlands.client.render.entity.RenderTriggeredFallingBlock;
import thebetweenlands.client.render.entity.RenderTriggeredSludgeWallJet;
import thebetweenlands.client.render.entity.RenderVolarkite;
import thebetweenlands.client.render.entity.RenderVolatileSoul;
import thebetweenlands.client.render.entity.RenderWallLamprey;
import thebetweenlands.client.render.entity.RenderWallLivingRoot;
import thebetweenlands.client.render.entity.RenderWeedwoodRowboat;
import thebetweenlands.client.render.entity.RenderWight;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.client.render.sky.RiftVariant;
import thebetweenlands.client.render.tile.RenderAlembic;
import thebetweenlands.client.render.tile.RenderAnimator;
import thebetweenlands.client.render.tile.RenderAspectVial;
import thebetweenlands.client.render.tile.RenderAspectrusCrop;
import thebetweenlands.client.render.tile.RenderBeamOrigin;
import thebetweenlands.client.render.tile.RenderCenser;
import thebetweenlands.client.render.tile.RenderChestBetweenlands;
import thebetweenlands.client.render.tile.RenderCompostBin;
import thebetweenlands.client.render.tile.RenderDecayPitControl;
import thebetweenlands.client.render.tile.RenderDecayPitGroundChain;
import thebetweenlands.client.render.tile.RenderDecayPitHangingChain;
import thebetweenlands.client.render.tile.RenderDruidAltar;
import thebetweenlands.client.render.tile.RenderDungeonDoorCombination;
import thebetweenlands.client.render.tile.RenderDungeonDoorRunes;
import thebetweenlands.client.render.tile.RenderGeckoCage;
import thebetweenlands.client.render.tile.RenderGroundItem;
import thebetweenlands.client.render.tile.RenderInfuser;
import thebetweenlands.client.render.tile.RenderItemCage;
import thebetweenlands.client.render.tile.RenderItemShelf;
import thebetweenlands.client.render.tile.RenderItemStackAsTileEntity;
import thebetweenlands.client.render.tile.RenderLivingWeedwoodShield;
import thebetweenlands.client.render.tile.RenderMudBrickSpikeTrap;
import thebetweenlands.client.render.tile.RenderMudFlowerPot;
import thebetweenlands.client.render.tile.RenderMudTilesSpikeTrap;
import thebetweenlands.client.render.tile.RenderPestleAndMortar;
import thebetweenlands.client.render.tile.RenderPossessedBlock;
import thebetweenlands.client.render.tile.RenderPurifier;
import thebetweenlands.client.render.tile.RenderRepeller;
import thebetweenlands.client.render.tile.RenderSpawnerBetweenlands;
import thebetweenlands.client.render.tile.RenderSpikeTrap;
import thebetweenlands.client.render.tile.RenderBarrel;
import thebetweenlands.client.render.tile.RenderTarLootPot1;
import thebetweenlands.client.render.tile.RenderTarLootPot2;
import thebetweenlands.client.render.tile.RenderTarLootPot3;
import thebetweenlands.client.render.tile.RenderWaystone;
import thebetweenlands.client.render.tile.RenderWeedwoodSign;
import thebetweenlands.client.render.tile.RenderWeedwoodWorkbench;
import thebetweenlands.client.render.tile.RenderWisp;
import thebetweenlands.client.render.tile.TileEntityPuffshroomRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.entity.EntityLurkerSkinRaft;
import thebetweenlands.common.entity.EntityMovingWall;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySpikeWave;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.entity.EntityTriggeredSludgeWallJet;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.entity.mobs.EntityDarkLight;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityEmberlingShaman;
import thebetweenlands.common.entity.mobs.EntityEmberling;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFlameJet;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.common.entity.mobs.EntityFortressBossProjectile;
import thebetweenlands.common.entity.mobs.EntityFortressBossSpawner;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.common.entity.mobs.EntityFortressBossTurret;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityGreebling;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntityMovingSpawnerHole;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntityPyradFlame;
import thebetweenlands.common.entity.mobs.EntityRootSprite;
import thebetweenlands.common.entity.mobs.EntityShambler;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySludgeJet;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.entity.mobs.EntitySporeJet;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTamedSpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.entity.projectiles.EntityPredatorArrowGuide;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.handler.ExtendedReachHandler;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.HLEntryRegistry;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerPouch;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.item.misc.ItemBarkAmulet;
import thebetweenlands.common.item.shields.ItemSwatShield;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.lib.ModInfo;
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
import thebetweenlands.common.tile.TileEntityBarrel;
import thebetweenlands.common.tile.TileEntityBeamOrigin;
import thebetweenlands.common.tile.TileEntityCenser;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDecayPitControl;
import thebetweenlands.common.tile.TileEntityDecayPitGroundChain;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudTilesSpikeTrap;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPuffshroom;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityTarLootPot1;
import thebetweenlands.common.tile.TileEntityTarLootPot2;
import thebetweenlands.common.tile.TileEntityTarLootPot3;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.common.world.event.EventSpoopy;
import thebetweenlands.common.world.event.EventWinter;
import thebetweenlands.util.GLUProjection;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {
	public static Render<EntityDragonFly> dragonFlyRenderer;

	private final List<RiftVariant> riftVariants = new ArrayList<>();
	  
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
				String name = item.hasDisplayName() ? item.getDisplayName(): I18n.format("container.bl.lurker_skin_pouch");
				return new GuiPouch(new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (item.getItemDamage() * 9), name)));
			}
			break;
		}

		case GUI_LURKER_POUCH_KEYBIND: {
			ItemStack item = ItemLurkerSkinPouch.getFirstPouch(player);
			if(!item.isEmpty()) {
				String name = item.hasDisplayName() ? item.getDisplayName(): I18n.format("container.bl.lurker_skin_pouch");
				return new GuiPouch(new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (item.getItemDamage() * 9), name)));
			}
		}

		case GUI_ITEM_RENAMING:
			if(!player.getHeldItemMainhand().isEmpty()) {
				return new GuiItemNaming(player, x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
			}
			break;

		case GUI_LORE:
			return new GuiLorePage(player.getHeldItem(x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
			
		case GUI_CENSER:
			if (tile instanceof TileEntityCenser) {
				return new GuiCenser(player.inventory, (TileEntityCenser) tile);
			}
			break;
			
		case GUI_BARREL:
			if (tile instanceof TileEntityBarrel) {
				return new GuiTarBarrel(player.inventory, (TileEntityBarrel) tile);
			}
			break;
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
		if (item instanceof ItemRegistry.ICustomMeshCallback) {
			ModelLoader.setCustomMeshDefinition(item, ((ItemRegistry.ICustomMeshCallback) item).getMeshDefinition());
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
		RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHookNode.class, RenderGrapplingHookNode::new);
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
        RenderingRegistry.registerEntityRenderingHandler(EntitySporeJet.class, RenderSporeJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySmollSludge.class, RenderSmollSludge::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGreebling.class, RenderGreebling::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVolarkite.class, RenderVolarkite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBoulderSprite.class, RenderBoulderSprite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritTreeFaceSmall.class, RenderSpiritTreeFaceSmall::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritTreeFaceLarge.class, RenderSpiritTreeFaceLarge::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTamedSpiritTreeFace.class, RenderSpiritTreeFaceSmall::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySapSpit.class, RenderSapSpit::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpikeWave.class, RenderSpikeWave::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRootGrabber.class, RenderRootGrabber::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiritTreeFaceMask.class, RenderSpiritTreeFaceMask::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRootSprite.class, RenderRootSprite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySludgeWorm.class, RenderSludgeWorm::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLargeSludgeWorm.class, RenderLargeSludgeWorm::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTinySludgeWorm.class, RenderTinySludgeWorm::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTinyWormEggSac.class, RenderTinyWormEggSac::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLurkerSkinRaft.class, RenderLurkerSkinRaft::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShambler.class, RenderShambler::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWallLamprey.class, RenderWallLamprey::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWallLivingRoot.class, RenderWallLivingRoot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMovingSpawnerHole.class, RenderMovingSpawnerHole::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCryptCrawler.class, RenderCryptCrawler::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBarrishee.class, RenderBarrishee::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAshSprite.class, RenderAshSprite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDecayPitTarget.class, RenderDecayPitTarget::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySludgeJet.class, RenderSludgeJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTriggeredFallingBlock.class, RenderTriggeredFallingBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySludgeWallJet.class, RenderSludgeWallJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTriggeredSludgeWallJet.class, RenderTriggeredSludgeWallJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySplodeshroom.class, RenderSplodeshroom::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPredatorArrowGuide.class, RenderPredatorArrowGuide::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCCGroundSpawner.class, RenderCCGroundSpawner::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEmberlingShaman.class, RenderEmberlingShaman::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlameJet.class, RenderFlameJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMovingWall.class, RenderMovingWall::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEmberling.class, RenderEmberling::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGalleryFrame.class, RenderGalleryFrame::new);

		//Tile entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new RenderPurifier());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new RenderDruidAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodWorkbench.class, new RenderWeedwoodWorkbench());
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPuffshroom.class, new TileEntityPuffshroomRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaystone.class, new RenderWaystone());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDungeonDoorRunes.class, new RenderDungeonDoorRunes());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDungeonDoorCombination.class, new RenderDungeonDoorCombination());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMudBricksSpikeTrap.class, new RenderMudBrickSpikeTrap());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMudTilesSpikeTrap.class, new RenderMudTilesSpikeTrap());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGroundItem.class, new RenderGroundItem());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBeamOrigin.class, new RenderBeamOrigin());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecayPitControl.class, new RenderDecayPitControl());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecayPitHangingChain.class, new RenderDecayPitHangingChain());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCenser.class, new RenderCenser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrel.class, new RenderBarrel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecayPitGroundChain.class, new RenderDecayPitGroundChain());
		
		IReloadableResourceManager resourceManager = ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager());
		resourceManager.registerReloadListener(ShaderHelper.INSTANCE);
		resourceManager.registerReloadListener(new FoodSickness.ResourceReloadListener());
		resourceManager.registerReloadListener(r -> {
			if (HLEntryRegistry.CATEGORIES.size() > 0) HLEntryRegistry.init();
		});
		resourceManager.registerReloadListener(this);

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
		File galleryFolder = new File(new File(Minecraft.getMinecraft().gameDir, "betweenlands_gallery"), "gallery_" + ModInfo.GALLERY_VERSION);
		galleryFolder.mkdirs();
		GalleryManager.INSTANCE.checkAndUpdate(galleryFolder);
		
	    RenderManager mgr = Minecraft.getMinecraft().getRenderManager();
		dragonFlyRenderer = mgr.getEntityClassRenderObject(EntityDragonFly.class);
        MinecraftForge.EVENT_BUS.register(mgr.getEntityClassRenderObject(EntityWeedwoodRowboat.class));

        Item.getItemFromBlock(BlockRegistry.WEEDWOOD_CHEST).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityChestBetweenlands.class));
		Item.getItemFromBlock(BlockRegistry.DRUID_ALTAR).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityDruidAltar.class));
		Item.getItemFromBlock(BlockRegistry.COMPOST_BIN).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityCompostBin.class));
		Item.getItemFromBlock(BlockRegistry.PURIFIER).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityPurifier.class));
		Item.getItemFromBlock(BlockRegistry.MOB_SPAWNER).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityMobSpawnerBetweenlands.class));
		Item.getItemFromBlock(BlockRegistry.SPIKE_TRAP).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntitySpikeTrap.class));
		Item.getItemFromBlock(BlockRegistry.POSSESSED_BLOCK).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityPossessedBlock.class));
		Item.getItemFromBlock(BlockRegistry.ITEM_CAGE).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityItemCage.class));
		Item.getItemFromBlock(BlockRegistry.GECKO_CAGE).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityGeckoCage.class));
		Item.getItemFromBlock(BlockRegistry.INFUSER).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityInfuser.class));
		Item.getItemFromBlock(BlockRegistry.MORTAR).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityMortar.class));
		Item.getItemFromBlock(BlockRegistry.ANIMATOR).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityAnimator.class));
		Item.getItemFromBlock(BlockRegistry.ALEMBIC).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityAlembic.class));
		Item.getItemFromBlock(BlockRegistry.ITEM_SHELF).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityItemShelf.class));
		Item.getItemFromBlock(BlockRegistry.REPELLER).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityRepeller.class));
		Item.getItemFromBlock(BlockRegistry.TAR_LOOT_POT).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(renderer -> {
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				renderer.add(EnumLootPot.POT_1.getMetadata(facing), TileEntityTarLootPot1.class);
				renderer.add(EnumLootPot.POT_2.getMetadata(facing), TileEntityTarLootPot2.class);
				renderer.add(EnumLootPot.POT_3.getMetadata(facing), TileEntityTarLootPot3.class);
			}
		}));
		ItemRegistry.LIVING_WEEDWOOD_SHIELD.setTileEntityItemStackRenderer(new RenderLivingWeedwoodShield());
		Item.getItemFromBlock(BlockRegistry.WAYSTONE).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityWaystone.class));
		Item.getItemFromBlock(BlockRegistry.WEEDWOOD_CHEST).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityChestBetweenlands.class));
		Item.getItemFromBlock(BlockRegistry.CENSER).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityCenser.class));
		Item.getItemFromBlock(BlockRegistry.WEEDWOOD_BARREL).setTileEntityItemStackRenderer(new RenderItemStackAsTileEntity(TileEntityBarrel.class));
		
		//Block colors
		for (Block block : BlockRegistry.BLOCKS) {
			if (block instanceof ITintedBlock) {
				final ITintedBlock tintedBlock = (ITintedBlock) block;
				if (Item.getItemFromBlock(block) != Items.AIR) {
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> tintedBlock.getColorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex), block);
				}
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

		//WeedwoodRowboatHandler.INSTANCE.init();
		WeedwoodRowboatHandler.INSTANCE.init();
		
		this.loadRiftVariants();

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
  //      MinecraftForge.EVENT_BUS.register(WeedwoodRowboatHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(OverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(ElixirClientHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GuiDownloadTerrainBetweenlands.class);
        MinecraftForge.EVENT_BUS.register(ItemBLBow.class);
        MinecraftForge.EVENT_BUS.register(ItemSwatShield.class);
        MinecraftForge.EVENT_BUS.register(EventWinter.class);
        MinecraftForge.EVENT_BUS.register(EventSpoopy.class);
        MinecraftForge.EVENT_BUS.register(ArmSwingSpeedHandler.class);
        MinecraftForge.EVENT_BUS.register(BLSkyRenderer.class);
        MinecraftForge.EVENT_BUS.register(ItemBarkAmulet.class);
        MinecraftForge.EVENT_BUS.register(RenderGrapplingHookNode.class);
        MinecraftForge.EVENT_BUS.register(ExtendedReachHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderVolarkite.class);
        MinecraftForge.EVENT_BUS.register(GalleryManager.class);
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
	public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ) {
		Particle fx = null;

		if (particleName.equals("spell"))
			fx = new ParticleSpell.Factory().createParticle(EnumParticleTypes.SPELL.getParticleID(), world, x, y, z, vecX, vecY, vecZ, 0);

		if (particleName.equals("flame"))
			fx = new ParticleFlame.Factory().createParticle(EnumParticleTypes.FLAME.getParticleID(), world, x, y, z, vecX, vecY, vecZ, 0);

		if (particleName.equals("egg_sac")) {
			fx = new ParticleBreaking.SnowballFactory().createParticle(EnumParticleTypes.SNOWBALL.getParticleID(), world, x, y, z, vecX, vecY, vecZ, 0);
			fx.setRBGColorF(48F, 64F, 91F);
		}

		if (particleName.equals("splode_shroom")) {
			fx = new ParticleBreaking.SnowballFactory().createParticle(EnumParticleTypes.SNOWBALL.getParticleID(), world, x, y, z, vecX, vecY, vecZ, 0);
			fx.setRBGColorF(128F, 203F, 175F);
		}

		if (fx != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

    @Override
    public Proxy getNetProxy() {
    	return Minecraft.getMinecraft().getProxy();
    }
    
    @Override
    public boolean isSingleplayer() {
    	return Minecraft.getMinecraft().isSingleplayer();
    }

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.loadRiftVariants();
	}
	
	private void loadRiftVariants() {
		this.riftVariants.clear();
		
		try {
			IResource riftsFile = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(ModInfo.ID, "textures/sky/rifts/rifts.json"));
			JsonParser parser = new JsonParser();
			try(InputStreamReader reader = new InputStreamReader(riftsFile.getInputStream())) {
				JsonArray array = parser.parse(reader).getAsJsonArray();
				for(int i = 0; i < array.size(); i++) {
					JsonObject jsonObj = array.get(i).getAsJsonObject();
					
					ResourceLocation textureMask = new ResourceLocation(JsonUtils.getString(jsonObj, "texture_mask"));
					textureMask = new ResourceLocation(textureMask.getNamespace(), "textures/" + textureMask.getPath());
					
					ResourceLocation textureOverlay = new ResourceLocation(JsonUtils.getString(jsonObj, "texture_overlay"));
					textureOverlay = new ResourceLocation(textureMask.getNamespace(), "textures/" + textureOverlay.getPath());
					
					ResourceLocation textureAltOverlay = null;
					if(jsonObj.has("texture_alt_overlay")) {
						textureAltOverlay = new ResourceLocation(JsonUtils.getString(jsonObj, "texture_alt_overlay"));
						textureAltOverlay = new ResourceLocation(textureAltOverlay.getNamespace(), "textures/" + textureAltOverlay.getPath());
					}
					
					JsonObject yawJson = JsonUtils.getJsonObject(jsonObj, "yaw");
					JsonObject pitchJson = JsonUtils.getJsonObject(jsonObj, "pitch");
					JsonObject rollJson = JsonUtils.getJsonObject(jsonObj, "roll");
					JsonObject scaleJson = JsonUtils.getJsonObject(jsonObj, "scale");
					JsonObject mirrorJson = JsonUtils.getJsonObject(jsonObj, "mirror");
					
					this.riftVariants.add(new RiftVariant(textureMask, textureOverlay, textureAltOverlay,
							JsonUtils.getFloat(yawJson, "min"), JsonUtils.getFloat(yawJson, "max"),
							JsonUtils.getFloat(pitchJson, "min"), JsonUtils.getFloat(pitchJson, "max"),
							JsonUtils.getFloat(rollJson, "min"), JsonUtils.getFloat(rollJson, "max"),
							JsonUtils.getFloat(scaleJson, "min"), JsonUtils.getFloat(scaleJson, "max"),
							JsonUtils.getBoolean(mirrorJson, "u"), JsonUtils.getBoolean(mirrorJson, "v")));
				}
			}
		} catch(Exception ex) {
			TheBetweenlands.logger.error("Failed loading sky rift variants", ex);
		}
	}
	
	@Override
	public List<RiftVariant> getRiftVariants() {
		return Collections.unmodifiableList(this.riftVariants);
	}
}
