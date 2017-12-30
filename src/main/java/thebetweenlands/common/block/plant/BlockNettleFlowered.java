package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockNettleFlowered extends BlockPlant {
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(rand.nextInt(350) == 0) {
			BlockPos randOffset = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			if(worldIn.isAreaLoaded(randOffset, 0) && worldIn.isAirBlock(randOffset) && canBlockStay(worldIn, randOffset, worldIn.getBlockState(randOffset))) {
				worldIn.setBlockState(randOffset, BlockRegistry.NETTLE.getDefaultState());
			}
		}
		if(rand.nextInt(220) == 0) {
			worldIn.setBlockState(pos, BlockRegistry.NETTLE.getDefaultState());
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(!worldIn.isRemote && entityIn instanceof IEntityBL == false && entityIn instanceof EntityLivingBase && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
		}
	}
}
