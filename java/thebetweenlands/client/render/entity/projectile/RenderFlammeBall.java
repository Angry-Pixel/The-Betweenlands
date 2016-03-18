package thebetweenlands.client.render.entity.projectile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thebetweenlands.entities.projectiles.EntityFlammeBall;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderFlammeBall extends Render
{
    private float scale;

    public RenderFlammeBall(float scale){
        this.scale = scale;
    }

    public void doRender(EntityFlammeBall flammeBall, double x, double y, double z, float rotationYaw, float partialTick) {
        GL11.glPushMatrix();
        bindEntityTexture(flammeBall);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float f2 = scale;
        GL11.glScalef(f2 / 1.0F, f2 / 1.0F, f2 / 1.0F);
        IIcon iicon = BLItemRegistry.itemsGeneric.getIcon(ItemGeneric.createStack(EnumItemGeneric.FLAMME_BALL), 0);
        Tessellator tessellator = Tessellator.instance;
        float f3 = iicon.getMinU();
        float f4 = iicon.getMaxU();
        float f5 = iicon.getMinV();
        float f6 = iicon.getMaxV();
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double)(0.0F - f8), (double)(0.0F - f9), 0.0D, (double)f3, (double)f6);
        tessellator.addVertexWithUV((double)(f7 - f8), (double)(0.0F - f9), 0.0D, (double)f4, (double)f6);
        tessellator.addVertexWithUV((double)(f7 - f8), (double)(1.0F - f9), 0.0D, (double)f4, (double)f5);
        tessellator.addVertexWithUV((double)(0.0F - f8), (double)(1.0F - f9), 0.0D, (double)f3, (double)f5);
        tessellator.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture(EntityFlammeBall flammeBall) {
        return TextureMap.locationItemsTexture;
    }

    protected ResourceLocation getEntityTexture(Entity flammeBall) {
        return getEntityTexture((EntityFlammeBall)flammeBall);
    }

    public void doRender(Entity flammeBall, double x, double y, double z, float rotationYaw, float partialTick) {
        doRender((EntityFlammeBall)flammeBall, x, y, z, rotationYaw, partialTick);
    }
}