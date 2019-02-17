package thebetweenlands.common.block.plant;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockHollowLog extends BlockHorizontal {
	public BlockHollowLog() {
		super(Material.WOOD);
		setHardness(0.8F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().with(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing)state.get(FACING)).getHorizontalIndex();
	}

	@Override
	public boolean isNormalCube(IBlockState state, IWorldReader world, BlockPos pos) {
		return false;
	}


	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate((EnumFacing)state.get(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.get(FACING)));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IWorldReader blockAccess, BlockPos pos, EnumFacing side) {
		return side.getDirectionVec().getY() != 0 || 
				blockAccess.getBlockState(pos.offset(side)).getBlock() != this ||
				side.getAxis() != blockAccess.getBlockState(pos.offset(side)).getValue(FACING).getAxis();
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IWorldReader world, BlockPos pos, EnumFacing side) {
		return side.getAxis() != base_state.get(FACING).getAxis();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return EnumItemMisc.DRY_BARK.getID();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return face.getAxis() == state.get(FACING).getAxis() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
