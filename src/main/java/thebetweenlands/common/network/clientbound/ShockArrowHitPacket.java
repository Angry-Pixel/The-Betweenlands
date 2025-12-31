package thebetweenlands.common.network.clientbound;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.LightningArcParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public record ShockArrowHitPacket(List<ArcPositions> shockPositions) implements CustomPacketPayload {

	public static final Type<ShockArrowHitPacket> TYPE = new Type<>(TheBetweenlands.prefix("shock_chain"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ShockArrowHitPacket> STREAM_CODEC = StreamCodec.composite(
		ArcPositions.STREAM_CODEC.apply(ByteBufCodecs.list()), ShockArrowHitPacket::shockPositions,
		ShockArrowHitPacket::new);

	@SuppressWarnings("unused")
	//if I dont have some other field here my IDE absolutely freaks out as it considers this the canonical constructor.
	//How stupid
	public ShockArrowHitPacket(boolean unused, List<Pair<Vec3, Vec3>> shockPairs) {
		this(shockPairs.stream().map(pair -> new ArcPositions(pair.getFirst(), pair.getSecond())).toList());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ShockArrowHitPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			for(ArcPositions shockPos : packet.shockPositions()) {
				Vec3 from = shockPos.from();
				Vec3 to = shockPos.to();

				if(from != null && to != null) {
					TheBetweenlands.createParticle(LightningArcParticleOptions.defaultArc(), context.player().level(), from.x(), from.y(), from.z(),
						ParticleFactory.ParticleArgs.get().withColor(0.3f, 0.5f, 1.0f, 0.9f).withData(new Vec3(to.x(), to.y(), to.z())));

					context.player().level().playSound(null, BlockPos.containing(from), SoundRegistry.ZAP.get(), SoundSource.PLAYERS, 1, 1);
				}
			}
		});
	}

	private record ArcPositions(@Nullable Vec3 from, @Nullable Vec3 to) {
		public static StreamCodec<RegistryFriendlyByteBuf, ArcPositions> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodec(Vec3.CODEC), ArcPositions::from,
			ByteBufCodecs.fromCodec(Vec3.CODEC), ArcPositions::to,
			ArcPositions::new
		);
	}
}
