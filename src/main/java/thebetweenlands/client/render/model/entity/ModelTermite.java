package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

public class ModelTermite extends MowzieModelBase {
    public MowzieModelRenderer body_base;
    public MowzieModelRenderer head;
    public MowzieModelRenderer legleft_f1;
    public MowzieModelRenderer legleft_m1;
    public MowzieModelRenderer legleft_b1;
    public MowzieModelRenderer legright_f1;
    public MowzieModelRenderer legright_m1;
    public MowzieModelRenderer legright_b1;
    public MowzieModelRenderer body1;
    public MowzieModelRenderer body2;
    public MowzieModelRenderer body3;
    public MowzieModelRenderer body4;
    public MowzieModelRenderer tail1;
    public MowzieModelRenderer leaf1;
    public MowzieModelRenderer leaf1b;
    public MowzieModelRenderer jawleft;
    public MowzieModelRenderer jawright;
    public MowzieModelRenderer jawmid;
    public MowzieModelRenderer sensorleft1;
    public MowzieModelRenderer sensorright1;
    public MowzieModelRenderer leafcrane1;
    public MowzieModelRenderer sensorleft1b;
    public MowzieModelRenderer sensorright1b;
    public MowzieModelRenderer leafcrane1b;
    public MowzieModelRenderer legleft_f2;
    public MowzieModelRenderer legleft_m2;
    public MowzieModelRenderer legleft_b2;
    public MowzieModelRenderer legright_f2;
    public MowzieModelRenderer legright_m2;
    public MowzieModelRenderer legright_b2;
    private MowzieModelRenderer[] tail;

