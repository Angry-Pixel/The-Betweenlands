package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.*;
import javax.annotation.Nullable;

import thebetweenlands.client.renderer.entity.RenderGecko;
import thebetweenlands.client.renderer.entity.RenderSwampHag;
import thebetweenlands.client.renderer.entity.RenderWight;
import thebetweenlands.client.renderer.model.baked.RootGeometry;
import thebetweenlands.client.model.entity.ModelGecko;
import thebetweenlands.client.model.entity.ModelSwampHag;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.renderer.shader.BetweenlandsShaders;
import thebetweenlands.client.renderer.shader.BetweenlandsSkyShaderInstance;
import thebetweenlands.client.particle.BetweenlandsParticle;
import thebetweenlands.client.particle.BetweenlandsPortalParticle;
import thebetweenlands.client.particle.CaveWaterDripParticle;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.*;

public class ClientEvents {

	// Wouldn't work with normal SubscribeEvents
	@EventBusSubscriber(modid = TheBetweenlands.ID, value = Dist.CLIENT, bus = Bus.MOD)
	public static class ModBusClientEvents {
		
		@SubscribeEvent
		public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
			event.register(TheBetweenlands.prefix("root"), RootGeometry.RootGeometryLoader.INSTANCE);
		}
		
	}
	
	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;

	private static RiftVariantReloadListener riftVariantListener;

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientEvents::registerRenderers);
		eventbus.addListener(ClientEvents::registerDimEffects);
//		eventbus.addListener(ClientEvents::registerShaders);
		eventbus.addListener(ClientEvents::registerLayerDefinition);
		eventbus.addListener(ClientEvents::particleStuff);
		eventbus.addListener(ClientEvents::registerBlockColors);
		eventbus.addListener(ClientEvents::registerReloadListeners);
	}

	private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.SWAMP_HAG.get(), RenderSwampHag::new);
		event.registerEntityRenderer(EntityRegistry.GECKO.get(), RenderGecko::new);
		event.registerEntityRenderer(EntityRegistry.WIGHT.get(), RenderWight::new);
	}

	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(riftVariantListener = new RiftVariantReloadListener());
	}

	public static void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
		event.register(DimensionRegistries.DIMENSION_RENDERER, new BetweenlandsSpecialEffects());
	}

	private static void registerShaders(final RegisterShadersEvent event) {
		//BetweenlandsShaderInstance betweenlandssky = new BetweenlandsShaderInstance(event.getResourceManager(), TheBetweenlands.prefix("starfield"), BetweenlandsVertexFormats.BETWEENLANDS_SKY);

		// todo: sky shader "starfield" name is being changed to "betweenlandsSky"
		BetweenlandsShaders.preloadShaders(event.getResourceProvider());
		event.registerShader(BetweenlandsShaders.BetweenlandsSky, BetweenlandsSkyShaderInstance.onLoad);
	}

	private static void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RenderSwampHag.SWAMP_HAG_MODEL_LAYER, ModelSwampHag::createModelLayer);
		event.registerLayerDefinition(RenderGecko.GECKO_MODEL_LAYER, ModelGecko::createModelLayer);
		event.registerLayerDefinition(RenderWight.WIGHT_MODEL_LAYER, ModelWight::createModelLayer);
	}

	private static void particleStuff(final RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ParticleRegistry.SULFUR_GENERIC.get(), BetweenlandsParticle.Helper::new);
		event.registerSpriteSet(ParticleRegistry.PORTAL_EFFECT.get(), BetweenlandsPortalParticle.Helper::new);
		event.registerSpriteSet(ParticleRegistry.CAVE_WATER_DRIP.get(), CaveWaterDripParticle.Factory::new);
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
