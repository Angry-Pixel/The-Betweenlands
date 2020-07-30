package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemRuneCarvingTable;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTableFiller;

public class BlockRuneCarvingTable extends BasicBlock implements ITileEntityProvider, ICustomItemBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
	public static final PropertyBool FULL_GRID = PropertyBool.create("full_grid");

	public BlockRuneCarvingTable() {
		super(Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(PART, EnumPartType.MAIN).withProperty(FULL_GRID, false));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return state.getValue(PART) == EnumPartType.MAIN ? new AxisAlignedBB(pos).expand(0, 1, 0) : new AxisAlignedBB(pos).expand(0, -1, 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return blockState.getValue(PART) == EnumPartType.MAIN ? super.getCollisionBoundingBox(blockState, worldIn, pos) : null;
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

		if(tile instanceof TileEntityRuneCarvingTable || tile instanceof TileEntityRuneCarvingTableFiller) {
			if(player.isSneaking()) {
				return false;
			}

			if(!world.isRemote) {
				if(state.getValue(PART) == EnumPartType.MAIN) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_CARVING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_CARVING_TABLE, world, pos.getX(), pos.getY() - 1, pos.getZ());
				}
			}

			return true;
		}

		return false;
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

		if(state.getValue(PART) == EnumPartType.FILLER) {
			if(worldIn.getBlockState(pos.down()).getBlock() != this) {
				worldIn.setBlockToAir(pos);
			}
		} else if(worldIn.getBlockState(pos.up()).getBlock() != this) {
			if(!worldIn.isRemote) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
			}

			worldIn.setBlockToAir(pos);
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
		return (state.getValue(FULL_GRID) ? 0b1000 : 0) | state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(PART) == EnumPartType.FILLER ? 0b1 : 0b0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta >> 1)).withProperty(PART, (meta & 0b1) != 0 ? EnumPartType.FILLER : EnumPartType.MAIN).withProperty(FULL_GRID, (meta & 0b1000) != 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRuneCarvingTable();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return state.getValue(PART) == EnumPartType.MAIN ? new TileEntityRuneCarvingTable() : new TileEntityRuneCarvingTableFiller();
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if(state.getValue(PART) == EnumPartType.MAIN) {
			super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(PART) == EnumPartType.MAIN ? super.getItemDropped(state, rand, fortune) : Items.AIR;
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
		return new BlockStateContainer(this, FACING, PART, FULL_GRID);
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

	@Override
	public ItemBlock getItemBlock() {
		return new ItemRuneCarvingTable();
	}

	public static enum EnumPartType implements IStringSerializable {
		MAIN("main"),
		FILLER("filler");

		private final String name;

		private EnumPartType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
}