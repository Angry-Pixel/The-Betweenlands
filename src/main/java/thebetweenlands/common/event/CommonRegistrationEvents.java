package thebetweenlands.common.event;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.VanillaHopperItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.util.ItemHandlerProvidingBlockEntity;
import thebetweenlands.common.capability.CenserWrapper;
import thebetweenlands.common.capability.MothHouseWrapper;
import thebetweenlands.common.command.AspectCommand;
import thebetweenlands.common.command.EventCommand;
import thebetweenlands.common.command.GenerateAnadiaCommand;
import thebetweenlands.common.command.ResetAspectsCommand;
import thebetweenlands.common.datagen.BLAdvancementGenerator;
import thebetweenlands.common.datagen.BLAtlasProvider;
import thebetweenlands.common.datagen.BLBlockStateProvider;
import thebetweenlands.common.datagen.BLDataMapProvider;
import thebetweenlands.common.datagen.BLItemModelProvider;
import thebetweenlands.common.datagen.BLLanguageProvider;
import thebetweenlands.common.datagen.BLRecipeProvider;
import thebetweenlands.common.datagen.BLRegistryProvider;
import thebetweenlands.common.datagen.BLSoundDefinitionProvider;
import thebetweenlands.common.datagen.loot.BLLootProvider;
import thebetweenlands.common.datagen.tags.BLBiomeTagProvider;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.datagen.tags.BLDamageTagProvider;
import thebetweenlands.common.datagen.tags.BLDimensionTypeTagProvider;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.dispenser.BetweenlandsDispenserBehaviours;
import thebetweenlands.common.entity.creature.frog.FrogVariant;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.network.clientbound.AddBetweenlandsBossBarPacket;
import thebetweenlands.common.network.clientbound.AddLocalStoragePacket;
import thebetweenlands.common.network.clientbound.AmateMapPacket;
import thebetweenlands.common.network.clientbound.BlockGuardDataPacket;
import thebetweenlands.common.network.clientbound.ChangeBlockGuardSectionPacket;
import thebetweenlands.common.network.clientbound.ClearBlockGuardPacket;
import thebetweenlands.common.network.clientbound.DruidParticlePacket;
import thebetweenlands.common.network.clientbound.GemProtectionPacket;
import thebetweenlands.common.network.clientbound.InfestWeedwoodBushPacket;
import thebetweenlands.common.network.clientbound.LivingWeedwoodShieldSpitPacket;
import thebetweenlands.common.network.clientbound.OpenHerbloreBookPacket;
import thebetweenlands.common.network.clientbound.OpenLoreScrapPacket;
import thebetweenlands.common.network.clientbound.OpenRenameScreenPacket;
import thebetweenlands.common.network.clientbound.RemoveBetweenlandsBossBarPacket;
import thebetweenlands.common.network.clientbound.RemoveLocalStoragePacket;
import thebetweenlands.common.network.clientbound.RiftSoundPacket;
import thebetweenlands.common.network.clientbound.ShockArrowHitPacket;
import thebetweenlands.common.network.clientbound.ShockParticlePacket;
import thebetweenlands.common.network.clientbound.ShowFoodSicknessPacket;
import thebetweenlands.common.network.clientbound.SoundRipplePacket;
import thebetweenlands.common.network.clientbound.SummonPeatMummyParticlesPacket;
import thebetweenlands.common.network.clientbound.SyncChunkStoragePacket;
import thebetweenlands.common.network.clientbound.SyncEnvironmentEventDataPacket;
import thebetweenlands.common.network.clientbound.SyncLocalStorageDataPacket;
import thebetweenlands.common.network.clientbound.SyncLocalStorageReferencesPacket;
import thebetweenlands.common.network.clientbound.SyncStaticAspectsPacket;
import thebetweenlands.common.network.clientbound.UpdateBetweenlandsBossBarPacket;
import thebetweenlands.common.network.clientbound.UpdateDruidAltarProgressPacket;
import thebetweenlands.common.network.clientbound.WeedwoodBushRustlePacket;
import thebetweenlands.common.network.clientbound.WightVolatileParticlesPacket;
import thebetweenlands.common.network.serverbound.ChiromawDoubleJumpPacket;
import thebetweenlands.common.network.serverbound.ChopFishPacket;
import thebetweenlands.common.network.serverbound.EquipItemPacket;
import thebetweenlands.common.network.serverbound.ExtendedReachAttackPacket;
import thebetweenlands.common.network.serverbound.OpenPouchPacket;
import thebetweenlands.common.network.serverbound.RenameItemPacket;
import thebetweenlands.common.network.serverbound.SetGalleryUrlPacket;
import thebetweenlands.common.network.serverbound.SetLastPageDataPacket;
import thebetweenlands.common.network.serverbound.UpdateRingStatePacket;
import thebetweenlands.common.registries.AttributeRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;

