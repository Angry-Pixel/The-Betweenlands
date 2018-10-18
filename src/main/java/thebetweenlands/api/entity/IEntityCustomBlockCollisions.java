package thebetweenlands.api.entity;

import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;

public interface IEntityCustomBlockCollisions {
	public void getCustomCollisionBoxes(AxisAlignedBB aabb, List<AxisAlignedBB> collisionBoxes);
}
