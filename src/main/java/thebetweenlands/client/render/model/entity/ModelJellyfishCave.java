package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJellyfishCave extends ModelBase {
    ModelRenderer mesoglea_base;
    ModelRenderer mouth;
    ModelRenderer tentacles_back1a;
    ModelRenderer tentacles_right1a;
    ModelRenderer tentacles_front1a;
    ModelRenderer tentacles_left1a;
    ModelRenderer tentacles_front1b;
    ModelRenderer tentacles_front1c;
    ModelRenderer tentacles_left1b;
    ModelRenderer tentacles_left1c;
    ModelRenderer oral_arm1a;
    ModelRenderer oral_arm1b;
    ModelRenderer oral_arm1c;
    ModelRenderer oral_arm1d;
    ModelRenderer oral_arm1e;
    ModelRenderer tentacles_back1b;
    ModelRenderer tentacles_back1c;
    ModelRenderer tentacles_right1b;
    ModelRenderer tentacles_right1c;

    public ModelJellyfishCave() {
        textureWidth = 32;
        textureHeight = 32;
        tentacles_front1a = new ModelRenderer(this, 0, 11);
        tentacles_front1a.setRotationPoint(0.0F, 0.0F, -1.5F);
        tentacles_front1a.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_front1a, 0.091106186954104F, 0.0F, 0.0F);
        tentacles_left1a = new ModelRenderer(this, 0, 18);
        tentacles_left1a.setRotationPoint(1.5F, 0.0F, 0.0F);
        tentacles_left1a.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_left1a, 0.0F, 0.0F, 0.091106186954104F);
        tentacles_right1c = new ModelRenderer(this, 7, 24);
        tentacles_right1c.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_right1c.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_right1c, 0.0F, 0.0F, 0.091106186954104F);
        mouth = new ModelRenderer(this, 0, 7);
        mouth.setRotationPoint(0.0F, 0.0F, 0.0F);
        mouth.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F);
        tentacles_front1c = new ModelRenderer(this, 0, 17);
        tentacles_front1c.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_front1c.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_front1c, -0.091106186954104F, 0.0F, 0.0F);
        oral_arm1b = new ModelRenderer(this, 17, 4);
        oral_arm1b.setRotationPoint(0.0F, 4.0F, 0.0F);
        oral_arm1b.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 0, 0.0F);
        setRotateAngle(oral_arm1b, -0.5009094953223726F, 0.0F, 0.0F);
        oral_arm1e = new ModelRenderer(this, 17, 16);
        oral_arm1e.setRotationPoint(0.0F, 4.0F, 0.0F);
        oral_arm1e.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 0, 0.0F);
        setRotateAngle(oral_arm1e, 0.091106186954104F, 0.0F, 0.0F);
        tentacles_left1c = new ModelRenderer(this, 0, 24);
        tentacles_left1c.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_left1c.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_left1c, 0.0F, 0.0F, -0.091106186954104F);
        tentacles_right1b = new ModelRenderer(this, 7, 21);
        tentacles_right1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_right1b.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_right1b, 0.0F, 0.0F, 0.091106186954104F);
        mesoglea_base = new ModelRenderer(this, 0, 0);
        mesoglea_base.setRotationPoint(0.0F, 0.0F, 0.0F);
        mesoglea_base.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        oral_arm1c = new ModelRenderer(this, 17, 8);
        oral_arm1c.setRotationPoint(0.0F, 4.0F, 0.0F);
        oral_arm1c.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 0, 0.0F);
        setRotateAngle(oral_arm1c, 0.5009094953223726F, 0.0F, 0.0F);
        tentacles_right1a = new ModelRenderer(this, 7, 18);
        tentacles_right1a.setRotationPoint(-1.5F, 0.0F, 0.0F);
        tentacles_right1a.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_right1a, 0.0F, 0.0F, -0.091106186954104F);
        tentacles_back1a = new ModelRenderer(this, 7, 11);
        tentacles_back1a.setRotationPoint(0.0F, 0.0F, 1.5F);
        tentacles_back1a.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_back1a, -0.091106186954104F, 0.0F, 0.0F);
        tentacles_front1b = new ModelRenderer(this, 0, 14);
        tentacles_front1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_front1b.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_front1b, -0.091106186954104F, 0.0F, 0.0F);
        oral_arm1a = new ModelRenderer(this, 17, 0);
        oral_arm1a.setRotationPoint(0.0F, 1.0F, 0.0F);
        oral_arm1a.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 0, 0.0F);
        setRotateAngle(oral_arm1a, 0.22759093446006054F, 0.7853981633974483F, 0.0F);
        tentacles_back1c = new ModelRenderer(this, 7, 17);
        tentacles_back1c.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_back1c.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_back1c, 0.091106186954104F, 0.0F, 0.0F);
        tentacles_left1b = new ModelRenderer(this, 0, 21);
        tentacles_left1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_left1b.addBox(0.0F, 0.0F, -1.5F, 0, 3, 3, 0.0F);
        setRotateAngle(tentacles_left1b, 0.0F, 0.0F, -0.091106186954104F);
        oral_arm1d = new ModelRenderer(this, 17, 12);
        oral_arm1d.setRotationPoint(0.0F, 4.0F, 0.0F);
        oral_arm1d.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 0, 0.0F);
        setRotateAngle(oral_arm1d, -0.31869712141416456F, 0.0F, 0.0F);
        tentacles_back1b = new ModelRenderer(this, 7, 14);
        tentacles_back1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        tentacles_back1b.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        setRotateAngle(tentacles_back1b, 0.091106186954104F, 0.0F, 0.0F);
        mesoglea_base.addChild(tentacles_front1a);
        mesoglea_base.addChild(tentacles_left1a);
        tentacles_right1b.addChild(tentacles_right1c);
        tentacles_front1b.addChild(tentacles_front1c);
        oral_arm1a.addChild(oral_arm1b);
        oral_arm1d.addChild(oral_arm1e);
        tentacles_left1b.addChild(tentacles_left1c);
        tentacles_right1a.addChild(tentacles_right1b);
        oral_arm1b.addChild(oral_arm1c);
        tentacles_front1a.addChild(tentacles_front1b);
        mouth.addChild(oral_arm1a);
        tentacles_back1b.addChild(tentacles_back1c);
        tentacles_left1a.addChild(tentacles_left1b);
        oral_arm1c.addChild(oral_arm1d);
        tentacles_back1a.addChild(tentacles_back1b);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        mouth.render(scale);
        mesoglea_base.render(scale);
        tentacles_right1a.render(scale);
        tentacles_back1a.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
