package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.common.block.DungeonDoorRunesBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class DungeonDoorRunesBlockEntity extends BlockEntity implements ScreenShaker {

	//private LightTowerBuildParts lightTowerBuild = new LightTowerBuildParts(null);
	private boolean mimic; // true = trap
	private boolean barrishee; // true = Barrishee / false = Crypt Crawler Chief
	public int top_code = -1, mid_code = -1, bottom_code = -1; // set back to -1
	public int top_state = 0, mid_state = 0, bottom_state = 0;
	public int top_state_prev = 0, mid_state_prev = 0, bottom_state_prev = 0;
	public int top_rotate = 0, mid_rotate = 0, bottom_rotate = 0;
	public int lastTickTopRotate = 0, lastTickMidRotate = 0, lastTickBottomRotate = 0;
	public int renderTicks = 0;
	public boolean animate_open = false;
	public boolean animate_open_recess = false;
	public boolean animate_tile_recess = false;
	public boolean break_blocks = false;
	public int slate_1_rotate = 0, slate_2_rotate = 0, slate_3_rotate = 0;
	public int last_tick_slate_1_rotate = 0, last_tick_slate_2_rotate = 0, last_tick_slate_3_rotate = 0;
	public int recess_pos = 0;
	public int last_tick_recess_pos = 0;

	public int tile_1_recess_pos = 0;
	public int last_tick_recess_pos_tile_1 = 0;
	public int tile_2_recess_pos = 0;
	public int last_tick_recess_pos_tile_2 = 0;
	public int tile_3_recess_pos = 0;
	public int last_tick_recess_pos_tile_3 = 0;

	private int prev_shake_timer;
	private int shake_timer;
	private boolean shaking = false;
	private boolean falling_shake = false;

	public boolean hide_slate_1 = false;
	public boolean hide_slate_2 = false;
	public boolean hide_slate_3 = false;
	public boolean hide_lock = false;
	public boolean hide_back_wall = false;
	public boolean is_in_dungeon = false;

	public boolean is_gate_entrance = false;

	private final ItemStack renderStack = new ItemStack(BlockRegistry.BEAM_RELAY);

	private int shakingTimerMax = 240;

	public DungeonDoorRunesBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, false, false);
	}

	public DungeonDoorRunesBlockEntity(BlockPos pos, BlockState state, boolean mimic, boolean barishee) {
		super(BlockEntityRegistry.DUNGEON_DOOR_RUNES.get(), pos, state);
		this.mimic = mimic;
		this.barrishee = barishee;
	}

	public void sinkingParticles(Level level, BlockPos pos, BlockState state, float ySpikeVel) {
		Direction facing = state.getValue(DungeonDoorRunesBlock.FACING);
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				if (level.isClientSide())
					this.spawnSinkingParticles(level, pos.offset(0, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				if (level.isClientSide())
					this.spawnSinkingParticles(level, pos.offset(x, -1, 0), 0F + ySpikeVel);
		}
	}

	public void crashingParticles(Level level, BlockPos pos, BlockState state, float ySpikeVel) { // used for damaging entities too atm
		Direction facing = state.getValue(DungeonDoorRunesBlock.FACING);
		AABB hitBox = new AABB(pos.relative(facing, 2)).inflate(1.0D);
		if (facing == Direction.EAST) {
			for (int x = 1; x <= 3; x++)
				for (int z = -1; z <= 1; z++)
					if (level.isClientSide())
						this.spawnCrashingParticles(level, pos.offset(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.WEST) {
			for (int x = -1; x >= -3; x--)
				for (int z = -1; z <= 1; z++)
					if (level.isClientSide())
						this.spawnCrashingParticles(level, pos.offset(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = 1; z <= 3; z++)
					if (level.isClientSide())
						this.spawnCrashingParticles(level, pos.offset(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == Direction.NORTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = -1; z >= -3; z--)
					if (level.isClientSide())
						this.spawnCrashingParticles(level, pos.offset(x, -1, z), 0F + ySpikeVel);
		}

		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, hitBox);
		for (LivingEntity entity : list) {
			if (entity != null/* && !(entity instanceof Barrishee)*/)
				if (!level.isClientSide())
					entity.hurt(level.damageSources().fallingBlock(null), 10F); // dunno what damage to do yet...
		}
	}

	//TODO shrink all this in to 1 method and eventually in to BL particles
	private void spawnSinkingParticles(Level level, BlockPos pos, float ySpikeVel) {
		if (level.isClientSide()) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + level.getRandom().nextInt(2); i < amount; i++) {
				double ox = level.getRandom().nextDouble() * 0.1F - 0.05F;
				double oz = level.getRandom().nextDouble() * 0.1F - 0.05F;
				double motionX = level.getRandom().nextDouble() * 0.2F - 0.1F;
				double motionY = level.getRandom().nextDouble() * 0.1F + 0.075F + ySpikeVel;
				double motionZ = level.getRandom().nextDouble() * 0.2F - 0.1F;
				level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.MUD_TILES.get().defaultBlockState()), px + ox, py, pz + oz, motionX, motionY, motionZ);
			}
		}
	}

	private void spawnCrashingParticles(Level level, BlockPos pos, float ySpikeVel) {
		if (level.isClientSide()) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + level.getRandom().nextInt(2); i < amount; i++) {
				double ox = level.getRandom().nextDouble() * 0.1F - 0.05F;
				double oz = level.getRandom().nextDouble() * 0.1F - 0.05F;
				double motionX = level.getRandom().nextDouble() * 0.2F - 0.1F;
				double motionY = level.getRandom().nextDouble() * 0.025F + 0.075F;
				double motionZ = level.getRandom().nextDouble() * 0.2F - 0.1F;
				level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.MUD_BRICKS.get().defaultBlockState()), px + ox, py, pz + oz, motionX, motionY + ySpikeVel, motionZ);
				level.addParticle(ParticleTypes.SMOKE, px + ox, py, pz + oz, motionX, 0D, motionZ);
			}
		}
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DungeonDoorRunesBlockEntity entity) {
		entity.renderTicks++;

		entity.lastTickTopRotate = entity.top_rotate;
		entity.lastTickMidRotate = entity.mid_rotate;
		entity.lastTickBottomRotate = entity.bottom_rotate;

		entity.last_tick_slate_1_rotate = entity.slate_1_rotate;
		entity.last_tick_slate_2_rotate = entity.slate_2_rotate;
		entity.last_tick_slate_3_rotate = entity.slate_3_rotate;

		entity.last_tick_recess_pos = entity.recess_pos;

		entity.last_tick_recess_pos_tile_1 = entity.tile_1_recess_pos;
		entity.last_tick_recess_pos_tile_2 = entity.tile_2_recess_pos;
		entity.last_tick_recess_pos_tile_3 = entity.tile_3_recess_pos;

		if (entity.top_state_prev != entity.top_state) {
			entity.top_rotate += 4;
			if (entity.top_rotate > 90) {
				entity.lastTickTopRotate = entity.top_rotate = 0;
				entity.top_state_prev = entity.top_state;
			}
		}

		if (entity.mid_state_prev != entity.mid_state) {
			entity.mid_rotate += 4;
			if (entity.mid_rotate > 90) {
				entity.lastTickMidRotate = entity.mid_rotate = 0;
				entity.mid_state_prev = entity.mid_state;
			}
		}

		if (entity.bottom_state_prev != entity.bottom_state) {
			entity.bottom_rotate += 4;
			if (entity.bottom_rotate > 90) {
				entity.lastTickBottomRotate = entity.bottom_rotate = 0;
				entity.bottom_state_prev = entity.bottom_state;
			}
		}

		if (entity.animate_open_recess) {
			if (entity.recess_pos <= 0)
				if (!level.isClientSide())
					entity.playOpenRecessSound(level, pos, true);
			entity.shake(240);
			entity.recess_pos += 1;
			int limit = 30;
			if (entity.recess_pos > limit) {
				entity.last_tick_recess_pos = entity.recess_pos = limit;
				if (!level.isClientSide()) {
					entity.animate_open_recess = false;
					if (!entity.animate_open) {
						entity.animate_open = true;
						level.sendBlockUpdated(pos, state, state, 3);
					}
				}
			}
		}

		if (entity.animate_tile_recess) {
			if (!entity.mimic) {
				if (entity.tile_1_recess_pos <= 0)
					if (!level.isClientSide())
						entity.playOpenRecessSound(level, pos, false);
				if (entity.tile_2_recess_pos == 2)
					if (!level.isClientSide())
						entity.playOpenRecessSound(level, pos, false);
				if (entity.tile_3_recess_pos == 2)
					if (!level.isClientSide())
						entity.playOpenRecessSound(level, pos, false);
				entity.tile_1_recess_pos += 2;
				if (entity.tile_1_recess_pos >= 20)
					entity.tile_2_recess_pos += 2;
				if (entity.tile_2_recess_pos >= 20)
					entity.tile_3_recess_pos += 2;
			}
			int limit = 60;
			if (entity.tile_1_recess_pos >= limit)
				entity.last_tick_recess_pos_tile_1 = entity.tile_1_recess_pos = limit;

			if (entity.tile_2_recess_pos >= limit)
				entity.last_tick_recess_pos_tile_2 = entity.tile_2_recess_pos = limit;

			if (entity.tile_3_recess_pos >= limit) {
				entity.last_tick_recess_pos_tile_3 = entity.tile_3_recess_pos = limit;
				if (!level.isClientSide()) {
					entity.break_blocks = true;
					level.sendBlockUpdated(pos, state, state, 3);
				}
			}
		}

		if (entity.animate_open) {
			if (entity.mimic) {
				if (entity.slate_1_rotate <= 0)
					if (!level.isClientSide()) {
						entity.playTrapFallingSound(level, pos);

						Direction facing = state.getValue(DungeonDoorRunesBlock.FACING);

						//TODO make this shit work properly
						BlockPos offsetPos = pos.relative(facing.getOpposite());
//						if (entity.barrishee) {
//							Barrishee barishee = new Barrishee(level);
//							barishee.moveTo(offsetPos.getX() + 0.5D, offsetPos.below().getY(), offsetPos.getZ() + 0.5D, 0F, 0.0F);
//							barishee.rotationYawHead = barishee.rotationYaw;
//							barishee.renderYawOffset = barishee.rotationYaw;
//							barishee.setIsAmbushSpawn(true);
//							barishee.setIsScreaming(true);
//							barishee.setScreamTimer(0);
//							level.addFreshEntity(barishee);
//						} else {
//							CryptCrawler crawler = new CryptCrawler(level);
//							crawler.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.below().getY(), offsetPos.getZ() + 0.5D, 0F, 0.0F);
//							crawler.rotationYawHead = crawler.rotationYaw;
//							crawler.renderYawOffset = crawler.rotationYaw;
//							crawler.setIsBiped(true);
//							crawler.setIsChief(true);
//							crawler.onInitialSpawn(level.getCurrentDifficultyAt(pos), null);
//							level.addFreshEntity(crawler);
//						}
					}
				entity.slate_1_rotate += 4 + (entity.last_tick_slate_1_rotate < 8 ? 0 : entity.last_tick_slate_1_rotate / 8);
				entity.slate_2_rotate += 2 + (entity.last_tick_slate_2_rotate < 6 ? 0 : entity.last_tick_slate_2_rotate / 6);
				entity.slate_3_rotate += 2 + (entity.last_tick_slate_3_rotate < 8 ? 0 : entity.last_tick_slate_3_rotate / 8);
				entity.hide_lock = true;
				entity.hide_back_wall = true;
			}
			if (!entity.mimic) {
				if (entity.slate_1_rotate == 0)
					if (!level.isClientSide()) {
						entity.playOpenSinkingSound(level, pos);
						if (entity.is_gate_entrance) {
//							entity.lightTowerBuild.destroyGateBeamLenses(level, pos);
//							entity.lightTowerBuild.destroyTowerBeamLenses(level, pos.offset(-15, -2, -14)); // centre stone of tower  bottom floor
						}
					}
				entity.slate_1_rotate += 4;
				entity.slate_2_rotate += 3;
				entity.slate_3_rotate += 3;
			}
			int limit = entity.mimic ? 90 : 360;
			if (!entity.mimic)
				if (entity.slate_3_rotate < limit - 6)
					entity.sinkingParticles(level, pos, state, 0F);
				else
					entity.sinkingParticles(level, pos, state, 0.25F);
			if (entity.slate_1_rotate >= limit) {
				if (entity.mimic) {
					entity.falling_shake = true;
					entity.crashingParticles(level, pos, state, 0.125F);
					entity.hide_slate_1 = true;
				}
				entity.last_tick_slate_1_rotate = entity.slate_1_rotate = limit;
			}
			if (entity.slate_2_rotate >= limit) {
				if (entity.mimic) {
					entity.crashingParticles(level, pos, state, 0.125F);
					entity.hide_slate_2 = true;
				}
				entity.last_tick_slate_2_rotate = entity.slate_2_rotate = limit;
			}
			if (entity.slate_3_rotate >= limit) {
				if (entity.mimic) {
					entity.crashingParticles(level, pos, state, 0.125F);
					entity.hide_slate_3 = true;
				}
				if (!entity.mimic) {
					entity.hide_slate_1 = true;
					entity.hide_slate_2 = true;
					entity.hide_slate_3 = true;
					entity.hide_lock = true;
					entity.hide_back_wall = true;
				}
				entity.last_tick_slate_3_rotate = entity.slate_3_rotate = limit;
				if (!level.isClientSide()) {
					if (entity.mimic)
						entity.break_blocks = true;
					if (!entity.mimic)
						if (entity.is_in_dungeon)
							entity.animate_tile_recess = true;
						else
							entity.break_blocks = true;
					entity.animate_open = false;
					level.sendBlockUpdated(pos, state, state, 3);
				}
			}
			if (entity.falling_shake) {
				entity.shake(10);
				entity.falling_shake = entity.shaking;
			}
		}

		if (!level.isClientSide()) {
			Direction facing = state.getValue(DungeonDoorRunesBlock.FACING);
			if (entity.top_state_prev == entity.top_code && entity.mid_state_prev == entity.mid_code && entity.bottom_state_prev == entity.bottom_code) {
				if (!entity.mimic) {
					if (!entity.animate_open_recess) {
						entity.animate_open_recess = true;
						level.sendBlockUpdated(pos, state, state, 3);
					}
				} else {
					if (!entity.animate_open) {
						entity.animate_open = true;
						level.sendBlockUpdated(pos, state, state, 3);
					}
				}
			}
			if (entity.break_blocks) {
				if (!entity.mimic)
					entity.breakAllDoorBlocks(level, pos, state, facing, entity.is_in_dungeon, false);
				else {
					entity.breakAllDoorBlocks(level, pos, state, facing, false, false);
				}
			}

			if (level.getGameTime() % 5 == 0)
				entity.checkComplete(level, pos, state, facing);
		}
	}

	public void shake(int shakeTimerMax) {
		this.shakingTimerMax = shakeTimerMax;
		this.prev_shake_timer = this.shake_timer;
		if (this.shake_timer == 0) {
			this.shake_timer = 1;
		}
		if (this.shake_timer > 0)
			this.shake_timer++;

		this.shaking = this.shake_timer < this.shakingTimerMax;
	}

	private void checkComplete(Level level, BlockPos pos, BlockState state, Direction facing) {
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = -1; y <= 1; y++)
					if (!(level.getBlockState(pos.offset(0, y, z)).getBlock() instanceof DungeonDoorRunesBlock))
						breakAllDoorBlocks(level, pos, state, facing, false, true);
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if (!(level.getBlockState(pos.offset(x, y, 0)).getBlock() instanceof DungeonDoorRunesBlock))
						breakAllDoorBlocks(level, pos, state, facing, false, true);
		}
	}

	public void breakAllDoorBlocks(Level level, BlockPos pos, BlockState state, Direction facing, boolean breakFloorBelow, boolean particles) {
		if (facing == Direction.WEST || facing == Direction.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						level.destroyBlock(pos.offset(0, y, z), false);
						level.removeBlockEntity(pos);
					} else {
						level.removeBlock(pos.offset(0, y, z), false);
						level.removeBlockEntity(pos);
					}
		}

		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						level.destroyBlock(pos.offset(x, y, 0), false);
						level.removeBlockEntity(pos);
					} else {
						level.removeBlock(pos.offset(x, y, 0), false);
						level.removeBlockEntity(pos);
					}
		}
	}

	public void cycleTopState(Level level, BlockPos pos) {
		top_state_prev = top_state;
		top_state++;
		if (top_state > 7)
			top_state = 0;
		this.setChanged();
		this.playLockSound(level, pos);
	}

	public void cycleMidState(Level level, BlockPos pos) {
		mid_state_prev = mid_state;
		mid_state++;
		if (mid_state > 7)
			mid_state = 0;
		this.setChanged();
		this.playLockSound(level, pos);
	}

	public void cycleBottomState(Level level, BlockPos pos) {
		bottom_state_prev = bottom_state;
		bottom_state++;
		if (bottom_state > 7)
			bottom_state = 0;
		this.setChanged();
		this.playLockSound(level, pos);
	}

	public void enterLockCode(Level level, BlockPos pos) {
		top_code = top_state;
		mid_code = mid_state;
		bottom_code = bottom_state;
		top_state_prev = top_state = 0;
		mid_state_prev = mid_state = 0;
		bottom_state_prev = bottom_state = 0;
		level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
		this.setChanged();
	}

	private void playLockSound(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.MUD_DOOR_LOCK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	private void playOpenRecessSound(Level level, BlockPos pos, boolean isBigDoor) {
		level.playSound(null, pos, SoundRegistry.MUD_DOOR_1.get(), SoundSource.BLOCKS, isBigDoor ? 1.0F : 0.5F, 1.0F);
	}

	private void playOpenSinkingSound(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.MUD_DOOR_2.get(), SoundSource.BLOCKS, 1.0F, 0.9F);
	}

	private void playTrapFallingSound(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.MUD_DOOR_TRAP.get(), SoundSource.BLOCKS, 1.0F, 1.25F);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("top_code", this.top_code);
		tag.putInt("mid_code", this.mid_code);
		tag.putInt("bottom_code", this.bottom_code);
		tag.putInt("top_state", this.top_state);
		tag.putInt("mid_state", this.mid_state);
		tag.putInt("bottom_state", this.bottom_state);
		tag.putInt("top_state_prev", this.top_state_prev);
		tag.putInt("mid_state_prev", this.mid_state_prev);
		tag.putInt("bottom_state_prev", this.bottom_state_prev);
		tag.putBoolean("mimic", this.mimic);
		tag.putBoolean("barrishee", this.barrishee);
		tag.putBoolean("animate_open", this.animate_open);
		tag.putBoolean("animate_open_recess", this.animate_open_recess);
		tag.putBoolean("animate_tile_recess", this.animate_tile_recess);
		tag.putBoolean("break_blocks", this.break_blocks);
		tag.putBoolean("hide_slate_1", this.hide_slate_1);
		tag.putBoolean("hide_slate_2", this.hide_slate_2);
		tag.putBoolean("hide_slate_3", this.hide_slate_3);
		tag.putBoolean("hide_lock", this.hide_lock);
		tag.putBoolean("hide_back_wall", this.hide_back_wall);
		tag.putBoolean("is_in_dungeon", this.is_in_dungeon);
		tag.putBoolean("is_gate_entrance", this.is_gate_entrance);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.top_code = tag.getInt("top_code");
		this.mid_code = tag.getInt("mid_code");
		this.bottom_code = tag.getInt("bottom_code");
		this.top_state = tag.getInt("top_state");
		this.mid_state = tag.getInt("mid_state");
		this.bottom_state = tag.getInt("bottom_state");
		this.top_state_prev = tag.getInt("top_state_prev");
		this.mid_state_prev = tag.getInt("mid_state_prev");
		this.bottom_state_prev = tag.getInt("bottom_state_prev");
		this.mimic = tag.getBoolean("mimic");
		this.barrishee = tag.getBoolean("barrishee");
		this.animate_open = tag.getBoolean("animate_open");
		this.animate_open_recess = tag.getBoolean("animate_open_recess");
		this.animate_tile_recess = tag.getBoolean("animate_tile_recess");
		this.break_blocks = tag.getBoolean("break_blocks");
		this.hide_slate_1 = tag.getBoolean("hide_slate_1");
		this.hide_slate_2 = tag.getBoolean("hide_slate_2");
		this.hide_slate_3 = tag.getBoolean("hide_slate_3");
		this.hide_lock = tag.getBoolean("hide_lock");
		this.hide_back_wall = tag.getBoolean("hide_back_wall");
		this.is_in_dungeon = tag.getBoolean("is_in_dungeon");
		this.is_gate_entrance = tag.getBoolean("is_gate_entrance");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (this.isShaking()) {
			double dist = this.getDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 10.0F);
			if (dist >= 10.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(this.getShakingProgress() * Math.PI) + 0.1F) * 0.075F * shakeMult);
		} else {
			return 0.0F;
		}
	}

	public float getDistance(Entity entity) {
		float distX = (float) (this.getBlockPos().getX() - entity.blockPosition().getX());
		float distY = (float) (this.getBlockPos().getY() - entity.blockPosition().getY());
		float distZ = (float) (this.getBlockPos().getZ() - entity.blockPosition().getZ());
		return Mth.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public boolean isShaking() {
		return this.shaking;
	}

	public float getShakingProgress() {
		return 1.0F / this.shakingTimerMax * (this.prev_shake_timer + (this.shake_timer - this.prev_shake_timer));
	}

	public boolean isMimic() {
		return this.mimic;
	}

	public ItemStack cachedStack() {
		return renderStack;
	}
}
