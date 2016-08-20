package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;

public class BlockPoisonIvy extends BlockVineBL {
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		//TODO add && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entity) when elxirs are added
		if (!worldIn.isRemote && !(entityIn instanceof IEntityBL) && entityIn instanceof EntityLivingBase && worldIn.rand.nextInt(200) == 0 ) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 50, 25));
		}
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.POISON_IVY_ITEM.create(1));
	}
}
