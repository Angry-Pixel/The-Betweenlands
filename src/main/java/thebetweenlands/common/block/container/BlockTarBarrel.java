package thebetweenlands.common.block.container;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.misc.ItemTarBarrel;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.tile.TileEntityTarBarrel;

public class BlockTarBarrel extends BasicBlock implements ITileEntityProvider, ICustomItemBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockTarBarrel() {
		super(Material.WOOD);
		setSoundType(SoundType.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);

		if(world.getTileEntity(pos) instanceof TileEntityTarBarrel) {
			TileEntityTarBarrel tile = (TileEntityTarBarrel) world.getTileEntity(pos);

			if(player.isSneaking()) {
				return false;
			}

			if(!heldItem.isEmpty()) {
				IFluidHandler handler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if(handler != null) {
					IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					if(playerInventory != null) {
						FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isRemote);

						if(fluidActionResult.isSuccess()) {
							if(!world.isRemote) {
								player.setHeldItem(hand, fluidActionResult.getResult());
							}
							return true;
						} else {
							fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isRemote);
							if(fluidActionResult.isSuccess()) {
								if(!world.isRemote) {
									player.setHeldItem(hand, fluidActionResult.getResult());
								}
								return true;
							}
						}
					}
				}
			}

			if(!world.isRemote && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_TAR_BARREL, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return true;
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
		return new TileEntityTarBarrel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemTarBarrel(this);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(!worldIn.isRemote && !player.isCreative() && worldIn.getGameRules().getBoolean("doTileDrops")) {
			TileEntity te = worldIn.getTileEntity(pos);

			if(te instanceof TileEntityTarBarrel) {
				Item item = Item.getItemFromBlock(worldIn.getBlockState(pos).getBlock());
				if(item instanceof ItemTarBarrel) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ((ItemTarBarrel) item).fromBarrel((TileEntityTarBarrel) te));
				}
			}
		}
	}
}