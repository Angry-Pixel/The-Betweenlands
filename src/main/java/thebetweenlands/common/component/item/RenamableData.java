package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RenamableData(int maxLength) {

	public static final RenamableData DEFAULT = new RenamableData(20);

	public static final Codec<RenamableData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("max_length").forGetter(RenamableData::maxLength)
	).apply(instance, RenamableData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, RenamableData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, RenamableData::maxLength,
			RenamableData::new
		);
	
	public RenamableData withMaxLength(int maxLength) {
		return new RenamableData(maxLength);
	}
	
}