    public ModelTermite() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.sensorleft1 = new MowzieModelRenderer(this, 36, 0);
        this.sensorleft1.setRotationPoint(2.0F, -2.0F, -2.0F);
        this.sensorleft1.addBox(-0.5F, 0.0F, -3.0F, 1, 0, 4, 0.0F);
        this.setRotateAngle(sensorleft1, -0.7740535232594852F, -0.5009094953223726F, 0.36425021489121656F);
        this.legleft_m2 = new MowzieModelRenderer(this, 65, 8);
        this.legleft_m2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legleft_m2.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, 0.0F);
        this.setRotateAngle(legleft_m2, 0.0F, 0.0F, 1.4114477660878142F);
        this.body_base = new MowzieModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 21.0F, -3.5F);
        this.body_base.addBox(-2.5F, -4.0F, -2.0F, 5, 4, 4, 0.0F);
        this.setRotateAngle(body_base, -0.091106186954104F, 0.0F, 0.0F);
        this.body1 = new MowzieModelRenderer(this, 0, 9);
        this.body1.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.body1.addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4, 0.0F);
        this.setRotateAngle(body1, 0.31869712141416456F, 0.0F, 0.0F);
        this.legleft_b2 = new MowzieModelRenderer(this, 65, 16);
        this.legleft_b2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legleft_b2.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.0F);
        this.setRotateAngle(legleft_b2, 0.0F, 0.0F, 1.3658946726107624F);
        this.body4 = new MowzieModelRenderer(this, 0, 36);
        this.body4.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.body4.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 3, 0.0F);
        this.setRotateAngle(body4, 0.7740535232594852F, 0.0F, 0.0F);
        this.body3 = new MowzieModelRenderer(this, 0, 27);
        this.body3.setRotationPoint(-0.01F, 0.0F, 4.0F);
        this.body3.addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4, 0.0F);
        this.setRotateAngle(body3, 0.31869712141416456F, 0.0F, 0.0F);
        this.legleft_f2 = new MowzieModelRenderer(this, 65, 0);
        this.legleft_f2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legleft_f2.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, 0.0F);
        this.setRotateAngle(legleft_f2, 0.0F, 0.0F, 1.3658946726107624F);
        this.legleft_b1 = new MowzieModelRenderer(this, 60, 16);
        this.legleft_b1.setRotationPoint(2.5F, 20.0F, -2.5F);
        this.legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft_b1, 0.8196066167365371F, 0.40980330836826856F, -1.8212510744560826F);
        this.legright_b2 = new MowzieModelRenderer(this, 75, 16);
        this.legright_b2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legright_b2.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.0F);
        this.setRotateAngle(legright_b2, 0.0F, 0.0F, -1.3658946726107624F);
        this.tail1 = new MowzieModelRenderer(this, 0, 43);
        this.tail1.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.tail1.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(tail1, 0.31869712141416456F, 0.0F, 0.0F);
        this.leaf1 = new MowzieModelRenderer(this, -3, 48);
        this.leaf1.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.leaf1.addBox(-2.5F, 0.0F, 0.0F, 5, 0, 3, 0.0F);
        this.setRotateAngle(leaf1, 1.0927506446736497F, 0.0F, 0.0F);
        this.head = new MowzieModelRenderer(this, 21, 0);
        this.head.setRotationPoint(0.0F, 18.0F, -3.5F);
        this.head.addBox(-2.0F, -2.0F, -5.0F, 4, 3, 5, 0.0F);
        this.setRotateAngle(head, 0.31869712141416456F, 0.0F, 0.0F);
        this.leafcrane1 = new MowzieModelRenderer(this, 37, 10);
        this.leafcrane1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.leafcrane1.addBox(-3.0F, 0.0F, 0.0F, 6, 0, 3, 0.0F);
        this.setRotateAngle(leafcrane1, 0.22759093446006054F, 0.0F, 0.0F);
        this.sensorleft1b = new MowzieModelRenderer(this, 39, 0);
        this.sensorleft1b.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.sensorleft1b.addBox(-0.5F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(sensorleft1b, 0.7285004297824331F, 0.0F, 0.0F);
        this.jawmid = new MowzieModelRenderer(this, 21, 21);
        this.jawmid.setRotationPoint(0.0F, -2.0F, -4.5F);
        this.jawmid.addBox(-0.5F, -1.5F, -3.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(jawmid, 0.136659280431156F, 0.0F, 0.0F);
        this.legright_m2 = new MowzieModelRenderer(this, 75, 8);
        this.legright_m2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legright_m2.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, 0.0F);
        this.setRotateAngle(legright_m2, 0.0F, 0.0F, -1.4114477660878142F);
        this.legright_f2 = new MowzieModelRenderer(this, 75, 0);
        this.legright_f2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.legright_f2.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, 0.0F);
        this.setRotateAngle(legright_f2, 0.0F, 0.0F, -1.3658946726107624F);
        this.legleft_m1 = new MowzieModelRenderer(this, 60, 8);
        this.legleft_m1.setRotationPoint(2.5F, 20.1F, -3.5F);
        this.legleft_m1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft_m1, 0.18203784098300857F, 0.136659280431156F, -1.9123572614101867F);
        this.leafcrane1b = new MowzieModelRenderer(this, 37, 14);
        this.leafcrane1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leafcrane1b.addBox(-3.0F, 0.0F, 0.0F, 6, 0, 3, 0.0F);
        this.setRotateAngle(leafcrane1b, 0.31869712141416456F, 0.0F, 0.0F);
        this.sensorright1b = new MowzieModelRenderer(this, 39, 5);
        this.sensorright1b.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.sensorright1b.addBox(-3.5F, 0.0F, -4.0F, 4, 0, 4, 0.0F);
        this.setRotateAngle(sensorright1b, 0.7285004297824331F, 0.0F, 0.0F);
        this.legright_f1 = new MowzieModelRenderer(this, 70, 0);
        this.legright_f1.setRotationPoint(-2.5F, 20.0F, -4.5F);
        this.legright_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright_f1, -0.7285004297824331F, 0.31869712141416456F, 1.6845917940249266F);
        this.sensorright1 = new MowzieModelRenderer(this, 36, 5);
        this.sensorright1.setRotationPoint(-2.0F, -2.0F, -2.0F);
        this.sensorright1.addBox(-0.5F, 0.0F, -3.0F, 1, 0, 4, 0.0F);
        this.setRotateAngle(sensorright1, -0.7740535232594852F, 0.5009094953223726F, -0.36425021489121656F);
        this.body2 = new MowzieModelRenderer(this, 0, 18);
        this.body2.setRotationPoint(0.01F, 0.0F, 4.0F);
        this.body2.addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4, 0.0F);
        this.setRotateAngle(body2, 0.31869712141416456F, 0.0F, 0.0F);
        this.jawleft = new MowzieModelRenderer(this, 21, 9);
        this.jawleft.setRotationPoint(1.0F, 0.0F, -4.5F);
        this.jawleft.addBox(0.0F, -0.5F, -3.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(jawleft, -0.18203784098300857F, 0.22759093446006054F, 0.31869712141416456F);
        this.jawright = new MowzieModelRenderer(this, 21, 15);
        this.jawright.setRotationPoint(-1.0F, 0.0F, -4.5F);
        this.jawright.addBox(-2.0F, -0.5F, -3.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(jawright, -0.18203784098300857F, -0.22759093446006054F, -0.31869712141416456F);
        this.legright_b1 = new MowzieModelRenderer(this, 70, 16);
        this.legright_b1.setRotationPoint(-2.5F, 20.0F, -2.5F);
        this.legright_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright_b1, 0.8196066167365371F, -0.40980330836826856F, 1.8212510744560826F);
        this.legleft_f1 = new MowzieModelRenderer(this, 60, 0);
        this.legleft_f1.setRotationPoint(2.5F, 20.0F, -4.5F);
        this.legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft_f1, -0.7285004297824331F, -0.31869712141416456F, -1.6845917940249266F);
        this.leaf1b = new MowzieModelRenderer(this, -4, 52);
        this.leaf1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.leaf1b.addBox(-2.5F, 0.0F, 0.0F, 5, 0, 4, 0.0F);
        this.setRotateAngle(leaf1b, 0.18203784098300857F, 0.0F, 0.0F);
        this.legright_m1 = new MowzieModelRenderer(this, 70, 8);
        this.legright_m1.setRotationPoint(-2.5F, 20.1F, -3.5F);
        this.legright_m1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright_m1, 0.18203784098300857F, -0.136659280431156F, 1.9123572614101867F);
        this.head.addChild(this.sensorleft1);
        this.legleft_m1.addChild(this.legleft_m2);
        this.body_base.addChild(this.body1);
        this.legleft_b1.addChild(this.legleft_b2);
        this.body3.addChild(this.body4);
        this.body2.addChild(this.body3);
        this.legleft_f1.addChild(this.legleft_f2);
        this.legright_b1.addChild(this.legright_b2);
        this.body4.addChild(this.tail1);
        this.tail1.addChild(this.leaf1);
        this.head.addChild(this.leafcrane1);
        this.sensorleft1.addChild(this.sensorleft1b);
        this.head.addChild(this.jawmid);
        this.legright_m1.addChild(this.legright_m2);
        this.legright_f1.addChild(this.legright_f2);
        this.leafcrane1.addChild(this.leafcrane1b);
        this.sensorright1.addChild(this.sensorright1b);
        this.head.addChild(this.sensorright1);
        this.body1.addChild(this.body2);
        this.head.addChild(this.jawleft);
        this.head.addChild(this.jawright);
        this.leaf1.addChild(this.leaf1b);

        setInitPose();

        tail = new MowzieModelRenderer[]{tail1, body4, body3, body2, body1};
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        this.body_base.render(unitPixel);
        this.legleft_b1.render(unitPixel);
        this.head.render(unitPixel);
        this.legleft_m1.render(unitPixel);
        this.legright_f1.render(unitPixel);
        this.legright_b1.render(unitPixel);
        this.legleft_f1.render(unitPixel);
        this.legright_m1.render(unitPixel);
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
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        float movement = MathHelper.cos(limbSwing * 1.25F + (float) Math.PI) * 2F * limbSwingAngle *0.5F;
        legright_f1.rotateAngleZ = movement + 1.95F;
        legright_m1.rotateAngleZ = -movement + 1.95F;
        legright_b1.rotateAngleZ = movement + 1.95F;
        legleft_f1.rotateAngleZ = movement - 1.95F;
        legleft_m1.rotateAngleZ = -movement - 1.95F;
        legleft_b1.rotateAngleZ = movement - 1.95F;

        legright_f1.rotateAngleY = movement;
        legright_m1.rotateAngleY = -movement;
        legright_b1.rotateAngleY = movement;
        legleft_f1.rotateAngleY = movement;
        legleft_m1.rotateAngleY = -movement;
        legleft_b1.rotateAngleY = movement;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        super.setLivingAnimations(entity, swing, speed, partialRenderTicks);
        setToInitPose();
        float frame = entity.ticksExisted + partialRenderTicks;
        float mandibleControl = (float) ((Math.sin(0.1 * frame - Math.cos(0.1 * frame)) + 1) / 2);
        chainWave(tail, 0.4f, 0.1f, 0, frame, 1);
        chainSwing(tail, 0.2f, 0.2f, 1, frame, 1);
//        swing(jawleft, 2, 0.3f * mandibleControl, false, 0, 0, frame, 1);
        jawleft.rotateAngleY -= 1 * mandibleControl * Math.cos(2 * frame);
        jawright.rotateAngleY += 1 * mandibleControl * Math.cos(2 * frame);
        jawmid.rotateAngleX -= 1 * mandibleControl * Math.cos(2 * frame);
    }
}
