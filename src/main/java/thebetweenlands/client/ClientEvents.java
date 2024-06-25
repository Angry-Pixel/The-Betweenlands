package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.renderer.entity.RenderGecko;
import thebetweenlands.client.renderer.entity.RenderSwampHag;
import thebetweenlands.client.renderer.entity.RenderWight;
import thebetweenlands.client.model.entity.ModelGecko;
import thebetweenlands.client.model.entity.ModelSwampHag;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.renderer.shader.BetweenlandsShaders;
import thebetweenlands.client.renderer.shader.BetweenlandsSkyShaderInstance;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.client.particle.BetweenlandsParticle;
import thebetweenlands.client.particle.BetweenlandsPortalParticle;
import thebetweenlands.client.particle.CaveWaterDripParticle;
import thebetweenlands.common.registries.*;

//Class for events relying on the client side
public class ClientEvents {

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientEvents::doClientStuff);
		eventbus.addListener(ClientEvents::registerDimEffects);
		eventbus.addListener(ClientEvents::registerShaders);
		eventbus.addListener(ClientEvents::registerLayerDefinition);
		eventbus.addListener(ClientEvents::particleStuff);
		eventbus.addListener(ClientEvents::registerBlockColors);
	}

	private static void doClientStuff(final FMLClientSetupEvent event) {
		// Client only setup

		// Entity renderers
		EntityRenderers.register(EntityRegistry.SWAMP_HAG.get(), RenderSwampHag::new);
		EntityRenderers.register(EntityRegistry.GECKO.get(), RenderGecko::new);
		EntityRenderers.register(EntityRegistry.WIGHT.get(), RenderWight::new);
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
		// Changes particle and block colors
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_WEEDWOOD_TREE.get());
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_NIBBLETWIG_TREE.get());
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_RUBBER_TREE.get());
		event.register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_GRASS.get());
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.POISON_IVY.get());
		event.register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_REED.get());
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.MOSS.get());
		event.register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.HANGER.get());
		event.register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_TALLGRASS.get());

		// Only effects particle colors
		event.register(BlockColorRegistry.SWAMP_WATER, BlockRegistry.SWAMP_WATER_BLOCK.get());
	}

	@Nullable
	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
