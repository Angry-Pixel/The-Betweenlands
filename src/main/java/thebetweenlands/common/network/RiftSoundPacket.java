package thebetweenlands.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Locale;
import java.util.function.Function;

public record RiftSoundPacket(RiftSoundType soundType) implements CustomPacketPayload {

	public static final Type<RiftSoundPacket> TYPE = new Type<>(TheBetweenlands.prefix("rift_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RiftSoundPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.fromCodec(RiftSoundType.CODEC), RiftSoundPacket::soundType, RiftSoundPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RiftSoundPacket packet, IPayloadContext context) {
		Player player = context.player();
		Level level = player.level();
		Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(packet.soundType().getSoundEvent().getLocation(), SoundSource.AMBIENT, 1.0F, 1.0F, level.getRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0F, 0.0F, 0.0F, false) {
			@Override
			public float getPitch() {
				if (player.getY() < TheBetweenlands.CAVE_START) {
					return (0.5F + (float) player.getY() / TheBetweenlands.CAVE_START * 0.5F) * packet.soundType().getPitch(level);
				}

				return 1.0F;
			}

			@Override
			public float getVolume() {
				if (player.getY() < TheBetweenlands.CAVE_START) {
					return (0.15F + (float) player.getY() / TheBetweenlands.CAVE_START * 0.85F);
				}
				return 1.0F;
			}
		});
	}

	public enum RiftSoundType implements StringRepresentable {
		CREAK(SoundRegistry.RIFT_CREAK.get(), level -> level.getRandom().nextFloat() * 0.3F + 0.7F),
		OPEN(SoundRegistry.RIFT_OPEN.get(), level -> 1.0F);

		private final SoundEvent event;
		private final Function<Level, Float> pitch;

		public static final StringRepresentable.EnumCodec<RiftSoundType> CODEC = StringRepresentable.fromEnum(RiftSoundType::values);

		RiftSoundType(SoundEvent event, Function<Level, Float> pitch) {
			this.event = event;
			this.pitch = pitch;
		}

		public SoundEvent getSoundEvent() {
			return this.event;
		}

		public float getPitch(Level level) {
			return this.pitch.apply(level);
		}

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
