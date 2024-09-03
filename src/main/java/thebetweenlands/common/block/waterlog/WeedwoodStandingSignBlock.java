package thebetweenlands.common.block.waterlog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;

public class WeedwoodStandingSignBlock extends WeedwoodSignBlock {
	public static final MapCodec<WeedwoodStandingSignBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type),
		propertiesCodec()
	).apply(instance, WeedwoodStandingSignBlock::new));
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

	public WeedwoodStandingSignBlock(WoodType type, BlockBehaviour.Properties properties) {
		super(type, properties.sound(type.soundType()));
		this.registerDefaultState(this.getStateDefinition().any().setValue(ROTATION, 0).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	public MapCodec<WeedwoodStandingSignBlock> codec() {
		return CODEC;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isSolid();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
			.setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation() + 180.0F))
			.setValue(WATER_TYPE, WaterType.getFromFluid(fluidstate.getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !this.canSurvive(state, level, currentPos)
			? Blocks.AIR.defaultBlockState()
			: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public float getYRotationDegrees(BlockState state) {
		return RotationSegment.convertToDegrees(state.getValue(ROTATION));
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(ROTATION, Integer.valueOf(rot.rotate(state.getValue(ROTATION), 16)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ROTATION, Integer.valueOf(mirror.mirror(state.getValue(ROTATION), 16)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, WATER_TYPE);
	}
}
