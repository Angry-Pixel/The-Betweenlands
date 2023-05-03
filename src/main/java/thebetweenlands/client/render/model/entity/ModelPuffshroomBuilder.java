package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityPuffshroomBuilder;

@SideOnly(Side.CLIENT)
public class ModelPuffshroomBuilder extends ModelBase {
    public ModelRenderer back_r_tent_origin;
    public ModelRenderer spore_1;
    public ModelRenderer back_r_tent_1;
    public ModelRenderer back_r_tent_2;
    public ModelRenderer back_r_tent_3;
    public ModelRenderer spore_2;
    public ModelRenderer spore_3;
    public ModelRenderer spore_5;
    public ModelRenderer spore_4;
    public ModelRenderer spore_6;

    public ModelPuffshroomBuilder() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.back_r_tent_1 = new ModelRenderer(this, 44, 21);
        this.back_r_tent_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.back_r_tent_1.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        this.spore_3 = new ModelRenderer(this, 0, 28);
        this.spore_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_3.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_3, 0.7853981633974483F, 0.0F, 0.0F);
        this.back_r_tent_3 = new ModelRenderer(this, 8, 22);
        this.back_r_tent_3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.back_r_tent_3.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(back_r_tent_3, -0.7853981633974483F, 0.0F, 0.0F);
        this.spore_4 = new ModelRenderer(this, 0, 28);
        this.spore_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_4.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_4, 0.0F, 0.7853981633974483F, 0.0F);
        this.spore_1 = new ModelRenderer(this, 0, 28);
        this.spore_1.setRotationPoint(0.0F, 20.0F, 0.0F);
        this.spore_1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.spore_6 = new ModelRenderer(this, 0, 28);
        this.spore_6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_6.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_6, 0.0F, 0.7853981633974483F, 0.0F);
        this.back_r_tent_origin = new ModelRenderer(this, 32, 8);
        this.back_r_tent_origin.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.back_r_tent_origin.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(back_r_tent_origin, 0.0F, -2.356194490192345F, 0.0F);
        this.back_r_tent_2 = new ModelRenderer(this, 0, 21);
        this.back_r_tent_2.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.back_r_tent_2.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F);
        this.setRotateAngle(back_r_tent_2, 0.7853981633974483F, 0.0F, 0.0F);
        this.spore_5 = new ModelRenderer(this, 0, 28);
        this.spore_5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_5.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_5, -0.7853981633974483F, 0.0F, 0.0F);
        this.spore_2 = new ModelRenderer(this, 0, 28);
        this.spore_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spore_2.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.setRotateAngle(spore_2, 0.0F, 0.7853981633974483F, 0.0F);
        this.back_r_tent_origin.addChild(this.back_r_tent_1);
        this.spore_1.addChild(this.spore_3);
        this.back_r_tent_2.addChild(this.back_r_tent_3);
        this.spore_3.addChild(this.spore_4);
        this.spore_5.addChild(this.spore_6);
        this.back_r_tent_1.addChild(this.back_r_tent_2);
        this.spore_1.addChild(this.spore_5);
        this.spore_1.addChild(this.spore_2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
      //  this.spore_1.render(scale);
      //  this.back_r_tent_origin.render(scale);
    }
    
    public void renderSpore(float scale) {
    	this.spore_1.render(scale);
    }
    
    public void renderTendril(float scale) {
    	this.back_r_tent_origin.render(scale);
    }
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntityPuffshroomBuilder puffShoom_builder = (EntityPuffshroomBuilder) entity;
		float smoothedTicks = puffShoom_builder.prev_renderTicks + (puffShoom_builder.renderTicks - puffShoom_builder.prev_renderTicks)* partialRenderTicks;
		float flap = MathHelper.sin((smoothedTicks) * 0.25F) * 0.125F;
		float flap2 = MathHelper.cos((smoothedTicks) * 0.25F) * 0.125F;
		if(!puffShoom_builder.getIsMiddle()) {
		    back_r_tent_origin.rotateAngleY = -2.356194490192345F - flap;
		    back_r_tent_origin.rotateAngleX = 0F - flap;
		    back_r_tent_1.rotateAngleX = 0F + flap2 ;
		    back_r_tent_2.rotateAngleX = 0.7853981633974483F + flap *2F;
		    back_r_tent_3.rotateAngleX = -0.7853981633974483F + flap2*3F;
		}
    }
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
