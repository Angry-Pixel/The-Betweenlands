package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.block.structure.BeamOriginBlock;
import thebetweenlands.common.block.structure.BeamRelayBlock;
import thebetweenlands.common.block.structure.DiagonalEnergyBarrierBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BeamOriginBlockEntity extends SyncedBlockEntity {

	public boolean active;

	public float prevVisibility = 0.0f;
	public float visibility = 0.0f;

	public float prevRotation = Mth.PI / 4;
	public float rotation = Mth.PI / 4;

	private int particleTimer = 0;

	public boolean beam_1_active = false;
	public boolean beam_2_active = false;
	public boolean beam_3_active = false;
	public boolean beam_4_active = false;

	public BeamOriginBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.BEAM_ORIGIN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BeamOriginBlockEntity entity) {
		int litBraziers = entity.checkForLitBraziers(level, pos);

		if (litBraziers == 4) {
			if (!entity.active) {
				entity.setActive(level, pos, state, true);
				if (!level.isClientSide()) {
					entity.spawnEmberling(level, pos.offset(3, -1, 3));
					entity.spawnEmberling(level, pos.offset(3, -1, -3));
					entity.spawnEmberling(level, pos.offset(-3, -1, 3));
					entity.spawnEmberling(level, pos.offset(-3, -1, -3));
				}
			}
		} else {
			if (entity.active)
				entity.setActive(level, pos, state, false);
		}


		if (level.getGameTime() % 10 == 0) {
			if (entity.checkForLitBrazier(level, pos.offset(3, -1, 3))) {
				if (level.isClientSide())
					entity.spawnBrazierParticles(level, pos, new Vec3(3, -1, 3));
				if (!level.isClientSide())
					if (!entity.beam_1_active) {
						entity.setBeam1Active(level, pos, state, true);
						level.playSound(null, pos.offset(3, -1, 3), SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (entity.beam_1_active)
						entity.setBeam1Active(level, pos, state, false);
			}

			if (entity.checkForLitBrazier(level, pos.offset(3, -1, -3))) {
				if (level.isClientSide())
					entity.spawnBrazierParticles(level, pos, new Vec3(3, -1, -3));
				if (!level.isClientSide())
					if (!entity.beam_2_active) {
						entity.setBeam2Active(level, pos, state, true);
						level.playSound(null, pos.offset(3, -1, -3), SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (entity.beam_2_active)
						entity.setBeam2Active(level, pos, state, false);
			}

			if (entity.checkForLitBrazier(level, pos.offset(-3, -1, 3))) {
				if (level.isClientSide())
					entity.spawnBrazierParticles(level, pos, new Vec3(-3, -1, 3));
				if (!level.isClientSide())
					if (!entity.beam_3_active) {
						entity.setBeam3Active(level, pos, state, true);
						level.playSound(null, pos.offset(-3, -1, 3), SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (entity.beam_3_active)
						entity.setBeam3Active(level, pos, state, false);
			}

			if (entity.checkForLitBrazier(level, pos.offset(-3, -1, -3))) {
				if (level.isClientSide())
					entity.spawnBrazierParticles(level, pos, new Vec3(-3, -1, -3));
				if (!level.isClientSide())
					if (!entity.beam_4_active) {
						entity.setBeam4Active(level, pos, state, true);
						level.playSound(null, pos.offset(-3, -1, -3), SoundRegistry.PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.125F, 0.25F);
					}
			} else {
				if (!level.isClientSide())
					if (entity.beam_4_active)
						entity.setBeam4Active(level, pos, state, false);
			}
		}

		entity.prevVisibility = entity.visibility;
		entity.prevRotation = entity.rotation;

		entity.rotation += litBraziers * 0.0025f;

		float targetVisibility = 0.2f + 0.8f * litBraziers / 4.0f;

		if (entity.visibility < targetVisibility) {
			entity.visibility += 0.02f;
			if (entity.visibility > targetVisibility) {
				entity.visibility = targetVisibility;
			}
		} else if (entity.visibility > targetVisibility) {
			entity.visibility -= 0.02f;
			if (entity.visibility < targetVisibility) {
				entity.visibility = targetVisibility;
			}
		}

		if (entity.active) {
			entity.activateBlock(level, pos, state);
		} else {
			entity.deactivateBlock(level, pos, state);
		}
	}

	private void spawnEmberling(Level level, BlockPos pos) {
//		EmberlingShaman entity = new EmberlingShaman (level);
//		entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
//		//entity.setBoundOrigin(pos); // may use this dunno yet...
//		entity.onInitialSpawn(level.getCurrentDifficultyAt(pos), null);
//		level.addFreshEntity(entity);
	}

	public int checkForLitBraziers(Level level, BlockPos pos) {
		int braziers = 0;
		if (this.checkForLitBrazier(level, pos.offset(3, -1, 3))) braziers++;
		if (this.checkForLitBrazier(level, pos.offset(3, -1, -3))) braziers++;
		if (this.checkForLitBrazier(level, pos.offset(-3, -1, 3))) braziers++;
		if (this.checkForLitBrazier(level, pos.offset(-3, -1, -3))) braziers++;
		return braziers;
	}

	public boolean checkForLitBrazier(Level level, BlockPos targetPos) {
		BlockState flame = level.getBlockState(targetPos);
		return flame.getBlock() instanceof FireBlock;
	}

	private void spawnBrazierParticles(Level level, BlockPos pos, Vec3 target) {
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(level, pos.getX() + 0.5 + target.x, pos.getY() + 0.5 + target.y, pos.getZ() + 0.5 + target.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(255F, 102F, 0F, 1F).withScale(1.5F).withData(30, target.scale(-1))));
//		for (int i = 0; i < 2; i++) {
//			float offsetLen = level.getRandom().nextFloat();
//			Vec3 offset = new Vec3(target.x * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.y * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.z * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f);
//			float vx = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float vy = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f + 0.008f;
//			float vz = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float scale = 0.5f + level.getRandom().nextFloat();
//			if (ShaderHelper.INSTANCE.canUseShaders() && level.getRandom().nextBoolean()) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
//			} else {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(255F, 102F, 0F, 1F).withScale(scale).withData(100)));
//			}
//		}
	}

	private void spawnBeamParticles(Level level, BlockPos pos, Vec3 target) {
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ParticleArgs.get().withMotion(0, 0, 0).withColor(40F, 220F, 130F, 1F).withScale(2.5F).withData(30, target)));
//		for (int i = 0; i < 3; i++) {
//			float offsetLen = this.level.getRandom().nextFloat();
//			Vec3 offset = new Vec3(target.x * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.y * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.z * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f);
//			float vx = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float vy = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f + 0.008f;
//			float vz = (level.getRandom().nextFloat() * 2f - 1) * 0.0025f;
//			float scale = 0.5f + level.getRandom().nextFloat();
//			if (ShaderHelper.INSTANCE.canUseShaders() && level.getRandom().nextBoolean()) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
//			} else {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(level, pos.getX() + 0.5 + offset.x, pos.getY() + 0.5 + offset.y, pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(40F, 220F, 130F, 1F).withScale(scale).withData(100)));
//			}
//		}
	}

	public void activateBlock(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			if (!state.getValue(BeamOriginBlock.POWERED)) {
				level.setBlockAndUpdate(pos, state.setValue(BeamOriginBlock.POWERED, true));
				level.playSound(null, pos, SoundRegistry.BEAM_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}

		Direction facing = Direction.DOWN;
		BlockPos targetPos = pos.relative(facing, getDistanceToObstruction(level, pos, facing));

		if (level.isClientSide()) {
			if (this.particleTimer++ >= 20) {
				this.particleTimer = 0;
				this.spawnBeamParticles(level, pos, new Vec3(targetPos.getX() - pos.getX(), targetPos.getY() - pos.getY(), targetPos.getZ() - pos.getZ()));
			}
		} else {
			BlockState stateofTarget = level.getBlockState(targetPos);

			if (stateofTarget.getBlock() instanceof BeamRelayBlock) {
				if (level.getBlockEntity(targetPos) instanceof BeamRelayBlockEntity relay) {
					relay.setTargetIncomingBeam(facing.getOpposite(), true);
					if (!level.getBlockState(targetPos).getValue(BeamRelayBlock.POWERED)) {
						stateofTarget = stateofTarget.cycle(BeamRelayBlock.POWERED);
						level.setBlockAndUpdate(targetPos, stateofTarget);
					}
				}
			}
		}
	}

	public void deactivateBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		if (state.getValue(BeamOriginBlock.POWERED)) {
			level.setBlock(pos, state.setValue(BeamOriginBlock.POWERED, false), 3);

			Direction facing = Direction.DOWN;
			BlockPos targetPos = pos.relative(facing, getDistanceToObstruction(level, pos, facing));
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
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public void setBeam1Active(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.beam_1_active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public void setBeam2Active(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.beam_2_active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public void setBeam3Active(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.beam_3_active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public void setBeam4Active(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.beam_4_active = isActive;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public static int getDistanceToObstruction(LevelAccessor level, BlockPos pos, Direction facing) {
		int distance;
		for (distance = 1; distance < 14; distance++) {
			BlockState state = level.getBlockState(pos.relative(facing, distance));
			if (!state.isAir()
				&& !(state.getBlock() instanceof DiagonalEnergyBarrierBlock)
				&& !(state.is(BlockRegistry.MUD_ENERGY_BARRIER))
				&& !(state.is(BlockRegistry.BEAM_LENS_SUPPORT))
				&& !isValidBeamTubeLens(state, facing))
				break;
		}
		return distance;
	}

	public static boolean isValidBeamTubeLens(BlockState state, Direction facing) {
		return state.is(BlockRegistry.BEAM_TUBE) && state.getValue(RotatedPillarBlock.AXIS) == facing.getAxis();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("active", this.active);
		tag.putBoolean("beam_1_active", this.beam_1_active);
		tag.putBoolean("beam_2_active", this.beam_2_active);
		tag.putBoolean("beam_3_active", this.beam_3_active);
		tag.putBoolean("beam_4_active", this.beam_4_active);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.active = tag.getBoolean("active");
		this.beam_1_active = tag.getBoolean("beam_1_active");
		this.beam_2_active = tag.getBoolean("beam_2_active");
		this.beam_3_active = tag.getBoolean("beam_3_active");
		this.beam_4_active = tag.getBoolean("beam_4_active");
	}
}
