package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.entity.CameraOffsetter;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.common.registries.MobEffectRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.LocationCragrockTower;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CameraPositionHandler {

	public static final CameraPositionHandler INSTANCE = new CameraPositionHandler();

	public void init() {
		NeoForge.EVENT_BUS.addListener(this::calculateShakers);
		NeoForge.EVENT_BUS.addListener(this::shakeCameraPre);
		NeoForge.EVENT_BUS.addListener(this::shakeCameraPost);
	}

	private float getShakeStrength(@Nullable Entity renderViewEntity) {
		float screenShake = 0.0F;

		if (renderViewEntity != null) {
			ClientLevel level = (ClientLevel) renderViewEntity.level();
			for (Entity entity : level.entitiesForRendering()) {
				if (entity instanceof ScreenShaker shake) {
					screenShake += shake.getShakeIntensity(renderViewEntity);
				}
			}

			HashSet<ChunkPos> chunksInRange = new HashSet<>();
			for (int x = -16; x <= 16; x += 16) {
				for (int z = -16; z <= 16; z += 16) {
					chunksInRange.add(new ChunkPos((int) (renderViewEntity.getX() + x) >> 4, (int) (renderViewEntity.getZ() + z) >> 4));
				}
			}
			for (ChunkPos pos : chunksInRange) {
				if (level.getChunkSource().getChunkNow(pos.x, pos.z) != null) {
					List<ScreenShaker> shakers = level.getChunk(pos.x, pos.z).getBlockEntities().values().stream()
						.filter(blockEntity -> blockEntity instanceof ScreenShaker)
						.map(ScreenShaker.class::cast)
						.toList();
					for (ScreenShaker shaker : shakers) {
						screenShake += shaker.getShakeIntensity(renderViewEntity);
					}
				}
			}

			//Crumbling cragrock tower
			BetweenlandsWorldStorage worldData = WorldStorageGetter.getNullable(level);
			if (worldData != null) {
				List<LocationCragrockTower> towers = worldData.getLocalStorageHandler().getLocalStorages(level, LocationCragrockTower.class, renderViewEntity.getX(), renderViewEntity.getZ(), location -> location.getInnerBoundingBox().inflate(4, 4, 4).contains(renderViewEntity.position()));
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
				MobEffectInstance effect = living.getEffect(MobEffectRegistry.SHOCKED);
				if (effect != null) {
					screenShake += Math.min(0.1f, effect.getDuration() / 30.0f * 0.1f);
				}
			}
		}

		return Mth.clamp(screenShake, 0.0F, 0.15F);
	}

	private double prevPosX;
	private double prevPosY;
	private double prevPosZ;
	private boolean didChange = false;

	private final List<CameraOffsetter> offsetEntities = new ArrayList<>();
	private float shakeStrength = 0.0F;

	private void calculateShakers(ClientTickEvent.Pre event) {
		Entity entity = Minecraft.getInstance().getCameraEntity();

		if (entity != null && !Minecraft.getInstance().isPaused()) {
			this.shakeStrength = this.getShakeStrength(entity);
			this.offsetEntities.clear();

			for (Entity rendered : Minecraft.getInstance().level.entitiesForRendering()) {
				if (rendered instanceof CameraOffsetter offsetter)
					this.offsetEntities.add(offsetter);
			}
		} else {
			this.shakeStrength = 0.0F;
			this.offsetEntities.clear();
		}
	}

	private void shakeCameraPre(RenderFrameEvent.Pre event) {
		Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

		if (renderViewEntity != null && !Minecraft.getInstance().isPaused()) {
			boolean shouldChange = this.shakeStrength > 0.0F || !this.offsetEntities.isEmpty();

			if ((shouldChange && !Minecraft.getInstance().isPaused()) || this.didChange) {
				this.prevPosX = renderViewEntity.getX();
				this.prevPosY = renderViewEntity.getY();
				this.prevPosZ = renderViewEntity.getZ();

				RandomSource rnd = renderViewEntity.getRandom();
				renderViewEntity.setPos(
					renderViewEntity.getX() + rnd.nextFloat() * this.shakeStrength,
					renderViewEntity.getY() + rnd.nextFloat() * this.shakeStrength,
					renderViewEntity.getZ() + rnd.nextFloat() * this.shakeStrength);

				if (!this.offsetEntities.isEmpty()) {
					for (CameraOffsetter offset : this.offsetEntities)
						if (((Entity) offset).isAlive() && offset.applyOffset(renderViewEntity, event.getPartialTick().getRealtimeDeltaTicks()))
							break;
				}

				this.didChange = true;
			}
		}
	}

	private void shakeCameraPost(RenderFrameEvent.Post event) {
		Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

		if (renderViewEntity != null && !Minecraft.getInstance().isPaused()) {
			boolean shouldChange = this.shakeStrength > 0.0F || !this.offsetEntities.isEmpty();

			if ((shouldChange && !Minecraft.getInstance().isPaused()) || this.didChange) {
				renderViewEntity.setPos(this.prevPosX, this.prevPosY, this.prevPosZ);
				this.didChange = false;
			}
		}
	}
}
