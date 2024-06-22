package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Hanger extends Block {

	public static final VoxelShape AABB = Block.box(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);

	public Hanger(Properties p_49795_) {
		super(p_49795_);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		return AABB;
	}
}
