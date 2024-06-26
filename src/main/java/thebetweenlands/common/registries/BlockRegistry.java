package thebetweenlands.common.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.*;
import thebetweenlands.common.world.BLTreeGrowers;

import java.util.function.Function;
import java.util.function.Supplier;

//TODO: as Material has been removed, evaluate each block's properties for accuracy
//TODO: look into new sounds added by vanilla. I mean imo some of them are just overreaching but players are particular...for some reason
//TODO: noLootTable means blocks will never drop. Go through any that use this method for actual drops
public class BlockRegistry {

	// Block list
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TheBetweenlands.ID);

	// Fluid Block inits (fluid blocks must be registered befor blocks)
	public static final DeferredBlock<LiquidBlock> SWAMP_WATER_BLOCK = BLOCKS.register("swamp_water", () -> new BetweenlandsSwampWater(FluidRegistry.SWAMP_WATER_STILL.get(), BlockBehaviour.Properties.of().noLootTable().strength(100f)));

	// Block inits
	// - Landscape (i should fix up grass blocks i think...)
	public static final DeferredBlock<Block> MUD = register("mud", () -> new BetweenlandsSlowingBlock(0.9f, true, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> SILT = register("silt", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.SAND)));
	public static final DeferredBlock<Block> PEAT = register("peat", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> SWAMP_DIRT = register("swamp_dirt", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> DEAD_SWAMP_GRASS = register("dead_grass", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SWAMP_GRASS = register("swamp_grass", () -> new BetweenlandsGrassBlock(BlockBehaviour.Properties.of().randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BETWEENSTONE = register("betweenstone", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> PITSTONE = register("pitstone", () -> new Block(BlockBehaviour.Properties.of().strength(3F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> LIMESTONE = register("limestone", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.CALCITE)));
	public static final DeferredBlock<Block> BETWEENLANDS_BEDROCK = register("betweenlands_bedrock", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().sound(SoundType.STONE)));
	public static final DeferredBlock<Block> CRAGROCK = register("cragrock", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.STONE)));
	// - Leaves
	public static final DeferredBlock<Block> LEAVES_WEEDWOOD_TREE = register("weedwood_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_SAP_TREE = register("sap_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_RUBBER_TREE = register("rubber_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_HEARTHGROVE_TREE = register("leaves_hearthgrove", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_NIBBLETWIG_TREE = register("nibbletwig_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	// - Woods
	public static final DeferredBlock<Block> WEEDWOOD_BARK_LOG = register("weedwood_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> WEEDWOOD_LOG = register("weedwood_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> WEEDWOOD = register("weedwood", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> LOG_NIBBLETWIG = register("nibbletwig_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> SAP_BARK_LOG = register("sap_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> SAP_LOG = register("sap_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> RUBBER_LOG = register("rubber_log", () -> new BetweenlandsThinLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).noOcclusion().sound(SoundType.WOOD), LEAVES_RUBBER_TREE.get()));
	public static final DeferredBlock<Block> LOG_HEARTHGROVE = register("log_hearthgrove", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> GIANT_ROOT = register("giant_root", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	// - Plants
	public static final DeferredBlock<Block> WEEDWOOD_SAPLING = register("weedwood_sapling", () -> new SaplingBlock(BLTreeGrowers.WEEDWOOD, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SAP_SAPLING = register("sap_sapling", () -> new SaplingBlock(BLTreeGrowers.SAP, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> RUBBER_SAPLING = register("rubber_sapling", () -> new SaplingBlock(BLTreeGrowers.RUBBER, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> NIBBLETWIG_SAPLING = register("nibbletwig_sapling", () -> new SaplingBlock(BLTreeGrowers.NIBBLETWIG, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BULB_CAPPED_MUSHROOM = register("bulb_capped_mushroom", () -> new MushroomBlock(ConfiguredFeatureRegistry.BIG_BULB_CAPPED_MUSHROOM, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BULB_CAPPED_MUSHROOM_CAP = register("bulb_capped_mushroom_cap", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).lightLevel((lightlevel) -> 13).noLootTable().noOcclusion().isViewBlocking((e, f, g) -> false).sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> BULB_CAPPED_MUSHROOM_STALK = register("bulb_capped_mushroom_stalk", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).lightLevel((lightlevel) -> 13).noLootTable().sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> SHELF_FUNGUS = register("shelf_fungus", () -> new Block(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> MOSS = register("moss", () -> new BetweenlandsMoss(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> THORNS = register("thorns", () -> new BetweenlandsThorns(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> POISON_IVY = register("poison_ivy", () -> new BlockPoisonIvy(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.VINE)));
	public static final DeferredBlock<Block> CAVE_MOSS = register("cave_moss", () -> new BetweenlandsCaveMoss(BetweenlandsBlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS), 20));
	public static final DeferredBlock<Block> SWAMP_REED = register("swamp_reed", () -> new BetweenlandsSeaPlant(BetweenlandsBlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> HANGER = register("hanger", () -> new Hanger(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SWAMP_TALLGRASS = register("swamp_tallgrass", () -> new TallGrassBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	// - Ores
	public static final DeferredBlock<Block> SULFUR_ORE = register("sulfur_ore", () -> new BetweenlandsParticleEmiterBlock(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> SYRMORITE_ORE = register("syrmorite_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> SLIMY_BONE_ORE = register("slimy_bone_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> OCTINE_ORE = register("octine_ore", () -> new OctineOreBlock(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE).lightLevel((lightlevel) -> 13)));
	public static final DeferredBlock<Block> SCABYST_ORE = register("scabyst_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> VALONITE_ORE = register("valonite_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> AQUA_MIDDLE_GEM_ORE = register("aqua_middle_gem_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> CRIMSON_MIDDLE_GEM_ORE = register("crimson_middle_gem_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> GREEN_MIDDLE_GEM_ORE = register("green_middle_gem_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	// - Others
	public static final DeferredBlock<Block> WISP = register("wisp", () -> new WispBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noCollission().isViewBlocking((s, l ,p) -> false).noOcclusion().sound(SoundType.STONE).strength(0.0F).randomTicks().replaceable()));
	// - Structure
	public static final DeferredBlock<Block> INACTIVE_GLOWING_SMOOTH_CRAGROCK = register("inactive_glowing_smooth_cragrock", () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 10.0F)));
	public static final DeferredBlock<Block> GLOWING_SMOOTH_CRAGROCK = register("glowing_smooth_cragrock", () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 10.0F).lightLevel((s) -> 13)));
	public static final DeferredBlock<Block> SMOOTH_CRAGROCK = register("smooth_cragrock", () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 10.0F)));
	public static final DeferredBlock<Block> WEAK_SMOOTH_CRAGROCK = register("weak_smooth_cragrock", () -> new CollapsingBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_CRAGROCK.get())));
	public static final DeferredBlock<Block> SMOOTH_CRAGROCK_SLAB = register("smooth_cragrock_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SMOOTH_CRAGROCK.get())));
	public static final DeferredBlock<Block> MOB_SPAWNER = register("mob_spawner", () -> new BetweenlandsMobSpawnerBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(10.0F, 0.0F).noOcclusion()));
	// - Portal
	public static final DeferredBlock<Block> PORTAL = BLOCKS.register("portal", () -> new BetweenlandsPortal(BlockBehaviour.Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel((lightlevel) -> 11)));

	// debug
	//public static final RegistryObject<Block> DEBUG = BLOCKS.register("debug", () -> new BetweenlandsDebugBlock(BlockBehaviour.Properties.of(Material.GRASS).randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));

	public static <T extends Block> DeferredBlock<T> register(String name, Supplier<? extends T> block) {
		return register(name, block, item -> () -> new BlockItem(item.get(), new Item.Properties()));
	}

	public static <T extends Block> DeferredBlock<T> register(String name, Supplier<? extends T> block, Function<DeferredBlock<T>, Supplier<? extends Item>> item) {
		DeferredBlock<T> reg = BLOCKS.register(name, block);
		ItemRegistry.ITEMS.register(name, item.apply(reg));
		return reg;
	}
}