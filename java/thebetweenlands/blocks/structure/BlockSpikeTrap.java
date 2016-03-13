package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.TileEntitySpikeTrap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpikeTrap extends BlockContainer {

	public BlockSpikeTrap() {
		super(Material.rock);
		setStepSound(Block.soundTypeStone);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.spikeTrap");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySpikeTrap();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {	
        return BLBlockRegistry.polishedLimestone.getBlockTextureFromSide(0);
    }

}