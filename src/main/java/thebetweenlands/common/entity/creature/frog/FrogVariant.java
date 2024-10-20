package thebetweenlands.common.entity.creature.frog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.BLRegistries;

import java.util.Optional;

public record FrogVariant(ResourceLocation texture, Optional<HolderSet<Biome>> biomes, Optional<ParticleOptions> particle, Optional<MobEffectInstance> touchEffect) {

	public FrogVariant(ResourceLocation texture) {
		this(texture, Optional.empty(), Optional.empty(), Optional.empty());
	}

	public FrogVariant(ResourceLocation texture, ParticleOptions particle, MobEffectInstance touchEffect) {
		this(texture, Optional.empty(), Optional.of(particle), Optional.of(touchEffect));
	}

	public static final Codec<FrogVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("texture").forGetter(FrogVariant::texture),
		RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(FrogVariant::biomes),
		ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(FrogVariant::particle),
		MobEffectInstance.CODEC.optionalFieldOf("touch_effect").forGetter(FrogVariant::touchEffect)
	).apply(instance, FrogVariant::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FrogVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, FrogVariant::texture,
		ByteBufCodecs.holderSet(Registries.BIOME).apply(ByteBufCodecs::optional), FrogVariant::biomes,
		ParticleTypes.STREAM_CODEC.apply(ByteBufCodecs::optional), FrogVariant::particle,
		MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs::optional), FrogVariant::touchEffect,
		FrogVariant::new
	);

	public static final Codec<Holder<FrogVariant>> CODEC = RegistryFileCodec.create(BLRegistries.Keys.FROG_VARIANT, DIRECT_CODEC);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FrogVariant>> STREAM_CODEC = ByteBufCodecs.holder(BLRegistries.Keys.FROG_VARIANT, DIRECT_STREAM_CODEC);
}
