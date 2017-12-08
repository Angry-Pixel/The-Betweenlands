package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityItemCage;


public class BlockItemCage extends BlockContainer {

	public BlockItemCage() {
		super(Material.WOOD);
		setHardness(10F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityItemCage();
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
		TileEntityItemCage swordStone = (TileEntityItemCage)world.getTileEntity(pos);
        if (swordStone != null && !swordStone.canBreak)
        	return -1;
        return blockHardness;
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			TileEntityItemCage swordStone = (TileEntityItemCage) world.getTileEntity(pos);
			if (swordStone != null && swordStone.isSwordEnergyBelow() != null) {
				EntitySwordEnergy energyBall = (EntitySwordEnergy) swordStone.isSwordEnergyBelow();
				world.playSound(null, pos, SoundRegistry.FORTRESS_PUZZLE_CAGE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				switch (swordStone.type) {
					case 0:
						energyBall.setSwordPart1Pos(energyBall.getSwordPart1Pos() - 0.05F);
						break;
					case 1:
						energyBall.setSwordPart2Pos(energyBall.getSwordPart2Pos() - 0.05F);
						break;
					case 2:
						energyBall.setSwordPart3Pos(energyBall.getSwordPart3Pos() - 0.05F);
						break;
					case 3:
						energyBall.setSwordPart4Pos(energyBall.getSwordPart4Pos() - 0.05F);
						break;
					}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
