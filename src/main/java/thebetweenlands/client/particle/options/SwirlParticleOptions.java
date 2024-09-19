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

public class SwirlParticleOptions implements ParticleOptions {

	protected final ParticleType<?> type;
	public final Vec3 offset;
	public final Vec3 target;
	public final Vec3 targetMotion;
	public final double rotationSpeed;
	public final boolean rotate3D;

	public SwirlParticleOptions(ParticleType<?> type, Vec3 offset, Vec3 target, Vec3 targetMotion, double rotationSpeed, boolean rotate) {
		this.type = type;
		this.offset = offset;
		this.target = target;
		this.targetMotion = targetMotion;
		this.rotationSpeed = rotationSpeed;
		this.rotate3D = rotate;
	}

	public static final MapCodec<SwirlParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(o -> o.type),
			Vec3.CODEC.fieldOf("offset").forGetter(o -> o.offset),
			Vec3.CODEC.fieldOf("target").forGetter(o -> o.target),
			Vec3.CODEC.fieldOf("target_motion").forGetter(o -> o.targetMotion),
			Codec.DOUBLE.fieldOf("rotation_speed").forGetter(o -> o.rotationSpeed),
			Codec.BOOL.fieldOf("rotate").forGetter(o -> o.rotate3D))
		.apply(instance, SwirlParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SwirlParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.PARTICLE_TYPE), p -> p.type,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.offset,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.target,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.targetMotion,
		ByteBufCodecs.DOUBLE, p -> p.rotationSpeed,
		ByteBufCodecs.BOOL, p -> p.rotate3D,
		SwirlParticleOptions::new
	);

	@Override
	public ParticleType<?> getType() {
		return this.type;
	}
}
