package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationStorage;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MapDecorationRegistry {
	public static final DeferredRegister<MapDecorationType> DECORATIONS = DeferredRegister.create(Registries.MAP_DECORATION_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<MapDecorationType, MapDecorationType> SMALL_MARKER = register("small_marker");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> PORTAL = register("portal");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> SPAWN = register("spawn");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> SMALL_DUNGEON = register("small_dungeon");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> GIANT_TREE = register("giant_tree");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> RUINS = register("ruins");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> TOWER = register("cragrock_tower");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> IDOL = register("idol_head");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> WAYSTONE = register("waystone");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> BURIAL_MOUND = register("burial_mound");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> SPIRIT_TREE = register("spirit_tree");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> WIGHT_TOWER = register("wight_tower");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> SLUDGE_WORM_DUNGEON = register("sludge_worm_dungeon");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> FLOATING_ISLAND = register("floating_island");
	public static final DeferredHolder<MapDecorationType, MapDecorationType> CHECK = register("check");

	private static DeferredHolder<MapDecorationType, MapDecorationType> register(String name) {
		return DECORATIONS.register(name, () -> new MapDecorationType(TheBetweenlands.prefix(name), true, -1, false, true));
	}

	@Nullable
	public static Holder<MapDecorationType> getLocation(LocationStorage storage) {
		if (storage instanceof LocationPortal) {
			return PORTAL;
		}
		if (storage.getType() == EnumLocationType.WAYSTONE) {
			return WAYSTONE;
		}
		String name = storage.getName();
		return switch (name) {
			case "small_dungeon" -> SMALL_DUNGEON;
			case "giant_tree" -> GIANT_TREE;
			case "abandoned_shack", "ruins" -> RUINS;
			case "cragrock_tower" -> TOWER;
			case "idol_head" -> IDOL;
			case "wight_tower" -> WIGHT_TOWER;
			case "spirit_tree" -> SPIRIT_TREE;
			case "sludge_worm_dungeon" -> SLUDGE_WORM_DUNGEON;
			case "floating_island" -> FLOATING_ISLAND;
			default -> null;
		};
	}
}
