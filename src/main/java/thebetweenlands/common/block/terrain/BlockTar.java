package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockTar extends BlockFluidClassic implements IStateMappedBlock {
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
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityLivingBase /*&& !(entity instanceof EntityTarBeast)*/ && !(entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode)) {
			entity.motionX *= 0.005D;
			entity.motionZ *= 0.005D;
			if(entity.motionY < 0)
				entity.motionY *= 0.005D;
			IBlockState blockStateAbove = world.getBlockState(pos.up());
			if(blockStateAbove.getMaterial() == BLMaterialRegistry.TAR && entity.isInsideOfMaterial(BLMaterialRegistry.TAR)) {
				((EntityLivingBase) entity).attackEntityFrom(DamageSource.drown, 2.0F);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		this.solidifyTar(world, pos);
		super.onBlockAdded(world, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		this.solidifyTar(world, pos);
		super.neighborChanged(state, world, pos, block);
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
}