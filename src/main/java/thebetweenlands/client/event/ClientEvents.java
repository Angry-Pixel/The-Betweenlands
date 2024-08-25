package thebetweenlands.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.bus.api.IEventBus;
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
import thebetweenlands.client.gui.screen.FishTrimmingTableScreen;
import thebetweenlands.client.model.baked.RootGeometry;
import thebetweenlands.client.model.block.*;
import thebetweenlands.client.model.block.simulacrum.*;
import thebetweenlands.client.model.entity.*;
import thebetweenlands.client.particle.BetweenlandsParticle;
import thebetweenlands.client.particle.BetweenlandsPortalParticle;
import thebetweenlands.client.renderer.block.*;
import thebetweenlands.client.renderer.entity.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;

public class ClientEvents {

	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;

	private static RiftVariantReloadListener riftVariantListener;

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientEvents::registerScreens);
		eventbus.addListener(ClientEvents::registerRenderers);
		eventbus.addListener(ClientEvents::registerDimEffects);
		eventbus.addListener(ClientEvents::registerExtensions);
		eventbus.addListener(ClientEvents::registerLayerDefinition);
		eventbus.addListener(ClientEvents::particleStuff);
		eventbus.addListener(ClientEvents::registerBlockColors);
		eventbus.addListener(ClientEvents::registerReloadListeners);
		eventbus.addListener(ClientEvents::registerGeometryLoaders);
		eventbus.addListener(ClientEvents::registerOverlays);
		MainMenuEvents.init();
	}

	private static void registerOverlays(final RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.AIR_LEVEL, TheBetweenlands.prefix("decay_meter"), (graphics, deltaTracker) -> DecayBarOverlay.renderDecayBar(graphics));
		event.registerAboveAll(TheBetweenlands.prefix("fishing_minigame"), FishStaminaBarOverlay::renderFishingHud);
	}

	private static void registerScreens(final RegisterMenuScreensEvent event) {
		event.register(MenuRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableScreen::new);
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

		event.registerBlockEntityRenderer(BlockEntityRegistry.CENSER.get(), CenserRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.COMPOST_BIN.get(), CompostBinRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT.get(), CrabPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.CRAB_POT_FILTER.get(), CrabPotFilterRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.DRUID_ALTAR.get(), DruidAltarRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.FISH_TRIMMING_TABLE.get(), FishTrimmingTableRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_POT.get(), LootPotRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.LOOT_URN.get(), LootUrnRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.PURIFIER.get(), PurifierRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SIMULACRUM.get(), SimulacrumRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityRegistry.SMOKING_RACK.get(), SmokingRackRenderer::new);
	}

	private static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(SwampHagRenderer.SWAMP_HAG_MODEL_LAYER, SwampHagModel::createModelLayer);
		event.registerLayerDefinition(GeckoRenderer.GECKO_MODEL_LAYER, GeckoModel::createModelLayer);
		event.registerLayerDefinition(RenderWight.WIGHT_MODEL_LAYER, ModelWight::createModelLayer);
		event.registerLayerDefinition(BLModelLayers.BUBBLER_CRAB, BubblerCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.SILT_CRAB, SiltCrabModel::create);
		event.registerLayerDefinition(BLModelLayers.ANADIA, AnadiaModel::create);
		event.registerLayerDefinition(BLModelLayers.FISH_HOOK, BLFishHookModel::create);

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
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_1, LakeCavernSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_2, LakeCavernSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.LAKE_CAVERN_SIMULACRUM_3, LakeCavernSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_1, LootPotModels::makePot1);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_2, LootPotModels::makePot2);
		event.registerLayerDefinition(BLModelLayers.LOOT_POT_3, LootPotModels::makePot3);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_1, LootUrnModels::makeUrn1);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_2, LootUrnModels::makeUrn2);
		event.registerLayerDefinition(BLModelLayers.LOOT_URN_3, LootUrnModels::makeUrn3);
		event.registerLayerDefinition(BLModelLayers.PURIFIER, PurifierModel::makeModel);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_1, RootmanSimulacrumModels::makeSimulacrum1);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_2, RootmanSimulacrumModels::makeSimulacrum2);
		event.registerLayerDefinition(BLModelLayers.ROOTMAN_SIMULACRUM_3, RootmanSimulacrumModels::makeSimulacrum3);
		event.registerLayerDefinition(BLModelLayers.SMOKING_RACK, SmokingRackModel::makeModel);
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
			BlockRegistry.CRAB_POT.asItem(), BlockRegistry.CRAB_POT_FILTER.asItem(),
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
						RenderSystem.setShaderTexture(0, potEffect.getIcon());
						Tesselator tesselator = Tesselator.getInstance();

						BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
						builder.addVertex(x + 6, y + 6, 0).setUv(0, 0);
						builder.addVertex(x + 6, y + 6 + 20, 0).setUv(0, 1);
						builder.addVertex(x + 6 + 20, y + 6 + 20, 0).setUv(1, 1);
						builder.addVertex(x + 6 + 20, y + 6, 0).setUv(1, 0);
						BufferUploader.drawWithShader(builder.buildOrThrow());
					}
					if (potEffect.localizedElixirName == null) {
						potEffect.localizedElixirName = potEffect.getDisplayName().getString();
					}
					//TODO reimplement once book is added
//					if (ElixirPotionEffect.this.nameContainer == null) {
//						ElixirPotionEffect.this.nameContainer = new TextContainer(88, 100, this.localizedElixirName, Minecraft.getInstance().font);
//						int width = Minecraft.getInstance().font.width(ElixirPotionEffect.this.localizedElixirName);
//						float scale = 1.0F;
//						if (width > 88) {
//							scale = 88.0F / (float) width;
//							scale -= scale % 0.25F;
//						}
//						if (scale < 0.5F) {
//							scale = 0.5F;
//						}
//						ElixirPotionEffect.this.nameContainer.setCurrentScale(scale);
//						ElixirPotionEffect.this.nameContainer.setCurrentColor(0xFFFFFFFF);
//						try {
//							ElixirPotionEffect.this.nameContainer.parse();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					if (ElixirPotionEffect.this.nameContainer != null && ElixirPotionEffect.this.nameContainer.getPages().size() > 0) {
//						TextContainer.TextPage page0 = this.nameContainer.getPages().get(0);
//						page0.render(x + 28, y + 6);
//						String s = Potion.getPotionDurationString(ElixirPotionEffect.this.effect, 1.0F);
//						graphics.drawString(Minecraft.getInstance().font, s, (float) (x + 10 + 18), (float) (y + 6 + 10), 8355711, true);
//					}
					return true;
				}

				@Override
				public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
					return true;
				}

				@Override
				public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
					if (potEffect.getIcon() != null) {
						RenderSystem.enableBlend();
						RenderSystem.setShaderTexture(0, potEffect.getIcon());

						Tesselator tesselator = Tesselator.getInstance();

						BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
						builder.addVertex(x + 2, y + 2, 0).setUv(0, 0);
						builder.addVertex(x + 2, y + 2 + 20, 0).setUv(0, 1);
						builder.addVertex(x + 2 + 20, y + 2 + 20, 0).setUv(1, 1);
						builder.addVertex(x + 2 + 20, y + 2, 0).setUv(1, 0);
						BufferUploader.drawWithShader(builder.buildOrThrow());
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
	}

	@Nullable
	public static ClientLevel getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static RiftVariantReloadListener getRiftVariantLoader() {
		return riftVariantListener;
	}
}
