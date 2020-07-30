package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
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
import thebetweenlands.common.item.ItemRuneWeavingTable;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;
import thebetweenlands.common.tile.TileEntityRuneWeavingTableFiller;

public class BlockRuneWeavingTable extends BlockContainer implements ICustomItemBlock {
	public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockRuneWeavingTable() {
		super(Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(PART, EnumPartType.MAIN));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		if(state.getValue(PART) == EnumPartType.MAIN) {
			facing = facing.rotateY();
		} else {
			facing = facing.rotateYCCW();
		}
		return new AxisAlignedBB(pos).expand(facing.getXOffset(), 0, facing.getZOffset());
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityRuneWeavingTable();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return state.getValue(PART) == EnumPartType.MAIN ? new TileEntityRuneWeavingTable() : new TileEntityRuneWeavingTableFiller();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TileEntityRuneWeavingTable || tile instanceof TileEntityRuneWeavingTableFiller) {
			if(player.isSneaking()) {
				return false;
			}

			if(!world.isRemote) {
				if(state.getValue(PART) == EnumPartType.MAIN) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_WEAVING_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					BlockPos offsetPos = pos.offset(state.getValue(FACING).rotateYCCW());
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_RUNE_WEAVING_TABLE, world, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileEntity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(world, pos, state);
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		EnumFacing offset = state.getValue(FACING).rotateYCCW();

		if(state.getValue(PART) == EnumPartType.FILLER) {
			if(worldIn.getBlockState(pos.offset(offset)).getBlock() != this) {
				worldIn.setBlockToAir(pos);
			}
		} else if(worldIn.getBlockState(pos.offset(offset.getOpposite())).getBlock() != this) {
			if(!worldIn.isRemote) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
			}

			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PART);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(PART) == EnumPartType.FILLER ? 0b1 : 0b0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta >> 1)).withProperty(PART, (meta & 0b1) != 0 ? EnumPartType.FILLER : EnumPartType.MAIN);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemRuneWeavingTable();
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