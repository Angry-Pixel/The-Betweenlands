package thebetweenlands.common.block.misc;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityOfferingTable;

public class BlockOfferingTable extends BlockGroundItem {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);	

	public BlockOfferingTable() {
		super(Material.ROCK);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setSoundType(SoundType.STONE);
		this.setHardness(0.5F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BOUNDING_AABB;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityGroundItem) {
			TileEntityGroundItem tile = (TileEntityGroundItem) tileEntity;

			ItemStack stack = playerIn.getHeldItem(hand);

			if(!stack.isEmpty() && tile.getStack().isEmpty()) {
				if(!worldIn.isRemote) {
					tile.setStack(stack);
					playerIn.setHeldItem(hand, ItemStack.EMPTY);
				}

				return true;
			}
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D + 2) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public boolean canStay(World world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
	@Override
	protected void onItemTaken(World world, BlockPos pos, EntityPlayer player, EnumHand hand, ItemStack stack) { }

	@Override
	public ItemBlock getItemBlock() {
		return BlockRegistry.ICustomItemBlock.getDefaultItemBlock(this);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityOfferingTable();
	}
}
