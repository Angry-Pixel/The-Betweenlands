package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBloodSnail extends ModelBase {
    ModelRenderer body_base;
    ModelRenderer tail;
    ModelRenderer head;
    ModelRenderer shell1;
    ModelRenderer snout_i_guess;
    ModelRenderer sensorright1;
    ModelRenderer sensorleft1;
    ModelRenderer sensorright2;
    ModelRenderer sensorleft2;
    ModelRenderer shell2;
    ModelRenderer shell3;
    ModelRenderer shell4;
    ModelRenderer shell5;

    public ModelBloodSnail() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shell3 = new ModelRenderer(this, 21, 17);
        this.shell3.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.shell3.addBox(-2.5F, 0.0F, 0.0F, 5, 5, 2, 0.0F);
        this.setRotation(shell3, -0.091106186954104F, 0.0F, 0.0F);
        this.shell1 = new ModelRenderer(this, 21, 0);
        this.shell1.setRotationPoint(0.0F, -2.0F, 1.0F);
        this.shell1.addBox(-2.5F, -3.0F, 0.0F, 5, 5, 1, 0.0F);
        this.setRotation(shell1, 0.5462880558742251F, -0.045553093477052F, 0.18203784098300857F);
        this.shell4 = new ModelRenderer(this, 21, 25);
        this.shell4.setRotationPoint(0.0F, 0.5F, 2.0F);
        this.shell4.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 2, 0.0F);
        this.setRotation(shell4, -0.091106186954104F, 0.0F, 0.0F);
        this.shell5 = new ModelRenderer(this, 34, 0);
        this.shell5.setRotationPoint(0.0F, 0.5F, 2.0F);
        this.shell5.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.setRotation(shell5, -0.091106186954104F, 0.0F, 0.0F);
        this.sensorright1 = new ModelRenderer(this, 0, 25);
        this.sensorright1.setRotationPoint(-1.5F, 0.0F, -1.5F);
        this.sensorright1.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotation(sensorright1, 0.8651597102135892F, 0.6829473363053812F, 0.0F);
        this.snout_i_guess = new ModelRenderer(this, 0, 21);
        this.snout_i_guess.setRotationPoint(0.0F, 0.5F, -2.0F);
        this.snout_i_guess.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F);
        this.setRotation(snout_i_guess, 0.4553564018453205F, 0.0F, 0.0F);
        this.sensorleft1 = new ModelRenderer(this, 5, 25);
        this.sensorleft1.setRotationPoint(1.5F, 0.0F, -1.5F);
        this.sensorleft1.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotation(sensorleft1, 0.8651597102135892F, -0.6829473363053812F, 0.0F);
        this.sensorleft2 = new ModelRenderer(this, 7, 21);
        this.sensorleft2.setRotationPoint(0.5F, 1.5F, 0.0F);
        this.sensorleft2.addBox(-0.5F, 0.0F, -2.0F, 1, 0, 2, 0.0F);
        this.setRotation(sensorleft2, -0.40980330836826856F, -0.5918411493512771F, -0.18203784098300857F);
        this.sensorright2 = new ModelRenderer(this, 5, 21);
        this.sensorright2.setRotationPoint(-0.5F, 1.5F, 0.0F);
        this.sensorright2.addBox(-0.5F, 0.0F, -2.0F, 1, 0, 2, 0.0F);
        this.setRotation(sensorright2, -0.40980330836826856F, 0.5918411493512771F, 0.18203784098300857F);
        this.head = new ModelRenderer(this, 0, 16);
        this.head.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.head.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
        this.setRotation(head, 0.091106186954104F, 0.0F, 0.0F);
        this.tail = new ModelRenderer(this, 0, 10);
        this.tail.setRotationPoint(0.0F, -0.2F, 6.0F);
        this.tail.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
        this.setRotation(tail, 0.091106186954104F, 0.0F, 0.0F);
        this.shell2 = new ModelRenderer(this, 21, 7);
        this.shell2.setRotationPoint(0.0F, -3.0F, 1.0F);
        this.shell2.addBox(-3.0F, -0.5F, 0.0F, 6, 6, 3, 0.0F);
        this.setRotation(shell2, -0.091106186954104F, 0.0F, 0.0F);
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 24.0F, -3.0F);
        this.body_base.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 6, 0.0F);
        this.setRotation(body_base, -0.091106186954104F, 0.0F, 0.0F);
        this.shell2.addChild(this.shell3);
        this.body_base.addChild(this.shell1);
        this.shell3.addChild(this.shell4);
        this.shell4.addChild(this.shell5);
        this.head.addChild(this.sensorright1);
        this.head.addChild(this.snout_i_guess);
        this.head.addChild(this.sensorleft1);
        this.snout_i_guess.addChild(this.sensorleft2);
        this.snout_i_guess.addChild(this.sensorright2);
        this.body_base.addChild(this.head);
        this.body_base.addChild(this.tail);
        this.shell1.addChild(this.shell2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        this.body_base.render(unitPixel);
    }

    public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        sensorleft1.rotateAngleX = sensorleft2.rotateAngleX = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle + 0.5F;
        sensorright1.rotateAngleX = sensorright2.rotateAngleX = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle + 0.5F;
        sensorleft1.rotateAngleY = sensorleft2.rotateAngleY = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle + 0.2F;
        sensorright1.rotateAngleY = sensorright2.rotateAngleY = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle - 0.2F;
    }
}
