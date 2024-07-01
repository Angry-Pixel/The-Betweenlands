package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class WispBlock extends Block {

	protected static final VoxelShape SHAPE = Shapes.box(3.2D, 3.2D, 3.2D, 12.8D, 12.8D, 12.8D);
	public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 3);
	public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");

	// Colors can be added here, always add a pair of colors for outer color and inner color
	public static final int[] COLORS = new int[] {
		0xFF7F1659, 0xFFFFFFFF, // Pink/White
		0xFF0707C8, 0xFFC8077B, // Blue/Pink
		0xFF0E2E0B, 0xFFC8077B, // Green/Yellow/White
		0xFF9A6908, 0xFF4F0303 // Red/Yellow/White
	};

	public WispBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 0).setValue(VISIBLE, false));
	}

	@Override
	protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(VISIBLE)) {
			return super.getInteractionShape(state, level, pos);
		}
		return Shapes.empty();
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack stack) {
		if (!level.isClientSide() && state.getValue(VISIBLE)) {
			ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(this));
			level.addFreshEntity(item);
		}
	}

	//TODO: tile entity

	@Override
	protected RenderShape getRenderShape(BlockState pState) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		state = this.defaultBlockState().setValue(COLOR, level.getRandom().nextInt(COLORS.length / 2));
		level.setBlock(pos, state, 2);
		this.updateVisibility(level, pos, state);
		level.scheduleTick(pos, this, 40);
	}

	public void generateBlock(Level level, BlockPos pos) {
		level.setBlock(pos, this.defaultBlockState().setValue(COLOR, level.getRandom().nextInt(COLORS.length / 2)), 2);
	}

	protected boolean checkVisibility(Level level, BlockPos pos) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(level);

		//TODO: reimplement when registry is back

		return false;
	}

	protected void updateVisibility(Level level, BlockPos pos, BlockState state) {
		level.setBlockAndUpdate(pos, state.setValue(VISIBLE, this.checkVisibility(level, pos)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COLOR, VISIBLE);
	}
}
