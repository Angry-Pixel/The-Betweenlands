package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.property.BooleanPropertyUnlisted;
import thebetweenlands.common.block.property.IntegerPropertyUnlisted;
import thebetweenlands.common.item.ItemBlockRoot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockRoot extends BasicBlock implements ICustomItemBlock {
	public static final BooleanPropertyUnlisted NO_BOTTOM = new BooleanPropertyUnlisted("no_bottom");
	public static final BooleanPropertyUnlisted NO_TOP = new BooleanPropertyUnlisted("no_top");
	public static final IntegerPropertyUnlisted DIST_UP = new IntegerPropertyUnlisted("dist_up");
	public static final IntegerPropertyUnlisted DIST_DOWN = new IntegerPropertyUnlisted("dist_down");
	public static final IntegerPropertyUnlisted POS_X = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Y = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Z = new IntegerPropertyUnlisted("pos_z");

	public BlockRoot() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.TANGLED_ROOT;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {POS_X, POS_Y, POS_Z, NO_BOTTOM, NO_TOP, DIST_UP, DIST_DOWN});
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IWorldReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		IBlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.add(0, 1 + distUp, 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT_UNDERWATER)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.add(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this || blockState.getBlock() == BlockRegistry.ROOT_UNDERWATER)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noBottom = true;
			break;
		}

		return state.with(POS_X, pos.getX()).with(POS_Y, pos.getY()).with(POS_Z, pos.getZ()).with(DIST_UP, distUp).with(DIST_DOWN, distDown).with(NO_TOP, noTop).with(NO_BOTTOM, noBottom);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockRoot();
	}
}
