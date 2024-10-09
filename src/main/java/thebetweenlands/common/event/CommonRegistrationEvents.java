package thebetweenlands.common.event;

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
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.VanillaHopperItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.CenserWrapper;
import thebetweenlands.common.capability.MothHouseWrapper;
import thebetweenlands.common.command.AspectCommand;
import thebetweenlands.common.command.EventCommand;
import thebetweenlands.common.command.GenerateAnadiaCommand;
import thebetweenlands.common.command.ResetAspectsCommand;
import thebetweenlands.common.datagen.*;
import thebetweenlands.common.datagen.loot.BLLootProvider;
import thebetweenlands.common.datagen.tags.*;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;
import thebetweenlands.common.entity.creature.Gecko;
import thebetweenlands.common.entity.creature.MireSnail;
import thebetweenlands.common.entity.fishing.BubblerCrab;
import thebetweenlands.common.entity.fishing.SiltCrab;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.monster.*;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.network.clientbound.*;
import thebetweenlands.common.network.clientbound.attachment.*;
import thebetweenlands.common.network.serverbound.ChopFishPacket;
import thebetweenlands.common.network.serverbound.ExtendedReachAttackPacket;
import thebetweenlands.common.network.serverbound.RenameItemPacket;
import thebetweenlands.common.network.serverbound.SetLastPageDataPacket;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;

import java.util.concurrent.CompletableFuture;

public class CommonRegistrationEvents {

