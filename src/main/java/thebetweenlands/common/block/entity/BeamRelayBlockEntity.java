package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.block.BeamRelayBlock;
import thebetweenlands.common.block.DungeonDoorRunesBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class BeamRelayBlockEntity extends SyncedBlockEntity {

	public boolean active;
	public boolean in_down, in_up, in_north, in_south, in_west, in_east;

	private int particleTimer = 0;

	public BeamRelayBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.BEAM_RELAY.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BeamRelayBlockEntity entity) {
		if (state.getValue(BeamRelayBlock.POWERED)) {
			if (!entity.active)
				entity.setActive(level, pos, state, true);
		} else {
			if (entity.active)
				entity.setActive(level, pos, state, false);
		}

		if (entity.active)
			entity.activateBlock(level, pos, state);
		else
			entity.deactivateBlock(level, pos, state);
	}

	private void spawnBeamParticles(Level level, BlockPos pos, Vec3 target) {
//		Vec3 dir = target.normalize();
//		float beamScale = 2.5F;
//		float beamScaleInset = 0.75f;
//		Vec3 beamStart = new Vec3(pos.getX() + 0.5 - dir.x * beamScale * beamScaleInset * 0.1f, pos.getY() + 0.5 - dir.y * beamScale * beamScaleInset * 0.1f, pos.getZ() + 0.5 - dir.z * beamScale * beamScaleInset * 0.1f);
//		Vec3 beamEnd = new Vec3(target.x + dir.x * beamScale * beamScaleInset * 0.1f * 2, target.y + dir.y * beamScale * beamScaleInset * 0.1f * 2, target.z + dir.z * beamScale * beamScaleInset * 0.1f * 2);
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(level, beamStart.x, beamStart.y, beamStart.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(40F, 220F, 130F, 1F).withScale(beamScale).withData(30, beamEnd)));
//		for(int i = 0; i < 3; i++) {
//			float offsetLen = level.getRandom().nextFloat();
//			Vec3 offset = new Vec3(target.x * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.y * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.z * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f);
//			float vx = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float vy = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f + 0.008f;
//			float vz = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float scale = 0.5f + level.getRandom().nextFloat();
//			if(ShaderHelper.INSTANCE.canUseShaders() && level.getRandom().nextBoolean()) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
//			} else {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(40F, 220F, 130F, 1F).withScale(scale).withData(100)));
//			}
//		}
	}

	public void activateBlock(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(BeamRelayBlock.FACING);
		BlockPos targetPos = pos.relative(facing, BeamOriginBlockEntity.getDistanceToObstruction(level, pos, facing));

		if (level.isClientSide()) {
			if (this.particleTimer++ >= 20) {
				this.particleTimer = 0;
				spawnBeamParticles(level, pos, new Vec3(targetPos.getX() - pos.getX(), targetPos.getY() - pos.getY(), targetPos.getZ() - pos.getZ()));
			}
		} else {
			BlockState stateofTarget = level.getBlockState(targetPos);


			if (stateofTarget.getBlock() instanceof BeamRelayBlock) {
				if (level.getBlockEntity(targetPos) instanceof BeamRelayBlockEntity relay) {
					relay.setTargetIncomingBeam(facing.getOpposite(), true);
					if (!level.getBlockState(targetPos).getValue(BeamRelayBlock.POWERED)) {
						stateofTarget = stateofTarget.cycle(BeamRelayBlock.POWERED);
						level.setBlock(targetPos, stateofTarget, 3);
					}
				}
			}

			if (stateofTarget.getBlock() instanceof DungeonDoorRunesBlock) {
				if (level.getBlockEntity(targetPos) instanceof DungeonDoorRunesBlockEntity runes) {
					if (runes.is_gate_entrance) {
						runes.top_state_prev = runes.top_code;
						runes.mid_state_prev = runes.mid_code;
						runes.bottom_state_prev = runes.bottom_code;
						level.setBlock(targetPos, stateofTarget, 3);
					}
				}
			}
		}
	}

	public void deactivateBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(BeamRelayBlock.FACING);
		BlockPos targetPos = pos.relative(facing, BeamOriginBlockEntity.getDistanceToObstruction(level, pos, facing));
		BlockState stateofTarget = level.getBlockState(targetPos);

		if (stateofTarget.getBlock() instanceof BeamRelayBlock) {
			if (level.getBlockEntity(targetPos) instanceof BeamRelayBlockEntity relay) {
				relay.setTargetIncomingBeam(facing.getOpposite(), false);
				if (!relay.isGettingBeamed())
					if (level.getBlockState(targetPos).getValue(BeamRelayBlock.POWERED)) {
						stateofTarget = stateofTarget.cycle(BeamRelayBlock.POWERED);
						level.setBlock(targetPos, stateofTarget, 3);
					}
			}
		}

	}

	public void setTargetIncomingBeam(Direction facing, boolean state) {
		switch (facing) {
			case DOWN -> this.in_down = state;
			case EAST -> this.in_east = state;
			case NORTH -> this.in_north = state;
			case SOUTH -> this.in_south = state;
			case UP -> this.in_up = state;
			case WEST -> this.in_west = state;
		}
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public boolean isGettingBeamed() {
		return this.in_up || this.in_down || this.in_north || this.in_south || this.in_west || this.in_east;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("active", this.active);
		tag.putBoolean("in_down", this.in_down);
		tag.putBoolean("in_up", this.in_up);
		tag.putBoolean("in_north", this.in_north);
		tag.putBoolean("in_south", this.in_south);
		tag.putBoolean("in_west)", this.in_west);
		tag.putBoolean("in_east", this.in_east);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.active = tag.getBoolean("active");
		this.in_down = tag.getBoolean("in_down");
		this.in_up = tag.getBoolean("in_up");
		this.in_north = tag.getBoolean("in_north");
		this.in_south = tag.getBoolean("in_south");
		this.in_west = tag.getBoolean("in_west)");
		this.in_east = tag.getBoolean("in_east");
	}
}
