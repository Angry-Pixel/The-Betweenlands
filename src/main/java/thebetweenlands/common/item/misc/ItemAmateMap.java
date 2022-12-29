package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.network.clientbound.MessageAmateMap;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ItemRegistry.ICustomMeshCallback;
import thebetweenlands.common.world.storage.AmateMapData;
import thebetweenlands.common.world.storage.AmateMapData.BLMapDecoration.Location;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class ItemAmateMap extends ItemMap implements ICustomMeshCallback, IRenamableItem {

    public static final String STR_ID = "amatemap";
    private static final Map<ResourceLocation, BiomeColor> BIOME_COLORS = new HashMap<>();

    public ItemAmateMap() {
        setCreativeTab(BLCreativeTabs.SPECIALS);
    }

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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(player.isSneaking()) {
            if (!world.isRemote) {
                player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
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
        if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.provider.getDimension() == data.dimension && viewer instanceof EntityPlayer && viewer.ticksExisted % 20 == 0) {
            int blocksPerPixel = 16;
            int centerX = data.xCenter;
            int centerZ = data.zCenter;
            int viewerBlockX = MathHelper.floor((viewer.posX) / blocksPerPixel) * blocksPerPixel;
            int viewerBlockZ = MathHelper.floor((viewer.posZ) / blocksPerPixel) * blocksPerPixel;
            int viewerOffsetX = viewerBlockX - MathHelper.floor(centerX / blocksPerPixel) * blocksPerPixel;
            int viewerOffsetZ = viewerBlockZ - MathHelper.floor(centerZ / blocksPerPixel) * blocksPerPixel;
            int viewerPixelX = viewerOffsetX / blocksPerPixel + 64;
            int viewerPixelZ = viewerOffsetZ / blocksPerPixel + 64;
            int viewRadiusPixels = 256 / blocksPerPixel;

            MapData.MapInfo mapInfo = data.getMapInfo((EntityPlayer)viewer);
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
                    
                    biomes[primaryBiomesArray] = world.getBiomeProvider().getBiomesForGeneration(biomes[primaryBiomesArray], (viewerBlockX + (xPixel - viewerPixelX) * blocksPerPixel) / 4, (viewerBlockZ - (viewRadiusPixels - 1) * blocksPerPixel) / 4, 1, biomeViewRange);
                    
                    if(updatedColumns == 0 || xPixel - prevXPixel != 1) {
                    	biomes[previousBiomesArray] = world.getBiomeProvider().getBiomesForGeneration(biomes[previousBiomesArray], (viewerBlockX + (xPixel - viewerPixelX - 1) * blocksPerPixel) / 4, (viewerBlockZ - (viewRadiusPixels - 1) * blocksPerPixel) / 4, 1, biomeViewRange);
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
                            
                            BiomeColor colorBrightness = this.getMapColorPerBiome(world, primaryBiome);
                            
                            MapColor mapcolor = colorBrightness.color;
                            int brightness = colorBrightness.brightness;

                            if (zPixel >= 0 && xPixelDist * xPixelDist + zPixelDist * zPixelDist < viewRadiusPixels * viewRadiusPixels && (!shouldFuzz || (xPixel + zPixel & 1) != 0)) {
                                byte oldPixel = data.colors[xPixel + zPixel * 128];
                                byte newPixel = (byte) (mapcolor.colorIndex * 4 + brightness);

                                if(isEdge) {
                                	newPixel = (byte)(29 * 4);
                                }
                                
                                if (oldPixel != newPixel) {
                                    data.colors[xPixel + zPixel * 128] = newPixel;
                                    data.updateMapData(xPixel, zPixel);
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
