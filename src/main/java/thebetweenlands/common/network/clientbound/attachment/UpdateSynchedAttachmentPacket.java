package thebetweenlands.common.network.clientbound.attachment;

import java.util.Optional;

import com.google.common.base.Preconditions;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.AttachmentHolderIdentifier;
import thebetweenlands.common.component.ISynchedAttachment;
import thebetweenlands.common.component.SynchedAttachmentType;

// TODO: Level and block data attachments (honestly really easy, just don't got the time rn)
public class UpdateSynchedAttachmentPacket<T extends ISynchedAttachment<T>> implements CustomPacketPayload {

	public static final Type<UpdateSynchedAttachmentPacket<?>> TYPE = new Type<>(TheBetweenlands.prefix("update_simple_attachment"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSynchedAttachmentPacket<?>> STREAM_CODEC = StreamCodec.of(UpdateSynchedAttachmentPacket::encode, UpdateSynchedAttachmentPacket::decode);

	// Serialization
	public static final <T extends ISynchedAttachment<T>> void encode(RegistryFriendlyByteBuf buffer, UpdateSynchedAttachmentPacket<T> packet) {
		T attachment = packet.getAttachment();
		Preconditions.checkNotNull(attachment);
		
		if(!(attachment instanceof ISynchedAttachment<T> synchedAttachment)) throw new IllegalArgumentException();
		
		AttachmentHolderIdentifier<?> holder = packet.getAttachmentHolder();
		
		SynchedAttachmentType<T> attachmentType = synchedAttachment.getSynchedAttachmentType(holder);
		
		Optional<ResourceKey<SynchedAttachmentType<?>>> resourceKeyOptional = BLRegistries.SYNCHED_ATTACHMENT_TYPES.getResourceKey(attachmentType);
		Preconditions.checkArgument(resourceKeyOptional.isPresent());
		
		holder.encode(buffer);
		buffer.writeResourceKey(resourceKeyOptional.get());
		attachmentType.getStreamCodec(holder).encode(buffer, attachment);
	}

	// Deserialization
	@SuppressWarnings("unchecked")
	public static final <T extends ISynchedAttachment<T>> UpdateSynchedAttachmentPacket<T> decode(RegistryFriendlyByteBuf buffer) {
		AttachmentHolderIdentifier<?> holder = AttachmentHolderIdentifier.decode(buffer);
		
		ResourceKey<SynchedAttachmentType<?>> resourceKey = buffer.readResourceKey(BLRegistries.Keys.SYNCHED_ATTACHMENT_TYPES);
		
		SynchedAttachmentType<T> attachmentType = (SynchedAttachmentType<T>) BLRegistries.SYNCHED_ATTACHMENT_TYPES.get(resourceKey);
		Preconditions.checkNotNull(attachmentType);
		
		AttachmentType<T> type = (AttachmentType<T>) NeoForgeRegistries.ATTACHMENT_TYPES.get(attachmentType.getAttachmentKey());
		
		StreamCodec<? super RegistryFriendlyByteBuf, T> codec = attachmentType.getStreamCodec(holder);
		T attachment = codec.decode(buffer);
		
		return new UpdateSynchedAttachmentPacket<T>(holder, type, attachment);
	}
	
	// Packet Contents
	
	private AttachmentType<T> attachmentType;
	private AttachmentHolderIdentifier<?> attachmentHolder;
	private T attachment;

	public UpdateSynchedAttachmentPacket(IAttachmentHolder attachmentHolder, AttachmentType<T> type, T attachment) {
		this.attachmentType = type;
		this.attachmentHolder = AttachmentHolderIdentifier.of(attachmentHolder);
		this.attachment = attachment;
	}

	public UpdateSynchedAttachmentPacket(AttachmentHolderIdentifier<?> attachmentHolder, AttachmentType<T> type, T attachment) {
		this.attachmentType = type;
		this.attachmentHolder = attachmentHolder;
		this.attachment = attachment;
	}
	
	public AttachmentHolderIdentifier<?> getAttachmentHolder() {
		return attachmentHolder;
	}
	
	public AttachmentType<T> getAttachmentType() {
		return attachmentType;
	}
	
	public T getAttachment() {
		return attachment;
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static <T extends ISynchedAttachment<T>> void handle(UpdateSynchedAttachmentPacket<T> message, IPayloadContext context) {
//		TheBetweenlands.LOGGER.info("Handle Sync Packet: {} {} {} {}", message, message.getAttachmentHolder(), message.getAttachmentType(), message.getAttachment());
		context.enqueueWork(() -> {
			IAttachmentHolder attachee = message.getAttachmentHolder().toRealHolder(context.player().level());
			
			if(attachee != null) {
				attachee.setData(message.getAttachmentType(), message.getAttachment());
//				TheBetweenlands.LOGGER.info("Successfully autosynced data {} to client!", NeoForgeRegistries.ATTACHMENT_TYPES.getKey(message.getAttachmentType()));
			}
		});
	}
}