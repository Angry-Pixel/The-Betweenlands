package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class BlockSwampWater extends BlockFluidClassic implements IStateMappedBlock, ITintedBlock {
	private boolean isUnderwaterBlock = false;

	public BlockSwampWater(Fluid fluid, Material material) {
		super(fluid, material);
	}

	public BlockSwampWater setUnderwaterBlock(boolean underwaterBlock) {
		this.isUnderwaterBlock = underwaterBlock;
		return this;
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos)
	{
		if (world.isAirBlock(pos)) return true;

		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof BlockSwampWater)
		{
			return false;
		}

		if (displacements.containsKey(state.getBlock()))
		{
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL)
		{
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE)
		{
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos)
	{
		if(world instanceof World && !((World)world).isBlockLoaded(pos)) return false;
		
		if (world.isAirBlock(pos))
		{
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof BlockSwampWater)
		{
			return false;
		}

		if (displacements.containsKey(block))
		{
			if (displacements.get(block))
			{
				block.dropBlockAsItem(world, pos, state, 0);
				return true;
			}
			return false;
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL)
		{
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE)
		{
			block.dropBlockAsItem(world, pos, state, 0);
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public float getFluidHeightForRender(IBlockAccess world, BlockPos pos)
	{
		IBlockState here = world.getBlockState(pos);
		IBlockState up = world.getBlockState(pos.down(densityDir));
		if (here.getBlock() instanceof BlockSwampWater)
		{
			if (up.getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock)
			{
				return 1;
			}

			if (here.getValue(LEVEL) == getMaxRenderHeightMeta())
			{
				return 0.875F;
			}
		}
		return !here.getMaterial().isSolid() && up.getBlock() instanceof BlockSwampWater ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

	@Override
	public Vec3d getFlowVector(IBlockAccess world, BlockPos pos)
	{
		Vec3d vec = new Vec3d(0.0D, 0.0D, 0.0D);
		int decay = quantaPerBlock - getQuantaValue(world, pos);

		for (int side = 0; side < 4; ++side)
		{
			int x2 = pos.getX();
			int z2 = pos.getZ();

			switch (side)
			{
			case 0: --x2; break;
			case 1: --z2; break;
			case 2: ++x2; break;
			case 3: ++z2; break;
			}

			BlockPos pos2 = new BlockPos(x2, pos.getY(), z2);
			IBlockState blockState1 = world.getBlockState(pos);
			IBlockState blockState2 = world.getBlockState(pos2);
			boolean isUnderwaterBlock = blockState1.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)blockState1.getBlock()).isUnderwaterBlock;
			boolean isOtherUnderwaterBlock = blockState2.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)blockState2.getBlock()).isUnderwaterBlock;
			if(!isUnderwaterBlock && !isOtherUnderwaterBlock) {
				int otherDecay = quantaPerBlock - getQuantaValue(world, pos2);
				if (otherDecay >= quantaPerBlock)
				{
					if (!world.getBlockState(pos2).getMaterial().blocksMovement())
					{
						otherDecay = quantaPerBlock - getQuantaValue(world, pos2.down());
						if (otherDecay >= 0)
						{
							int power = otherDecay - (decay - quantaPerBlock);
							vec = vec.addVector((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
						}
					}
				}
				else if (otherDecay >= 0)
				{
					int power = otherDecay - decay;
					vec = vec.addVector((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
				}
			}
		}

		if (world.getBlockState(pos.up()).getBlock() instanceof BlockSwampWater)
		{
			boolean flag =
					causesDownwardCurrent(world, pos.add( 0,  0, -1), EnumFacing.NORTH) ||
					causesDownwardCurrent(world, pos.add( 0,  0,  1), EnumFacing.SOUTH) ||
					causesDownwardCurrent(world, pos.add(-1,  0,  0), EnumFacing.WEST) ||
					causesDownwardCurrent(world, pos.add( 1,  0,  0), EnumFacing.EAST) ||
					causesDownwardCurrent(world, pos.add( 0,  1, -1), EnumFacing.NORTH) ||
					causesDownwardCurrent(world, pos.add( 0,  1,  1), EnumFacing.SOUTH) ||
					causesDownwardCurrent(world, pos.add(-1,  1,  0), EnumFacing.WEST) ||
					causesDownwardCurrent(world, pos.add( 1,  1,  0), EnumFacing.EAST);

			if (flag)
			{
				vec = vec.normalize().addVector(0.0D, -6.0D, 0.0D);
			}
		}
		vec = vec.normalize();
		return vec;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos)
	{
		IExtendedBlockState state = (IExtendedBlockState)oldState;
		state = state.withProperty(FLOW_DIRECTION, (float)getFlowDirection(worldIn, pos));
		float[][] height = new float[3][3];
		float[][] corner = new float[2][2];
		height[1][1] = getFluidHeightForRender(worldIn, pos);
		if(height[1][1] == 1)
		{
			for(int i = 0; i < 2; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					corner[i][j] = 1;
				}
			}
		}
		else
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					if(i != 1 || j != 1)
					{
						height[i][j] = getFluidHeightForRender(worldIn, pos.add(i - 1, 0, j - 1));
					}
				}
			}
			for(int i = 0; i < 2; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					corner[i][j] = getFluidHeightAverage(height[i][j], height[i][j + 1], height[i + 1][j], height[i + 1][j + 1]);
				}
			}
		}
		state = state.withProperty(LEVEL_CORNERS[0], corner[0][0]);
		state = state.withProperty(LEVEL_CORNERS[1], corner[0][1]);
		state = state.withProperty(LEVEL_CORNERS[2], corner[1][1]);
		state = state.withProperty(LEVEL_CORNERS[3], corner[1][0]);
		return state;
	}

	@Override
	public int getQuantaValue(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Block blockHere = blockState.getBlock();
		if(blockHere instanceof BlockSwampWater == false || !((BlockSwampWater)blockHere).isUnderwaterBlock)
			return super.getQuantaValue(world, pos);
		return this.quantaPerBlock;
	}

	@Override
	public boolean isSourceBlock(IBlockAccess world, BlockPos pos) {
		return super.isSourceBlock(world, pos);
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, BlockPos pos)
	{
		if(world instanceof World && !((World)world).isBlockLoaded(pos)) return false;
		
		if (world.isAirBlock(pos)) return true;

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockSwampWater)
		{
			return true;
		}

		if (displacements.containsKey(state.getBlock()))
		{
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement()  ||
				material == Material.WATER ||
				material == Material.LAVA  ||
				material == Material.PORTAL)
		{
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE)
		{
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected void flowIntoBlock(World world, BlockPos pos, int meta) {
		if (meta < 0) return;
		if (displaceIfPossible(world, pos))
		{
			world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getBlockState().getBaseState().withProperty(LEVEL, meta), 3);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);

		//Replenishing source
		if(quantaRemaining < quantaPerBlock && !world.isAirBlock(pos.down())) {
			int adjacentSources = 0;
			if(this.isSourceBlock(world, pos.east())) adjacentSources++;
			if(this.isSourceBlock(world, pos.north())) adjacentSources++;
			if(this.isSourceBlock(world, pos.south())) adjacentSources++;
			if(this.isSourceBlock(world, pos.west())) adjacentSources++;
			if(adjacentSources >= 2) {
				world.setBlockState(pos, state.withProperty(LEVEL, 0), 2);
				quantaRemaining = quantaPerBlock;
			}
		}

		int expQuanta = -101;

		if(!(state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)state.getBlock()).isUnderwaterBlock)) {
			// check adjacent block levels if non-source
			if (quantaRemaining < quantaPerBlock)
			{
				if (world.getBlockState(pos.add( 0, -densityDir,  0)).getBlock() == this ||
						world.getBlockState(pos.add(-1, -densityDir,  0)).getBlock() == this ||
						world.getBlockState(pos.add( 1, -densityDir,  0)).getBlock() == this ||
						world.getBlockState(pos.add( 0, -densityDir, -1)).getBlock() == this ||
						world.getBlockState(pos.add( 0, -densityDir,  1)).getBlock() == this)
				{
					expQuanta = quantaPerBlock - 1;
				}
				else
				{
					int maxQuanta = -100;
					maxQuanta = getLargerQuanta(world, pos.add(-1, 0,  0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add( 1, 0,  0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add( 0, 0, -1), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add( 0, 0,  1), maxQuanta);

					expQuanta = maxQuanta - 1;
				}

				// decay calculation
				if (expQuanta != quantaRemaining)
				{
					quantaRemaining = expQuanta;

					if (expQuanta <= 0)
					{
						world.setBlockToAir(pos);
					}
					else
					{
						world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), 2);
						world.scheduleUpdate(pos, this, tickRate);
						world.notifyNeighborsOfStateChange(pos, this, false);
					}
				}
			}
			// This is a "source" block, set meta to zero, and send a server only update
			else if (quantaRemaining >= quantaPerBlock)
			{
				world.setBlockState(pos, this.getDefaultState(), 2);
			}
		}

		// Flow vertically if possible
		if (canDisplace(world, pos.up(densityDir)))
		{
			flowIntoBlock(world, pos.up(densityDir), 1);
			return;
		}

		// Flow outward if possible
		int flowMeta = quantaPerBlock - quantaRemaining + 1;
		if (flowMeta >= quantaPerBlock)
		{
			return;
		}

		if (isSourceBlock(world, pos) || !isFlowingVertically(world, pos))
		{
			if (world.getBlockState(pos.down(densityDir)).getBlock() == this)
			{
				flowMeta = 1;
			}
			boolean flowTo[] = getOptimalFlowDirections(world, pos);

			if (flowTo[0]) flowIntoBlock(world, pos.add(-1, 0,  0), flowMeta);
			if (flowTo[1]) flowIntoBlock(world, pos.add( 1, 0,  0), flowMeta);
			if (flowTo[2]) flowIntoBlock(world, pos.add( 0, 0, -1), flowMeta);
			if (flowTo[3]) flowIntoBlock(world, pos.add( 0, 0,  1), flowMeta);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		if(state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)state.getBlock()).isUnderwaterBlock)
			return state.getBoundingBox(worldIn, pos).offset(pos);
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		if(blockState.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)blockState.getBlock()).isUnderwaterBlock)
			return blockState.getBoundingBox(worldIn, pos);
		return null;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean fullHit) {
		if(state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)state.getBlock()).isUnderwaterBlock)
			return true;
		return super.canCollideCheck(state, fullHit);
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		if(state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater)state.getBlock()).isUnderwaterBlock)
			return false;
		return super.isReplaceable(worldIn, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockSwampWater.LEVEL);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && (!this.isUnderwaterBlock || worldIn.getBlockState(pos).getMaterial() == Material.WATER);
	}

	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		if(worldIn == null || pos == null || tintIndex != 0) {
			return -1;
		}

		int r = 0;
		int g = 0;
		int b = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				int colorMultiplier = worldIn.getBiome(pos.add(dx, 0, dz)).getWaterColorMultiplier();
				r += (colorMultiplier & 0xFF0000) >> 16; g += (colorMultiplier & 0x00FF00) >> 8; b += colorMultiplier & 0x0000FF;
			}
		}
		r /= 9;
		g /= 9;
		b /= 9;
		float depth = 0;
		if (pos.getY() > WorldProviderBetweenlands.CAVE_START) {
			depth = 1;
		} else {
			if (pos.getY() < WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
				depth = 0;
			} else {
				depth = (pos.getY() - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) / (float) (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT);
			}
		}
		r = (int) (r * depth + DEEP_COLOR_R * (1 - depth) + 0.5F);	
		g = (int) (g * depth + DEEP_COLOR_G * (1 - depth) + 0.5F);
		b = (int) (b * depth + DEEP_COLOR_B * (1 - depth) + 0.5F);
		return r << 16 | g << 8 | b;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(rand.nextInt(1500) == 0) {
			if(world.getBlockState(pos.up(2)).getMaterial().isLiquid()) {
				BLParticles.FISH.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else if(world.getBlockState(pos.down()).getBlock() == BlockRegistry.MUD) {
				if(rand.nextInt(2) == 0) {
					BLParticles.MOSQUITO.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				} else {
					BLParticles.FLY.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				}
			}
		}
	}
}