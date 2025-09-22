package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AmuletData(boolean canUnequip, boolean canDrop) {

	public static final Codec<AmuletData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.fieldOf("can_unequip").forGetter(AmuletData::canUnequip),
		Codec.BOOL.fieldOf("can_drop").forGetter(AmuletData::canDrop)
	).apply(instance, AmuletData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, AmuletData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, AmuletData::canUnequip,
		ByteBufCodecs.BOOL, AmuletData::canDrop,
		AmuletData::new
	);
}
