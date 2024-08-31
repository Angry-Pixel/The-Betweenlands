package thebetweenlands.common.registries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectTier;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.common.herblore.aspect.calculator.DoubleGroupAspectCalculator;
import thebetweenlands.common.herblore.aspect.calculator.RandomAspectCalculator;
import thebetweenlands.common.herblore.aspect.calculator.SetAspectCalculator;

import java.util.List;

public class AspectItemRegistry {

	public static final ResourceKey<AspectItem> ALGAE = registerKey("algae");
	public static final ResourceKey<AspectItem> ANGLER_TOOTH = registerKey("angler_tooth");
	public static final ResourceKey<AspectItem> AQUA_MIDDLE_GEM = registerKey("aqua_middle_gem");
	public static final ResourceKey<AspectItem> ARROW_ARUM = registerKey("arrow_arum");
	public static final ResourceKey<AspectItem> BLACKHAT_MUSHROOM = registerKey("blackhat_mushroom");
	public static final ResourceKey<AspectItem> BLADDERWORT_FLOWER = registerKey("bladderwort_flower");
	public static final ResourceKey<AspectItem> BLADDERWORT_STALK = registerKey("bladderwort_stalk");
	public static final ResourceKey<AspectItem> BLUE_EYED_GRASS = registerKey("blue_eyed_grass");
	public static final ResourceKey<AspectItem> BLUE_IRIS = registerKey("blue_iris");
	public static final ResourceKey<AspectItem> BOG_BEAN = registerKey("bog_bean");
	public static final ResourceKey<AspectItem> BONESET = registerKey("boneset");
	public static final ResourceKey<AspectItem> BOTTLE_BRUSH_GRASS = registerKey("bottle_brush_grass");
	public static final ResourceKey<AspectItem> BROOMSEDGE = registerKey("broomsedge");
	public static final ResourceKey<AspectItem> BULB_CAPPED_MUSHROOM = registerKey("bulb_capped_mushroom");
	public static final ResourceKey<AspectItem> BUTTON_BUSH = registerKey("button_bush");
	public static final ResourceKey<AspectItem> CARDINAL_FLOWER = registerKey("cardinal_flower");
	public static final ResourceKey<AspectItem> CATTAIL = registerKey("cattail");
	public static final ResourceKey<AspectItem> CAVE_GRASS = registerKey("cave_grass");
	public static final ResourceKey<AspectItem> CAVE_MOSS = registerKey("cave_moss");
	public static final ResourceKey<AspectItem> COPPER_IRIS = registerKey("copper_iris");
	public static final ResourceKey<AspectItem> CRIMSON_MIDDLE_GEM = registerKey("crimson_middle_gem");
	public static final ResourceKey<AspectItem> CRIMSON_SNAIL_SHELL = registerKey("crimson_snail_shell");
	public static final ResourceKey<AspectItem> CRYPTWEED = registerKey("cryptweed");
	public static final ResourceKey<AspectItem> DEEP_WATER_CORAL = registerKey("deep_water_coral");
	public static final ResourceKey<AspectItem> DRIED_SWAMP_REED = registerKey("dried_swamp_reed");
	public static final ResourceKey<AspectItem> EDGE_LEAF = registerKey("edge_leaf");
	public static final ResourceKey<AspectItem> EDGE_MOSS = registerKey("edge_moss");
	public static final ResourceKey<AspectItem> EDGE_SHROOM = registerKey("edge_shroom");
	public static final ResourceKey<AspectItem> FLATHEAD_MUSHROOM = registerKey("flathead_mushroom");
	public static final ResourceKey<AspectItem> GOLDEN_CLUB = registerKey("golden_club");
	public static final ResourceKey<AspectItem> GREEN_MIDDLE_GEM = registerKey("green_middle_gem");
	public static final ResourceKey<AspectItem> HANGER = registerKey("hanger");
	public static final ResourceKey<AspectItem> LEAF = registerKey("leaf");
	public static final ResourceKey<AspectItem> LICHEN = registerKey("lichen");
	public static final ResourceKey<AspectItem> MARSH_HIBUSCUS = registerKey("marsh_hibiscus");
	public static final ResourceKey<AspectItem> MARSH_MALLOW = registerKey("marsh_mallow");
	public static final ResourceKey<AspectItem> MARSH_MARIGOLD = registerKey("marsh_marigold");
	public static final ResourceKey<AspectItem> MILKWEED = registerKey("milkweed");
	public static final ResourceKey<AspectItem> MIRE_CORAL = registerKey("mire_coral");
	public static final ResourceKey<AspectItem> MOSS = registerKey("moss");
	public static final ResourceKey<AspectItem> NETTLE = registerKey("nettle");
	public static final ResourceKey<AspectItem> OCHRE_SNAIL_SHELL = registerKey("ochre_snail_shell");
	public static final ResourceKey<AspectItem> PALE_GRASS = registerKey("pale_grass");
	public static final ResourceKey<AspectItem> PHRAGMITES = registerKey("phragmites");
	public static final ResourceKey<AspectItem> PICKERELWEED = registerKey("pickerelweed");
	public static final ResourceKey<AspectItem> PITCHER_PLANT = registerKey("pitcher_plant");
	public static final ResourceKey<AspectItem> POISON_IVY = registerKey("poison_ivy");
	public static final ResourceKey<AspectItem> ROOTS = registerKey("roots");
	public static final ResourceKey<AspectItem> ROTBULB = registerKey("rotbulb");
	public static final ResourceKey<AspectItem> SAP_SPIT = registerKey("sap_spit");
	public static final ResourceKey<AspectItem> SHOOTS = registerKey("shoots");
	public static final ResourceKey<AspectItem> SLUDGECREEP = registerKey("sludgecreep");
	public static final ResourceKey<AspectItem> SOFT_RUSH = registerKey("soft_rush");
	public static final ResourceKey<AspectItem> STRING_ROOTS = registerKey("string_roots");
	public static final ResourceKey<AspectItem> SUNDEW = registerKey("sundew");
	public static final ResourceKey<AspectItem> SWAMP_GRASS = registerKey("swamp_grass");
	public static final ResourceKey<AspectItem> SWAMP_KELP = registerKey("swamp_kelp");
	public static final ResourceKey<AspectItem> THORNS = registerKey("thorns");
	public static final ResourceKey<AspectItem> VENUS_FLY_TRAP = registerKey("venus_fly_trap");
	public static final ResourceKey<AspectItem> VOLARPAD = registerKey("volarpad");
	public static final ResourceKey<AspectItem> WATER_WEEDS = registerKey("water_weeds");
	public static final ResourceKey<AspectItem> WEEDWOOD_BARK = registerKey("weedwood_bark");

