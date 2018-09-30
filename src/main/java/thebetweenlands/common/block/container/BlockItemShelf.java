package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityItemShelf;

public class BlockItemShelf extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);

	public BlockItemShelf() {
		super(Material.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing)state.getValue(FACING)) {
		default:
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
			return NORTH_AABB;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if(!world.isRemote && (!player.isSwingInProgress || player.prevSwingProgress != player.swingProgress)
				/*Ugly check so that it doesn't give 2 items when clicking with empty hand*/) {
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof TileEntityItemShelf) {
				IBlockState state = world.getBlockState(pos);

				TileEntityItemShelf shelf = (TileEntityItemShelf) te;

				RayTraceResult ray = this.rayTrace(pos, player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(10)), this.getBoundingBox(state, world, pos));
				if(ray != null) {
					InvWrapper wrapper = new InvWrapper(shelf);

					int slot = this.getSlot(state.getValue(FACING), (float)(ray.hitVec.x - pos.getX()), (float)(ray.hitVec.y - pos.getY()), (float)(ray.hitVec.z - pos.getZ()));

					ItemStack result = wrapper.extractItem(slot, player.isSneaking() ? 64 : 1, true);
					if(!result.isEmpty() && result.getCount() > 0) {
						result = wrapper.extractItem(slot, result.getCount(), false);
						world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
						if(!player.inventory.addItemStackToInventory(result)) {
							player.entityDropItem(result, 0);
						}
						world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_PLACE, SoundCategory.BLOCKS, 1, 0.8f);
					}
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND) {
			if(!world.isRemote) {
				ItemStack heldItem = player.getHeldItem(hand);
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof TileEntityItemShelf) {
					TileEntityItemShelf shelf = (TileEntityItemShelf) te;

					InvWrapper wrapper = new InvWrapper(shelf);

					int slot = this.getSlot(state.getValue(FACING), hitX, hitY, hitZ);

					if(!heldItem.isEmpty()) {
						ItemStack result = wrapper.insertItem(slot, heldItem, true);
						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							result = wrapper.insertItem(slot, heldItem.copy(), false);
							world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
							if(!player.isCreative()) {
								player.setHeldItem(hand, result);
							}
							world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_PLACE, SoundCategory.BLOCKS, 1, 1);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	protected int getSlot(EnumFacing blockDir, float hitX, float hitY, float hitZ) {
		float cx, cy;

		Vec3i up = new Vec3i(0, 1, 0);
		Vec3i dir = up.crossProduct(blockDir.getDirectionVec());

		cx = dir.getX() * hitX + dir.getZ() * hitZ;
		cy = hitY;
		
		if(cx <= 0.0D) {
			cx = cx + 1;
		}

		int slot = 0;

		if(cx >= 0.0D && cx <= 0.5D) {
			slot++;
		}

		if(cy >= 0.0D && cy <= 0.5D) {
			slot += 2;
		}

		return slot;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityItemShelf();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
