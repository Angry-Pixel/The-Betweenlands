package thebetweenlands.common.block.property;

import net.minecraft.block.state.IBlockState;

public class PropertyBlockStateUnlisted extends UnlistedPropertyHelper<IBlockState> {
	public PropertyBlockStateUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<IBlockState> getType() {
		return IBlockState.class;
	}
}