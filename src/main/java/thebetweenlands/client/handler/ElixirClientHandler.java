package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.*;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

import java.util.*;

public class ElixirClientHandler {

	private static class EntityTrail {
		private static final int MAX_CACHE_SIZE = 80;
		private final Entity entity;
		private final List<Vec3> cachedPositions = new ArrayList<>();

		private EntityTrail(Entity entity) {
			this.entity = entity;
		}

		private void update(int strength) {
			if (this.entity != null && !this.entity.isRemoved()) {
				Vec3 newPos = this.entity.position();
				if (!this.cachedPositions.isEmpty()) {
					Vec3 lastPos = this.cachedPositions.getLast();
					if (lastPos.distanceTo(newPos) > 0.5D) {
						this.cachedPositions.add(newPos);
					}
					if (this.cachedPositions.size() > MAX_CACHE_SIZE + MAX_CACHE_SIZE / 4 * (strength + 1)) {
						this.cachedPositions.removeFirst();
					}
				} else {
					this.cachedPositions.add(newPos);
				}
			}
		}
	}

	private static final Map<Entity, EntityTrail> entityTrails = new HashMap<>();

	private static EntityTrail getTrail(Entity entity) {
		EntityTrail trail = entityTrails.get(entity);
		if (trail == null) {
			trail = new EntityTrail(entity);
			entityTrails.put(entity, trail);
		}
		return trail;
	}

	private static class TrailPos {
		private final Vec3 pos;
		private Vec3 nextPos;
		private final Entity entity;
		private final int index;

		private TrailPos(Vec3 pos, int index, Entity entity) {
			this.pos = pos;
			this.index = index;
			this.entity = entity;
		}
	}

	private static Vec3 playerPos;
	private static final Comparator<TrailPos> dstSorter = new Comparator<TrailPos>() {
		@Override
		public int compare(TrailPos v1, TrailPos v2) {
			double d1 = v1.pos.distanceTo(playerPos);
			double d2 = v2.pos.distanceTo(playerPos);
			return Double.compare(d1, d2);
		}
	};

	//TODO this is a custom event apparently
//	static void setArmSwing(ArmSwingSpeedEvent event) {
//		EntityLivingBase living = event.getEntityLiving();
//		if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(living)) {
//			int strength = ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(living);
//			event.setSpeed(event.getSpeed() / (float)(2 << strength));
//		}
//	}

	static void onClientTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;

		if (player != null && player.level().isClientSide() && player == Minecraft.getInstance().player) {
			if (ElixirEffectRegistry.EFFECT_HUNTERSSENSE.get().isActive(player)) {
				int strength = ElixirEffectRegistry.EFFECT_HUNTERSSENSE.get().getStrength(player);
				Level level = player.level();
				List<Entity> entityList = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(50));
				List<TrailPos> availablePositions = new ArrayList<>();
				for (Entity e : entityList) {
					if (e == player) continue;
					EntityTrail trail = getTrail(e);
					trail.update(strength);
					TrailPos lastPos = null;
					for (int i = 0; i < trail.cachedPositions.size(); i++) {
						Vec3 pos = trail.cachedPositions.get(i);
						if (lastPos != null) lastPos.nextPos = pos;
						TrailPos tp = new TrailPos(pos, i, e);
						availablePositions.add(tp);
						lastPos = tp;
					}
				}
				playerPos = player.position();
				availablePositions.sort(dstSorter);
				int maxPointCount = 200;
				int pointCount = Math.min(maxPointCount, availablePositions.size());
				int crawlTicks = Mth.floor(20.0F + 120.0F - 120.0F / 4.0F * strength);
				for (int i = 0; i < pointCount; i++) {
					TrailPos tp = availablePositions.get(i);
					if ((player.tickCount - Mth.floor((float) crawlTicks / (float) EntityTrail.MAX_CACHE_SIZE * (float) tp.index)) % crawlTicks == 0) {
						Vec3 pos = tp.pos;
						if (tp.nextPos != null) {
							int subSegments = 10;
							Vec3 nextPos = tp.nextPos;
							for (int s = 0; s <= subSegments; s++) {
								if ((player.tickCount - Mth.floor((float) crawlTicks / (float) EntityTrail.MAX_CACHE_SIZE * (tp.index + s / (float) subSegments))) % crawlTicks == 0) {
									double tpx = pos.x + 0.5F;
									double tpy = pos.y + 0.05F;
									double tpz = pos.z + 0.5F;
									double tpx2 = nextPos.x + 0.5F;
									double tpy2 = nextPos.y;
									double tpz2 = nextPos.z + 0.5F;
									double tpxi = tpx + (tpx2 - tpx) / (double) subSegments * s;
									double tpyi = tpy + (tpy2 - tpy) / (double) subSegments * s;
									double tpzi = tpz + (tpz2 - tpz) / (double) subSegments * s;
//									BLParticles.PORTAL.spawn(level, tpxi, tpyi, tpzi, ParticleFactory.ParticleArgs.get().withScale(0.3F));
								}
							}
						} else {
							double tpx = pos.x + 0.5F;
							double tpy = pos.y + 0.05F;
							double tpz = pos.z + 0.5F;
//							BLParticles.PORTAL.spawn(level, tpx, tpy, tpz, ParticleFactory.ParticleArgs.get().withScale(0.3F));
						}
					}
				}
				Iterator<Map.Entry<Entity, EntityTrail>> it = entityTrails.entrySet().iterator();
				Map.Entry<Entity, EntityTrail> entry;
				while (it.hasNext() && (entry = it.next()) != null) {
					EntityTrail trail = entry.getValue();
					if (trail.entity == null || trail.entity.isRemoved() || !entityList.contains(entry.getKey())) {
						it.remove();
					}
				}
			} else {
				entityTrails.clear();
			}

