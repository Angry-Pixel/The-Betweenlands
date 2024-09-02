package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateRotSmellPacket(int entityID, long smellStamp, long immuneStamp) implements CustomPacketPayload {

	public static final Type<UpdateRotSmellPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_rot_smell"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRotSmellPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, UpdateRotSmellPacket::entityID,
		ByteBufCodecs.VAR_LONG, UpdateRotSmellPacket::smellStamp,
		ByteBufCodecs.VAR_LONG, UpdateRotSmellPacket::immuneStamp,
		UpdateRotSmellPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateRotSmellPacket message, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = context.player().level().getEntity(message.entityID());
			if(entity instanceof LivingEntity livingEntity)
				livingEntity.setData(AttachmentRegistry.ROT_SMELL, new RotSmellData(message.smellStamp(), message.immuneStamp()));
		});
	}
}
