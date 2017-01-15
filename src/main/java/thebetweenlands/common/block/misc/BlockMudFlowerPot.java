package thebetweenlands.common.block.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.plant.BlockPlant;
import thebetweenlands.common.block.plant.BlockPlantUnderwater;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.common.block.property.PropertyBlockStateUnlisted;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;

public class BlockMudFlowerPot extends BlockContainer {
	protected static final AxisAlignedBB FLOWER_POT_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

	public static final PropertyBlockStateUnlisted FLOWER = new PropertyBlockStateUnlisted("flower");

	public BlockMudFlowerPot() {
		super(Material.CIRCUITS);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.STONE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FLOWER_POT_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{ FLOWER });
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = ((IExtendedBlockState)state).withProperty(FLOWER, Blocks.AIR.getDefaultState());

		TileEntity te = world.getTileEntity(pos);

		if(te != null && te instanceof TileEntityMudFlowerPot) {
			TileEntityMudFlowerPot flowerPot = (TileEntityMudFlowerPot) te;

			if(flowerPot.getFlowerPotItem() instanceof ItemBlock) {
				Block flowerBlock = ((ItemBlock)flowerPot.getFlowerPotItem()).getBlock();

				if(flowerBlock != null) {
					@SuppressWarnings("deprecation")
					IBlockState blockState = flowerBlock.getStateFromMeta(flowerPot.getFlowerPotData());
					state = ((IExtendedBlockState)state).withProperty(FLOWER, blockState);
				}
			}
		}
		return state;
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (heldItem != null && heldItem.getItem() instanceof ItemBlock)
		{
			TileEntityMudFlowerPot te = this.getTileEntity(worldIn, pos);

			if (te == null)
			{
				return false;
			}
			else if (te.getFlowerPotItem() != null)
			{
				return false;
			}
			else
			{
				Block block = Block.getBlockFromItem(heldItem.getItem());

				if (!this.canContain(block, heldItem.getMetadata()))
				{
					return false;
				}
				else
				{
					te.setFlowerPotData(heldItem.getItem(), heldItem.getMetadata());
					te.markDirty();
					worldIn.notifyBlockUpdate(pos, state, state, 3);
					playerIn.addStat(StatList.FLOWER_POTTED);

					if (!playerIn.capabilities.isCreativeMode)
					{
						--heldItem.stackSize;
					}

					return true;
				}
			}
		}
		else
		{
			return false;
		}
	}

	private boolean canContain(@Nullable Block blockIn, int meta) {
		if(blockIn == Blocks.YELLOW_FLOWER || blockIn == Blocks.RED_FLOWER || 
				blockIn == Blocks.CACTUS || blockIn == Blocks.BROWN_MUSHROOM || 
				blockIn == Blocks.RED_MUSHROOM || blockIn == Blocks.SAPLING || 
				blockIn == Blocks.DEADBUSH ||
				(blockIn == Blocks.TALLGRASS && meta == BlockTallGrass.EnumType.FERN.getMeta())
				|| (blockIn instanceof BlockPlant && blockIn instanceof BlockPlantUnderwater == false && blockIn instanceof BlockStackablePlant == false)) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityMudFlowerPot te = this.getTileEntity(worldIn, pos);

		if (te != null) {
			ItemStack itemstack = te.getFlowerItemStack();

			if (itemstack != null) {
				return itemstack;
			}
		}

		return new ItemStack(BlockRegistry.MUD_FLOWER_POT);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);

		if (player.capabilities.isCreativeMode) {
			TileEntityMudFlowerPot te = this.getTileEntity(worldIn, pos);

			if (te != null) {
				te.setFlowerPotData((Item)null, 0);
			}
		}
	}

	@Nullable
	private TileEntityMudFlowerPot getTileEntity(World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof TileEntityMudFlowerPot ? (TileEntityMudFlowerPot) tileentity : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/*============================FORGE START=====================================*/
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
		TileEntityMudFlowerPot te = world.getTileEntity(pos) instanceof TileEntityMudFlowerPot ? (TileEntityMudFlowerPot) world.getTileEntity(pos) : null;
		if (te != null && te.getFlowerPotItem() != null)
			ret.add(new ItemStack(te.getFlowerPotItem(), 1, te.getFlowerPotData()));
		return ret;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (willHarvest)
			return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool) {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}
	/*===========================FORGE END==========================================*/

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMudFlowerPot();
	}
}