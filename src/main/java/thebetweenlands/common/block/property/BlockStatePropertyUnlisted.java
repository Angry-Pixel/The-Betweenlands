package thebetweenlands.common.block.property;

import net.minecraft.block.state.IBlockState;

public class BlockStatePropertyUnlisted extends AbstractUnlistedProperty<IBlockState> {
	public BlockStatePropertyUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<IBlockState> getType() {
		return IBlockState.class;
	}
}