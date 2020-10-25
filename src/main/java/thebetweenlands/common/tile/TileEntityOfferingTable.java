package thebetweenlands.common.tile;

import net.minecraft.util.EnumFacing;
import thebetweenlands.common.block.misc.BlockOfferingTable;
import thebetweenlands.util.StatePropertyHelper;

public class TileEntityOfferingTable extends TileEntityGroundItem {
	@Override
	public boolean hasRandomOffset() {
		return false;
	}

	@Override
	public float getYOffset() {
		return 0.5f;
	}

	@Override
	public boolean isItemUpsideDown() {
		return false;
	}

	@Override
	public float getYRotation(float randomRotation) {
		EnumFacing facing = StatePropertyHelper.getStatePropertySafely(this, BlockOfferingTable.class, BlockOfferingTable.FACING, EnumFacing.NORTH);
		return -facing.getHorizontalAngle();
	}
	
	@Override
	public float getTiltRotation() {
		return 0;
	}
	
	@Override
	public float getItemScale() {
		return 0.5f;
	}
}
