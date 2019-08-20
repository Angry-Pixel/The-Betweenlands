package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.IConnectedTextureBlock;

public class BlockDiagonalEnergyBarrier extends Block implements IConnectedTextureBlock {
	public static final PropertyBool FLIPPED = PropertyBool.create("flipped");

	public BlockDiagonalEnergyBarrier() {
		super(Material.GLASS);
		setDefaultState(getBlockState().getBaseState().withProperty(FLIPPED, false));
		setSoundType(SoundType.GLASS);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setLightLevel(0.8F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
    public boolean causesSuffocation(IBlockState state) {
    	return false;
    }

	@Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

	@Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FLIPPED, Boolean.valueOf(meta > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FLIPPED) ? 1 : 0;
	}
	
	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FLIPPED, getFacingFromEntity(pos, placer));
	}

	public static boolean getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
		EnumFacing facing = entity.getHorizontalFacing();
		if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
			return true;
		return false;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { FLIPPED }, new IUnlistedProperty[0]));
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final boolean flipped = state.getValue(FLIPPED);
		return this.getExtendedConnectedTextureState((IExtendedBlockState) state, world, pos, p -> {
			int xzSteps = Math.abs(p.getX() - pos.getX()) + Math.abs(p.getZ() - pos.getZ());
			IBlockState otherState = world.getBlockState(p);
			
			//Only connect up/down or diagonals
			if((p.getY() != pos.getY() && xzSteps == 0) || xzSteps > 1) {
				return otherState.getBlock() instanceof BlockDiagonalEnergyBarrier && otherState.getValue(FLIPPED) == flipped;
			}
			
			return false;
		}, false);
	}

    public static final AxisAlignedBB CORNER_NW_AABB = new AxisAlignedBB(0D, 0D, 0D, 0.25D, 1D, 0.25D);
    public static final AxisAlignedBB CORNER_SW_AABB = new AxisAlignedBB(0D, 0D, 0.75D, 0.25D, 1D, 1D);
    public static final AxisAlignedBB CORNER_NE_AABB = new AxisAlignedBB(0.75D, 0D, 0D, 1D, 1D, 0.25D);
    public static final AxisAlignedBB CORNER_SE_AABB = new AxisAlignedBB(0.75D, 0D, 0.75D, 1D, 1D, 1D);
    
    public static final AxisAlignedBB MID_NW_AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 0.5D, 1D, 0.5D);
    public static final AxisAlignedBB MID_SW_AABB = new AxisAlignedBB(0.25D, 0D, 0.5D, 0.5D, 1D, 0.75D);
    public static final AxisAlignedBB MID_NE_AABB = new AxisAlignedBB(0.5D, 0D, 0.25D, 0.75D, 1D, 0.5D);
    public static final AxisAlignedBB MID_SE_AABB = new AxisAlignedBB(0.5D, 0D, 0.5D, 0.75D, 1D, 0.75D);

    @Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
			state = state.getActualState(world, pos);

		if (state.getValue(FLIPPED)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SE_AABB);
		}

		if (!state.getValue(FLIPPED)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NE_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SW_AABB);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SW_AABB);
		}
	}
    
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
    	RayTraceResult result = super.collisionRayTrace(blockState, worldIn, pos, start, end);
    	
    	if(result != null) {
    		//Got intersection with full AABB, now check for intersection with
    		//plane
    		
    		Vec3d diff = end.subtract(start);
    		Vec3d dir = diff.normalize();
    		
    		Vec3d p0 = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    		Vec3d n = blockState.getValue(FLIPPED) ? new Vec3d(0.70710678118D, 0, -0.70710678118D) : new Vec3d(0.70710678118D, 0, 0.70710678118D);
    		
    		double d = p0.subtract(start).dotProduct(n) / (dir.dotProduct(n));
    		
    		Vec3d intercept = start.add(dir.scale(d));
    		
    		if(intercept.x >= pos.getX() && intercept.x <= pos.getX() + 1 &&
    				intercept.y >= pos.getY() && intercept.y <= pos.getY() + 1 &&
    				intercept.z >= pos.getZ() && intercept.z <= pos.getZ() + 1) {
    			return new RayTraceResult(intercept, result.sideHit, result.getBlockPos());
    		}
    	}
    	
    	return null;
    }
    
	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		/*if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.isSpectator()) {
				entity.attackEntityFrom(DamageSource.MAGIC, 1);
				double dx = (entity.posX - (pos.getX())) * 2 - 1;
				double dz = (entity.posZ - (pos.getZ())) * 2 - 1;
				if (Math.abs(dx) > Math.abs(dz))
					dz = 0;
				else
					dx = 0;
				dx = (int) dx;
				dz = (int) dz;
				entity.addVelocity(dx * 0.85D, 0.08D, dz * 0.85D);
				entity.playSound(SoundRegistry.REJECTED, 0.5F, 1F);
			}
		}*/
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}