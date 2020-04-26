package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;

@SideOnly(Side.CLIENT)
public class ModelChiromawEgg extends ModelBase {
    ModelRenderer egg_base;
    ModelRenderer egg1;
    ModelRenderer egg2;
    ModelRenderer egg3;

    public ModelChiromawEgg() {
        textureWidth = 64;
        textureHeight = 64;
        egg1 = new ModelRenderer(this, 0, 13);
        egg1.setRotationPoint(0.0F, -2.0F, 0.0F);
        egg1.addBox(-6.0F, -8.0F, -6.0F, 12, 8, 12, 0.0F);
        egg_base = new ModelRenderer(this, 0, 0);
        egg_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        egg_base.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10, 0.0F);
        egg3 = new ModelRenderer(this, 0, 49);
        egg3.setRotationPoint(0.0F, -4.0F, 0.0F);
        egg3.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        egg2 = new ModelRenderer(this, 0, 34);
        egg2.setRotationPoint(0.0F, -8.0F, 0.0F);
        egg2.addBox(-5.0F, -4.0F, -5.0F, 10, 4, 10, 0.0F);
        egg_base.addChild(egg1);
        egg2.addChild(egg3);
        egg1.addChild(egg2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
    	egg_base.render(scale);
    }

	public void renderEgg(EntityChiromawHatchling entity, float partialTicks, float scale) {
		EntityChiromawHatchling chiromaw = (EntityChiromawHatchling) entity;
		float flap = MathHelper.sin((chiromaw.hatchAnimation + partialTicks) * 0.125F) * 0.03125F;
		if(chiromaw.getHatchTick() < 600)
			flap = 0F;
		GlStateManager.pushMatrix();
		GlStateManager.scale(1F + flap, 1F - flap, 1F + flap);
		GlStateManager.translate(0F, 0F + flap * 2F, 0F);
        egg_base.render(scale);
        GlStateManager.popMatrix();
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
