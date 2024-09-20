package thebetweenlands.util;

import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

import java.util.function.Function;

public class ExtraCodecs {
	public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS_CODEC = StreamCodec.composite(ByteBufCodecs.INT, o -> o.x, ByteBufCodecs.INT, o -> o.z, ChunkPos::new);
	public static final Codec<MapDecoration> DECORATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.MAP_DECORATION_TYPE.holderByNameCodec().fieldOf("type").forGetter(MapDecoration::type),
		Codec.BYTE.fieldOf("x").forGetter(MapDecoration::x),
		Codec.BYTE.fieldOf("y").forGetter(MapDecoration::y),
		Codec.BYTE.fieldOf("rot").forGetter(MapDecoration::rot),
		ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(MapDecoration::name)
	).apply(instance, MapDecoration::new));

	public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
		final StreamCodec<? super B, T1> codec1,
		final Function<C, T1> getter1,
		final StreamCodec<? super B, T2> codec2,
		final Function<C, T2> getter2,
		final StreamCodec<? super B, T3> codec3,
		final Function<C, T3> getter3,
		final StreamCodec<? super B, T4> codec4,
		final Function<C, T4> getter4,
		final StreamCodec<? super B, T5> codec5,
		final Function<C, T5> getter5,
		final StreamCodec<? super B, T6> codec6,
		final Function<C, T6> getter6,
		final StreamCodec<? super B, T7> codec7,
		final Function<C, T7> getter7,
		final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
	) {
		return new StreamCodec<>() {
			@Override
			public C decode(B p_330310_) {
				T1 t1 = codec1.decode(p_330310_);
				T2 t2 = codec2.decode(p_330310_);
				T3 t3 = codec3.decode(p_330310_);
				T4 t4 = codec4.decode(p_330310_);
				T5 t5 = codec5.decode(p_330310_);
				T6 t6 = codec6.decode(p_330310_);
				T7 t7 = codec7.decode(p_330310_);
				return factory.apply(t1, t2, t3, t4, t5, t6, t7);
			}

			@Override
			public void encode(B p_332052_, C p_331912_) {
				codec1.encode(p_332052_, getter1.apply(p_331912_));
				codec2.encode(p_332052_, getter2.apply(p_331912_));
				codec3.encode(p_332052_, getter3.apply(p_331912_));
				codec4.encode(p_332052_, getter4.apply(p_331912_));
				codec5.encode(p_332052_, getter5.apply(p_331912_));
				codec6.encode(p_332052_, getter6.apply(p_331912_));
				codec7.encode(p_332052_, getter7.apply(p_331912_));
			}
		};
	}
}
