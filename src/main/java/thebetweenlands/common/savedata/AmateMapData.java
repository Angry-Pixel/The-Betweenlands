package thebetweenlands.common.savedata;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.*;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.networking.AmateMapPacket;
import thebetweenlands.common.registries.DimensionRegistries;

import java.util.HashMap;
import java.util.Map;

// Refined vanilla map
public class AmateMapData extends MapItemSavedData {

	public final Int2ObjectMap<BLMapDecoration> decorations = new Int2ObjectOpenHashMap<>();
	private final IntSet occupiedSpots = new IntOpenHashSet();
	private static final Map<String, AmateMapData> CLIENT_DATA = new HashMap<>();

    public AmateMapData(int x, int y, boolean tracking, boolean trackingUnlimited, boolean locked) {
        super(x, y, (byte)4, tracking, trackingUnlimited, locked, DimensionRegistries.BETWEENLANDS_DIMENSION_KEY);
    }

    public static AmateMapData createFresh(double x, double y, boolean tracking, boolean trackingUnlimited, boolean locked) {
        int scale = 128 * 5;
        int j = Mth.floor((x + 64.0D) / (double)scale);
        int k = Mth.floor((y + 64.0D) / (double)scale);
        int l = j * scale + scale / 2 - 64;
        int i1 = k * scale + scale / 2 - 64;
        return new AmateMapData(l, i1, tracking, trackingUnlimited, locked);
    }


    public static AmateMapData load(CompoundTag tag) {
        int i = tag.getInt("x");
        int j = tag.getInt("z");
        boolean position = !tag.contains("trackingPosition", 1) || tag.getBoolean("trackingPosition");
        boolean unlimitedTracking = tag.getBoolean("unlimitedTracking");
        boolean locked = tag.getBoolean("locked");
        AmateMapData data = new AmateMapData(i, j, position, unlimitedTracking, locked);
		byte[] colors = tag.getByteArray("colors");
		if (colors.length == 16384) {
			data.colors = colors;
		}
        return data;
    }

    // Optimised save data
	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("x", this.centerX);
		tag.putInt("y", this.centerZ);
		tag.putBoolean("trackingPosition", this.trackingPosition);
		tag.putBoolean("unlimitedTracking", this.unlimitedTracking);
		tag.putBoolean("locked", this.locked);
		tag.putByteArray("colors", this.colors);
        return tag;
    }

	public void addDecoration(BLMapDecoration deco) {
		int x = deco.x();
		int y = deco.y();
		int index = ((x + y * 128) << 8) | deco.location().getId();

		if(!this.decorations.containsKey(index)) {
			int gridSize = 3; //Check for occupied spots at a larger scale
			int area = 24 >> gridSize;

			boolean occupied = false;

			for (int i = -area; i <= area; i++) {
				for (int j = -area; j <= area; j++) {
					if(i*i + j*j <= area*area) {
						int offsetIndex = ((((x >> gridSize) + i) + ((y >> gridSize) + j) * (128 >> gridSize)) << 8) | deco.location().getId();
						if(this.occupiedSpots.contains(offsetIndex)) {
							occupied = true;
							break;
						}
					}
				}
			}

			if(!occupied) {
				this.occupiedSpots.add((((x >> gridSize) + (y >> gridSize) * (128 >> gridSize)) << 8) | deco.location().getId());
				this.decorations.put(index, deco);
			} else {
				this.decorations.put(index, new BLMapDecoration(BLMapDecoration.Location.SMALL_MARKER, deco.x(), deco.y(), deco.rotation()));
			}
		}
	}

	//TODO check if still needed
//	public void updateMapTexture(MapId id) {
//		MapRenderer mapItemRenderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
//		MapRenderer.MapInstance instance = mapItemRenderer.getOrCreateMapInstance(id, this);
//		for (int i = 0; i < 16384; ++i) {
//			int j = this.colors[i] & 255;
//
//			if (j / 4 == 0) {
//				instance.texture.getPixels().setPixelRGBA(i, j, this.changeColor((i + i / 128 & 1) * 8 + 16 << 24));
//			} else {
//				instance.texture.getPixels().setPixelRGBA(i, j, this.changeColor(MapColor.byId(j / 4).getMapColor(j & 3)));
//			}
//		}
//		instance.forceUpload();
//	}
//
//	private int changeColor(int rgb) {
//		int r = (rgb >> 16) & 0xFF;
//		int g = (rgb >> 8) & 0xFF;
//		int b = rgb & 0xFF;
//		int rs = Mth.clamp((int) ((0.293 * r + 0.269 * g + 0.089 * b) * 5), 0, 255);
//		int rg = Mth.clamp((int) ((0.049 * r + 0.386 * g + 0.128 * b) * 5), 0, 255);
//		int rb = Mth.clamp((int) ((0.072 * r + 0.034 * g + 0.111 * b) * 5), 0, 255);
//		return rs << 16 | rg << 8 | rb | ((rgb >> 24) & 0xFF << 24);
//	}

	public void deserializeLocations(byte[] arr) {
		this.decorations.clear();
		this.occupiedSpots.clear();

		for (int i = 0; i < arr.length / 3; ++i) {
			byte id = arr[i * 3];
			byte mapX = arr[i * 3 + 1];
			byte mapZ = arr[i * 3 + 2];
			byte mapRotation = 8;

			this.addDecoration(new BLMapDecoration(BLMapDecoration.Location.byId(id), mapX, mapZ, mapRotation));
		}
	}

	public byte[] serializeLocations() {
		byte[] storage = new byte[this.decorations.size() * 3];

		int i = 0;
		for (BLMapDecoration location : this.decorations.values()) {
			storage[i * 3] = location.location().getId();
			storage[i * 3 + 1] = location.x();
			storage[i * 3 + 2] = location.y();
			i++;
		}

		return storage;
	}

	// [VanillaCopy] Adapted from World.getMapData
	@Nullable
	public static AmateMapData getMapData(Level level, String name) {
		if (level instanceof ServerLevel serverLevel) return (AmateMapData) serverLevel.getServer().overworld().getDataStorage().get(AmateMapData.factory(), name);
		else return CLIENT_DATA.get(name);
	}

	// Like the method above, but if we know we're on client
	@Nullable
	public static AmateMapData getClientMapData(String name) {
		return CLIENT_DATA.get(name);
	}

	// [VanillaCopy] Adapted from World.registerMapData
	public static void registerMapData(Level level, AmateMapData data, String id) {
		if (level instanceof ServerLevel serverLevel) serverLevel.getServer().overworld().getDataStorage().set(id, data);
		else CLIENT_DATA.put(id, data);
	}

	public static Factory<MapItemSavedData> factory() {
		return new SavedData.Factory<>(() -> {
			throw new IllegalStateException("Should never create an empty map saved data");
		}, AmateMapData::load, DataFixTypes.SAVED_DATA_MAP_DATA);
	}

	@Nullable
	@Override
	public Packet<?> getUpdatePacket(MapId mapId, Player player) {
		Packet<?> packet = super.getUpdatePacket(mapId, player);
		return packet instanceof ClientboundMapItemDataPacket mapItemDataPacket ? new ClientboundCustomPayloadPacket(new AmateMapPacket(this, mapItemDataPacket)) : packet;
	}

}
