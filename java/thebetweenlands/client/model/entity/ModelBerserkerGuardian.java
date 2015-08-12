package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.client.model.MowzieModelRenderer;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;

/**
 * BLTempleGuardian3 - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelBerserkerGuardian extends MowzieModelBase {
    public MowzieModelRenderer waist_invisible;
    public MowzieModelRenderer body_base;
    public MowzieModelRenderer legright_1;
    public MowzieModelRenderer legleft_1;
    public MowzieModelRenderer armouredskirt_back;
    public MowzieModelRenderer armouredskirt_right;
    public MowzieModelRenderer armouredskirt_left;
    public MowzieModelRenderer headconnection;
    public MowzieModelRenderer chest_invisible;
    public MowzieModelRenderer headbase;
    public MowzieModelRenderer facepiece;
    public MowzieModelRenderer hat;
    public MowzieModelRenderer nose;
    public MowzieModelRenderer chestpiece_right;
    public MowzieModelRenderer chestpiece_mid1;
    public MowzieModelRenderer chestpiece_left;
    public MowzieModelRenderer shoulder_right;
    public MowzieModelRenderer armright_1;
    public MowzieModelRenderer armright_2;
    public MowzieModelRenderer hammerhandle_right;
    public MowzieModelRenderer hammerhead1_right;
    public MowzieModelRenderer pommel1_right;
    public MowzieModelRenderer pommel2_right;
    public MowzieModelRenderer hammerhead2_right;
    public MowzieModelRenderer chestpiece_mid2;
    public MowzieModelRenderer shoulder_left;
    public MowzieModelRenderer armleft_1;
    public MowzieModelRenderer armleft_2;
    public MowzieModelRenderer hammerhandle_left;
    public MowzieModelRenderer headpommel1_left;
    public MowzieModelRenderer pommel1_left;
    public MowzieModelRenderer hammerhead1_left;
    public MowzieModelRenderer hammerhead2_left;
    public MowzieModelRenderer legright_2;
    public MowzieModelRenderer footpieceright;
    public MowzieModelRenderer legleft_2;
    public MowzieModelRenderer footpieceleft;
    public MowzieModelRenderer armouredskirt_edge;
    public MowzieModelRenderer headJoint;

    public ModelBerserkerGuardian() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.headpommel1_left = new MowzieModelRenderer(this, 42, 78);
        this.headpommel1_left.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.headpommel1_left.addBox(-1.5F, -8.0F, -1.5F, 3, 8, 3, 0.0F);
        this.legright_2 = new MowzieModelRenderer(this, 100, 13);
        this.legright_2.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.legright_2.addBox(-2.5F, -0.7F, -2.5F, 5, 10, 5, 0.0F);
        this.setRotateAngle(legright_2, 0.09843656981248018F, -0.045553093477052F, -0.06998770300497262F);
        this.body_base = new MowzieModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 0.21999999999999997F, 3.03F);
        this.body_base.addBox(-5.0F, -8.0F, -7.0F, 10, 8, 8, 0.0F);
        this.setRotateAngle(body_base, 0.091106186954104F, 0.0F, 0.0F);
        this.shoulder_left = new MowzieModelRenderer(this, 33, 36);
        this.shoulder_left.setRotationPoint(5.0F, -7.0F, 5.0F);
        this.shoulder_left.addBox(-0.5F, -1.5F, -2.5F, 5, 6, 6, 0.0F);
        this.setRotateAngle(shoulder_left, -0.18203784098300857F, -0.045553093477052F, -0.18203784098300857F);
        this.headbase = new MowzieModelRenderer(this, 150, 9);
        this.headbase.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headbase.addBox(-4.0F, -7.0F, -6.0F, 8, 6, 8, 0.0F);
        this.legright_1 = new MowzieModelRenderer(this, 100, 0);
        this.legright_1.setRotationPoint(-2.5F, -0.004985738891883029F, -0.0030482055673419772F);
        this.legright_1.addBox(-2.0F, -1.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(legright_1, -0.09022613734280603F, 0.13194747126916856F, 0.057855201805975254F);
        this.headJoint = new MowzieModelRenderer(this, 0, 0);
        this.headJoint.setRotationPoint(0.0F, -7.66F, -2.58F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.armright_1 = new MowzieModelRenderer(this, 0, 49);
        this.armright_1.setRotationPoint(-2.0F, 4.0F, 0.5F);
        this.armright_1.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(armright_1, 0.0F, -0.045553093477052F, -0.045553093477052F);
        this.waist_invisible = new MowzieModelRenderer(this, 140, 100);
        this.waist_invisible.setRotationPoint(0.0F, 7.78F, 1.9700000000000002F);
        this.waist_invisible.addBox(-5.0F, -0.78F, -3.97F, 0, 0, 0, 0.0F);
        this.chestpiece_right = new MowzieModelRenderer(this, 0, 17);
        this.chestpiece_right.setRotationPoint(0.0F, 3.256872722792262F, -6.603683036966476F);
        this.chestpiece_right.addBox(-6.0F, -8.0F, 0.0F, 6, 8, 10, 0.0F);
        this.setRotateAngle(chestpiece_right, 0.18229715219141673F, 0.10074570373475358F, -0.0822866827200876F);
        this.armright_2 = new MowzieModelRenderer(this, 0, 62);
        this.armright_2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.armright_2.addBox(-2.5F, -1.0F, -2.5F, 5, 10, 5, 0.0F);
        this.setRotateAngle(armright_2, -0.136659280431156F, -0.045553093477052F, 0.0F);
        this.legleft_2 = new MowzieModelRenderer(this, 125, 13);
        this.legleft_2.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.legleft_2.addBox(-2.5F, -0.7F, -2.5F, 5, 10, 5, 0.0F);
        this.setRotateAngle(legleft_2, 0.09843656981248018F, 0.045553093477052F, 0.06998770300497262F);
        this.footpieceleft = new MowzieModelRenderer(this, 125, 29);
        this.footpieceleft.setRotationPoint(0.0F, 7.3F, -2.5F);
        this.footpieceleft.addBox(-2.49F, 0.0F, -3.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(footpieceleft, 0.18203784098300857F, 0.0F, 0.0F);
        this.pommel1_right = new MowzieModelRenderer(this, 9, 78);
        this.pommel1_right.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.pommel1_right.addBox(-1.5F, 0.0F, -1.5F, 3, 2, 3, 0.0F);
        this.armouredskirt_left = new MowzieModelRenderer(this, 121, 52);
        this.armouredskirt_left.setRotationPoint(3.0F, -0.5029120894944759F, -0.048538307519127244F);
        this.armouredskirt_left.addBox(0.0F, 0.0F, -4.01F, 2, 5, 8, 0.0F);
        this.setRotateAngle(armouredskirt_left, 0.08960893524327929F, 0.016471265678173405F, -0.18129934381238744F);
        this.hammerhead1_left = new MowzieModelRenderer(this, 33, 95);
        this.hammerhead1_left.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.hammerhead1_left.addBox(-2.5F, 0.0F, -4.5F, 5, 5, 7, 0.0F);
        this.pommel1_left = new MowzieModelRenderer(this, 55, 78);
        this.pommel1_left.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.pommel1_left.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
        this.headconnection = new MowzieModelRenderer(this, 150, 0);
        this.headconnection.setRotationPoint(0.0F, 0.0027307498269166786F, 0.002651046064242879F);
        this.headconnection.addBox(-4.0F, -1.0F, -4.0F, 8, 2, 6, 0.0F);
        this.armleft_2 = new MowzieModelRenderer(this, 33, 62);
        this.armleft_2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.armleft_2.addBox(-2.5F, -1.0F, -2.5F, 5, 10, 5, 0.0F);
        this.setRotateAngle(armleft_2, -0.136659280431156F, 0.045553093477052F, 0.0F);
        this.armouredskirt_edge = new MowzieModelRenderer(this, 100, 48);
        this.armouredskirt_edge.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.armouredskirt_edge.addBox(-4.0F, 0.0F, -2.0F, 8, 1, 2, 0.0F);
        this.footpieceright = new MowzieModelRenderer(this, 100, 29);
        this.footpieceright.setRotationPoint(0.0F, 7.3F, -2.5F);
        this.footpieceright.addBox(-2.51F, 0.0F, -3.0F, 5, 2, 3, 0.0F);
        this.setRotateAngle(footpieceright, 0.18203784098300857F, 0.0F, 0.0F);
        this.hammerhandle_right = new MowzieModelRenderer(this, 0, 78);
        this.hammerhandle_right.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.hammerhandle_right.addBox(-1.0F, -13.0F, -1.0F, 2, 23, 2, 0.0F);
        this.setRotateAngle(hammerhandle_right, 1.5025539530419183F, 0.0F, 0.0F);
        this.armleft_1 = new MowzieModelRenderer(this, 33, 49);
        this.armleft_1.setRotationPoint(2.0F, 4.0F, 0.5F);
        this.armleft_1.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(armleft_1, 0.0F, 0.045553093477052F, 0.045553093477052F);
        this.hammerhead2_right = new MowzieModelRenderer(this, 0, 118);
        this.hammerhead2_right.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.hammerhead2_right.addBox(-1.5F, 0.0F, -0.5F, 3, 5, 1, 0.0F);
        this.hammerhead1_right = new MowzieModelRenderer(this, 0, 104);
        this.hammerhead1_right.setRotationPoint(0.0F, -12.0F, 0.0F);
        this.hammerhead1_right.addBox(-2.5F, 0.0F, -4.5F, 5, 5, 8, 0.0F);
        this.chestpiece_mid1 = new MowzieModelRenderer(this, 66, 17);
        this.chestpiece_mid1.setRotationPoint(0.0F, -6.077341561538823F, -6.653116772305529F);
        this.chestpiece_mid1.addBox(-3.0F, 0.0F, -3.0F, 6, 10, 5, 0.0F);
        this.setRotateAngle(chestpiece_mid1, 0.31869712141416456F, 0.0F, 0.0F);
        this.chestpiece_mid2 = new MowzieModelRenderer(this, 66, 33);
        this.chestpiece_mid2.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.chestpiece_mid2.addBox(-3.01F, 0.0F, 0.0F, 6, 3, 7, 0.0F);
        this.setRotateAngle(chestpiece_mid2, -0.136659280431156F, 0.0F, 0.0F);
        this.legleft_1 = new MowzieModelRenderer(this, 125, 0);
        this.legleft_1.setRotationPoint(2.5F, -0.004985738891883029F, -0.0030482055673419772F);
        this.legleft_1.addBox(-2.0F, -1.0F, -2.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(legleft_1, -0.09022613734280603F, -0.13194747126916856F, -0.057855201805975254F);
        this.chest_invisible = new MowzieModelRenderer(this, 140, 100);
        this.chest_invisible.setRotationPoint(0.0F, -9.64F, -1.13F);
        this.chest_invisible.addBox(-6.5F, -5.0F, -7.0F, 0, 0, 0, 0.0F);
        this.setRotateAngle(chest_invisible, -0.091106186954104F, 0.0F, 0.0F);
        this.hammerhead2_left = new MowzieModelRenderer(this, 33, 108);
        this.hammerhead2_left.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.hammerhead2_left.addBox(-1.5F, -1.5F, 2.5F, 3, 3, 2, 0.0F);
        this.shoulder_right = new MowzieModelRenderer(this, 0, 36);
        this.shoulder_right.setRotationPoint(-5.0F, -7.0F, 5.0F);
        this.shoulder_right.addBox(-4.5F, -1.5F, -2.5F, 5, 6, 6, 0.0F);
        this.setRotateAngle(shoulder_right, -0.18203784098300857F, 0.045553093477052F, 0.18203784098300857F);
        this.hammerhandle_left = new MowzieModelRenderer(this, 33, 78);
        this.hammerhandle_left.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.hammerhandle_left.addBox(-1.0F, -5.0F, -1.0F, 2, 14, 2, 0.0F);
        this.setRotateAngle(hammerhandle_left, 1.5025539530419183F, 0.0F, 0.0F);
        this.hat = new MowzieModelRenderer(this, 150, 34);
        this.hat.setRotationPoint(0.0F, -5.5F, -2.0F);
        this.hat.addBox(-4.5F, -4.0F, -4.5F, 9, 4, 9, 0.0F);
        this.setRotateAngle(hat, -0.136659280431156F, 0.0F, 0.0F);
        this.nose = new MowzieModelRenderer(this, 150, 29);
        this.nose.setRotationPoint(0.0F, -2.0F, -2.0F);
        this.nose.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(nose, -0.18203784098300857F, 0.0F, 0.0F);
        this.armouredskirt_back = new MowzieModelRenderer(this, 100, 40);
        this.armouredskirt_back.setRotationPoint(0.0F, -0.8668329051087553F, 3.934872497301616F);
        this.armouredskirt_back.addBox(-5.0F, 0.0F, -2.0F, 10, 5, 2, 0.0F);
        this.setRotateAngle(armouredskirt_back, 0.18221237390820796F, 0.0F, 0.0F);
        this.chestpiece_left = new MowzieModelRenderer(this, 33, 17);
        this.chestpiece_left.setRotationPoint(0.0F, 3.256872722792262F, -6.603683036966476F);
        this.chestpiece_left.addBox(0.0F, -8.0F, 0.0F, 6, 8, 10, 0.0F);
        this.setRotateAngle(chestpiece_left, 0.1822812264057397F, -0.09900762508362462F, 0.08244640259162492F);
        this.armouredskirt_right = new MowzieModelRenderer(this, 100, 52);
        this.armouredskirt_right.setRotationPoint(-3.0F, -0.5029120894944759F, -0.048538307519127244F);
        this.armouredskirt_right.addBox(-2.0F, 0.0F, -4.01F, 2, 5, 8, 0.0F);
        this.setRotateAngle(armouredskirt_right, 0.08960893524327929F, -0.016471265678173405F, 0.18129934381238744F);
        this.pommel2_right = new MowzieModelRenderer(this, 9, 84);
        this.pommel2_right.setRotationPoint(0.0F, 10.0F, 0.0F);
        this.pommel2_right.addBox(-1.5F, 0.0F, -1.5F, 3, 2, 3, 0.0F);
        this.facepiece = new MowzieModelRenderer(this, 150, 24);
        this.facepiece.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.facepiece.addBox(-3.0F, -1.0F, -2.0F, 6, 2, 2, 0.0F);
        this.hammerhandle_left.addChild(this.headpommel1_left);
        this.legright_1.addChild(this.legright_2);
        this.waist_invisible.addChild(this.body_base);
        this.chestpiece_left.addChild(this.shoulder_left);
        this.headconnection.addChild(this.headbase);
        this.waist_invisible.addChild(this.legright_1);
        this.chest_invisible.addChild(this.headJoint);
        this.shoulder_right.addChild(this.armright_1);
        this.chest_invisible.addChild(this.chestpiece_right);
        this.armright_1.addChild(this.armright_2);
        this.legleft_1.addChild(this.legleft_2);
        this.legleft_2.addChild(this.footpieceleft);
        this.hammerhandle_right.addChild(this.pommel1_right);
        this.waist_invisible.addChild(this.armouredskirt_left);
        this.headpommel1_left.addChild(this.hammerhead1_left);
        this.hammerhandle_left.addChild(this.pommel1_left);
        this.headJoint.addChild(this.headconnection);
        this.armleft_1.addChild(this.armleft_2);
        this.armouredskirt_back.addChild(this.armouredskirt_edge);
        this.legright_2.addChild(this.footpieceright);
        this.armright_2.addChild(this.hammerhandle_right);
        this.shoulder_left.addChild(this.armleft_1);
        this.hammerhead1_right.addChild(this.hammerhead2_right);
        this.hammerhandle_right.addChild(this.hammerhead1_right);
        this.chest_invisible.addChild(this.chestpiece_mid1);
        this.chestpiece_mid1.addChild(this.chestpiece_mid2);
        this.waist_invisible.addChild(this.legleft_1);
        this.body_base.addChild(this.chest_invisible);
        this.hammerhead1_left.addChild(this.hammerhead2_left);
        this.chestpiece_right.addChild(this.shoulder_right);
        this.armleft_2.addChild(this.hammerhandle_left);
        this.headbase.addChild(this.hat);
        this.facepiece.addChild(this.nose);
        this.waist_invisible.addChild(this.armouredskirt_back);
        this.chest_invisible.addChild(this.chestpiece_left);
        this.waist_invisible.addChild(this.armouredskirt_right);
        this.hammerhandle_right.addChild(this.pommel2_right);
        this.headbase.addChild(this.facepiece);

        parts = new MowzieModelRenderer[] {headJoint, waist_invisible, body_base, legright_1, legleft_1, armouredskirt_back, armouredskirt_right, armouredskirt_left, headconnection, chest_invisible, headbase, facepiece, hat, nose, chestpiece_right, chestpiece_mid1, chestpiece_left, shoulder_right, armright_1, armright_2, hammerhandle_right, hammerhead1_right, pommel1_right, pommel2_right, hammerhead2_right, chestpiece_mid2, shoulder_left, armleft_1, armleft_2, hammerhandle_left, headpommel1_left, pommel1_left, hammerhead1_left, hammerhead2_left, legright_2, footpieceright, legleft_2, footpieceleft, armouredskirt_edge};
        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.waist_invisible.render(f5);
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
        super.setLivingAnimations(entity, f, f1, partialTicks);
        setToInitPose();
        EntityBerserkerGuardian guardian = (EntityBerserkerGuardian) entity;
        float frame = guardian.ticksExisted + partialTicks;

//        f = entity.ticksExisted + partialTicks;
//        f1 = 0.7f;

        float globalSpeed = 1f;
        float globalDegree = 1.2f;
        float globalHeight = 1.7f;

        waist_invisible.rotationPointY += 1 * f1;
        bob(waist_invisible, 1 * globalSpeed, 1f * globalHeight, false, f, f1);
        swing(chest_invisible, 0.5f * globalSpeed, 0.4f * globalDegree, true, 0, 0, f, f1);
        swing(headJoint, 0.5f * globalSpeed, 0.4f * globalDegree, false, 0, 0, f, f1);
        walk(waist_invisible, 1 * globalSpeed, 0.1f * globalHeight, false, 0, 0.2f, f, f1);
        walk(headJoint, 1 * globalSpeed, 0.1f * globalHeight, true, 0, -0.2f, f, f1);
        walk(legleft_1, 1 * globalSpeed, 0.1f * globalHeight, true, 0, -0.2f, f, f1);
        walk(legright_1, 1 * globalSpeed, 0.1f * globalHeight, true, 0, -0.2f, f, f1);

        walk(armouredskirt_back, 1 * globalSpeed, 0.3f * globalHeight, false, -1, 0.5f, f, f1);
        flap(armouredskirt_left, 1 * globalSpeed, 0.2f * globalHeight, true, -1, -0.4f, f, f1);
        flap(armouredskirt_right, 1 * globalSpeed, 0.2f * globalHeight, false, -1, 0.4f, f, f1);

        walk(legright_1, 0.5F * globalSpeed, 1F * globalDegree, false, 0, 0.2f, f, f1);
        walk(legleft_1, 0.5F * globalSpeed, 1F * globalDegree, true, 0, 0.2f, f, f1);
        walk(legright_2, 0.5F * globalSpeed, 0.8F * globalDegree, false, -2.2F, 0.6F, f, f1);
        walk(legleft_2, 0.5F * globalSpeed, 0.8F * globalDegree, true, -2.2F, 0.6F, f, f1);

        walk(shoulder_right, 0.5F * globalSpeed, 0.6F * globalDegree, true, 0F, -0.3F * f1, f, f1);
        walk(shoulder_left, 0.5F * globalSpeed, 0.6F * globalDegree, false, 0F, -0.3F * f1, f, f1);
        walk(armright_2, 0.5F * globalSpeed, 0.4F * globalDegree, true, -1F, -0.5F * f1, f, f1);
        walk(armleft_2, 0.5F * globalSpeed, 0.4F * globalDegree, false, -1F, -0.5F * f1, f, f1);
    }
}
