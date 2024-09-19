package thebetweenlands.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.util.ExtraCodecs;

public class EntitySwirlParticleOptions extends SwirlParticleOptions {

	public final Vec3 targetOffset;

	public EntitySwirlParticleOptions(ParticleType<?> type, Vec3 offset, Vec3 target, Vec3 targetMotion, Vec3 targetOffset, double rotationSpeed, boolean rotate) {
		super(type, offset, target, targetMotion, rotationSpeed, rotate);
		this.targetOffset = targetOffset;
	}

	public static final MapCodec<EntitySwirlParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(o -> o.type),
			Vec3.CODEC.fieldOf("offset").forGetter(o -> o.offset),
			Vec3.CODEC.fieldOf("target").forGetter(o -> o.target),
			Vec3.CODEC.fieldOf("target_motion").forGetter(o -> o.targetMotion),
			Vec3.CODEC.fieldOf("target_offset").forGetter(o -> o.targetOffset),
			Codec.DOUBLE.fieldOf("rotation_speed").forGetter(o -> o.rotationSpeed),
			Codec.BOOL.fieldOf("rotate").forGetter(o -> o.rotate3D))
		.apply(instance, EntitySwirlParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntitySwirlParticleOptions> STREAM_CODEC = ExtraCodecs.composite(
		ByteBufCodecs.registry(Registries.PARTICLE_TYPE), p -> p.type,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.offset,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.target,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.targetMotion,
		ByteBufCodecs.fromCodec(Vec3.CODEC), p -> p.targetOffset,
		ByteBufCodecs.DOUBLE, p -> p.rotationSpeed,
		ByteBufCodecs.BOOL, p -> p.rotate3D,
		EntitySwirlParticleOptions::new
	);
}
