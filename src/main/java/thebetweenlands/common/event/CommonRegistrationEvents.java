package thebetweenlands.common.event;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.VanillaHopperItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.CenserWrapper;
import thebetweenlands.common.capability.MothHouseWrapper;
import thebetweenlands.common.command.AspectCommand;
import thebetweenlands.common.command.GenerateAnadiaCommand;
import thebetweenlands.common.command.ResetAspectsCommand;
import thebetweenlands.common.datagen.*;
import thebetweenlands.common.datagen.loot.BLLootProvider;
import thebetweenlands.common.datagen.tags.*;
import thebetweenlands.common.entities.MireSnail;
import thebetweenlands.common.entities.fishing.BubblerCrab;
import thebetweenlands.common.entities.fishing.SiltCrab;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.handler.HandlerEvents;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.network.*;
import thebetweenlands.common.network.clientbound.*;
import thebetweenlands.common.network.clientbound.attachment.*;
import thebetweenlands.common.network.serverbound.ChopFishPacket;
import thebetweenlands.common.network.serverbound.ExtendedReachAttackPacket;
import thebetweenlands.common.registries.*;

import java.util.concurrent.CompletableFuture;

public class CommonRegistrationEvents {

	public static void init(IEventBus bus, Dist dist) {
		bus.addListener(CommonRegistrationEvents::createDatagen);
		bus.addListener(CommonRegistrationEvents::makeNewRegistries);
		bus.addListener(CommonRegistrationEvents::makeDatapackRegistries);
		bus.addListener(CommonRegistrationEvents::populateVanillaTabs);
		bus.addListener(CommonRegistrationEvents::registerAttributes);
		bus.addListener(CommonRegistrationEvents::registerBlockEntityValidBlocks);
		bus.addListener(CommonRegistrationEvents::registerPackets);
		bus.addListener(CommonRegistrationEvents::registerDataMaps);
		bus.addListener(CommonRegistrationEvents::registerCapabilities);

		NeoForge.EVENT_BUS.addListener(CommonRegistrationEvents::registerCommands);
		NeoForge.EVENT_BUS.addListener(CommonRegistrationEvents::protectFromMagicDamage);

		SimulacrumEvents.init();
		HandlerEvents.init(dist);
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
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			EntityRegistry.SPAWN_EGGS.getEntries().forEach(item -> event.accept(new ItemStack(item)));
		}
	}

	private static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityRegistry.BUBBLER_CRAB.get(), BubblerCrab.registerAttributes().build());
		event.put(EntityRegistry.SILT_CRAB.get(), SiltCrab.registerAttributes().build());
		event.put(EntityRegistry.ANADIA.get(), Anadia.registerAttributes().build());
		event.put(EntityRegistry.MIRE_SNAIL.get(), MireSnail.registerAttributes().build());
	}

	private static void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("betweenlands")
			.then(Commands.literal("debug")
				.then(GenerateAnadiaCommand.register()))
			.then(AspectCommand.register())
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
		event.register(BLRegistries.ASPECT_CALCULATOR_TYPE);
		event.register(BLRegistries.CENSER_RECIPES);
		event.register(BLRegistries.ELIXIR_EFFECTS);
		event.register(BLRegistries.ENVIRONMENT_EVENTS);
		event.register(BLRegistries.SIMULACRUM_EFFECTS);
		event.register(BLRegistries.WORLD_STORAGE);
		event.register(BLRegistries.SYNCHED_ATTACHMENT_TYPES);
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
		
		registrar.playToClient(UpdateSynchedAttachmentPacket.TYPE, UpdateSynchedAttachmentPacket.STREAM_CODEC, UpdateSynchedAttachmentPacket::handle);

		registrar.playToServer(ChopFishPacket.TYPE, ChopFishPacket.STREAM_CODEC, (payload, context) -> ChopFishPacket.handle(context));
		registrar.playToServer(ExtendedReachAttackPacket.TYPE, ExtendedReachAttackPacket.STREAM_CODEC, ExtendedReachAttackPacket::handle);
	}

	private static void registerDataMaps(RegisterDataMapTypesEvent event) {
		event.register(DataMapRegistry.DECAY_FOOD);
		event.register(DataMapRegistry.FLUX_MULTIPLIER);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.BARREL.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.CENSER.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.GRUB_HUB.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.FILTERED_SILT_GLASS_JAR.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.INFUSER.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.PURIFIER.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.RUBBER_TAP.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.STEEPING_POT.get(), (tile, context) -> tile.tank);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityRegistry.WATER_FILTER.get(), (tile, context) -> tile.tank);

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
	}

	private static void protectFromMagicDamage(LivingIncomingDamageEvent event) {
		if(event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			float damageMultiplier = 1.0F;

			LivingEntity entityHit = event.getEntity();

			ItemStack boots = entityHit.getItemBySlot(EquipmentSlot.FEET);
			ItemStack legs = entityHit.getItemBySlot(EquipmentSlot.LEGS);
			ItemStack chest = entityHit.getItemBySlot(EquipmentSlot.CHEST);
			ItemStack helm = entityHit.getItemBySlot(EquipmentSlot.HEAD);

			if (!boots.isEmpty() && boots.is(ItemRegistry.ANCIENT_BOOTS) && boots.getDamageValue() < boots.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!legs.isEmpty()  && legs.is(ItemRegistry.ANCIENT_LEGGINGS) && legs.getDamageValue() < legs.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!chest.isEmpty() && chest.is(ItemRegistry.ANCIENT_CHESTPLATE) && chest.getDamageValue() < chest.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!helm.isEmpty() && helm.is(ItemRegistry.ANCIENT_HELMET) && helm.getDamageValue() < helm.getMaxDamage())
				damageMultiplier -= 0.125F;

			event.setAmount(event.getAmount() * damageMultiplier);
		}
	}
}
