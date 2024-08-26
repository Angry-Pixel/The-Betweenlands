package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MapDecorationRegistry;
import thebetweenlands.common.world.storage.AmateMapData;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AmateMapItem extends MapItem {

	public static final String STR_ID = "amatemap";
	public static final Map<ResourceLocation, BiomeColor> BIOME_COLORS = new HashMap<>();

	public AmateMapItem(Properties properties) {
		super(properties);
	}

	public static ItemStack setupNewMap(Level level, int worldX, int worldZ, boolean trackingPosition, boolean unlimitedTracking) {
		ItemStack itemstack = new ItemStack(ItemRegistry.FILLED_AMATE_MAP.get());
		createMapData(itemstack, level, worldX, worldZ, trackingPosition, unlimitedTracking);
		return itemstack;
	}

	@Nullable
	public static AmateMapData getData(ItemStack stack, Level level) {
		MapId mapid = stack.get(DataComponents.MAP_ID);
		return mapid == null ? null : AmateMapData.getMapData(level, getMapName(mapid.id()));
	}

	@Nullable
	public static AmateMapData getData(ItemStack stack, TooltipContext context) {
		MapId mapid = stack.get(DataComponents.MAP_ID);
		return mapid != null && context.mapData(mapid) instanceof AmateMapData mapData ? mapData : null;
	}

	@Nullable
	@Override
	protected AmateMapData getCustomMapData(ItemStack stack, Level level) {
		AmateMapData mapdata = getData(stack, level);
		if (mapdata == null && !level.isClientSide()) {
			BlockPos sharedSpawnPos = level.getSharedSpawnPos();
			mapdata = AmateMapItem.createMapData(stack, level, sharedSpawnPos.getX(), sharedSpawnPos.getZ(), false, false);
		}

		return mapdata;
	}

	private static AmateMapData createMapData(ItemStack stack, Level level, int x, int z, boolean trackingPosition, boolean unlimitedTracking) {
		MapId freeMapId = level.getFreeMapId();

		AmateMapData mapdata = new AmateMapData(x, z, trackingPosition, unlimitedTracking, false);
		AmateMapData.registerMapData(level, mapdata, getMapName(freeMapId.id())); // call our own register method
		stack.set(DataComponents.MAP_ID, freeMapId);
		return mapdata;
	}

	public static String getMapName(int id) {
		return STR_ID + "_" + id;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player) {
		//disable zooming
	}

	@Override
	@Nullable
	public Packet<?> getUpdatePacket(ItemStack stack, Level world, Player player) {
		MapId mapId = stack.get(DataComponents.MAP_ID);
		AmateMapData mapdata = getCustomMapData(stack, world);
		return mapId == null || mapdata == null ? null : mapdata.getUpdatePacket(mapId, player);
	}

	private static final Map<ChunkPos, ResourceLocation[]> CACHE = new HashMap<>();
	private static final ResourceLocation NULL_BIOME = ResourceLocation.withDefaultNamespace("null");

	@Override
	public void update(Level level, Entity viewer, MapItemSavedData data) {
		// don't update if not in the betweenlands
		if (level.dimension() != DimensionRegistries.DIMENSION_KEY) {
			return;
		}

		int biomesPerPixel = 4;
		int blocksPerPixel = 16; // don't even bother with the scale, just hardcode it
		int centerX = data.centerX;
		int centerZ = data.centerZ;
		int viewerX = Mth.floor(viewer.getX() - centerX) / blocksPerPixel + 64;
		int viewerZ = Mth.floor(viewer.getZ() - centerZ) / blocksPerPixel + 64;
		int viewRadiusPixels = 512 / blocksPerPixel;

		int startX = (centerX / blocksPerPixel) * biomesPerPixel;
		int startZ = (centerZ / blocksPerPixel) * biomesPerPixel;
		ResourceLocation[] biomes = CACHE.computeIfAbsent(new ChunkPos(startX, startZ), pos -> {
			ResourceLocation[] array = new ResourceLocation[128 * biomesPerPixel * 128 * biomesPerPixel];
			for (int l = 0; l < 128 * biomesPerPixel; ++l) {
				for (int i1 = 0; i1 < 128 * biomesPerPixel; ++i1) {
					array[l * 128 * biomesPerPixel + i1] = level
						.getBiome(new BlockPos(startX * biomesPerPixel + i1 * biomesPerPixel, 0, startZ * biomesPerPixel + l * biomesPerPixel))
						.unwrapKey()
						.map(ResourceKey::location)
						.orElse(NULL_BIOME);
				}
			}
			return array;
		});

		Map<Integer, List<Integer>> checkedChunks = new HashMap<>();
		for (int xPixel = viewerX - viewRadiusPixels + 1; xPixel < viewerX + viewRadiusPixels; ++xPixel) {
			for (int zPixel = viewerZ - viewRadiusPixels - 1; zPixel < viewerZ + viewRadiusPixels; ++zPixel) {
				if (xPixel >= 0 && zPixel >= 0 && xPixel < 128 && zPixel < 128) {

					int xPixelDist = xPixel - viewerX;
					int zPixelDist = zPixel - viewerZ;
					boolean shouldFuzz = xPixelDist * xPixelDist + zPixelDist * zPixelDist > (viewRadiusPixels - 2) * (viewRadiusPixels - 2);

					ResourceLocation biome = biomes[xPixel * biomesPerPixel + zPixel * biomesPerPixel * 128 * biomesPerPixel];

					BiomeColor colorBrightness = this.getMapColorPerBiome(biome);

					MapColor mapcolor = colorBrightness.color;
					int brightness = colorBrightness.brightness;

					if (xPixelDist * xPixelDist + zPixelDist * zPixelDist < viewRadiusPixels * viewRadiusPixels && (!shouldFuzz || (xPixel + zPixel & 1) != 0)) {
						byte orgPixel = data.colors[xPixel + zPixel * 128];
						byte ourPixel = (byte) (mapcolor.id * 4 + brightness);

						if (orgPixel != ourPixel) {
							data.setColor(xPixel, zPixel, ourPixel);
							data.setDirty();
						}
					}
				}
			}
		}

		this.locateBLLocations(level, checkedChunks, centerX, centerZ, blocksPerPixel, (AmateMapData) data);
	}

	private void locateBLLocations(Level world, Map<Integer, List<Integer>> posList, int centerX, int centerZ, int blocksPerPixel, AmateMapData data) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(world);
		if (worldStorage != null) {
			ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();

			for (Map.Entry<Integer, List<Integer>> chunkX : posList.entrySet()) {
				for (Integer z : chunkX.getValue()) {
					int x = chunkX.getKey();
					List<LocationStorage> localStorages = handler.getLocalStorages(LocationStorage.class, x << 4, z << 4, input -> true);
					if (!localStorages.isEmpty()) {
						for (LocationStorage storage : localStorages) {
							AABB aabb = storage.getEnclosingBounds();
							Vec3 center = new Vec3(aabb.minX + (aabb.maxX - aabb.minX) * 0.5D, aabb.minY + (aabb.maxY - aabb.minY) * 0.5D, aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5D);
							byte mapX = (byte) ((center.x - centerX) / (float) blocksPerPixel * 2F);
							byte mapZ = (byte) ((center.z - centerZ) / (float) blocksPerPixel * 2F);
							Holder<MapDecorationType> location = MapDecorationRegistry.getLocation(storage);
							if (location != null) {
								data.addDecoration(location, world, makeName(location, mapX, mapZ), mapX, mapZ, 180.0F, null);

								boolean done = false;
								if (location == MapDecorationRegistry.TOWER) {
									LocationCragrockTower tower = (LocationCragrockTower) storage;
									if (tower.isTopConquered()) {
										done = true;
									}
								} else if (location == MapDecorationRegistry.WIGHT_TOWER || location == MapDecorationRegistry.SPIRIT_TREE || location == MapDecorationRegistry.SLUDGE_WORM_DUNGEON) {
									LocationGuarded guarded = (LocationGuarded) storage;
									if (guarded.getGuard().isClear(world)) {
										done = true;
									}
								}
								if (done) {
									data.addDecoration(MapDecorationRegistry.CHECK, world, makeName(MapDecorationRegistry.CHECK, mapX, mapZ), mapX, mapZ, 180.0F, null);
								}
							}
						}
					}
				}
			}
		}
	}

	public static String makeName(Holder<MapDecorationType> type, int x, int z) {
		return type.value().assetId() + "_" + x + "_" + z;
	}

	private BiomeColor getMapColorPerBiome(ResourceLocation biome) {
		if (BIOME_COLORS.isEmpty()) {
			setupBiomeColors();
		}
		BiomeColor color = BIOME_COLORS.get(biome);
		return Objects.requireNonNullElseGet(color, () -> new BiomeColor(MapColor.COLOR_PINK));
	}

	public static void setupBiomeColors() {
		putBiomeColor(BiomeRegistry.PATCHY_ISLANDS, new BiomeColor(MapColor.COLOR_PINK, 7));
		putBiomeColor(BiomeRegistry.SWAMPLANDS, new BiomeColor(MapColor.METAL, 1));
		putBiomeColor(BiomeRegistry.DEEP_WATERS, new BiomeColor(MapColor.COLOR_MAGENTA, 1));
		putBiomeColor(BiomeRegistry.COARSE_ISLANDS, new BiomeColor(MapColor.COLOR_MAGENTA, 3));
		putBiomeColor(BiomeRegistry.SLUDGE_PLAINS, new BiomeColor(MapColor.EMERALD, 10));
		putBiomeColor(BiomeRegistry.MARSH, new BiomeColor(MapColor.COLOR_BLUE, 10));
		putBiomeColor(BiomeRegistry.ERODED_MARSH, new BiomeColor(MapColor.COLOR_BLUE, 8));
		putBiomeColor(BiomeRegistry.SWAMPLANDS_CLEARING, new BiomeColor(MapColor.COLOR_YELLOW, 9));
	}

	private static void putBiomeColor(ResourceKey<Biome> biome, BiomeColor color) {
		BIOME_COLORS.put(biome.location(), color);
	}

	private static class BiomeColor {
		public MapColor color;
		public int brightness;

		public BiomeColor(MapColor color, int brightness) {
			this.color = color;
			this.brightness = brightness;
		}

		public BiomeColor(MapColor color) {
			this.color = color;
			this.brightness = 1;
		}
	}
}
