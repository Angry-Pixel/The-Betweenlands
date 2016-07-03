package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockDruidStone extends BasicBlock implements BlockRegistry.ISubtypeBlock {
	public static final PropertyInteger RANDOM = PropertyInteger.create("random", 0, 7);

	public BlockDruidStone(Material blockMaterialIn, String blockName) {
		super(blockMaterialIn);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, RANDOM);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(RANDOM, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(RANDOM);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rot = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 2.5D) + 2 & 3;
		worldIn.setBlockState(pos, getStateFromMeta(rot), 3);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double pixel = 0.0625D;
		if (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) <= 3 && rand.nextInt(3) == 0) {
			for (int l = 0; l <= 5; l++) {
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() + rand.nextFloat();
				double particleZ = pos.getZ() + rand.nextFloat();

				if (l == 0 && !world.getBlockState(pos.up()).isOpaqueCube())
					particleY = pos.getY() + 1 + pixel;

				if (l == 1 && !world.getBlockState(pos.down()).isOpaqueCube())
					particleY = pos.getY() - pixel;

				if (l == 2 && !world.getBlockState(pos.south()).isOpaqueCube())
					particleZ = pos.getZ() + 1 + pixel;

				if (l == 3 && !world.getBlockState(pos.north()).isOpaqueCube())
					particleZ = pos.getZ() - pixel;

				if (l == 4 && !world.getBlockState(pos.east()).isOpaqueCube())
					particleX = pos.getX() + 1 + pixel;

				if (l == 5 && !world.getBlockState(pos.west()).isOpaqueCube())
					particleX = pos.getX() - pixel;
				//TODO enable when particles are added
				/*if (particleX < pos.getX() || particleX > pos.getX() + 1 || particleY < pos.getY() || particleY > pos.getY() + 1 || particleZ < pos.getZ() || particleZ > pos.getZ() + 1)
                    BLParticle.DRUID_MAGIC_BIG.spawn(world, particleX, particleY, particleZ, (rand.nextFloat() - rand.nextFloat()) *0.1, 0, (rand.nextFloat() - rand.nextFloat())*0.1, rand.nextFloat() + 0.5F);*/
			}
		}
	}

	@Override
	public int getSubtypeNumber() {
		return 2;
	}

	@Override
	public String getSubtypeName(int meta) {
		switch(meta) {
		default:
		case 0:
			return "%s";
		case 1:
			return "%s_active";
		}
	}
}
