package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.items.RubberBootsItem;
import thebetweenlands.common.registries.BlockRegistry;

public class PeatBlock extends Block {
	protected static final VoxelShape PEAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public PeatBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PEAT_AABB;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!(entity instanceof Player player) || !(player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem)) {
			entity.makeStuckInBlock(state, new Vec3(0.85D, 0.85D, 0.85D));
		}
	}

	@Override
	public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 0;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (!level.isClientSide() && neighborPos.getY() > pos.getY() && neighborBlock.defaultBlockState().is(BlockTags.FIRE) && !level.getBlockState(neighborPos).is(BlockTags.FIRE)) {
			level.setBlockAndUpdate(pos, BlockRegistry.SMOULDERING_PEAT.get().defaultBlockState());
		}
	}
}
