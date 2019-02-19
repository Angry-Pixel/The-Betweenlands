package thebetweenlands.common.block.plant;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockWeepingBlue extends BlockDoublePlantBL {
	@Override
	public ArrayList<ItemStack> getDrops(IWorldReader world, BlockPos pos, IBlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(ItemRegistry.WEEPING_BLUE_PETAL, 1 + rand.nextInt(2 + fortune)));
		return drops;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isRemote() && !stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.025F);
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(4) == 0 && worldIn.getBlockState(pos).get(HALF) == EnumBlockHalf.UPPER) {
			worldIn.spawnParticle(Particles.DRIPPING_WATER, pos.getX() + 0.25D + rand.nextFloat() * 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.25D + rand.nextFloat() * 0.5D, 0.0D, 0.0D, 0.0D);
		}
	}
}
