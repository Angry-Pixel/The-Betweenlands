package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockPlantUnderwater extends BlockSwampWater implements net.minecraftforge.common.IPlantable, IStateMappedBlock, IShearable, ISickleHarvestable {
	protected static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

	protected ItemStack sickleHarvestableDrop;
	protected boolean isReplaceable = false;

	public BlockPlantUnderwater() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setHardness(0.5F);
	}

	public BlockPlantUnderwater(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	public BlockPlantUnderwater setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}

	public BlockPlantUnderwater setReplaceable(boolean replaceable) {
		this.isReplaceable = replaceable;
		return this;
	}

	protected IBlockState getReplacementBlock(IBlockAccess world, BlockPos pos, IBlockState state) {
		return BlockRegistry.SWAMP_WATER.getDefaultState();
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return this.isReplaceable;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PLANT_AABB;
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return net.minecraftforge.common.EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) return getDefaultState();
		return state;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		super.neighborChanged(state, worldIn, pos, blockIn);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, this.getReplacementBlock(worldIn, pos, state), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
		{
			IBlockState soil = worldIn.getBlockState(pos.down());
			return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) || 
					this.canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
		}
		return this.canSustainPlant(worldIn.getBlockState(pos.down()));
	}

	protected boolean canSustainPlant(IBlockState state) {
		return SoilHelper.canSustainUnderwaterPlant(state);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, this.getReplacementBlock(world, pos, state), world.isRemote ? 11 : 3);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos).getMaterial() == Material.WATER && 
				(soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this) || this.canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this));
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
		return super.canSustainPlant(state, world, pos, direction, plantable) || (plantable instanceof BlockPlantUnderwater && ((BlockPlantUnderwater)plantable).canSustainPlant(state));
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
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}
}
