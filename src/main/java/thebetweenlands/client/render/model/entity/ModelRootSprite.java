package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * BLRootSprite - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelRootSprite extends ModelBase {
    public ModelRenderer wobbly_body_base;
    public ModelRenderer wobbly_body_top;
    public ModelRenderer footleft;
    public ModelRenderer footright;
    public ModelRenderer head;
    public ModelRenderer headroots;

    public ModelRootSprite() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.headroots = new ModelRenderer(this, 0, 18);
        this.headroots.setRotationPoint(0.0F, -3.0F, -3.0F);
        this.headroots.addBox(-2.0F, -2.0F, 1.0F, 4, 2, 4, 0.0F);
        this.setRotateAngle(headroots, -0.136659280431156F, 0.0F, 0.0F);
        this.footright = new ModelRenderer(this, 14, 5);
        this.footright.setRotationPoint(-0.5F, -0.2F, 0.0F);
        this.footright.addBox(-1.5F, 0.0F, -2.0F, 2, 1, 3, 0.0F);
        this.setRotateAngle(footright, 0.091106186954104F, 0.091106186954104F, 0.0F);
        this.head = new ModelRenderer(this, 0, 10);
        this.head.setRotationPoint(0.0F, -1.7F, -1.0F);
        this.head.addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(head, -0.18203784098300857F, 0.0F, 0.091106186954104F);
        this.footleft = new ModelRenderer(this, 14, 0);
        this.footleft.setRotationPoint(0.5F, -0.2F, 0.0F);
        this.footleft.addBox(-0.5F, 0.0F, -2.0F, 2, 1, 3, 0.0F);
        this.setRotateAngle(footleft, 0.091106186954104F, -0.091106186954104F, 0.0F);
        this.wobbly_body_top = new ModelRenderer(this, 0, 5);
        this.wobbly_body_top.setRotationPoint(0.0F, -2.0F, 1.0F);
        this.wobbly_body_top.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        this.setRotateAngle(wobbly_body_top, 0.136659280431156F, 0.0F, 0.0F);
        this.wobbly_body_base = new ModelRenderer(this, 0, 0);
        this.wobbly_body_base.setRotationPoint(0.0F, 23.2F, 0.0F);
        this.wobbly_body_base.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 2, 0.0F);
        this.setRotateAngle(wobbly_body_base, -0.091106186954104F, 0.0F, 0.0F);
        this.head.addChild(this.headroots);
        this.wobbly_body_base.addChild(this.footright);
        this.wobbly_body_top.addChild(this.head);
        this.wobbly_body_base.addChild(this.footleft);
        this.wobbly_body_base.addChild(this.wobbly_body_top);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.wobbly_body_base.render(f5);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    	super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    
    	this.head.rotateAngleY = (float)Math.toRadians(netHeadYaw);
    	
    	float wobbleX = MathHelper.cos(ageInTicks / 11.0F) * 0.08F;
    	float wobbleZ = MathHelper.cos(ageInTicks / 12.0F) * 0.08F;
    	
    	this.footleft.rotateAngleX = MathHelper.cos(limbSwing * 2F) * 1.4F * limbSwingAmount * 0.5F + 0.1F - wobbleX;
    	this.footright.rotateAngleX = MathHelper.cos(limbSwing * 2F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F + 0.1F - wobbleX;
    	
    	this.footleft.rotateAngleZ = -wobbleZ;
    	this.footright.rotateAngleZ = -wobbleZ;
    	
    	this.head.rotateAngleX = -0.18203784098300857F + wobbleX*2;
    	this.head.rotateAngleZ = 0.091106186954104F + wobbleZ*2;
    	
    	this.wobbly_body_base.rotateAngleX = -0.091106186954104F + wobbleX;
    	this.wobbly_body_base.rotateAngleZ = wobbleZ;
    }
    
    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
