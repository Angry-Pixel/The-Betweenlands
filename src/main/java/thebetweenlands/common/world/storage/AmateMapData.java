package thebetweenlands.common.world.storage;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.MathUtils;

public class AmateMapData extends MapData {
	public final Int2ObjectMap<BLMapDecoration> decorations = new Int2ObjectOpenHashMap<>();
	private final IntSet occupiedSpots = new IntOpenHashSet();
	
    public AmateMapData(String mapname) {
        super(mapname);
    }

    public void addDecoration(BLMapDecoration deco) {
    	int x = deco.getX();
        int y = deco.getY();
    	int index = ((x + y * 128) << 8) | deco.location.id;
    	
    	if(!this.decorations.containsKey(index)) {
    		int gridSize = 3; //Check for occupied spots at a larger scale
    		int area = 24 >> gridSize;
    	
    		boolean occupied = false;
    	
    		for (int i = -area; i <= area; i++) {
                for (int j = -area; j <= area; j++) {
                	if(i*i + j*j <= area*area) {
	                    int offsetIndex = ((((x >> gridSize) + i) + ((y >> gridSize) + j) * (128 >> gridSize)) << 8) | deco.location.id;
	                    if(this.occupiedSpots.contains(offsetIndex)) {
	                    	occupied = true;
	                    	break;
	                    }
                	}
                }
            }
    		
    		if(!occupied) {
	    		this.occupiedSpots.add((((x >> gridSize) + (y >> gridSize) * (128 >> gridSize)) << 8) | deco.location.id);
	    		this.decorations.put(index, deco);
    		} else {
    			this.decorations.put(index, new BLMapDecoration(BLMapDecoration.Location.SMALL_MARKER, deco.getX(), deco.getY(), deco.getRotation()));
    		}
    	}
    }

    public void updateMapTexture() {
        MapItemRenderer mapItemRenderer = Minecraft.getMinecraft().entityRenderer.getMapItemRenderer();
        MapItemRenderer.Instance instance = mapItemRenderer.getMapRendererInstance(this);
        for (int i = 0; i < 16384; ++i) {
            int j = this.colors[i] & 255;

            if (j / 4 == 0) {
                instance.mapTextureData[i] = changeColor((i + i / 128 & 1) * 8 + 16 << 24);
            } else {
                instance.mapTextureData[i] = changeColor(MapColor.COLORS[j / 4].getMapColor(j & 3));
            }
        }
        instance.mapTexture.updateDynamicTexture();
    }

    private int changeColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int rs = MathHelper.clamp((int) ((0.293 * r + 0.269 * g + 0.089 * b) * 5), 0, 255);
        int rg = MathHelper.clamp((int) ((0.049 * r + 0.386 * g + 0.128 * b) * 5), 0, 255);
        int rb = MathHelper.clamp((int) ((0.072 * r + 0.034 * g + 0.111 * b) * 5), 0, 255);
        return rs << 16 | rg << 8 | rb | ((rgb >> 24) & 0xFF << 24);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte[] locationStorage = nbt.getByteArray("locations");
        if (locationStorage.length > 0) {
            deserializeLocations(locationStorage);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.decorations.size() > 0) {
            compound.setByteArray("locations", serializeLocations());
        }

