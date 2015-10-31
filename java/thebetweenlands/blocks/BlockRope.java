package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;

import java.util.Random;

/**
 * Created by Bart on 23-10-2015.
 */
public class BlockRope extends BlockLadder {
    public IIcon iconTop;
    public IIcon iconMiddle;
    public IIcon iconBottom;

    public BlockRope() {
        super();
        setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockName("thebeteenlands.rope");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        iconTop = icon.registerIcon("thebetweenlands:climbingRopeTop");
        iconMiddle = icon.registerIcon("thebetweenlands:climbingRopeMiddle");
        iconBottom = icon.registerIcon("thebetweenlands:climbingRopeBottom");
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y + 1, z).isSideSolid(world, x, y, z, ForgeDirection.DOWN) || world.getBlock(x, y + 1, z) == BLBlockRegistry.rope;
    }

    @Override
    public int getRenderType() {
        return 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
        if (world.getBlock(x, y + 1, z) != BLBlockRegistry.rope)
            return iconTop;
        else if (world.getBlock(x, y - 1, z) != BLBlockRegistry.rope)
            return iconBottom;
        else
            return iconMiddle;
    }

    @Override
    public void func_149797_b(int meta) {
        this.setBlockBounds(0.4375f, 0f, 0.4375f, 0.5625f, 1f, 0.5625f);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int p_149650_3_) {
        return BLItemRegistry.rope;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.getBlock(x, y + 1, z).isSideSolid(world, x, y, z, ForgeDirection.DOWN) && world.getBlock(x, y + 1, z) != BLBlockRegistry.rope) {
            this.dropBlockAsItem(world, x, y, z, 1, 0);
            world.setBlockToAir(x, y, z);
        }
    }
}
