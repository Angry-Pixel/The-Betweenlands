package thebetweenlands.common;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.common.datagen.DataGenerators;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.network.*;
import thebetweenlands.common.registries.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(TheBetweenlands.ID)
public class TheBetweenlands {
	public static final String ID = "thebetweenlands";

	public static final Logger LOGGER = LogManager.getLogger();

	public static final GameRules.Key<GameRules.BooleanValue> FOOD_SICKNESS_GAMERULE = GameRules.register("blFoodSickness", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> ROTTEN_FOOD_GAMERULE = GameRules.register("blRottenFood", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> DECAY_GAMERULE = GameRules.register("blDecay", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> CORROSION_GAMERULE = GameRules.register("blCorrosion", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> TOOL_WEAKNESS_GAMERULE = GameRules.register("blToolWeakness", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> TORCH_BLACKLIST_GAMERULE = GameRules.register("blTorchBlacklist", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> FIRE_TOOL_GAMERULE = GameRules.register("blFireToolBlacklist", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> POTION_GAMERULE = GameRules.register("blPotionBlacklist", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> FERTILIZER_GAMERULE = GameRules.register("blFertilizerBlacklist", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> TIMED_EVENT_GAMERULE = GameRules.register("blTimedEvents", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

	public static final int LAYER_HEIGHT = 120;
	public static final int CAVE_WATER_HEIGHT = 15;
	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 30;
	public static final int CAVE_START = LAYER_HEIGHT - 10;

	public TheBetweenlands(IEventBus eventbus, Dist dist) {
		if (dist.isClient()) {
			ClientEvents.initClient(eventbus);
		}

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
		ElixirEffectRegistry.ELIXIRS.register(eventbus);
		ElixirEffectRegistry.EFFECTS.register(eventbus);
		AttachmentRegistry.ATTACHMENT_TYPES.register(eventbus);
		ArmorMaterialRegistry.MATERIALS.register(eventbus);
		BlockEntityRegistry.BLOCK_ENTITIES.register(eventbus);
		DataComponentRegistry.COMPONENTS.register(eventbus);
		RecipeRegistry.RECIPE_TYPES.register(eventbus);
		RecipeRegistry.RECIPE_SERIALIZERS.register(eventbus);
		FeatureRegistry.FEATURES.register(eventbus);
		SimulacrumEffectRegistry.EFFECTS.register(eventbus);

		StorageRegistry.preInit();

		eventbus.addListener(this::setup);
		eventbus.addListener(this::registerBlockEntityValidBlocks);
		eventbus.addListener(DataGenerators::gatherData);
		eventbus.addListener(CreativeGroupRegistry::populateTabs);
		eventbus.addListener(this::registerPackets);
	}

	@SuppressWarnings("deprecation") // TODO: remove once the jsons are done
	private void setup(final FMLCommonSetupEvent event) {
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("legacy_biomeprovider"), LegacyBiomeSource.CODEC);
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("betweenlands_biomeprovider"), BetweenlandsBiomeProvider.CODEC);
//		Registry.register(Registries.CHUNK_GENERATOR, TheBetweenlands.prefix("the_betweenlands_chunkgen"), ChunkGeneratorBetweenlands.CODEC);

		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
	}

	public void registerBlockEntityValidBlocks(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.JUKEBOX, BlockRegistry.WEEDWOOD_JUKEBOX.get());
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
		registrar.playToClient(ShockParticlePacket.TYPE, ShockParticlePacket.STREAM_CODEC, ShockParticlePacket::handle);
		registrar.playToClient(SoundRipplePacket.TYPE, SoundRipplePacket.STREAM_CODEC, SoundRipplePacket::handle);
		registrar.playToClient(UpdateDecayDataPacket.TYPE, UpdateDecayDataPacket.STREAM_CODEC, UpdateDecayDataPacket::handle);
		registrar.playToClient(UpdateMudWalkerPacket.TYPE, UpdateMudWalkerPacket.STREAM_CODEC, UpdateMudWalkerPacket::handle);
		registrar.playToClient(UpdateRotSmellPacket.TYPE, UpdateRotSmellPacket.STREAM_CODEC, UpdateRotSmellPacket::handle);
		registrar.playToClient(UpdateGemsPacket.TYPE, UpdateGemsPacket.STREAM_CODEC, UpdateGemsPacket::handle);
		registrar.playToClient(GemProtectionPacket.TYPE, GemProtectionPacket.STREAM_CODEC, GemProtectionPacket::handle);
		registrar.playToClient(UpdateDruidAltarProgressPacket.TYPE, UpdateDruidAltarProgressPacket.STREAM_CODEC, UpdateDruidAltarProgressPacket::handle);
	}

	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(ID, name.toLowerCase(Locale.ROOT));
	}
}


