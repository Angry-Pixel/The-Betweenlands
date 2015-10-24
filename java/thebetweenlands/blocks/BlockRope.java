package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

import static net.minecraftforge.common.util.ForgeDirection.*;

/**
 * Created by Bart on 23-10-2015.
 */
public class BlockRope extends BlockLadder {
    public BlockRope() {
        super();
        setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockTextureName("thebeteenlands:rope");
        this.setBlockName("thebeteenlands.rope");
    }

    @Override
    public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
        return true;
    }

    @Override
    public int getRenderType() {
        return 0;
    }

    public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
        return 0;
    }

    @Override
    public void func_149797_b(int meta) {
        this.setBlockBounds(0.25f, 0f, 0.25f, 0.75f, 1f, 0.75f);
    }

    @Override
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {

    }
}
