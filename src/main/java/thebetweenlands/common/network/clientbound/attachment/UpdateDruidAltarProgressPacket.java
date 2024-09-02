package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.BlockEntitySoundInstance;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DruidAltarBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public record UpdateDruidAltarProgressPacket(BlockPos pos, int progress) implements CustomPacketPayload {

	public static final Type<UpdateDruidAltarProgressPacket> TYPE = new Type<>(TheBetweenlands.prefix("druid_altar_progress"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateDruidAltarProgressPacket> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, UpdateDruidAltarProgressPacket::pos,
		ByteBufCodecs.INT, UpdateDruidAltarProgressPacket::progress,
		UpdateDruidAltarProgressPacket::new
	);

	public UpdateDruidAltarProgressPacket(DruidAltarBlockEntity entity) {
		this(entity.getBlockPos(), entity.craftingProgress);
	}

	public UpdateDruidAltarProgressPacket(DruidAltarBlockEntity entity, int progress) {
		this(entity.getBlockPos(), progress);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateDruidAltarProgressPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> handleClient(packet, context));
	}

	private static void handleClient(UpdateDruidAltarProgressPacket packet, IPayloadContext context) {
		if (context.flow().isClientbound()) {
			context.enqueueWork(() -> {
				Level level = context.player().level();
				if (level.getBlockEntity(packet.pos()) instanceof DruidAltarBlockEntity altar) {
					if (packet.progress >= 0) {
						altar.craftingProgress = packet.progress;
					} else if (packet.progress == -1) {
						for (int x = -8; x <= 8; x++) {
							for (int y = -8; y <= 8; y++) {
								for (int z = -8; z <= 8; z++) {
									BlockPos pos = altar.getBlockPos().offset(x, y, z);
									BlockState block = level.getBlockState(pos);
									if (block.is(BlockRegistry.DRUID_STONE_1) || block.is(BlockRegistry.DRUID_STONE_2) ||
										block.is(BlockRegistry.DRUID_STONE_3) || block.is(BlockRegistry.DRUID_STONE_4) ||
										block.is(BlockRegistry.DRUID_STONE_5)) {
//										BLParticles.ALTAR_CRAFTING.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ParticleArgs.get().withScale(0.25F + level.getRandom().nextFloat() * 0.2F).withData(altar));
									}
								}
							}
						}

						BetweenlandsClient.playLocalSound(new BlockEntitySoundInstance<>(SoundRegistry.DRUID_CHANT.get(), SoundSource.BLOCKS, altar, entity -> entity.craftingProgress > 0));
					}
				}
			});
		}
	}
}
