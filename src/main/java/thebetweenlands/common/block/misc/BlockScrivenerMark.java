package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockScrivenerMark extends BasicBlock implements IConnectedTextureBlock {
	public static final PropertyBool NORTH_SIDE = PropertyBool.create("north_side");
	public static final PropertyBool EAST_SIDE = PropertyBool.create("east_side");
	public static final PropertyBool SOUTH_SIDE = PropertyBool.create("south_side");
	public static final PropertyBool WEST_SIDE = PropertyBool.create("west_side");
	
	public BlockScrivenerMark() {
		super(Material.CIRCUITS);
		this.setItemDropped(() -> Items.AIR);
		this.setLightOpacity(0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH_SIDE, false).withProperty(EAST_SIDE, false).withProperty(SOUTH_SIDE, false).withProperty(WEST_SIDE, false));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1D, 1.0D);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		return this.getExtendedConnectedTextureState(state, worldIn, pos, p -> worldIn.getBlockState(p).getBlock() instanceof BlockScrivenerMark, false);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		state = state.withProperty(WEST_SIDE, this.shouldAttachUp(worldIn, pos, EnumFacing.WEST));
		state = state.withProperty(EAST_SIDE, this.shouldAttachUp(worldIn, pos, EnumFacing.EAST));
		state = state.withProperty(NORTH_SIDE, this.shouldAttachUp(worldIn, pos, EnumFacing.NORTH));
		state = state.withProperty(SOUTH_SIDE, this.shouldAttachUp(worldIn, pos, EnumFacing.SOUTH));
		return state;
	}

	private boolean shouldAttachUp(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
		BlockPos offsetPos = pos.offset(direction);
		IBlockState offsetState = worldIn.getBlockState(offsetPos);

		if(!canConnectTo(worldIn.getBlockState(offsetPos), direction, worldIn, offsetPos) && (offsetState.isNormalCube() || !canConnectUpwardsTo(worldIn, offsetPos.down()))) {
			if(worldIn.getBlockState(offsetPos).isSideSolid(worldIn, offsetPos, EnumFacing.UP) && canConnectUpwardsTo(worldIn, offsetPos.up()) && offsetState.isBlockNormalCube()) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
		return canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
	}

	protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
		return blockState.getBlock() instanceof BlockScrivenerMark;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState downState = worldIn.getBlockState(pos.down());
		return downState.isTopSolid() || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote) {
			if(!this.canPlaceBlockAt(worldIn, pos)) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}
}
