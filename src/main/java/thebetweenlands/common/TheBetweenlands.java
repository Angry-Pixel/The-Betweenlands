package thebetweenlands.common;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.common.datagen.DataGenerators;
import thebetweenlands.common.network.*;
import thebetweenlands.common.registries.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(TheBetweenlands.ID)
public class TheBetweenlands {
	public static final String ID = "thebetweenlands";

	// debug values TODO remove
	public static float apeture = 0.53f; // start point of fog
	public static float range = 0.4f; // how far the fog reaches up to cover the sky
	public static float rotation = 0.0f; // a rotation value sent to the shader to save proc time

	public static final Logger LOGGER = LogManager.getLogger();

	public static final int LAYER_HEIGHT = 120;
	public static final int CAVE_WATER_HEIGHT = 15;
	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 30;
	public static final int CAVE_START = LAYER_HEIGHT - 10;

	public TheBetweenlands(IEventBus eventbus, Dist dist) {
		if (dist.isClient()) {
			ClientEvents.initClient(eventbus);
		}

		// Register mod contents
		SoundRegistry.SOUNDS.register(eventbus);
		ParticleRegistry.PARTICLES.register(eventbus);
		CarverRegistry.CARVER_TYPES.register(eventbus);
		BlockRegistry.BLOCKS.register(eventbus);
		ItemRegistry.ITEMS.register(eventbus);
		FluidRegistry.FLUIDS.register(eventbus);
		FluidTypeRegistry.FLUID_TYPES.register(eventbus);
		CreativeGroupRegistry.CREATIVE_TABS.register(eventbus);
		AttributeRegistry.ATTRIBUTES.register(eventbus);
		EntityRegistry.ENTITY_TYPES.register(eventbus);

		StorageRegistry.preInit();

		eventbus.addListener(this::setup);
		eventbus.addListener(DataGenerators::gatherData);
		eventbus.addListener(CreativeGroupRegistry::populateTabs);
		eventbus.addListener(this::registerPackets);
	}

	private void setup(final FMLCommonSetupEvent event) {
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("legacy_biomeprovider"), LegacyBiomeSource.CODEC);
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("betweenlands_biomeprovider"), BetweenlandsBiomeProvider.CODEC);
//		Registry.register(Registries.CHUNK_GENERATOR, TheBetweenlands.prefix("the_betweenlands_chunkgen"), ChunkGeneratorBetweenlands.CODEC);

		//TODO define these in the block jsons instead
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.PORTAL.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_GRASS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.DEAD_SWAMP_GRASS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MOSS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.POISON_IVY.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_WEEDWOOD_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WEEDWOOD_SAPLING.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUBBER_SAPLING.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.NIBBLETWIG_SAPLING.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_NIBBLETWIG_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_SAP_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_RUBBER_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_HEARTHGROVE_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SAP_SAPLING.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.THORNS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CAVE_MOSS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_REED.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.HANGER.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_TALLGRASS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
	}

	public void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(ID).versioned("1.0.0");
		registrar.playToClient(AmateMapPacket.TYPE, AmateMapPacket.STREAM_CODEC, AmateMapPacket::handle);
		registrar.playToClient(AddLocalStoragePacket.TYPE, AddLocalStoragePacket.STREAM_CODEC, AddLocalStoragePacket::handle);
		registrar.playToClient(RemoveLocalStoragePacket.TYPE, RemoveLocalStoragePacket.STREAM_CODEC, RemoveLocalStoragePacket::handle);
		registrar.playToClient(BlockGuardDataPacket.TYPE, BlockGuardDataPacket.STREAM_CODEC, BlockGuardDataPacket::handle);
		registrar.playToClient(ClearBlockGuardPacket.TYPE, ClearBlockGuardPacket.STREAM_CODEC, ClearBlockGuardPacket::handle);
		registrar.playToClient(ChangeBlockGuardSectionPacket.TYPE, ChangeBlockGuardSectionPacket.STREAM_CODEC, ChangeBlockGuardSectionPacket::handle);
		registrar.playToClient(SyncLocalStorageDataPacket.TYPE, SyncLocalStorageDataPacket.STREAM_CODEC, SyncLocalStorageDataPacket::handle);
		registrar.playToClient(SyncChunkStoragePacket.TYPE, SyncChunkStoragePacket.STREAM_CODEC, SyncChunkStoragePacket::handle);
		registrar.playToClient(SyncLocalStorageReferencesPacket.TYPE, SyncLocalStorageReferencesPacket.STREAM_CODEC, SyncLocalStorageReferencesPacket::handle);
		registrar.playToClient(SoundRipplePacket.TYPE, SoundRipplePacket.STREAM_CODEC, SoundRipplePacket::handle);
	}

	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(ID, name.toLowerCase(Locale.ROOT));
	}
}


