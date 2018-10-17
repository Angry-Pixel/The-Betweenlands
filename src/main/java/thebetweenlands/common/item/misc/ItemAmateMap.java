package thebetweenlands.common.item.misc;

import com.google.common.collect.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.network.clientbound.MessageAmateMap;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ItemRegistry.ICustomMeshCallback;
import thebetweenlands.common.world.storage.AmateMapData;
import thebetweenlands.common.world.storage.AmateMapData.BLMapDecoration.Location;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.LocalStorageHandlerImpl;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.common.world.storage.location.LocationStorage;

import javax.annotation.Nullable;
import java.util.*;

public class ItemAmateMap extends ItemMap implements ICustomMeshCallback {

    public static final String STR_ID = "amatemap";
    private static final Map<ResourceLocation, BiomeColor> BIOME_COLORS = new HashMap<>();

    public static ItemStack setupNewMap(World worldIn, double worldX, double worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
        ItemStack itemstack = new ItemStack(ItemRegistry.AMATE_MAP, 1, worldIn.getUniqueDataId(STR_ID));
        String s = STR_ID + "_" + itemstack.getMetadata();
        MapData mapdata = new AmateMapData(s);
        worldIn.setData(s, mapdata);
        mapdata.scale = scale;
        mapdata.calculateMapCenter(worldX, worldZ, mapdata.scale);
        mapdata.dimension = worldIn.provider.getDimension();
        mapdata.trackingPosition = trackingPosition;
        mapdata.unlimitedTracking = unlimitedTracking;
        mapdata.markDirty();
        return itemstack;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static AmateMapData loadMapData(int mapId, World worldIn) {
        String s = STR_ID + "_" + mapId;
        return (AmateMapData)worldIn.loadData(AmateMapData.class, s);
    }

    @Nullable
    @Override
    public AmateMapData getMapData(ItemStack stack, World worldIn) {
        String s = STR_ID + "_" + stack.getMetadata();
        AmateMapData mapdata = (AmateMapData)worldIn.loadData(AmateMapData.class, s);

        if (mapdata == null && !worldIn.isRemote) {
            stack.setItemDamage(worldIn.getUniqueDataId(STR_ID));
            s = STR_ID + "_" + stack.getMetadata();
            mapdata = new AmateMapData(s);
            mapdata.scale = 3;
            mapdata.calculateMapCenter((double)worldIn.getWorldInfo().getSpawnX(), (double)worldIn.getWorldInfo().getSpawnZ(), mapdata.scale);
            mapdata.dimension = worldIn.provider.getDimension();
            mapdata.markDirty();
            worldIn.setData(s, mapdata);
        }

        return mapdata;
    }

    @Override
    public void updateMapData(World world, Entity viewer, MapData data) {
        if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.provider.getDimension() == data.dimension && viewer instanceof EntityPlayer) {
            int biomesPerPixel = 4;
            int blocksPerPixel = 16;
            int centerX = data.xCenter;
            int centerZ = data.zCenter;
            int viewerX = MathHelper.floor(viewer.posX - (double) centerX) / blocksPerPixel + 64;
            int viewerZ = MathHelper.floor(viewer.posZ - (double) centerZ) / blocksPerPixel + 64;
            int viewRadiusPixels = 256 / blocksPerPixel;

            MapData.MapInfo mapInfo = data.getMapInfo((EntityPlayer)viewer);
            ++mapInfo.step;
            boolean flag = false;

            // use the generation map, which is larger scale than the other biome map
            int startX = (centerX / blocksPerPixel - 64) * biomesPerPixel;
            int startZ = (centerZ / blocksPerPixel - 64) * biomesPerPixel;
            Biome[] biomes = null;

            Map<Integer, List<Integer>> checkedChunks = new HashMap<>();
            for (int xPixel = viewerX - viewRadiusPixels + 1; xPixel < viewerX + viewRadiusPixels; ++xPixel) {
                if ((xPixel & 15) == (mapInfo.step & 15) || flag) {
                    if (biomes == null) {
                        biomes = world.getBiomeProvider().getBiomesForGeneration(null, startX, startZ, 128 * biomesPerPixel, 128 * biomesPerPixel);
                    }
                    flag = false;
                    for (int zPixel = viewerZ - viewRadiusPixels - 1; zPixel < viewerZ + viewRadiusPixels; ++zPixel) {
                        if (xPixel >= 0 && zPixel >= 0 && xPixel < 128 && zPixel < 128) {
                            int xPixelDist = xPixel - viewerX;
                            int zPixelDist = zPixel - viewerZ;
                            boolean shouldFuzz = xPixelDist * xPixelDist + zPixelDist * zPixelDist > (viewRadiusPixels - 2) * (viewRadiusPixels - 2);

                            Biome biome = biomes[xPixel * biomesPerPixel + zPixel * biomesPerPixel * 128 * biomesPerPixel];

                            BiomeColor colorBrightness = this.getMapColorPerBiome(world, biome);

                            MapColor mapcolor = colorBrightness.color;
                            int brightness = colorBrightness.brightness;

                            if (zPixel >= 0 && xPixelDist * xPixelDist + zPixelDist * zPixelDist < viewRadiusPixels * viewRadiusPixels && (!shouldFuzz || (xPixel + zPixel & 1) != 0)) {
                                byte orgPixel = data.colors[xPixel + zPixel * 128];
                                byte ourPixel = (byte) (mapcolor.colorIndex * 4 + brightness);

                                if (orgPixel != ourPixel) {
                                    data.colors[xPixel + zPixel * 128] = ourPixel;
                                    data.updateMapData(xPixel, zPixel);
                                    flag = true;
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
                }
            }
            locateBLLocations(world, checkedChunks, centerX, centerZ, blocksPerPixel, data);
        }
    }

    private void locateBLLocations(World world, Map<Integer, List<Integer>> posList, int centerX, int centerZ, int blocksPerPixel, MapData data) {
        BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();
        AmateMapData amateMapData = (AmateMapData) data;

        for (Map.Entry<Integer, List<Integer>> chunkX: posList.entrySet()) {
            for (Integer z : chunkX.getValue()) {
                int x = chunkX.getKey();
                List<LocationStorage> localStorages = handler.getLocalStorages(LocationStorage.class, x << 4, z << 4, input -> true);
                if (localStorages.size() > 0) {
                    for (LocationStorage storage : localStorages) {
                        Vec3d center = storage.getEnclosingBounds().getCenter();
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
                            } else if (location == Location.FORTRESS || location == Location.SPIRIT_TREE) {
                                LocationGuarded guarded = (LocationGuarded) storage;
                                if (guarded.getGuard().isClear(world)) {
                                    done = true;
                                }
                            }
                            if (done) {
                                amateMapData.addDecoration(new AmateMapData.BLMapDecoration(Location.CHECK, mapX, (byte)(mapZ + 1), (byte) 8));
                            }
                        }
                    }
                }
            }
        }
    }

    private BiomeColor getMapColorPerBiome(World world, Biome biome) {
        if (BIOME_COLORS.isEmpty()) {
            setupBiomeColors();
        }
        BiomeColor color = BIOME_COLORS.get(biome.getRegistryName());
        if (color != null) {
            return color;
        } else {
            return new BiomeColor(biome.topBlock.getMapColor(world, BlockPos.ORIGIN));
        }
    }

    private static void setupBiomeColors() {
        putBiomeColor(BiomeRegistry.PATCHY_ISLANDS, new BiomeColor(MapColor.PINK, 7));
        putBiomeColor(BiomeRegistry.SWAMPLANDS, new BiomeColor(MapColor.IRON, 1));
        putBiomeColor(BiomeRegistry.DEEP_WATERS, new BiomeColor(MapColor.MAGENTA, 1));
        putBiomeColor(BiomeRegistry.COARSE_ISLANDS, new BiomeColor(MapColor.MAGENTA, 3));
        putBiomeColor(BiomeRegistry.SLUDGE_PLAINS, new BiomeColor(MapColor.EMERALD, 10));
        putBiomeColor(BiomeRegistry.MARSH_0, new BiomeColor(MapColor.BLUE, 10));
        putBiomeColor(BiomeRegistry.MARSH_1, new BiomeColor(MapColor.BLUE, 8));
        putBiomeColor(BiomeRegistry.SWAMPLANDS_CLEARING, new BiomeColor(MapColor.YELLOW, 9));
    }

    private static void putBiomeColor(Biome biome, BiomeColor color) {
        BIOME_COLORS.put(biome.getRegistryName(), color);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        // disable zooming?
    }

    @Nullable
    @Override
    public Packet<?> createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
        Packet<?> p = super.createMapDataPacket(stack, worldIn, player);
        if (p instanceof SPacketMaps) {
            AmateMapData mapData = getMapData(stack, worldIn);
            return TheBetweenlands.networkWrapper.getPacketFrom(new MessageAmateMap(stack.getItemDamage(), mapData, (SPacketMaps) p));
        } else {
            return p;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {}

    @SideOnly(Side.CLIENT)
    @Override
    public ItemMeshDefinition getMeshDefinition() {
        return stack -> new ModelResourceLocation(getRegistryName(), "inventory");
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
