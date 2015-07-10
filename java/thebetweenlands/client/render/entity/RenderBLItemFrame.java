package thebetweenlands.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.EntityBLItemFrame;

/**
 * Created by Bart on 27-6-2015.
 */
public class RenderBLItemFrame extends RenderItemFrame {

    private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    private final RenderBlocks renderBlocks = new RenderBlocks();
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private IIcon icon;

    @Override
    public void updateIcons(IIconRegister iconRegister) {
        this.icon = iconRegister.registerIcon("thebetweenlands:weedwoodItemframe_background");
    }

    public void renderItemFrame(EntityBLItemFrame entity, double x, double y, double z, float yaw, float tick) {
        GL11.glPushMatrix();
        double d3 = entity.posX - x - 0.5D;
        double d4 = entity.posY - y - 0.5D;
        double d5 = entity.posZ - z - 0.5D;
        int i = entity.field_146063_b + Direction.offsetX[entity.hangingDirection];
        int j = entity.field_146064_c;
        int k = entity.field_146062_d + Direction.offsetZ[entity.hangingDirection];
        GL11.glTranslated((double) i - d3, (double) j - d4, (double) k - d5);

        if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map) {
            this.renderEntity(entity);
        } else {
            this.renderFrameItemAsBlock(entity);
        }

