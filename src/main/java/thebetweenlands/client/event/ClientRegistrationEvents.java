package thebetweenlands.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.client.*;
import thebetweenlands.client.extensions.effect.ElixirEffectExtension;
import thebetweenlands.client.extensions.effect.InvisibleEffectRegistration;
import thebetweenlands.client.extensions.item.BigSwingExtension;
import thebetweenlands.client.extensions.item.armor.*;
import thebetweenlands.client.gui.overlay.*;
import thebetweenlands.client.gui.overlay.swarm.SwarmOverlay;
import thebetweenlands.client.gui.screen.*;
import thebetweenlands.client.handler.equipment.RadialMenuHandler;
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.client.model.armor.*;
import thebetweenlands.client.model.baked.RootGeometry;
import thebetweenlands.client.model.baked.barnacle.BarnacleModelLoader;
import thebetweenlands.client.model.baked.blackhatmushroom1.BlackHatMushroom1ModelLoader;
import thebetweenlands.client.model.baked.blackhatmushroom2.BlackHatMushroom2ModelLoader;
import thebetweenlands.client.model.baked.blackhatmushroom3.BlackHatMushroom3ModelLoader;
import thebetweenlands.client.model.baked.brazier.BrazierModelLoader;
import thebetweenlands.client.model.baked.bulbcappedmushroom.BulbCappedMushroomModelLoader;
import thebetweenlands.client.model.baked.bush.BushModelLoader;
import thebetweenlands.client.model.baked.connectedtextures.ConnectedTextureGeometry;
import thebetweenlands.client.model.baked.custom.CustomElementsModel;
import thebetweenlands.client.model.baked.dungeonwallcandle.DungeonWallCandleModelLoader;
import thebetweenlands.client.model.baked.flatheadmushroom1.FlatHeadMushroom1ModelLoader;
import thebetweenlands.client.model.baked.flatheadmushroom2.FlatHeadMushroom2ModelLoader;
import thebetweenlands.client.model.baked.funguscrop.FungusCropModelLoader;
import thebetweenlands.client.model.baked.paperlantern.PaperLanternModelLoader;
import thebetweenlands.client.model.baked.pitcherplant.PitcherPlantModelLoader;
import thebetweenlands.client.model.baked.siltglasslantern.SiltGlassLanternModelLoader;
import thebetweenlands.client.model.baked.slant.SlantModelLoader;
import thebetweenlands.client.model.baked.sundew.SundewModelLoader;
import thebetweenlands.client.model.baked.swampplant.SwampPlantModelLoader;
import thebetweenlands.client.model.baked.venusflytrap.VenusFlyTrapModelLoader;
import thebetweenlands.client.model.baked.volarpad.VolarpadModelLoader;
import thebetweenlands.client.model.baked.walkway.WalkwayModelLoader;
import thebetweenlands.client.model.baked.weepingblue.WeepingBlueModelLoader;
import thebetweenlands.client.model.baked.whitepearcrop.WhitePearCropModelLoader;
import thebetweenlands.client.model.baked.woodensupportbeam1.WoodenSupportBeam1ModelLoader;
import thebetweenlands.client.model.baked.woodensupportbeam2.WoodenSupportBeam2ModelLoader;
import thebetweenlands.client.model.baked.woodensupportbeam3.WoodenSupportBeam3ModelLoader;
import thebetweenlands.client.model.block.*;
import thebetweenlands.client.model.block.aspectrus.AspectrusCrop1Model;
import thebetweenlands.client.model.block.aspectrus.AspectrusCrop2Model;
import thebetweenlands.client.model.block.aspectrus.AspectrusCrop3Model;
import thebetweenlands.client.model.block.aspectrus.AspectrusCrop4Model;
import thebetweenlands.client.model.block.cage.CagedGeckoModel;
import thebetweenlands.client.model.block.cage.GeckoCageModel;
import thebetweenlands.client.model.block.simulacrum.DeepmanSimulacrumModels;
import thebetweenlands.client.model.block.simulacrum.LakeCavernSimulacrumModels;
import thebetweenlands.client.model.block.simulacrum.RootmanSimulacrumModels;
import thebetweenlands.client.model.entity.*;
import thebetweenlands.client.model.entity.rowboat.HumanoidRowerModel;
import thebetweenlands.client.model.entity.rowboat.PlayerRowerModel;
import thebetweenlands.client.model.entity.rowboat.RowboatLanternModel;
import thebetweenlands.client.model.entity.rowboat.WeedwoodRowboatModel;
import thebetweenlands.client.model.entity.volarkite.VolarkiteModel;
import thebetweenlands.client.model.item.*;
import thebetweenlands.client.particle.*;
import thebetweenlands.client.renderer.BLItemRenderer;
import thebetweenlands.client.renderer.block.*;
import thebetweenlands.client.renderer.entity.*;
import thebetweenlands.client.renderer.entity.rowboat.WeedwoodRowboatRenderer;
import thebetweenlands.client.renderer.entity.volarkite.VolarkiteRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.PresentBlock;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.component.item.ShockwaveSwordData;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;
import thebetweenlands.common.fluid.BasicFluidTypeExtension;
import thebetweenlands.common.fluid.ColoredFluidTypeExtension;
import thebetweenlands.common.fluid.SwampWaterFluidTypeExtension;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.misc.AnadiaMobItem;
import thebetweenlands.common.item.misc.BLItemFrameItem;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.item.misc.bucket.InfusionBucketItem;
import thebetweenlands.common.item.shield.SwatShieldItem;
import thebetweenlands.common.item.tool.WeedwoodBowItem;
import thebetweenlands.common.registries.*;
import thebetweenlands.util.BLDyeColor;
import thebetweenlands.util.BipedTextureUVs;
import thebetweenlands.util.DrinkableBrew;

import java.io.File;

public class ClientRegistrationEvents {

	public static final ResourceLocation DECAY_METER_OVERLAY_LAYER = TheBetweenlands.prefix("decay_meter");
	public static final ResourceLocation GUNK_METER_OVERLAY_LAYER = TheBetweenlands.prefix("gunk_meter");

