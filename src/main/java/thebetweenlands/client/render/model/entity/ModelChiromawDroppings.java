package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;

@SideOnly(Side.CLIENT)
public class ModelChiromawDroppings extends ModelBase {
    public ModelRenderer poop_1;

    public ModelChiromawDroppings() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.poop_1 = new ModelRenderer(this, 0, 0);
        this.poop_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.poop_1.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
    }

    public void render(EntityChiromawDroppings entity, float partialTicks) {
    	GlStateManager.pushMatrix();
		GlStateManager.rotate(entity.prevRotationTicks + (entity.rotationTicks - entity.prevRotationTicks) * partialTicks, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationTicks + (entity.rotationTicks - entity.rotationTicks) * partialTicks, 1.0F, 0.0F, 0.0F);
        this.poop_1.render(0.0625F);
        GlStateManager.popMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}