        this.renderItem(entity);
        GL11.glPopMatrix();
        this.func_147914_a(entity, x + (double) ((float) Direction.offsetX[entity.hangingDirection] * 0.3F), y - 0.25D, z + (double) ((float) Direction.offsetZ[entity.hangingDirection] * 0.3F));
    }


    private void renderEntity(EntityBLItemFrame entityBLItemFrame) {
        GL11.glPushMatrix();
        GL11.glRotatef(entityBLItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block block = Blocks.planks;
        float f = 0.0625F;
        float f1 = 1.0F;
        float f2 = f1 / 2.0F;
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double) (0.5F - f2 + 0.0625F), (double) (0.5F - f2 + 0.0625F), (double) f, (double) (0.5F + f2 - 0.0625F), (double) (0.5F + f2 - 0.0625F));
        this.renderBlocks.setOverrideBlockTexture(this.icon);
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        this.renderBlocks.clearOverrideBlockTexture();
        this.renderBlocks.unlockBlockBounds();
        GL11.glPopMatrix();
        this.renderBlocks.setOverrideBlockTexture(BLBlockRegistry.weedwoodPlanks.getIcon(0, 0));
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double) (0.5F - f2), (double) (0.5F - f2), (double) (f + 1.0E-4F), (double) (f + 0.5F - f2), (double) (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double) (0.5F + f2 - f), (double) (0.5F - f2), (double) (f + 1.0E-4F), (double) (0.5F + f2), (double) (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double) (0.5F - f2), (double) (0.5F - f2), (double) f, (double) (0.5F + f2), (double) (f + 0.5F - f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double) (0.5F - f2), (double) (0.5F + f2 - f), (double) f, (double) (0.5F + f2), (double) (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        this.renderBlocks.unlockBlockBounds();
        this.renderBlocks.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }

    private void renderFrameItemAsBlock(EntityBLItemFrame entityBLItemFrame) {
        GL11.glPushMatrix();
        GL11.glRotatef(entityBLItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block block = Blocks.planks;
        float f = 0.0625F;
        float f1 = 0.75F;
        float f2 = f1 / 2.0F;
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double)(0.5F - f2 + 0.0625F), (double)(0.5F - f2 + 0.0625F), (double)(f * 0.5F), (double)(0.5F + f2 - 0.0625F), (double)(0.5F + f2 - 0.0625F));
        this.renderBlocks.setOverrideBlockTexture(this.icon);
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        this.renderBlocks.clearOverrideBlockTexture();
        this.renderBlocks.unlockBlockBounds();
        GL11.glPopMatrix();
        this.renderBlocks.setOverrideBlockTexture(BLBlockRegistry.weedwoodPlanks.getIcon(0, 0));
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(f + 0.5F - f2), (double)(0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double)(0.5F + f2 - f), (double)(0.5F - f2), (double)(f + 1.0E-4F), (double)(0.5F + f2), (double)(0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double)(0.5F - f2), (double)(0.5F - f2), (double)f, (double)(0.5F + f2), (double)(f + 0.5F - f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (double)(0.5F - f2), (double)(0.5F + f2 - f), (double)f, (double)(0.5F + f2), (double)(0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        this.renderBlocks.unlockBlockBounds();
        this.renderBlocks.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }

    private void renderItem(EntityBLItemFrame entityBLItemFrame) {
        ItemStack itemstack = entityBLItemFrame.getDisplayedItem();

        if (itemstack != null) {
            EntityItem entityitem = new EntityItem(entityBLItemFrame.worldObj, 0.0D, 0.0D, 0.0D, itemstack);
            Item item = entityitem.getEntityItem().getItem();
            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.453125F * (float) Direction.offsetX[entityBLItemFrame.hangingDirection], -0.18F, -0.453125F * (float) Direction.offsetZ[entityBLItemFrame.hangingDirection]);
            GL11.glRotatef(180.0F + entityBLItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float) (-90 * entityBLItemFrame.getRotation()), 0.0F, 0.0F, 1.0F);

            switch (entityBLItemFrame.getRotation()) {
                case 1:
                    GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
                    break;
                case 2:
                    GL11.glTranslatef(0.0F, -0.32F, 0.0F);
                    break;
                case 3:
                    GL11.glTranslatef(0.16F, -0.16F, 0.0F);
            }
            net.minecraftforge.client.event.RenderItemInFrameEvent event = new net.minecraftforge.client.event.RenderItemInFrameEvent(entityBLItemFrame, this);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                if (item == Items.filled_map) {
                    this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    float f = 0.0078125F;
                    GL11.glScalef(f, f, f);

                    switch (entityBLItemFrame.getRotation()) {
                        case 0:
                            GL11.glTranslatef(-64.0F, -87.0F, -1.5F);
                            break;
                        case 1:
                            GL11.glTranslatef(-66.5F, -84.5F, -1.5F);
                            break;
                        case 2:
                            GL11.glTranslatef(-64.0F, -82.0F, -1.5F);
                            break;
                        case 3:
                            GL11.glTranslatef(-61.5F, -84.5F, -1.5F);
                    }

                    GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                    MapData mapdata = Items.filled_map.getMapData(entityitem.getEntityItem(), entityBLItemFrame.worldObj);
                    GL11.glTranslatef(0.0F, 0.0F, -1.0F);

                    if (mapdata != null) {
                        this.minecraft.entityRenderer.getMapItemRenderer().func_148250_a(mapdata, true);
                    }
                } else {
                    if (item == Items.compass) {
                        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
                        texturemanager.bindTexture(TextureMap.locationItemsTexture);
                        TextureAtlasSprite textureatlassprite1 = ((TextureMap) texturemanager.getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());

                        if (textureatlassprite1 instanceof TextureCompass) {
                            TextureCompass texturecompass = (TextureCompass) textureatlassprite1;
                            double d0 = texturecompass.currentAngle;
                            double d1 = texturecompass.angleDelta;
                            texturecompass.currentAngle = 0.0D;
                            texturecompass.angleDelta = 0.0D;
                            texturecompass.updateCompass(entityBLItemFrame.worldObj, entityBLItemFrame.posX, entityBLItemFrame.posZ, (double) MathHelper.wrapAngleTo180_float((float) (180 + entityBLItemFrame.hangingDirection * 90)), false, true);
                            texturecompass.currentAngle = d0;
                            texturecompass.angleDelta = d1;
                        }
                    }

                    RenderItem.renderInFrame = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderItem.renderInFrame = false;

                    if (item == Items.compass) {
                        TextureAtlasSprite textureatlassprite = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite(Items.compass.getIconIndex(entityitem.getEntityItem()).getIconName());

                        if (textureatlassprite.getFrameCount() > 0) {
                            textureatlassprite.updateAnimation();
                        }
                    }

                }

                GL11.glPopMatrix();
            }
        }
    }

    protected void func_147914_a(EntityBLItemFrame entityBLItemFrame, double x, double y, double z) {
        if (Minecraft.isGuiEnabled() && entityBLItemFrame.getDisplayedItem() != null && entityBLItemFrame.getDisplayedItem().hasDisplayName() && this.renderManager.field_147941_i == entityBLItemFrame) {
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            double d3 = entityBLItemFrame.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f2 = entityBLItemFrame.isSneaking() ? 32.0F : 64.0F;

            if (d3 < (double) (f2 * f2)) {
                String s = entityBLItemFrame.getDisplayedItem().getDisplayName();

                if (entityBLItemFrame.isSneaking()) {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x + 0.0F, (float) y + entityBLItemFrame.height + 0.5F, (float) z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f1, -f1, f1);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int i = fontrenderer.getStringWidth(s) / 2;
                    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    tessellator.addVertex((double) (-i - 1), -1.0D, 0.0D);
                    tessellator.addVertex((double) (-i - 1), 8.0D, 0.0D);
                    tessellator.addVertex((double) (i + 1), 8.0D, 0.0D);
                    tessellator.addVertex((double) (i + 1), -1.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                } else {
                    this.func_147906_a(entityBLItemFrame, s, x, y, z, 64);
                }
            }
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
        renderItemFrame((EntityBLItemFrame) entity, x, y, z, yaw, tick);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return mapBackgroundTextures;
    }
}
