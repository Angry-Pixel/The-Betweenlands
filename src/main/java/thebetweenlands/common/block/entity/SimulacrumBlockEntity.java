package thebetweenlands.common.block.entity;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.component.entity.LastKilledData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class SimulacrumBlockEntity extends RepellerBlockEntity implements Spawner {

	private Effect effect = Effect.NONE;
	private Effect secondaryEffect = Effect.NONE;

	private boolean isActive = false;
	@Nullable
	private Component name;
	private int soundCooldown = 0;
	@Nullable
	private RepellerBlockEntity sourceRepeller;
	private boolean readFromNbt = false;

	private final BetweenlandsBaseSpawner mireSnailSpawner = new BetweenlandsBaseSpawner() {
		{
			this.setDelayRange(600, 1200);
			this.setSpawnInAir(false);
			this.setParticles(false);
			this.setCheckRange(24);
			this.setMaxSpawnCount(1);
		}

		@Override
		public void broadcastEvent(Level level, BlockPos pos, int eventId) {
		}

		@Override
		public Either<BlockEntity, Entity> getOwner() {
			return Either.left(SimulacrumBlockEntity.this);
		}

		@Override
		public void setChanged(@Nullable Level level, BlockPos pos) {
			if (level != null) {
				BlockState blockState = level.getBlockState(pos);
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	};

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final Method Mob_getAmbientSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getAmbientSound");
	private static final MethodHandle handle_Mob_getAmbientSound;
	private static final Method Mob_getHurtSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getHurtSound");
	private static final MethodHandle handle_Mob_getHurtSound;
	private static final Method Mob_getDeathSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getDeathSound");
	private static final MethodHandle handle_Mob_getDeathSound;

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		MethodHandle tmp_handle_Mob_getHurtSound = null;
		MethodHandle tmp_handle_Mob_getDeathSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = LOOKUP.unreflect(Mob_getAmbientSound);
			tmp_handle_Mob_getHurtSound = LOOKUP.unreflect(Mob_getHurtSound);
			tmp_handle_Mob_getDeathSound = LOOKUP.unreflect(Mob_getDeathSound);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		handle_Mob_getAmbientSound = tmp_handle_Mob_getAmbientSound;
		handle_Mob_getHurtSound = tmp_handle_Mob_getHurtSound;
		handle_Mob_getDeathSound = tmp_handle_Mob_getDeathSound;
	}

	public SimulacrumBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);

		if (!this.readFromNbt) {
			this.mireSnailSpawner.delay(level, this.getBlockPos());
		}
	}

	@Override
	public BlockEntityType<?> getType() {
		return BlockEntityRegistry.SIMULACRUM.get();
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (entity.isActive()) {
			entity.updateEffects(level, pos, state, entity.effect);
			entity.updateEffects(level, pos, state, entity.secondaryEffect);
		}
	}

	@Nullable
	public Component getCustomName() {
		return this.name;
	}

	public void setCustomName(@Nullable Component name) {
		this.name = name;
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
		this.isActive = active;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public Effect getEffect() {
		return this.effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
		this.setSecondaryEffect(Effect.NONE);
		this.setChanged();
	}

	public Effect getSecondaryEffect() {
		return this.secondaryEffect;
	}

	public void setSecondaryEffect(Effect effect) {
		this.secondaryEffect = effect;
		this.setChanged();
	}

	private void updateEffects(Level level, BlockPos pos, BlockState state, Effect effect) {
		switch (effect) {
			case RANDOM -> {
				if (!level.isClientSide() && level.getGameTime() % 20 == 0 && level.getRandom().nextInt(200) == 0) {
					this.setSecondaryEffect(Effect.values()[level.getRandom().nextInt(Effect.values().length)]);
				}
			}
			case THEM -> {
				if (level.isClientSide() && level.getGameTime() % 20 == 0 && level.getRandom().nextInt(5) == 0) {
					this.spawnThem(level, pos);
				}
			}
			case IMITATION -> {
				if (level.isClientSide() && level.getGameTime() % 20 == 0 && --this.soundCooldown <= 0) {
					this.soundCooldown = level.getRandom().nextInt(30) + 30;
					this.playImitationSound(level, pos);
				}
			}
			case SANCTUARY -> {
				this.setRadiusState(3);

				RepellerBlockEntity repeller = getClosestActiveTile(RepellerBlockEntity.class, this, level, pos.getX(), pos.getY(), pos.getZ(), 18.0D, null, null);

				if (repeller != null && repeller.getBlockPos().distToCenterSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > repeller.getRadius(1) * repeller.getRadius(1)) {
					repeller = null;
				}

				if (repeller instanceof SimulacrumBlockEntity simulacrum) {
					this.sourceRepeller = repeller = simulacrum.sourceRepeller;
				} else {
					this.sourceRepeller = repeller;
				}

				if (this.sourceRepeller != null && (this.sourceRepeller.isRemoved() || !level.isLoaded(this.sourceRepeller.getBlockPos()))) {
					this.sourceRepeller = null;

					if (repeller == this.sourceRepeller) {
						repeller = null;
					}
				}

				int prevFuel = 0;

				if (repeller != null) {
					this.running = repeller.running;
					this.hasShimmerstone = repeller.hasShimmerstone;
					prevFuel = this.fuel = repeller.fuel;
				} else {
					this.running = false;
					this.hasShimmerstone = false;
					this.fuel = 0;
				}

				RepellerBlockEntity.tick(level, pos, state, this);

				if (repeller != null) {
					if (this.fuel < prevFuel) {
						repeller.fuel -= (prevFuel - this.fuel);
						repeller.setChanged();
						this.setChanged();
					}

					this.running = repeller.running;
					this.hasShimmerstone = repeller.hasShimmerstone;
					this.fuel = repeller.fuel;
				}
			}
			case FERTILITY -> {
				if (!level.isClientSide() && BetweenlandsWorldStorage.forWorld(level).getEnvironmentEventRegistry().heavyRain.isActive()) {
					this.mireSnailSpawner.serverTick((ServerLevel) level, pos);
				}
			}
			case WISP -> {
				if (!level.isClientSide() && level.isEmptyBlock(pos.above()) && level.getGameTime() % 200 == 0 && BetweenlandsWorldStorage.forWorld(level).getEnvironmentEventRegistry().auroras.isActive()) {
					level.setBlockAndUpdate(pos.above(), BlockRegistry.WISP.get().defaultBlockState());
				}
			}
			case WISDOM -> {
				//TODO
//				if (!level.isClientSide() && level.getGameTime() % 160 == 0 && level.getEntitiesOfClass(FalseXPOrb.class, new AABB(pos).inflate(16.0D)).isEmpty()) {
//					Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8.0D, false);
//
//					if (player != null) {
//						int xp = level.getRandom().nextInt((int) Math.min(Math.abs(level.getRandom().nextGaussian() * Math.min(player.totalExperience, 1200)), 2400) + 1);
//
//						if (xp < player.totalExperience) {
//							UUID playerUuid = player.getUUID();
//
//							int negativeXp = xp;
//
//							float multiplier = 1.0f + Mth.clamp((float) level.getRandom().nextGaussian(), -0.5f, 0.5f) - 0.025f;
//
//							xp = (int) (xp * multiplier);
//
//							List<Entity> orbs = new ArrayList<>();
//
//							for (int i = 0; i < 32; i++) {
//								int negativeOrbXp = 0;
//								if (negativeXp > 0) {
//									if (i != 31) {
//										negativeOrbXp = level.getRandom().nextInt(negativeXp / 8 + 1) + 1;
//										negativeXp -= negativeOrbXp;
//									} else {
//										negativeOrbXp = negativeXp;
//									}
//								}
//
//								int orbXp = 0;
//								if (xp > 0) {
//									if (i != 31) {
//										orbXp = level.getRandom().nextInt(xp / 8 + 1) + 1;
//										xp -= orbXp;
//									} else {
//										orbXp = xp;
//									}
//								}
//
//								Entity negativeOrb = null;
//								Entity orb = null;
//
//								if (level.getRandom().nextBoolean()) {
//									if (negativeOrbXp > 0) negativeOrb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
//									if (orbXp > 0) orb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, orbXp, playerUuid);
//								} else {
//									orb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, orbXp, playerUuid);
//									if (orbXp > 0) if (negativeOrbXp > 0) negativeOrb = new FalseXPOrb(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, -negativeOrbXp, playerUuid);
//								}
//
//								if (orb != null) {
//									orbs.add(orb);
//								}
//
//								if (negativeOrb != null) {
//									orbs.add(negativeOrb);
//								}
//							}
//
//							Collections.shuffle(orbs);
//
//							float stepH = Mth.PI * 2 / (float) orbs.size() * 6;
//							float stepV = Mth.PI * 2 / (float) orbs.size() / 4;
//
//							for (int i = 0; i < orbs.size(); i++) {
//								double hc = Math.cos(stepH * i);
//								double hs = Math.sin(stepH * i);
//
//								double vc = Math.cos(stepV * i);
//								double vs = Math.sin(stepV * i);
//
//								double dx = hs * vc;
//								double dz = hc * vc;
//
//								Entity orb = orbs.get(i);
//
//								orb.setDeltaMovement(dx * 0.25f, 0.1f + vs * 0.35f, dz * 0.25f);
//
//								orb.moveTo(orb.getX() + dx * 0.65f, orb.getY() + vs * 2f, orb.getZ() + dz * 0.65f, 0, 0);
//
//								level.addFreshEntity(orb);
//							}
//						}
//					}
//				}
			}
			case BLESSING -> {
				if (level.getGameTime() % 4 == 0) {
					Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5D, pos.getZ() + 0.5D, 4, e -> {
						if (!e.isSpectator() && e.hasData(AttachmentRegistry.BLESSING)) {
							BlessingData cap = e.getData(AttachmentRegistry.BLESSING);
							return (!cap.isBlessed() || cap.getBlessingDimension() != e.level().dimension() || !pos.equals(cap.getBlessingLocation()));
						}
						return false;
					});

					if (player != null) {
						OfferingTableBlockEntity offering = getClosestActiveTile(OfferingTableBlockEntity.class, null, level, player.getX(), player.getY(), player.getZ(), 2.5f, null, stack -> !stack.isEmpty() && stack.is(ItemRegistry.SPIRIT_FRUIT));

						if (offering != null) {
							if (!level.isClientSide() && level.getRandom().nextInt(40) == 0) {
								BlessingData cap = player.getData(AttachmentRegistry.BLESSING);
								ItemStack stack = offering.getTheItem();
								stack.shrink(1);
								offering.setTheItem(stack);
								cap.setBlessed(player.level().dimension(), pos);
								player.displayClientMessage(Component.translatable("chat.simulacrum.blessed"), true);
							} else if (level.isClientSide()) {
								this.spawnBlessingParticles(level.getGameTime() * 0.025f, offering.getBlockPos().getX() + 0.5f, offering.getBlockPos().getY() + 0.4f, offering.getBlockPos().getZ() + 0.5f);
							}
						}
					}
				}
			}
		}
	}

	private void spawnBlessingParticles(float rot, float x, float y, float z) {
		float step = Mth.PI * 2 / 20;
		for (int i = 0; i < 20; i++) {
			float dx = (float) Math.cos(rot + step * i);
			float dz = (float) Math.sin(rot + step * i);

//			BLParticles.CORRUPTED.spawn(this.getLevel(), x, y, z, ParticleArgs.get().withMotion(dx * 0.05f, 0.2f, dz * 0.05f).withData(80, true, 0.1f, true));
		}
	}

	private void spawnThem(Level level, BlockPos pos) {
		Entity viewer = Minecraft.getInstance().getCameraEntity();

		if (viewer != null && pos.distToCenterSqr(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16 && FogHandler.hasDenseFog(level) && (FogHandler.getCurrentFogEnd() + FogHandler.getCurrentFogStart()) / 2 < 65.0F) {
			if (SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below())) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below(2))) || SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(level.getBlockState(viewer.blockPosition().below(3)))) {
				double xOff = level.getRandom().nextInt(50) - 25;
				double zOff = level.getRandom().nextInt(50) - 25;
				double sx = viewer.getX() + xOff;
				double sz = viewer.getZ() + zOff;
				double sy = viewer.blockPosition().getY() + level.getRandom().nextFloat() * 0.75f;
//				BLParticles.THEM.spawn(level, sx, sy, sz);
			}
		}
	}

	private void playImitationSound(Level level, BlockPos pos) {
		Entity viewer = Minecraft.getInstance().getCameraEntity();

		if (viewer != null && pos.distToCenterSqr(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16) {
			LastKilledData cap = viewer.getData(AttachmentRegistry.LAST_KILLED);

			if (cap != null) {
				ResourceLocation key = cap.getLastKilled();

				if (key != null) {
					Entity entity = BuiltInRegistries.ENTITY_TYPE.get(key).create(level);

					if (entity != null) {
						SoundEvent sound;

						int r = viewer.level().getRandom().nextInt(20);

						try {
							if (r <= 15) {
								sound = (SoundEvent) Mob_getAmbientSound.invoke(entity);
							} else if (r <= 19) {
								sound = (SoundEvent) Mob_getHurtSound.invoke(entity, level.damageSources().generic());
							} else {
								sound = (SoundEvent) Mob_getDeathSound.invoke(entity);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}

						if (sound != null) {
							level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.75f, 0.9f, false);
						}

						entity.kill();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BlockEntity> T getClosestActiveTile(Class<T> tileCls, @Nullable BlockEntity exclude, Level level, double x, double y, double z, double range, @Nullable Effect effect, @Nullable Predicate<ItemStack> offeringPredicate) {
		int sx = (Mth.floor(x - range) >> 4);
		int sz = (Mth.floor(z - range) >> 4);
		int ex = (Mth.floor(x + range) >> 4);
		int ez = (Mth.floor(z + range) >> 4);

		T closest = null;

		for (int cx = sx; cx <= ex; cx++) {
			for (int cz = sz; cz <= ez; cz++) {
				ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);

				if (chunk != null) {
					for (BlockPos pos : chunk.getBlockEntitiesPos()) {
						BlockEntity tile = level.getBlockEntity(pos);

						if (tile != exclude && tileCls.isInstance(tile)) {
							double dstSq = pos.distToCenterSqr(x, y, z);

							if (dstSq <= range * range && (closest == null || dstSq <= closest.getBlockPos().distToCenterSqr(x, y, z)) &&
								(effect == null || !(tile instanceof SimulacrumBlockEntity simulacrum) || simulacrum.getEffect() == effect && simulacrum.isActive()) &&
								(offeringPredicate == null || !(tile instanceof OfferingTableBlockEntity table) || offeringPredicate.test(table.getTheItem()))) {
								closest = (T) tile;
							}
						}
					}
				}
			}
		}

		return closest;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("effect_id", this.effect.id);
		tag.putInt("secondary_effect_id", this.secondaryEffect.id);
		tag.putBoolean("is_active", this.isActive);
		if (this.name != null) {
			tag.putString("name", Component.Serializer.toJson(this.name, registries));
		}

		this.mireSnailSpawner.save(tag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.readFromNbt = true;

		this.effect = Effect.byId(tag.getInt("effect_id"));
		this.secondaryEffect = Effect.byId(tag.getInt("secondary_effect_id"));
		this.isActive = tag.getBoolean("is_active");
		if (tag.contains("name", Tag.TAG_STRING)) {
			this.name = parseCustomNameSafe(tag.getString("name"), registries);
		}

		this.mireSnailSpawner.load(this.getLevel(), this.getBlockPos(), tag);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	@Override
	public void setEntityId(EntityType<?> entityType, RandomSource random) {
		this.mireSnailSpawner.setEntityId(EntityRegistry.MIRE_SNAIL.get(), this.getLevel(), random, this.getBlockPos());
	}

	public enum Effect {
		NONE("none", 0),
		RANDOM("random", 1),
		THEM("them", 2),
		IMITATION("imitation", 3),
		WEAKNESS("weakness", 4),
		RESURRECTION("resurrection", 5),
		SANCTUARY("sanctuary", 6),
		FERTILITY("fertility", 7),
		ATTRACTION("attraction", 8),
		WISP("wisp", 9),
		WISDOM("wisdom", 10),
		BLESSING("blessing", 11);

		public final String name;
		public final int id;

		Effect(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public static Effect byId(int id) {
			for (Effect effect : Effect.values()) {
				if (effect.id == id) {
					return effect;
				}
			}
			return Effect.NONE;
		}
	}
}
