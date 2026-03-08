package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.event.ArmSwingSpeedEvent;
import thebetweenlands.api.item.BigSwingAnimation;
import thebetweenlands.common.component.item.SwingData;
import thebetweenlands.common.network.serverbound.HandleSwingPacket;
import thebetweenlands.common.registries.DataComponentRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ClientSwingHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ClientSwingHandler::handleSwing);
		NeoForge.EVENT_BUS.addListener(ClientSwingHandler::setHitCooldown);
		NeoForge.EVENT_BUS.addListener(ClientSwingHandler::modifyArmSwingSpeed);
	}

	private static void modifyArmSwingSpeed(ArmSwingSpeedEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.swinging && entity.swingingArm != null) {
			ItemStack stack = entity.getItemInHand(entity.swingingArm);

			if (!stack.isEmpty() && stack.getItem() instanceof BigSwingAnimation animation) {
				event.setSpeed(event.getSpeed() * animation.getSwingSpeedMultiplier(entity, stack));
			}
		}
	}

	private static void setHitCooldown(AttackEntityEvent event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();

		if (!stack.isEmpty() && stack.getItem() instanceof BigSwingAnimation) {
			stack.update(DataComponentRegistry.SWING_DATA, SwingData.DEFAULT, data -> data.withCooldown(player.getAttackStrengthScale(0.5F)));
		}
	}

	private static void handleSwing(InputEvent.InteractionKeyMappingTriggered event) {
		if (event.isAttack() && event.getHand() == InteractionHand.MAIN_HAND) {
			ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
			if (stack.getItem() instanceof BigSwingAnimation swingAnimation) {
				List<Integer> hitEntities = new ArrayList<>();
				if (extendedRayTrace(trace -> {
					if (trace instanceof EntityHitResult result)
						hitEntities.add(result.getEntity().getId());
				})) {
					swingAnimation.onLeftClick(Minecraft.getInstance().player, stack);
					PacketDistributor.sendToServer(new HandleSwingPacket(hitEntities));
				}
			}
		}
	}

	private static boolean extendedRayTrace(Consumer<HitResult> consumer) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player != null) {
			ItemStack stack = player.getMainHandItem();
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof BigSwingAnimation) {
					HitResult trace = getExtendedRayTrace(player.entityInteractionRange());
					if (trace instanceof EntityHitResult result && result.getEntity().invulnerableTime == 0 && result.getEntity() != player) {
						consumer.accept(trace);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	public static HitResult getExtendedRayTrace(double dist) {
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();

		HitResult result = null;
		if (mc.level != null && viewEntity != null) {
			result = viewEntity.pick(dist, 0.0F, false);
			Vec3 viewPos = viewEntity.getEyePosition(0.0F);

			double calcdist = dist;
			if (result.getType() != HitResult.Type.MISS) {
				calcdist = result.getLocation().distanceTo(viewPos);
			}
			Vec3 startVec = viewEntity.getViewVector(1.0F);
			Vec3 endVec = viewPos.add(startVec.x * dist, startVec.y * dist, startVec.z * dist);

			Entity targetEntity = null;
			Vec3 hitVec = null;
			List<Entity> list = mc.level.getEntities(viewEntity, viewEntity.getBoundingBox()
				.expandTowards(startVec.x * dist, startVec.y * dist, startVec.z * dist)
				.inflate(1.0D, 1.0D, 1.0D), EntitySelector.NO_SPECTATORS.and(Entity::isAttackable));
			double entityDist = calcdist;
			for (Entity entity : list) {
				AABB entityAABB = entity.getBoundingBox().inflate(entity.getPickRadius());
				Optional<Vec3> traceResult = entityAABB.clip(viewPos, endVec);

				if (entityAABB.contains(viewPos)) {
					if (entityDist >= 0.0D) {
						targetEntity = entity;
						hitVec = traceResult.orElse(viewPos);
						entityDist = 0.0D;
					}
				} else if (traceResult.isPresent()) {
					double d1 = viewPos.distanceTo(traceResult.get());
					if ((d1 < entityDist) || (entityDist == 0.0D)) {
						if (entity.getRootVehicle() == viewEntity.getRootVehicle() && !entity.canRiderInteract()) {
							if (entityDist == 0.0D) {
								targetEntity = entity;
								hitVec = traceResult.get();
							}
						} else {
							targetEntity = entity;
							hitVec = traceResult.get();
							entityDist = d1;
						}
					}
				}
			}
			if ((targetEntity != null) && ((entityDist < calcdist) || (result == null))) {
				result = new EntityHitResult(targetEntity, hitVec);
			}
		}
		return result;
	}
}
