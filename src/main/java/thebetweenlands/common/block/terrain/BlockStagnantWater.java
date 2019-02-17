package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.BlockFluidClassic;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockStagnantWater extends BlockFluidClassic implements IStateMappedBlock, ICustomItemBlock {
	public BlockStagnantWater() {
		super(FluidRegistry.STAGNANT_WATER, Material.WATER);
	}

	@Override
	public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof EntityPlayer && !worldIn.isRemote && !((EntityPlayer)entityIn).isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect())) {
			((EntityPlayer)entityIn).addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
		}
		if(!worldIn.isRemote && entityIn.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
			IDecayCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			cap.getDecayStats().addDecayAcceleration(0.1F);
		}
	}

	@Override
	public boolean canDisplace(IWorldReader world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockStagnantWater.LEVEL);
	}

	@Override
	public Boolean isEntityInsideMaterial(IWorldReader world, BlockPos blockpos, IBlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(materialIn == Material.WATER) {
			double liquidHeight = (double)((float)(blockpos.getY() + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)state.get(BlockLiquid.LEVEL)).intValue()));
			if(testingHead) {
				double liquidHeightBelow = 0;
				if(world.getBlockState(blockpos.up()).getBlock() == state.getBlock()) {
					liquidHeightBelow = (double)((float)(blockpos.getY() + 2) - BlockLiquid.getLiquidHeightPercent(((Integer)world.getBlockState(blockpos.up()).getValue(BlockLiquid.LEVEL)).intValue()));
				}
				return entity.posY + entity.getEyeHeight() < 0.1D + liquidHeight || entity.posY + entity.getEyeHeight() < 0.1D + liquidHeightBelow;
			} else {
				return entity.getBoundingBox().maxY >= liquidHeight && entity.getBoundingBox().minY < liquidHeight;
			}
		}
		return null;
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return 100;
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
