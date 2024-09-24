package thebetweenlands.common.world.gen.warp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record NoiseSlider(double target, int size, int offset) {
	public static final Codec<NoiseSlider> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.DOUBLE.fieldOf("target").forGetter(obj -> obj.target),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(obj -> obj.size),
			Codec.INT.fieldOf("offset").forGetter(obj -> obj.offset))
		.apply(instance, NoiseSlider::new));

	public double applySlide(double density, double y) {
		if (this.size <= 0) {
			return density;
		} else {
			double slide = (y - (double)this.offset) / (double)this.size;
			return Mth.clampedLerp(this.target, density, slide);
		}
	}
}
