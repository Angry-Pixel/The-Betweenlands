package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.TriState;
import thebetweenlands.api.block.DungeonFogBlock;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public abstract class DugSoilBlock extends BaseEntityBlock {

	public static final BooleanProperty FOGGED = BooleanProperty.create("fogged");
	public static final BooleanProperty DECAYED = BooleanProperty.create("decayed");
	public static final BooleanProperty COMPOSTED = BooleanProperty.create("composted");

	private final boolean purified;

	protected DugSoilBlock(boolean purified, Properties properties) {
		super(properties);
		this.purified = purified;
	}

	public static boolean copy(Level level, BlockPos pos, DugSoilBlockEntity from) {
		if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil) {
			soil.copy(level, pos, from);
			return true;
		}
		return false;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof DugSoilBlockEntity soil) {
			if (state.getValue(COMPOSTED)) {
				soil.setCompost(level, pos, 30);
			}
			if (state.getValue(DECAYED)) {
				soil.setDecay(level, pos, 20);
				soil.setCompost(level, pos, 30);
			}
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide()) {
			state = this.updateFoggedState(level, pos, state);

			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil) {
				if (!this.purified && soil.isComposted() && !soil.isFullyDecayed() && random.nextFloat() <= this.getDecayChance(level, pos, state, random)) {
					soil.setDecay(level, pos, soil.getDecay() + 1);
				}

				if (soil.isComposted()) {
					BlockState stateUp = level.getBlockState(pos.above());

					if (stateUp.getBlock() instanceof FarmablePlant plant) {

						if (plant.isFarmable(level, pos.above(), stateUp)) {
							BlockPos offsetPos = pos.above();

							offsetPos = switch (random.nextInt(4)) {
								case 0 -> offsetPos.north();
								case 1 -> offsetPos.south();
								case 2 -> offsetPos.east();
								case 3 -> offsetPos.west();
								default -> offsetPos;
							};

							float spreadChance = plant.getSpreadChance(level, pos.above(), stateUp, offsetPos, random);

							if (state.getValue(FOGGED)) {
								spreadChance *= 2;
							}

							if (random.nextFloat() <= spreadChance && plant.canSpreadTo(level, pos.above(), stateUp, offsetPos, random)) {
								plant.spreadTo(level, pos.above(), stateUp, offsetPos, random);
								soil.setCompost(level, pos, Math.max(soil.getCompost() - plant.getCompostCost(level, pos.above(), stateUp, random), 0));
							}
						}
					}
				}

				if (soil.isFullyDecayed()) {
					for (int i = 0; i < 1 + random.nextInt(6); i++) {
						BlockPos offsetPos = pos.above();
						offsetPos = switch (random.nextInt(5)) {
							case 0 -> offsetPos.north();
							case 1 -> offsetPos.south();
							case 2 -> offsetPos.east();
							case 3 -> offsetPos.west();
							default -> offsetPos;
						};
						BlockState stateOffset = level.getBlockState(offsetPos);
						if (stateOffset.getBlock() instanceof FarmablePlant plant) {
							if (plant.isFarmable(level, offsetPos, stateOffset)) {
								plant.decayPlant(level, offsetPos, stateOffset, random);
							}
						}
					}

					//Spread decay
					for (int xo = -1; xo <= 1; xo++) {
						for (int zo = -1; zo <= 1; zo++) {
							if ((xo == 0 && zo == 0) || (zo != 0 && xo != 0) || random.nextInt(3) != 0) {
								continue;
							}
							BlockPos offset = pos.offset(xo, 0, zo);
							BlockState offsetState = level.getBlockState(offset);
							if (offsetState.getBlock() instanceof DugSoilBlock offsetSoil) {
								if (!offsetSoil.purified) {
									if (level.getBlockEntity(offset) instanceof DugSoilBlockEntity offsetEntity && !offsetEntity.isFullyDecayed() && offsetEntity.isComposted()) {
										offsetEntity.setDecay(level, pos, offsetEntity.getDecay() + 5);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Updates the fogged state, i.e. whether a nearby censer is producing fog or not. Pretty much
	 * like farmland and water.
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	protected BlockState updateFoggedState(Level level, BlockPos pos, BlockState state) {
		boolean shouldBeFogged = false;

		for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-6, 0, -6), pos.offset(6, 1, 6))) {
			if (level.isLoaded(checkPos)) {
				BlockState offsetState = level.getBlockState(checkPos);
				if (offsetState.getBlock() instanceof DungeonFogBlock fog && fog.isCreatingDungeonFog(level, checkPos, offsetState)) {
					shouldBeFogged = true;
					break;
				}
			}
		}

		if (shouldBeFogged != state.getValue(FOGGED)) {
			state = state.setValue(FOGGED, shouldBeFogged);
			level.setBlockAndUpdate(pos, state);
		}

		return state;
	}

	/**
	 * Returns the decay chance
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @param random
	 * @return
	 */
	protected float getDecayChance(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 0.25F;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil && soil.getCompost() == 0 && stack.is(ItemRegistry.COMPOST)) {
			if (!level.isClientSide()) {
				level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.PLAYERS, 1, 0.5f + level.getRandom().nextFloat() * 0.5f);
				soil.setCompost(level, pos, 30);
				if (!player.isCreative()) {
					stack.shrink(1);
				}
			}
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(FOGGED)) {
//			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + rand.nextFloat(), pos.getY() + 1, pos.getZ() + rand.nextFloat(),
//				ParticleArgs.get()
//					.withMotion((random.nextFloat() - 0.5f) * 0.05f, random.nextFloat() * 0.02F + 0.005F, (random.nextFloat() - 0.5f) * 0.05f)
//					.withScale(5.0f)
//					.withColor(1, 1, 1, 0.1f)
//					.withData(80, true, 0.0F, true)));
		}

		if (state.getValue(DECAYED)) {
//			BLParticles.DIRT_DECAY.spawn(level, pos.getX() + random.nextFloat(), pos.getY() + 1.0F, pos.getZ() + random.nextFloat());

			for (int i = 0; i < 5; i++) {
				BlockPos offsetPos = pos.above();
				offsetPos = switch (i) {
					case 0 -> offsetPos.north();
					case 1 -> offsetPos.south();
					case 2 -> offsetPos.east();
					case 3 -> offsetPos.west();
					default -> offsetPos;
				};
				BlockState stateOffset = level.getBlockState(offsetPos);
				if (stateOffset.getBlock() instanceof FarmablePlant plant && plant.isFarmable(level, offsetPos, stateOffset)) {
//					BLParticles.DIRT_DECAY.spawn(level, offsetPos.getX() + random.nextFloat(), offsetPos.getY(), offsetPos.getZ() + random.nextFloat());
				}
			}
		} else {
			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil && soil.getDecay() >= 11) {
				if (random.nextInt(Math.max(120 - (soil.getDecay() - 11) * 14, 2)) == 0) {
//					BLParticles.DIRT_DECAY.spawn(level, pos.getX() + random.nextFloat(), pos.getY() + 1.0F, pos.getZ() + random.nextFloat());

					for (int i = 0; i < 5; i++) {
						BlockPos offsetPos = pos.above();
						offsetPos = switch (i) {
							case 0 -> offsetPos.north();
							case 1 -> offsetPos.south();
							case 2 -> offsetPos.east();
							case 3 -> offsetPos.west();
							default -> offsetPos;
						};
						BlockState stateOffset = level.getBlockState(offsetPos);
						if (stateOffset.getBlock() instanceof FarmablePlant plant && plant.isFarmable(level, offsetPos, stateOffset)) {
//							BLParticles.DIRT_DECAY.spawn(level, offsetPos.getX() + random.nextFloat(), offsetPos.getY(), offsetPos.getZ() + random.nextFloat());
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
		return state.is(BlockTags.CROPS) ? TriState.TRUE : super.canSustainPlant(state, level, soilPosition, facing, plant);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DugSoilBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FOGGED, DECAYED, COMPOSTED);
	}

	/**
	 * Returns whether the soil is purified
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	public boolean isPurified(Level level, BlockPos pos, BlockState state) {
		return this.purified;
	}

	/**
	 * Returns how often crops can be harvested before the purified soil turns into unpurified soil
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	public int getPurifiedHarvests(Level level, BlockPos pos, BlockState state) {
		return 3;
	}

	/**
	 * Returns the unpurified variant of this soil.
	 * This should also copy the {@link #COMPOSTED} and {@link #DECAYED}
	 * properties.
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	public abstract BlockState getUnpurifiedDugSoil(Level level, BlockPos pos, BlockState state);
}
