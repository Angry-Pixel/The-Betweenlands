package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CorrosionData(int corrosion, int coating) {

	public static final CorrosionData EMPTY = new CorrosionData(0, 0);

	public static final Codec<CorrosionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("corrosion").forGetter(CorrosionData::corrosion),
		Codec.INT.fieldOf("coating").forGetter(CorrosionData::coating)
	).apply(instance, CorrosionData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, CorrosionData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, CorrosionData::corrosion,
		ByteBufCodecs.INT, CorrosionData::coating,
		CorrosionData::new
	);

	public CorrosionData withCorrosion(int corrosion) {
		return new CorrosionData(corrosion, this.coating);
	}

	public CorrosionData withCoating(int coating) {
		return new CorrosionData(this.corrosion, coating);
	}

}
