package thebetweenlands.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.LightningArcParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class BLLightningBolt extends LightningBolt implements IEntityWithComplexSpawn {

	private static final byte EVENT_STRIKE = 80;

	private BlockPos startPos = BlockPos.ZERO;
	private int delay = 60;
	private boolean isFloatingTarget;

	public BLLightningBolt(EntityType<? extends LightningBolt> type, Level level) {
		super(type, level);
	}

	public BLLightningBolt(Level level, double x, double y, double z, int delay, boolean isFloatingTarget, boolean effectOnly) {
		super(EntityRegistry.LIGHTNING_BOLT.get(), level);
		this.delay = Math.max(8, delay);
		this.startPos = BlockPos.containing(x, y, z).offset(this.getRandom().nextInt(40) - 20, 80, this.getRandom().nextInt(40) - 20);
		this.isFloatingTarget = isFloatingTarget;
		this.setVisualOnly(effectOnly);
	}

	@Override
	public void moveTo(double x, double y, double z, float yRot, float xRot) {
		super.moveTo(x, y, z, yRot, xRot);

		if (BlockPos.ZERO.equals(this.startPos)) {
			this.startPos = BlockPos.containing(x, y, z).offset(this.getRandom().nextInt(40) - 20, 80, this.getRandom().nextInt(40) - 20);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_STRIKE) {
			this.createParticle(new Vec3(this.startPos.getX() + 0.5f, this.startPos.getY(), this.startPos.getZ() + 0.5f), this.position());

			if (this.isFloatingTarget) {
				BlockPos ground = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition());
				this.createParticle(this.position(), new Vec3(ground.getX() + 0.5f, ground.getY(), ground.getZ() + 0.5f));
			}
		}
	}

	private void createParticle(Vec3 start, Vec3 end) {
		LightningArcParticleOptions.Builder options = new LightningArcParticleOptions.Builder();

		options.setBaseSize(0.8f);
		options.setSubdivs(15, 4);
		options.setOffsets(4.0f, 0.8f);
		options.setSplits(3);
		options.setSplitSpeed(0.1f, 0.65f);
		options.setLengthDecay(0.1f);
		options.setSizeDecay(0.3f);

		TheBetweenlands.createParticle(options.build(), this.level(), start.x, start.y, start.z, ParticleFactory.ParticleArgs.get().withColor(0.5f, 0.4f, 1.0f, 0.9f).withData(end, 20));
	}

	@Override
	public void tick() {
		this.baseTick();

		this.delay = Math.max(this.delay - 1, 0);

		if (this.delay == 6) {
			this.level().playSound(null, this.blockPosition(), SoundRegistry.THUNDER.get(), SoundSource.WEATHER, 10000.0F, 0.8F + this.getRandom().nextFloat() * 0.2F);
			this.level().playSound(null, this.blockPosition(), SoundRegistry.LIGHTNING.get(), SoundSource.WEATHER, 2.0F, 0.5F + this.getRandom().nextFloat() * 0.2F);

			this.level().broadcastEntityEvent(this, EVENT_STRIKE);
		} else if (this.delay > 0 && this.delay <= 4) {
			if (this.level().isClientSide()) {
				this.level().setSkyFlashTime(2);
			} else if (!this.visualOnly) {
				if (this.delay == 4) {
					this.spawnFire(4);

					this.powerLightningRod();
					clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
					this.gameEvent(GameEvent.LIGHTNING_STRIKE);
				}

				Vec3 start = new Vec3(this.startPos.getX() + 0.5F, this.startPos.getY(), this.startPos.getZ());
				Vec3 end = this.position();

				Vec3 diff = end.subtract(start);
				Vec3 dir = diff.normalize();

				double length = diff.length();

				double range = 5.0D;

				int steps = Mth.ceil(length / range / 2);
				for (int i = 0; i < steps; i++) {
					Vec3 checkPos = start.add(diff.scale(1 / (float) steps * (i + 1)));

					List<Entity> nearbyEntities = this.level().getEntities(this, new AABB(checkPos.x - range, checkPos.y - range, checkPos.z - range, checkPos.x + range, checkPos.y + range, checkPos.z + range));

					for (Entity entity : nearbyEntities) {
						if (!(entity instanceof LightningBolt)) {
							Vec3 entityPos = entity.position();

							Vec3 projection = start.add(dir.scale(dir.dot(entityPos.subtract(start))));

							if (projection.subtract(entityPos).length() < range) {

								if (entity instanceof ItemEntity item) {
									ItemStack stack = item.getItem();
									var conversionData = stack.getItem().builtInRegistryHolder().getData(DataMapRegistry.LIGHTNING_CONVERSION);

									if (conversionData != null) {
										if (this.getRandom().nextFloat() <= conversionData.convertChance()) {
											int convertedCount = conversionData.randomCount() ? this.getRandom().nextInt(Math.min(stack.getCount(), conversionData.maxConvert())) + 1 : stack.getCount();

											stack.shrink(convertedCount);
											if (stack.isEmpty()) {
												item.discard();
											} else {
												item.setItem(stack);
											}

											ItemEntity converted = new ItemEntity(this.level(), item.getX(), item.getY(), item.getZ(), new ItemStack(conversionData.convertTo(), convertedCount));
											this.level().addFreshEntity(converted);
										}
									}

								} else if (!EventHooks.onEntityStruckByLightning(entity, this)) {
									entity.thunderHit((ServerLevel) this.level(), this);

									if (this.isFloatingTarget && entity instanceof ServerPlayer sp) {
										AdvancementCriteriaRegistry.STRUCK_BY_LIGHTNING_WHILE_FLYING.get().trigger(sp);
									}
								}

							}
						}
					}
				}
			}
		} else if (this.delay == 0 && !this.level().isClientSide()) {
			this.discard();
		}

		if (this.level().isClientSide()) {
			this.spawnArcs();
		}
	}

	private void spawnArcs() {
		Entity view = Minecraft.getInstance().getCameraEntity();

		if (view != null && (this.delay < 30 || this.tickCount % (this.delay / 20 + 1) == 0)) {
			float dst = view.distanceTo(this);

			if (dst < 100) {
				float ox = (this.getRandom().nextFloat() - 0.5f) * 4;
				float oy;
				if (this.isFloatingTarget) {
					oy = (this.getRandom().nextFloat() - 0.5f) * 4;
				} else {
					oy = this.getRandom().nextFloat() * 2;
				}
				float oz = (this.getRandom().nextFloat() - 0.5f) * 4;

				LightningArcParticleOptions.Builder options = new LightningArcParticleOptions.Builder();

				if (dst > 50) {
					//lower quality
					options.setBaseSize(0.1f);
					options.setSubdivs(2, 1);
					options.setSplits(2);
				}

				TheBetweenlands.createParticle(options.build(), this.level(), this.getX(), this.getY(), this.getZ(), ParticleFactory.ParticleArgs.get()
						.withColor(0.5f, 0.4f, 1.0f, 0.9f)
						.withData(new Vec3(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));

				if (dst < 16) {
					this.level().playSound(null, this.blockPosition(), SoundRegistry.ZAP.get(), SoundSource.AMBIENT, 1, 1);
				}
			}
		}
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buf) {
		buf.writeBoolean(this.isFloatingTarget);
		buf.writeInt(this.delay);
		buf.writeInt(this.startPos.getX());
		buf.writeInt(this.startPos.getY());
		buf.writeInt(this.startPos.getZ());
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		this.isFloatingTarget = buf.readBoolean();
		this.delay = buf.readInt();
		this.startPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
}
