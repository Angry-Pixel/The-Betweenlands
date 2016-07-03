package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPitcherPlant extends BlockBush implements IGrowable, net.minecraftforge.common.IShearable
{
	protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public BlockPitcherPlant()
	{
		super(Material.VINE);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return TALL_GRASS_AABB;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		return super.canBlockStay(worldIn, pos, state);
	}

	/**
	 * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
	 */
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	/**
	 * Get the quantity dropped based on the given fortune level
	 */
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		return 1 + random.nextInt(fortune * 2 + 1);
	}

	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack)
	{
		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this, 1, 0);
	}

	/**
	 * Whether this IGrowable can grow
	 */
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		return true;
	}

	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.GRASS;

		if (Blocks.DOUBLE_PLANT.canPlaceBlockAt(worldIn, pos))
		{
			Blocks.DOUBLE_PLANT.placeAt(worldIn, pos, blockdoubleplant$enumplanttype, 2);
		}
	}

	/**
	 * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
	 */
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.XYZ;
	}

	@Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
	{
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		ret.add(new ItemStack(Blocks.TALLGRASS, 1, 0));
		return ret;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		if (RANDOM.nextInt(8) != 0) return ret;
		ItemStack seed = net.minecraftforge.common.ForgeHooks.getGrassSeed(RANDOM, fortune);
		if (seed != null) ret.add(seed);
		return ret;
	}
}