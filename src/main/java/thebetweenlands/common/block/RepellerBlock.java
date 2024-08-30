package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.common.block.entity.RepellerBlockEntity;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.items.AspectVialItem;
import thebetweenlands.common.items.DentrothystVialItem;
import thebetweenlands.common.registries.AspectTypeRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;

public class RepellerBlock extends HorizontalBaseEntityBlock {

	public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.create("half", DoubleBlockHalf.class);
	public static final VoxelShape BOTTOM_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	public static final VoxelShape TOP_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 11.0D, 14.0D);

	public RepellerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE : TOP_SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER && level.getBlockState(pos.below()).is(this)) {
			this.useItemOn(stack, level.getBlockState(pos.below()), level, pos.below(), player, hand, hitResult);
		} else if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			if (!player.isCrouching() && level.getBlockEntity(pos) instanceof RepellerBlockEntity repeller) {
				if (stack.is(ItemRegistry.SHIMMER_STONE) && !repeller.hasShimmerstone()) {
					repeller.addShimmerstone();
					stack.consume(1, player);
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				} else if (stack.getItem() instanceof AspectVialItem) {
					if (repeller.hasShimmerstone()) {
						if (repeller.getFuel() < repeller.getMaxFuel()) {
							AspectContainerItem aspectContainer = AspectContainerItem.fromItem(stack);
							int amount = aspectContainer.get(level.registryAccess().registryOrThrow(BLRegistries.Keys.ASPECT_TYPES).getHolderOrThrow(AspectTypeRegistry.BYARIIS));
							int loss = 10; //Loss when adding
							if (amount >= loss) {
								if (!level.isClientSide()) {
									int added = repeller.addFuel(amount - loss);
									if (!player.isCreative()) {
										int leftAmount = amount - added - loss;
										if (leftAmount > 0) {
											aspectContainer.set(level.registryAccess().registryOrThrow(BLRegistries.Keys.ASPECT_TYPES).getHolderOrThrow(AspectTypeRegistry.BYARIIS), leftAmount);
										} else {
											player.setItemInHand(hand, stack.getCraftingRemainingItem());
										}
									}
								}
								return ItemInteractionResult.sidedSuccess(level.isClientSide());
							}
						}
					} else {
						if (!level.isClientSide()) {
							player.displayClientMessage(Component.translatable("chat.repeller.shimmerstone_missing"), true);
						}
					}
				} else if (stack.getItem() instanceof DentrothystVialItem vial && repeller.getFuel() > 0) {
					ItemStack newStack = new ItemStack(vial.getFullAspectBottle());
					if (!level.isClientSide()) {
						AspectContainerItem aspectContainer = AspectContainerItem.fromItem(newStack);
						aspectContainer.set(level.registryAccess().registryOrThrow(BLRegistries.Keys.ASPECT_TYPES).getHolderOrThrow(AspectTypeRegistry.BYARIIS), repeller.removeFuel(Amounts.VIAL));
					}
					stack.shrink(1);
					if (!player.getInventory().add(newStack)) {
						player.drop(newStack, false);
					}
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER && level.getBlockState(pos.below()).is(this)) {
			this.useWithoutItem(level.getBlockState(pos.below()), level, pos.below(), player, hitResult);
		} else if (state.getValue(HALF) == DoubleBlockHalf.LOWER && level.getBlockEntity(pos) instanceof RepellerBlockEntity repeller) {
			if (player.isCrouching() && repeller.hasShimmerstone()) {
				repeller.removeShimmerstone();
				ItemStack stack = new ItemStack(ItemRegistry.SHIMMER_STONE.get());
				if (!player.getInventory().add(stack)) {
					player.drop(stack, false);
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else if (!player.isCrouching()) {
				if (!level.isClientSide()) {
					repeller.cycleRadiusState();
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
		if (facing.getAxis() != Direction.Axis.Y
			|| doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)
			|| facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf) {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		} else {
			return Blocks.AIR.defaultBlockState();
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)
			? super.getStateForPlacement(context)
			: null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
			return super.canSurvive(state, level, pos);
		} else {
			BlockState blockstate = level.getBlockState(pos.below());
			if (state.getBlock() != this) return super.canSurvive(state, level, pos);
			return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			if (player.isCreative()) {
				preventDropFromBottomPart(level, pos, state, player);
			} else {
				dropResources(state, level, pos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
	}

	protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
		DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) {
			BlockPos blockpos = pos.below();
			BlockState blockstate = level.getBlockState(blockpos);
			if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
				level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(6) == 0) {
			if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
				if (level.getBlockEntity(pos) instanceof RepellerBlockEntity repeller && repeller.isRunning()) {
					Direction facing = state.getValue(FACING);
					for (int i = 0; i < 60; i++) {
						float rot = Mth.PI * 2.0F / 60.0F * i + Mth.PI * random.nextFloat() / 60.0F;
						double radius = Math.max(repeller.getRadius(0.0F), 1.0D);
						double rotX = Math.sin(rot) * radius;
						double rotZ = Math.cos(rot) * radius;
						double xOff = -facing.getStepX() * 0.23F;
						double zOff = facing.getStepZ() * 0.23F;
						double centerX = pos.getX() + 0.5F + xOff;
						double centerY = pos.getY() + 1.3F;
						double centerZ = pos.getZ() + 0.5F - zOff;
						List<Vec3> points = new ArrayList<>();
						points.add(new Vec3(centerX, centerY, centerZ));
						points.add(new Vec3(centerX, centerY + radius, centerZ));
						points.add(new Vec3(centerX + rotX, centerY + radius, centerZ + rotZ));
						points.add(new Vec3(centerX + rotX, pos.getY() + 0.1D, centerZ + rotZ));
//						BLParticles.ANIMATOR.spawn(level, centerX, centerY, centerZ, ParticleArgs.get().withData(points));
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new RepellerBlockEntity(pos, state) : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.REPELLER.get(), RepellerBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HALF));
	}
}