        return compound;
    }

    public void deserializeLocations(byte[] arr) {
    	this.decorations.clear();
    	this.occupiedSpots.clear();

        for (int i = 0; i < arr.length / 3; ++i) {
            byte id = arr[i * 3];
            byte mapX = arr[i * 3 + 1];
            byte mapZ = arr[i * 3 + 2];
            byte mapRotation = 8;

            addDecoration(new BLMapDecoration(BLMapDecoration.Location.byId(id), mapX, mapZ, mapRotation));
        }
    }

    public byte[] serializeLocations() {
        byte[] storage = new byte[this.decorations.size() * 3];

        int i = 0;
        for (BLMapDecoration location : this.decorations.values()) {
            storage[i * 3] = location.location.id;
            storage[i * 3 + 1] = location.getX();
            storage[i * 3 + 2] = location.getY();
            i++;
        }

        return storage;
    }

    public static class BLMapDecoration extends MapDecoration implements Comparable<BLMapDecoration> {

        private static final ResourceLocation MAP_ICONS = new ResourceLocation(ModInfo.ID, "textures/gui/map_icon_sheet.png");
        private final Location location;

        public BLMapDecoration(Location location, byte xIn, byte yIn, byte rotationIn) {
            super(Type.TARGET_X, xIn, yIn, rotationIn);
            this.location = location;
        }

        @Override
        public boolean render(int index) {
            Minecraft.getMinecraft().renderEngine.bindTexture(MAP_ICONS);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F + getX() / 2.0F + 64.0F, 0.0F + getY() / 2.0F + 64.0F, -0.02F);
            GlStateManager.rotate((float) (getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(location.scale, location.scale, 1.0F);

            //We don't care about depth, just the rendering order which is already sorted out
            GlStateManager.depthMask(false);
            
            float f1 = location.x;
            float f2 = location.y;
            float f3 = location.x2;
            float f4 = location.y2;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-1.0D, 1.0D, 0).tex((double) f3, (double) f2).endVertex();
            bufferbuilder.pos(1.0D, 1.0D, 0).tex((double) f1, (double) f2).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, 0).tex((double) f1, (double) f4).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, 0).tex((double) f3, (double) f4).endVertex();
            tessellator.draw();
            
            GlStateManager.depthMask(true);
            
            GlStateManager.popMatrix();
            
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof BLMapDecoration))
                return false;
            if (!super.equals(o))
                return false;
            BLMapDecoration that = (BLMapDecoration) o;
            return location == that.location;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), location);
        }

        @Override
        public int compareTo(BLMapDecoration o) {
            return Integer.compare(this.location.ordinal(), o.location.ordinal());
        }

        public enum Location {
            NONE(0, 0, 0, 0, 0, 4.0f),
            SMALL_MARKER(12, 32, 16, 16, 16, 4.0f),
            PORTAL(1, 0, 0, 16, 16, 4.0f),
            SPAWN(2, 16, 0, 16, 16, 4.0f),
            SHRINE(3, 32, 0, 16, 16, 4.0f),
            GIANT_TREE(4, 48, 0, 16, 16, 4.0f),
            RUINS(5, 64, 0, 16, 16, 4.0f),
            TOWER(6, 80, 0, 16, 16, 4.0f),
            IDOL(7, 96, 0, 16, 16, 4.0f),
            WAYSTONE(8, 112, 0, 16, 16, 4.0f),
            BURIAL_MOUND(9, 0, 16, 16, 16, 4.0f),
            SPIRIT_TREE(10, 16, 16, 16, 16, 4.0f),
            FORTRESS(11, 0, 104, 24, 24, 6.0f),
            SLUDGE_WORM_DUNGEON(13, 24, 96, 32, 32, 8.0f),
            FLOATING_ISLAND(14, 48, 16, 16, 16, 4.0f),

            CHECK(127, 0, 32, 16, 16, 4.0f);

            private float x;
            private float y;
            private float x2;
            private float y2;
            private float scale;
            
            private final byte id;

            public static final ImmutableList<Location> VALUES = ImmutableList.copyOf(values());

            Location(int id, int x, int y, int width, int height, float scale) {
            	this.id = (byte) id;
                this.x = MathUtils.linearTransformf(x, 0, 128, 0, 1);
                this.y = MathUtils.linearTransformf(y, 0, 128, 0, 1);
                this.x2 = MathUtils.linearTransformf(width + x, 0, 128, 0, 1);
                this.y2 = MathUtils.linearTransformf(height + y, 0, 128, 0, 1);
                this.scale = scale;
            }
            
            public static Location byId(int id) {
            	for(Location location : VALUES) {
            		if(location.id == id) {
            			return location;
            		}
            	}
            	return NONE;
            }

            public static Location getLocation(LocationStorage storage) {
                if (storage instanceof LocationPortal) {
                    return PORTAL;
                }
                if(storage.getType() == EnumLocationType.WAYSTONE) {
                	return WAYSTONE;
                }
                String name = storage.getName();
                switch (name) {
                    case "small_dungeon":
                        return SHRINE;
                    case "giant_tree":
                        return GIANT_TREE;
                    case "abandoned_shack":
                    case "ruins":
                        return RUINS;
                    case "cragrock_tower":
                        return TOWER;
                    case "idol_head":
                        return IDOL;
                    case "wight_tower":
                        return FORTRESS;
                    case "spirit_tree":
                        return SPIRIT_TREE;
                    case "sludge_worm_dungeon":
                    	return SLUDGE_WORM_DUNGEON;
                    case "floating_island":
                    	return FLOATING_ISLAND;
                }
                return NONE;
            }
        }
    }
}
