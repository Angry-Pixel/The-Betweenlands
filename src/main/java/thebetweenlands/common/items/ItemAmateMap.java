package thebetweenlands.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import thebetweenlands.common.colors.AmateMapColor;
import thebetweenlands.common.handlers.SaveDataHandler;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAmateMap extends MapItem {

    public static final String STR_ID = "amatemap";
    public static final Map<ResourceLocation, BiomeColor> BIOME_COLORS = new HashMap<>();

    public ItemAmateMap(Properties p_42847_) {
        super(p_42847_);
    }

    public static ItemStack create(Level level, int x, int y, byte scale, boolean p_42891_, boolean p_42892_) {
        ItemStack itemstack = new ItemStack(ItemRegistry.USED_AMATE_MAP.get());

        // setup map nbt
        AmateMapItemSavedData mapdata = AmateMapItemSavedData.createFresh((double) x, (double) y, p_42891_, p_42892_, false);
        int id = SaveDataHandler.getFreeAmateMapId(level);
        itemstack.getOrCreateTag().putInt("amatemap", id);
        SaveDataHandler.setAmateMapData(level ,"amatemap_" + id, mapdata);
        return itemstack;
    }

    @Override
    public void update(Level level, Entity viewer, MapItemSavedData mapdata) {
        AmateMapItemSavedData map = (AmateMapItemSavedData) mapdata;
        // don't update if not in the betweenlands
        if (level.dimension() != DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) {
            return;
        }

        int blocksPerPixel = 16;
        int centerX = map.x;
        int centerZ = map.z;
        int viewerBlockX = Mth.floor((viewer.getX()) / blocksPerPixel) * blocksPerPixel;
        int viewerBlockZ = Mth.floor((viewer.getZ()) / blocksPerPixel) * blocksPerPixel;
        int viewerOffsetX = viewerBlockX - Mth.floor(centerX / blocksPerPixel) * blocksPerPixel;
        int viewerOffsetZ = viewerBlockZ - Mth.floor(centerZ / blocksPerPixel) * blocksPerPixel;
        int viewerPixelX = viewerOffsetX / blocksPerPixel + 64;
        int viewerPixelZ = viewerOffsetZ / blocksPerPixel + 64;
        int viewRadiusPixels = 256 / blocksPerPixel;

        MapItemSavedData.HoldingPlayer mapInfo = map.getHoldingPlayer((Player)viewer);
        ++mapInfo.step;

        boolean terrainChanged = false;

        final int biomeViewRange = viewRadiusPixels * blocksPerPixel / 4 * 2 + 1;

        int updatedColumns = 0;

        int prevXPixel = 0;

        Biome[][] biomes = new Biome[2][];
        biomes[0] = new Biome[biomeViewRange];
        biomes[1] = new Biome[biomeViewRange];

        Map<Integer, List<Integer>> checkedChunks = new HashMap<>();
        for (int xPixel = viewerPixelX - viewRadiusPixels + 1; xPixel < viewerPixelX + viewRadiusPixels; ++xPixel) {
            if ((xPixel & 15) == (mapInfo.step & 15) || terrainChanged) {
                terrainChanged = false;

                final int primaryBiomesArray = updatedColumns % 2;
                final int previousBiomesArray = (updatedColumns + 1) % 2;

                biomes[primaryBiomesArray] = getBiomesForGeneration(level, biomes[primaryBiomesArray], (viewerBlockX + (xPixel - viewerPixelX) * blocksPerPixel) / 4, (viewerBlockZ - (viewRadiusPixels - 1) * blocksPerPixel) / 4, 1, biomeViewRange);

                if(updatedColumns == 0 || xPixel - prevXPixel != 1) {
                    biomes[previousBiomesArray] = getBiomesForGeneration(level, biomes[previousBiomesArray], (viewerBlockX + (xPixel - viewerPixelX - 1) * blocksPerPixel) / 4, (viewerBlockZ - (viewRadiusPixels - 1) * blocksPerPixel) / 4, 1, biomeViewRange);
                }

                for (int zPixel = viewerPixelZ - viewRadiusPixels - 1; zPixel < viewerPixelZ + viewRadiusPixels; ++zPixel) {
                    if (xPixel >= 0 && zPixel >= 0 && xPixel < 128 && zPixel < 128) {
                        int xPixelDist = xPixel - viewerPixelX;
                        int zPixelDist = zPixel - viewerPixelZ;
                        boolean shouldFuzz = xPixelDist * xPixelDist + zPixelDist * zPixelDist > (viewRadiusPixels - 2) * (viewRadiusPixels - 2);

                        int biomeIndex = (zPixel - viewerPixelZ + viewRadiusPixels + 1) * blocksPerPixel / 4;
                        Biome primaryBiome = biomes[primaryBiomesArray][biomeIndex];

                        boolean isEdge = false;

                        if(biomeIndex >= blocksPerPixel / 4) {
                            Biome biomeUp = biomes[primaryBiomesArray][biomeIndex - blocksPerPixel / 4];
                            if(biomeUp != primaryBiome) {
                                isEdge = true;
                            }
                        }

                        Biome biomeLeft = biomes[previousBiomesArray][biomeIndex];
                        if(biomeLeft != primaryBiome) {
                            isEdge = true;
                        }

                        BiomeColor colorBrightness = this.getMapColorPerBiome(level, primaryBiome);

                        AmateMapColor mapcolor = colorBrightness.color;

                        if (zPixel >= 0 && xPixelDist * xPixelDist + zPixelDist * zPixelDist < viewRadiusPixels * viewRadiusPixels && (!shouldFuzz || (xPixel + zPixel & 1) != 0)) {

                            // make sure this reads the correct color
                            byte oldPixel = map.colors[xPixel + zPixel * 128];
                            byte newPixel = (byte)mapcolor.id;

                            if(isEdge) {
                                newPixel = (byte)AmateMapColor.BORDER.id;
                            }

                            if (oldPixel != newPixel) {
                                map.updateColor(xPixel, zPixel, newPixel);
                                terrainChanged = true;
                            }

                            int worldX = (centerX / blocksPerPixel + xPixel - 64) * blocksPerPixel;
                            int worldZ = (centerZ / blocksPerPixel + zPixel - 64) * blocksPerPixel;
                            int chunkX = worldX >> 4;
                            int chunkZ = worldZ >> 4;
                            if (!checkedChunks.containsKey(chunkX) || (checkedChunks.containsKey(chunkX) && !checkedChunks.get(chunkX).contains(chunkZ))) {
                                checkedChunks.computeIfAbsent(chunkX, integer -> new ArrayList<>()).add(chunkZ);
                            }
                        }
                    }
                }

                updatedColumns++;
                prevXPixel = xPixel;
            }
        }

        //locateBLLocations(level, checkedChunks, centerX, centerZ, blocksPerPixel, data);

        //AmateMapItemSavedData amateMapData = (AmateMapItemSavedData)data;


        //data.setColor(10,10, MaterialColor.COLOR_PINK.getPackedId(MaterialColor.Brightness.NORMAL));
    }


    public AmateMapItemSavedData getCustomMapData(ItemStack p_42854_, Level p_42855_) {
        Integer integer = getAmateMapId(p_42854_);
        return getSavedData(integer, p_42855_);
    }

    @Nullable
    public static Integer getAmateMapId(ItemStack p_151132_) {
        CompoundTag compoundtag = p_151132_.getTag();
        return compoundtag != null && compoundtag.contains("amatemap", 99) ? compoundtag.getInt("amatemap") : null;
    }

    @Nullable
    public static AmateMapItemSavedData getSavedData(@Nullable Integer p_151129_, Level p_151130_) {
        return p_151129_ == null ? null : SaveDataHandler.getAmateMapData(p_151130_, makeKey(p_151129_));
    }

    @Nullable
    public Packet<?> getUpdatePacket(ItemStack item, Level level, Player player) {
        Integer integer = getAmateMapId(item);
        AmateMapItemSavedData mapitemsaveddata = getSavedData(integer, level);
        return mapitemsaveddata != null ? mapitemsaveddata.getUpdatePacket(integer, player) : null;
    }

    public static String makeKey(int p_42849_) {
        return "amatemap_" + p_42849_;
    }

    public Biome[] getBiomesForGeneration(Level level, Biome[] biomes, int x, int z, int width, int height) {

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        //int[] aint = this.genBiomes.getInts(x, z, width, height);

        try
        {
            for (int x1 = x; x1 < width + x; ++x1) {
                for (int z1 = z; z1 < height + z; ++z1) {
                    biomes[(x1-x)+(z1-z)] = level.getNoiseBiome(x1, 64, z1).value();
                }
            }
            return biomes;
        }
        catch (Throwable throwable)
        {
            throw throwable;
        }
    }

    /*
    private void locateBLLocations(Level world, Map<Integer, List<Integer>> posList, int centerX, int centerZ, int blocksPerPixel, MapItemSavedData data) {
        BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();
        AmateMapData amateMapData = (AmateMapData) data;

        for (Map.Entry<Integer, List<Integer>> chunkX: posList.entrySet()) {
            for (Integer z : chunkX.getValue()) {
                int x = chunkX.getKey();
                List<LocationStorage> localStorages = handler.getLocalStorages(LocationStorage.class, x << 4, z << 4, input -> true);
                if (localStorages.size() > 0) {
                    for (LocationStorage storage : localStorages) {
                        AxisAlignedBB aabb = storage.getEnclosingBounds();
                        Vec3d center = new Vec3d(aabb.minX + (aabb.maxX - aabb.minX) * 0.5D, aabb.minY + (aabb.maxY - aabb.minY) * 0.5D, aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5D);
                        byte mapX = (byte) ((center.x - centerX) / (float) blocksPerPixel * 2F);
                        byte mapZ = (byte) ((center.z - centerZ) / (float) blocksPerPixel * 2F);
                        Location location = Location.getLocation(storage);
                        if (location != Location.NONE) {
                            amateMapData.addDecoration(new AmateMapData.BLMapDecoration(location, mapX, mapZ, (byte) 8));

                            boolean done = false;
                            if (location == Location.TOWER) {
                                LocationCragrockTower tower = (LocationCragrockTower) storage;
                                if (tower.isTopConquered()) {
                                    done = true;
                                }
                            } else if (location == Location.FORTRESS || location == Location.SPIRIT_TREE || location == Location.SLUDGE_WORM_DUNGEON) {
                                LocationGuarded guarded = (LocationGuarded) storage;
                                if (guarded.getGuard().isClear(world)) {
                                    done = true;
                                }
                            }
                            if (done) {
                                amateMapData.addDecoration(new AmateMapData.BLMapDecoration(Location.CHECK, mapX, mapZ, (byte) 8));
                            }
                        }
                    }
                }
            }
        }
    }*/

    private BiomeColor getMapColorPerBiome(Level world, Biome biome) {
        if (BIOME_COLORS.isEmpty()) {
            setupBiomeColors();
        }
        BiomeColor color = BIOME_COLORS.get(biome.getRegistryName());
        if (color != null) {
            return color;
        } else {
            return new BiomeColor(AmateMapColor.DEFALT);
        }
    }

    public static void setupBiomeColors() {
        putBiomeColor(BiomeRegistry.PATCHY_ISLANDS.biome.get(), new BiomeColor(AmateMapColor.PATCHY_ISLANDS));
        putBiomeColor(BiomeRegistry.SWAMPLANDS.biome.get(), new BiomeColor(AmateMapColor.SWAMPLANDS));
        putBiomeColor(BiomeRegistry.DEEP_WATERS.biome.get(), new BiomeColor(AmateMapColor.DEEP_WATERS));
        putBiomeColor(BiomeRegistry.COARSE_ISLANDS.biome.get(), new BiomeColor(AmateMapColor.COARSE_ISLANDS));
        putBiomeColor(BiomeRegistry.RAISED_ISLES.biome.get(), new BiomeColor(AmateMapColor.RAISED_ISLES));
        putBiomeColor(BiomeRegistry.SLUDGE_PLAINS.biome.get(), new BiomeColor(AmateMapColor.SLUDGE_PLAINS));
        putBiomeColor(BiomeRegistry.MARSH.biome.get(), new BiomeColor(AmateMapColor.MARSH));
        putBiomeColor(BiomeRegistry.ERODED_MARSH.biome.get(), new BiomeColor(AmateMapColor.ERODED_MARSH));
        //putBiomeColor(BiomeRegistry.SWAMPLANDS_CLEARING, new BiomeColor(MaterialColor.COLOR_YELLOW, 9));
    }

    private static void putBiomeColor(Biome biome, BiomeColor color) {
        BIOME_COLORS.put(biome.getRegistryName(), color);
    }

    private static class BiomeColor {
        public AmateMapColor color;

        public BiomeColor(AmateMapColor color) {
            this.color = color;
        }
    }
}
