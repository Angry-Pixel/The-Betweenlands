package thebetweenlands.common;

import java.util.Locale;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.client.event.ClientEvents;
import thebetweenlands.common.command.GenerateAnadiaCommand;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.network.AddLocalStoragePacket;
import thebetweenlands.common.network.AmateMapPacket;
import thebetweenlands.common.network.BlockGuardDataPacket;
import thebetweenlands.common.network.ChangeBlockGuardSectionPacket;
import thebetweenlands.common.network.ChopFishPacket;
import thebetweenlands.common.network.ClearBlockGuardPacket;
import thebetweenlands.common.network.GemProtectionPacket;
import thebetweenlands.common.network.InfestWeedwoodBushPacket;
import thebetweenlands.common.network.RemoveLocalStoragePacket;
import thebetweenlands.common.network.ShockParticlePacket;
import thebetweenlands.common.network.ShowFoodSicknessPacket;
import thebetweenlands.common.network.SoundRipplePacket;
import thebetweenlands.common.network.SyncChunkStoragePacket;
import thebetweenlands.common.network.SyncLocalStorageDataPacket;
import thebetweenlands.common.network.SyncLocalStorageReferencesPacket;
import thebetweenlands.common.network.clientbound.UpdateDecayDataPacket;
import thebetweenlands.common.network.clientbound.UpdateDruidAltarProgressPacket;
import thebetweenlands.common.network.clientbound.UpdateFoodSicknessPacket;
import thebetweenlands.common.network.clientbound.UpdateGemsPacket;
import thebetweenlands.common.network.clientbound.UpdateMudWalkerPacket;
import thebetweenlands.common.network.clientbound.UpdateRotSmellPacket;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.AttributeRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CarverRegistry;
import thebetweenlands.common.registries.CreativeGroupRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.EntityPredicateRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.registries.FeatureRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootFunctionRegistry;
import thebetweenlands.common.registries.MapDecorationRegistry;
import thebetweenlands.common.registries.MenuRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SimulacrumEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.registries.StorageRegistry;
import thebetweenlands.common.world.BetweenlandsSurfaceRules;

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
		BetweenlandsEvents.init(eventbus);

		SoundRegistry.SOUNDS.register(eventbus);
		ParticleRegistry.PARTICLES.register(eventbus);
		CarverRegistry.CARVER_TYPES.register(eventbus);
		BlockRegistry.BLOCKS.register(eventbus);
		EntityRegistry.SPAWN_EGGS.register(eventbus);
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
		EnvironmentEventRegistry.EVENTS.register(eventbus);
		AdvancementCriteriaRegistry.TRIGGERS.register(eventbus);
		BetweenlandsSurfaceRules.SOURCES.register(eventbus);
		MapDecorationRegistry.DECORATIONS.register(eventbus);
		MenuRegistry.MENUS.register(eventbus);
		EntityPredicateRegistry.PREDICATES.register(eventbus);
		LootFunctionRegistry.FUNCTIONS.register(eventbus);

		StorageRegistry.preInit();

		eventbus.addListener(this::setup);
		eventbus.addListener(this::makeNewRegistries);
		eventbus.addListener(this::makeDatapackRegistries);
		eventbus.addListener(this::registerBlockEntityValidBlocks);
		eventbus.addListener(thebetweenlands.common.datagen.DataGenerators::gatherData);
		eventbus.addListener(CreativeGroupRegistry::populateTabs);
		eventbus.addListener(this::registerPackets);
		eventbus.addListener(this::registerDataMaps);
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
	}

	
	private void setup(final FMLCommonSetupEvent event) {
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("legacy_biomeprovider"), LegacyBiomeSource.CODEC);
//		Registry.register(Registries.BIOME_SOURCE, TheBetweenlands.prefix("betweenlands_biomeprovider"), BetweenlandsBiomeProvider.CODEC);
//		Registry.register(Registries.CHUNK_GENERATOR, TheBetweenlands.prefix("the_betweenlands_chunkgen"), ChunkGeneratorBetweenlands.CODEC);

		if(FMLLoader.getDist() == Dist.CLIENT) {
			setRenderLayers();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void setRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
	}

	public void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("betweenlands")
			.then(Commands.literal("debug")
				.then(GenerateAnadiaCommand.register()));
		LiteralCommandNode<CommandSourceStack> node = event.getDispatcher().register(builder);
		event.getDispatcher().register(Commands.literal("bl").redirect(node));
		event.getDispatcher().register(Commands.literal(ID).redirect(node));
	}

	public void registerBlockEntityValidBlocks(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.JUKEBOX, BlockRegistry.WEEDWOOD_JUKEBOX.get());
		event.modify(BlockEntityType.SIGN, BlockRegistry.WEEDWOOD_SIGN.get(), BlockRegistry.WEEDWOOD_WALL_SIGN.get());
	}

	public void makeDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(BLRegistries.Keys.ASPECTS, AspectType.DIRECT_CODEC, AspectType.DIRECT_CODEC);
	}

	public void makeNewRegistries(NewRegistryEvent event) {
		event.register(BLRegistries.CENSER_RECIPES);
		event.register(BLRegistries.ELIXIR_EFFECTS);
		event.register(BLRegistries.ENVIRONMENT_EVENTS);
		event.register(BLRegistries.SIMULACRUM_EFFECTS);
		event.register(BLRegistries.WORLD_STORAGE);
	}

	public void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(ID).versioned("1.0.0");
		registrar.playToClient(AmateMapPacket.TYPE, AmateMapPacket.STREAM_CODEC, AmateMapPacket::handle);
		registrar.playToClient(AddLocalStoragePacket.TYPE, AddLocalStoragePacket.STREAM_CODEC, AddLocalStoragePacket::handle);
		registrar.playToClient(RemoveLocalStoragePacket.TYPE, RemoveLocalStoragePacket.STREAM_CODEC, RemoveLocalStoragePacket::handle);
		registrar.playToClient(BlockGuardDataPacket.TYPE, BlockGuardDataPacket.STREAM_CODEC, BlockGuardDataPacket::handle);
		registrar.playToClient(ClearBlockGuardPacket.TYPE, ClearBlockGuardPacket.STREAM_CODEC, ClearBlockGuardPacket::handle);
		registrar.playToClient(ChangeBlockGuardSectionPacket.TYPE, ChangeBlockGuardSectionPacket.STREAM_CODEC, ChangeBlockGuardSectionPacket::handle);
		registrar.playToClient(InfestWeedwoodBushPacket.TYPE, InfestWeedwoodBushPacket.STREAM_CODEC, InfestWeedwoodBushPacket::handle);
		registrar.playToClient(ShowFoodSicknessPacket.TYPE, ShowFoodSicknessPacket.STREAM_CODEC, ShowFoodSicknessPacket::handle);
		registrar.playToClient(SyncLocalStorageDataPacket.TYPE, SyncLocalStorageDataPacket.STREAM_CODEC, SyncLocalStorageDataPacket::handle);
		registrar.playToClient(SyncChunkStoragePacket.TYPE, SyncChunkStoragePacket.STREAM_CODEC, SyncChunkStoragePacket::handle);
		registrar.playToClient(SyncLocalStorageReferencesPacket.TYPE, SyncLocalStorageReferencesPacket.STREAM_CODEC, SyncLocalStorageReferencesPacket::handle);
		registrar.playToClient(ShockParticlePacket.TYPE, ShockParticlePacket.STREAM_CODEC, ShockParticlePacket::handle);
		registrar.playToClient(SoundRipplePacket.TYPE, SoundRipplePacket.STREAM_CODEC, SoundRipplePacket::handle);
		registrar.playToClient(UpdateDecayDataPacket.TYPE, UpdateDecayDataPacket.STREAM_CODEC, UpdateDecayDataPacket::handle);
		registrar.playToClient(UpdateMudWalkerPacket.TYPE, UpdateMudWalkerPacket.STREAM_CODEC, UpdateMudWalkerPacket::handle);
		registrar.playToClient(UpdateRotSmellPacket.TYPE, UpdateRotSmellPacket.STREAM_CODEC, UpdateRotSmellPacket::handle);
		registrar.playToClient(UpdateFoodSicknessPacket.TYPE, UpdateFoodSicknessPacket.STREAM_CODEC, UpdateFoodSicknessPacket::handle);
		registrar.playToClient(UpdateGemsPacket.TYPE, UpdateGemsPacket.STREAM_CODEC, UpdateGemsPacket::handle);
		registrar.playToClient(GemProtectionPacket.TYPE, GemProtectionPacket.STREAM_CODEC, GemProtectionPacket::handle);
		registrar.playToClient(UpdateDruidAltarProgressPacket.TYPE, UpdateDruidAltarProgressPacket.STREAM_CODEC, UpdateDruidAltarProgressPacket::handle);

		registrar.playToServer(ChopFishPacket.TYPE, ChopFishPacket.STREAM_CODEC, (payload, context) -> ChopFishPacket.handle(context));
	}

	public void registerDataMaps(RegisterDataMapTypesEvent event) {
		event.register(DataMapRegistry.DECAY_FOOD);
		event.register(DataMapRegistry.FLUX_MULTIPLIER);
	}

	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(ID, name.toLowerCase(Locale.ROOT));
	}

	@Nullable
	public static Level getLevelWorkaround(ResourceKey<Level> dimension) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.getLevel(dimension);
		} else if (FMLLoader.getDist().isClient()) {
			return ClientEvents.getClientLevelWhereThisCouldBeDedicated();
		}
		return null;
	}

	@Nullable
	public static ServerLevel tryGetServer(ResourceKey<Level> dimension) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.getLevel(dimension);
		}
		return null;
	}
}


