package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class ProcessedEntityCollisionBox<T extends Entity> extends AxisAlignedBB {
	private final T entity;

	public ProcessedEntityCollisionBox(T entity, BlockPos pos) {
		super(pos);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, BlockPos pos1, BlockPos pos2) {
		super(pos1, pos2);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, Vec3d min, Vec3d max) {
		super(min, max);
		this.entity = entity;
	}

	public ProcessedEntityCollisionBox(T entity, AxisAlignedBB other) {
		super(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
		this.entity = entity;
	}

	public T getEntity() {
		return this.entity;
	}

	@Nullable
	public abstract AxisAlignedBB process(@Nullable Entity other, AxisAlignedBB otherAabb);
}
