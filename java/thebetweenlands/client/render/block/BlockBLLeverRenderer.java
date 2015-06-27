package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLLever;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 17-6-2015.
 */

public class BlockBLLeverRenderer implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int l = world.getBlockMetadata(x, y, z);
        int i1 = l & 7;
        boolean flag = (l & 8) > 0;
        Tessellator tessellator = Tessellator.instance;
        boolean flag1 = renderer.hasOverrideBlockTexture();

        if (!flag1) {
            renderer.setOverrideBlockTexture(renderer.getBlockIcon(BLBlockRegistry.weedwoodBark));
        }

        float f = 0.25F;
        float f1 = 0.1875F;
        float f2 = 0.1875F;

        if (i1 == 5) {
            renderer.setRenderBounds((double) (0.5F - f1), 0.0D, (double) (0.5F - f), (double) (0.5F + f1), (double) f2, (double) (0.5F + f));
        } else if (i1 == 6) {
            renderer.setRenderBounds((double) (0.5F - f), 0.0D, (double) (0.5F - f1), (double) (0.5F + f), (double) f2, (double) (0.5F + f1));
        } else if (i1 == 4) {
            renderer.setRenderBounds((double) (0.5F - f1), (double) (0.5F - f), (double) (1.0F - f2), (double) (0.5F + f1), (double) (0.5F + f), 1.0D);
        } else if (i1 == 3) {
            renderer.setRenderBounds((double) (0.5F - f1), (double) (0.5F - f), 0.0D, (double) (0.5F + f1), (double) (0.5F + f), (double) f2);
        } else if (i1 == 2) {
            renderer.setRenderBounds((double) (1.0F - f2), (double) (0.5F - f), (double) (0.5F - f1), 1.0D, (double) (0.5F + f), (double) (0.5F + f1));
        } else if (i1 == 1) {
            renderer.setRenderBounds(0.0D, (double) (0.5F - f), (double) (0.5F - f1), (double) f2, (double) (0.5F + f), (double) (0.5F + f1));
        } else if (i1 == 0) {
            renderer.setRenderBounds((double) (0.5F - f), (double) (1.0F - f2), (double) (0.5F - f1), (double) (0.5F + f), 1.0D, (double) (0.5F + f1));
        } else if (i1 == 7) {
            renderer.setRenderBounds((double) (0.5F - f1), (double) (1.0F - f2), (double) (0.5F - f), (double) (0.5F + f1), 1.0D, (double) (0.5F + f));
        }

        renderer.renderStandardBlock(block, x, y, z);

        if (!flag1) {
            renderer.clearOverrideBlockTexture();
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon iicon = renderer.getBlockIconFromSide(block, 0);

        if (renderer.hasOverrideBlockTexture()) {
            iicon = renderer.overrideBlockTexture;
        }

        double d0 = (double) iicon.getMinU();
        double d1 = (double) iicon.getMinV();
        double d2 = (double) iicon.getMaxU();
        double d3 = (double) iicon.getMaxV();
        Vec3[] avec3 = new Vec3[8];
        float f3 = 0.0625F;
        float f4 = 0.0625F;
        float f5 = 0.625F;
        avec3[0] = Vec3.createVectorHelper((double) (-f3), 0.0D, (double) (-f4));
        avec3[1] = Vec3.createVectorHelper((double) f3, 0.0D, (double) (-f4));
        avec3[2] = Vec3.createVectorHelper((double) f3, 0.0D, (double) f4);
        avec3[3] = Vec3.createVectorHelper((double) (-f3), 0.0D, (double) f4);
        avec3[4] = Vec3.createVectorHelper((double) (-f3), (double) f5, (double) (-f4));
        avec3[5] = Vec3.createVectorHelper((double) f3, (double) f5, (double) (-f4));
        avec3[6] = Vec3.createVectorHelper((double) f3, (double) f5, (double) f4);
        avec3[7] = Vec3.createVectorHelper((double) (-f3), (double) f5, (double) f4);

        for (int j1 = 0; j1 < 8; ++j1) {
            if (flag) {
                avec3[j1].zCoord -= 0.0625D;
                avec3[j1].rotateAroundX(((float) Math.PI * 2F / 9F));
            } else {
                avec3[j1].zCoord += 0.0625D;
                avec3[j1].rotateAroundX(-((float) Math.PI * 2F / 9F));
            }

            if (i1 == 0 || i1 == 7) {
                avec3[j1].rotateAroundZ((float) Math.PI);
            }

            if (i1 == 6 || i1 == 0) {
                avec3[j1].rotateAroundY(((float) Math.PI / 2F));
            }

            if (i1 > 0 && i1 < 5) {
                avec3[j1].yCoord -= 0.375D;
                avec3[j1].rotateAroundX(((float) Math.PI / 2F));

                if (i1 == 4) {
                    avec3[j1].rotateAroundY(0.0F);
                }

                if (i1 == 3) {
                    avec3[j1].rotateAroundY((float) Math.PI);
                }

                if (i1 == 2) {
                    avec3[j1].rotateAroundY(((float) Math.PI / 2F));
                }

                if (i1 == 1) {
                    avec3[j1].rotateAroundY(-((float) Math.PI / 2F));
                }

                avec3[j1].xCoord += (double) x + 0.5D;
                avec3[j1].yCoord += (double) ((float) y + 0.5F);
                avec3[j1].zCoord += (double) z + 0.5D;
            } else if (i1 != 0 && i1 != 7) {
                avec3[j1].xCoord += (double) x + 0.5D;
                avec3[j1].yCoord += (double) ((float) y + 0.125F);
                avec3[j1].zCoord += (double) z + 0.5D;
            } else {
                avec3[j1].xCoord += (double) x + 0.5D;
                avec3[j1].yCoord += (double) ((float) y + 0.875F);
                avec3[j1].zCoord += (double) z + 0.5D;
            }
        }

        Vec3 vec33 = null;
        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;

        for (int k1 = 0; k1 < 6; ++k1) {
            if (k1 == 0) {
                d0 = (double) iicon.getInterpolatedU(7.0D);
                d1 = (double) iicon.getInterpolatedV(6.0D);
                d2 = (double) iicon.getInterpolatedU(9.0D);
                d3 = (double) iicon.getInterpolatedV(8.0D);
            } else if (k1 == 2) {
                d0 = (double) iicon.getInterpolatedU(7.0D);
                d1 = (double) iicon.getInterpolatedV(6.0D);
                d2 = (double) iicon.getInterpolatedU(9.0D);
                d3 = (double) iicon.getMaxV();
            }

            if (k1 == 0) {
                vec33 = avec3[0];
                vec3 = avec3[1];
                vec31 = avec3[2];
                vec32 = avec3[3];
            } else if (k1 == 1) {
                vec33 = avec3[7];
                vec3 = avec3[6];
                vec31 = avec3[5];
                vec32 = avec3[4];
            } else if (k1 == 2) {
                vec33 = avec3[1];
                vec3 = avec3[0];
                vec31 = avec3[4];
                vec32 = avec3[5];
            } else if (k1 == 3) {
                vec33 = avec3[2];
                vec3 = avec3[1];
                vec31 = avec3[5];
                vec32 = avec3[6];
            } else if (k1 == 4) {
                vec33 = avec3[3];
                vec3 = avec3[2];
                vec31 = avec3[6];
                vec32 = avec3[7];
            } else if (k1 == 5) {
                vec33 = avec3[0];
                vec3 = avec3[3];
                vec31 = avec3[7];
                vec32 = avec3[4];
            }

            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d0, d1);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.LEVER.id();
    }
}
