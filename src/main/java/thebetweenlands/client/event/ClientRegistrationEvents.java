package thebetweenlands.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.client.*;
import thebetweenlands.client.gui.overlay.DecayBarOverlay;
import thebetweenlands.client.gui.overlay.FishStaminaBarOverlay;
import thebetweenlands.client.gui.screen.*;
import thebetweenlands.client.handler.ClientHandlerEvents;
import thebetweenlands.client.model.baked.RootGeometry;
import thebetweenlands.client.model.block.*;
import thebetweenlands.client.model.block.cage.GeckoCageModel;
import thebetweenlands.client.model.block.simulacrum.*;
import thebetweenlands.client.model.entity.*;
import thebetweenlands.client.particle.BetweenlandsParticle;
import thebetweenlands.client.particle.BetweenlandsPortalParticle;
import thebetweenlands.client.renderer.block.*;
import thebetweenlands.client.renderer.entity.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.PresentBlock;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.entities.fishing.anadia.AnadiaParts;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.items.AnadiaMobItem;
import thebetweenlands.common.registries.*;

public class ClientRegistrationEvents {

	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;

	public static RiftVariantReloadListener riftVariantListener;

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientRegistrationEvents::clientSetup);
		eventbus.addListener(ClientRegistrationEvents::registerScreens);
		eventbus.addListener(ClientRegistrationEvents::registerRenderers);
		eventbus.addListener(ClientRegistrationEvents::registerDimEffects);
		eventbus.addListener(ClientRegistrationEvents::registerExtensions);
		eventbus.addListener(ClientRegistrationEvents::registerLayerDefinition);
		eventbus.addListener(ClientRegistrationEvents::particleStuff);
		eventbus.addListener(ClientRegistrationEvents::registerBlockColors);
		eventbus.addListener(ClientRegistrationEvents::registerReloadListeners);
		eventbus.addListener(ClientRegistrationEvents::registerGeometryLoaders);
		eventbus.addListener(ClientRegistrationEvents::registerPropertyOverrides);
		eventbus.addListener(ClientRegistrationEvents::registerOverlays);
		eventbus.addListener(ClientRegistrationEvents::registerItemColors);
		MainMenuEvents.init();
		ClientHandlerEvents.init();
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
		});
	}

	private static void registerOverlays(final RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.AIR_LEVEL, TheBetweenlands.prefix("decay_meter"), DecayBarOverlay::renderDecayBar);
		event.registerAboveAll(TheBetweenlands.prefix("fishing_minigame"), FishStaminaBarOverlay::renderFishingHud);
	}

	private static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(MenuRegistry.ANIMATOR.get(), AnimatorScreen::new);
		event.register(MenuRegistry.CENSER.get(), CenserScreen::new);
		event.register(MenuRegistry.DRUID_ALTAR.get(), DruidAltarScreen::new);
		event.register(MenuRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxScreen::new);
		event.register(MenuRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableScreen::new);
		event.register(MenuRegistry.MORTAR.get(), MortarScreen::new);
		event.register(MenuRegistry.SMOKING_RACK.get(), SmokingRackScreen::new);
	}

	private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.SWAMP_HAG.get(), SwampHagRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GECKO.get(), GeckoRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WIGHT.get(), RenderWight::new);
		event.registerEntityRenderer(EntityRegistry.BUBBLER_CRAB.get(), BubblerCrabRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SILT_CRAB.get(), SiltCrabRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ANADIA.get(), AnadiaRenderer::new);
		event.registerEntityRenderer(EntityRegistry.FISH_HOOK.get(), BLFishHookRenderer::new);
		event.registerEntityRenderer(EntityRegistry.SEAT.get(), NoopRenderer::new);
		event.registerEntityRenderer(EntityRegistry.ELIXIR.get(), ThrownItemRenderer::new);

		event.registerBlockEntityRenderer(BlockEntityRegistry.MUD_BRICK_ALCOVE.get(), AlcoveRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ALEMBIC.get(), AlembicRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.ANIMATOR.get(), AnimatorRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CENSER.get(), CenserRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.COMPOST_BIN.get(), CompostBinRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT.get(), CrabPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT_FILTER.get(), CrabPotFilterRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DRUID_ALTAR.get(), DruidAltarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.GECKO_CAGE.get(), GeckoCageRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_POT.get(), LootPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_URN.get(), LootUrnRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MOB_SPAWNER.get(), MobSpawnerRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.MORTAR.get(), MortarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.OFFERING_TABLE.get(), OfferingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.PURIFIER.get(), PurifierRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SIMULACRUM.get(), SimulacrumRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SMOKING_RACK.get(), SmokingRackRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SPIKE_TRAP.get(), SpikeTrapRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WAYSTONE.get(), WaystoneRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.WIND_CHIME.get(), WindChimeRenderer::new);
	}

	private static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(SwampHagRenderer.SWAMP_HAG_MODEL_LAYER, SwampHagModel::createModelLayer);
		event.registerLayerDefinition(GeckoRenderer.GECKO_MODEL_LAYER, GeckoModel::createModelLayer);
		event.registerLayerDefinition(RenderWight.WIGHT_MODEL_LAYER, ModelWight::createModelLayer);
		event.registerLayerDefinition(BLModelLayers.BUBBLER_CRAB, BubblerCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.SILT_CRAB, SiltCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.ANADIA, AnadiaModel::create);
		event.registerLayerDefinition(BLModelLayers.FISH_HOOK, BLFishHookModel::create);

		event.registerLayerDefinition(BLModelLayers.ALCOVE, AlcoveModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ALEMBIC, AlembicModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ANIMATOR, AnimatorModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CENSER, CenserModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.COMPOST_BIN, CompostBinModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CRAB_POT, CrabPotModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.CRAB_POT_FILTER, CrabPotFilterModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_1, DeepmanSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_2, DeepmanSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_3, DeepmanSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.DRUID_ALTAR, DruidAltarModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.DRUID_STONES, DruidAltarModel::makeStones);
		event.registerLayerDefinition(BLModelLayers.FISHING_TACKLE_BOX, FishingTackleBoxModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.FISH_TRIMMING_TABLE, FishTrimmingTableModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.GECKO_CAGE, GeckoCageModel::makeModel);
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
		event.registerLayerDefinition(BLModelLayers.OFFERING_TABLE, OfferingTableModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.PURIFIER, PurifierModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_1, RootmanSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_2, RootmanSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_3, RootmanSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.SMOKING_RACK, SmokingRackModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.SPIKE_BLOCK, SpikeTrapModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.SPOOP, SpikeTrapModel::makeSpoop);
		event.registerLayerDefinition(BLModelLayers.WAYSTONE, WaystoneModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.WIND_CHIME, WindChimeModel::makeModel);
	}

	private static void registerPropertyOverrides(ModelEvent.ModifyBakingResult event) {
		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("head"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && mob.hasEntityData(stack)) {
				return mob.getEntityData(stack).getByte("head_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("body"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && mob.hasEntityData(stack)) {
				return mob.getEntityData(stack).getByte("body_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.ANADIA.get(), TheBetweenlands.prefix("tail"), (stack, level, entity, idk) -> {
			if (stack.getItem() instanceof AnadiaMobItem mob && mob.hasEntityData(stack)) {
				return mob.getEntityData(stack).getByte("tail_type");
			}
			return 0;
		});

		ItemProperties.register(ItemRegistry.PESTLE.get(), TheBetweenlands.prefix("active"), (stack, level, entity, idk) -> stack.has(DataComponentRegistry.PESTLE_ACTIVE) ? 1.0F : 0.0F);

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
	}

	private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(TheBetweenlands.prefix("root"), RootGeometry.RootGeometryLoader.INSTANCE);
	}

	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(riftVariantListener = new RiftVariantReloadListener());
		event.registerReloadListener(new CircleGemTextureManager());
		event.registerReloadListener(new AspectIconTextureManager(Minecraft.getInstance().getTextureManager()));
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
			BlockRegistry.MORTAR.asItem(), BlockRegistry.MUD_BRICK_ALCOVE.asItem(),
			BlockRegistry.LOOT_POT_1.asItem(), BlockRegistry.LOOT_POT_2.asItem(), BlockRegistry.LOOT_POT_3.asItem(),
			BlockRegistry.TAR_LOOT_POT_1.asItem(), BlockRegistry.TAR_LOOT_POT_2.asItem(), BlockRegistry.TAR_LOOT_POT_3.asItem(),
			BlockRegistry.MUD_LOOT_POT_1.asItem(), BlockRegistry.MUD_LOOT_POT_2.asItem(), BlockRegistry.MUD_LOOT_POT_3.asItem(),
			BlockRegistry.LOOT_URN_1.asItem(), BlockRegistry.LOOT_URN_2.asItem(), BlockRegistry.LOOT_URN_3.asItem(),
			BlockRegistry.DEEPMAN_SIMULACRUM_1.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_2.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_3.asItem(),
			BlockRegistry.LAKE_CAVERN_SIMULACRUM_1.asItem(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_2.asItem(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_3.asItem(),
			BlockRegistry.ROOTMAN_SIMULACRUM_1.asItem(), BlockRegistry.ROOTMAN_SIMULACRUM_2.asItem(), BlockRegistry.ROOTMAN_SIMULACRUM_3.asItem());

		event.registerMobEffect(new IClientMobEffectExtensions() {
			@Override
			public boolean isVisibleInInventory(MobEffectInstance instance) {
				return false;
			}

			@Override
			public boolean isVisibleInGui(MobEffectInstance instance) {
				return false;
			}
		}, ElixirEffectRegistry.ENLIGHTENED.get(), ElixirEffectRegistry.ROOT_BOUND.get());

		for (DeferredHolder<MobEffect, ?> effect : ElixirEffectRegistry.EFFECTS.getEntries().stream().filter(holder -> holder.get() instanceof ElixirEffect.ElixirPotionEffect).toList()) {
			ElixirEffect.ElixirPotionEffect potEffect = (ElixirEffect.ElixirPotionEffect) effect.get();
			event.registerMobEffect(new IClientMobEffectExtensions() {
				@Override
				public boolean isVisibleInInventory(MobEffectInstance instance) {
					return potEffect.getIcon() != null;
				}

				@Override
				public boolean isVisibleInGui(MobEffectInstance instance) {
					return potEffect.getIcon() != null;
				}

				@Override
				public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
					if (potEffect.getIcon() != null) {
						RenderSystem.enableBlend();
						graphics.blit(potEffect.getIcon(), x + 1, y + 7, 0, 0, 0, 16, 16, 16, 16);
					}
					return true;
				}

				@Override
				public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
					return true;
				}

				@Override
				public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
					if (potEffect.getIcon() != null) {
						graphics.blit(potEffect.getIcon(), x + 4, y + 4, 0, 0, 0, 16, 16, 16, 16);
					}
					return true;
				}
			}, potEffect);
		}

		for (DeferredHolder<FluidType, ? extends FluidType> type : FluidTypeRegistry.FLUID_TYPES.getEntries()) {
			event.registerFluidType(new IClientFluidTypeExtensions() {
				@Override
				public ResourceLocation getStillTexture() {
					return TheBetweenlands.prefix("fluid/" + type.getId().getPath() + "_still");
				}

				@Override
				public ResourceLocation getFlowingTexture() {
					return TheBetweenlands.prefix("fluid/" + type.getId().getPath() + "_flowing");
				}
			}, type.get());
		}
	}

	private static void particleStuff(final RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ParticleRegistry.SULFUR_GENERIC.get(), BetweenlandsParticle.Helper::new);
		event.registerSpriteSet(ParticleRegistry.PORTAL_EFFECT.get(), BetweenlandsPortalParticle.Helper::new);
	}

	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
			BlockRegistry.WEEDWOOD_LEAVES.get(),
			BlockRegistry.NIBBLETWIG_LEAVES.get(),
			BlockRegistry.RUBBER_TREE_LEAVES.get(),
			BlockRegistry.POISON_IVY.get(),
			BlockRegistry.MOSS.get(),
			BlockRegistry.HANGER.get(),
			BlockRegistry.SEEDED_HANGER.get());

		event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor(),
			BlockRegistry.SWAMP_GRASS.get(),
			BlockRegistry.SHORT_SWAMP_GRASS.get(),
			BlockRegistry.TALL_SWAMP_GRASS.get(),
			BlockRegistry.SWAMP_REED.get());

		event.register((state, level, pos, tintIndex) -> {
			if (level == null || pos == null || tintIndex != 0) {
				return -1;
			}

			int r = 0;
			int g = 0;
			int b = 0;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					int colorMultiplier = BiomeColors.getAverageWaterColor(level, pos);
					r += (colorMultiplier & 0xFF0000) >> 16;
					g += (colorMultiplier & 0x00FF00) >> 8;
					b += colorMultiplier & 0x0000FF;
				}
			}
			r /= 9;
			g /= 9;
			b /= 9;
			float depth;
			if (pos.getY() > TheBetweenlands.CAVE_START) {
				depth = 1;
			} else {
				if (pos.getY() < TheBetweenlands.CAVE_WATER_HEIGHT) {
					depth = 0;
				} else {
					depth = (pos.getY() - TheBetweenlands.CAVE_WATER_HEIGHT) / (float) (TheBetweenlands.CAVE_START - TheBetweenlands.CAVE_WATER_HEIGHT);
				}
			}
			r = (int) (r * depth + DEEP_COLOR_R * (1 - depth) + 0.5F);
			g = (int) (g * depth + DEEP_COLOR_G * (1 - depth) + 0.5F);
			b = (int) (b * depth + DEEP_COLOR_B * (1 - depth) + 0.5F);
			return r << 16 | g << 8 | b;
		}, BlockRegistry.SWAMP_WATER.get());

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
			BlockRegistry.WHITE_PRESENT.get(), BlockRegistry.LIGHT_GRAY_PRESENT.get(), BlockRegistry.GRAY_PRESENT.get(), BlockRegistry.BLACK_PRESENT.get(),
			BlockRegistry.RED_PRESENT.get(), BlockRegistry.ORANGE_PRESENT.get(), BlockRegistry.YELLOW_PRESENT.get(), BlockRegistry.GREEN_PRESENT.get(),
			BlockRegistry.LIME_PRESENT.get(), BlockRegistry.BLUE_PRESENT.get(), BlockRegistry.CYAN_PRESENT.get(), BlockRegistry.LIGHT_BLUE_PRESENT.get(),
			BlockRegistry.PURPLE_PRESENT.get(), BlockRegistry.MAGENTA_PRESENT.get(), BlockRegistry.PINK_PRESENT.get(), BlockRegistry.BROWN_PRESENT.get());

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
		}, ItemRegistry.ANADIA.get());

		event.register((stack, tintIndex) -> {
			if (tintIndex != 1) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect().map(aspect -> aspect.value().color()).orElse(0xFFFFFFFF);
		}, ItemRegistry.ASPECTRUS_FRUIT.get());

		event.register((stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).getElixirColor();
		}, ItemRegistry.GREEN_ELIXIR.get(), ItemRegistry.ORANGE_ELIXIR.get());

		event.register((stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFFFF;
			return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).getAspectColor();
		}, ItemRegistry.GREEN_ASPECT_VIAL.get(), ItemRegistry.ORANGE_ASPECT_VIAL.get());
	}
}
