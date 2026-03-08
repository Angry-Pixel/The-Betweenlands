package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SwingData(int startTick, boolean swinging, float startCooldown, float cooldown) {

	public static SwingData DEFAULT = new SwingData(0, false, 0, 0);

	public static final Codec<SwingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("start_tick").forGetter(SwingData::startTick),
		Codec.BOOL.fieldOf("swinging").forGetter(SwingData::swinging),
		Codec.FLOAT.fieldOf("start_cooldown").forGetter(SwingData::startCooldown),
		Codec.FLOAT.fieldOf("cooldown").forGetter(SwingData::cooldown)
	).apply(instance, SwingData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, SwingData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, SwingData::startTick,
		ByteBufCodecs.BOOL, SwingData::swinging,
		ByteBufCodecs.FLOAT, SwingData::startCooldown,
		ByteBufCodecs.FLOAT, SwingData::cooldown,
		SwingData::new
	);

	public SwingData withCooldown(float cooldown) {
		return new SwingData(this.startTick, this.swinging, this.startCooldown, cooldown);
	}

	public SwingData setSwinging(boolean swinging) {
		return new SwingData(this.startTick, swinging, this.startCooldown, this.cooldown);
	}
}
