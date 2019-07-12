package thebetweenlands.client.render.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntitySplodeshroom;

@SideOnly(Side.CLIENT)
public class ModelSplodeshroom extends ModelBase {
    ModelRenderer stem1;
    ModelRenderer hat_main;
    ModelRenderer stem2;
    ModelRenderer hat_right;
    ModelRenderer hat_mid_front;
    ModelRenderer hat_left;
    ModelRenderer hat_mid_back;

    public ModelSplodeshroom() {
        textureWidth = 64;
        textureHeight = 32;
        hat_main = new ModelRenderer(this, 26, 0);
        hat_main.setRotationPoint(0.0F, 13.2F, 0.0F);
        hat_main.addBox(-4.0F, 0.0F, -3.5F, 7, 8, 7, 0.0F);
        hat_mid_front = new ModelRenderer(this, 15, 22);
        hat_mid_front.setRotationPoint(0.0F, -3.0F, 0.0F);
        hat_mid_front.addBox(-1.0F, -1.0F, -2.5F, 1, 4, 2, 0.0F);
        hat_right = new ModelRenderer(this, 22, 19);
        hat_right.setRotationPoint(0.0F, -3.0F, 0.0F);
        hat_right.addBox(-3.0F, -1.0F, -2.5F, 2, 4, 5, 0.0F);
        stem1 = new ModelRenderer(this, 13, 0);
        stem1.setRotationPoint(0.0F, 24.0F, 0.0F);
        stem1.addBox(-1.5F, -4.0F, -1.5F, 3, 5, 3, 0.0F);
        setRotateAngle(stem1, 0.0F, 0.0F, -0.13665927946567535F);
        stem2 = new ModelRenderer(this, 0, 0);
        stem2.setRotationPoint(-1.5F, -4.0F, 0.0F);
        stem2.addBox(0.0F, -10.0F, -1.5F, 3, 10, 3, 0.0F);
        setRotateAngle(stem2, 0.0F, 0.0F, 0.136659280431156F);
        hat_left = new ModelRenderer(this, 0, 19);
        hat_left.setRotationPoint(0.0F, -3.0F, 0.0F);
        hat_left.addBox(0.0F, -1.0F, -2.5F, 2, 4, 5, 0.0F);
        hat_mid_back = new ModelRenderer(this, 37, 22);
        hat_mid_back.setRotationPoint(0.0F, -3.0F, 0.0F);
        hat_mid_back.addBox(-1.0F, -1.0F, 0.5F, 1, 4, 2, 0.0F);
        stem1.addChild(stem2);
        hat_main.addChild(hat_mid_front);
        hat_main.addChild(hat_right);
        hat_main.addChild(hat_left);
        hat_main.addChild(hat_mid_back);
    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		EntitySplodeshroom splodeshroom = (EntitySplodeshroom) entity;
		float swell = splodeshroom.getSwellCount() * 0.02F;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);
		stem1.render(scale);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0F, 0F - swell * 0.25F, 0F);
		GlStateManager.scale(1F + swell, 1F + swell * 0.25F, 1F + swell);
		
        hat_main.render(scale);
        GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
