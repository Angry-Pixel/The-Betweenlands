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

public class BlockNettle extends BlockPlant {
	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);
		if(rand.nextInt(240) == 0) {
			worldIn.setBlockState(pos, BlockRegistry.NETTLE_FLOWERED.getDefaultState());
		}
	}

	@Override
	public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if(!worldIn.isRemote() && entityIn instanceof IEntityBL == false && entityIn instanceof EntityLivingBase && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
		}
	}
}
