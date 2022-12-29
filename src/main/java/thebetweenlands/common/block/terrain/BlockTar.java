package thebetweenlands.common.block.terrain;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockTar extends BlockFluidClassic implements IStateMappedBlock, ICustomItemBlock {
	public BlockTar() {
		super(FluidRegistry.TAR, BLMaterialRegistry.TAR);
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, pos);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityLivingBase && !(entity instanceof EntityTarBeast) && !(entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode)) {
			double liquidHeight = (double)((float)(pos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if (entity.posY + entity.getEyeHeight() < liquidHeight) {
				((EntityLivingBase) entity).attackEntityFrom(DamageSource.DROWN, 2.0F);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		this.solidifyTar(world, pos);
		super.onBlockAdded(world, pos, state);
	}

	@Override
	public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos) {

		this.solidifyTar(world, pos);
		super.neighborChanged(state, world, pos, neighborBlock, neighbourPos);
	}

	private void solidifyTar(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			boolean placeTar = false;

			if (!placeTar && world.getBlockState(pos.add(0, 0, -1)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.add(0, 0, 1)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.add(-1, 0, 0)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.add(1, 0, 0)).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.up()).getMaterial() == Material.WATER)
				placeTar = true;

			if (!placeTar && world.getBlockState(pos.down()).getMaterial() == Material.WATER) {
				//Set water block below to solid tar
				world.setBlockState(pos.down(), BlockRegistry.TAR_SOLID.getDefaultState());
			}

			if (placeTar) {
				world.setBlockState(pos, BlockRegistry.TAR_SOLID.getDefaultState());
				if(world.isRemote) {
					playEffects(world, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void playEffects(World world, int x, int y, int z) {
		world.playSound(null, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

		for (int l = 0; l < 8; ++l) {
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + 1.2D, z + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockTar.LEVEL);
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(entity instanceof EntityTarBeast == false && materialIn == Material.WATER) {
			double liquidHeight = (double)((float)(blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue()));
			if(testingHead) {
				double liquidHeightBelow = 0;
				if(world.getBlockState(blockpos.up()).getBlock() == state.getBlock()) {
					liquidHeightBelow = (double)((float)(blockpos.getY() + 2) - BlockLiquid.getLiquidHeightPercent(((Integer)world.getBlockState(blockpos.up()).getValue(BlockLiquid.LEVEL)).intValue()));
				}
				return entity.posY + entity.getEyeHeight() < 0.1D + liquidHeight || entity.posY + entity.getEyeHeight() < 0.1D + liquidHeightBelow;
			} else {
				return entity.getEntityBoundingBox().maxY >= liquidHeight && entity.getEntityBoundingBox().minY < liquidHeight;
			}
		}
		return null;
	}

	@Override
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
		BlockPos entityPos = new BlockPos(entityIn.posX, entityIn.posY + 0.5D, entityIn.posZ);
		if(entityIn instanceof EntityTarBeast == false && pos.equals(entityPos) /*make sure it's only changed once*/) {
			entityIn.motionX *= 0.6D;
			entityIn.motionY *= 0.8D;
			entityIn.motionY -= 0.0175D;
			entityIn.motionZ *= 0.6D;
		}
		return new Vec3d(0, 0, 0);
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}