public class CommonRegistrationEvents {

	public static void init(IEventBus bus) {
		bus.addListener(CommonRegistrationEvents::commonSetup);
		bus.addListener(CommonRegistrationEvents::createDatagen);
		bus.addListener(CommonRegistrationEvents::makeNewRegistries);
		bus.addListener(CommonRegistrationEvents::extraRegistration);
		bus.addListener(CommonRegistrationEvents::makeDatapackRegistries);
		bus.addListener(CommonRegistrationEvents::populateVanillaTabs);
		bus.addListener(CommonRegistrationEvents::registerAttributes);
		bus.addListener(CommonRegistrationEvents::registerExtraAttributes);
		bus.addListener(CommonRegistrationEvents::registerBlockEntityValidBlocks);
		bus.addListener(CommonRegistrationEvents::registerPackets);
		bus.addListener(CommonRegistrationEvents::registerDataMaps);
		bus.addListener(CommonRegistrationEvents::registerCapabilities);

		NeoForge.EVENT_BUS.addListener(CommonRegistrationEvents::registerCommands);

		CommonEvents.init();
	}

	private static void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			BetweenlandsDispenserBehaviours.registerBehaviours();

			FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;

			pot.addPlant(BlockRegistry.WEEDWOOD_SAPLING.getId(), BlockRegistry.POTTED_WEEDWOOD_SAPLING);
			pot.addPlant(BlockRegistry.SAP_SAPLING.getId(), BlockRegistry.POTTED_SAP_SAPLING);
			pot.addPlant(BlockRegistry.RUBBER_SAPLING.getId(), BlockRegistry.POTTED_RUBBER_SAPLING);
			pot.addPlant(BlockRegistry.HEARTHGROVE_SAPLING.getId(), BlockRegistry.POTTED_HEARTHGROVE_SAPLING);
			pot.addPlant(BlockRegistry.NIBBLETWIG_SAPLING.getId(), BlockRegistry.POTTED_NIBBLETWIG_SAPLING);
			pot.addPlant(BlockRegistry.SPIRIT_TREE_SAPLING.getId(), BlockRegistry.POTTED_SPIRIT_TREE_SAPLING);

