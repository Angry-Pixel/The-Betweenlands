package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.IWaterDisplacementHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.item.armor.ItemMarshRunnerBoots;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class BlockSwampWater extends BlockFluidClassic implements IStateMappedBlock, ITintedBlock, ICustomItemBlock {
	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;
	private boolean isUnderwaterBlock = false;

	public BlockSwampWater(Fluid fluid, Material material) {
		super(fluid, material);
	}

	public BlockSwampWater setUnderwaterBlock(boolean underwaterBlock) {
		this.isUnderwaterBlock = underwaterBlock;
		return this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if(entityIn instanceof EntityPlayer && ItemMarshRunnerBoots.checkPlayerWalkOnWater((EntityPlayer) entityIn)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 0, 1, ((float)this.getQuantaValue(worldIn, pos) / (float)this.quantaPerBlock) * 0.8F + 0.3F, 1));
				return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }
	
	@Override
    @Nonnull
    public Vec3d modifyAcceleration(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull Vec3d vec) {
		if(entity instanceof EntityPlayer && ItemMarshRunnerBoots.checkPlayerWalkOnWater((EntityPlayer) entity)) {
			return Vec3d.ZERO;
		}
		if (densityDir > 0) return vec;
        Vec3d vec_flow = this.getFlowVector(world, pos);
        return vec.add(
                vec_flow.x,
                vec_flow.y,
                vec_flow.z);
    }
	
	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.isAirBlock(pos)) return true;

		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof BlockSwampWater) {
			return false;
		}

		if (displacements.containsKey(state.getBlock())) {
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world instanceof World && !((World) world).isBlockLoaded(pos)) return false;

		if (world.isAirBlock(pos)) {
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof BlockSwampWater) {
			return false;
		}
		
		if(block instanceof IWaterDisplacementHandler && ((IWaterDisplacementHandler) block).onWaterDisplacement(world, pos, this)) {
			return false;
		}

		if (displacements.containsKey(block)) {
			if (displacements.get(block)) {
				block.dropBlockAsItem(world, pos, state, 0);
				return true;
			}
			return false;
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			block.dropBlockAsItem(world, pos, state, 0);
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float getFluidHeightForRender(IBlockAccess world, BlockPos pos, @Nonnull IBlockState up) {
		IBlockState here = world.getBlockState(pos);
		if (here.getBlock() instanceof BlockSwampWater) {
			if ((up.getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock) && (up.getBlock() instanceof BlockSwampWater == false || !((BlockSwampWater)up.getBlock()).isUnderwaterBlock)) {
				return 1;
			}

			if (here.getValue(LEVEL) == getMaxRenderHeightMeta()) {
				return 0.875F;
			}
		}
		return !here.getMaterial().isSolid() && up.getBlock() instanceof BlockSwampWater ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

	@Override
	public Vec3d getFlowVector(IBlockAccess world, BlockPos pos) {
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
			int otherDecay = quantaPerBlock - getQuantaValue(world, pos2);
			if (otherDecay >= quantaPerBlock)
			{
				if (!world.getBlockState(pos2).getMaterial().blocksMovement())
				{
					otherDecay = quantaPerBlock - getQuantaValue(world, pos2.down());
					if (otherDecay >= 0)
					{
						int power = otherDecay - (decay - quantaPerBlock);
						vec = vec.add((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
					}
				}
			}
			else if (otherDecay >= 0)
			{
				int power = otherDecay - decay;
				vec = vec.add((pos2.getX() - pos.getX()) * power, 0, (pos2.getZ() - pos.getZ()) * power);
			}
		}
		
		if (!this.isSourceBlock(world, pos) && world.getBlockState(pos.up()).getBlock() instanceof BlockSwampWater) {
            for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                if (this.causesDownwardCurrent(world, pos.offset(dir), dir) || this.causesDownwardCurrent(world, pos.offset(dir).up(), dir)) {
                	vec = vec.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }
		
		vec = vec.normalize();
		return vec;
	}

	@Override
	protected boolean causesDownwardCurrent(IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();

        if (material == this.material) {
            return false;
        } else if (side == EnumFacing.UP) {
            return true;
        } else if (material == Material.ICE) {
            return false;
        } else {
            boolean flag = isExceptBlockForAttachWithPiston(block) || block instanceof BlockStairs;
            return !flag && isBlockSolid(world, pos, side);
        }
    }
	
	@Override
	public int getQuantaValue(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater) state.getBlock()).isUnderwaterBlock) {
			return this.quantaPerBlock;
		}

		if (state.getBlock() == Blocks.AIR) {
			return 0;
		}

		if (state.getBlock() instanceof BlockSwampWater == false) {
			return -1;
		}

		int quantaRemaining = this.quantaPerBlock - state.getValue(LEVEL);
		return quantaRemaining;
	}

	@Override
	public boolean isSourceBlock(IBlockAccess world, BlockPos pos) {
		return super.isSourceBlock(world, pos);
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, BlockPos pos) {
		if (world instanceof World && !((World) world).isBlockLoaded(pos)) return false;

		if (world.isAirBlock(pos)) return true;

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockSwampWater) {
			return true;
		}

		if (displacements.containsKey(state.getBlock())) {
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() ||
				material == Material.WATER ||
				material == Material.LAVA ||
				material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void flowIntoBlock(World world, BlockPos pos, int meta) {
		if (meta < 0) return;
		if (displaceIfPossible(world, pos)) {
			world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getBlockState().getBaseState().withProperty(LEVEL, meta), 3);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);

		//Replenishing source
		if (quantaRemaining < quantaPerBlock && !world.isAirBlock(pos.down())) {
			int adjacentSources = 0;
			if (this.isSourceBlock(world, pos.east())) adjacentSources++;
			if (this.isSourceBlock(world, pos.north())) adjacentSources++;
			if (this.isSourceBlock(world, pos.south())) adjacentSources++;
			if (this.isSourceBlock(world, pos.west())) adjacentSources++;
			if (adjacentSources >= 2) {
				world.setBlockState(pos, state.withProperty(LEVEL, 0), 2);
				quantaRemaining = quantaPerBlock;
			}
		}

		int expQuanta = -101;

		if (!(state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater) state.getBlock()).isUnderwaterBlock)) {
			// check adjacent block levels if non-source
			if (quantaRemaining < quantaPerBlock) {
				if (world.getBlockState(pos.add(0, -densityDir, 0)).getBlock() == this) {
					expQuanta = quantaPerBlock - 1;
				} else {
					int maxQuanta = -100;
					maxQuanta = getLargerQuanta(world, pos.add(-1, 0, 0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add(1, 0, 0), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add(0, 0, -1), maxQuanta);
					maxQuanta = getLargerQuanta(world, pos.add(0, 0, 1), maxQuanta);

					expQuanta = maxQuanta - 1;
				}

				// decay calculation
				if (expQuanta != quantaRemaining) {
					quantaRemaining = expQuanta;

					if (expQuanta <= 0) {
						world.setBlockToAir(pos);
					} else {
						world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), 2);
						world.scheduleUpdate(pos, this, tickRate);
						world.notifyNeighborsOfStateChange(pos, this, true);
					}
				}
			}
			// This is a "source" block, set meta to zero, and send a server only update
			else if (quantaRemaining >= quantaPerBlock) {
				world.setBlockState(pos, this.getDefaultState(), 2);
			}
		}

		// Flow vertically if possible
		if (canDisplace(world, pos.up(densityDir))) {
			flowIntoBlock(world, pos.up(densityDir), 1);
			return;
		}

		// Flow outward if possible
		int flowMeta = quantaPerBlock - quantaRemaining + 1;
		if (flowMeta >= quantaPerBlock) {
			return;
		}

		if (isSourceBlock(world, pos) || !isFlowingVertically(world, pos)) {
			if (world.getBlockState(pos.down(densityDir)).getBlock() instanceof BlockSwampWater) {
				flowMeta = 1;
			}
			boolean flowTo[] = getOptimalFlowDirections(world, pos);

			if (flowTo[0]) flowIntoBlock(world, pos.add(-1, 0, 0), flowMeta);
			if (flowTo[1]) flowIntoBlock(world, pos.add(1, 0, 0), flowMeta);
			if (flowTo[2]) flowIntoBlock(world, pos.add(0, 0, -1), flowMeta);
			if (flowTo[3]) flowIntoBlock(world, pos.add(0, 0, 1), flowMeta);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		if (state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater) state.getBlock()).isUnderwaterBlock)
			return state.getBoundingBox(worldIn, pos).offset(pos);
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		if (blockState.getBlock() instanceof BlockSwampWater && ((BlockSwampWater) blockState.getBlock()).isUnderwaterBlock)
			return blockState.getBoundingBox(worldIn, pos);
		return null;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean fullHit) {
		return state.getBlock() instanceof BlockSwampWater && ((BlockSwampWater) state.getBlock()).isUnderwaterBlock || super.canCollideCheck(state, fullHit);
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		return !(state.getBlock() instanceof BlockSwampWater) || !((BlockSwampWater) state.getBlock()).isUnderwaterBlock && super.isReplaceable(worldIn, pos);
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

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null || tintIndex != 0) {
			return -1;
		}

		int r = 0;
		int g = 0;
		int b = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				int colorMultiplier = worldIn.getBiome(pos.add(dx, 0, dz)).getWaterColorMultiplier();
				r += (colorMultiplier & 0xFF0000) >> 16;
			g += (colorMultiplier & 0x00FF00) >> 8;
		b += colorMultiplier & 0x0000FF;
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
		if (rand.nextInt(1500) == 0) {
			if (world.getBlockState(pos.up(2)).getMaterial().isLiquid()) {
				BLParticles.FISH.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else if (world.getBlockState(pos.down()).getBlock() == BlockRegistry.MUD) {
				if (rand.nextInt(2) == 0) {
					BLParticles.MOSQUITO.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				} else {
					BLParticles.FLY.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
				}
			}
		}
	}

	@Override
	public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
		if(this.isUnderwaterBlock) {
			IBlockState neighbor = world.getBlockState(pos.offset(side));
			if (neighbor.getMaterial() == state.getMaterial()) {
				return false;
			}
			if(this.densityDir == -1 && side == EnumFacing.UP) {
				return true;
			}
			if(this.densityDir == 1 && side == EnumFacing.DOWN) {
				return true;
			}

			//Ignore AABB check, only render sides if there's no block that actually blocks that side visually!
			return !world.getBlockState(pos.offset(side)).doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	private boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return world.getBlockState(pos).getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return 100;
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if(this.isUnderwaterBlock) {
			this.onBlockHarvested(world, pos, state, player);
			return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState(), world.isRemote ? 11 : 3);
		} else {
			return super.removedByPlayer(state, world, pos, player, willHarvest);
		}
	}
	
	@Override
	public int place(World world, BlockPos pos, FluidStack fluidStack, boolean doPlace) {
		if(fluidStack.amount < Fluid.BUCKET_VOLUME) {
            return 0;
        }
		IBlockState state = world.getBlockState(pos);
		boolean replaceable = state.getBlock().isReplaceable(world, pos);
		boolean isUnderwaterBlock = false;
		Fluid fluid = null;
		if(state.getBlock() instanceof BlockSwampWater) {
			fluid = ((BlockSwampWater) state.getBlock()).getFluid();
			isUnderwaterBlock = ((BlockSwampWater) state.getBlock()).isUnderwaterBlock;
		}
		if(!replaceable && isUnderwaterBlock && fluid != fluidStack.getFluid()) {
			return 0;
		}
        if(doPlace && (replaceable || !isUnderwaterBlock)) {
            FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            world.setBlockState(pos, this.getDefaultState(), 11);
        }
        return Fluid.BUCKET_VOLUME;
	}
	
	@Override
	public FluidStack drain(World world, BlockPos pos, boolean doDrain) {
		if(this.isUnderwaterBlock) {
			if(world.getBlockState(pos).getMaterial() == Material.WATER) {
				// allow draining water but don't remove block
				return stack.copy();
			}
			return null;
		}
		return super.drain(world, pos, doDrain);
	}
}
