package thebetweenlands.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRenderHelper
{
    /**
     * Renders an item with the size of a normal block.
     *
     * @param item       ItemStack
     * @param renderPass int
     */
    public static void renderItem(ItemStack item, int renderPass) {
    	GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0, 1.0D / 16.0D);
        GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        RenderManager.instance.itemRenderer.renderItem(Minecraft.getMinecraft().thePlayer, item, renderPass, ItemRenderType.ENTITY);
        GL11.glPopMatrix();
    }

    ///--------------------------------------
    /// MY SH*T, THANKS. ~SanAndreasP
    ///--------------------------------------

    private static final ResourceLocation GLINT_PNG = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    protected static RenderItem itemRender = new RenderItem();
    protected static RenderBlocks renderBlocksRi = new RenderBlocks();

    /**
     * Renders an ItemStack in a GUI.
     * @param mc the Minecraft instance
     * @param stack the ItemStack to be rendered
     * @param x x-position in the GUI
     * @param y y-position in the GUI
     */
    public static void renderItemInGui(Minecraft mc, ItemStack stack, int x, int y) {
        if( stack != null ) {
            GL11.glTranslatef(0.0F, 0.0F, 32.0F);
            itemRender.zLevel = 200.0F;
            itemRender.renderItemIntoGUI(null, mc.getTextureManager(), stack, x, y);
            itemRender.zLevel = 0.0F;
        }
    }

    /**
     * Renders an IIcon instance in 3D.
     * @param icon the icon to be rendered
     * @param isBlock if it should be rendered as a block or not
     * @param hasAlpha if it has a transparent texture
     * @param color the tint it should be rendered in
     */
    public static void renderIconIn3D(IIcon icon, boolean isBlock, boolean hasAlpha, int color) {
        GL11.glPushMatrix();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if( isBlock ) {
            Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(0));
            if( hasAlpha ) {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }

            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 0.5F);

            GL11.glPushMatrix();

            Tessellator tessellator = Tessellator.instance;

            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, 1.0F);

            Blocks.stone.setBlockBoundsForItemRender();
            renderBlocksRi.setRenderBoundsFromBlock(Blocks.stone);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderBlocksRi.renderFaceYNeg(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderBlocksRi.renderFaceYPos(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderBlocksRi.renderFaceZNeg(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderBlocksRi.renderFaceZPos(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderBlocksRi.renderFaceXNeg(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderBlocksRi.renderFaceXPos(Blocks.stone, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();

            GL11.glPopMatrix();

            if( hasAlpha ) {
                GL11.glDisable(GL11.GL_BLEND);
            }
        } else {
            if( hasAlpha ) {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }

            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, 1.0F);
            renderItemIn3D(icon, false, 1);

            if( hasAlpha ) {
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        TextureUtil.func_147945_b();
    }

    /**
     * Renders an ItemStack in 3D.
     * @param stack the ItemStack to be rendered
     */
    public static void renderItemIn3D(ItemStack stack) {
        if( stack.getItem() != null ) {
            GL11.glPushMatrix();

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            if( stack.getItemSpriteNumber() == 0 && stack.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType()) )
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().renderEngine.getResourceLocation(stack.getItemSpriteNumber()));

                Block block = Block.getBlockFromItem(stack.getItem());

                float blockScale = 0.5F;
                int renderType = block.getRenderType();

                if( renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ) {
                    blockScale = 1.0F;
                }

                if( block.getRenderBlockPass() > 0 ) {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glTranslatef(0.5F, 0.5F, 0.0F);
                GL11.glScalef(blockScale, blockScale, blockScale);

                GL11.glPushMatrix();
                renderBlocksRi.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
                GL11.glPopMatrix();

                if( block.getRenderBlockPass() > 0 ) {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            } else {
                if( stack.getItem().requiresMultipleRenderPasses() ) {
                    for( int j = 0; j < stack.getItem().getRenderPasses(stack.getItemDamage()); j++ ) {
                        IIcon icon = stack.getItem().getIcon(stack, j);

                        int color = stack.getItem().getColorFromItemStack(stack, j);
                        float red = (color >> 16 & 255) / 255.0F;
                        float green = (color >> 8 & 255) / 255.0F;
                        float blue = (color & 255) / 255.0F;
                        GL11.glColor4f(red, green, blue, 1.0F);
                        renderItemIn3D(icon, stack.hasEffect(j), 1);
                    }
                } else {
                    IIcon icon = stack.getItem().getIcon(stack, 0);

                    if( stack.getItem() instanceof ItemCloth ) {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    int color = stack.getItem().getColorFromItemStack(stack, 0);
                    float red = (color >> 16 & 255) / 255.0F;
                    float green = (color >> 8 & 255) / 255.0F;
                    float blue = (color & 255) / 255.0F;
                    GL11.glColor4f(red, green, blue, 1.0F);
                    renderItemIn3D(icon, stack.hasEffect(0), stack.getItemSpriteNumber());

                    if( stack.getItem() instanceof ItemCloth ) {
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            TextureUtil.func_147945_b();
        }
    }

    private static void renderItemIn3D(IIcon icon, boolean withEffect, int spriteIndex) {
        GL11.glPushMatrix();

        if( icon == null ) {
            GL11.glPopMatrix();
            return;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        Tessellator tessellator = Tessellator.instance;

        Minecraft.getMinecraft().renderEngine.bindTexture(
                Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex)
        );

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625F, false);

        if( withEffect ) {
            float baseClr = 0.76F;
            float glintScale = 0.125F;
            float glintTransX = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;

            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);

            Minecraft.getMinecraft().renderEngine.bindTexture(GLINT_PNG);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glColor4f(0.5F * baseClr, 0.25F * baseClr, 0.8F * baseClr, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glScalef(glintScale, glintScale, glintScale);
            GL11.glTranslatef(glintTransX, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F, false);

            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(glintScale, glintScale, glintScale);

            glintTransX = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;

            GL11.glTranslatef(-glintTransX, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);

            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F, false);

            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static void renderItem(ItemStack stack, int layer, boolean isGlowing) {
        if( stack == null ) {
            return;
        }

        renderIcon(getItemIcon(stack, layer), stack.getItemSpriteNumber(), stack.hasEffect(layer), isGlowing);
    }

    /**
     * Renders an IIcon in 2D.
     * @param icon the icon to be rendered
     * @param spriteIndex the index of the texture to be used (0 = blocks, 1 = items)
     * @param hasEffect if it has an enchantment effect
     * @param isGlowing if it should glow in the dark
     */
    public static void renderIcon(IIcon icon, int spriteIndex, boolean hasEffect, boolean isGlowing) {
        GL11.glPushMatrix();

        if( icon == null ) {
            GL11.glPopMatrix();
            return;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float transX = 0.0F;
        float transY = 0.3F;
        float scale = 1.5F;
        Tessellator tessellator = Tessellator.instance;

        Minecraft.getMinecraft().renderEngine.bindTexture(
                Minecraft.getMinecraft().renderEngine.getResourceLocation(spriteIndex)
        );

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-transX, -transY, 0.0F);
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);

        renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625F, isGlowing);

        if( hasEffect ) {
            float baseClr = 0.76F;
            float glintScale = 0.125F;
            float glintTransX = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;

            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);

            Minecraft.getMinecraft().renderEngine.bindTexture(GLINT_PNG);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glColor4f(0.5F * baseClr, 0.25F * baseClr, 0.8F * baseClr, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glScalef(glintScale, glintScale, glintScale);
            GL11.glTranslatef(glintTransX, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F, false);

            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(glintScale, glintScale, glintScale);

            glintTransX = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;

            GL11.glTranslatef(-glintTransX, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);

            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F, false);

            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private static void renderItemIn2D(Tessellator tess, float minU, float minV, float maxU, float maxV, int scaleX, int scaleY, float negZLevel, boolean isGlowing) {
        if( isGlowing ) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LIGHT0);
            GL11.glDisable(GL11.GL_LIGHT1);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);

            float prevLGTX = OpenGlHelper.lastBrightnessX;
            float prevLGTY = OpenGlHelper.lastBrightnessY;
            int bright = 0xF0;
            int brightX = bright % 65536;
            int brightY = bright / 65536;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
            ItemRenderer.renderItemIn2D(tess, minU, minV, maxU, maxV, scaleX, scaleY, negZLevel);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLGTX, prevLGTY);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_LIGHT0);
            GL11.glEnable(GL11.GL_LIGHT1);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        } else {
            ItemRenderer.renderItemIn2D(tess, minU, minV, maxU, maxV, scaleX, scaleY, negZLevel);
        }
    }

    @Deprecated
    public static void renderGlint(int par1, int minU, int minV, int maxU, int maxV, double zLevel) {
        for( int j1 = 0; j1 < 2; ++j1 ) {
            if( j1 == 0 ) {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            if( j1 == 1 ) {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f5 = 3000.0F + j1 * 1873.0F;
            float f2 = Minecraft.getSystemTime() % f5 / f5 * 256.0F;
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0F;

            if( j1 == 1 ) {
                f4 = -1.0F;
            }

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(minU, minV + maxV, zLevel, (f2 + maxV * f4) * f, (f3 + maxV) * f1);
            tessellator.addVertexWithUV(minU + maxU, minV + maxV, zLevel, (f2 + maxU + maxV * f4) * f, (f3 + maxV) * f1);
            tessellator.addVertexWithUV(minU + maxU, minV, zLevel, (f2 + maxU) * f, (f3 + 0.0F) * f1);
            tessellator.addVertexWithUV(minU, minV, zLevel, (f2 + 0.0F) * f, (f3 + 0.0F) * f1);
            tessellator.draw();
        }
    }

    public static IIcon getItemIcon(ItemStack stack, int layer)
    {
        return stack.getItem().requiresMultipleRenderPasses() ? stack.getItem().getIcon(stack, layer) : stack.getIconIndex();
    }
}
