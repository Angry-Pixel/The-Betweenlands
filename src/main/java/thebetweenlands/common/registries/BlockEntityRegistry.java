package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.*;

public class BlockEntityRegistry {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WispBlockEntity>> WISP = BLOCK_ENTITIES.register("wisp", () -> BlockEntityType.Builder.of(WispBlockEntity::new, BlockRegistry.WISP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootPotBlockEntity>> LOOT_POT = BLOCK_ENTITIES.register("loot_pot", () -> BlockEntityType.Builder.of(LootPotBlockEntity::new, BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LootUrnBlockEntity>> LOOT_URN = BLOCK_ENTITIES.register("loot_urn", () -> BlockEntityType.Builder.of(LootUrnBlockEntity::new, BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpikeTrapBlockEntity>> SPIKE_TRAP = BLOCK_ENTITIES.register("spike_trap", () -> BlockEntityType.Builder.of(SpikeTrapBlockEntity::new, BlockRegistry.SPIKE_TRAP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PossessedBlockEntity>> POSSESSED_BLOCK = BLOCK_ENTITIES.register("possessed_block", () -> BlockEntityType.Builder.of(PossessedBlockEntity::new, BlockRegistry.POSSESSED_BLOCK.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemCageBlockEntity>> ITEM_CAGE = BLOCK_ENTITIES.register("item_cage", () -> BlockEntityType.Builder.of(ItemCageBlockEntity::new, BlockRegistry.ITEM_CAGE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemShelfBlockEntity>> ITEM_SHELF = BLOCK_ENTITIES.register("item_shelf", () -> BlockEntityType.Builder.of(ItemShelfBlockEntity::new, BlockRegistry.ITEM_SHELF.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PuffshroomBlockEntity>> PUFFSHROOM = BLOCK_ENTITIES.register("puffshroom", () -> BlockEntityType.Builder.of(PuffshroomBlockEntity::new, BlockRegistry.PUFFSHROOM.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeamOriginBlockEntity>> BEAM_ORIGIN = BLOCK_ENTITIES.register("beam_origin", () -> BlockEntityType.Builder.of(BeamOriginBlockEntity::new, BlockRegistry.BEAM_ORIGIN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeamRelayBlockEntity>> BEAM_RELAY = BLOCK_ENTITIES.register("beam_relay", () -> BlockEntityType.Builder.of(BeamRelayBlockEntity::new, BlockRegistry.BEAM_RELAY.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MudBrickAlcoveBlockEntity>> MUD_BRICK_ALCOVE = BLOCK_ENTITIES.register("mud_brick_alcove", () -> BlockEntityType.Builder.of(MudBrickAlcoveBlockEntity::new, BlockRegistry.MUD_BRICK_ALCOVE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonDoorRunesBlockEntity>> DUNGEON_DOOR_RUNES = BLOCK_ENTITIES.register("dungeon_door_runes", () -> BlockEntityType.Builder.of(DungeonDoorRunesBlockEntity::new, BlockRegistry.DUNGEON_DOOR_RUNES.get(), BlockRegistry.MIMIC_DUNGEON_DOOR_RUNES.get(), BlockRegistry.CRAWLER_DUNGEON_DOOR_RUNES.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonDoorCombinationBlockEntity>> DUNGEON_DOOR_COMBINATION = BLOCK_ENTITIES.register("dungeon_door_combination", () -> BlockEntityType.Builder.of(DungeonDoorCombinationBlockEntity::new, BlockRegistry.DUNGEON_DOOR_COMBINATION.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayPitGroundChainBlockEntity>> DECAY_PIT_GROUND_CHAIN = BLOCK_ENTITIES.register("decay_pit_ground_chain", () -> BlockEntityType.Builder.of(DecayPitGroundChainBlockEntity::new, BlockRegistry.DECAY_PIT_GROUND_CHAIN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayPitHangingChainBlockEntity>> DECAY_PIT_HANGING_CHAIN = BLOCK_ENTITIES.register("decay_pit_hanging_chain", () -> BlockEntityType.Builder.of(DecayPitHangingChainBlockEntity::new, BlockRegistry.DECAY_PIT_HANGING_CHAIN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecayPitControlBlockEntity>> DECAY_PIT_CONTROL = BLOCK_ENTITIES.register("decay_pit_control", () -> BlockEntityType.Builder.of(DecayPitControlBlockEntity::new, BlockRegistry.DECAY_PIT_CONTROL.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PresentBlockEntity>> PRESENT = BLOCK_ENTITIES.register("present", () -> BlockEntityType.Builder.of(PresentBlockEntity::new,
		BlockRegistry.WHITE_PRESENT.get(), BlockRegistry.LIGHT_BLUE_PRESENT.get(), BlockRegistry.GRAY_PRESENT.get(), BlockRegistry.BLACK_PRESENT.get(),
		BlockRegistry.RED_PRESENT.get(), BlockRegistry.ORANGE_PRESENT.get(), BlockRegistry.YELLOW_PRESENT.get(), BlockRegistry.GREEN_PRESENT.get(),
		BlockRegistry.LIME_PRESENT.get(), BlockRegistry.BLUE_PRESENT.get(), BlockRegistry.CYAN_PRESENT.get(), BlockRegistry.LIGHT_BLUE_PRESENT.get(),
		BlockRegistry.PURPLE_PRESENT.get(), BlockRegistry.MAGENTA_PRESENT.get(), BlockRegistry.PINK_PRESENT.get(), BlockRegistry.BROWN_PRESENT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DugSoilBlockEntity>> DUG_SOIL = BLOCK_ENTITIES.register("dug_soil", () -> BlockEntityType.Builder.of(DugSoilBlockEntity::new, BlockRegistry.DUG_SWAMP_DIRT.get(), BlockRegistry.DUG_SWAMP_GRASS.get(), BlockRegistry.PURIFIED_DUG_SWAMP_DIRT.get(), BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DruidAltarBlockEntity>> DRUID_ALTAR = BLOCK_ENTITIES.register("druid_altar", () -> BlockEntityType.Builder.of(DruidAltarBlockEntity::new, BlockRegistry.DRUID_ALTAR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PurifierBlockEntity>> PURIFIER = BLOCK_ENTITIES.register("purifier", () -> BlockEntityType.Builder.of(PurifierBlockEntity::new, BlockRegistry.PURIFIER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeedwoodCraftingTableBlockEntity>> WEEDWOOD_CRAFTING_TABLE = BLOCK_ENTITIES.register("weedwood_crafting_table", () -> BlockEntityType.Builder.of(WeedwoodCraftingTableBlockEntity::new, BlockRegistry.WEEDWOOD_CRAFTING_TABLE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompostBinBlockEntity>> COMPOST_BIN = BLOCK_ENTITIES.register("compost_bin", () -> BlockEntityType.Builder.of(CompostBinBlockEntity::new, BlockRegistry.COMPOST_BIN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SulfurFurnaceBlockEntity>> SULFUR_FURNACE = BLOCK_ENTITIES.register("sulfur_furnace", () -> BlockEntityType.Builder.of(SulfurFurnaceBlockEntity::new, BlockRegistry.SULFUR_FURNACE.get(), BlockRegistry.DUAL_SULFUR_FURNACE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RubberTapBlockEntity>> RUBBER_TAP = BLOCK_ENTITIES.register("rubber_tap", () -> BlockEntityType.Builder.of(RubberTapBlockEntity::new, BlockRegistry.WEEDWOOD_RUBBER_TAP.get(), BlockRegistry.SYRMORITE_RUBBER_TAP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SyrmoriteHopperBlockEntity>> SYRMORITE_HOPPER = BLOCK_ENTITIES.register("syrmorite_hopper", () -> BlockEntityType.Builder.of(SyrmoriteHopperBlockEntity::new, BlockRegistry.SYRMORITE_HOPPER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MudFlowerPotBlockEntity>> MUD_FLOWER_POT = BLOCK_ENTITIES.register("mud_flower_pot", () -> BlockEntityType.Builder.of(MudFlowerPotBlockEntity::new, BlockRegistry.MUD_FLOWER_POT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeckoCageBlockEntity>> GECKO_CAGE = BLOCK_ENTITIES.register("gecko_cage", () -> BlockEntityType.Builder.of(GeckoCageBlockEntity::new, BlockRegistry.GECKO_CAGE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER = BLOCK_ENTITIES.register("infuser", () -> BlockEntityType.Builder.of(InfuserBlockEntity::new, BlockRegistry.INFUSER.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AspectVialBlockEntity>> ASPECT_VIAL = BLOCK_ENTITIES.register("aspect_vial", () -> BlockEntityType.Builder.of(AspectVialBlockEntity::new, BlockRegistry.ORANGE_ASPECT_VIAL.get(), BlockRegistry.GREEN_ASPECT_VIAL.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MortarBlockEntity>> MORTAR = BLOCK_ENTITIES.register("mortar", () -> BlockEntityType.Builder.of(MortarBlockEntity::new, BlockRegistry.MORTAR.get()).build(null));

}