			pot.addPlant(BlockRegistry.ARROW_ARUM.getId(), BlockRegistry.POTTED_ARROW_ARUM);
			pot.addPlant(BlockRegistry.BLUE_IRIS.getId(), BlockRegistry.POTTED_BLUE_IRIS);
			pot.addPlant(BlockRegistry.BONESET.getId(), BlockRegistry.POTTED_BONESET);
			pot.addPlant(BlockRegistry.BUTTON_BUSH.getId(), BlockRegistry.POTTED_BUTTON_BUSH);
			pot.addPlant(BlockRegistry.COPPER_IRIS.getId(), BlockRegistry.POTTED_COPPER_IRIS);
			pot.addPlant(BlockRegistry.DEAD_WEEDWOOD_BUSH.getId(), BlockRegistry.POTTED_DEAD_WEEDWOOD_BUSH);
			pot.addPlant(BlockRegistry.FLOWERED_NETTLE.getId(), BlockRegistry.POTTED_FLOWERED_NETTLE);
			pot.addPlant(BlockRegistry.MARSH_HIBISCUS.getId(), BlockRegistry.POTTED_MARSH_HIBISCUS);
			pot.addPlant(BlockRegistry.MARSH_MALLOW.getId(), BlockRegistry.POTTED_MARSH_MALLOW);
			pot.addPlant(BlockRegistry.MILKWEED.getId(), BlockRegistry.POTTED_MILKWEED);
			pot.addPlant(BlockRegistry.NETTLE.getId(), BlockRegistry.POTTED_NETTLE);
			pot.addPlant(BlockRegistry.PICKERELWEED.getId(), BlockRegistry.POTTED_PICKERELWEED);
		});
	}

	private static void createDatagen(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		//Belongs in /assets/
		boolean assets = event.includeClient();
		//Belongs in /data/
		boolean data = event.includeServer();

		// Registry
		BLRegistryProvider datapack = new BLRegistryProvider(output, event.getLookupProvider());
		CompletableFuture<HolderLookup.Provider> dataProvider = datapack.getRegistryProvider();
		gen.addProvider(data, datapack);

		// Tags
		BLBlockTagProvider blockTags = new BLBlockTagProvider(output, dataProvider, helper);
		gen.addProvider(data, blockTags);
		gen.addProvider(data, new BLEntityTagProvider(output, dataProvider, helper));
		gen.addProvider(data, new BLItemTagProvider(output, dataProvider, blockTags.contentsGetter(), helper));
		gen.addProvider(data, new BLFluidTagGenerator(output, dataProvider, helper));
		gen.addProvider(data, new BLBiomeTagProvider(output, dataProvider, helper));
		gen.addProvider(data, new BLDimensionTypeTagProvider(output, dataProvider, helper));
		gen.addProvider(data, new BLDamageTagProvider(output, dataProvider, helper));

		// Misc Data
		gen.addProvider(data, new BLRecipeProvider(output, dataProvider));
		gen.addProvider(data, new BLLootProvider(output, dataProvider));
		gen.addProvider(data, new BLDataMapProvider(output, dataProvider));
		gen.addProvider(data, new BLAdvancementGenerator(output, dataProvider, helper));

		// Assets
		gen.addProvider(assets, new BLAtlasProvider(output, dataProvider, helper));
		gen.addProvider(assets, new BLBlockStateProvider(output, helper));
		gen.addProvider(assets, new BLItemModelProvider(output, helper));
		gen.addProvider(assets, new BLSoundDefinitionProvider(output, helper));
		gen.addProvider(assets, new BLLanguageProvider(output));
	}

	private static void populateVanillaTabs(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			for (DeferredHolder<Item, ? extends Item> bucket : ItemRegistry.ITEMS.getEntries().stream().filter(item -> item.get() instanceof BucketItem).toList()) {
				event.accept(new ItemStack(bucket.get()));
			}
		}
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			EntityRegistry.SPAWN_EGGS.getEntries().stream().sorted(Comparator.comparing(DeferredHolder::getId)).forEach(item -> event.accept(new ItemStack(item)));
		}
	}

	@SuppressWarnings("unchecked") //entities added this way will always extend LivingEntity
	private static void registerAttributes(EntityAttributeCreationEvent event) {
		EntityRegistry.ATTRIBUTES.forEach((type, builder) -> event.put((EntityType<? extends LivingEntity>) type.value(), builder.get().build()));
	}

	private static void registerExtraAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, AttributeRegistry.DECAY_RESISTANCE, 0.0);
		event.add(EntityType.PLAYER, AttributeRegistry.CORROSION_RESISTANCE, 0.0);
	}

	private static void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("betweenlands")
			.then(Commands.literal("debug")
				.then(GenerateAnadiaCommand.register()))
			.then(AspectCommand.register())
			.then(EventCommand.register())
			.then(ResetAspectsCommand.register());
		LiteralCommandNode<CommandSourceStack> node = event.getDispatcher().register(builder);
		event.getDispatcher().register(Commands.literal("bl").redirect(node));
		event.getDispatcher().register(Commands.literal(TheBetweenlands.ID).redirect(node));
	}

	private static void registerBlockEntityValidBlocks(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.JUKEBOX, BlockRegistry.WEEDWOOD_JUKEBOX.get());
		event.modify(BlockEntityType.SIGN, BlockRegistry.WEEDWOOD_SIGN.get(), BlockRegistry.WEEDWOOD_WALL_SIGN.get());
	}

	private static void makeDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
		// Synced registries
		event.dataPackRegistry(BLRegistries.Keys.ASPECT_ITEMS,   AspectItem.DIRECT_CODEC,  AspectItem.DIRECT_CODEC);
		event.dataPackRegistry(BLRegistries.Keys.ASPECT_TYPES,   AspectType.DIRECT_CODEC,  AspectType.DIRECT_CODEC);
		event.dataPackRegistry(BLRegistries.Keys.ELIXIR_RECIPES, ElixirRecipe.CODEC,       ElixirRecipe.CODEC);
		event.dataPackRegistry(BLRegistries.Keys.FROG_VARIANT,   FrogVariant.DIRECT_CODEC, FrogVariant.DIRECT_CODEC);

		// Unsynced registries
		event.dataPackRegistry(BLRegistries.Keys.CONFIGURED_GENERATORS, ConfiguredEarlyGenerator.DIRECT_CODEC);
	}

	private static void makeNewRegistries(NewRegistryEvent event) {
		event.register(BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES);
		event.register(BLRegistries.ASPECT_CALCULATOR_TYPE);
		event.register(BLRegistries.CENSER_RECIPES);
		event.register(BLRegistries.ELIXIR_EFFECTS);
		event.register(BLRegistries.ENVIRONMENT_EVENTS);
		event.register(BLRegistries.SIMULACRUM_EFFECTS);
		event.register(BLRegistries.WORLD_STORAGE);

		event.register(BLRegistries.EARLY_GENERATORS);
		event.register(BLRegistries.BIOME_LAYER_TYPE);
	}

	private static void extraRegistration(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.BIOME_SOURCE)) {
			Registry.register(BuiltInRegistries.BIOME_SOURCE, TheBetweenlands.prefix("bl_biome_source"), BetweenlandsBiomeSource.BL_CODEC);
		}
		if (event.getRegistryKey().equals(Registries.CHUNK_GENERATOR)) {
			Registry.register(BuiltInRegistries.CHUNK_GENERATOR, TheBetweenlands.prefix("bl_chunk_generator"), BetweenlandsChunkGenerator.BL_CODEC);
		}
	}

	private static void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(TheBetweenlands.ID).versioned("1.0.0");
		registrar.playToClient(AmateMapPacket.TYPE, AmateMapPacket.STREAM_CODEC, AmateMapPacket::handle);
		registrar.playToClient(AddLocalStoragePacket.TYPE, AddLocalStoragePacket.STREAM_CODEC, AddLocalStoragePacket::handle);
		registrar.playToClient(RemoveLocalStoragePacket.TYPE, RemoveLocalStoragePacket.STREAM_CODEC, RemoveLocalStoragePacket::handle);
		registrar.playToClient(BlockGuardDataPacket.TYPE, BlockGuardDataPacket.STREAM_CODEC, BlockGuardDataPacket::handle);
		registrar.playToClient(ClearBlockGuardPacket.TYPE, ClearBlockGuardPacket.STREAM_CODEC, ClearBlockGuardPacket::handle);
		registrar.playToClient(ChangeBlockGuardSectionPacket.TYPE, ChangeBlockGuardSectionPacket.STREAM_CODEC, ChangeBlockGuardSectionPacket::handle);
		registrar.playToClient(DruidParticlePacket.TYPE, DruidParticlePacket.STREAM_CODEC, DruidParticlePacket::handle);
		registrar.playToClient(InfestWeedwoodBushPacket.TYPE, InfestWeedwoodBushPacket.STREAM_CODEC, InfestWeedwoodBushPacket::handle);
		registrar.playToClient(OpenLoreScrapPacket.TYPE, OpenLoreScrapPacket.STREAM_CODEC, OpenLoreScrapPacket::handle);
		registrar.playToClient(ShowFoodSicknessPacket.TYPE, ShowFoodSicknessPacket.STREAM_CODEC, ShowFoodSicknessPacket::handle);
		registrar.playToClient(SyncLocalStorageDataPacket.TYPE, SyncLocalStorageDataPacket.STREAM_CODEC, SyncLocalStorageDataPacket::handle);
		registrar.playToClient(SyncChunkStoragePacket.TYPE, SyncChunkStoragePacket.STREAM_CODEC, SyncChunkStoragePacket::handle);
		registrar.playToClient(SyncLocalStorageReferencesPacket.TYPE, SyncLocalStorageReferencesPacket.STREAM_CODEC, SyncLocalStorageReferencesPacket::handle);
		registrar.playToClient(ShockParticlePacket.TYPE, ShockParticlePacket.STREAM_CODEC, ShockParticlePacket::handle);
		registrar.playToClient(SoundRipplePacket.TYPE, SoundRipplePacket.STREAM_CODEC, SoundRipplePacket::handle);
		registrar.playToClient(GemProtectionPacket.TYPE, GemProtectionPacket.STREAM_CODEC, GemProtectionPacket::handle);
		registrar.playToClient(UpdateDruidAltarProgressPacket.TYPE, UpdateDruidAltarProgressPacket.STREAM_CODEC, UpdateDruidAltarProgressPacket::handle);
		registrar.playToClient(OpenHerbloreBookPacket.TYPE, OpenHerbloreBookPacket.STREAM_CODEC, OpenHerbloreBookPacket::handle);
		registrar.playToClient(OpenRenameScreenPacket.TYPE, OpenRenameScreenPacket.STREAM_CODEC, OpenRenameScreenPacket::handle);
		registrar.playToClient(LivingWeedwoodShieldSpitPacket.TYPE, LivingWeedwoodShieldSpitPacket.STREAM_CODEC, LivingWeedwoodShieldSpitPacket::handle);
		registrar.playToClient(SyncEnvironmentEventDataPacket.TYPE, SyncEnvironmentEventDataPacket.STREAM_CODEC, SyncEnvironmentEventDataPacket::handle);
		registrar.playToClient(SyncStaticAspectsPacket.TYPE, SyncStaticAspectsPacket.STREAM_CODEC, SyncStaticAspectsPacket::handle);
		registrar.playToClient(SummonPeatMummyParticlesPacket.TYPE, SummonPeatMummyParticlesPacket.STREAM_CODEC, SummonPeatMummyParticlesPacket::handle);
		registrar.playToClient(AddBetweenlandsBossBarPacket.TYPE, AddBetweenlandsBossBarPacket.STREAM_CODEC, AddBetweenlandsBossBarPacket::handle);
		registrar.playToClient(UpdateBetweenlandsBossBarPacket.TYPE, UpdateBetweenlandsBossBarPacket.STREAM_CODEC, UpdateBetweenlandsBossBarPacket::handle);
		registrar.playToClient(RemoveBetweenlandsBossBarPacket.TYPE, RemoveBetweenlandsBossBarPacket.STREAM_CODEC, RemoveBetweenlandsBossBarPacket::handle);
		registrar.playToClient(WeedwoodBushRustlePacket.TYPE, WeedwoodBushRustlePacket.STREAM_CODEC, WeedwoodBushRustlePacket::handle);
		registrar.playToClient(WightVolatileParticlesPacket.TYPE, WightVolatileParticlesPacket.STREAM_CODEC, WightVolatileParticlesPacket::handle);
		registrar.playToClient(RiftSoundPacket.TYPE, RiftSoundPacket.STREAM_CODEC, RiftSoundPacket::handle);
		registrar.playToClient(ShockArrowHitPacket.TYPE, ShockArrowHitPacket.STREAM_CODEC, ShockArrowHitPacket::handle);

		registrar.playToServer(ChiromawDoubleJumpPacket.TYPE, ChiromawDoubleJumpPacket.STREAM_CODEC, ChiromawDoubleJumpPacket::handle);
		registrar.playToServer(ChopFishPacket.TYPE, ChopFishPacket.STREAM_CODEC, (payload, context) -> ChopFishPacket.handle(context));
		registrar.playToServer(ExtendedReachAttackPacket.TYPE, ExtendedReachAttackPacket.STREAM_CODEC, ExtendedReachAttackPacket::handle);
		registrar.playToServer(EquipItemPacket.TYPE, EquipItemPacket.STREAM_CODEC, EquipItemPacket::handle);
		registrar.playToServer(OpenPouchPacket.TYPE, OpenPouchPacket.STREAM_CODEC, OpenPouchPacket::handle);
		registrar.playToServer(RenameItemPacket.TYPE, RenameItemPacket.STREAM_CODEC, RenameItemPacket::handle);
		registrar.playToServer(SetGalleryUrlPacket.TYPE, SetGalleryUrlPacket.STREAM_CODEC, SetGalleryUrlPacket::handle);
		registrar.playToServer(SetLastPageDataPacket.TYPE, SetLastPageDataPacket.STREAM_CODEC, SetLastPageDataPacket::handle);
		registrar.playToServer(UpdateRingStatePacket.TYPE, UpdateRingStatePacket.STREAM_CODEC, UpdateRingStatePacket::handle);
	}

	private static void registerDataMaps(RegisterDataMapTypesEvent event) {
		event.register(DataMapRegistry.AMULET_SPAWNS);
		event.register(DataMapRegistry.COMPOSTABLE);
		event.register(DataMapRegistry.DECAY_FOOD);
		event.register(DataMapRegistry.FLUX_MULTIPLIER);
		event.register(DataMapRegistry.LIGHTNING_CONVERSION);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.BARREL.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.CENSER.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.GRUB_HUB.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FILTERED_SILT_GLASS_JAR.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.INFUSER.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.PURIFIER.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.RUBBER_TAP.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.STEEPING_POT.get(), (tile, context) -> tile);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.WATER_FILTER.get(), (tile, context) -> tile);

		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ANIMATOR.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SULFUR_FURNACE.get(), (tile, context) -> new SidedInvWrapper(tile, context));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CENSER.get(), (tile, context) -> new CenserWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CRAB_POT.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.COMPOST_BIN.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CRAB_POT_FILTER.get(), (tile, context) -> new SidedInvWrapper(tile, context));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.DRUID_ALTAR.get(), (tile, context) -> new SidedInvWrapper(tile, context));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FISHING_TACKLE_BOX.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.GRUB_HUB.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.INFUSER.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_SHELF.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.LOOT_POT.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.LOOT_URN.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SYRMORITE_HOPPER.get(), (tile, context) -> new VanillaHopperItemHandler(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MORTAR.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MOTH_HOUSE.get(), (tile, context) -> new MothHouseWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.PURIFIER.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SILT_GLASS_JAR.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.STEEPING_POT.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.WATER_FILTER.get(), ItemHandlerProvidingBlockEntity::getItemHandlerCapability);

		event.registerItem(Capabilities.FluidHandler.ITEM, (object, context) -> new FluidHandlerItemStack(DataComponentRegistry.STORED_FLUID, object, FluidType.BUCKET_VOLUME), ItemRegistry.WEEDWOOD_BUCKET, ItemRegistry.SYRMORITE_BUCKET);
	}
}
