package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;

public interface IBlessingCapability {
	public boolean isBlessed();

	@Nullable
	public BlockPos getBlessingLocation();
	
	public int getBlessingDimension();

	public void setBlessed(int dimension, BlockPos location);
	
	public void clearBlessed();
}
