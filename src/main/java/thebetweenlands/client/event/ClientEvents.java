package thebetweenlands.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
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
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.client.*;
import thebetweenlands.client.model.baked.RootGeometry;
import thebetweenlands.client.model.block.DeepmanSimulacrumModel1;
import thebetweenlands.client.model.block.DeepmanSimulacrumModel2;
import thebetweenlands.client.model.block.DeepmanSimulacrumModel3;
import thebetweenlands.client.model.entity.GeckoModel;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.model.entity.SwampHagModel;
import thebetweenlands.client.particle.BetweenlandsParticle;
import thebetweenlands.client.particle.BetweenlandsPortalParticle;
import thebetweenlands.client.renderer.block.BLItemRenderer;
import thebetweenlands.client.renderer.block.SimulacrumRenderer;
import thebetweenlands.client.renderer.entity.GeckoRenderer;
import thebetweenlands.client.renderer.entity.RenderWight;
import thebetweenlands.client.renderer.entity.SwampHagRenderer;
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
		eventbus.addListener(ClientEvents::registerRenderers);
		eventbus.addListener(ClientEvents::registerDimEffects);
		eventbus.addListener(ClientEvents::registerExtensions);
		eventbus.addListener(ClientEvents::registerLayerDefinition);
		eventbus.addListener(ClientEvents::particleStuff);
		eventbus.addListener(ClientEvents::registerBlockColors);
		eventbus.addListener(ClientEvents::registerReloadListeners);
		eventbus.addListener(ClientEvents::registerGeometryLoaders);
		MainMenuEvents.init();
	}

	private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.SWAMP_HAG.get(), SwampHagRenderer::new);
		event.registerEntityRenderer(EntityRegistry.GECKO.get(), GeckoRenderer::new);
		event.registerEntityRenderer(EntityRegistry.WIGHT.get(), RenderWight::new);

		event.registerBlockEntityRenderer(BlockEntityRegistry.SIMULACRUM.get(), SimulacrumRenderer::new);
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
			BlockRegistry.DEEPMAN_SIMULACRUM_1.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_2.asItem(), BlockRegistry.DEEPMAN_SIMULACRUM_3.asItem());

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

	private static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(SwampHagRenderer.SWAMP_HAG_MODEL_LAYER, SwampHagModel::createModelLayer);
		event.registerLayerDefinition(GeckoRenderer.GECKO_MODEL_LAYER, GeckoModel::createModelLayer);
		event.registerLayerDefinition(RenderWight.WIGHT_MODEL_LAYER, ModelWight::createModelLayer);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_1, DeepmanSimulacrumModel1::makeModel);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_2, DeepmanSimulacrumModel2::makeModel);
		event.registerLayerDefinition(BLModelLayers.DEEPMAN_SIMULACRUM_3, DeepmanSimulacrumModel3::makeModel);
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
