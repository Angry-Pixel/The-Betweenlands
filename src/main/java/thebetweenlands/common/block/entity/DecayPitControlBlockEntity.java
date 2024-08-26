package thebetweenlands.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.client.audio.DecayPitGearsSoundInstance;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class DecayPitControlBlockEntity extends SyncedBlockEntity implements ScreenShaker {

	public float animationTicks = 0;
	public float animationTicksPrev = 0;
	public float plugDropTicks = 0;
	public float plugDropTicksPrev = 0;
	public float floorFadeTicks = 0;
	public float floorFadeTicksPrev = 0;
	public int spawnType = 0;
	public boolean isPlugged = false; // set to true if boss spawn needed
	public boolean showFloor = true; // set to false if boss spawn needed
	private int prevShakeTimer;
	private int shakeTimer;
	private boolean shaking = false;
	private int shakingTimerMax = 60;
	public boolean playGearSound = true;
	public boolean spawnDrops = false;
	public int deathTicks = 0;
	public int tentacleCooldown = 300;
	public int plugJump = 0;
	public int plugJumpPrev = 0;
	public float plugRotation = 0F;

	public DecayPitControlBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DECAY_PIT_CONTROL.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DecayPitControlBlockEntity entity) {
		if (!entity.isPlugged()) {
			entity.animationTicksPrev = entity.animationTicks;

			entity.animationTicks += 1F;
			if (entity.animationTicks >= 360F)
				entity.animationTicks = entity.animationTicksPrev = 0;

			if (!level.isClientSide()) {

				if (entity.animationTicks == 15 || entity.animationTicks == 195) {
					entity.spawnSludgeJet(level, pos.getX() + 5.5D, pos.getY() + 3D, pos.getZ() - 1.5D);
					entity.spawnSludgeJet(level, pos.getX() - 4.5D, pos.getY() + 3D, pos.getZ() + 2.5D);
				}

				if (entity.animationTicks == 60 || entity.animationTicks == 240) {
					entity.spawnSludgeJet(level, pos.getX() + 2.5D, pos.getY() + 3D, pos.getZ() - 4.5D);
					entity.spawnSludgeJet(level, pos.getX() - 1.5D, pos.getY() + 3D, pos.getZ() + 5.5D);
				}

				if (entity.animationTicks == 105 || entity.animationTicks == 285) {
					entity.spawnSludgeJet(level, pos.getX() - 1.5D, pos.getY() + 3D, pos.getZ() - 4.5D);
					entity.spawnSludgeJet(level, pos.getX() + 2.5D, pos.getY() + 3D, pos.getZ() + 5.5D);
				}

				if (entity.animationTicks == 150 || entity.animationTicks == 330) {
					entity.spawnSludgeJet(level, pos.getX() - 4.5D, pos.getY() + 3D, pos.getZ() - 1.5D);
					entity.spawnSludgeJet(level, pos.getX() + 5.5D, pos.getY() + 3D, pos.getZ() + 2.5D);
				}

				// TODO remove ghetto syncing
				if (level.getGameTime() % 20 == 0)
					entity.updateBlock(level, pos, state);

				if (level.getGameTime() % 2400 == 0) { // once every 2 minutes
					// S
					entity.checkTurretSpawn(level, pos, 4, 12, 11);
					entity.checkTurretSpawn(level, pos, -4, 12, 11);
					// E
					entity.checkTurretSpawn(level, pos, 11, 12, 4);
					entity.checkTurretSpawn(level, pos, 11, 12, -4);
					// N
					entity.checkTurretSpawn(level, pos, 4, 12, -11);
					entity.checkTurretSpawn(level, pos, -4, 12, -11);
					// W
					entity.checkTurretSpawn(level, pos, -11, 12, -4);
					entity.checkTurretSpawn(level, pos, -11, 12, 4);
				}

				// spawn stuff here
				if (level.getGameTime() % 80 == 0) {
					Entity thing = entity.getEntitySpawned(level, pos, entity.getSpawnType());
					if (thing != null) {
						thing.setPos(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
						level.addFreshEntity(thing);
					}
				}
				if (entity.getSpawnType() == 5) {
					entity.setPlugged(true);
					entity.setSpawnXPAndDrops(true);
					entity.removeInvisiBlocks(level, pos);
					entity.updateBlock(level, pos, state);
					level.playSound(null, pos.offset(1, 6, 0), SoundEvents.ANVIL_BREAK, SoundSource.HOSTILE, 0.5F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
					level.playSound(null, pos.offset(-1, 6, 0), SoundEvents.ANVIL_BREAK, SoundSource.HOSTILE, 0.5F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
					level.playSound(null, pos.offset(0, 6, 1), SoundEvents.ANVIL_BREAK, SoundSource.HOSTILE, 0.5F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
					level.playSound(null, pos.offset(0, 6, -1), SoundEvents.ANVIL_BREAK, SoundSource.HOSTILE, 0.5F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);


				}
			} else {
				entity.spawnAmbientParticles(level, pos);
			}
			entity.checkSurfaceCollisions(level, pos);
		}

		if (entity.isPlugged()) {
			entity.plugDropTicksPrev = entity.plugDropTicks;
			entity.floorFadeTicksPrev = entity.floorFadeTicks;
			if (level.isClientSide()) {
				if (entity.plugDropTicks <= 0.8F) {
					entity.chainBreakParticles(level, pos.offset(1, 6, 0));
					entity.chainBreakParticles(level, pos.offset(-1, 6, 0));
					entity.chainBreakParticles(level, pos.offset(0, 6, 1));
					entity.chainBreakParticles(level, pos.offset(0, 6, -1));
				}
			}

			if (entity.plugDropTicks <= 1.6F)
				entity.plugDropTicks += 0.2F;


			if (entity.plugDropTicks == 0.6F) {
				entity.shaking = true;
				if (!level.isClientSide())
					level.playSound(null, pos, SoundRegistry.PLUG_LOCK.get(), SoundSource.HOSTILE, 1F, 1F);
			}

			if (entity.plugDropTicks > 1.6F && entity.plugDropTicks <= 2)
				entity.plugDropTicks += 0.1F;

			if (entity.plugDropTicks >= 2)
				if (entity.getShowFloor())
					entity.floorFadeTicks += 0.025F;

			if (entity.floorFadeTicks >= 1)
				if (!level.isClientSide()) {
					entity.setShowFloor(false);
					entity.shakeTimer = 0;
					entity.updateBlock(level, pos, state);
				}

			if (entity.shaking)
				entity.shake(60);
		}

		if (!level.isClientSide() && entity.getSpawnXPAndDrops()) {
			entity.setDeathTicks(entity.getDeathTicks() + 1);
			if (entity.getDeathTicks() > 40 && entity.getDeathTicks() % 5 == 0) {
				int xp = 10;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					level.addFreshEntity(new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 3.0D, pos.getZ() + 0.5D, dropXP));
				}
			}

			if (entity.getDeathTicks() == 80) {
				int xp = 120;
				while (xp > 0) {
					int dropXP = ExperienceOrb.getExperienceValue(xp);
					xp -= dropXP;
					level.addFreshEntity(new ExperienceOrb(level, pos.getX() + 0.5D, pos.getY() + 3.0D, pos.getZ() + 0.5D, dropXP));
				}
			}

			if (entity.getDeathTicks() > 120) {
				entity.setSpawnXPAndDrops(false);
				entity.updateBlock(level, pos, state);
			}
		}

		if (level.isClientSide()) {
			if (!entity.isPlugged())
				if (entity.playGearSound) {
					entity.playGearsSound();
					entity.playGearSound = false;
				}
		}

		if (entity.isPlugged() && !entity.getShowFloor() && entity.getTentacleSpawnCountDown() >= 0) {

			if (!level.isClientSide()) {
				entity.setTentacleSpawnCountDown(entity.getTentacleSpawnCountDown() - 1);

				// Syncs to add shake and final particles
				if (entity.getTentacleSpawnCountDown() == 100 || entity.getTentacleSpawnCountDown() == 59 || entity.getTentacleSpawnCountDown() == 29 || entity.getTentacleSpawnCountDown() == 1)
					entity.updateBlock(level, pos, state);

				// sounds
				if (entity.getTentacleSpawnCountDown() % 30 == 0 && entity.getTentacleSpawnCountDown() <= 270 && entity.getTentacleSpawnCountDown() > 150 || entity.getTentacleSpawnCountDown() % 33 == 0 && entity.getTentacleSpawnCountDown() <= 270 && entity.getTentacleSpawnCountDown() > 150)
					level.playSound(null, pos, SoundRegistry.PIT_FALL.get(), SoundSource.HOSTILE, (entity.getTentacleSpawnCountDown() * 0.004F) * 0.25F, 0.5F + (entity.getTentacleSpawnCountDown() * 0.004F) * 0.5F);
				// sounds
				if (entity.getTentacleSpawnCountDown() == 150)
					level.playSound(null, pos, SoundRegistry.WORM_SPLAT.get(), SoundSource.HOSTILE, 0.125F, 0.3F);

				if (entity.getTentacleSpawnCountDown() == 60 || entity.getTentacleSpawnCountDown() == 30) {
					level.playSound(null, pos, SoundRegistry.PLUG_LOCK.get(), SoundSource.HOSTILE, 0.5F, 1F);
					level.playSound(null, pos, SoundRegistry.WALL_SLAM.get(), SoundSource.HOSTILE, 1F, 0.75F);
					entity.updateBlock(level, pos, state);
				}

				if (entity.getTentacleSpawnCountDown() == 0) {
					// whizz-bang
					level.playSound(null, pos, SoundRegistry.WALL_SLAM.get(), SoundSource.BLOCKS, 1F, 0.75F);
					level.playSound(null, pos, SoundRegistry.SLUDGE_MENACE_SPAWN.get(), SoundSource.BLOCKS, 1, 1);
					level.setBlockAndUpdate(pos, BlockRegistry.GLOWING_BETWEENSTONE_TILE.get().defaultBlockState());

//					EntitySludgeMenace menace = new EntitySludgeMenace(level);
//					menace.setPositionToAnchor(pos, Direction.UP, Direction.NORTH);
//					level.addFreshEntity(menace);
				}
			}

			if (entity.getTentacleSpawnCountDown() <= 100) {
				entity.shaking = true;
				entity.shakeTimer = 0;

				if (level.isClientSide()) {
					entity.spawnAmbientParticles(level, pos);
				}
			}

			if (level.isClientSide()) {
				entity.plugJumpPrev = entity.plugJump;
				if (entity.plugJump > 0)
					entity.plugJump--;
			}

			if (entity.getTentacleSpawnCountDown() == 60 || entity.getTentacleSpawnCountDown() == 30 || entity.getTentacleSpawnCountDown() == 1) {
				if (level.isClientSide()) {
					entity.plugJump = 2 + level.getRandom().nextInt(5);
					entity.plugRotation = (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 5F;
				}
			}

			if (entity.getTentacleSpawnCountDown() == 1) {
				if (level.isClientSide()) {
					entity.plugBreakParticles(level, pos.offset(0, 1, -1));
					entity.plugBreakParticles(level, pos.offset(1, 1, 0));
					entity.plugBreakParticles(level, pos.offset(-1, 1, 0));
					entity.plugBreakParticles(level, pos.offset(0, 1, 1));
					entity.plugBreakParticles(level, pos.offset(0, 0, -0));
				}
			}
		}
	}

	private void setTentacleSpawnCountDown(int tentacle_countdown) {
		tentacleCooldown = tentacle_countdown;
		this.setChanged();
	}

	private int getTentacleSpawnCountDown() {
		return tentacleCooldown;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void playGearsSound() {
		SoundInstance chainSound = new DecayPitGearsSoundInstance(this);
		Minecraft.getInstance().getSoundManager().play(chainSound);
	}

	public void chainBreakParticles(Level level, BlockPos pos) {
		double px = pos.getX() + 0.5D;
		double py = pos.getY() + 0.5D;
		double pz = pos.getZ() + 0.5D;
		for (int i = 0, amount = 10; i < amount; i++) {
			double ox = level.getRandom().nextDouble() * 0.6F - 0.3F;
			double oz = level.getRandom().nextDouble() * 0.6F - 0.3F;
			double motionX = level.getRandom().nextDouble() * 0.4F - 0.2F;
			double motionY = level.getRandom().nextDouble() * 0.3F + 0.075F;
			double motionZ = level.getRandom().nextDouble() * 0.4F - 0.2F;
			level.addAlwaysVisibleParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.DECAY_PIT_HANGING_CHAIN.get().defaultBlockState()), px + ox, py, pz + oz, motionX, motionY, motionZ);
		}
	}

	public void plugBreakParticles(Level level, BlockPos pos) {
		double px = pos.getX() + 0.5D;
		double py = pos.getY() + 0.5D;
		double pz = pos.getZ() + 0.5D;
		for (int i = 0, amount = 40; i < amount; i++) {
			double ox = level.getRandom().nextDouble() * 0.6F - 0.3F;
			double oz = level.getRandom().nextDouble() * 0.6F - 0.3F;
			double motionX = level.getRandom().nextDouble() * 0.6F - 0.3F;
			double motionY = level.getRandom().nextDouble() * 0.6F;
			double motionZ = level.getRandom().nextDouble() * 0.6F - 0.3F;
			level.addAlwaysVisibleParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(BlockRegistry.DUNGEON_DOOR_RUNES)), px + ox, py, pz + oz, motionX, motionY, motionZ);
			level.addAlwaysVisibleParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4.get().defaultBlockState()), px + ox, py, pz + oz, motionX, motionY, motionZ);
		}
	}

	private void removeInvisiBlocks(Level level, BlockPos pos) {
		Iterable<BlockPos> blocks = BlockPos.betweenClosed(pos.offset(-4, 2, -4), pos.offset(4, 2, 4));
//		for (BlockPos posIteration : blocks)
//			if (this.isInvisibleBlock(level.getBlockState(posIteration)))
//				level.removeBlock(posIteration, false);
	}

	private void checkTurretSpawn(Level level, BlockPos pos, int x, int y, int z) {
		BlockPos checkPos = pos.offset(x, y, z);
		AABB checkBox = new AABB(checkPos);
//		List<EntityTriggeredSludgeWallJet> entityList = level.getEntitiesOfClass(EntityTriggeredSludgeWallJet.class, checkBox);
//		for (EntityTriggeredSludgeWallJet entity : entityList) {
//			if (entity instanceof EntityTriggeredSludgeWallJet) {
//				break;
//			}
//		}
//		if (entityList.isEmpty()) {
//			EntityTriggeredSludgeWallJet jet = new EntityTriggeredSludgeWallJet(level);
//			jet.setPos(checkPos.getX() + 0.5D, checkPos.getY(), checkPos.getZ() + 0.5D);
//			level.addFreshEntity(jet);
//		}
	}

	private void spawnAmbientParticles(Level level, BlockPos pos) {

		double x = pos.getX() + 0.5D + (level.getRandom().nextFloat() - 0.5F) / 2.0F;
		double y = pos.getY() + 1.5D;
		double z = pos.getZ() + 0.5D + (level.getRandom().nextFloat() - 0.5F) / 2.0F;
		double mx = (level.getRandom().nextFloat() - 0.5F) * 0.08F;
		double my = level.getRandom().nextFloat() * 0.175F;
		double mz = (level.getRandom().nextFloat() - 0.5F) * 0.08F;
		int[] color = {100, 70, 0, 255};

//		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(level, x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
//				.withScale(8f));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
//
//		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(level, x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
//				.withScale(4f));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}

	private void updateBlock(Level level, BlockPos pos, BlockState state) {
		level.sendBlockUpdated(pos, state, state, 3);
	}

	private void checkSurfaceCollisions(Level level, BlockPos pos) {
		boolean reverse = false;
//		for (Entity entity : getEntityAbove(level, pos)) {
//			if (entity != null && !(entity instanceof EntitySludgeJet) && !(entity instanceof EntityRootGrabber) && !(entity instanceof BLEntity) && !(entity instanceof EntityShockwaveBlock)) {
//				if (entity instanceof AbstractArrow)
//					entity.kill();
//				if (this.getDistance(pos, entity) >= 4.25F - entity.getBbWidth() * 0.5F && this.getDistance(pos, entity) <= 7F + entity.getBbWidth() * 0.5F) {
//					reverse = false;
//					if (entity.getY() <= pos.getY() + 3D) {
//						entity.setDeltaMovement(0.0D, 0.1D, 0.0D);
//					} else if (entity.getDeltaMovement().y() < 0) {
//						entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
//						this.checkJumpOnTopOfAABB(entity);
//					}
//				}
//
//				if (this.getDistance(pos, entity) < 4.25F - entity.getBbWidth() * 0.5F && this.getDistance(pos, entity) >= 2.5F + entity.getBbWidth() * 0.5F) {
//					if (entity.getY() <= pos.getY() + 2D + 0.0625D) {
//						reverse = true;
//						this.checkJumpOnTopOfAABB(entity);
//					}
//				}
//
//				if (this.getDistance(pos, entity) >= 2.5F + entity.getBbWidth() * 0.5F) {
//					Vec3 center = new Vec3(pos.getX() + 0.5D, 0, pos.getZ() + 0.5D);
//					Vec3 entityOffset = new Vec3(entity.getX(), 0, entity.getZ());
//
//					double dist = entityOffset.distanceTo(center);
//					double circumference = 2 * Math.PI * dist;
//					double speed = circumference / 360 * (reverse ? 1F : 0.75F) /* angle per tick */;
//
//					Vec3 push = new Vec3(0, 1, 0).cross(entityOffset.subtract(center).normalize()).normalize().scale(reverse ? -speed : speed);
//
//					if (!entity.level().isClientSide() || entity instanceof Player) {
//						entity.move(MoverType.SELF, push.multiply(1.0D, 0.0D, 1.0D));
//					}
//				}
//			}
//		}
	}

	public float getDistance(BlockPos pos, Entity entity) {
		float f = (float) (pos.getX() + 0.5D - entity.getX());
		float f1 = (float) (pos.getY() + 2D - entity.getY());
		float f2 = (float) (pos.getZ() + 0.5D - entity.getZ());
		return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	public void checkJumpOnTopOfAABB(Entity entity) {
		if (entity.level().isClientSide() && entity instanceof Player player) {
			boolean jump = Minecraft.getInstance().options.keyJump.isDown();
			if (jump)
				player.jumpFromGround();
		}
	}

	public List<Entity> getEntityAbove(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(Entity.class, this.getFloorEntityBoundingBox(pos), EntitySelector.ENTITY_STILL_ALIVE);
	}

	private AABB getFloorEntityBoundingBox(BlockPos pos) {
		return new AABB(pos).inflate(7D, 0.0625D, 7D).expandTowards(0D, 2D, 0D);
	}

	private AABB getSpawningBoundingBox(BlockPos pos) {
		return new AABB(pos).inflate(12D, 6D, 12D).expandTowards(0D, 6D, 0D);
	}

	private void spawnSludgeJet(Level level, double posX, double posY, double posZ) {
//		EntitySludgeJet jet = new EntitySludgeJet(level);
//		jet.setPosition(posX, posY, posZ);
//		level.spawnEntity(jet);
//		level.playSound(null, jet.blockPosition(), SoundRegistry.POOP_JET.get(), SoundSource.HOSTILE, 1F, 0.8F + level.getRandom().nextFloat() * 0.5F);
	}

	public void setSpawnType(int spawn_type) {
		this.spawnType = spawn_type;
		this.setChanged();
	}

	public int getSpawnType() {
		return this.spawnType;
	}

	protected Entity getEntitySpawned(Level level, BlockPos pos, int spawnType) {
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, this.getSpawningBoundingBox(pos));
		if (list.stream().filter(e -> e instanceof Enemy).count() >= 5 && list.stream().filter(e -> e instanceof BLEntity).count() >= 5)
			return null;
		Entity spawned_entity = null;
		RandomSource random = level.getRandom();
		return null;
//		return switch (spawnType) {
//			case 0 -> random.nextBoolean() ? new EntityTinySludgeWorm(level) : random.nextBoolean() ? new EntitySmollSludge(level) : random.nextBoolean() ? new EntityTermite(level) : new EntityLargeSludgeWorm(level);
//			case 1 -> random.nextBoolean() ? new EntitySludgeWorm(level) : random.nextBoolean() ? new EntityChiromaw(level) : new EntityLargeSludgeWorm(level);
//			case 2 -> random.nextBoolean() ? new EntitySwampHag(level) : random.nextBoolean() ? new EntitySludge(level) : new EntityLargeSludgeWorm(level);
//			case 3 -> random.nextBoolean() ? new EntityShambler(level) : random.nextBoolean() ? new EntityChiromaw(level) : new EntityLargeSludgeWorm(level);
//			case 4 -> new EntityLargeSludgeWorm(level);
//			default -> spawned_entity;
//		};
	}

	public void setPlugged(boolean plugged) {
		isPlugged = plugged;
		this.setChanged();
	}

	public boolean isPlugged() {
		return isPlugged;
	}

	public boolean isUnPlugged() {
		return !isPlugged;
	}

	public void setShowFloor(boolean show_floor) {
		showFloor = show_floor;
		this.setChanged();
	}

	public boolean getShowFloor() {
		return showFloor;
	}

	private void setSpawnXPAndDrops(boolean spawn_drops) {
		spawnDrops = spawn_drops;
		this.setChanged();
	}

	private boolean getSpawnXPAndDrops() {
		return spawnDrops;
	}

	private void setDeathTicks(int death_ticks) {
		deathTicks = death_ticks;
		this.setChanged();
	}

	private int getDeathTicks() {
		return deathTicks;
	}

	public void shake(int shakeTimerMax) {
		this.shakingTimerMax = shakeTimerMax;
		this.prevShakeTimer = this.shakeTimer;
		if (this.shakeTimer == 0) {
			this.shakeTimer = 1;
		}
		if (this.shakeTimer > 0)
			this.shakeTimer++;

		this.shaking = this.shakeTimer < this.shakingTimerMax;
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (this.isShaking()) {
			double dist = getShakeDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 10.0F);
			if (dist >= 10.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getShakingProgress() * Mth.PI) + 0.1F) * 2F * shakeMult);
		} else {
			return 0.0F;
		}
	}

	public float getShakeDistance(Entity entity) {
		float distX = (float) (this.getBlockPos().getX() - entity.blockPosition().getX());
		float distY = (float) (this.getBlockPos().getY() - entity.blockPosition().getY());
		float distZ = (float) (this.getBlockPos().getZ() - entity.blockPosition().getZ());
		return Mth.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public boolean isShaking() {
		return shaking;
	}

	public float getShakingProgress() {
		return 1.0F / shakingTimerMax * (prevShakeTimer + (shakeTimer - prevShakeTimer));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putFloat("animation_ticks", this.animationTicks);
		tag.putInt("spawn_type", this.getSpawnType());
		tag.putFloat("plug_drop_ticks", this.plugDropTicks);
		tag.putBoolean("plugged", this.isPlugged());
		tag.putBoolean("show_floor", this.getShowFloor());
		tag.putBoolean("spawn_drops", this.getSpawnXPAndDrops());
		tag.putInt("death_ticks", this.getDeathTicks());
		tag.putInt("tentacle_countdown", this.getTentacleSpawnCountDown());
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicks = tag.getFloat("animation_ticks");
		this.setSpawnType(tag.getInt("spawn_type"));
		this.plugDropTicks = tag.getFloat("plug_drop_ticks");
		this.setPlugged(tag.getBoolean("plugged"));
		this.setShowFloor(tag.getBoolean("show_floor"));
		this.setSpawnXPAndDrops(tag.getBoolean("spawn_drops"));
		this.setDeathTicks(tag.getInt("death_ticks"));
		this.setTentacleSpawnCountDown(tag.getInt("tentacle_countdown"));
	}
}
