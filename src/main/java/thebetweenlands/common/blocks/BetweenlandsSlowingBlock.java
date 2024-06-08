package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BetweenlandsSlowingBlock extends BetweenlandsBlock {
	// Soul sand block depth
	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
	private boolean WebLike = false;
	private float Resistance = 1.0f;
	
	public BetweenlandsSlowingBlock(float resistance, boolean weblike, Properties p_48756_) {
		super(p_48756_);
		Resistance = 1 - resistance;
		WebLike = weblike;
	}
	
	public VoxelShape getBlockSupportShape(BlockState p_56707_, BlockGetter p_56708_, BlockPos p_56709_) {
		return Shapes.block();
	}

	public VoxelShape getVisualShape(BlockState p_56684_, BlockGetter p_56685_, BlockPos p_56686_, CollisionContext p_56687_) {
		return Shapes.block();
	}
	
	public VoxelShape getCollisionShape(BlockState p_56702_, BlockGetter p_56703_, BlockPos p_56704_, CollisionContext p_56705_) {
		return SHAPE;
	}
	
	
	// Check player armor for slow resist items
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity.isInWater() == false)
		{
			if (WebLike == true)
			{
				// use cobweb slowing
				entity.makeStuckInBlock(state, new Vec3(Resistance, Resistance, Resistance));
			}
			else
			{
				// use soul sand like slowing
				entity.setDeltaMovement(entity.getDeltaMovement().x * Resistance, entity.getDeltaMovement().y, entity.getDeltaMovement().z * Resistance);
			}
		}
	}

}
