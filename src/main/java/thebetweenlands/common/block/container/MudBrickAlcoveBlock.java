package thebetweenlands.common.block.container;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.MudBrickAlcoveBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.entity.monster.AshSprite;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class MudBrickAlcoveBlock extends HorizontalBaseEntityBlock implements SwampWaterLoggable {

	public static final BooleanProperty HAS_URN = BooleanProperty.create("has_urn");
	public static final IntegerProperty MUD_LEVEL = IntegerProperty.create("mud", 0, 4);
	public static final VoxelShape NORTH_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 10.0D, 16.0D, 12.0D, 16.0D), //back
		Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
		Block.box(0.0D, 10.0D, 0.0D, 2.0D, 12.0D, 10.0D), //left top support beam
		Block.box(14.0D, 10.0D, 0.0D, 16.0D, 12.0D, 10.0D), //right top support beam
		Block.box(0.0D, 0.0D, 8.0D, 2.0D, 10.0D, 10.0D), //left side beam
		Block.box(14.0D, 0.0D, 8.0D, 16.0D, 10.0D, 10.0D) //right side beam
	);
	public static final VoxelShape SOUTH_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 6.0D), //back
		Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
		Block.box(0.0D, 10.0D, 6.0D, 2.0D, 12.0D, 16.0D), //left top support beam
		Block.box(14.0D, 10.0D, 6.0D, 16.0D, 12.0D, 16.0D), //right top support beam
		Block.box(0.0D, 0.0D, 6.0D, 2.0D, 10.0D, 8.0D), //left side beam
		Block.box(14.0D, 0.0D, 6.0D, 16.0D, 10.0D, 8.0D) //right side beam
	);
	public static final VoxelShape WEST_SHAPE = Shapes.or(
		Block.box(10.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), //back
		Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
		Block.box(0.0D, 10.0D, 0.0D, 10.0D, 12.0D, 2.0D), //left top support beam
		Block.box(0.0D, 10.0D, 14.0D, 10.0D, 12.0D, 16.0D), //right top support beam
		Block.box(8.0D, 0.0D, 0.0D, 10.0D, 10.0D, 2.0D), //left side beam
		Block.box(8.0D, 0.0D, 14.0D, 10.0D, 10.0D, 16.0D) //right side beam
	);
	public static final VoxelShape EAST_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 6.0D, 12.0D, 16.0D), //back
		Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
		Block.box(6.0D, 10.0D, 0.0D, 16.0D, 12.0D, 2.0D), //left top support beam
		Block.box(6.0D, 10.0D, 14.0D, 16.0D, 12.0D, 16.0D), //right top support beam
		Block.box(6.0D, 0.0D, 0.0D, 8.0D, 10.0D, 2.0D), //left side beam
		Block.box(6.0D, 0.0D, 14.0D, 8.0D, 10.0D, 16.0D) //right side beam
	);
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, NORTH_SHAPE,
		Direction.WEST, WEST_SHAPE,
		Direction.SOUTH, SOUTH_SHAPE,
		Direction.EAST, EAST_SHAPE
	));

	public MudBrickAlcoveBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HAS_URN, false).setValue(WATER_TYPE, WaterType.NONE).setValue(MUD_LEVEL, 0));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && state.getValue(HAS_URN)) {
			if (level.getBlockEntity(pos) instanceof MudBrickAlcoveBlockEntity alcove) {
				BlockHitResult result = getHitResult(level, player, ClipContext.Fluid.NONE);

				if (result.getType() == HitResult.Type.BLOCK && result.getDirection() == state.getValue(FACING)) {
					BlockPos offsetPos = pos.relative(state.getValue(FACING));

					alcove.unpackLootTable(player);
					Containers.dropContents(level, pos, alcove.getItems());

					if (level.getRandom().nextInt(3) == 0) {
						AshSprite entity = new AshSprite(EntityRegistry.ASH_SPRITE.get(), level);
						entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
						entity.setBoundOrigin(offsetPos);
						level.addFreshEntity(entity);
					}

					level.playSound(null, pos, state.getSoundType(level, pos, player).getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
					level.levelEvent(null, 2001, pos, Block.getId(BlockRegistry.MUD_FLOWER_POT.get().defaultBlockState())); //this will do unless we want specific particles

					level.setBlockAndUpdate(pos, state.setValue(HAS_URN, false));
					level.sendBlockUpdated(pos, state, state, 2);
					if (player instanceof ServerPlayer sp) {
						AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().trigger(sp);
					}
				}
			}
		}
	}

	protected static BlockHitResult getHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
		Vec3 vec3 = player.getEyePosition();
		Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.blockInteractionRange()));
		return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluidMode, player));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MudBrickAlcoveBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HAS_URN, MUD_LEVEL, WATER_TYPE));
	}
}
