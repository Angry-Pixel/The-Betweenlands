package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MirageBlock extends Block {

	private final Block mirage;

	public MirageBlock(Block mirage, Properties properties) {
		super(properties);
		this.mirage = mirage;
	}

	public Block getMirageBlock() {
		return this.mirage;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
}
