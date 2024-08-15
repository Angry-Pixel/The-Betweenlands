package thebetweenlands.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class ExtraCodecs {
	public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS_CODEC = StreamCodec.composite(ByteBufCodecs.INT, o -> o.x, ByteBufCodecs.INT, o -> o.z, ChunkPos::new);
	public static final Codec<MapDecoration> DECORATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.MAP_DECORATION_TYPE.holderByNameCodec().fieldOf("type").forGetter(MapDecoration::type),
		Codec.BYTE.fieldOf("x").forGetter(MapDecoration::x),
		Codec.BYTE.fieldOf("y").forGetter(MapDecoration::y),
		Codec.BYTE.fieldOf("rot").forGetter(MapDecoration::rot),
		ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(MapDecoration::name)
	).apply(instance, MapDecoration::new));
}
