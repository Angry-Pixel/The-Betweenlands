package thebetweenlands.common.block.misc;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.item.misc.ItemGlowingGoop;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockGlowingGoopUnderwater extends BlockSwampWater {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockGlowingGoopUnderwater() {
		super(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setUnderwaterBlock(true);
		setDefaultState(this.getBlockState().getBaseState());
		setLightLevel(1.0F);
		setHardness(0.025F);
		setResistance(2.0F);
		setSoundType(SoundType.SLIME);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	@Nullable
	public ItemBlock getItemBlock() {
		return null;
	}

	@Override
	public int quantityDropped(Random rng) {
		return 1;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockRegistry.GLOWING_GOOP);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		float widthMin = 0, heightMin = 0, depthMin = 0;
		float widthMax = 0, heightMax = 0, depthMax = 0;
		switch (state.getValue(FACING)) {
		case UP:
			widthMin = 0.125F;
			heightMin = 0F;
			depthMin = 0.125F;
			widthMax = 0.125F;
			heightMax = 0.75F;
			depthMax = 0.125F;
			break;
		case DOWN:
			widthMin = 0.125F;
			heightMin = 0.75F;
			depthMin = 0.125F;
			widthMax = 0.125F;
			heightMax = 0F;
			depthMax = 0.125F;
			break;
		case NORTH:
			widthMin = 0.125F;
			heightMin = 0.125F;
			depthMin = 0.75F;
			widthMax = 0.125F;
			heightMax = 0.125F;
			depthMax = 0F;
			break;
		case WEST:
			widthMin = 0.75F;
			heightMin = 0.125F;
			depthMin = 0.125F;
			widthMax = 0F;
			heightMax = 0.125F;
			depthMax = 0.125F;
			break;
		case SOUTH:
			widthMin = 0.125F;
			heightMin = 0.125F;
			depthMin = 0F;
			widthMax = 0.125F;
			heightMax = 0.125F;
			depthMax = 0.75F;
			break;
		case EAST:
			widthMin = 0F;
			heightMin = 0.125F;
			depthMin = 0.125F;
			widthMax = 0.75F;
			heightMax = 0.125F;
			depthMax = 0.125F;
			break;
		}
		return new AxisAlignedBB(0F + widthMin, 0F + heightMin, 0F + depthMin, 1F - widthMax, 1F - heightMax, 1F - depthMax);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return canPlaceBlock(world, pos, side);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		for (EnumFacing enumfacing : EnumFacing.values())
			if (canPlaceBlock(world, pos, enumfacing))
				return true;
		return false;
	}

	public boolean canPlaceBlock(World world, BlockPos pos, EnumFacing direction) {
		BlockPos blockpos = pos.offset(direction.getOpposite());
		IBlockState state = world.getBlockState(blockpos);
		boolean isSolid = state.getBlockFaceShape(world, blockpos, direction) == BlockFaceShape.SOLID;

		return world.isBlockNormalCube(blockpos, true) && state.isOpaqueCube() && isSolid;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), new IProperty[] { FACING }, new IUnlistedProperty[0]);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		super.neighborChanged(state, world, fromPos, block, fromPos);

		boolean shouldStay = false;

		if (world.isSideSolid(pos.offset(state.getValue(FACING).getOpposite()), state.getValue(FACING)))
			shouldStay = true;

		if (!shouldStay) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState(), world.isRemote ? 11 : 3);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.GLOWING_GOOP);
	}
}
