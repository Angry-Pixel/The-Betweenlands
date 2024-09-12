package thebetweenlands.common.component;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.network.clientbound.attachment.UpdateSimpleAttachmentPacket;

public class SimpleAttachmentHandler {

	public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
		ServerPlayer player = event.getEntity() instanceof ServerPlayer p ? p : null;
		if(player != null && !player.level().isClientSide()) {
			for(int i = 0; i < BLRegistries.SIMPLE_ATTACHMENT_TYPES.size(); ++i) {
				SimpleAttachmentType<?> type = BLRegistries.SIMPLE_ATTACHMENT_TYPES.byId(i);
				AttachmentType<?> type2 = NeoForgeRegistries.ATTACHMENT_TYPES.get(type.getAttachmentKey());
//				TheBetweenlands.LOGGER.info("Checking if player {} has attachment {}: {}", player, type.getAttachmentKey(), player.hasData(type2));
				if(player.hasData(type2)) {
					PacketDistributor.sendToPlayer(player, new UpdateSimpleAttachmentPacket<>(player.getId(), player.getData(type2)));
				}
			}
		}
	}
	
}
