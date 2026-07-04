package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChirobarbErrupterData(int rotation, boolean shooting) {

	public static final ChirobarbErrupterData DEFAULT = new ChirobarbErrupterData(0, false);

	public static final Codec<ChirobarbErrupterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("rotation").forGetter(ChirobarbErrupterData::rotation),
		Codec.BOOL.fieldOf("shooting").forGetter(ChirobarbErrupterData::shooting)
	).apply(instance, ChirobarbErrupterData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, ChirobarbErrupterData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, ChirobarbErrupterData::rotation,
		ByteBufCodecs.BOOL, ChirobarbErrupterData::shooting,
		ChirobarbErrupterData::new
	);
}
