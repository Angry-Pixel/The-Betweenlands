package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJellyfish extends ModelBase {
    ModelRenderer mesogloea_base;
    ModelRenderer mouth;
    ModelRenderer tentacles_front1a;
    ModelRenderer tentacles_left1a;
    ModelRenderer tentacles_right1a;
    ModelRenderer tentacles_back1a;
    ModelRenderer oral_arm_front1a;
    ModelRenderer oral_arm_left1a;
    ModelRenderer oral_arm_right1a;
    ModelRenderer oral_arm_back1a;
    ModelRenderer tentacles_front1b;
    ModelRenderer tentacles_front_1c;
    ModelRenderer tentacles_left1b;
    ModelRenderer tentacles_left1c;
    ModelRenderer tentacles_right1b;
    ModelRenderer tentacles_right1c;
    ModelRenderer tentacles_back1b;
    ModelRenderer tentacles_back1c;

    public ModelJellyfish() {
        textureWidth = 32;
        textureHeight = 32;
        tentacles_right1a = new ModelRenderer(this, 21, -3);
        tentacles_right1a.setRotationPoint(-1.5F, 0.0F, 0.0F);
        tentacles_right1a.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_right1a, 0.0F, 0.0F, 0.091106186954104F);
        tentacles_left1b = new ModelRenderer(this, 14, 17);
        tentacles_left1b.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_left1b.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_left1b, 0.0F, 0.0F, 0.136659280431156F);
        oral_arm_right1a = new ModelRenderer(this, 7, 16);
        oral_arm_right1a.setRotationPoint(-1.0F, 2.0F, 0.0F);
        oral_arm_right1a.addBox(0.0F, 0.0F, -1.5F, 0, 5, 3, 0.0F);
        tentacles_left1a = new ModelRenderer(this, 14, 12);
        tentacles_left1a.setRotationPoint(1.5F, 0.0F, 0.0F);
        tentacles_left1a.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_left1a, 0.0F, 0.0F, -0.091106186954104F);
        tentacles_left1c = new ModelRenderer(this, 14, 22);
        tentacles_left1c.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_left1c.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_left1c, 0.0F, 0.0F, -0.091106186954104F);
        oral_arm_left1a = new ModelRenderer(this, 0, 22);
        oral_arm_left1a.setRotationPoint(1.0F, 2.0F, 0.0F);
        oral_arm_left1a.addBox(0.0F, 0.0F, -1.5F, 0, 5, 3, 0.0F);
        tentacles_front1a = new ModelRenderer(this, 14, 0);
        tentacles_front1a.setRotationPoint(0.0F, 0.0F, -1.5F);
        tentacles_front1a.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_front1a, -0.091106186954104F, 0.0F, 0.0F);
        tentacles_right1c = new ModelRenderer(this, 21, 7);
        tentacles_right1c.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_right1c.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_right1c, 0.0F, -0.0017453292519943296F, 0.091106186954104F);
        oral_arm_front1a = new ModelRenderer(this, 0, 19);
        oral_arm_front1a.setRotationPoint(0.0F, 2.0F, -1.0F);
        oral_arm_front1a.addBox(-1.5F, 0.0F, 0.0F, 3, 5, 0, 0.0F);
        tentacles_back1a = new ModelRenderer(this, 21, 15);
        tentacles_back1a.setRotationPoint(0.0F, 0.0F, 1.5F);
        tentacles_back1a.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_back1a, 0.091106186954104F, 0.0F, 0.0F);
        tentacles_front1b = new ModelRenderer(this, 14, 5);
        tentacles_front1b.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_front1b.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_front1b, 0.136659280431156F, 0.0F, 0.0F);
        mesogloea_base = new ModelRenderer(this, 0, 0);
        mesogloea_base.setRotationPoint(0.0F, 12.0F, 0.0F);
        mesogloea_base.addBox(-1.5F, -10.0F, -1.5F, 3, 10, 3, 0.0F);
        tentacles_back1c = new ModelRenderer(this, 21, 25);
        tentacles_back1c.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_back1c.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_back1c, 0.091106186954104F, 0.0F, 0.0F);
        oral_arm_back1a = new ModelRenderer(this, 7, 25);
        oral_arm_back1a.setRotationPoint(0.0F, 1.5F, 1.0F);
        oral_arm_back1a.addBox(-1.5F, 0.0F, 0.0F, 3, 5, 0, 0.0F);
        tentacles_back1b = new ModelRenderer(this, 21, 20);
        tentacles_back1b.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_back1b.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_back1b, -0.136659280431156F, 0.0F, 0.0F);
        mouth = new ModelRenderer(this, 0, 14);
        mouth.setRotationPoint(0.0F, 0.0F, 0.0F);
        mouth.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, 0.0F);
        tentacles_right1b = new ModelRenderer(this, 21, 2);
        tentacles_right1b.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_right1b.addBox(0.0F, 0.0F, -1.5F, 0, 4, 3, 0.0F);
        setRotateAngle(tentacles_right1b, 0.0F, 0.0F, -0.136659280431156F);
        tentacles_front_1c = new ModelRenderer(this, 14, 10);
        tentacles_front_1c.setRotationPoint(0.0F, 4.0F, 0.0F);
        tentacles_front_1c.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
        setRotateAngle(tentacles_front_1c, -0.091106186954104F, 0.0F, 0.0F);
        mesogloea_base.addChild(tentacles_right1a);
        tentacles_left1a.addChild(tentacles_left1b);
        mouth.addChild(oral_arm_right1a);
        mesogloea_base.addChild(tentacles_left1a);
        tentacles_left1b.addChild(tentacles_left1c);
        mouth.addChild(oral_arm_left1a);
        mesogloea_base.addChild(tentacles_front1a);
        tentacles_right1b.addChild(tentacles_right1c);
        mouth.addChild(oral_arm_front1a);
        mesogloea_base.addChild(tentacles_back1a);
        tentacles_front1a.addChild(tentacles_front1b);
        tentacles_back1b.addChild(tentacles_back1c);
        mouth.addChild(oral_arm_back1a);
        tentacles_back1a.addChild(tentacles_back1b);
        mesogloea_base.addChild(mouth);
        tentacles_right1a.addChild(tentacles_right1b);
        tentacles_front1b.addChild(tentacles_front_1c);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        mesogloea_base.render(scale);
        tentacles_left1a.rotateAngleZ = -0.091106186954104F * 2F + MathHelper.cos(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_right1a.rotateAngleZ = 0.091106186954104F * 2F - MathHelper.cos(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_front1a.rotateAngleX = -0.091106186954104F * 2F + MathHelper.cos(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_back1a.rotateAngleX = 0.091106186954104F * 2F - MathHelper.cos(limbSwing * 1.0F) * limbSwingAmount* 1F;
        
        tentacles_left1b.rotateAngleZ = 0.136659280431156F + MathHelper.sin(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_right1b.rotateAngleZ = -0.136659280431156F - MathHelper.sin(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_front1b.rotateAngleX = 0.136659280431156F + MathHelper.sin(limbSwing * 1.0F) * limbSwingAmount * 1F;
        tentacles_back1b.rotateAngleX = -0.136659280431156F - MathHelper.sin(limbSwing * 1.0F) * limbSwingAmount * 1F;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }


}
