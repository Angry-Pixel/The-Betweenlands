package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class ModelDecayPitTargetShield extends ModelBase {
    ModelRenderer base_mid;
    ModelRenderer base_left;
    ModelRenderer base_right;
    ModelRenderer upperplate_mid;
    ModelRenderer lowerplate_mid;
    ModelRenderer upperplate_left;
    ModelRenderer upperplate_right;
    ModelRenderer lowerplate_left;
    ModelRenderer lowerplate_right;

    public ModelDecayPitTargetShield() {
        textureWidth = 64;
        textureHeight = 64;
        lowerplate_left = new ModelRenderer(this, 17, 31);
        lowerplate_left.setRotationPoint(3.0F, 0.0F, 0.0F);
        lowerplate_left.addBox(0.0F, 0.0F, 0.0F, 4, 5, 2, 0.0F);
        setRotateAngle(lowerplate_left, 0.0F, -0.18203784098300857F, 0.0F);
        base_mid = new ModelRenderer(this, 0, 0);
        base_mid.setRotationPoint(0.0F, 16.0F, 0.0F);
        base_mid.addBox(-3.0F, -1.5F, -3.0F, 6, 3, 3, 0.0F);
        upperplate_left = new ModelRenderer(this, 0, 31);
        upperplate_left.setRotationPoint(3.0F, 0.0F, 0.0F);
        upperplate_left.addBox(0.0F, -5.0F, 0.0F, 4, 5, 2, 0.0F);
        setRotateAngle(upperplate_left, 0.0F, -0.18203784098300857F, 0.0F);
        lowerplate_mid = new ModelRenderer(this, 17, 21);
        lowerplate_mid.setRotationPoint(0.0F, 1.5F, -2.5F);
        lowerplate_mid.addBox(-3.0F, 0.0F, 0.0F, 6, 7, 2, 0.0F);
        setRotateAngle(lowerplate_mid, 0.22759093446006054F, 0.0F, 0.0F);
        base_right = new ModelRenderer(this, 0, 14);
        base_right.setRotationPoint(-3.0F, 0.0F, -3.0F);
        base_right.addBox(-5.0F, -1.49F, 0.0F, 5, 3, 3, 0.0F);
        setRotateAngle(base_right, 0.0F, 0.18203784098300857F, 0.0F);
        upperplate_right = new ModelRenderer(this, 0, 39);
        upperplate_right.setRotationPoint(-3.0F, 0.0F, 0.0F);
        upperplate_right.addBox(-4.0F, -5.0F, 0.0F, 4, 5, 2, 0.0F);
        setRotateAngle(upperplate_right, 0.0F, 0.18203784098300857F, 0.0F);
        base_left = new ModelRenderer(this, 0, 7);
        base_left.setRotationPoint(3.0F, 0.0F, -3.0F);
        base_left.addBox(0.0F, -1.49F, 0.0F, 5, 3, 3, 0.0F);
        setRotateAngle(base_left, 0.0F, -0.18203784098300857F, 0.0F);
        upperplate_mid = new ModelRenderer(this, 0, 21);
        upperplate_mid.setRotationPoint(0.0F, -1.5F, -2.5F);
        upperplate_mid.addBox(-3.0F, -7.0F, 0.0F, 6, 7, 2, 0.0F);
        setRotateAngle(upperplate_mid, -0.18203784098300857F, 0.0F, 0.0F);
        lowerplate_right = new ModelRenderer(this, 17, 39);
        lowerplate_right.setRotationPoint(-3.0F, 0.0F, 0.0F);
        lowerplate_right.addBox(-4.0F, 0.0F, 0.0F, 4, 5, 2, 0.0F);
        setRotateAngle(lowerplate_right, 0.0F, 0.18203784098300857F, 0.0F);
        lowerplate_mid.addChild(lowerplate_left);
        upperplate_mid.addChild(upperplate_left);
        base_mid.addChild(lowerplate_mid);
        base_mid.addChild(base_right);
        upperplate_mid.addChild(upperplate_right);
        base_mid.addChild(base_left);
        base_mid.addChild(upperplate_mid);
        lowerplate_mid.addChild(lowerplate_right);
    }

    public void render(float scale) { 
    	base_mid.render(scale);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
