package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockHangingPlant extends BlockBush implements ISickleHarvestable, IShearable, IStateMappedBlock {
	public static final PropertyBool CAN_GROW = PropertyBool.create("can_grow");
	public static final PropertyBool IS_TOP = PropertyBool.create("is_top");
	public static final PropertyBool IS_BOTTOM = PropertyBool.create("is_bottom");
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);

	public BlockHangingPlant(Material material) {
		super(material);
		setTickRandomly(true);
		setHardness(0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, true).withProperty(IS_BOTTOM, false).withProperty(CAN_GROW, true));
	}

	public BlockHangingPlant() {
		this(Material.PLANTS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{IS_TOP, IS_BOTTOM, CAN_GROW});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean isTop = worldIn.getBlockState(pos.up()).getBlock() != this;
		boolean isBottom = worldIn.getBlockState(pos.down()).getBlock() != this;
		System.out.println(isBottom);
		return state.withProperty(IS_TOP, isTop).withProperty(IS_BOTTOM, isBottom);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return isValidBlock(world, pos.up(), world.getBlockState(pos.up())) && canBlockStay(world, pos, world.getBlockState(pos));
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return isValidBlock(worldIn, pos.up(), worldIn.getBlockState(pos.up()));
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!isValidBlock(worldIn, pos.up(), worldIn.getBlockState(pos.up()))) {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = rand.nextFloat() - 0.5F;
			float py = rand.nextFloat();
			float pz = rand.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			BLParticles.CAVE_WATER_DRIP.spawn(worldIn, pos.getX() + px, pos.getY() + py, pos.getZ() + pz);
		}
	}

	protected boolean isValidBlock(World world, BlockPos pos, IBlockState blockState) {
		return blockState.isSideSolid(world, pos, EnumFacing.DOWN) || blockState.getBlock() == this;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(CAN_GROW) ? 1 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(CAN_GROW, true);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CAN_GROW, meta == 1);
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(CAN_GROW);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(rand.nextInt(16) == 0 && state.getValue(CAN_GROW) && worldIn.isAirBlock(pos.down())) {
			worldIn.setBlockState(pos.down(), this.getDefaultState());
		}
	}
}
