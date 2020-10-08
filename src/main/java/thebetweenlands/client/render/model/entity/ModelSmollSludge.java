package thebetweenlands.client.render.model.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntitySludge;

/**
 * ModelSmollSludge - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelSmollSludge extends MowzieModelBase {
    public MowzieModelRenderer skullbase;
    public MowzieModelRenderer sludge1;
    public MowzieModelRenderer skull2;
    public MowzieModelRenderer jaw;
    public MowzieModelRenderer sludge2;
    public MowzieModelRenderer sludge3;
    private float scale;
    
    public ModelSmollSludge() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.sludge3 = new MowzieModelRenderer(this, 36, 15);
        this.sludge3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sludge3.addBox(-3.5F, 3.5F, -3.5F, 7, 1, 7, 0.0F);
        this.sludge1 = new MowzieModelRenderer(this, 0, 16);
        this.sludge1.setRotationPoint(0.0F, 19.5F, 0.0F);
        this.sludge1.addBox(-4.5F, -3.5F, -4.5F, 9, 7, 9, 0.0F);
        this.sludge2 = new MowzieModelRenderer(this, 36, 24);
        this.sludge2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sludge2.addBox(-3.5F, -4.5F, -3.5F, 7, 1, 7, 0.0F);
        this.skull2 = new MowzieModelRenderer(this, 0, 8);
        this.skull2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.skull2.addBox(-1.5F, 0.0F, 0.0F, 3, 1, 1, 0.0F);
        this.skullbase = new MowzieModelRenderer(this, 0, 0);
        this.skullbase.setRotationPoint(0.0F, 20.5F, 0.0F);
        this.skullbase.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(skullbase, -0.5462880558742251F, 0.0F, -0.18203784098300857F);
        this.jaw = new MowzieModelRenderer(this, 0, 11);
        this.jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.jaw.addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        this.setRotateAngle(jaw, 0.40980330836826856F, 0.0F, 0.091106186954104F);
        this.sludge1.addChild(this.sludge3);
        this.sludge1.addChild(this.sludge2);
        this.skullbase.addChild(this.skull2);
        this.skull2.addChild(this.jaw);
        
		setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		this.skullbase.render(f5);
		GlStateManager.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this.sludge1.render(f5);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    @Override
	public void setLivingAnimations(EntityLivingBase entity, float f, float f1, float partialTicks) {
		setToInitPose();
		scale = ((EntitySludge) entity).scale.getAnimationProgressSinSqrt(partialTicks);
		float frame = entity.ticksExisted + partialTicks;
		float controller = (float) (0.5 * Math.sin(frame * 0.1f) * Math.sin(frame * 0.1f)) + 0.5f;
		skullbase.rotationPointY += 1.5f;
		walk(jaw, 1f, 0.3f * controller, false, 0, -0.2f * controller, frame, 1f);
		bob(skullbase, 0.25f, 1f * controller, false, frame, 1f);
		skullbase.rotationPointX += 1.25F * Math.sin(frame * 0.25) * controller;
		flap(skullbase, 0.25f, 0.2f * controller, false, 0, 0, frame, 1f);
		skullbase.rotateAngleZ += 0.15f;
	}
}
