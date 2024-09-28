package thebetweenlands.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.SynchedAttachmentHandler;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.handler.*;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.clientbound.SyncStaticAspectsPacket;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DamageTypeRegistry;

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
		NeoForge.EVENT_BUS.addListener(CommonEvents::tickSwarm);
	}

	static void syncAspects(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			CompoundTag tag = new CompoundTag();
			AspectManager.get(event.getLevel()).saveStaticAspects(tag, event.getLevel().registryAccess());
			PacketDistributor.sendToPlayer(sp, new SyncStaticAspectsPacket(tag));
		}
	}

	static void tickSwarm(PlayerTickEvent.Post event) {
		if (!event.getEntity().level().isClientSide()) {
			Player player = event.getEntity();
			SwarmedData cap = player.getData(AttachmentRegistry.SWARMED);

			Entity source = cap.getSwarmSource();
			if (source != null && !source.isAlive()) {
				source = null;
				cap.setSwarmSource(null);
			}

			if (cap.getHurtTimer() > 0) {
				cap.setHurtTimer(player, cap.getHurtTimer() - 1);
			}

			if (cap.getSwarmedStrength() > 0) {
				if (player.isInWater() || player.isOnFire()) {
					cap.setSwarmedStrength(player, 0);
				} else if (player.swinging || (player.getY() - player.yo) > 0.1f || player.isShiftKeyDown()) {
					cap.setSwarmedStrength(player, cap.getSwarmedStrength() - 0.01f);
					cap.setHurtTimer(player, 5);
					player.setShiftKeyDown(false);
				}

				float dYaw = Mth.wrapDegrees(player.getYRot() - cap.getLastYaw());
				float dPitch = Mth.wrapDegrees(player.getXRot() - cap.getLastPitch());
				float ddYaw = Mth.wrapDegrees(dYaw - cap.getLastYawDelta());
				float ddPitch = Mth.wrapDegrees(dPitch - cap.getLastPitchDelta());
				float ddRot = Mth.sqrt(ddYaw * ddYaw + ddPitch * ddPitch);

				if (ddRot > 30) {
					cap.setSwarmedStrength(player, cap.getSwarmedStrength() - (ddRot - 30) * 0.001f);
				}

				cap.setLastRotations(player.getYRot(), player.getXRot());
				cap.setLastRotationDeltas(dYaw, dPitch);
			}

			if (cap.getSwarmedStrength() < 0.1f) {
				cap.setSwarmedStrength(player, cap.getSwarmedStrength() - 0.0005f);
			} else {
				cap.setDamageTimer(cap.getDamageTimer() + 1);

				if (cap.getDamageTimer() > 15 + (1.0f - cap.getSwarmedStrength()) * 75) {
					cap.setDamageTimer(0);

					float damage = Math.max(0, Math.min(player.getHealth() - 1.0f, cap.getDamage()));

					if (damage > 0) {
						player.hurt(player.damageSources().source(DamageTypeRegistry.SWARM, source), damage);
					}
				}
			}
		}
	}
}
