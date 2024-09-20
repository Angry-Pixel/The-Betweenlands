package thebetweenlands.common.registries;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import thebetweenlands.common.TheBetweenlands;

public class StructureRegistry {

	public static final ResourceKey<Structure> CHIROMAW_NEST = makeKey("chiromaw_nest");
	public static final ResourceKey<Structure> CRAGROCK_TOWER = makeKey("cragrock_tower");
	public static final ResourceKey<Structure> DRUID_CIRCLE = makeKey("druid_circle");
	public static final ResourceKey<Structure> FLOATING_ISLAND = makeKey("floating_island");
	public static final ResourceKey<Structure> GIANT_TREE = makeKey("giant_tree");
	public static final ResourceKey<Structure> IDOL_HEAD = makeKey("idol_head");
	public static final ResourceKey<Structure> MUD_STRUCTURES = makeKey("mud_structures");
	public static final ResourceKey<Structure> SLUDGE_WORM_DUNGEON = makeKey("sludge_worm_dungeon");
	public static final ResourceKey<Structure> SLUDGE_WORM_DUNGEON_MAZE = makeKey("sludge_worm_dungeon_maze");
	public static final ResourceKey<Structure> SMALL_PORTAL = makeKey("small_portal");
	public static final ResourceKey<Structure> SMALL_RUINS = makeKey("small_ruins");
	public static final ResourceKey<Structure> SPAWNER = makeKey("spawner");
	public static final ResourceKey<Structure> SPIRIT_TREE = makeKey("spirit_tree");
	public static final ResourceKey<Structure> TAR_POOL_DUNGEON = makeKey("tar_pool_dungeon");
	public static final ResourceKey<Structure> UNDERGROUND_RUINS = makeKey("underground_ruins");
	public static final ResourceKey<Structure> UNDERWATER_RUINS = makeKey("underwater_ruins");
	public static final ResourceKey<Structure> WAYSTONE = makeKey("waystone");
	public static final ResourceKey<Structure> WEEDWOOD_PORTAL_TREE = makeKey("weedwood_portal_tree");
	public static final ResourceKey<Structure> WIGHT_FORTRESS = makeKey("wight_fortress");

	private static ResourceKey<Structure> makeKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE, TheBetweenlands.prefix(name));
	}

	//TODO these only exist for advancement datagen, please get rid of placeholder values eventually
	public static void bootstrap(BootstrapContext<Structure> context) {
		context.register(CRAGROCK_TOWER, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(FLOATING_ISLAND, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(GIANT_TREE, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(IDOL_HEAD, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(SLUDGE_WORM_DUNGEON, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(SLUDGE_WORM_DUNGEON_MAZE, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(SMALL_RUINS, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
		context.register(WIGHT_FORTRESS, new JungleTempleStructure(new Structure.StructureSettings(HolderSet.empty())));
	}
}
