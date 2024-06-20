package thebetweenlands.common.registries;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.*;

//TODO: as Material has been removed, evaluate each block's properties for accuracy
//TODO: look into new sounds added by vanilla. I mean imo some of them are just overreaching but players are particular...for some reason
//TODO: noLootTable means blocks will never drop. Go through any that use this method for actual drops
public class BlockRegistry {
	
	// Block list
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TheBetweenlands.ID);

	// Fluid Block inits (fluid blocks must be registered befor blocks)
	public static final DeferredBlock<LiquidBlock> SWAMP_WATER_BLOCK = BLOCKS.register("swamp_water", () -> new BetweenlandsSwampWater(() -> FluidRegistry.SWAMP_WATER_FLOW.get(), BlockBehaviour.Properties.of().noLootTable().strength(100f)));

	// Block inits
	// - Landscape (i should fix up grass blocks i think...)
	public static final DeferredBlock<Block> MUD = BLOCKS.register("mud", () -> new BetweenlandsSlowingBlock(0.9f, true, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> SILT = BLOCKS.register("silt", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.SAND)));
	public static final DeferredBlock<Block> PEAT = BLOCKS.register("peat", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> SWAMP_DIRT = BLOCKS.register("swamp_dirt", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final DeferredBlock<Block> DEAD_SWAMP_GRASS = BLOCKS.register("dead_grass", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SWAMP_GRASS = BLOCKS.register("swamp_grass", () -> new BetweenlandsGrassBlock(BlockBehaviour.Properties.of().randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BETWEENSTONE = BLOCKS.register("betweenstone", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> PITSTONE = BLOCKS.register("pitstone", () -> new Block(BlockBehaviour.Properties.of().strength(3F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> LIMESTONE = BLOCKS.register("limestone", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.CALCITE)));
	public static final DeferredBlock<Block> BETWEENLANDS_BEDROCK = BLOCKS.register("betweenlands_bedrock", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable().sound(SoundType.STONE)));
	public static final DeferredBlock<Block> CRAGROCK = BLOCKS.register("cragrock", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).sound(SoundType.STONE)));
	// - Leaves
	public static final DeferredBlock<Block> LEAVES_WEEDWOOD_TREE = BLOCKS.register("weedwood_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_SAP_TREE = BLOCKS.register("sap_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_RUBBER_TREE = BLOCKS.register("rubber_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_HEARTHGROVE_TREE = BLOCKS.register("leaves_hearthgrove", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> LEAVES_NIBBLETWIG_TREE = BLOCKS.register("nibbletwig_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.GRASS)));
	// - Woods
	public static final DeferredBlock<Block> WEEDWOOD_BARK_LOG = BLOCKS.register("weedwood_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> WEEDWOOD_LOG = BLOCKS.register("weedwood_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> WEEDWOOD = BLOCKS.register("weedwood", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> LOG_NIBBLETWIG = BLOCKS.register("nibbletwig_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> SAP_BARK_LOG = BLOCKS.register("sap_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> SAP_LOG = BLOCKS.register("sap_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> RUBBER_LOG = BLOCKS.register("rubber_log", () -> new BetweenlandsThinLogBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).noOcclusion().sound(SoundType.WOOD), LEAVES_RUBBER_TREE.get()));
	public static final DeferredBlock<Block> LOG_HEARTHGROVE = BLOCKS.register("log_hearthgrove", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	public static final DeferredBlock<Block> GIANT_ROOT = BLOCKS.register("giant_root", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.WOOD)));
	// - Plants
	public static final DeferredBlock<Block> WEEDWOOD_SAPLING = BLOCKS.register("weedwood_sapling", () -> new WeedwoodSapling(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SAP_SAPLING = BLOCKS.register("sap_sapling", () -> new SapSapling(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> RUBBER_SAPLING = BLOCKS.register("rubber_sapling", () -> new RubberSapling(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> NIBBLETWIG_SAPLING = BLOCKS.register("nibbletwig_sapling", () -> new NibbletwigSapling(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BULB_CAPED_MUSHROOM = BLOCKS.register("bulb_caped_mushroom_sapling", () -> new SapSapling(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> BULB_CAPPED_MUSHROOM_CAP = BLOCKS.register("bulb_capped_mushroom_cap", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).lightLevel((lightlevel) -> 13).noLootTable().noOcclusion().isViewBlocking((e, f, g) -> {return false;}).sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> BULB_CAPPED_MUSHROOM_STALK = BLOCKS.register("bulb_capped_mushroom_stalk", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).lightLevel((lightlevel) -> 13).noLootTable().sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> SHELF_FUNGUS = BLOCKS.register("shelf_fungus", () -> new Block(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noOcclusion().sound(SoundType.WOOL)));
	public static final DeferredBlock<Block> MOSS = BLOCKS.register("moss", () -> new BetweenlandsMoss(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> THORNS = BLOCKS.register("thorns", () -> new BetweenlandsThorns(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> POISON_IVY = BLOCKS.register("poison_ivy", () -> new BlockPoisonIvy(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.VINE)));
	public static final DeferredBlock<Block> CAVE_MOSS = BLOCKS.register("cave_moss", () -> new BetweenlandsCaveMoss(BetweenlandsBlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS),20));
	public static final DeferredBlock<Block> SWAMP_REED = BLOCKS.register("swamp_reed", () -> new BetweenlandsSeaPlant(BetweenlandsBlockBehaviour.Properties.of().strength(0.5F, 2.5F).randomTicks().noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> HANGER = BLOCKS.register("hanger", () -> new Hanger(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final DeferredBlock<Block> SWAMP_TALLGRASS = BLOCKS.register("swamp_tallgrass", () -> new TallGrassBlock(BlockBehaviour.Properties.of().strength(0.5F, 2.5F).noLootTable().noCollission().noOcclusion().sound(SoundType.GRASS)));
	// - Ores
	public static final DeferredBlock<Block> SULFUR_ORE = BLOCKS.register("sulfur_ore", () -> new BetweenlandsParticleEmiterBlock(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> SYRMORITE_ORE = BLOCKS.register("syrmorite_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> SLIMY_BONE_ORE = BLOCKS.register("slimy_bone_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final DeferredBlock<Block> OCTINE_ORE = BLOCKS.register("octine_ore", () -> new OctineOreBlock(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.STONE).lightLevel((lightlevel) -> 13)));
	public static final DeferredBlock<Block> SCABYST_ORE = BLOCKS.register("scabyst_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredBlock<Block> VALONITE_ORE = BLOCKS.register("valonite_ore", () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	// - Portal
	public static final DeferredBlock<Block> PORTAL = BLOCKS.register("portal", () -> new BetweenlandsPortal(BlockBehaviour.Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel((lightlevel) -> 11)));

	// debug
	//public static final RegistryObject<Block> DEBUG = BLOCKS.register("debug", () -> new BetweenlandsDebugBlock(BlockBehaviour.Properties.of(Material.GRASS).randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
}