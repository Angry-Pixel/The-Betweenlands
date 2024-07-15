package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.registries.AttachmentRegistry;

public class SludgeBlock extends Block {

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public SludgeBlock(Properties properties) {
		super(properties);
	}

	public void generateTemporaryBlock(Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos, this.defaultBlockState());
		level.scheduleTick(pos, this, 1200);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.removeBlock(pos, false);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos.below());
		return Block.isFaceFull(blockstate.getCollisionShape(level, pos.below()), Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		MudWalkerData data = entity.getData(AttachmentRegistry.MUD_WALKER);
		boolean mudWalker = entity instanceof Player player && data.isActive(player);
		if (!(entity instanceof BLEntity) && entity.onGround() && !mudWalker) {
			entity.makeStuckInBlock(state, new Vec3(0.25D, 0.05D, 0.25D));
		}
	}
}
