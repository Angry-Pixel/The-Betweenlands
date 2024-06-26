package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.BlockEvent;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.blocks.WispBlock;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class LocationCragrockTower extends LocationGuarded {
	private List<BlockPos> glowingCragrockBlocks = new ArrayList<>();
	private List<BlockPos> wisps = new ArrayList<>();
	private List<BlockPos> inactiveWisps = new ArrayList<>();
	private BlockPos[][] levelBlockadeBlocks = new BlockPos[5][0];
	private boolean[] spawners = { true, true, true, true, true };
	private boolean[] blockades = { true, true, true, true, true };
	private BlockPos structurePos;
	private boolean isTopConquered = false;
	private boolean isTopReached = false;
	private boolean wasEntered = false;
	private int wispUpdateTicks = 0;
	private int topSpawners = 2;

	private final int[][] levelYBounds = { {-5, 9}, {10, 18}, {19, 27}, {28, 36}, {37, 45}, {46, 56} };

	protected static final EntityDataAccessor<Boolean> CRUMBLING = GenericDataAccessor.defineId(LocationCragrockTower.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Integer> CRUMBLING_TICKS = GenericDataAccessor.defineId(LocationCragrockTower.class, EntityDataSerializers.INT);

	public LocationCragrockTower(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region, "cragrock_tower", EnumLocationType.CRAGROCK_TOWER);
		this.dataManager.register(CRUMBLING, false);
		this.dataManager.register(CRUMBLING_TICKS, 20, 0);
	}

	/**
	 * Sets the structure entrance
	 * @param pos
	 */
	public void setStructurePos(BlockPos pos) {
		this.structurePos = pos;
		this.setDirty(true);
	}

	/**
	 * Returns the structure entrance
	 * @return
	 */
	public BlockPos getStructurePos() {
		return this.structurePos;
	}

	/**
	 * Returns the inner AABB
	 * @return
	 */
	public AABB getInnerBoundingBox() {
		return this.getBounds().size() > 1 ? this.getBounds().get(1) : new AABB(0, 0, 0, 0, 0, 0);
	}

	/**
	 * Returns whether the top was reached and all mobs and spawners are destroyed
	 * @return
	 */
	public boolean isTopConquered() {
		return this.isTopConquered;
	}

	/**
	 * Sets whether the top was reached and changes all glowing cragrock blocks accordingly
	 * @param reached
	 */
	public void setTopConquered(boolean reached) {
		this.isTopConquered = reached;

		Level level = this.getWorldStorage().getLevel();
		for(BlockPos pos : this.glowingCragrockBlocks) {
			if(reached) {
				level.setBlockAndUpdate(pos, BlockRegistry.GLOWING_SMOOTH_CRAGROCK.get().defaultBlockState());
			} else {
				level.setBlockAndUpdate(pos, BlockRegistry.INACTIVE_GLOWING_SMOOTH_CRAGROCK.get().defaultBlockState());
			}
		}

		this.setDirty(true);
	}

	/**
	 * Adds an inactive glowing cragrock block that is activated once the top is reached
	 * @param pos
	 */
	public void addGlowingCragrock(BlockPos pos) {
		this.glowingCragrockBlocks.add(pos);
		this.setDirty(true);
	}

	/**
	 * Adds an inactive wisp that is activated once a player gets close to the tower
	 * @param pos
	 */
	public void addInactiveWisp(BlockPos pos) {
		this.wisps.add(pos);
		this.inactiveWisps.add(pos);
		this.setDirty(true);
	}

	/**
	 * Sets whether the tower was entered. Wisps are changed accordingly in the next tick
	 * @param entered
	 * @return
	 */
	public void setEntered(boolean entered) {
		this.wasEntered = entered;
		this.setDirty(true);
	}

	/**
	 * Returns whether the tower was entered
	 * @return
	 */
	public boolean wasEntered() {
		return this.wasEntered;
	}

	/**
	 * Sets the level blockade blocks
	 * @param level
	 * @param blocks
	 */
	public void setLevelBlockadeBlocks(int level, BlockPos[] blocks) {
		this.levelBlockadeBlocks[level] = blocks;
		this.setDirty(true);
	}

	/**
	 * Returns the level blockades
	 * @param level
	 * @return
	 */
	public BlockPos[] getLevelBlockadeBlocks(int level) {
		return this.levelBlockadeBlocks[level];
	}

	/**
	 * Destroys the blockade blocks for the specified level
	 * @param level
	 */
	public void destroyBlockade(int level) {
		BlockPos[] blocks = this.levelBlockadeBlocks[level];
		if(blocks != null && blocks.length != 0) {
			Level world = this.getWorldStorage().getLevel();

			for(BlockPos pos : blocks) {
				world.playSound(null, pos, SoundRegistry.CRUMBLE.get(), SoundSource.BLOCKS, 0.2F, 1F);
				world.levelEvent(null, 2001, pos.below(), Block.getId(world.getBlockState(pos)));

				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				this.getGuard().setGuarded(world, pos, false);
			}
		}

		this.blockades[level] = false;

		this.setDirty(true);
	}

	/**
	 * Restores the blockade blocks for the specified level
	 * @param level
	 */
	public void restoreBlockade(int level) {
		BlockPos[] blocks = this.levelBlockadeBlocks[level];
		if(blocks != null && blocks.length != 0) {
			Level world = this.getWorldStorage().getLevel();

			for(BlockPos pos : blocks) {
				world.setBlockAndUpdate(pos, BlockRegistry.SMOOTH_CRAGROCK_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
				this.getGuard().setGuarded(world, pos, true);
			}
		}

		this.blockades[level] = true;

		this.setDirty(true);
	}

	/**
	 * Returns whether the spawner is still active
	 * @param level
	 * @return
	 */
	public boolean getSpawnerState(int level) {
		return this.spawners[level];
	}

	/**
	 * Sets whether the spawner is still active
	 * @param level
	 * @param active
	 * @return
	 */
	public void setSpawnerState(int level, boolean active) {
		this.spawners[level] = active;
		this.setDirty(true);
	}

	/**
	 * Returns whether the blockade is still active
	 * @param level
	 * @return
	 */
	public boolean getBlockadeState(int level) {
		return this.blockades[level];
	}

	/**
	 * Returns the bounding box for the specified level
	 * @param level
	 * @return
	 */
	public AABB getLevelBounds(int level) {
		double minY = this.levelYBounds[level][0] + this.structurePos.getY();
		double maxY = this.levelYBounds[level][1] + this.structurePos.getY();
		AABB inside = this.getInnerBoundingBox();
		if(level == 0) {
			inside = inside.inflate(1, 1, 1); //Basement is wider
		}
		return new AABB(inside.minX, minY, inside.minZ, inside.maxX, maxY, inside.maxZ);
	}

	/**
	 * Returns the level for the specified Y position
	 * @param y
	 * @return
	 */
	public int getLevel(int y) {
		y -= this.structurePos.getY(); //Relative position to structure
		for(int i = 0; i < this.levelYBounds.length; i++) {
			if(y >= this.levelYBounds[i][0] && y <= this.levelYBounds[i][1]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns whether the tower is crumbling
	 * @return
	 */
	public boolean isCrumbling() {
		return this.dataManager.get(CRUMBLING);
	}

	/**
	 * Sets whether the tower is crumbling
	 * @param crumbling
	 */
	public void setCrumbling(boolean crumbling) {
		this.dataManager.set(CRUMBLING, crumbling);
		this.setDirty(true);
	}

	/**
	 * Returns the crumbling ticks
	 * @return
	 */
	public int getCrumblingTicks() {
		return this.dataManager.get(CRUMBLING_TICKS);
	}

	/**
	 * Sets the crumbling ticks
	 * @param ticks
	 */
	public void setCrumblingTicks(int ticks) {
		this.dataManager.set(CRUMBLING_TICKS, ticks);
		this.setDirty(true);
	}

	/**
	 * Updates the wisp blocks
	 * @param player Closest non-creative player
	 */
	public void updateWispBlocks(@Nullable Player player) {
		Level level = this.getWorldStorage().getLevel();

		if(this.wasEntered() && !this.inactiveWisps.isEmpty()) {
			if(player != null) {
				this.wispUpdateTicks++;

				if(this.wispUpdateTicks >= 10) {
					//Pick the closest wisp to be placed, set block and play sound

					this.wispUpdateTicks = 0;

					for(int i = 0; i < 4; i++) {
						if(!this.inactiveWisps.isEmpty()) {
							BlockPos src = player != null ? player.blockPosition() : this.structurePos;

							BlockPos closest = this.inactiveWisps.get(0);
							for(BlockPos pos : this.inactiveWisps) {
								if(pos.distSqr(src) < closest.distSqr(src)) {
									closest = pos;
								}
							}

							boolean canLightUp = false;

							if((closest.getY() - this.structurePos.getY() < 16 && player.getY() - this.structurePos.getY() < 45) ||
									(closest.getY() - this.structurePos.getY() >= 16 && player.getY() - this.structurePos.getY() >= 45)) {
								canLightUp = true;
							}

							if(canLightUp) {
								level.setBlockAndUpdate(closest, BlockRegistry.WISP.get().defaultBlockState().setValue(WispBlock.COLOR, level.getRandom().nextInt(4)));
								level.playSound(null, closest.getX(), closest.getY(), closest.getZ(), SoundRegistry.IGNITE.get(), SoundSource.AMBIENT, 1.6F + level.getRandom().nextFloat() * 0.45F, 1.0F + level.getRandom().nextFloat() * 0.4F);

								this.inactiveWisps.remove(closest);

								this.setDirty(true);
							}
						} else {
							break;
						}
					}
				}
			}
		} else if(!this.wasEntered() && this.inactiveWisps.size() != this.wisps.size()) {
			//Remove all wisps

			this.inactiveWisps.clear();
			this.inactiveWisps.addAll(this.wisps);

			for(BlockPos pos : this.wisps) {
				level.removeBlock(pos, false);
			}

			this.setDirty(true);
		}
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.structurePos = BlockPos.of(nbt.getLong("structurePos"));
		this.isTopConquered = nbt.getBoolean("isTopConquered");
		this.isTopReached = nbt.getBoolean("isTopReached");
		this.wasEntered = nbt.getBoolean("wasEntered");
		this.readBlockList(nbt, "glowingBlocks", this.glowingCragrockBlocks);
		this.readBlockList(nbt, "wisps", this.wisps);
		this.readBlockList(nbt, "inactiveWisps", this.inactiveWisps);
		for(int i = 0; i < 5; i++) {
			CompoundTag blockadeNbt = nbt.getCompound("blockade." + i);
			List<BlockPos> blocks = new ArrayList<>();
			this.readBlockList(blockadeNbt, "blocks", blocks);
			if(!blocks.isEmpty()) {
				this.levelBlockadeBlocks[i] = blocks.toArray(new BlockPos[0]);
			} else {
				this.levelBlockadeBlocks[i] = new BlockPos[0];
			}
			this.blockades[i] = blockadeNbt.getBoolean("broken");
		}
		for(int i = 0; i < 5; i++) {
			this.spawners[i] = nbt.getBoolean("spawner." + i);
		}
		this.setCrumbling(nbt.getBoolean("crumbling"));
		this.setCrumblingTicks(nbt.getInt("crumblingTicks"));
		this.topSpawners = nbt.getInt("topSpawners");
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag) {
		super.writeToNBT(tag);
		tag.putLong("structurePos", this.structurePos.asLong());
		tag.putBoolean("isTopConquered", this.isTopConquered);
		tag.putBoolean("isTopReached", this.isTopReached);
		tag.putBoolean("wasEntered", this.wasEntered);
		this.saveBlockList(tag, "glowingBlocks", this.glowingCragrockBlocks);
		this.saveBlockList(tag, "wisps", this.wisps);
		this.saveBlockList(tag, "inactiveWisps", this.inactiveWisps);
		for(int i = 0; i < 5; i++) {
			CompoundTag blockadeNbt = new CompoundTag();
			BlockPos[] blocks = this.levelBlockadeBlocks[i];
			this.saveBlockList(blockadeNbt, "blocks", blocks != null && blocks.length != 0 ? Arrays.asList(blocks) : new ArrayList<>());
			blockadeNbt.putBoolean("broken", this.blockades[i]);
			tag.put("blockade." + i, blockadeNbt);
		}
		for(int i = 0; i < 5; i++) {
			tag.putBoolean("spawner." + i, this.spawners[i]);
		}
		tag.putBoolean("crumbling", this.isCrumbling());
		tag.putInt("crumblingTicks", this.getCrumblingTicks());
		tag.putInt("topSpawners", this.topSpawners);
		return tag;
	}

	protected void saveBlockList(CompoundTag tag, String name, List<BlockPos> blocks) {
		ListTag blockList = new ListTag();
		for(BlockPos pos : blocks) {
			blockList.add(LongTag.valueOf(pos.asLong()));
		}
		tag.put(name, blockList);
	}

	protected void readBlockList(CompoundTag nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		ListTag blockList = nbt.getList(name, Tag.TAG_LONG);
		for(int i = 0; i < blockList.size(); i++) {
			LongTag posNbt = (LongTag) blockList.get(i);
			blocks.add(BlockPos.of(posNbt.getAsLong()));
		}
	}

	@Override
	public void tick() {
		super.tick();

		Level level = this.getWorldStorage().getLevel();

		if(this.isCrumbling()) {
			this.dataManager.set(CRUMBLING_TICKS, this.getCrumblingTicks() + 1);

			if(!level.isClientSide()) {
				if(this.getCrumblingTicks() > 1200) {
					this.dataManager.set(CRUMBLING_TICKS, -1).syncImmediately();
					this.setCrumbling(false);
					this.destroyBlockade(4);
					this.getGuard().clear(level);
				} else {
					this.setDirty(true);
				}
			}
		}

		if(!level.isClientSide()) {
			List<Player> players = level.getEntitiesOfClass(Player.class, this.getEnclosingBounds(), player -> this.isInside(player) && !player.isCreative() && !player.isSpectator());

			if(!players.isEmpty()) {
				if(this.isTopConquered() && !this.isCrumbling() && this.getCrumblingTicks() == 0) {
					List<Player> topPlayers = level.getEntitiesOfClass(Player.class, this.getLevelBounds(5).inflate(5, 3, 5), player -> !player.isCreative() && !player.isSpectator());
					if (topPlayers.isEmpty()) {
						this.setCrumbling(true);
						this.restoreBlockade(4);
					}
				}

				for(Player player : players) {
					BlockPos structurePos = this.getStructurePos();

					if (!this.wasEntered()) {
						this.setEntered(true);
					}

					if (this.getLevelBounds(5).contains(player.position())) {
						this.isTopReached = true;
						this.setDirty(true);
					}

					//TODO
					if(this.isTopConquered() && player instanceof ServerPlayer && this.getLevel(Mth.floor(player.getY())) == 5) {
						//AdvancementCriterionRegistry.CRAGROCK_TOP.trigger((ServerPlayer) player);
					} else if (!this.isTopReached && !this.getInnerBoundingBox().inflate(0.5D, 0.5D, 0.5D).contains(player.position()) && player.getY() - structurePos.getY() > 12) {
						//Player trying to bypass tower, teleport to entrance

						player.stopRiding();
						if (player instanceof ServerPlayer) {
							ServerPlayer playerMP = (ServerPlayer) player;
							playerMP.connection.teleport(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.getYRot(), player.getXRot());
						} else {
							player.moveTo(structurePos.getX() + 0.5D, structurePos.getY(), structurePos.getZ() + 0.5D, player.getYRot(), player.getXRot());
						}
						player.fallDistance = 0.0F;
						player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 2));
						player.level().playSound(null, player.blockPosition(), SoundRegistry.FORTRESS_BOSS_TELEPORT.get(), SoundSource.AMBIENT, 1, 1);
					}
				}

				Player closest = players.get(0);
				for(Player player : players) {
					if(player.distanceToSqr(Vec3.atCenterOf(this.structurePos)) < closest.distanceToSqr(Vec3.atCenterOf(this.structurePos))) {
						closest = player;
					}
				}
				this.updateWispBlocks(closest);
			} else {
				this.updateWispBlocks(null);
			}

			if(!this.isTopConquered()) {
				for(int i = 0; i < 5; i++) {
					if(this.getBlockadeState(i) && !this.getSpawnerState(i)) {
						List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.getLevelBounds(i), entity -> entity instanceof Mob || entity instanceof Enemy);
						if(entities.isEmpty()) {
							this.destroyBlockade(i);
						}
					}
				}

				if(this.topSpawners == 0) {
					List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.getLevelBounds(5).inflate(3, 2, 3), entity -> entity instanceof Mob || entity instanceof Enemy);
					if(entities.isEmpty()) {
						this.setTopConquered(true);

						for(Player player : players) {
							player.giveExperiencePoints(350);
						}
					}
				}
			}

			if(this.isCrumbling()) {
				for(int i = 0; i < Math.min(Math.pow(this.getCrumblingTicks() / 400.0f, 4) * 30.0f, 30) + 1; i++) {
					BlockPos pos = this.getRandomPosInTower();
					BlockState blockState = level.getBlockState(pos);

					if(!blockState.isAir() && level.isEmptyBlock(pos.below()) && level.isEmptyBlock(pos.below(2))) {
						FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, BlockRegistry.WEAK_SMOOTH_CRAGROCK.get().defaultBlockState());
						fallingBlock.time = -60;
						fallingBlock.dropItem = false;
						fallingBlock.setHurtsEntities(2.0F, 40);
						level.addFreshEntity(fallingBlock);

						if(level.getRandom().nextInt(25) == 0) {
							level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						}

						level.playSound(null, pos, SoundRegistry.CRUMBLE.get(), SoundSource.BLOCKS, 0.2F, 0.5F + level.getRandom().nextFloat() * 0.4F);
					}
				}
			}
		}
	}

	/**
	 * Returns a random block pos inside the tower
	 * @return
	 */
	protected BlockPos getRandomPosInTower() {
		AABB innerBB = this.getInnerBoundingBox().inflate(-1, -1, -1);

		int x = Mth.ceil(innerBB.minX);
		int y = Mth.ceil(innerBB.minY);
		int z = Mth.ceil(innerBB.minZ);
		int width = Mth.floor(innerBB.maxX) - x;
		int height = Mth.floor(innerBB.maxY) - y;
		int depth = Mth.floor(innerBB.maxZ) - z;

		Level level = this.getWorldStorage().getLevel();

		return new BlockPos(x + level.getRandom().nextInt(width + 1), y + level.getRandom().nextInt(height + 1), z + level.getRandom().nextInt(depth + 1));
	}

	@Override
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		if(!event.getLevel().isClientSide()) {
			BlockPos pos = event.getPos();
			BlockState blockState = event.getState();

			if(blockState.is(BlockRegistry.MOB_SPAWNER.get())) {
				int level = this.getLevel(pos.getY());

				if(level != -1) {
					if(level == 5) {
						if(this.topSpawners > 0) {
							this.topSpawners--;
							this.setDirty(true);
						}
					} else {
						this.setSpawnerState(level, false);
					}
				}
			}
		}
	}
}
