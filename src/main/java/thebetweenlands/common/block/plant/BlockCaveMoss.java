package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockCaveMoss extends BlockBush implements ISickleHarvestable, IShearable, IStateMappedBlock {
	public static final BooleanProperty CAN_GROW = BooleanProperty.create("can_grow");
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	public static final BooleanProperty IS_BOTTOM = BooleanProperty.create("is_bottom");
	public static final AxisAlignedBB CAVE_MOSS_AABB = new AxisAlignedBB(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);

	public BlockCaveMoss() {
		super(Material.PLANTS);
		setTickRandomly(true);
		setHardness(0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().with(IS_TOP, true).with(IS_BOTTOM, false).with(CAN_GROW, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{IS_TOP, IS_BOTTOM, CAN_GROW});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
		boolean isTop = worldIn.getBlockState(pos.up()).getBlock() != this;
		boolean isBottom = worldIn.getBlockState(pos.down()).getBlock() != this;
		return state.with(IS_TOP, isTop).with(IS_BOTTOM, isBottom);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		return CAVE_MOSS_AABB;
	}

	@Override
	public boolean isLadder(IBlockState state, IWorldReader world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return isValidBlock(world.getBlockState(pos.up())) && canBlockStay(world, pos, world.getBlockState(pos));
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return isValidBlock(worldIn.getBlockState(pos.up()));
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state,  World world, BlockPos pos, int fortune) {
		return Items.AIR;
	}

	@Override
	public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
		return 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!isValidBlock(worldIn.getBlockState(pos.up()))) {
			worldIn.removeBlock(pos);
		}
	}

	@Override
	public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
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

	protected boolean isValidBlock(IBlockState blockState) {
		return SurfaceType.UNDERGROUND.matches(blockState) || blockState.getBlock() == this;
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.get(CAN_GROW) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().with(CAN_GROW, true);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(CAN_GROW, meta == 1);
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(CAN_GROW);
	}
	
	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(16) == 0 && state.get(CAN_GROW) && worldIn.isAirBlock(pos.down())) {
			worldIn.setBlockState(pos.down(), this.getDefaultState());
		}
	}
}
