package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.items.RubberBootsItem;

public class SiltBlock extends Block {
	protected static final VoxelShape SILT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public SiltBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SILT_AABB;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!(entity instanceof Player player) || !(player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem)) {
			entity.makeStuckInBlock(state, new Vec3(0.4D, 1.0D, 0.4D));
		}
	}
}