			if (ElixirEffectRegistry.EFFECT_SWIFTARM.get().isActive(player)) {
				if (Minecraft.getInstance().options.keyAttack.consumeClick() && !player.isBlocking()) {
					try {
						HitResult target = Minecraft.getInstance().hitResult;
						if (target == null || target.getType() == HitResult.Type.MISS) {
							Minecraft.getInstance().startAttack();
						} else if (target != null) {
							if (!player.swinging) {
								Minecraft.getInstance().continueAttack(true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			entityTrails.clear();
		}

		if (player != null) {
			updatePlayerRootboundTicks(player);
		}
	}

	static void changePlayerRotation(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		if (player != null) {
			CompoundTag nbt = player.getPersistentData();

			if (ElixirEffectRegistry.EFFECT_BASILISK.get().isActive(player) || ElixirEffectRegistry.EFFECT_PETRIFY.get().isActive(player)) {
				player.setXRot(player.xRotO = nbt.getFloat("thebetweenlands.petrify.pitch"));
				player.setYRot(player.yRotO = nbt.getFloat("thebetweenlands.petrify.yaw"));
				player.setYHeadRot(player.yHeadRotO = nbt.getFloat("thebetweenlands.petrify.yawHead"));
			}
		}
	}

	//TODO good god the prediction renderer sucks, im not doing this right now
//	static void setPredictionRotation(ArrowLooseEvent event) {
//		if (event.getEntity() == Minecraft.getInstance().player) {
//			ArrowPredictionRenderer.setRandomYawPitch();
//		}
//	}
//
//	static void renderArrowPrediction(RenderLevelStageEvent event) {
//		Player player = Minecraft.getInstance().player;
//		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && player != null) {
//			if (ElixirEffectRegistry.EFFECT_SAGITTARIUS.get().isActive(player)) {
//				ArrowPredictionRenderer.render(Math.min((ElixirEffectRegistry.EFFECT_SAGITTARIUS.get().getStrength(player) + 1) / 3.0F, 1.0F));
//			}
//		}
//	}

	private static void updatePlayerRootboundTicks(LivingEntity entity) {
		CompoundTag nbt = entity.getPersistentData();
		if (entity.hasEffect(ElixirEffectRegistry.EFFECT_SHOCKED.get().getElixirEffect()) || entity.hasEffect(ElixirEffectRegistry.ROOT_BOUND) || ElixirEffectRegistry.EFFECT_BASILISK.get().isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.get().isActive(entity)) {
			nbt.putInt("thebetweenlands.stuckTicks", 5);
		} else {
			int rootBoundTicks = nbt.getInt("thebetweenlands.stuckTicks");
			if (rootBoundTicks > 1) {
				nbt.putInt("thebetweenlands.stuckTicks", rootBoundTicks - 1);
			} else {
				nbt.remove("thebetweenlands.stuckTicks");
			}
		}
	}

	static void updateFOVHack(ViewportEvent.ComputeFov event) {
		Player entity = Minecraft.getInstance().player;
		CompoundTag nbt = entity.getPersistentData();

		//NBT is necessary so that FOV doesn't flicker when potion wears off .-.
		if (entity.hasEffect(ElixirEffectRegistry.EFFECT_SHOCKED.get().getElixirEffect()) || entity.hasEffect(ElixirEffectRegistry.ROOT_BOUND) || ElixirEffectRegistry.EFFECT_BASILISK.get().isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.get().isActive(entity) || nbt.contains("thebetweenlands.stuckTicks")) {
			event.setFOV(1);
		}
	}
}
