package thebetweenlands.common.component;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.network.clientbound.attachment.UpdateSynchedAttachmentPacket;

public class SimpleAttachmentHandler {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void syncPlayerAttachment(ServerPlayer player, AttachmentType<? extends ISynchedAttachment<?>> attachmentType) {
		PacketDistributor.sendToPlayer(player, new UpdateSynchedAttachmentPacket(AttachmentHolderIdentifier.of(player), attachmentType, player.getData(attachmentType)));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void syncLevelAttachmentToPlayer(Level level, ServerPlayer player, AttachmentType<? extends ISynchedAttachment<?>> attachmentType) {
		PacketDistributor.sendToPlayer(player, new UpdateSynchedAttachmentPacket(AttachmentHolderIdentifier.of(level), attachmentType, level.getData(attachmentType)));
	}
	
	public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
		Level level = event.getLevel();
		ServerPlayer player = event.getEntity() instanceof ServerPlayer p ? p : null;
		if(player != null && !player.level().isClientSide()) {
			for(int i = 0; i < BLRegistries.SYNCHED_ATTACHMENT_TYPES.size(); ++i) {
				SynchedAttachmentType<?> type = BLRegistries.SYNCHED_ATTACHMENT_TYPES.byId(i);
				AttachmentType<?> type2 = NeoForgeRegistries.ATTACHMENT_TYPES.get(type.getAttachmentKey());
				try {
					@SuppressWarnings("unchecked")
					AttachmentType<? extends ISynchedAttachment<?>> attachmentType = (AttachmentType<? extends ISynchedAttachment<?>>)type2;
					
					if(player.hasData(type2)) {
						syncPlayerAttachment(player, attachmentType);
					}
					
					if(level.hasAttachments() && level.hasData(type2)) {
						syncLevelAttachmentToPlayer(level, player, attachmentType);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
