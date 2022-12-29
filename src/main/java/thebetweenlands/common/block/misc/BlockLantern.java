package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockLantern extends Block {
	private static final AxisAlignedBB AABB_LARGE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
	private static final AxisAlignedBB AABB_SMALL = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.6D, 0.75D);
	
	public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 7);
	public static final PropertyBool HANGING = PropertyBool.create("hanging");

	public BlockLantern() {
		this(Material.CLOTH, SoundType.CLOTH);
	}

	protected BlockLantern(Material material, SoundType soundType) {
		super(material);
		this.setHardness(0.1F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(soundType);
		this.setLightLevel(1.0f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0).withProperty(HANGING, false));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(ROTATION, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ROTATION);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(ROTATION, rot.rotate(state.getValue(ROTATION), 8));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withProperty(ROTATION, mirrorIn.mirrorRotation(state.getValue(ROTATION), 8));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { ROTATION, HANGING });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(HANGING, worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false) ||
				worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN) != BlockFaceShape.UNDEFINED ||
				worldIn.getBlockState(pos.up()).getBlock() instanceof BlockRope);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return this.getActualState(state, source, pos).getValue(HANGING) ? AABB_LARGE : AABB_SMALL;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(((placer.rotationYaw + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;

		if(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockRope) {
			rotation -= rotation % 2;
		}

		worldIn.setBlockState(pos, state.withProperty(ROTATION, rotation), 11);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) &&
				(worldIn.isSideSolid(pos.up(), EnumFacing.DOWN) || worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN) != BlockFaceShape.UNDEFINED ||
				worldIn.isSideSolid(pos.down(), EnumFacing.UP) || worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) != BlockFaceShape.UNDEFINED ||
				worldIn.getBlockState(pos.up()).getBlock() instanceof BlockRope);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isSideSolid(pos.up(), EnumFacing.DOWN) && worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN) == BlockFaceShape.UNDEFINED &&
				!worldIn.isSideSolid(pos.down(), EnumFacing.UP) && worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.UNDEFINED &&
				!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockRope)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double px = (double)pos.getX() + 0.5D;
		double py = (double)pos.getY() + 0.3D;
		double pz = (double)pos.getZ() + 0.5D;

		if(worldIn.rand.nextInt(20) == 0 && worldIn.canBlockSeeSky(pos)) {
			int particle = rand.nextInt(2);
			switch(particle) {
			default:
			case 0:
				BLParticles.FLY.spawn(worldIn, px, py, pz);
				break;
			case 1:
				BLParticles.MOTH.spawn(worldIn, px, py, pz);
				break;
			}
		}
	}
}