	public static RiftVariantReloadListener riftVariantListener;
	public static AspectIconTextureManager aspectIcons;
	public static CircleGemTextureManager circleGems;

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientRegistrationEvents::clientSetup);
		eventbus.addListener(ClientRegistrationEvents::registerKeybinds);
		eventbus.addListener(ClientRegistrationEvents::registerScreens);
		eventbus.addListener(ClientRegistrationEvents::registerRenderers);
		eventbus.addListener(ClientRegistrationEvents::registerDimEffects);
		eventbus.addListener(ClientRegistrationEvents::registerExtensions);
		eventbus.addListener(ClientRegistrationEvents::registerLayerDefinition);
		eventbus.addListener(ClientRegistrationEvents::registerParticleSprites);
		eventbus.addListener(ClientRegistrationEvents::registerBlockColors);
		eventbus.addListener(ClientRegistrationEvents::registerReloadListeners);
		eventbus.addListener(ClientRegistrationEvents::registerGeometryLoaders);
		eventbus.addListener(ClientRegistrationEvents::registerSpecialModels);
		eventbus.addListener(ClientRegistrationEvents::registerPropertyOverrides);
		eventbus.addListener(ClientRegistrationEvents::registerOverlays);
		eventbus.addListener(ClientRegistrationEvents::registerItemColors);
		eventbus.addListener(ClientRegistrationEvents::corrosiveBootsLayer);
		ClientEvents.init();

		eventbus.addListener(BetweenlandsShaders::registerShaders);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		File galleryFolder = new File(new File(Minecraft.getInstance().gameDirectory, "betweenlands_gallery"), "gallery_" + TheBetweenlands.GALLERY_VERSION);
		galleryFolder.mkdirs();
		GalleryManager.INSTANCE.checkAndUpdate(galleryFolder);
		AmbienceRegistry.init();

		event.enqueueWork(() -> {
			ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
		});
	}

	private static void registerKeybinds(final RegisterKeyMappingsEvent event) {
		event.register(BetweenlandsKeybinds.CONNECT_CAVING_ROPE);
		event.register(BetweenlandsKeybinds.OPEN_POUCH);
		event.register(BetweenlandsKeybinds.RADIAL_MENU);
		event.register(BetweenlandsKeybinds.USE_RING);
		event.register(BetweenlandsKeybinds.USE_SECONDARY_RING);
	}

	private static void registerOverlays(final RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, TheBetweenlands.prefix("item_equipment"), EquipmentOverlay::renderEquipment);
		event.registerAbove(VanillaGuiLayers.HOTBAR, TheBetweenlands.prefix("radial_equipment_menu"), RadialMenuHandler.INSTANCE::renderRadialMenu);
		event.registerAbove(VanillaGuiLayers.AIR_LEVEL, DECAY_METER_OVERLAY_LAYER, DecayBarOverlay::renderDecayBar);
		event.registerAbove(DECAY_METER_OVERLAY_LAYER, GUNK_METER_OVERLAY_LAYER, GunkOverlay::renderGunkBar);
		event.registerAboveAll(TheBetweenlands.prefix("fishing_minigame"), FishStaminaBarOverlay::renderFishingHud);
		event.registerAboveAll(TheBetweenlands.prefix("swarm"), SwarmOverlay.INSTANCE::renderSwarm);
		event.registerAboveAll(TheBetweenlands.prefix("april_fools"), AprilFoolsOverlay.INSTANCE::renderAprilFools);
	}

	private static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(MenuRegistry.AMPHIBIOUS_ARMOR.get(), AmphibiousArmorScreen::new);
		event.register(MenuRegistry.ANIMATOR.get(), AnimatorScreen::new);
		event.register(MenuRegistry.BARREL.get(), BarrelScreen::new);
		event.register(MenuRegistry.CENSER.get(), CenserScreen::new);
		event.register(MenuRegistry.CRAB_POT_FILTER.get(), CrabPotFilterScreen::new);
		event.register(MenuRegistry.DRUID_ALTAR.get(), DruidAltarScreen::new);
		event.register(MenuRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxScreen::new);
		event.register(MenuRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableScreen::new);
		event.register(MenuRegistry.LURKER_SKIN_POUCH.get(), LurkerSkinPouchScreen::new);
		event.register(MenuRegistry.MORTAR.get(), MortarScreen::new);
		event.register(MenuRegistry.PURIFIER.get(), PurifierScreen::new);
		event.register(MenuRegistry.SILK_BUNDLE.get(), SilkBundleScreen::new);
		event.register(MenuRegistry.SMOKING_RACK.get(), SmokingRackScreen::new);
	}

	private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.SWAMP_HAG.get(), SwampHagRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GECKO.get(), GeckoRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WIGHT.get(), WightRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BUBBLER_CRAB.get(), BubblerCrabRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SILT_CRAB.get(), SiltCrabRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANADIA.get(), AnadiaRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANGLER.get(), AnglerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FISH_HOOK.get(), BLFishHookRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SEAT.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ELIXIR.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANGRY_PEBBLE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ITEM_FRAME.get(), BLItemFrameRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BETWEENSTONE_PEBBLE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FISH_BAIT.get(), ItemEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FISH_VORTEX.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.URCHIN_SPIKE.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ELECTRIC_SHOCK.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SAP_SPIT.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(EntityRegistry.LURKER_SKIN_RAFT.get(), LurkerSkinRaftRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANGLER_TOOTH_ARROW.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/angler_tooth_arrow.png")));
		event.registerEntityRenderer(EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/poisoned_angler_tooth_arrow.png")));
		event.registerEntityRenderer(EntityRegistry.BASILISK_ARROW.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/basilisk_arrow.png")));
		event.registerEntityRenderer(EntityRegistry.SHOCK_ARROW.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/shock_arrow.png")));
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_BARB.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/chiromaw_barb.png")));
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_SHOCK_BARB.get(), context -> new CustomArrowRenderer(context, TheBetweenlands.prefix("textures/entity/arrow/chiromaw_barb.png")));
		event.registerEntityRenderer(EntityRegistry.OCTINE_ARROW.get(), OctineArrowRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_WORM_ARROW.get(), SludgeWormArrowRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PREDATOR_ARROW_GUIDE.get(), PredatorArrowGuideRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_WORM.get(), SludgeWormRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TINY_SLUDGE_WORM.get(), TinySludgeWormRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TINY_SLUDGE_WORM_HELPER.get(), TinySludgeWormRenderer::new);
		event.registerEntityRenderer(EntityRegistry.STALKER.get(), StalkerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PEAT_MUMMY.get(), PeatMummyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.DREADFUL_PEAT_MUMMY.get(), DreadfulPeatMummyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.MUMMY_ARM.get(), MummyArmRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_BALL.get(), SludgeBallRenderer::new);
		event.registerEntityRenderer(EntityRegistry.MIRE_SNAIL.get(), MireSnailRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ASH_SPRITE.get(), AshSpriteRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BARRISHEE.get(), BarrisheeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GREEBLING.get(), GreeblingRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GREEBLING_CORACLE.get(), GreeblingCoracleRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CRYPT_CRAWLER.get(), CryptCrawlerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BIPED_CRYPT_CRAWLER.get(), BipedCryptCrawlerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIEF_CRYPT_CRAWLER.get(), ChiefCryptCrawlerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.OLM.get(), OlmRenderer::new);
		event.registerEntityRenderer(EntityRegistry.EMBERLING.get(), EmberlingRenderer::new);
		event.registerEntityRenderer(EntityRegistry.EMBERLING_SHAMAN.get(), EmberlingShamanRenderer::new);
		event.registerEntityRenderer(EntityRegistry.DRAGONFLY.get(), DragonflyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FIREFLY.get(), FireflyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.JELLYFISH.get(), JellyfishRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CAVE_JELLYFISH.get(), CaveJellyfishRenderer::new);
		event.registerEntityRenderer(EntityRegistry.LURKER.get(), LurkerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SHAMBLER.get(), ShamblerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FRESHWATER_URCHIN.get(), FreshwaterUrchinRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CAVE_FISH.get(), CaveFishRenderer::new);
		event.registerEntityRenderer(EntityRegistry.DARK_DRUID.get(), DarkDruidRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FROG.get(), FrogRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ROOT_SPRITE.get(), RootSpriteRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SHALLOWBREATH.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SPLODESHROOM.get(), SplodeshroomRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SPORELING.get(), SporelingRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TARMINION.get(), TarminionRenderer::new);
		event.registerEntityRenderer(EntityRegistry.THROWN_TARMINION.get(), ThrownTarminionRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE.get(), SludgeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SMOL_SLUDGE.get(), SmolSludgeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GALLERY_FRAME.get(), GalleryFrameRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE.get(), PrimordialMalevolenceRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE_BLOCKADE.get(), PrimordialMalevolenceBlockadeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE_PROJECTILE.get(), PrimordialMalevolenceProjectileRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE_SPAWNER.get(), PrimordialMalevolenceSpawnerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE_TELEPORTER.get(), PrimordialMalevolenceTeleporterRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PRIMORDIAL_MALEVOLENCE_TURRET.get(), PrimordialMalevolenceTurretRenderer::new);
		event.registerEntityRenderer(EntityRegistry.VOLATILE_SOUL.get(), VolatileSoulRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TAR_BEAST.get(), TarBeastRenderer::new);
		event.registerEntityRenderer(EntityRegistry.LARGE_SLUDGE_WORM.get(), LargeSludgeWormRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_WORM_EGG_SAC.get(), SludgeWormEggSacRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIROMAW.get(), ChiromawRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_GREEBLING_RIDER.get(), ChiromawGreeblingRiderRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_MATRIARCH.get(), ChiromawMatriarchRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_DROPPINGS.get(), ChiromawDroppingsRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GREEBLING_VOLARPAD_FLOATER.get(), GreeblingVolarpadFloaterRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CHIROMAW_HATCHLING.get(), ChiromawHatchlingRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TAME_CHIROMAW.get(), TameChiromawRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SHOCKWAVE_BLOCK.get(), ShockwaveBlockRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TERMITE.get(), TermiteRenderer::new);
		event.registerEntityRenderer(EntityRegistry.LEECH.get(), LeechRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BLOOD_SNAIL.get(), BloodSnailRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SNAIL_POISON_JET.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SHOCKWAVE_SWORD_ITEM.get(), ShockwaveSwordItemEntityRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SWORD_ENERGY.get(), SwordEnergyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.INFESTATION.get(), InfestationRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FALSE_XP.get(), ExperienceOrbRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FISHING_SPEAR.get(), context -> new FishingSpearRenderer<>(context, new FishingSpearModel<>(context.bakeLayer(BLModelLayers.FISHING_SPEAR))));
		event.registerEntityRenderer(EntityRegistry.AMPHIBIOUS_FISHING_SPEAR.get(), context -> new FishingSpearRenderer<>(context, new FishingSpearModel<>(context.bakeLayer(BLModelLayers.AMPHIBIOUS_FISHING_SPEAR))));
		event.registerEntityRenderer(EntityRegistry.ROBUST_FISHING_SPEAR.get(), context -> new FishingSpearRenderer<>(context, new FishingSpearModel<>(context.bakeLayer(BLModelLayers.ROBUST_FISHING_SPEAR))));
		event.registerEntityRenderer(EntityRegistry.SPIRIT_TREE_FACE_MASK.get(), SpiritTreeFaceMaskRenderer::new);
		event.registerEntityRenderer(EntityRegistry.DECAY_PIT_TARGET.get(), DecayPitTargetRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_JET.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PYRAD.get(), PyradRenderer::new);
		event.registerEntityRenderer(EntityRegistry.PYRAD_FLAME.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
		event.registerEntityRenderer(EntityRegistry.GREEBLING_CORPSE.get(), GreeblingCorpseRenderer::new);
		event.registerEntityRenderer(EntityRegistry.MOVING_WALL.get(), MovingWallRenderer::new);
		event.registerEntityRenderer(EntityRegistry.TRIGGERED_FALLING_BLOCK.get(), TriggeredFallingBlockRenderer::new);
		event.registerEntityRenderer(EntityRegistry.CC_GROUND_SPAWNER.get(), CCGroundSpawnerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WORM_GROUND_SPAWNER.get(), WormGroundSpawnerRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ROCK_SNOT.get(), RockSnotRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ROCK_SNOT_TENDRIL.get(), RockSnotTendrilRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GLOWING_GOOP_ENTITY.get(), context -> new ThrownItemRenderer<>(context, 3.0F, true));
		event.registerEntityRenderer(EntityRegistry.LIGHTNING_BOLT.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BUBBLER_CRAB_BUBBLE.get(), BubblerCrabBubbleRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WALL_LAMPREY.get(), WallLampreyRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SLUDGE_WALL_JET.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BONE_PUPPET_MELEE.get(), BonePuppetMeleeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BONE_PUPPET_RANGED.get(), BonePuppetRangedRenderer::new);
		event.registerEntityRenderer(EntityRegistry.THROWN_BONE.get(), ThrownBoneRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BONE_SHAMAN.get(), BoneShamanRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SMALL_SPIRIT_TREE_FACE.get(), SmallSpiritTreeFaceRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SMALL_TAMED_SPIRIT_TREE_FACE.get(), SmallSpiritTreeFaceRenderer::new);
		event.registerEntityRenderer(EntityRegistry.LARGE_SPIRIT_TREE_FACE.get(), LargeSpiritTreeFaceRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ROOT_GRABBER.get(), RootGrabberRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SPIKE_WAVE.get(), SpikeWaveRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WATCHER_EYES.get(), WatcherEyesRenderer::new);
		event.registerEntityRenderer(EntityRegistry.BONE_SHAMAN_PROJECTILE.get(), BoneShamanProjectileRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WALL_ROOT.get(), WallRootRenderer::new);
		//Temp for teting
		event.registerEntityRenderer(EntityRegistry.VOLARKITE.get(), VolarkiteRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WEEDWOOD_ROWBOAT.get(), WeedwoodRowboatRenderer::new);

		event.registerBlockEntityRenderer(BlockEntityRegistry.ASPECTRUS_CROP.get(), AspectrusCropRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ASPECT_VIAL.get(), AspectVialRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MUD_BRICK_ALCOVE.get(), AlcoveRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ALEMBIC.get(), AlembicRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ANIMATOR.get(), AnimatorRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.BARREL.get(), BarrelRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.BEAM_ORIGIN.get(), BeamOriginRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CENSER.get(), CenserRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.COMPOST_BIN.get(), CompostBinRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT.get(), CrabPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT_FILTER.get(), CrabPotFilterRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DECAY_PIT_CONTROL.get(), DecayPitControlRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DECAY_PIT_GROUND_CHAIN.get(), DecayPitGroundChainRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DECAY_PIT_HANGING_CHAIN.get(), DecayPitHangingChainRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DRUID_ALTAR.get(), DruidAltarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DUNGEON_DOOR_RUNES.get(), DungeonDoorRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DUNGEON_DOOR_COMBINATION.get(), DungeonDoorCombinationRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FILTERED_SILT_GLASS_JAR.get(), FilteredSiltGlassJarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.GECKO_CAGE.get(), GeckoCageRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.GRUB_HUB.get(), GrubHubRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.INFUSER.get(), InfuserRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ITEM_CAGE.get(), ItemCageRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ITEM_SHELF.get(), ItemShelfRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_POT.get(), LootPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_URN.get(), LootUrnRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MOB_SPAWNER.get(), MobSpawnerRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MORTAR.get(), MortarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MOSS_BED.get(), MossBedRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MOTH_HOUSE.get(), MothHouseRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MUD_FLOWER_POT.get(), MudFlowerPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.OFFERING_TABLE.get(), OfferingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.POSSESSED_BLOCK.get(), PossessedBlockRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.PUFFSHROOM.get(), PuffshroomRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.PURIFIER.get(), PurifierRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.REPELLER.get(), RepellerRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.RUBBER_TAP.get(), RubberTapRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SILT_GLASS_JAR.get(), SiltGlassJarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SIMULACRUM.get(), SimulacrumRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SMOKING_RACK.get(), SmokingRackRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SPIKE_TRAP.get(), SpikeTrapRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.STEEPING_POT.get(), SteepingPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WATER_FILTER.get(), WaterFilterRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WAYSTONE.get(), WaystoneRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WEEDWOOD_CHEST.get(), WeedwoodChestRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WEEDWOOD_CRAFTING_TABLE.get(), WeedwoodCraftingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WIND_CHIME.get(), WindChimeRenderer::new);
		event.registerEntityRenderer(EntityRegistry.MIST_BRIDGE.get(), NoopRenderer::new);
	}

	private static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BLModelLayers.AMPHIBIOUS_ARMOR, AmphibiousArmorModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.BONE_ARMOR, BoneArmorModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.EXPLORERS_HAT, ExplorersHatModel::create);
		event.registerLayerDefinition(BLModelLayers.LARGE_SPIRIT_TREE_MASK, () -> LargeSpiritTreeFaceModel.create(true));
		event.registerLayerDefinition(BLModelLayers.SMALL_SPIRIT_TREE_MASK, () -> SmallSpiritTreeFaceModel.createFace2(true));
		event.registerLayerDefinition(BLModelLayers.SILK_MASK, SilkMaskModel::create);
		event.registerLayerDefinition(BLModelLayers.SYRMORITE_ARMOR, SyrmoriteArmorModel::makeModel);

		event.registerLayerDefinition(BLModelLayers.BONE_SHIELD, BoneShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.DENTROTHYST_SHIELD, DentrothystShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.LURKER_SKIN_SHIELD, LurkerSkinShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.OCTINE_SHIELD, OctineShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.SYRMORITE_SHIELD, SyrmoriteShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.VALONITE_SHIELD, ValoniteShieldModel::create);
		event.registerLayerDefinition(BLModelLayers.WEEDWOOD_SHIELD, WeedwoodShieldModel::create);

		event.registerLayerDefinition(BLModelLayers.SWAMP_HAG, SwampHagModel::createModelLayer);
		event.registerLayerDefinition(BLModelLayers.WIGHT, WightModel::create);
		event.registerLayerDefinition(BLModelLayers.BUBBLER_CRAB, BubblerCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.SILT_CRAB, SiltCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.ANADIA, AnadiaModel::create);
		event.registerLayerDefinition(BLModelLayers.ANGLER, AnglerModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.FISH_HOOK, BLFishHookModel::create);
		event.registerLayerDefinition(BLModelLayers.GREEBLING_1, GreeblingModel::createVariant1);
		event.registerLayerDefinition(BLModelLayers.GREEBLING_2, GreeblingModel::createVariant2);
		event.registerLayerDefinition(BLModelLayers.SLUDGE_WORM_ARROW, SludgeWormArrowModel::create);
		event.registerLayerDefinition(BLModelLayers.SMALL_SPIRIT_TREE_FACE_1, SmallSpiritTreeFaceModel::createFace1);
		event.registerLayerDefinition(BLModelLayers.SMALL_SPIRIT_TREE_FACE_2, () -> SmallSpiritTreeFaceModel.createFace2(false));
		event.registerLayerDefinition(BLModelLayers.SLUDGE_WORM, SludgeWormModel::create);
		event.registerLayerDefinition(BLModelLayers.TINY_SLUDGE_WORM, TinySludgeWormModel::create);
		event.registerLayerDefinition(BLModelLayers.STALKER, StalkerModel::create);
		event.registerLayerDefinition(BLModelLayers.DREADFUL_PEAT_MUMMY, DreadfulPeatMummyModel::create);
		event.registerLayerDefinition(BLModelLayers.PEAT_MUMMY, PeatMummyModel::create);
		event.registerLayerDefinition(BLModelLayers.MUMMY_ARM, MummyArmModel::create);
		event.registerLayerDefinition(BLModelLayers.SLUDGE_BALL, SludgeBallModel::create);
		event.registerLayerDefinition(BLModelLayers.MIRE_SNAIL, MireSnailModel::create);
		event.registerLayerDefinition(BLModelLayers.ASH_SPRITE, AshSpriteModel::create);
		event.registerLayerDefinition(BLModelLayers.BARRISHEE, BarrisheeModel::create);
		event.registerLayerDefinition(BLModelLayers.GREEBLING_CORACLE, GreeblingCoracleModel::create);
		event.registerLayerDefinition(BLModelLayers.BIPED_CRYPT_CRAWLER, BipedCryptCrawlerModel::create);
		event.registerLayerDefinition(BLModelLayers.CRYPT_CRAWLER, CryptCrawlerModel::create);
		event.registerLayerDefinition(BLModelLayers.OLM, OlmModel::create);
		event.registerLayerDefinition(BLModelLayers.EMBERLING, EmberlingModel::create);
		event.registerLayerDefinition(BLModelLayers.EMBERLING_SHAMAN, EmberlingShamanModel::create);
		event.registerLayerDefinition(BLModelLayers.DRAGONFLY, DragonflyModel::create);
		event.registerLayerDefinition(BLModelLayers.FIREFLY, FireflyModel::create);
		event.registerLayerDefinition(BLModelLayers.JELLYFISH, JellyfishModel::create);
		event.registerLayerDefinition(BLModelLayers.JELLYFISH_CAVE, CaveJellyfishModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.LURKER, LurkerModel::create);
		event.registerLayerDefinition(BLModelLayers.SHAMBLER, ShamblerModel::create);
		event.registerLayerDefinition(BLModelLayers.FRESHWATER_URCHIN, FreshwaterUrchinModel::create);
		event.registerLayerDefinition(BLModelLayers.CAVE_FISH, CaveFishModel::create);
		event.registerLayerDefinition(BLModelLayers.DARK_DRUID, DarkDruidModel::create);
		event.registerLayerDefinition(BLModelLayers.FROG, FrogModel::create);
		event.registerLayerDefinition(BLModelLayers.ROOT_SPRITE, RootSpriteModel::create);
		event.registerLayerDefinition(BLModelLayers.SPLODESHROOM, SplodeshroomModel::create);
		event.registerLayerDefinition(BLModelLayers.SPORELING, SporelingModel::create);
		event.registerLayerDefinition(BLModelLayers.TARMINION, TarminionModel::create);
		event.registerLayerDefinition(BLModelLayers.SLUDGE, SludgeModel::create);
		event.registerLayerDefinition(BLModelLayers.SMOL_SLUDGE, SmolSludgeModel::create);
		event.registerLayerDefinition(BLModelLayers.PRIMORDIAL_MALEVOLENCE, PrimordialMalevolenceModel::create);
		event.registerLayerDefinition(BLModelLayers.SWORD_ENERGY, SwordEnergyModel::create);
		event.registerLayerDefinition(BLModelLayers.TAR_BEAST, TarBeastModel::create);
		event.registerLayerDefinition(BLModelLayers.LARGE_SLUDGE_WORM, LargeSludgeWormModel::create);
		event.registerLayerDefinition(BLModelLayers.LARGE_SLUDGE_WORM_OUTER, LargeSludgeWormModel::createSludgeLayer);
		event.registerLayerDefinition(BLModelLayers.SLUDGE_WORM_EGG_SAC, SludgeWormEggSacModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW, ChiromawModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW_GREEBLING_RIDER, ChiromawGreeblingRiderModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW_MATRIARCH, ChiromawMatriarchModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW_DROPPINGS, ChiromawDroppingsModel::create);
		event.registerLayerDefinition(BLModelLayers.GREEBLING_VOLARPAD_FLOATER, GreeblingVolarpadFloaterModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW_HATCHLING, ChiromawHatchlingModel::create);
		event.registerLayerDefinition(BLModelLayers.CHIROMAW_EGG, ChiromawEggModel::create);
		event.registerLayerDefinition(BLModelLayers.TERMITE, TermiteModel::create);
		event.registerLayerDefinition(BLModelLayers.LEECH, LeechModel::create);
		event.registerLayerDefinition(BLModelLayers.BLOOD_SNAIL, BloodSnailModel::create);
		event.registerLayerDefinition(BLModelLayers.INFESTATION, InfestationModel::create);
		event.registerLayerDefinition(BLModelLayers.FISHING_SPEAR, FishingSpearModel::create);
		event.registerLayerDefinition(BLModelLayers.AMPHIBIOUS_FISHING_SPEAR, FishingSpearModel::createFinned);
		event.registerLayerDefinition(BLModelLayers.ROBUST_FISHING_SPEAR, RobustFishingSpearModel::create);
		event.registerLayerDefinition(BLModelLayers.LARGE_SPIRIT_TREE_FACE, () -> LargeSpiritTreeFaceModel.create(false));
		event.registerLayerDefinition(BLModelLayers.PYRAD, PyradModel::create);
		event.registerLayerDefinition(BLModelLayers.GREEBLING_CORPSE, GreeblingCorpseModel::create);
		event.registerLayerDefinition(BLModelLayers.MOVING_WALL, MovingWallModel::create);
		event.registerLayerDefinition(BLModelLayers.ROCK_SNOT, RockSnotModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.ROCK_SNOT_GRABBER, RockSnotGrabberModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.WALL_LAMPREY, WallLampreyModel::create);
		event.registerLayerDefinition(BLModelLayers.WALL_HOLE, WallHoleModel::create);
		event.registerLayerDefinition(BLModelLayers.BONE_PUPPET_MELEE, BonePuppetMeleeModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.BONE_PUPPET_RANGED, BonePuppetRangedModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.THROWN_BONE, ThrownBoneModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.BONE_SHAMAN, BoneShamanModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.WATCHER_EYES, WatcherEyesModel::createBodyLayer);
		event.registerLayerDefinition(BLModelLayers.VOLARKITE, VolarkiteModel::createBodyLayer);

		event.registerLayerDefinition(BLModelLayers.DRAETON_CARRIAGE, DraetonModel::createCarriage);
		event.registerLayerDefinition(BLModelLayers.DRAETON_ANCHOR, DraetonModel::createAnchor);
		event.registerLayerDefinition(BLModelLayers.DRAETON_CRAFTING, DraetonModel::createCraftingUpgrade);
		event.registerLayerDefinition(BLModelLayers.DRAETON_FURNACE, DraetonModel::createFurnaceUpgrade);

		event.registerLayerDefinition(BLModelLayers.WEEDWOOD_ROWBOAT, WeedwoodRowboatModel::createBoat);
		event.registerLayerDefinition(BLModelLayers.WEEDWOOD_ROWBOAT_LANTERN, RowboatLanternModel::createLantern);

		event.registerLayerDefinition(BLModelLayers.PLAYER_ROWER, () -> PlayerRowerModel.create(CubeDeformation.NONE, false, BipedTextureUVs.forPlayer()));
		event.registerLayerDefinition(BLModelLayers.PLAYER_ROWER_OUTER_ARMOR, () -> HumanoidRowerModel.create(LayerDefinitions.OUTER_ARMOR_DEFORMATION, false, BipedTextureUVs.forBasicHumanoid(64, 32)));
		event.registerLayerDefinition(BLModelLayers.PLAYER_ROWER_INNER_ARMOR, () -> HumanoidRowerModel.create(LayerDefinitions.INNER_ARMOR_DEFORMATION, false, BipedTextureUVs.forBasicHumanoid(64, 32)));
		event.registerLayerDefinition(BLModelLayers.SLIM_PLAYER_ROWER, () -> PlayerRowerModel.create(CubeDeformation.NONE, true, BipedTextureUVs.forPlayer()));
		event.registerLayerDefinition(BLModelLayers.SLIM_PLAYER_ROWER_OUTER_ARMOR, () -> HumanoidRowerModel.create(LayerDefinitions.OUTER_ARMOR_DEFORMATION, true, BipedTextureUVs.forBasicHumanoid(64, 32)));
		event.registerLayerDefinition(BLModelLayers.SLIM_PLAYER_ROWER_INNER_ARMOR, () -> HumanoidRowerModel.create(LayerDefinitions.INNER_ARMOR_DEFORMATION, true, BipedTextureUVs.forBasicHumanoid(64, 32)));

		event.registerLayerDefinition(BLModelLayers.CORRUPT_GECKO, CagedGeckoModel::createCorruptGecko);
		event.registerLayerDefinition(BLModelLayers.GECKO, GeckoModel::create);
		event.registerLayerDefinition(BLModelLayers.MUTATED_GECKO, CagedGeckoModel::createMutatedGecko);

		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_1, AspectrusCrop1Model::makeModel);
		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_2, AspectrusCrop2Model::makeModel);
		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_3, AspectrusCrop3Model::makeModel);
		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_3_ASPECT, AspectrusCrop3Model::makeAspectModel);
		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_4, AspectrusCrop4Model::makeModel);
		event.registerLayerDefinition(BLModelLayers.ASPECTRUS_CROP_4_ASPECT, AspectrusCrop4Model::makeAspectModel);

		event.registerLayerDefinition(BLModelLayers.ALCOVE, AlcoveModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ALEMBIC, AlembicModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ANIMATOR, AnimatorModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.BARREL, BarrelModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CENSER, CenserModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.COMPOST_BIN, CompostBinModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CRAB_POT, CrabPotModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CRAB_POT_FILTER, CrabPotFilterModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DECAY_PIT_CHAIN, DecayPitChainModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DECAY_PIT_PLUG, DecayPitPlugModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DECAY_PIT_SHIELD, DecayPitShieldModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DECAY_PIT_TARGET, DecayPitTargetModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_1, DeepmanSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_2, DeepmanSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_3, DeepmanSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.DRUID_ALTAR, DruidAltarModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DRUID_STONES, DruidAltarModel::makeStones);
		event.registerLayerDefinition(BLModelLayers.DUNGEON_DOOR, DungeonDoorModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DUNGEON_DOOR_RUNES, DungeonDoorRunesModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.FISHING_TACKLE_BOX, FishingTackleBoxModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.FISH_TRIMMING_TABLE, FishTrimmingTableModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.GECKO_CAGE, GeckoCageModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.GLASS_JAR, GlassJarModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.HANGING_STEEPING_POT, SteepingPotModel::makeHangingModel);
		event.registerLayerDefinition(BLModelLayers.INFUSER, InfuserModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ITEM_CAGE, ItemCageModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_1, LakeCavernSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_2, LakeCavernSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_3, LakeCavernSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_1, LootPotModels::makePot1);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_2, LootPotModels::makePot2);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_3, LootPotModels::makePot3);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_1, LootUrnModels::makeUrn1);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_2, LootUrnModels::makeUrn2);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_3, LootUrnModels::makeUrn3);
		event.registerLayerDefinition(BLModelLayers.MOB_SPAWNER_CRYSTAL, MobSpawnerCrystalModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.MORTAR, MortarModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.MOSS_BED, MossBedModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.MOTH_HOUSE, MothHouseModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.OFFERING_TABLE, OfferingTableModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.POSSESSED_BLOCK, PossessedBlockModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.PUFFSHROOM, PuffshroomModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.PURIFIER, PurifierModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.REPELLER, RepellerModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_1, RootmanSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_2, RootmanSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_3, RootmanSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.RUBBER_TAP, RubberTapModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.RUBBER_TAP_FLOW, RubberTapModel::theRubberMustFlow);
		event.registerLayerDefinition(BLModelLayers.SMOKING_RACK, SmokingRackModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.SPIKE_BLOCK, SpikeTrapModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.SPOOP, SpikeTrapModel::makeSpoop);
		event.registerLayerDefinition(BLModelLayers.STEEPING_POT, SteepingPotModel::makeNormalModel);
		event.registerLayerDefinition(BLModelLayers.WATER_FILTER, WaterFilterModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.WAYSTONE, WaystoneModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.WIND_CHIME, WindChimeModel::makeModel);
	}

	private static void registerPropertyOverrides(ModelEvent.ModifyBakingResult event) {
		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("head"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && !mob.getEntityData(stack).isEmpty()) {
				return mob.getEntityData(stack).getByte("head_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("body"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && !mob.getEntityData(stack).isEmpty()) {
				return mob.getEntityData(stack).getByte("body_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("tail"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && !mob.getEntityData(stack).isEmpty()) {
				return mob.getEntityData(stack).getByte("tail_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.PESTLE.get(), TheBetweenlands.prefix("active"), (stack, level, entity, idk) -> stack.has(DataComponentRegistry.PESTLE_ACTIVE) ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.SHOCKWAVE_SWORD.get(), TheBetweenlands.prefix("cooldown"), (stack, level, entity, seed) -> level != null && stack.getOrDefault(DataComponentRegistry.SHOCKWAVE_DATA, ShockwaveSwordData.DEFAULT).cooldownTimestamp() + 60L >= level.getGameTime() ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.LIFE_CRYSTAL.get(), TheBetweenlands.prefix("remaining"), (stack, level, entity, seed) -> {
			int damage = stack.getDamageValue();
			if (damage >= stack.getMaxDamage())
				return 4;
			if (damage > stack.getMaxDamage() * 0.75F)
				return 3;
			if (damage > stack.getMaxDamage() * 0.5F)
				return 2;
			if (damage > stack.getMaxDamage() * 0.25F)
				return 1;
			return 0;
		});

		ItemProperties.register(ItemRegistry.LIFE_CRYSTAL_FRAGMENT.get(), TheBetweenlands.prefix("remaining"), (stack, level, entity, seed) -> {
			int damage = stack.getDamageValue();
			if (damage >= stack.getMaxDamage())
				return 4;
			if (damage > stack.getMaxDamage() * 0.75F)
				return 3;
			if (damage > stack.getMaxDamage() * 0.5F)
				return 2;
			if (damage > stack.getMaxDamage() * 0.25F)
				return 1;
			return 0;
		});

		ItemProperties.register(ItemRegistry.ANGRY_PEBBLE.get(), TheBetweenlands.prefix("charging"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.SILKY_PEBBLE.get(), TheBetweenlands.prefix("charging"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.SLINGSHOT.get(), TheBetweenlands.prefix("pull"), (stack, level, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
			}
		});
		ItemProperties.register(ItemRegistry.SLINGSHOT.get(), TheBetweenlands.prefix("pulling"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.FISHING_SPEAR.get(), TheBetweenlands.prefix("throwing"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.AMPHIBIOUS_FISHING_SPEAR.get(), TheBetweenlands.prefix("throwing"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.ROBUST_AMPHIBIOUS_FISHING_SPEAR.get(), TheBetweenlands.prefix("throwing"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.BIOPATHIC_TRIGGERSTONE.get(), TheBetweenlands.prefix("effect"), (stack, level, entity, seed) -> {
			var upgrade = stack.getOrDefault(DataComponentRegistry.SELECTED_UPGRADE, AmphibiousArmorUpgradeRegistry.NONE.get());
			if (upgrade == AmphibiousArmorUpgradeRegistry.ELECTRIC.get()) return 1.0F;
			if (upgrade == AmphibiousArmorUpgradeRegistry.URCHIN_SPIKE.get()) return 2.0F;
			if (upgrade == AmphibiousArmorUpgradeRegistry.FISH_VORTEX.get()) return 3.0F;
			return 0.0F;
		});

		ItemProperties.register(ItemRegistry.BONE_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.GREEN_DENTROTHYST_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.ORANGE_DENTROTHYST_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.POLISHED_GREEN_DENTROTHYST_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.POLISHED_ORANGE_DENTROTHYST_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.LURKER_SKIN_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.OCTINE_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.SYRMORITE_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.VALONITE_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.WEEDWOOD_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.LIVING_WEEDWOOD_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem().is(stack.getItem()) ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.SYRMORITE_SHIELD.get(), TheBetweenlands.prefix("charging"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack && (SwatShieldItem.getRemainingChargeTicks(stack, entity) > 0 || SwatShieldItem.isPreparingCharge(stack, entity)) ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.VALONITE_SHIELD.get(), TheBetweenlands.prefix("charging"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack && (SwatShieldItem.getRemainingChargeTicks(stack, entity) > 0 || SwatShieldItem.isPreparingCharge(stack, entity)) ? 1.0F : 0.0F);

		ItemProperties.register(ItemRegistry.WEEDWOOD_BOW.get(), TheBetweenlands.prefix("pull"), (stack, level, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
			}
		});
		ItemProperties.register(ItemRegistry.WEEDWOOD_BOW.get(), TheBetweenlands.prefix("pulling"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.WEEDWOOD_BOW.get(), TheBetweenlands.prefix("type"), (stack, level, entity, seed) -> entity instanceof Player player ? WeedwoodBowItem.getArrowType(player, stack) : 0.0F);

		ItemProperties.register(ItemRegistry.PREDATOR_BOW.get(), TheBetweenlands.prefix("pull"), (stack, level, entity, seed) -> {
			if (entity == null) {
				return 0.0F;
			} else {
				return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
			}
		});
		ItemProperties.register(ItemRegistry.PREDATOR_BOW.get(), TheBetweenlands.prefix("pulling"), (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
		ItemProperties.register(ItemRegistry.PREDATOR_BOW.get(), TheBetweenlands.prefix("type"), (stack, level, entity, seed) -> entity instanceof Player player ? WeedwoodBowItem.getArrowType(player, stack) : 0.0F);

		ItemProperties.register(ItemRegistry.JELLYFISH.get(), TheBetweenlands.prefix("color"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof MobItem<?> mob && !mob.getEntityData(stack).isEmpty()) {
				return mob.getEntityData(stack).getByte("color");
			}
			return 0;
		});
	}

	private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(TheBetweenlands.prefix("bush"), BushModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("swamp_plant"), SwampPlantModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("bulb_capped_mushroom"), BulbCappedMushroomModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("flat_head_mushroom_1"), FlatHeadMushroom1ModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("flat_head_mushroom_2"), FlatHeadMushroom2ModelLoader.INSTANCE);

		event.register(TheBetweenlands.prefix("black_hat_mushroom_1"), BlackHatMushroom1ModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("black_hat_mushroom_2"), BlackHatMushroom2ModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("black_hat_mushroom_3"), BlackHatMushroom3ModelLoader.INSTANCE);

		event.register(TheBetweenlands.prefix("fungus_crop"), FungusCropModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("white_pear_crop"), WhitePearCropModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("barnacle"), BarnacleModelLoader.INSTANCE);
		
		event.register(TheBetweenlands.prefix("paper_lantern"), PaperLanternModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("silt_glass_lantern"), SiltGlassLanternModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("dungeon_wall_candle"), DungeonWallCandleModelLoader.INSTANCE);

		event.register(TheBetweenlands.prefix("wooden_support_beam_1"), WoodenSupportBeam1ModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("wooden_support_beam_2"), WoodenSupportBeam2ModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("wooden_support_beam_3"), WoodenSupportBeam3ModelLoader.INSTANCE);

		event.register(TheBetweenlands.prefix("brazier"), BrazierModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("walkway"), WalkwayModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("venus_fly_trap"), VenusFlyTrapModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("pitcher_plant"), PitcherPlantModelLoader.INSTANCE);
//		event.register(TheBetweenlands.prefix("weeping_blue"), WeepingBlueModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("sundew"), SundewModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("volarpad"), VolarpadModelLoader.INSTANCE);
		
		event.register(TheBetweenlands.prefix("root"), RootGeometry.RootGeometryLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("elements"), CustomElementsModel.Loader.INSTANCE);
		event.register(TheBetweenlands.prefix("slant"), SlantModelLoader.INSTANCE);
		event.register(TheBetweenlands.prefix("connected_texture"), ConnectedTextureGeometry.ConnectedTextureGeometryLoader.INSTANCE);
	}

	private static void registerSpecialModels(ModelEvent.RegisterAdditional event) {
		event.register(BLItemFrameRenderer.FRAME_MODEL);
		event.register(BLItemFrameRenderer.FRAME_BG_MODEL);
		event.register(BLItemFrameRenderer.FRAME_MAP_MODEL);
	}

	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(riftVariantListener = new RiftVariantReloadListener());
		event.registerReloadListener(BLItemRenderer.INSTANCE.get());
		event.registerReloadListener(aspectIcons = new AspectIconTextureManager(Minecraft.getInstance().getTextureManager()));
		event.registerReloadListener(circleGems = new CircleGemTextureManager(Minecraft.getInstance().getTextureManager()));

		// Armour extensions
		event.registerReloadListener(AmphibiousArmorRenderer.INSTANCE);
		event.registerReloadListener(BoneArmorRenderer.INSTANCE);
		event.registerReloadListener(SyrmoriteArmorRenderer.INSTANCE);

		event.registerReloadListener(ExplorersHatRenderer.INSTANCE);
		event.registerReloadListener(SilkMaskRenderer.INSTANCE);
		event.registerReloadListener(SmallSpiritTreeMaskRenderer.INSTANCE);
		event.registerReloadListener(LargeSpiritTreeMaskRenderer.INSTANCE);
	}

	public static void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
		event.register(DimensionRegistries.DIMENSION_RENDERER, new BetweenlandsSpecialEffects());
	}

	public static void registerExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(BLItemRenderer.CLIENT_ITEM_EXTENSION,
			BlockRegistry.CENSER.asItem(), BlockRegistry.DRUID_ALTAR.asItem(), BlockRegistry.PURIFIER.asItem(), BlockRegistry.COMPOST_BIN.asItem(),
			BlockRegistry.FISHING_TACKLE_BOX.asItem(), BlockRegistry.SMOKING_RACK.asItem(), BlockRegistry.FISH_TRIMMING_TABLE.asItem(),
			BlockRegistry.CRAB_POT.asItem(), BlockRegistry.CRAB_POT_FILTER.asItem(), BlockRegistry.ANIMATOR.asItem(),
			BlockRegistry.WIND_CHIME.asItem(), BlockRegistry.OFFERING_TABLE.asItem(), BlockRegistry.MOB_SPAWNER.asItem(),
			BlockRegistry.GECKO_CAGE.asItem(), BlockRegistry.ALEMBIC.asItem(), BlockRegistry.WAYSTONE.asItem(),
			BlockRegistry.MORTAR.asItem(), BlockRegistry.MUD_BRICK_ALCOVE.asItem(), BlockRegistry.ITEM_CAGE.asItem(),
			BlockRegistry.SILT_GLASS_JAR.asItem(), BlockRegistry.FILTERED_SILT_GLASS_JAR.asItem(), BlockRegistry.STEEPING_POT.asItem(),
			BlockRegistry.SYRMORITE_BARREL.asItem(), BlockRegistry.WEEDWOOD_BARREL.asItem(), BlockRegistry.MOTH_HOUSE.asItem(), BlockRegistry.WATER_FILTER.asItem(),
			BlockRegistry.INFUSER.asItem(), BlockRegistry.REPELLER.asItem(), BlockRegistry.WEEDWOOD_CHEST.asItem(),
			BlockRegistry.LOOT_POT_1.asItem(), BlockRegistry.LOOT_POT_2.asItem(), BlockRegistry.LOOT_POT_3.asItem(),
			BlockRegistry.TAR_LOOT_POT_1.asItem(), BlockRegistry.TAR_LOOT_POT_2.asItem(), BlockRegistry.TAR_LOOT_POT_3.asItem(),
			BlockRegistry.MUD_LOOT_POT_1.asItem(), BlockRegistry.MUD_LOOT_POT_2.asItem(), BlockRegistry.MUD_LOOT_POT_3.asItem(),
			BlockRegistry.LOOT_URN_1.asItem(), BlockRegistry.LOOT_URN_2.asItem(), BlockRegistry.LOOT_URN_3.asItem(),
			BlockRegistry.DEEPMAN_SIMULACRUM_1.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_2.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_3.asItem(),
			BlockRegistry.LAKE_CAVERN_SIMULACRUM_1.asItem(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_2.asItem(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_3.asItem(),
			BlockRegistry.ROOTMAN_SIMULACRUM_1.asItem(), BlockRegistry.ROOTMAN_SIMULACRUM_2.asItem(), BlockRegistry.ROOTMAN_SIMULACRUM_3.asItem(),
			ItemRegistry.OCTINE_SHIELD.get(), ItemRegistry.VALONITE_SHIELD.get(), ItemRegistry.WEEDWOOD_SHIELD.get(), ItemRegistry.LIVING_WEEDWOOD_SHIELD.get(),
			ItemRegistry.SYRMORITE_SHIELD.get(), ItemRegistry.BONE_SHIELD.get(), ItemRegistry.GREEN_DENTROTHYST_SHIELD.get(), ItemRegistry.POLISHED_GREEN_DENTROTHYST_SHIELD.get(),
			ItemRegistry.ORANGE_DENTROTHYST_SHIELD.get(), ItemRegistry.POLISHED_ORANGE_DENTROTHYST_SHIELD.get(), ItemRegistry.LURKER_SKIN_SHIELD.get(),
			ItemRegistry.DRAETON.get(), ItemRegistry.DRAETON_ANCHOR_UPGRADE.get(), ItemRegistry.DRAETON_CRAFTING_UPGRADE.get(), ItemRegistry.DRAETON_FURNACE_UPGRADE.get(),
			ItemRegistry.WEEDWOOD_ROWBOAT.get(), ItemRegistry.WEEDWOOD_ROWBOAT_LANTERN_UPGRADE.get());

		event.registerItem(BigSwingExtension.INSTANCE, ItemRegistry.VALONITE_GREATAXE.get(), ItemRegistry.ANCIENT_BATTLEAXE.get(), ItemRegistry.ANCIENT_GREATSWORD.get());

		event.registerItem(AmphibiousArmorRenderer.INSTANCE,
			ItemRegistry.AMPHIBIOUS_HELMET.get(), ItemRegistry.AMPHIBIOUS_CHESTPLATE.get(),
			ItemRegistry.AMPHIBIOUS_LEGGINGS.get(), ItemRegistry.AMPHIBIOUS_BOOTS.get());
		event.registerItem(BoneArmorRenderer.INSTANCE,
			ItemRegistry.BONE_HELMET, ItemRegistry.BONE_CHESTPLATE,
			ItemRegistry.BONE_LEGGINGS, ItemRegistry.BONE_BOOTS);
		event.registerItem(SyrmoriteArmorRenderer.INSTANCE,
			ItemRegistry.SYRMORITE_HELMET.get(), ItemRegistry.SYRMORITE_CHESTPLATE.get(),
			ItemRegistry.SYRMORITE_LEGGINGS.get(), ItemRegistry.SYRMORITE_BOOTS.get());

		event.registerItem(ExplorersHatRenderer.INSTANCE, ItemRegistry.EXPLORERS_HAT.get());
		event.registerItem(SkullMaskRenderer.INSTANCE, ItemRegistry.SKULL_MASK.get());
		event.registerItem(SilkMaskRenderer.INSTANCE, ItemRegistry.SILK_MASK.get());
		event.registerItem(SmallSpiritTreeMaskRenderer.INSTANCE, ItemRegistry.SMALL_SPIRIT_TREE_FACE_MASK.get());
		event.registerItem(LargeSpiritTreeMaskRenderer.INSTANCE, ItemRegistry.LARGE_SPIRIT_TREE_FACE_MASK.get());

		event.registerMobEffect(InvisibleEffectRegistration.INSTANCE, MobEffectRegistry.ENLIGHTENED.get(), MobEffectRegistry.ROOT_BOUND.get());

		for (DeferredHolder<MobEffect, ?> effect : MobEffectRegistry.EFFECTS.getEntries().stream().filter(holder -> holder.get() instanceof ElixirEffect.ElixirPotionEffect).toList()) {
			ElixirEffect.ElixirPotionEffect potEffect = (ElixirEffect.ElixirPotionEffect) effect.get();
			event.registerMobEffect(new ElixirEffectExtension(potEffect), potEffect);
		}

		event.registerFluidType(new SwampWaterFluidTypeExtension(), FluidTypeRegistry.SWAMP_WATER.get());
		event.registerFluidType(new BasicFluidTypeExtension("stagnant_water"), FluidTypeRegistry.STAGNANT_WATER.get());
		event.registerFluidType(new BasicFluidTypeExtension("tar"), FluidTypeRegistry.TAR.get());
		event.registerFluidType(new BasicFluidTypeExtension("rubber"), FluidTypeRegistry.RUBBER.get());
		event.registerFluidType(new BasicFluidTypeExtension("fog"), FluidTypeRegistry.FOG.get());
		event.registerFluidType(new BasicFluidTypeExtension("shallowbreath"), FluidTypeRegistry.SHALLOWBREATH.get());
		event.registerFluidType(new BasicFluidTypeExtension("clean_water"), FluidTypeRegistry.CLEAN_WATER.get());
		event.registerFluidType(new BasicFluidTypeExtension("fish_oil"), FluidTypeRegistry.FISH_OIL.get());

		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.DULL_LAVENDER.getColorValue(), "dye"), FluidTypeRegistry.DULL_LAVENDER_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.MAROON.getColorValue(), "dye"), FluidTypeRegistry.MAROON_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.SHADOW_GREEN.getColorValue(), "dye"), FluidTypeRegistry.SHADOW_GREEN_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.CAMELOT_MAGENTA.getColorValue(), "dye"), FluidTypeRegistry.CAMELOT_MAGENTA_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.SAFFRON.getColorValue(), "dye"), FluidTypeRegistry.SAFFRON_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.CARIBBEAN_GREEN.getColorValue(), "dye"), FluidTypeRegistry.CARIBBEAN_GREEN_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.VIVID_TANGERINE.getColorValue(), "dye"), FluidTypeRegistry.VIVID_TANGERINE_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.CHAMPAGNE.getColorValue(), "dye"), FluidTypeRegistry.CHAMPAGNE_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.RAISIN_BLACK.getColorValue(), "dye"), FluidTypeRegistry.RAISIN_BLACK_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.SUSHI_GREEN.getColorValue(), "dye"), FluidTypeRegistry.SUSHI_GREEN_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.ELM_CYAN.getColorValue(), "dye"), FluidTypeRegistry.ELM_CYAN_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.CADMIUM_GREEN.getColorValue(), "dye"), FluidTypeRegistry.CADMIUM_GREEN_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.LAVENDER_BLUE.getColorValue(), "dye"), FluidTypeRegistry.LAVENDER_BLUE_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.BROWN_RUST.getColorValue(), "dye"), FluidTypeRegistry.BROWN_RUST_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.MIDNIGHT_PURPLE.getColorValue(), "dye"), FluidTypeRegistry.MIDNIGHT_PURPLE_DYE.get());
		event.registerFluidType(new ColoredFluidTypeExtension(BLDyeColor.PEWTER_GREY.getColorValue(), "dye"), FluidTypeRegistry.PEWTER_GREY_DYE.get());

		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.NETTLE_SOUP.getColorValue(), "brew"), FluidTypeRegistry.NETTLE_SOUP.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.NETTLE_TEA.getColorValue(), "brew"), FluidTypeRegistry.NETTLE_TEA.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.PHEROMONE_EXTRACT.getColorValue(), "brew"), FluidTypeRegistry.PHEROMONE_EXTRACT.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.SWAMP_BROTH.getColorValue(), "brew"), FluidTypeRegistry.SWAMP_BROTH.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.STURDY_STOCK.getColorValue(), "brew"), FluidTypeRegistry.STURDY_STOCK.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.PEAR_CORDIAL.getColorValue(), "brew"), FluidTypeRegistry.PEAR_CORDIAL.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.SHAMANS_BREW.getColorValue(), "brew"), FluidTypeRegistry.SHAMANS_BREW.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.LAKE_BROTH.getColorValue(), "brew"), FluidTypeRegistry.LAKE_BROTH.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.SHELL_STOCK.getColorValue(), "brew"), FluidTypeRegistry.SHELL_STOCK.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.FROG_LEG_EXTRACT.getColorValue(), "brew"), FluidTypeRegistry.FROG_LEG_EXTRACT.get());
		event.registerFluidType(new ColoredFluidTypeExtension(DrinkableBrew.WITCH_TEA.getColorValue(), "brew"), FluidTypeRegistry.WITCH_TEA.get());
	}

	private static void registerParticleSprites(final RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ParticleRegistry.PORTAL_EFFECT.get(), PortalParticle.Provider::new);
		event.registerSpriteSet(ParticleRegistry.ANIMATOR.get(), AnimatorParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.FLY.get(), BugParticle.FlyFactory::new);
		event.registerSpriteSet(ParticleRegistry.MOSQUITO.get(), BugParticle.MosquitoFactory::new);
		event.registerSpriteSet(ParticleRegistry.MOTH.get(), BugParticle.MothFactory::new);
		event.registerSpriteSet(ParticleRegistry.SILK_MOTH.get(), BugParticle.SilkMothFactory::new);
		event.registerSpriteSet(ParticleRegistry.SPIRIT_BUTTERFLY.get(), SpiritButterflyParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.FLYING_SWARM.get(), EmissiveBugParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.SWARM.get(), SwarmParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.EMISSIVE_SWARM.get(), EmissiveSwarmParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.WATER_BUG.get(), BugParticle.WaterBugFactory::new);
		event.registerSpriteSet(ParticleRegistry.FANCY_BUBBLE.get(), FancyBubbleParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.FANCY_DRIP.get(), FancyDripParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.RAIN.get(), BLRainParticle.Factory::new);
		event.registerSpecial(ParticleRegistry.SPIKE.get(), new SpikeParticle.Factory());
		event.registerSpriteSet(ParticleRegistry.FISH_VORTEX.get(), FishVortexParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.INFUSER_BUBBLE.get(), BLBubbleParticle.InfuserFactory::new);
		event.registerSpriteSet(ParticleRegistry.PURIFIER_BUBBLE.get(), BLBubbleParticle.PurifierFactory::new);
		event.registerSpriteSet(ParticleRegistry.TAR_BUBBLE.get(), BLBubbleParticle.TarFactory::new);
		event.registerSpriteSet(ParticleRegistry.WATER_BUBBLE.get(), BLBubbleParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.SONIC_SCREAM.get(), SonicScreamParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.SCRAP.get(), ScrapParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.LEAF.get(), ScrapParticle.Factory::new);
		event.registerSprite(ParticleRegistry.SLEEPING.get(), (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> new SleepingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed));
		event.registerSpriteSet(ParticleRegistry.EMBER_SWIRL.get(), EntitySwirlParticle.DefaultFactory::new);
		event.registerSpriteSet(ParticleRegistry.DRUID_CASTING.get(), DruidCastingParticle.Factory::new);
		event.registerSprite(ParticleRegistry.DRIPPING_FLUID.get(), ColoredDripParticle::createFluidHangParticle);
		event.registerSpriteSet(ParticleRegistry.FALLING_FLUID.get(), ColoredDripParticle.FallFactory::new);
		event.registerSprite(ParticleRegistry.LANDING_FLUID.get(), ColoredDripParticle::createFluidLandParticle);
		event.registerSpriteSet(ParticleRegistry.GAS_CLOUD.get(), GasCloudParticle.GasCloudFactory::new);
		event.registerSpriteSet(ParticleRegistry.GAS_CLOUD_HAZE.get(), GasCloudHazeParticle.GasCloudHazeFactory::new);
		event.registerSpriteSet(ParticleRegistry.CHIROMAW_TRANSFORM.get(), EntitySwirlParticle.DefaultFactory::new);
		event.registerSpriteSet(ParticleRegistry.CORRUPTED.get(), SimpleParticle.CorruptedFactory::new);
		event.registerSpriteSet(ParticleRegistry.EMBER.get(), SimpleParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.SMOOTH_SMOKE.get(), SimpleParticle.Factory::new);
		event.registerSpriteSet(ParticleRegistry.LEAF_SWIRL.get(), EntitySwirlParticle.DefaultFactory::new);
		event.registerSpriteSet(ParticleRegistry.WATER_RIPPLE.get(), WaterRippleParticle.Factory::new);
		event.registerSpecial(ParticleRegistry.LIGHTNING_ARC.get(), new LightningArcParticle.Factory());
		event.registerSpriteSet(ParticleRegistry.FLY_SWIRL.get(), EntitySwirlParticle.DefaultFactory::new);
		event.registerSpriteSet(ParticleRegistry.WIGHT_FACE_SWIRL.get(), EntitySwirlParticle.DefaultFactory::new);
		event.registerSpriteSet(ParticleRegistry.WIGHT_FACE.get(), BugParticle.FlyFactory::new);

	}

	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, level, pos, tintIndex) -> {
				if (tintIndex <= 0) {
					return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
				}
				return 0xFFFFFFFF;
			},
			BlockRegistry.WEEDWOOD_LEAVES.get(),
			BlockRegistry.RUBBER_TREE_LEAVES.get(),
			BlockRegistry.POISON_IVY.get(),
			BlockRegistry.MOSS.get(),
			BlockRegistry.DEAD_MOSS.get(),
			BlockRegistry.HANGER.get(),
			BlockRegistry.SEEDED_HANGER.get(),
			BlockRegistry.EDGE_LEAF.get(),
			BlockRegistry.EDGE_MOSS.get());

		event.register((state, level, pos, tintIndex) -> {
				if (tintIndex <= 0 && level != null && pos != null) {
					return IClientFluidTypeExtensions.of(FluidRegistry.SWAMP_WATER_STILL.get()).getTintColor(FluidRegistry.SWAMP_WATER_STILL.get().defaultFluidState(), level, pos);
				}
				return 0xFF2981FF;
			},
			BlockRegistry.PUDDLE.get());

		event.register((state, level, pos, tintIndex) -> {
				if (tintIndex == 0) {
					return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
				}
				return 0xFFFFFFFF;
			},
			BlockRegistry.WEEDWOOD_BUSH.get());

		event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 1 && level != null && pos != null) {
				if (level.getBlockEntity(pos) instanceof AspectrusCropBlockEntity crop) {
					Aspect aspect = crop.getAspect();
					if (aspect != null) {
						return aspect.type().value().color();
					}
				}
			}
			return 0xFFFFFFFF;
		}, BlockRegistry.ASPECTRUS_CROP.get());

		event.register((state, level, pos, tintIndex) -> {
				if (tintIndex <= 0) {
					return level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor();
				}
				return 0xFFFFFFFF;
			},
			BlockRegistry.SWAMP_GRASS.get(),
			BlockRegistry.SHORT_SWAMP_GRASS.get(),
			BlockRegistry.TALL_SWAMP_GRASS.get(),
			BlockRegistry.CATTAIL.get(),
			BlockRegistry.POISON_IVY.get(),
			BlockRegistry.SWAMP_REED.get());

		event.register((state, level, pos, tintIndex) -> {
				if (tintIndex == 0 && state.getBlock() instanceof PresentBlock present) {
					return present.getColor().getTextureDiffuseColor();
				}
				return 0xFFFFFFFF;
			}, BlockRegistry.WHITE_PRESENT.get(), BlockRegistry.LIGHT_GRAY_PRESENT.get(), BlockRegistry.GRAY_PRESENT.get(), BlockRegistry.BLACK_PRESENT.get(),
			BlockRegistry.RED_PRESENT.get(), BlockRegistry.ORANGE_PRESENT.get(), BlockRegistry.YELLOW_PRESENT.get(), BlockRegistry.GREEN_PRESENT.get(),
			BlockRegistry.LIME_PRESENT.get(), BlockRegistry.BLUE_PRESENT.get(), BlockRegistry.CYAN_PRESENT.get(), BlockRegistry.LIGHT_BLUE_PRESENT.get(),
			BlockRegistry.PURPLE_PRESENT.get(), BlockRegistry.MAGENTA_PRESENT.get(), BlockRegistry.PINK_PRESENT.get(), BlockRegistry.BROWN_PRESENT.get());
	}

	private static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();

		event.register((stack, tintIndex) -> stack.getItem() instanceof BlockItem blocc ? blockColors.getColor(blocc.getBlock().defaultBlockState(), null, null, tintIndex) : 0xFFFFFFFF,
			BlockRegistry.WHITE_PRESENT, BlockRegistry.LIGHT_GRAY_PRESENT, BlockRegistry.GRAY_PRESENT, BlockRegistry.BLACK_PRESENT,
			BlockRegistry.RED_PRESENT, BlockRegistry.ORANGE_PRESENT, BlockRegistry.YELLOW_PRESENT, BlockRegistry.GREEN_PRESENT,
			BlockRegistry.LIME_PRESENT, BlockRegistry.BLUE_PRESENT, BlockRegistry.CYAN_PRESENT, BlockRegistry.LIGHT_BLUE_PRESENT,
			BlockRegistry.PURPLE_PRESENT, BlockRegistry.MAGENTA_PRESENT, BlockRegistry.PINK_PRESENT, BlockRegistry.BROWN_PRESENT,
			BlockRegistry.SHORT_SWAMP_GRASS, BlockRegistry.POISON_IVY, BlockRegistry.TALL_SWAMP_GRASS, BlockRegistry.MOSS, BlockRegistry.DEAD_MOSS,
			BlockRegistry.WEEDWOOD_LEAVES, BlockRegistry.RUBBER_TREE_LEAVES, BlockRegistry.SWAMP_GRASS, BlockRegistry.WEEDWOOD_BUSH,
			BlockRegistry.EDGE_LEAF, BlockRegistry.EDGE_MOSS);

		event.register((stack, tintIndex) -> {
			if (stack.get(DataComponents.ENTITY_DATA) != null) {
				if (stack.has(DataComponentRegistry.ROT_TIME)) {
					long rottingTime = stack.get(DataComponentRegistry.ROT_TIME);
					if (rottingTime - Minecraft.getInstance().level.getGameTime() <= 0) {
						return 0xFF5FB050;
					}
				}

				return AnadiaParts.AnadiaColor.get(stack.get(DataComponents.ENTITY_DATA).copyTag().getByte("fish_color")).getColor();
			}
			return AnadiaParts.AnadiaColor.UNKNOWN.getColor();
		}, ItemRegistry.ANADIA);

		event.register((stack, tintIndex) -> {
			if (tintIndex != 1) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect().map(aspect -> aspect.value().color()).orElse(0xFFFFFFFF);
		}, ItemRegistry.ASPECTRUS_FRUIT);

		event.register((stack, tintIndex) -> {
			if (tintIndex != 1) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(BLDyeColor.CHAMPAGNE.getColorValue(), false)).rgb() | 0xFF000000;
		}, ItemRegistry.SMALL_LURKER_SKIN_POUCH, ItemRegistry.MEDIUM_LURKER_SKIN_POUCH, ItemRegistry.LARGE_LURKER_SKIN_POUCH, ItemRegistry.XL_LURKER_SKIN_POUCH);

		event.register((stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).getElixirColor();
		}, ItemRegistry.GREEN_ELIXIR, ItemRegistry.ORANGE_ELIXIR);

		event.register((stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).getAspectColor();
		}, ItemRegistry.GREEN_ASPECT_VIAL, ItemRegistry.ORANGE_ASPECT_VIAL);

		event.register((stack, tintIndex) -> {
				if (tintIndex != 0) return 0xFFFFFFFF;
				return stack.getItem() instanceof BLItemFrameItem frame ? frame.getColor() | 0xFF000000 : 0xFFFFFFFF;
			}, ItemRegistry.DULL_LAVENDER_ITEM_FRAME, ItemRegistry.MAROON_ITEM_FRAME, ItemRegistry.SHADOW_GREEN_ITEM_FRAME, ItemRegistry.CAMELOT_MAGENTA_ITEM_FRAME,
			ItemRegistry.SAFFRON_ITEM_FRAME, ItemRegistry.CARIBBEAN_GREEN_ITEM_FRAME, ItemRegistry.VIVID_TANGERINE_ITEM_FRAME, ItemRegistry.CHAMPAGNE_ITEM_FRAME,
			ItemRegistry.RAISIN_BLACK_ITEM_FRAME, ItemRegistry.SUSHI_GREEN_ITEM_FRAME, ItemRegistry.ELM_CYAN_ITEM_FRAME, ItemRegistry.CADMIUM_GREEN_ITEM_FRAME,
			ItemRegistry.LAVENDER_BLUE_ITEM_FRAME, ItemRegistry.BROWN_RUST_ITEM_FRAME, ItemRegistry.MIDNIGHT_PURPLE_ITEM_FRAME, ItemRegistry.PEWTER_GREY_ITEM_FRAME);

		event.register((stack, tint) -> {
			var fluid = stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).copy();
			return tint == 1 ? IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid) : -1;
		}, ItemRegistry.WEEDWOOD_BUCKET, ItemRegistry.SYRMORITE_BUCKET);

		event.register((stack, tint) -> {
				var fluid = ((BucketItem) stack.getItem()).content;
				return tint == 1 ? IClientFluidTypeExtensions.of(fluid).getTintColor(new FluidStack(fluid, FluidType.BUCKET_VOLUME)) : -1;
			}, ItemRegistry.DULL_LAVENDER_DYE_BUCKET, ItemRegistry.MAROON_DYE_BUCKET, ItemRegistry.SHADOW_GREEN_DYE_BUCKET, ItemRegistry.CAMELOT_MAGENTA_DYE_BUCKET,
			ItemRegistry.SAFFRON_DYE_BUCKET, ItemRegistry.CARIBBEAN_GREEN_DYE_BUCKET, ItemRegistry.VIVID_TANGERINE_DYE_BUCKET, ItemRegistry.CHAMPAGNE_DYE_BUCKET,
			ItemRegistry.RAISIN_BLACK_DYE_BUCKET, ItemRegistry.SUSHI_GREEN_DYE_BUCKET, ItemRegistry.ELM_CYAN_DYE_BUCKET, ItemRegistry.CADMIUM_GREEN_DYE_BUCKET,
			ItemRegistry.LAVENDER_BLUE_DYE_BUCKET, ItemRegistry.BROWN_RUST_DYE_BUCKET, ItemRegistry.MIDNIGHT_PURPLE_DYE_BUCKET, ItemRegistry.PEWTER_GREY_DYE_BUCKET,
			ItemRegistry.NETTLE_SOUP_BUCKET, ItemRegistry.NETTLE_TEA_BUCKET, ItemRegistry.PHEROMONE_EXTRACT_BUCKET, ItemRegistry.SWAMP_BROTH_BUCKET,
			ItemRegistry.STURDY_STOCK_BUCKET, ItemRegistry.PEAR_CORDIAL_BUCKET, ItemRegistry.SHAMANS_BREW_BUCKET, ItemRegistry.LAKE_BROTH_BUCKET,
			ItemRegistry.SHELL_STOCK_BUCKET, ItemRegistry.FROG_LEG_EXTRACT_BUCKET, ItemRegistry.WITCH_TEA_BUCKET, ItemRegistry.SWAMP_BROTH_BUCKET.get(),
			ItemRegistry.SWAMP_WATER_BUCKET);

		event.register((stack, tint) -> tint == 1 ? InfusionBucketItem.getColor(stack) : -1, ItemRegistry.WEEDWOOD_INFUSION_BUCKET, ItemRegistry.SYRMORITE_INFUSION_BUCKET);
	}

	public static void corrosiveBootsLayer(RegisterItemDecorationsEvent event) {
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.BOOTS) {
				event.register(item, new CorrosiveBootsOverlay());
			}
			event.register(item, new CircleGemItemOverlay());
		}
	}
}
