package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.tools.ISickleHarvestable;

public class BlockWeedwoodBush extends Block implements /*IShearable,*/ ISickleHarvestable {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyIntegerUnlisted POS_X = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Y = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Z = new PropertyIntegerUnlisted("pos_z");

	public BlockWeedwoodBush() {
		super(Material.PLANTS);

		this.setHardness(0.35F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false))
				.withProperty(UP, Boolean.valueOf(false))
				.withProperty(DOWN, Boolean.valueOf(false)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, UP, DOWN}, new IUnlistedProperty[]{POS_X, POS_Y, POS_Z});
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		return ImmutableList.of(EnumItemMisc.WEEDWOOD_STICK.create(1));
	}

	//Not quite sure whether this block should be obtainable without shears or not, I'll just leave this here for now
	/*@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}*/

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state
				.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north())))
				.withProperty(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east())))
				.withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south())))
				.withProperty(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())))
				.withProperty(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.up())))
				.withProperty(DOWN, Boolean.valueOf(this.canConnectTo(worldIn, pos.down())));
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block == this;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState)oldState;
		return state.withProperty(POS_X, pos.getX()).withProperty(POS_Y, pos.getY()).withProperty(POS_Z, pos.getZ());
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
