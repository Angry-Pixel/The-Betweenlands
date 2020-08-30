package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.tile.TileEntitySmokingRack;
import thebetweenlands.util.AdvancedStateMap.Builder;


public class BlockSmokingRack extends BlockContainer implements IStateMappedBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool INVISIBLE = PropertyBool.create("invisible");

	public BlockSmokingRack() {
		super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(INVISIBLE, false));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(INVISIBLE, false);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(INVISIBLE, false), 3);
		world.setBlockState(pos.up(), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(INVISIBLE, true), 3);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.down());
		if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up()))
			return false;
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(!getStateFromMeta(meta).getValue(INVISIBLE))
			return new TileEntitySmokingRack();
		return null;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byHorizontalIndex(meta);
		return getDefaultState().withProperty(FACING, facing.getAxis().isHorizontal() ? facing: EnumFacing.NORTH).withProperty(INVISIBLE, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta = meta | state.getValue(FACING).getIndex();
		if (state.getValue(INVISIBLE))
			meta |= 8;
		return meta;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, INVISIBLE);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if(!state.getValue(INVISIBLE))
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_SMOKING_RACK, world, pos.getX(), pos.getY(), pos.getZ());
			else
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_SMOKING_RACK, world, pos.getX(), pos.getY() - 1, pos.getZ());
		}
		return true;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			// left here just in case - dunno
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!state.getValue(INVISIBLE)) {
			TileEntitySmokingRack tile = (TileEntitySmokingRack) world.getTileEntity(pos);
			if (tile != null) {
				world.destroyBlock(pos.up(), false);
				InventoryHelper.dropInventoryItems(world, pos, tile);
				world.removeTileEntity(pos);
			}
		}
		if (state.getValue(INVISIBLE)) {
			TileEntitySmokingRack tile = (TileEntitySmokingRack) world.getTileEntity(pos.down());
			if (tile != null) {
				world.destroyBlock(pos.down(), false);
				InventoryHelper.dropInventoryItems(world, pos.down(), tile);
				world.removeTileEntity(pos.down());
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(INVISIBLE).build();
	}

}