	public static void init(IEventBus bus) {
		bus.addListener(CommonRegistrationEvents::createDatagen);
		bus.addListener(CommonRegistrationEvents::makeNewRegistries);
		bus.addListener(CommonRegistrationEvents::extraRegistration);
		bus.addListener(CommonRegistrationEvents::makeDatapackRegistries);
		bus.addListener(CommonRegistrationEvents::populateVanillaTabs);
		bus.addListener(CommonRegistrationEvents::registerAttributes);
		bus.addListener(CommonRegistrationEvents::registerBlockEntityValidBlocks);
		bus.addListener(CommonRegistrationEvents::registerPackets);
		bus.addListener(CommonRegistrationEvents::registerDataMaps);
		bus.addListener(CommonRegistrationEvents::registerCapabilities);

		NeoForge.EVENT_BUS.addListener(CommonRegistrationEvents::registerCommands);

		CommonEvents.init();
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
			EntityRegistry.SPAWN_EGGS.getEntries().forEach(item -> event.accept(new ItemStack(item)));
		}
	}

	private static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityRegistry.BUBBLER_CRAB.get(), BubblerCrab.registerAttributes().build());
		event.put(EntityRegistry.SILT_CRAB.get(), SiltCrab.registerAttributes().build());
		event.put(EntityRegistry.ANADIA.get(), Anadia.registerAttributes().build());
		event.put(EntityRegistry.MIRE_SNAIL.get(), MireSnail.registerAttributes().build());
		event.put(EntityRegistry.WIGHT.get(), Wight.registerAttributes().build());
		event.put(EntityRegistry.SWAMP_HAG.get(), SwampHag.registerAttributes().build());
		event.put(EntityRegistry.GECKO.get(), Gecko.registerAttributes().build());
		event.put(EntityRegistry.SLUDGE_WORM.get(), SludgeWorm.registerAttributes().build());
		event.put(EntityRegistry.STALKER.get(), Stalker.registerAttributes().build());
		event.put(EntityRegistry.PEAT_MUMMY.get(), PeatMummy.registerAttributes().build());
		event.put(EntityRegistry.DREADFUL_PEAT_MUMMY.get(), DreadfulPeatMummy.registerAttributes().build());
		event.put(EntityRegistry.MUMMY_ARM.get(), MummyArm.registerAttributes().build());
		event.put(EntityRegistry.ASH_SPRITE.get(), AshSprite.registerAttributes().build());
		event.put(EntityRegistry.BARRISHEE.get(), Barrishee.registerAttributes().build());
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
		event.dataPackRegistry(BLRegistries.Keys.ASPECT_ITEMS, AspectItem.DIRECT_CODEC, AspectItem.DIRECT_CODEC);
		event.dataPackRegistry(BLRegistries.Keys.ASPECT_TYPES, AspectType.DIRECT_CODEC, AspectType.DIRECT_CODEC);
		event.dataPackRegistry(BLRegistries.Keys.ELIXIR_RECIPES, ElixirRecipe.CODEC, ElixirRecipe.CODEC);
	}

	private static void makeNewRegistries(NewRegistryEvent event) {
		event.register(BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES);
		event.register(BLRegistries.ASPECT_CALCULATOR_TYPE);
		event.register(BLRegistries.CENSER_RECIPES);
		event.register(BLRegistries.ELIXIR_EFFECTS);
		event.register(BLRegistries.ENVIRONMENT_EVENTS);
		event.register(BLRegistries.SIMULACRUM_EFFECTS);
		event.register(BLRegistries.WORLD_STORAGE);
		event.register(BLRegistries.SYNCHED_ATTACHMENT_TYPES);
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
		registrar.playToClient(InfestWeedwoodBushPacket.TYPE, InfestWeedwoodBushPacket.STREAM_CODEC, InfestWeedwoodBushPacket::handle);
		registrar.playToClient(OpenLoreScrapPacket.TYPE, OpenLoreScrapPacket.STREAM_CODEC, OpenLoreScrapPacket::handle);
		registrar.playToClient(ShowFoodSicknessPacket.TYPE, ShowFoodSicknessPacket.STREAM_CODEC, ShowFoodSicknessPacket::handle);
		registrar.playToClient(SyncLocalStorageDataPacket.TYPE, SyncLocalStorageDataPacket.STREAM_CODEC, SyncLocalStorageDataPacket::handle);
		registrar.playToClient(SyncChunkStoragePacket.TYPE, SyncChunkStoragePacket.STREAM_CODEC, SyncChunkStoragePacket::handle);
		registrar.playToClient(SyncLocalStorageReferencesPacket.TYPE, SyncLocalStorageReferencesPacket.STREAM_CODEC, SyncLocalStorageReferencesPacket::handle);
		registrar.playToClient(ShockParticlePacket.TYPE, ShockParticlePacket.STREAM_CODEC, ShockParticlePacket::handle);
		registrar.playToClient(SoundRipplePacket.TYPE, SoundRipplePacket.STREAM_CODEC, SoundRipplePacket::handle);
		registrar.playToClient(UpdateDecayDataPacket.TYPE, UpdateDecayDataPacket.STREAM_CODEC, UpdateDecayDataPacket::handle);
		registrar.playToClient(UpdateMudWalkerPacket.TYPE, UpdateMudWalkerPacket.STREAM_CODEC, UpdateMudWalkerPacket::handle);
//		registrar.playToClient(UpdateRotSmellPacket.TYPE, UpdateRotSmellPacket.STREAM_CODEC, UpdateRotSmellPacket::handle);
		registrar.playToClient(UpdateFoodSicknessPacket.TYPE, UpdateFoodSicknessPacket.STREAM_CODEC, UpdateFoodSicknessPacket::handle);
		registrar.playToClient(UpdateGemsPacket.TYPE, UpdateGemsPacket.STREAM_CODEC, UpdateGemsPacket::handle);
		registrar.playToClient(GemProtectionPacket.TYPE, GemProtectionPacket.STREAM_CODEC, GemProtectionPacket::handle);
		registrar.playToClient(UpdateDruidAltarProgressPacket.TYPE, UpdateDruidAltarProgressPacket.STREAM_CODEC, UpdateDruidAltarProgressPacket::handle);
		registrar.playToClient(UpdateFallReductionPacket.TYPE, UpdateFallReductionPacket.STREAM_CODEC, UpdateFallReductionPacket::handle);
		registrar.playToClient(UpdateInfestationPacket.TYPE, UpdateInfestationPacket.STREAM_CODEC, UpdateInfestationPacket::handle);
		registrar.playToClient(OpenHerbloreBookPacket.TYPE, OpenHerbloreBookPacket.STREAM_CODEC, OpenHerbloreBookPacket::handle);
		registrar.playToClient(OpenRenameScreenPacket.TYPE, OpenRenameScreenPacket.STREAM_CODEC, (packet, context) -> OpenRenameScreenPacket.handle(context));
		registrar.playToClient(LivingWeedwoodShieldSpitPacket.TYPE, LivingWeedwoodShieldSpitPacket.STREAM_CODEC, LivingWeedwoodShieldSpitPacket::handle);
		registrar.playToClient(SyncStaticAspectsPacket.TYPE, SyncStaticAspectsPacket.STREAM_CODEC, SyncStaticAspectsPacket::handle);
		registrar.playToClient(UpdateSwarmedPacket.TYPE, UpdateSwarmedPacket.STREAM_CODEC, UpdateSwarmedPacket::handle);
		registrar.playToClient(SummonPeatMummyParticlesPacket.TYPE, SummonPeatMummyParticlesPacket.STREAM_CODEC, SummonPeatMummyParticlesPacket::handle);
		registrar.playToClient(AddBetweenlandsBossBarPacket.TYPE, AddBetweenlandsBossBarPacket.STREAM_CODEC, AddBetweenlandsBossBarPacket::handle);

		registrar.playToClient(UpdateSynchedAttachmentPacket.TYPE, UpdateSynchedAttachmentPacket.STREAM_CODEC, UpdateSynchedAttachmentPacket::handle);

		registrar.playToServer(ChopFishPacket.TYPE, ChopFishPacket.STREAM_CODEC, (payload, context) -> ChopFishPacket.handle(context));
		registrar.playToServer(ExtendedReachAttackPacket.TYPE, ExtendedReachAttackPacket.STREAM_CODEC, ExtendedReachAttackPacket::handle);
		registrar.playToServer(RenameItemPacket.TYPE, RenameItemPacket.STREAM_CODEC, RenameItemPacket::handle);
		registrar.playToServer(SetLastPageDataPacket.TYPE, SetLastPageDataPacket.STREAM_CODEC, SetLastPageDataPacket::handle);
	}

	private static void registerDataMaps(RegisterDataMapTypesEvent event) {
		event.register(DataMapRegistry.COMPOSTABLE);
		event.register(DataMapRegistry.DECAY_FOOD);
		event.register(DataMapRegistry.FLUX_MULTIPLIER);
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
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SULFUR_FURNACE.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CENSER.get(), (tile, context) -> new CenserWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CRAB_POT.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.COMPOST_BIN.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.CRAB_POT_FILTER.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.DRUID_ALTAR.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.FISHING_TACKLE_BOX.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.GRUB_HUB.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SYRMORITE_HOPPER.get(), (tile, context) -> new VanillaHopperItemHandler(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.ITEM_SHELF.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MORTAR.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.MOTH_HOUSE.get(), (tile, context) -> new MothHouseWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.PURIFIER.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.SILT_GLASS_JAR.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.STEEPING_POT.get(), (tile, context) -> new InvWrapper(tile));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.WATER_FILTER.get(), (tile, context) -> new InvWrapper(tile));

		event.registerItem(Capabilities.FluidHandler.ITEM, (object, context) -> new FluidHandlerItemStack(DataComponentRegistry.STORED_FLUID, object, FluidType.BUCKET_VOLUME), ItemRegistry.WEEDWOOD_BUCKET, ItemRegistry.SYRMORITE_BUCKET);
	}
}
