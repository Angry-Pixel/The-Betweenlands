package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ShieldSpitData(int ticks, int cooldown) {

	public static final ShieldSpitData EMPTY = new ShieldSpitData(0, 0);

	public static final Codec<ShieldSpitData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("ticks").forGetter(ShieldSpitData::ticks),
		Codec.INT.fieldOf("cooldown").forGetter(ShieldSpitData::cooldown)
	).apply(instance, ShieldSpitData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, ShieldSpitData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, ShieldSpitData::ticks,
		ByteBufCodecs.INT, ShieldSpitData::cooldown,
		ShieldSpitData::new
	);

	public ShieldSpitData setSpitTicks(int ticks) {
		return new ShieldSpitData(ticks, this.cooldown());
	}

	public ShieldSpitData setCooldown(int cooldown) {
		return new ShieldSpitData(this.ticks(), cooldown);
	}
}
