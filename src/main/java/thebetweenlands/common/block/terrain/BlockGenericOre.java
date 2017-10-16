package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;

public class BlockGenericOre extends BasicBlock {
	private Random rand = new Random();
	private int minXP = 0, maxXP = 0;

	public BlockGenericOre(Material materialIn) {
		super(materialIn);
		this.setDefaultCreativeTab()
		.setSoundType2(SoundType.STONE)
		.setHardness(1.5F)
		.setResistance(10.0F);
	}

	public BlockGenericOre setXP(int min, int max) {
		this.minXP = min;
		this.maxXP = max;
		return this;
	}

	protected ItemStack getOreDrop(Random rand, int fortune) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

	@Override
	public int quantityDropped(Random random) {
		ItemStack oreDrop = this.getOreDrop(random, 0);
		return !oreDrop.isEmpty() ? oreDrop.getCount() : 0;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		ItemStack oreDrop = this.getOreDrop(random, fortune);
		return !oreDrop.isEmpty() ? oreDrop.getCount() : 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		ItemStack oreDrop = this.getOreDrop(rand, fortune);
		return !oreDrop.isEmpty() ? oreDrop.getItem() : null;
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return MathHelper.getInt(rand, this.minXP, this.maxXP);
	}

	@Override
	public int damageDropped(IBlockState state) {
		ItemStack oreDrop = this.getOreDrop(this.rand, 0);
		return !oreDrop.isEmpty() ? oreDrop.getItemDamage() : 0;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double pixel = 0.0625D;
		if(rand.nextInt(3) == 0) {
			for (int l = 0; l < 5; l++) {
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() + rand.nextFloat();
				double particleZ = pos.getZ() + rand.nextFloat();

				if (l == 0 && !worldIn.getBlockState(pos.up()).isOpaqueCube())
					particleY = pos.getY() + 1 + pixel;

				if (l == 1 && !worldIn.getBlockState(pos.down()).isOpaqueCube())
					particleY = pos.getY() - pixel;

				if (l == 2 && !worldIn.getBlockState(pos.add(0, 0, 1)).isOpaqueCube())
					particleZ = pos.getZ() + 1 + pixel;

				if (l == 3 && !worldIn.getBlockState(pos.add(0, 0, -1)).isOpaqueCube())
					particleZ = pos.getZ() - pixel;

				if (l == 4 && !worldIn.getBlockState(pos.add(1, 0, 0)).isOpaqueCube())
					particleX = pos.getX() + 1 + pixel;

				if (l == 5 && !worldIn.getBlockState(pos.add(-1, 0, 0)).isOpaqueCube())
					particleX = pos.getX() - pixel;

				if (particleX < pos.getX() || particleX > pos.getX() + 1 || particleY < pos.getY() || particleY > pos.getY() + 1 || particleZ < pos.getZ() || particleZ > pos.getZ() + 1) {
					this.spawnParticle(worldIn, particleX, particleY, particleZ);
				}
			}
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

	public void spawnParticle(World world, double x, double y, double z) { }
}
