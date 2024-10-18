package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FishBaitStats(int sinkSpeed, int dissolveTime, int range, boolean glowing) {

	public static final FishBaitStats DEFAULT = new FishBaitStats(3, 200, 1, false);

	public static final Codec<FishBaitStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("sink_speed").forGetter(FishBaitStats::sinkSpeed),
		Codec.INT.fieldOf("dissolve_time").forGetter(FishBaitStats::dissolveTime),
		Codec.INT.fieldOf("range").forGetter(FishBaitStats::range),
		Codec.BOOL.fieldOf("glowing").forGetter(FishBaitStats::glowing)
	).apply(instance, FishBaitStats::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, FishBaitStats> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, FishBaitStats::sinkSpeed,
		ByteBufCodecs.INT, FishBaitStats::dissolveTime,
		ByteBufCodecs.INT, FishBaitStats::range,
		ByteBufCodecs.BOOL, FishBaitStats::glowing,
		FishBaitStats::new
	);

	public FishBaitStats withSinkSpeed(int speed) {
		return new FishBaitStats(speed, this.dissolveTime(), this.range(), this.glowing());
	}

	public FishBaitStats withDissolveTime(int time) {
		return new FishBaitStats(this.sinkSpeed(), time, this.range(), this.glowing());
	}

	public FishBaitStats withRange(int range) {
		return new FishBaitStats(this.sinkSpeed(), this.dissolveTime(), range, this.glowing());
	}

	public FishBaitStats withGlow(boolean glow) {
		return new FishBaitStats(this.sinkSpeed(), this.dissolveTime(), this.range(), glow);
	}
}
