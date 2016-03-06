package thebetweenlands.blocks.structure;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.EntitySwordEnergy;
import thebetweenlands.tileentities.TileEntitySwordStone;

public class BlockSwordStone extends BlockContainer {

	public BlockSwordStone() {
		super(Material.rock);
		setHardness(15F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.swordStone");
		setBlockTextureName("thebetweenlands:polishedDentrothyst21"); //temp spam reduction 
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySwordStone();
	}

	@Override
    public float getBlockHardness(World world, int x, int y, int z) {
		TileEntitySwordStone swordStone = (TileEntitySwordStone)world.getTileEntity(x, y, z);
        if (swordStone != null && !swordStone.canBreak)
        	return -1;
        return blockHardness;
    }

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			TileEntitySwordStone swordStone = (TileEntitySwordStone) world.getTileEntity(x, y, z);
			if (swordStone != null && swordStone.isSwordEnergyBelow() != null) {
				EntitySwordEnergy energyBall = (EntitySwordEnergy) swordStone.isSwordEnergyBelow();
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
	}

	@Override
	public int getRenderType() {
		return - 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

}
