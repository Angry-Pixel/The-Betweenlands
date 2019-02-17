package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockBulbCappedMushroomCap extends BasicBlock {
	public BlockBulbCappedMushroomCap() {
		super(Material.WOOD);
		setSoundType(SoundType.CLOTH);
		setHardness(0.2F);
		setLightLevel(1.0F);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IWorldReader blockAccess, BlockPos pos, EnumFacing side) {
		Block block = blockAccess.getBlockState(pos.offset(side)).getBlock();
		return block != BlockRegistry.BULB_CAPPED_MUSHROOM_CAP;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote) {
			int dropChance = 3;

			if (fortune > 0) {
				dropChance -= 2 * fortune;
				if (dropChance < 1) {
					dropChance = 1;
				}
			}

			if (dropChance <= 1 || worldIn.rand.nextInt(dropChance) <= 0) {
				spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);

		if(worldIn.rand.nextInt(150) == 0) {
			int particle = rand.nextInt(3);
			if(particle == 0) {
				BLParticles.MOSQUITO.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else if(particle == 1) {
				BLParticles.FLY.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			} else {
				BLParticles.MOTH.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			}
		}
	}
}
