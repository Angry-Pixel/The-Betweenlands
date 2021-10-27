package thebetweenlands.common.block.terrain;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockStagnantWater extends BlockFluidClassic implements IStateMappedBlock, ICustomItemBlock {
	public BlockStagnantWater() {
		super(FluidRegistry.STAGNANT_WATER, Material.WATER);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityPlayer && !worldIn.isRemote && !((EntityPlayer)entityIn).isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect())) {
			((EntityPlayer)entityIn).addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
		}
		if(!worldIn.isRemote ) {
			IDecayCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if (cap != null) {
				cap.getDecayStats().addDecayAcceleration(0.1F);
			}
			
			if (entityIn instanceof EntityPlayer) {
				IRotSmellCapability capSmell = entityIn.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
				if(capSmell != null)
					if(!capSmell.isSmellingBad())
						capSmell.setSmellingBad(Math.max(capSmell.getRemainingSmellyTicks(), 24000));
			}
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(BlockStagnantWater.LEVEL);
	}

	@Override
	public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
		if(materialIn == Material.WATER) {
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
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return 100;
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
