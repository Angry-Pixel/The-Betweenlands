package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CompostData(int compostTime, int compostLevel, boolean showTooltip) {

	public static final Codec<CompostData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("compost_ticks").forGetter(CompostData::compostTime),
		Codec.INT.fieldOf("compost_fill_level").forGetter(CompostData::compostLevel),
		Codec.BOOL.fieldOf("show_tooltip").forGetter(CompostData::showTooltip)
	).apply(instance, CompostData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, CompostData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, CompostData::compostTime,
		ByteBufCodecs.INT, CompostData::compostLevel,
		ByteBufCodecs.BOOL, CompostData::showTooltip,
		CompostData::new
	);

	public CompostData(int compostTime, int compostLevel) {
		this(compostTime, compostLevel, true);
	}
}
