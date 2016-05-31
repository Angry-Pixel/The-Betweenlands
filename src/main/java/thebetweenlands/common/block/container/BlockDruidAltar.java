package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.tileentity.TileEntityDruidAltar;

public class BlockDruidAltar extends BasicBlock implements ITileEntityProvider {
    public BlockDruidAltar() {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(100.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDruidAltar();
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
