package thebetweenlands.common.block.container;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;

public class BlockRuneCarvingTable extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool FULL_GRID = PropertyBool.create("full_grid");

	public BlockRuneCarvingTable() {
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(FULL_GRID, false));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(FULL_GRID, this.checkFullGridState(world, pos));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()).withProperty(FULL_GRID, this.checkFullGridState(world, pos)), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TileEntityRuneCarvingTable) {
			if (player.isSneaking()) {
				return false;
			}

			if (!world.isRemote && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_CARVING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		if(!worldIn.isRemote) {
			boolean fullGridState = this.checkFullGridState(worldIn, pos);
			if(state.getValue(FULL_GRID) != fullGridState) {
				worldIn.setBlockState(pos, state.withProperty(FULL_GRID, fullGridState));

				if(!fullGridState) {
					TileEntity tile = worldIn.getTileEntity(pos);

					if(tile instanceof TileEntityRuneCarvingTable) {
						TileEntityRuneCarvingTable carvingTable = (TileEntityRuneCarvingTable) tile;

						for(int i = 1; i < 9; ++i) {
							ItemStack stack = carvingTable.getStackInSlot(i);

							if(!stack.isEmpty()) {
								InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.5f, pos.getZ(), stack);

								carvingTable.setInventorySlotContents(i, ItemStack.EMPTY);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRuneCarvingTable();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, FULL_GRID);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	protected boolean checkFullGridState(IBlockAccess world, BlockPos pos) {
		for(EnumFacing facing : EnumFacing.VALUES) {
			if(facing != EnumFacing.UP) {
				IBlockState state = world.getBlockState(pos.offset(facing));
				if(state.getBlock() instanceof BlockWeedwoodWorkbench || state.getBlock() instanceof BlockWorkbench) {
					return true;
				}
			}
		}
		return false;
	}
}