package thebetweenlands.blocks.stalactite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.Point2D;

import java.util.Random;

public class BlockStalactite extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon topIcon, bottomIcon;

    public BlockStalactite(Material material)
    {
        super(material);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
    {
        return null;
    }

    public int getRenderType()
    {
        return ClientProxy.BlockRenderIDs.STALACTITE.id();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        blockIcon = par1IconRegister.registerIcon("thebetweenlands:stalacmite_middle");
        topIcon = par1IconRegister.registerIcon("thebetweenlands:stalacmite_top");
        bottomIcon = par1IconRegister.registerIcon("thebetweenlands:stalacmite_bottom");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int i, int j)
    {
        switch(i)
        {
            case 0: return topIcon;
            case 1: return bottomIcon;
            default: return blockIcon;
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        int md = world.getBlockMetadata(x, y, z);
        if (md == 1 && random.nextInt(10) == 0)
        {
            StalactiteHelper core = StalactiteHelper.getValsFor(x, y, z);
            double midHeight = 0.2 + random.nextDouble() * 0.4;
            Point2D mid = core.getMidway(midHeight);

            float dripRange = 0.1f;
            double d5 = x + mid.x + StalactiteHelper.randRange(random, -dripRange, dripRange);
            double d7 = (double)y + midHeight;
            double d6 = z + mid.y + StalactiteHelper.randRange(random, -dripRange, dripRange);

            world.spawnParticle("dripWater", d5, d7, d6, 0.0D, 0.0D, 0.0D);
        }
    }

    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (world.getBlock(x, y - 1, z) == Blocks.air)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 0x02);
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        StalactiteData info = StalactiteData.getData(world, x, y, z);
        int md = world.getBlockMetadata(x, y, z);

        if (info.noTop && info.noBottom)
        {
            //todo
        }
        else if (md == 0 && info.noBottom && info.distDown == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 0x02);
        }
        else if (md == 1 && (!info.noBottom || info.distDown > 0))
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 0x02);
        }
    }

    public static boolean renderBlock(Block block, int x, int y, int z, IBlockAccess blockAccess)
    {
        StalactiteData info = StalactiteData.getData(blockAccess, x, y, z);
        return renderBlock(block, info.posX, info.posY, info.posZ, info.noBottom, info.distDown, info.noTop, info.distUp, block.getMixedBrightnessForBlock(blockAccess, x, y, z));
    }

    public static boolean renderBlock(Block block, int _x, int _y, int _z, boolean noBottom, int distDown, boolean noTop, int distUp, int brightness)
    {
        int totalHeight = 1 + distDown + distUp;
        float distToMidBottom, distToMidTop;

        double squareAmount = 1.2D;
        double halfTotalHeightSQ;

        if(noTop)
        {
            halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
            distToMidBottom = Math.abs(distUp + 1);
            distToMidTop = Math.abs(distUp);
        }
        else if(noBottom)
        {
            halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
            distToMidBottom = Math.abs(distDown);
            distToMidTop = Math.abs(distDown + 1);
        }
        else
        {
            float halfTotalHeight = totalHeight * 0.5F;
            halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
            distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
            distToMidTop = Math.abs(halfTotalHeight - distUp);
        }

        int minValBottom = (noBottom && distDown == 0) ? 0 : 1;
        int minValTop = (noTop && distUp == 0) ? 0 : 1;
        int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
        int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

        int iconId = (distUp == 0 && !noTop)? 0 : ((distDown == 0 && !noBottom) ? 1 : 2);
        IIcon icon = block.getIcon(iconId, 0);
        float u0 = icon.getMinU();
        float u1 = icon.getMaxU();
        float v0 = icon.getMinV();
        float v1 = icon.getMaxV();

        double halfSize, halfSizeTexW;
        double halfSize1, halfSizeTex1;
        halfSize = (double) scaledValBottom / 16;
        halfSizeTexW = halfSize * (u1 - u0);
        halfSize1 = (double) (scaledValTop) / 16;
        halfSizeTex1 = halfSize1 * (u1 - u0);

        StalactiteHelper core = StalactiteHelper.getValsFor(_x, _y, _z);

        Tessellator t = Tessellator.instance;
        t.setBrightness(brightness);
        float f = 0.9F;
        t.setColorOpaque_F(f, f, f);

        // front
        t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
        t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0, v1);
        t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0, v0);
        t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
        // back
        t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
        t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0, v1);
        t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
        t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
        // left
        t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0, v1);
        t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
        t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
        t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
        // right
        t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
        t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0, v1);
        t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0, v0);
        t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);

        icon = block.getIcon(2, 0);
        u0 = icon.getMinU();
        v0 = icon.getMinV();

        // top
        if(distUp == 0)
        {
            t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
            t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
            t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0 + halfSizeTex1 * 2);
            t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0 + halfSizeTex1 * 2);
        }

        // bottom
        if(distDown == 0)
        {
            t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0);
            t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0, v0);
            t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0, v0 + halfSizeTexW * 2);
            t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0 + halfSizeTexW * 2);
        }

        return true;
    }
}