	public static ResourceKey<AspectItem> registerKey(String name) {
		return ResourceKey.create(BLRegistries.Keys.ASPECT_ITEMS, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<AspectItem> context) {
		List<ResourceKey<AspectType>> dungeonAspects = List.of(AspectTypeRegistry.UDURIIS, AspectTypeRegistry.WODREN);
		context.register(ALGAE, new AspectItem(ItemRegistry.GROUND_ALGAE.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(ARROW_ARUM, new AspectItem(ItemRegistry.GROUND_ARROW_ARUM.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(BLACKHAT_MUSHROOM, new AspectItem(ItemRegistry.GROUND_BLACKHAT_MUSHROOM.get(), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON)));
		context.register(BOG_BEAN, new AspectItem(ItemRegistry.GROUND_BOG_BEAN.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(BONESET, new AspectItem(ItemRegistry.GROUND_BONESET.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(BOTTLE_BRUSH_GRASS, new AspectItem(ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(BROOMSEDGE, new AspectItem(ItemRegistry.GROUND_BROOMSEDGE.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(BUTTON_BUSH, new AspectItem(ItemRegistry.GROUND_BUTTON_BUSH.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(CATTAIL, new AspectItem(ItemRegistry.GROUND_CATTAIL.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(CAVE_GRASS, new AspectItem(ItemRegistry.GROUND_CAVE_GRASS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(CAVE_MOSS, new AspectItem(ItemRegistry.GROUND_CAVE_MOSS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(COPPER_IRIS, new AspectItem(ItemRegistry.GROUND_COPPER_IRIS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(DRIED_SWAMP_REED, new AspectItem(ItemRegistry.GROUND_DRIED_SWAMP_REED.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(FLATHEAD_MUSHROOM, new AspectItem(ItemRegistry.GROUND_FLATHEAD_MUSHROOM.get(), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON)));
		context.register(HANGER, new AspectItem(ItemRegistry.GROUND_HANGER.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(LICHEN, new AspectItem(ItemRegistry.GROUND_LICHEN.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(MARSH_HIBUSCUS, new AspectItem(ItemRegistry.GROUND_MARSH_HIBUSCUS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(MARSH_MALLOW, new AspectItem(ItemRegistry.GROUND_MARSH_MALLOW.get(), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON)));
		context.register(MILKWEED, new AspectItem(ItemRegistry.GROUND_MILKWEED.get(), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON)));
		context.register(MOSS, new AspectItem(ItemRegistry.GROUND_MOSS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(NETTLE, new AspectItem(ItemRegistry.GROUND_NETTLE.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(PHRAGMITES, new AspectItem(ItemRegistry.GROUND_PHRAGMITES.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(PICKERELWEED, new AspectItem(ItemRegistry.GROUND_PICKERELWEED.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(SHOOTS, new AspectItem(ItemRegistry.GROUND_SHOOTS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(SLUDGECREEP, new AspectItem(ItemRegistry.GROUND_SLUDGECREEP.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(SOFT_RUSH, new AspectItem(ItemRegistry.GROUND_SOFT_RUSH.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(SWAMP_KELP, new AspectItem(ItemRegistry.GROUND_SWAMP_KELP.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(SWAMP_GRASS, new AspectItem(ItemRegistry.GROUND_SWAMP_GRASS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(ROOTS, new AspectItem(ItemRegistry.GROUND_ROOTS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(WEEDWOOD_BARK, new AspectItem(ItemRegistry.GROUND_WEEDWOOD_BARK.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(WATER_WEEDS, new AspectItem(ItemRegistry.GROUND_WATER_WEEDS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(VOLARPAD, new AspectItem(ItemRegistry.GROUND_VOLARPAD.get(), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON)));
		context.register(THORNS, new AspectItem(ItemRegistry.GROUND_THORNS.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(POISON_IVY, new AspectItem(ItemRegistry.GROUND_POISON_IVY.get(), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON)));
		context.register(LEAF, new AspectItem(ItemRegistry.GROUND_LEAF.get(), new RandomAspectCalculator(0.15F, 0.2F, 4, AspectTier.COMMON)));
		context.register(BLADDERWORT_FLOWER, new AspectItem(ItemRegistry.GROUND_BLADDERWORT_FLOWER.get(), new RandomAspectCalculator(0.25F, 0.5F, 2, AspectTier.COMMON)));
		context.register(BLADDERWORT_STALK, new AspectItem(ItemRegistry.GROUND_BLADDERWORT_STALK.get(), new RandomAspectCalculator(0.15F, 0.5F, 4, AspectTier.COMMON)));
		context.register(CRIMSON_SNAIL_SHELL, new AspectItem(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL.get(), new RandomAspectCalculator(0.85F, 0.5F, 2, AspectTier.UNCOMMON)));
		context.register(BLUE_IRIS, new AspectItem(ItemRegistry.GROUND_BLUE_IRIS.get(), new RandomAspectCalculator(0.85F, 0.5F, AspectTier.UNCOMMON)));
		context.register(BLUE_EYED_GRASS, new AspectItem(ItemRegistry.GROUND_BLUE_EYED_GRASS.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(CARDINAL_FLOWER, new AspectItem(ItemRegistry.GROUND_CARDINAL_FLOWER.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(MIRE_CORAL, new AspectItem(ItemRegistry.GROUND_MIRE_CORAL.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(MARSH_MARIGOLD, new AspectItem(ItemRegistry.GROUND_MARSH_MARIGOLD.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(GOLDEN_CLUB, new AspectItem(ItemRegistry.GROUND_GOLDEN_CLUB.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(DEEP_WATER_CORAL, new AspectItem(ItemRegistry.GROUND_DEEP_WATER_CORAL.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(OCHRE_SNAIL_SHELL, new AspectItem(ItemRegistry.GROUND_OCHRE_SNAIL_SHELL.get(), new RandomAspectCalculator(0.85F, 0.45F, 2, AspectTier.UNCOMMON)));
		context.register(BULB_CAPPED_MUSHROOM, new AspectItem(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM.get(), new RandomAspectCalculator(0.85F, 0.45F, AspectTier.UNCOMMON)));
		context.register(ANGLER_TOOTH, new AspectItem(ItemRegistry.GROUND_ANGLER_TOOTH.get(), new RandomAspectCalculator(0.85F, 0.45F, 2, AspectTier.UNCOMMON)));
		context.register(SUNDEW, new AspectItem(ItemRegistry.GROUND_SUNDEW.get(), new RandomAspectCalculator(1.25F, 0.5F, AspectTier.RARE)));
		context.register(PITCHER_PLANT, new AspectItem(ItemRegistry.GROUND_PITCHER_PLANT.get(), new RandomAspectCalculator(1.25F, 0.5F, AspectTier.RARE)));
		context.register(VENUS_FLY_TRAP, new AspectItem(ItemRegistry.GROUND_VENUS_FLY_TRAP.get(), new RandomAspectCalculator(1.25F, 0.5F, AspectTier.RARE)));

		//Sludge worm dungeon plants
		context.register(EDGE_SHROOM, new AspectItem(ItemRegistry.GROUND_EDGE_SHROOM.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON))));
		context.register(EDGE_MOSS, new AspectItem(ItemRegistry.GROUND_EDGE_MOSS.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON))));
		context.register(EDGE_LEAF, new AspectItem(ItemRegistry.GROUND_EDGE_LEAF.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 3, AspectTier.COMMON))));
		context.register(ROTBULB, new AspectItem(ItemRegistry.GROUND_ROTBULB.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON))));
		context.register(PALE_GRASS, new AspectItem(ItemRegistry.GROUND_PALE_GRASS.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON))));
		context.register(STRING_ROOTS, new AspectItem(ItemRegistry.GROUND_STRING_ROOTS.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON))));
		context.register(CRYPTWEED, new AspectItem(ItemRegistry.GROUND_CRYPTWEED.get(), new DoubleGroupAspectCalculator(new RandomAspectCalculator(0.425F, 0.35F, 1, AspectTier.COMMON, dungeonAspects), new RandomAspectCalculator(0.425F, 0.35F, 2, AspectTier.COMMON))));

		//Gems
		context.register(GREEN_MIDDLE_GEM, new AspectItem(ItemRegistry.GROUND_GREEN_MIDDLE_GEM.get(), new SetAspectCalculator(1.75F, 0.25F, List.of(AspectTypeRegistry.FERGALAZ))));
		context.register(CRIMSON_MIDDLE_GEM, new AspectItem(ItemRegistry.GROUND_CRIMSON_MIDDLE_GEM.get(),  new SetAspectCalculator(1.75F, 0.25F, List.of(AspectTypeRegistry.FIRNALAZ))));
		context.register(AQUA_MIDDLE_GEM, new AspectItem(ItemRegistry.GROUND_AQUA_MIDDLE_GEM.get(),  new SetAspectCalculator(1.75F, 0.25F, List.of(AspectTypeRegistry.BYRGINAZ))));

		//Sap spit
		context.register(SAP_SPIT, new AspectItem(ItemRegistry.SAP_SPIT.get(), new SetAspectCalculator(1.0F, 0.1F, List.of(AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.ORDANIIS))));
	}
}
