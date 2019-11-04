package thebetweenlands.api.capability;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public interface IEntityCustomCollisionsCapability {
	@FunctionalInterface
	public static interface EntityCollisionPredicate {
		public static EntityCollisionPredicate ALL = (entity, aabb, otherEntity, otherAabb) -> true;
		public static EntityCollisionPredicate NONE = (entity, aabb, otherEntity, otherAabb) -> false;

		public boolean isColliding(Entity entity, AxisAlignedBB aabb, Entity otherEntity, AxisAlignedBB otherAabb);
	}

	@FunctionalInterface
	public static interface BlockCollisionPredicate {
		public static EntityCollisionPredicate ALL = (entity, aabb, pos, state) -> true;
		public static EntityCollisionPredicate NONE = (entity, aabb, pos, state) -> false;

		public boolean isColliding(Entity entity, AxisAlignedBB aabb, MutableBlockPos pos, IBlockState state, @Nullable AxisAlignedBB blockAabb);
	}

	@FunctionalInterface
	public static interface CollisionBoxHelper {
		public void getCollisionBoxes(Entity entity, AxisAlignedBB aabb, EntityCollisionPredicate entityPredicate, BlockCollisionPredicate blockPredicate, List<AxisAlignedBB> collisionBoxes);
	}

	public void getCustomCollisionBoxes(CollisionBoxHelper collisionBoxHelper, AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxes);
	
	public boolean isPhasing();
	
	public double getViewObstructionCheckDistance();
	
	public double getViewObstructionDistance();
	
	public double getObstructionCheckDistance();
	
	public double getObstructionDistance();
}
