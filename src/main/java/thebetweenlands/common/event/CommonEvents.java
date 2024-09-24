package thebetweenlands.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.SynchedAttachmentHandler;
import thebetweenlands.common.handler.*;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.clientbound.SyncStaticAspectsPacket;

public class CommonEvents {

	public static void init() {
		ArmorHandler.init();
		AttackDamageHandler.init();
		CorrosionHandler.init();
		ElixirCommonHandler.init();
		FoodSicknessHandler.init();
		PlayerDecayHandler.init();
		ShieldHandler.init();
		SimulacrumHandler.init();

		NeoForge.EVENT_BUS.addListener(SynchedAttachmentHandler::onPlayerJoinWorld);
		NeoForge.EVENT_BUS.addListener(CommonEvents::syncAspects);
	}

	static void syncAspects(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			CompoundTag tag = new CompoundTag();
			AspectManager.get(event.getLevel()).saveStaticAspects(tag, event.getLevel().registryAccess());
			PacketDistributor.sendToPlayer(sp, new SyncStaticAspectsPacket(tag));
		}
	}
}
