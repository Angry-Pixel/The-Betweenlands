package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import thebetweenlands.client.rendering.BetweenlandsSkyShaderHandler;
import thebetweenlands.client.rendering.entities.RenderGecko;
import thebetweenlands.client.rendering.entities.RenderSwampHag;
import thebetweenlands.client.rendering.entities.RenderWight;
import thebetweenlands.client.rendering.model.entity.ModelGecko;
import thebetweenlands.client.rendering.model.entity.ModelSwampHag;
import thebetweenlands.client.rendering.model.entity.ModelWight;
import thebetweenlands.client.rendering.shader.BetweenlandsShaders;
import thebetweenlands.client.rendering.shader.BetweenlandsSkyShaderInstance;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.particles.BetweenlandsParticle;
import thebetweenlands.common.particles.BetweenlandsPortalParticle;
import thebetweenlands.common.particles.CaveWaterDripParticle;
import thebetweenlands.common.registries.*;

//Class for events relying on the client side
public class ClientEvents {
	public static BetweenlandsSkyShaderHandler skyTextureHandler;

	public static void initClient(IEventBus eventbus) {
		eventbus.addListener(ClientEvents::doClientStuff);
		eventbus.addListener(ClientEvents::registerDimEffects);
		eventbus.addListener(ClientEvents::registerShaders);
		eventbus.addListener(ClientEvents::registerLayerDefinition);
		eventbus.addListener(ClientEvents::particleStuff);
		eventbus.addListener(ClientEvents::registerBlockColors);
		eventbus.addListener(ClientEvents::betweenlandsAmbienceHandler);
	}

	private static void doClientStuff(final FMLClientSetupEvent event) {
		// Client only setup

		// Entity renderers
		EntityRenderers.register(EntityRegistry.SWAMP_HAG.get(), RenderSwampHag::new);
		EntityRenderers.register(EntityRegistry.GECKO.get(), RenderGecko::new);
		EntityRenderers.register(EntityRegistry.WIGHT.get(), RenderWight::new);


		// Compile all rift textures into an atlas
		TheBetweenlands.LOGGER.info("Building rift texture atlas");
		skyTextureHandler = new BetweenlandsSkyShaderHandler(true);
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

	// Ticks every render tick
	// This is some very old code
	public static void betweenlandsAmbienceHandler(final ClientTickEvent event) {
		// Get time for shaders
		//FractinalTime += 1 / Minecraft.getInstance().getFrameTime();
		//Time = (int) FractinalTime;

		// Init vars
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ClientLevel level = Minecraft.getInstance().level;
			//double x = player.getX(), y = player.getY(), z = player.getZ();
			//BlockPos blockpos = new BlockPos(x,y,z);
			BlockPos eyeblockpos = player.eyeBlockPosition();
			//render.render
			//LOGGER.info(ModFluids.SWAMP_WATER_FLOW.get().getTags());

			// TODO: replace with a more professional solution

			if (level.dimension() == DimensionRegistries.DIMENSION_KEY) {

				rotation += 0.001f;

				// Sound
				if (player.isEyeInFluid(FluidTags.WATER) && level.getFluidState(eyeblockpos).getType().isSame(FluidRegistry.SWAMP_WATER_STILL.get().getSource())) {
					// Water state
					if (loopstate != 3) {
						loopstate = 3;
						soundManager.stopLoopedSound();
					}
					soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_WATER_LOOP.get());
				} else if (eyeblockpos.getY() <= 47) {
					// Cave state
					if (loopstate != 2) {
						loopstate = 2;
						soundManager.stopLoopedSound();
					}
					soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_CAVES_LOOP.get());
				} else {
					// Default state
					if (loopstate != 1) {
						loopstate = 1;
						soundManager.stopLoopedSound();
					}
					soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_SWAMP_LOOP.get());
				}
			} else {

				// Sound
				// Off state
				if (loopstate != 0) {
					loopstate = 0;
					soundManager.stopLoopedSound();
				}
			}
		}
	}
}
