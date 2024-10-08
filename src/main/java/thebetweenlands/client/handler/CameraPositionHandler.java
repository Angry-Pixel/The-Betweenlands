package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.entity.CameraOffsetter;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;

import java.util.ArrayList;
import java.util.List;

public class CameraPositionHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(CameraPositionHandler::calculateShakers);
		NeoForge.EVENT_BUS.addListener(CameraPositionHandler::shakeCamera);
	}

	private static float getShakeStrength(@Nullable Entity renderViewEntity) {
		float screenShake = 0.0F;

		if (renderViewEntity != null) {
			ClientLevel level = (ClientLevel) renderViewEntity.level();
			for (Entity entity : level.entitiesForRendering()) {
				if (entity instanceof ScreenShaker shake) {
					screenShake += shake.getShakeIntensity(renderViewEntity);
				}
			}

			//TODO vanilla no longer has a list of loaded block entities
//			for(BlockEntity tile : level.blockEntityList) {
//				if(tile instanceof ScreenShaker shake) {
//					screenShake += shake.getShakeIntensity(renderViewEntity);
//				}
//			}

			//Crumbling cragrock tower
			BetweenlandsWorldStorage worldData = BetweenlandsWorldStorage.get(level);
			if (worldData != null) {
				List<LocationCragrockTower> towers = worldData.getLocalStorageHandler().getLocalStorages(LocationCragrockTower.class, renderViewEntity.getX(), renderViewEntity.getZ(), location -> location.getInnerBoundingBox().inflate(4, 4, 4).contains(renderViewEntity.position()));
				for (LocationCragrockTower tower : towers) {
					if (tower.isCrumbling()) {
						screenShake += (float) Math.min(Math.pow(tower.getCrumblingTicks() / 400.0f, 4) * 0.08f, 0.08f);
					}
				}
			}

			//Ring of Summoning
//			List<Player> nearbyPlayers = renderViewEntity.level().getEntitiesOfClass(Player.class, renderViewEntity.getBoundingBox().inflate(32), entity -> entity.distanceTo(renderViewEntity) <= 32.0D);
//
//			for(Player player : nearbyPlayers) {
//				ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);
//				if (cap != null) {
//					if(cap.isActive()) {
//						screenShake += (ItemRingOfSummoning.MAX_USE_TIME - cap.getActiveTicks()) / (float)ItemRingOfSummoning.MAX_USE_TIME * 0.1F + 0.01F;
//					}
//				}
//			}

			//Shock
			if (renderViewEntity instanceof LivingEntity living) {
				MobEffectInstance effect = ElixirEffectRegistry.EFFECT_SHOCKED.get().getPotionEffect(living);
				if (effect != null) {
					screenShake += Math.min(0.1f, effect.getDuration() / 30.0f * 0.1f);
				}
			}
		}

		return Math.min(1.0F, Mth.clamp(screenShake, 0.0F, 0.15F) * 10.0F);
	}

	private static final List<CameraOffsetter> offsetEntities = new ArrayList<>();
	private static float shakeStrength = 0.0F;

	private static void calculateShakers(ClientTickEvent.Pre event) {
		Entity entity = Minecraft.getInstance().getCameraEntity();

		if (entity != null) {
			shakeStrength = getShakeStrength(entity);
			if (shakeStrength > 0.0F) {
				entity.moveTo(entity.getX(), entity.getY(), entity.getZ(),
					entity.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeStrength,
					entity.getXRot() + (entity.getRandom().nextFloat() * 2.5F - 1.25F) * shakeStrength);
			}

			offsetEntities.clear();

			for (Entity rendered : Minecraft.getInstance().level.entitiesForRendering()) {
				if (rendered instanceof CameraOffsetter offsetter)
					offsetEntities.add(offsetter);
			}
		} else {
			shakeStrength = 0.0f;
			offsetEntities.clear();
		}
	}

	private static void shakeCamera(ViewportEvent.ComputeCameraAngles event) {
		boolean shouldChange = shakeStrength > 0.0F;

		if (shouldChange && !Minecraft.getInstance().isPaused()) {
			RandomSource rnd = Minecraft.getInstance().player.getRandom();

			event.setYaw((float) Mth.lerp(event.getPartialTick(), event.getYaw(), event.getYaw() + (rnd.nextFloat() * 2F - 1F) * shakeStrength));
			event.setPitch((float) Mth.lerp(event.getPartialTick(), event.getPitch(), event.getPitch() + (rnd.nextFloat() * 2F - 1F) * shakeStrength));
			event.setRoll((float) Mth.lerp(event.getPartialTick(), event.getRoll(), event.getRoll() + (rnd.nextFloat() * 2F - 1F) * shakeStrength));

			if (!offsetEntities.isEmpty()) {
				for (CameraOffsetter offset : offsetEntities)
					if (((Entity) offset).isAlive() && offset.applyOffset(event.getCamera().getEntity(), (float) event.getPartialTick()))
						break;
			}

			shakeStrength = 0.0F;
		}
	}
}
