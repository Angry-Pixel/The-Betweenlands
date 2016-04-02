package thebetweenlands.common.blocks.terrain;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.common.blocks.BasicBlock;

public class BlockGenericOre extends BasicBlock {
	private Random rand = new Random();
	private int minXP = 0, maxXP = 0;

	public BlockGenericOre(Material materialIn) {
		super(materialIn);
	}

	public BlockGenericOre setXP(int min, int max) {
		this.minXP = min;
		this.maxXP = max;
		return this;
	}

	protected ItemStack getOreDrop(Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random random) {
		ItemStack oreDrop = this.getOreDrop(random, 0);
		return oreDrop != null ? oreDrop.stackSize : 0;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		ItemStack oreDrop = this.getOreDrop(random, fortune);
		return oreDrop != null ? oreDrop.stackSize : 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		ItemStack oreDrop = this.getOreDrop(rand, fortune);
		return oreDrop != null ? oreDrop.getItem() : null;
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return MathHelper.getRandomIntegerInRange(this.rand, this.minXP, this.maxXP);
	}

	@Override
	public int damageDropped(IBlockState state) {
		ItemStack oreDrop = this.getOreDrop(this.rand, 0);
		return oreDrop != null ? oreDrop.getItemDamage() : 0;
	}
}
