package thebetweenlands.common.world.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.*;
import javax.annotation.Nullable;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.AmateMapPacket;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmateMapData extends MapItemSavedData {

	private static final Map<String, AmateMapData> CLIENT_DATA = new HashMap<>();

	public AmateMapData(int x, int y, boolean tracking, boolean trackingUnlimited, boolean locked) {
		super(x, y, (byte) 4, tracking, trackingUnlimited, locked, DimensionRegistries.DIMENSION_KEY);
	}

	public static AmateMapData createFresh(double x, double y, boolean tracking, boolean trackingUnlimited, boolean locked) {
		int scale = 128 * 5;
		int j = Mth.floor((x + 64.0D) / (double) scale);
		int k = Mth.floor((y + 64.0D) / (double) scale);
		int l = j * scale + scale / 2 - 64;
		int i1 = k * scale + scale / 2 - 64;
		return new AmateMapData(l, i1, tracking, trackingUnlimited, locked);
	}

	public static AmateMapData load(CompoundTag tag, HolderLookup.Provider registries) {
		MapItemSavedData ogData = MapItemSavedData.load(tag, registries);
		final boolean trackingPosition = !tag.contains("trackingPosition", 1) || tag.getBoolean("trackingPosition");
		final boolean unlimitedTracking = tag.getBoolean("unlimitedTracking");
		final boolean locked = tag.getBoolean("locked");
		AmateMapData data = new AmateMapData(ogData.centerX, ogData.centerZ, trackingPosition, unlimitedTracking, locked);

		data.colors = ogData.colors;
		data.bannerMarkers.putAll(ogData.bannerMarkers);
		data.frameMarkers.putAll(ogData.frameMarkers);

		for (DecorationHolder decoration : DecorationHolder.CODEC.listOf()
			.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("decorations"))
			.resultOrPartial(error -> TheBetweenlands.LOGGER.warn("Failed to parse map decoration: '{}'", error))
			.orElse(List.of())) {
			MapDecoration mapdecoration1 = decoration.decoration();
			MapDecoration mapdecoration = data.decorations.put(decoration.id(), mapdecoration1);
			if (!mapdecoration1.equals(mapdecoration)) {
				if (mapdecoration != null && mapdecoration.type().value().trackCount()) {
					data.trackedDecorationCount--;
				}

				if (decoration.decoration().type().value().trackCount()) {
					data.trackedDecorationCount++;
				}
				data.setDecorationsDirty();
			}
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		tag = super.save(tag, registries);

		List<DecorationHolder> holders = new ArrayList<>();
		this.decorations.forEach((s, decoration) -> {
			if (decoration.type().value().showOnItemFrame()) {
				holders.add(new DecorationHolder(s, decoration));
			}
		});
		tag.put("decorations", DecorationHolder.CODEC.listOf().encodeStart(NbtOps.INSTANCE, holders).getOrThrow());
		return tag;
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
		return packet instanceof ClientboundMapItemDataPacket mapItemDataPacket ? new AmateMapPacket(mapItemDataPacket).toVanillaClientbound() : packet;
	}

	public record DecorationHolder(String id, MapDecoration decoration) {
		public static final Codec<DecorationHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(DecorationHolder::id),
			ExtraCodecs.DECORATION_CODEC.fieldOf("decoration").forGetter(DecorationHolder::decoration)
		).apply(instance, DecorationHolder::new));
	}
}
