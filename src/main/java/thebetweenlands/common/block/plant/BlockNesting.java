package thebetweenlands.common.block.plant;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockNesting extends BlockWeedwoodBush {
	private ItemStack drop;

	public BlockNesting(ItemStack drop) {
		this.drop = drop;
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.drop.getItem();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.drop.getItemDamage();
	}

	@Override
	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing dir) {
		if(dir == EnumFacing.DOWN && worldIn.isSideSolid(pos, EnumFacing.UP, false)) {
			return true;
		}
		return worldIn.getBlockState(pos).getBlock() instanceof BlockNesting;
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote && !(entityIn instanceof IEntityBL) && entityIn instanceof EntityLivingBase && !ElixirEffectRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entityIn)) {
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
		}
	}

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return 0xFFFFFFFF;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return EnumItemMisc.SLIMY_BONE.isItemOf(drop) ? new ItemStack(BlockRegistry.NESTING_BLOCK_BONES) : new ItemStack(BlockRegistry.NESTING_BLOCK_STICKS);
	}
}
