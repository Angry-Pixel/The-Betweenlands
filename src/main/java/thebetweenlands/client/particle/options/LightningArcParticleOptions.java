package thebetweenlands.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.util.ExtraCodecs;

public record LightningArcParticleOptions(float baseSize, int baseSubdivs, int branchSubdivs, float baseOffsets, float branchOffsets, int splits, float minSplitSpeed, float maxSplitSpeed, float lengthDecay, float sizeDecay, boolean lighting) implements ParticleOptions {

	public static final MapCodec<LightningArcParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("base_size").forGetter(LightningArcParticleOptions::baseSize),
			Codec.INT.fieldOf("base_subdivs").forGetter(LightningArcParticleOptions::baseSubdivs),
			Codec.INT.fieldOf("branch_subdivs").forGetter(LightningArcParticleOptions::branchSubdivs),
			Codec.FLOAT.fieldOf("base_offsets").forGetter(LightningArcParticleOptions::baseOffsets),
			Codec.FLOAT.fieldOf("branch_offsets").forGetter(LightningArcParticleOptions::branchOffsets),
			Codec.INT.fieldOf("splits").forGetter(LightningArcParticleOptions::splits),
			Codec.FLOAT.fieldOf("min_split_speed").forGetter(LightningArcParticleOptions::minSplitSpeed),
			Codec.FLOAT.fieldOf("max_split_speed").forGetter(LightningArcParticleOptions::maxSplitSpeed),
			Codec.FLOAT.fieldOf("length_decay").forGetter(LightningArcParticleOptions::lengthDecay),
			Codec.FLOAT.fieldOf("size_decay").forGetter(LightningArcParticleOptions::sizeDecay),
			Codec.BOOL.fieldOf("lighting").forGetter(LightningArcParticleOptions::lighting))
		.apply(instance, LightningArcParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LightningArcParticleOptions> STREAM_CODEC = ExtraCodecs.composite(
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::baseSize,
		ByteBufCodecs.INT, LightningArcParticleOptions::baseSubdivs,
		ByteBufCodecs.INT, LightningArcParticleOptions::branchSubdivs,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::baseOffsets,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::branchOffsets,
		ByteBufCodecs.INT, LightningArcParticleOptions::splits,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::minSplitSpeed,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::maxSplitSpeed,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::lengthDecay,
		ByteBufCodecs.FLOAT, LightningArcParticleOptions::sizeDecay,
		ByteBufCodecs.BOOL, LightningArcParticleOptions::lighting,
		LightningArcParticleOptions::new
	);

	public static LightningArcParticleOptions defaultArc() {
		return new LightningArcParticleOptions(0.04F, 3, 3, 0.3F, 0.1F, 5, 1, 2, 0.25F, 0.8F, true);
	}

	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.LIGHTNING_ARC.get();
	}

	public static class Builder {
		private float baseSize = 0.04F;
		private int baseSubdivs = 3;
		private int branchSubdivs = 3;
		private float baseOffsets = 0.3F;
		private float branchOffsets = 0.1F;
		private int splits = 5;
		private float minSplitSpeed = 1;
		private float maxSplitSpeed = 2;
		private float lengthDecay = 0.25F;
		private float sizeDecay = 0.8F;
		private boolean lighting = true;

		public Builder setBaseSize(float size) {
			this.baseSize = size;
			return this;
		}

		public Builder setSubdivs(int baseSubdivs, int branchSubdivs) {
			this.baseSubdivs = baseSubdivs;
			this.branchSubdivs = branchSubdivs;
			return this;
		}

		public Builder setOffsets(float baseOffsets, float branchOffsets) {
			this.baseOffsets = baseOffsets;
			this.branchOffsets = branchOffsets;
			return this;
		}

		public Builder setSplits(int splits) {
			this.splits = splits;
			return this;
		}

		public Builder setSplitSpeed(float minSplitSpeed, float maxSplitSpeed) {
			this.minSplitSpeed = minSplitSpeed;
			this.maxSplitSpeed = maxSplitSpeed;
			return this;
		}

		public Builder setLengthDecay(float multiplier) {
			this.lengthDecay = multiplier;
			return this;
		}

		public Builder setSizeDecay(float multiplier) {
			this.sizeDecay = multiplier;
			return this;
		}

		public Builder setLighting(boolean light) {
			this.lighting = light;
			return this;
		}

		public LightningArcParticleOptions build() {
			return new LightningArcParticleOptions(this.baseSize, this.baseSubdivs, this.branchSubdivs, this.baseOffsets, this.branchOffsets, this.splits, this.minSplitSpeed, this.maxSplitSpeed, this.lengthDecay, this.sizeDecay, this.lighting);
		}
	}
}
