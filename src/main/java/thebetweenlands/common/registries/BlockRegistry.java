package thebetweenlands.common.registries;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.*;

public class BlockRegistry {
	
	// Block list
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheBetweenlands.ID);

	static { init(); }

	// Fluid Block inits (fluid blocks must be registered befor blocks)
	public static final RegistryObject<LiquidBlock> SWAMP_WATER_BLOCK = BLOCKS.register("swamp_water", () -> new BetweenlandsSwampWater(() -> FluidRegistry.SWAMP_WATER_FLOW.get(), BlockBehaviour.Properties.of(Material.WATER).noDrops().strength(100f)));

	// Block inits
	// - Landscape (i should fix up grass blocks i think...)
	public static final RegistryObject<Block> MUD = BLOCKS.register("mud", () -> new BetweenlandsSlowingBlock(0.9f, true, BlockBehaviour.Properties.of(Material.DIRT).strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final RegistryObject<Block> SILT = BLOCKS.register("silt", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of(Material.SAND).strength(0.5F, 2.5F).sound(SoundType.SAND)));
	public static final RegistryObject<Block> PEAT = BLOCKS.register("peat", () -> new BetweenlandsSlowingBlock(0.6f, false, BlockBehaviour.Properties.of(Material.SAND).strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final RegistryObject<Block> SWAMP_DIRT = BLOCKS.register("swamp_dirt", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of(Material.DIRT).strength(0.5F, 2.5F).sound(SoundType.GRAVEL)));
	public static final RegistryObject<Block> DEAD_SWAMP_GRASS = BLOCKS.register("dead_grass", () -> new BetweenlandsPlantable(BlockBehaviour.Properties.of(Material.DIRT).strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final RegistryObject<Block> SWAMP_GRASS = BLOCKS.register("swamp_grass", () -> new BetweenlandsGrassBlock(BlockBehaviour.Properties.of(Material.GRASS).randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	public static final RegistryObject<Block> BETWEENSTONE = BLOCKS.register("betweenstone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 10.0F).sound(SoundType.STONE)));
	public static final RegistryObject<Block> PITSTONE = BLOCKS.register("pitstone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(3F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final RegistryObject<Block> LIMESTONE = BLOCKS.register("limestone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 10.0F).sound(SoundType.CALCITE)));
	public static final RegistryObject<Block> BETWEENLANDS_BEDROCK = BLOCKS.register("betweenlands_bedrock", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of(Material.DIRT).strength(-1.0F, 3600000.0F).noDrops().sound(SoundType.STONE)));
	public static final RegistryObject<Block> CRAGROCK = BLOCKS.register("cragrock", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 10.0F).sound(SoundType.STONE)));
	// - Leaves
	public static final RegistryObject<Block> LEAVES_WEEDWOOD_TREE = BLOCKS.register("weedwood_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> LEAVES_SAP_TREE = BLOCKS.register("sap_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> LEAVES_RUBBER_TREE = BLOCKS.register("rubber_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> LEAVES_HEARTHGROVE_TREE = BLOCKS.register("leaves_hearthgrove", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> LEAVES_NIBBLETWIG_TREE = BLOCKS.register("nibbletwig_leaves", () -> new BetweenlandsLeaves(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.GRASS)));
	// - Woods
	public static final RegistryObject<Block> WEEDWOOD_BARK_LOG = BLOCKS.register("weedwood_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> WEEDWOOD_LOG = BLOCKS.register("weedwood_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> WEEDWOOD = BLOCKS.register("weedwood", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> LOG_NIBBLETWIG = BLOCKS.register("nibbletwig_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> SAP_BARK_LOG = BLOCKS.register("sap_bark_log", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> SAP_LOG = BLOCKS.register("sap_log", () -> new BetweenlandsLogBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> RUBBER_LOG = BLOCKS.register("rubber_log", () -> new BetweenlandsThinLogBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).noOcclusion().sound(SoundType.WOOD), LEAVES_RUBBER_TREE.get()));
	public static final RegistryObject<Block> LOG_HEARTHGROVE = BLOCKS.register("log_hearthgrove", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> GIANT_ROOT = BLOCKS.register("giant_root", () -> new BetweenlandsWoodBlock(BetweenlandsBlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	// - Plants
	public static final RegistryObject<Block> WEEDWOOD_SAPLING = BLOCKS.register("weedwood_sapling", () -> new WeedwoodSapling(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> SAP_SAPLING = BLOCKS.register("sap_sapling", () -> new SapSapling(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> RUBBER_SAPLING = BLOCKS.register("rubber_sapling", () -> new RubberSapling(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> NIBBLETWIG_SAPLING = BLOCKS.register("nibbletwig_sapling", () -> new NibbletwigSapling(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> BULB_CAPED_MUSHROOM = BLOCKS.register("bulb_caped_mushroom_sapling", () -> new SapSapling(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> BULB_CAPPED_MUSHROOM_CAP = BLOCKS.register("bulb_capped_mushroom_cap", () -> new HalfTransparentBlock(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0.5F, 2.5F).lightLevel((lightlevel) -> {return 13;}).noDrops().noOcclusion().isViewBlocking((e, f, g) -> {return false;}).sound(SoundType.WOOL)));
	public static final RegistryObject<Block> BULB_CAPPED_MUSHROOM_STALK = BLOCKS.register("bulb_capped_mushroom_stalk", () -> new BetweenlandsBlock(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0.5F, 2.5F).lightLevel((lightlevel) -> {return 13;}).noDrops().sound(SoundType.WOOL)));
	public static final RegistryObject<Block> SHELF_FUNGUS = BLOCKS.register("shelf_fungus", () -> new Block(BlockBehaviour.Properties.of(Material.VEGETABLE).strength(0.5F, 2.5F).noDrops().noOcclusion().sound(SoundType.WOOL)));
	public static final RegistryObject<Block> MOSS = BLOCKS.register("moss", () -> new BetweenlandsMoss(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> THORNS = BLOCKS.register("thorns", () -> new BetweenlandsThorns(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).randomTicks().noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> POISON_IVY = BLOCKS.register("poison_ivy", () -> new BlockPoisonIvy(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).randomTicks().noDrops().noCollission().noOcclusion().sound(SoundType.VINE)));
	public static final RegistryObject<Block> CAVE_MOSS = BLOCKS.register("cave_moss", () -> new BetweenlandsCaveMoss(BetweenlandsBlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).randomTicks().noDrops().noCollission().noOcclusion().sound(SoundType.GRASS),20));
	public static final RegistryObject<Block> SWAMP_REED = BLOCKS.register("swamp_reed", () -> new BetweenlandsSeaPlant(BetweenlandsBlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).randomTicks().noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> HANGER = BLOCKS.register("hanger", () -> new Hanger(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	public static final RegistryObject<Block> SWAMP_TALLGRASS = BLOCKS.register("swamp_tallgrass", () -> new TallGrassBlock(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5F, 2.5F).noDrops().noCollission().noOcclusion().sound(SoundType.GRASS)));
	// - Ores
	public static final RegistryObject<Block> SULFUR_ORE = BLOCKS.register("sulfur_ore", () -> new BetweenlandsParticleEmiterBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final RegistryObject<Block> SYRMORITE_ORE = BLOCKS.register("syrmorite_ore", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final RegistryObject<Block> SLIMY_BONE_ORE = BLOCKS.register("slimy_bone_ore", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.STONE)));
	public static final RegistryObject<Block> OCTINE_ORE = BLOCKS.register("octine_ore", () -> new OctineOreBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.STONE).lightLevel((lightlevel) -> {return 13;})));
	public static final RegistryObject<Block> SCABYST_ORE = BLOCKS.register("scabyst_ore", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	public static final RegistryObject<Block> VALONITE_ORE = BLOCKS.register("valonite_ore", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 30.0F).sound(SoundType.DEEPSLATE)));
	// - Portal
	public static final RegistryObject<Block> PORTAL = BLOCKS.register("portal", () -> new BetweenlandsPortal(BlockBehaviour.Properties.of(Material.STONE).noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel((lightlevel) -> {return 11;})));
	private static void init() {
	}
	// debug
	//public static final RegistryObject<Block> DEBUG = BLOCKS.register("debug", () -> new BetweenlandsDebugBlock(BlockBehaviour.Properties.of(Material.GRASS).randomTicks().strength(0.5F, 2.5F).sound(SoundType.GRASS)));
	
	
	// Register block list
	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
		//Blocks.OAK_SAPLING
	}
}