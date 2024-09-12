package thebetweenlands.common.network.clientbound.attachment;

import com.google.common.base.Preconditions;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.ISimpleAttachment;
import thebetweenlands.common.component.SimpleAttachmentType;

// TODO: Level and block data attachments (honestly really easy, just don't got the time rn)
public record UpdateSimpleAttachmentPacket<T>(int entityID, T attachment) implements CustomPacketPayload {

	public static final Type<UpdateSimpleAttachmentPacket<?>> TYPE = new Type<>(TheBetweenlands.prefix("update_simple_attachment"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSimpleAttachmentPacket<?>> STREAM_CODEC = StreamCodec.of(UpdateSimpleAttachmentPacket::encode, UpdateSimpleAttachmentPacket::decode);

	public static final <T> void encode(RegistryFriendlyByteBuf buffer, UpdateSimpleAttachmentPacket<T> packet) {
		if(!(packet.attachment() instanceof ISimpleAttachment simpleAttachment)) throw new IllegalArgumentException();
		
		buffer.writeVarInt(packet.entityID());
		
		T attachment = packet.attachment();

		ResourceKey<SimpleAttachmentType<?>> resourceKey = simpleAttachment.getResourceKey();
		SimpleAttachmentType<?> genericAttachmentType = BLRegistries.SIMPLE_ATTACHMENT_TYPES.get(resourceKey);
		
		Preconditions.checkArgument(genericAttachmentType.canSerialize(attachment));
		
		// I love java generics :))))))
		@SuppressWarnings("unchecked")
		SimpleAttachmentType<T> attachmentType = (SimpleAttachmentType<T>)genericAttachmentType;
		
		buffer.writeResourceKey(resourceKey);
		attachmentType.serialize(buffer, attachment);
	}

	public static final UpdateSimpleAttachmentPacket<?> decode(RegistryFriendlyByteBuf buffer) {
		final int entityID = buffer.readVarInt();
		
		ResourceKey<SimpleAttachmentType<?>> resourceKey = buffer.readResourceKey(BLRegistries.Keys.SIMPLE_ATTACHMENT_TYPES);
		
		SimpleAttachmentType<?> attachmentType = BLRegistries.SIMPLE_ATTACHMENT_TYPES.get(resourceKey);
		Preconditions.checkNotNull(attachmentType);
		Preconditions.checkArgument(attachmentType.canDeserialize());
		
		return new UpdateSimpleAttachmentPacket<>(entityID, attachmentType.deserialize(buffer));
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static <T> void handle(UpdateSimpleAttachmentPacket<T> message, IPayloadContext context) {
		Preconditions.checkArgument(message.attachment() instanceof ISimpleAttachment);
		context.enqueueWork(() -> {
			Entity entity = context.player().level().getEntity(message.entityID());
			if(entity instanceof LivingEntity livingEntity) {
				ResourceKey<SimpleAttachmentType<?>> key = ((ISimpleAttachment)message.attachment).getResourceKey();
				SimpleAttachmentType<?> attachmentType = BLRegistries.SIMPLE_ATTACHMENT_TYPES.get(key);
				// I love java generics soooo muchh ::)))))))))
				@SuppressWarnings("unchecked")
				AttachmentType<T> attachmentKey = (AttachmentType<T>) NeoForgeRegistries.ATTACHMENT_TYPES.get(attachmentType.getAttachmentKey());
				livingEntity.setData(attachmentKey, message.attachment());
				TheBetweenlands.LOGGER.info("Successfully autosynced data {} to client! (it didn't crash!!!)", key);
			}
		});
	}
}
