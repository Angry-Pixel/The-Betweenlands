package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.item.misc.ItemFallenLeaves;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockFallenLeaves extends BlockBush implements IShearable, ISickleHarvestable, ICustomItemBlock {
	public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 4);
	
	private static final AxisAlignedBB BOUNDS[] = new AxisAlignedBB[] { new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F), new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F), new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F), new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F) };

	private String type;

	public BlockFallenLeaves(String blockName) {
		this.setHardness(0.1F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
		this.type = blockName;
		this.setTranslationKey("thebetweenlands." + type);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LAYERS);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LAYERS) - 1;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(LAYERS, (meta & 0b11) + 1);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS[state.getValue(LAYERS) - 1];
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).isOpaqueCube();
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		return world.getBlockState(pos.down()).isOpaqueCube();
	}

	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState offsetState = blockAccess.getBlockState(pos.offset(side));
		return (offsetState.getBlock() != this || state.getValue(LAYERS) > offsetState.getValue(LAYERS)) && super.shouldSideBeRendered(state, blockAccess, pos, side);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getValue(LAYERS) == 1;
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return Collections.singletonList(new ItemStack(this, world.getBlockState(pos).getValue(LAYERS)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return Collections.singletonList(EnumItemPlantDrop.GENERIC_LEAF.create(world.getBlockState(pos).getValue(LAYERS)));
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return new ItemFallenLeaves(this);
	}
}