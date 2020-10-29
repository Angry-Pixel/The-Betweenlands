package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityGiantToad;

@SideOnly(Side.CLIENT)
public class ModelGiantToad extends MowzieModelBase {
    public MowzieModelRenderer body_base;
    public MowzieModelRenderer tubbyneck;
    public MowzieModelRenderer leg_front_left1;
    public MowzieModelRenderer leg_front_right1;
    public MowzieModelRenderer sexy_toad_butt;
    public MowzieModelRenderer leg_back_left1;
    public MowzieModelRenderer leg_back_right1;
    public MowzieModelRenderer head_base;
    public MowzieModelRenderer snout1;
    public MowzieModelRenderer lower_jaw;
    public MowzieModelRenderer eyeleft1;
    public MowzieModelRenderer eyebrow_left1;
    public MowzieModelRenderer eyeright1;
    public MowzieModelRenderer eyebrow_right1;
    public MowzieModelRenderer beardthing_left1;
    public MowzieModelRenderer beardthing_right1;
    public AdvancedModelRenderer snout2;
    public MowzieModelRenderer beardthing_left3;
    public MowzieModelRenderer beardthing_right3;
    public MowzieModelRenderer eyebrow_left2;
    public MowzieModelRenderer eyebrow_left3;
    public MowzieModelRenderer eyebrow_left4;
    public MowzieModelRenderer eyebrow_right2;
    public MowzieModelRenderer eyebrow_right3;
    public MowzieModelRenderer eyebrow_right4;
    public MowzieModelRenderer beardthing_left2;
    public MowzieModelRenderer beardthing_right2;
    public MowzieModelRenderer leg_front_left2;
    public MowzieModelRenderer foot_front_left1;
    public MowzieModelRenderer foot_front_left2;
    public MowzieModelRenderer toe_front_left1;
    public MowzieModelRenderer toe_front_left2;
    public MowzieModelRenderer toe_front_left3;
    public MowzieModelRenderer toe_front_left4;
    public MowzieModelRenderer leg_front_right2;
    public MowzieModelRenderer foot_front_right1;
    public MowzieModelRenderer foot_front_right2;
    public MowzieModelRenderer toe_front_right1;
    public MowzieModelRenderer toe_front_right2;
    public MowzieModelRenderer toe_front_right3;
    public MowzieModelRenderer toe_front_right4;
    public MowzieModelRenderer leg_back_left2;
    public MowzieModelRenderer foot_back_left1;
    public MowzieModelRenderer foot_back_left2;
    public MowzieModelRenderer toe_back_left1;
    public MowzieModelRenderer toe_back_left2;
    public MowzieModelRenderer toe_back_left3;
    public MowzieModelRenderer toe_back_left4;
    public MowzieModelRenderer leg_back_right2;
    public MowzieModelRenderer foot_back_right1;
    public MowzieModelRenderer foot_back_right2;
    public MowzieModelRenderer toe_back_right1;
    public MowzieModelRenderer toe_back_right2;
    public MowzieModelRenderer toe_back_right3;
    public MowzieModelRenderer toe_back_right4;

    public ModelGiantToad() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.leg_back_right2 = new MowzieModelRenderer(this, 198, 22);
        this.leg_back_right2.setRotationPoint(-2.0F, 9.0F, 5.0F);
        this.leg_back_right2.addBox(-2.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(leg_back_right2, 2.6406831582674206F, 0.0F, 0.0F);
        this.eyebrow_right4 = new MowzieModelRenderer(this, 52, 86);
        this.eyebrow_right4.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.eyebrow_right4.addBox(0.0F, -1.02F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_right4, 0.0F, -0.7740535232594852F, 0.0F);
        this.eyebrow_left2 = new MowzieModelRenderer(this, 52, 41);
        this.eyebrow_left2.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.eyebrow_left2.addBox(0.0F, -0.99F, 0.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_left2, 0.0F, 0.27314402793711257F, 0.0F);
        this.foot_front_right2 = new MowzieModelRenderer(this, 146, 36);
        this.foot_front_right2.setRotationPoint(2.0F, 2.0F, 2.0F);
        this.foot_front_right2.addBox(-1.99F, -2.0F, -5.0F, 4, 2, 5, 0.0F);
        this.setRotateAngle(foot_front_right2, 0.40980330836826856F, 0.0F, 0.0F);
        this.beardthing_right3 = new MowzieModelRenderer(this, 110, 8);
        this.beardthing_right3.setRotationPoint(-3.0F, 0.0F, -4.0F);
        this.beardthing_right3.addBox(-2.0F, 0.0F, -1.99F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_right3, 0.0F, 0.0F, 0.136659280431156F);
        this.toe_back_right4 = new MowzieModelRenderer(this, 198, 81);
        this.toe_back_right4.setRotationPoint(2.0F, 0.0F, -6.0F);
        this.toe_back_right4.addBox(-1.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_back_right4, 0.27314402793711257F, -0.6373942428283291F, 0.0F);
        this.leg_back_right1 = new MowzieModelRenderer(this, 198, 0);
        this.leg_back_right1.setRotationPoint(-8.0F, 4.0F, 16.0F);
        this.leg_back_right1.addBox(-5.0F, -3.0F, -4.0F, 5, 12, 9, 0.0F);
        this.setRotateAngle(leg_back_right1, -1.5481070465189704F, 0.31869712141416456F, 0.31869712141416456F);
        this.sexy_toad_butt = new MowzieModelRenderer(this, 0, 48);
        this.sexy_toad_butt.setRotationPoint(0.0F, -2.0F, 16.0F);
        this.sexy_toad_butt.addBox(-7.0F, 0.0F, 0.0F, 14, 10, 6, 0.0F);
        this.setRotateAngle(sexy_toad_butt, -0.5009094953223726F, 0.0F, 0.0F);
        this.snout2 = new AdvancedModelRenderer(this, 73, 29);
        this.snout2.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.snout2.addBox(-5.0F, 0.0F, -5.0F, 10, 5, 5, 0.0F);
        this.setRotateAngle(snout2, 0.5918411493512771F, 0.0F, 0.0F);
        this.foot_front_left2 = new MowzieModelRenderer(this, 123, 36);
        this.foot_front_left2.setRotationPoint(-2.0F, 2.0F, 2.0F);
        this.foot_front_left2.addBox(-2.01F, -2.0F, -5.0F, 4, 2, 5, 0.0F);
        this.setRotateAngle(foot_front_left2, 0.40980330836826856F, 0.0F, 0.0F);
        this.toe_back_right1 = new MowzieModelRenderer(this, 198, 57);
        this.toe_back_right1.setRotationPoint(1.0F, 0.0F, -10.0F);
        this.toe_back_right1.addBox(0.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F);
        this.setRotateAngle(toe_back_right1, 0.22759093446006054F, -0.5516985765554075F, -0.10314895879286487F);
        this.foot_back_right2 = new MowzieModelRenderer(this, 198, 43);
        this.foot_back_right2.setRotationPoint(2.0F, 3.0F, 2.0F);
        this.foot_back_right2.addBox(-2.01F, 0.0F, -10.0F, 4, 3, 10, 0.0F);
        this.setRotateAngle(foot_back_right2, -0.6829473363053812F, 0.0F, 0.0F);
        this.foot_front_left1 = new MowzieModelRenderer(this, 123, 29);
        this.foot_front_left1.setRotationPoint(2.0F, 6.0F, -2.0F);
        this.foot_front_left1.addBox(-4.0F, 0.0F, -1.99F, 4, 2, 4, 0.0F);
        this.setRotateAngle(foot_front_left1, 0.0F, 0.0F, 0.36425021489121656F);
        this.beardthing_left2 = new MowzieModelRenderer(this, 110, 24);
        this.beardthing_left2.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.beardthing_left2.addBox(0.0F, 0.0F, -1.98F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_left2, 0.0F, 0.0F, -0.40980330836826856F);
        this.foot_back_left1 = new MowzieModelRenderer(this, 169, 35);
        this.foot_back_left1.setRotationPoint(2.0F, 8.0F, 2.0F);
        this.foot_back_left1.addBox(-4.0F, 0.0F, -1.99F, 4, 3, 4, 0.0F);
        this.setRotateAngle(foot_back_left1, 0.0F, 0.0F, 0.22759093446006054F);
        this.eyebrow_left3 = new MowzieModelRenderer(this, 52, 48);
        this.eyebrow_left3.setRotationPoint(3.0F, 0.0F, -3.0F);
        this.eyebrow_left3.addBox(-3.0F, -1.01F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_left3, 0.0F, 0.40980330836826856F, 0.0F);
        this.foot_back_left2 = new MowzieModelRenderer(this, 169, 43);
        this.foot_back_left2.setRotationPoint(-2.0F, 3.0F, 2.0F);
        this.foot_back_left2.addBox(-1.99F, 0.0F, -10.0F, 4, 3, 10, 0.0F);
        this.setRotateAngle(foot_back_left2, -0.6829473363053812F, 0.0F, 0.0F);
        this.toe_back_left1 = new MowzieModelRenderer(this, 169, 57);
        this.toe_back_left1.setRotationPoint(-1.0F, 0.0F, -10.0F);
        this.toe_back_left1.addBox(-1.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F);
        this.setRotateAngle(toe_back_left1, 0.22759093446006054F, 0.5462880558742251F, 0.10314895879286487F);
        this.beardthing_left1 = new MowzieModelRenderer(this, 110, 16);
        this.beardthing_left1.setRotationPoint(4.0F, 10.0F, -4.0F);
        this.beardthing_left1.addBox(0.0F, 0.0F, -1.99F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_left1, 0.0F, 0.0F, -0.136659280431156F);
        this.leg_back_left1 = new MowzieModelRenderer(this, 169, 0);
        this.leg_back_left1.setRotationPoint(8.0F, 4.0F, 16.0F);
        this.leg_back_left1.addBox(0.0F, -3.0F, -4.0F, 5, 12, 9, 0.0F);
        this.setRotateAngle(leg_back_left1, -1.5481070465189704F, -0.31869712141416456F, -0.31869712141416456F);
        this.head_base = new MowzieModelRenderer(this, 73, 0);
        this.head_base.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.head_base.addBox(-6.0F, 0.0F, -6.0F, 12, 10, 6, 0.0F);
        this.setRotateAngle(head_base, 0.22759093446006054F, 0.0F, 0.0F);
        this.snout1 = new MowzieModelRenderer(this, 73, 17);
        this.snout1.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.snout1.addBox(-5.0F, 0.0F, -6.0F, 10, 5, 6, 0.0F);
        this.setRotateAngle(snout1, 0.31869712141416456F, 0.0F, 0.0F);
        this.toe_front_left2 = new MowzieModelRenderer(this, 123, 51);
        this.toe_front_left2.setRotationPoint(0.5F, -2.0F, -5.0F);
        this.toe_front_left2.addBox(-1.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_front_left2, 0.27314402793711257F, 0.27314402793711257F, 0.056025068989017976F);
        this.toe_front_right2 = new MowzieModelRenderer(this, 146, 51);
        this.toe_front_right2.setRotationPoint(-0.5F, -2.0F, -5.0F);
        this.toe_front_right2.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_front_right2, 0.27314402793711257F, -0.27314402793711257F, -0.056025068989017976F);
        this.leg_front_left2 = new MowzieModelRenderer(this, 123, 18);
        this.leg_front_left2.setRotationPoint(2.0F, 7.0F, 1.0F);
        this.leg_front_left2.addBox(-2.01F, 0.0F, -4.0F, 4, 6, 4, 0.0F);
        this.setRotateAngle(leg_front_left2, -0.5462880558742251F, 0.0F, 0.0F);
        this.leg_front_right2 = new MowzieModelRenderer(this, 146, 18);
        this.leg_front_right2.setRotationPoint(-2.0F, 7.0F, 1.0F);
        this.leg_front_right2.addBox(-1.99F, 0.0F, -4.0F, 4, 6, 4, 0.0F);
        this.setRotateAngle(leg_front_right2, -0.5462880558742251F, 0.0F, 0.0F);
        this.tubbyneck = new MowzieModelRenderer(this, 0, 31);
        this.tubbyneck.setRotationPoint(0.0F, -2.0F, -2.0F);
        this.tubbyneck.addBox(-8.0F, 0.0F, -6.0F, 16, 10, 6, 0.0F);
        this.setRotateAngle(tubbyneck, 0.22759093446006054F, 0.0F, 0.0F);
        this.leg_back_left2 = new MowzieModelRenderer(this, 169, 22);
        this.leg_back_left2.setRotationPoint(2.0F, 9.0F, 5.0F);
        this.leg_back_left2.addBox(-2.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
        this.setRotateAngle(leg_back_left2, 2.6406831582674206F, 0.0F, 0.0F);
        this.foot_back_right1 = new MowzieModelRenderer(this, 198, 35);
        this.foot_back_right1.setRotationPoint(-2.0F, 8.0F, 2.0F);
        this.foot_back_right1.addBox(0.0F, 0.0F, -1.99F, 4, 3, 4, 0.0F);
        this.setRotateAngle(foot_back_right1, 0.0F, 0.0F, -0.22759093446006054F);
        this.lower_jaw = new MowzieModelRenderer(this, 73, 40);
        this.lower_jaw.setRotationPoint(0.0F, 10.0F, -6.0F);
        this.lower_jaw.addBox(-5.0F, -5.0F, -6.0F, 10, 5, 6, 0.0F);
        this.setRotateAngle(lower_jaw, -0.091106186954104F, 0.0F, 0.0F);
        this.eyeleft1 = new MowzieModelRenderer(this, 60, 0);
        this.eyeleft1.setRotationPoint(5.0F, 1.5F, -3.0F);
        this.eyeleft1.addBox(-1.0F, -1.0F, -2.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(eyeleft1, 0.0F, 0.136659280431156F, -0.091106186954104F);
        this.toe_front_left4 = new MowzieModelRenderer(this, 123, 64);
        this.toe_front_left4.setRotationPoint(-2.0F, -2.0F, -2.5F);
        this.toe_front_left4.addBox(0.0F, 0.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(toe_front_left4, 0.18203784098300857F, 0.9560913642424937F, 0.0F);
        this.eyebrow_left4 = new MowzieModelRenderer(this, 52, 55);
        this.eyebrow_left4.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.eyebrow_left4.addBox(-3.0F, -1.02F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_left4, 0.0F, 0.7740535232594852F, 0.0F);
        this.toe_front_right1 = new MowzieModelRenderer(this, 146, 44);
        this.toe_front_right1.setRotationPoint(1.0F, -2.0F, -5.0F);
        this.toe_front_right1.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_front_right1, 0.22759093446006054F, -0.5462880558742251F, -0.10314895879286487F);
        this.body_base = new MowzieModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 6.3F, -2.0F);
        this.body_base.addBox(-9.0F, -2.0F, -2.0F, 18, 12, 18, 0.0F);
        this.setRotateAngle(body_base, -0.31869712141416456F, 0.0F, 0.0F);
        this.eyebrow_left1 = new MowzieModelRenderer(this, 52, 31);
        this.eyebrow_left1.setRotationPoint(4.0F, 1.0F, -3.0F);
        this.eyebrow_left1.addBox(0.0F, -1.0F, -3.0F, 3, 2, 7, 0.0F);
        this.setRotateAngle(eyebrow_left1, -0.136659280431156F, 0.136659280431156F, -0.7285004297824331F);
        this.leg_front_left1 = new MowzieModelRenderer(this, 123, 0);
        this.leg_front_left1.setRotationPoint(7.5F, 6.0F, -1.0F);
        this.leg_front_left1.addBox(0.0F, -3.0F, -3.0F, 4, 10, 7, 0.0F);
        this.setRotateAngle(leg_front_left1, 0.31869712141416456F, 0.5918411493512771F, -0.136659280431156F);
        this.toe_front_left3 = new MowzieModelRenderer(this, 123, 58);
        this.toe_front_left3.setRotationPoint(2.0F, -2.0F, -5.0F);
        this.toe_front_left3.addBox(-1.01F, 0.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(toe_front_left3, 0.27314402793711257F, 0.0F, 0.0F);
        this.foot_front_right1 = new MowzieModelRenderer(this, 146, 29);
        this.foot_front_right1.setRotationPoint(-2.0F, 6.0F, -2.0F);
        this.foot_front_right1.addBox(0.0F, 0.0F, -1.99F, 4, 2, 4, 0.0F);
        this.setRotateAngle(foot_front_right1, 0.0F, 0.0F, -0.36425021489121656F);
        this.beardthing_right2 = new MowzieModelRenderer(this, 110, 40);
        this.beardthing_right2.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.beardthing_right2.addBox(-2.0F, 0.0F, -1.98F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_right2, 0.0F, 0.0F, 0.40980330836826856F);
        this.eyebrow_right3 = new MowzieModelRenderer(this, 52, 79);
        this.eyebrow_right3.setRotationPoint(-3.0F, 0.0F, -3.0F);
        this.eyebrow_right3.addBox(0.0F, -1.01F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_right3, 0.0F, -0.40980330836826856F, 0.0F);
        this.eyebrow_right2 = new MowzieModelRenderer(this, 52, 72);
        this.eyebrow_right2.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.eyebrow_right2.addBox(-3.0F, -0.99F, 0.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(eyebrow_right2, 0.0F, -0.27314402793711257F, 0.0F);
        this.beardthing_left3 = new MowzieModelRenderer(this, 110, 0);
        this.beardthing_left3.setRotationPoint(3.0F, 0.0F, -4.0F);
        this.beardthing_left3.addBox(0.0F, 0.0F, -1.99F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_left3, 0.0F, 0.0F, -0.136659280431156F);
        this.eyeright1 = new MowzieModelRenderer(this, 60, 7);
        this.eyeright1.setRotationPoint(-5.0F, 1.5F, -3.0F);
        this.eyeright1.addBox(-2.0F, -1.0F, -2.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(eyeright1, 0.0F, -0.136659280431156F, 0.091106186954104F);
        this.toe_back_right2 = new MowzieModelRenderer(this, 198, 66);
        this.toe_back_right2.setRotationPoint(-0.5F, 0.0F, -10.0F);
        this.toe_back_right2.addBox(0.0F, 0.0F, -5.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(toe_back_right2, 0.27314402793711257F, -0.27314402793711257F, -0.056025068989017976F);
        this.beardthing_right1 = new MowzieModelRenderer(this, 110, 32);
        this.beardthing_right1.setRotationPoint(-4.0F, 10.0F, -4.0F);
        this.beardthing_right1.addBox(-2.0F, 0.0F, -1.99F, 2, 3, 4, 0.0F);
        this.setRotateAngle(beardthing_right1, 0.0F, 0.0F, 0.136659280431156F);
        this.eyebrow_right1 = new MowzieModelRenderer(this, 52, 62);
        this.eyebrow_right1.setRotationPoint(-4.0F, 1.0F, -3.0F);
        this.eyebrow_right1.addBox(-3.0F, -1.0F, -3.0F, 3, 2, 7, 0.0F);
        this.setRotateAngle(eyebrow_right1, -0.136659280431156F, -0.136659280431156F, 0.7285004297824331F);
        this.toe_front_right4 = new MowzieModelRenderer(this, 146, 64);
        this.toe_front_right4.setRotationPoint(2.0F, -2.0F, -2.5F);
        this.toe_front_right4.addBox(0.0F, 0.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(toe_front_right4, 0.18203784098300857F, -0.9560913642424937F, 0.0F);
        this.toe_front_right3 = new MowzieModelRenderer(this, 146, 58);
        this.toe_front_right3.setRotationPoint(-2.0F, -2.0F, -5.0F);
        this.toe_front_right3.addBox(0.01F, 0.0F, -3.0F, 1, 2, 3, 0.0F);
        this.setRotateAngle(toe_front_right3, 0.27314402793711257F, 0.0F, 0.0F);
        this.toe_back_left4 = new MowzieModelRenderer(this, 169, 81);
        this.toe_back_left4.setRotationPoint(-2.0F, 0.0F, -6.0F);
        this.toe_back_left4.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_back_left4, 0.27314402793711257F, 0.6373942428283291F, 0.0F);
        this.toe_front_left1 = new MowzieModelRenderer(this, 123, 44);
        this.toe_front_left1.setRotationPoint(-1.0F, -2.0F, -5.0F);
        this.toe_front_left1.addBox(-1.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_front_left1, 0.22759093446006054F, 0.5462880558742251F, 0.10314895879286487F);
        this.leg_front_right1 = new MowzieModelRenderer(this, 146, 0);
        this.leg_front_right1.setRotationPoint(-7.5F, 6.0F, -1.0F);
        this.leg_front_right1.addBox(-4.0F, -3.0F, -3.0F, 4, 10, 7, 0.0F);
        this.setRotateAngle(leg_front_right1, 0.31869712141416456F, -0.5918411493512771F, 0.136659280431156F);
        this.toe_back_left3 = new MowzieModelRenderer(this, 169, 74);
        this.toe_back_left3.setRotationPoint(2.0F, 0.0F, -10.0F);
        this.toe_back_left3.addBox(-1.01F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_back_left3, 0.27314402793711257F, 0.0F, 0.0F);
        this.toe_back_right3 = new MowzieModelRenderer(this, 198, 74);
        this.toe_back_right3.setRotationPoint(-2.0F, 0.0F, -10.0F);
        this.toe_back_right3.addBox(0.01F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(toe_back_right3, 0.27314402793711257F, 0.0F, 0.0F);
        this.toe_back_left2 = new MowzieModelRenderer(this, 169, 66);
        this.toe_back_left2.setRotationPoint(0.5F, 0.0F, -10.0F);
        this.toe_back_left2.addBox(-1.0F, 0.0F, -5.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(toe_back_left2, 0.27314402793711257F, 0.27314402793711257F, 0.056025068989017976F);
        this.leg_back_right1.addChild(this.leg_back_right2);
        this.eyebrow_right3.addChild(this.eyebrow_right4);
        this.eyebrow_left1.addChild(this.eyebrow_left2);
        this.foot_front_right1.addChild(this.foot_front_right2);
        this.lower_jaw.addChild(this.beardthing_right3);
        this.foot_back_right2.addChild(this.toe_back_right4);
        this.body_base.addChild(this.leg_back_right1);
        this.body_base.addChild(this.sexy_toad_butt);
        this.snout1.addChild(this.snout2);
        this.foot_front_left1.addChild(this.foot_front_left2);
        this.foot_back_right2.addChild(this.toe_back_right1);
        this.foot_back_right1.addChild(this.foot_back_right2);
        this.leg_front_left2.addChild(this.foot_front_left1);
        this.beardthing_left1.addChild(this.beardthing_left2);
        this.leg_back_left2.addChild(this.foot_back_left1);
        this.eyebrow_left1.addChild(this.eyebrow_left3);
        this.foot_back_left1.addChild(this.foot_back_left2);
        this.foot_back_left2.addChild(this.toe_back_left1);
        this.head_base.addChild(this.beardthing_left1);
        this.body_base.addChild(this.leg_back_left1);
        this.tubbyneck.addChild(this.head_base);
        this.head_base.addChild(this.snout1);
        this.foot_front_left2.addChild(this.toe_front_left2);
        this.foot_front_right2.addChild(this.toe_front_right2);
        this.leg_front_left1.addChild(this.leg_front_left2);
        this.leg_front_right1.addChild(this.leg_front_right2);
        this.body_base.addChild(this.tubbyneck);
        this.leg_back_left1.addChild(this.leg_back_left2);
        this.leg_back_right2.addChild(this.foot_back_right1);
        this.head_base.addChild(this.lower_jaw);
        this.head_base.addChild(this.eyeleft1);
        this.foot_front_left2.addChild(this.toe_front_left4);
        this.eyebrow_left3.addChild(this.eyebrow_left4);
        this.foot_front_right2.addChild(this.toe_front_right1);
        this.head_base.addChild(this.eyebrow_left1);
        this.body_base.addChild(this.leg_front_left1);
        this.foot_front_left2.addChild(this.toe_front_left3);
        this.leg_front_right2.addChild(this.foot_front_right1);
        this.beardthing_right1.addChild(this.beardthing_right2);
        this.eyebrow_right1.addChild(this.eyebrow_right3);
        this.eyebrow_right1.addChild(this.eyebrow_right2);
        this.lower_jaw.addChild(this.beardthing_left3);
        this.head_base.addChild(this.eyeright1);
        this.foot_back_right2.addChild(this.toe_back_right2);
        this.head_base.addChild(this.beardthing_right1);
        this.head_base.addChild(this.eyebrow_right1);
        this.foot_front_right2.addChild(this.toe_front_right4);
        this.foot_front_right2.addChild(this.toe_front_right3);
        this.foot_back_left2.addChild(this.toe_back_left4);
        this.foot_front_left2.addChild(this.toe_front_left1);
        this.body_base.addChild(this.leg_front_right1);
        this.foot_back_left2.addChild(this.toe_back_left3);
        this.foot_back_right2.addChild(this.toe_back_right3);
        this.foot_back_left2.addChild(this.toe_back_left2);

        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.body_base.render(f5);
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
    public void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float partialRenderTicks) {
        EntityGiantToad toad = (EntityGiantToad) entity;
        setToInitPose();
        float frame = toad.ticksExisted + partialRenderTicks;
        float leapingProgress = toad.getLeapProgress(partialRenderTicks);
        float swimProgress = toad.getSwimProgress(partialRenderTicks);
        float inWaterProgress = toad.getWaterStanceProgress(partialRenderTicks);

        //Fixes some z-fighting
        snout2.setScale(0.998F);

        //Idle animation
        bob(body_base, 0.07f, 0.1f, false, frame, 1);
        bob(leg_back_left1, 0.07f, -0.1f, false, frame, 1);
        bob(leg_back_right1, 0.07f, -0.1f, false, frame, 1);
        bob(leg_front_left1, 0.07f, -0.1f, false, frame, 1);
        bob(leg_front_right1, 0.07f, -0.1f, false, frame, 1);

        walk(head_base, 0.07f, 0.035f, false, 1, 0, frame, 1);

        walk(beardthing_left1, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);
        walk(beardthing_right1, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);
        walk(beardthing_left2, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);
        walk(beardthing_right2, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);

        walk(beardthing_left3, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);
        walk(beardthing_right3, 0.07f, -0.15f, false, 1.8f, 0, frame, 1);

        walk(eyebrow_left1, 0.07f, 0.1f, false, 1f, 0, frame, 1);
        walk(eyebrow_left3, 0.07f, -0.1f, false, 1f, 0, frame, 1);
        walk(eyebrow_left2, 0.1f, 0.2f, false, 2, 0, frame, 1);
        bob(eyebrow_left1, 0.07f, 0.2f, false, frame, 1);

        walk(eyebrow_right1, 0.07f, 0.1f, false, 1f, 0, frame, 1);
        walk(eyebrow_right3, 0.07f, -0.1f, false, 1f, 0, frame, 1);
        walk(eyebrow_right2, 0.1f, 0.2f, false, 2, 0, frame, 1);
        bob(eyebrow_right1, 0.07f, 0.2f, false, frame, 1);


        //Water stance
        bob(body_base, 0.1f, 0.6f * inWaterProgress, false, frame, 1);
        body_base.rotateAngleX += 0.25 * inWaterProgress;
        head_base.rotateAngleX -= 0.4 * inWaterProgress;
        head_base.rotationPointZ += 2.5F * inWaterProgress;
        leg_back_left1.rotateAngleX += 0.8 * inWaterProgress;
        leg_back_right1.rotateAngleX += 0.8 * inWaterProgress;
        leg_front_left1.rotateAngleX -= 0.6 * inWaterProgress;
        leg_front_right1.rotateAngleX -= 0.6 * inWaterProgress;
        leg_front_left2.rotateAngleY -= 0.6 * inWaterProgress;
        leg_front_right2.rotateAngleY += 0.6 * inWaterProgress;


        //Swimming
        if (swimProgress - 0.05F > leapingProgress) {
            body_base.rotateAngleX += 0.2 * swimProgress;

            leg_back_left1.rotateAngleY += -0.1F * swimProgress;
            leg_back_right1.rotateAngleY += 0.1F * swimProgress;
            leg_back_left1.rotateAngleX += 1.8 * swimProgress;
            leg_back_right1.rotateAngleX += 1.8 * swimProgress;
            leg_back_left1.rotateAngleZ -= 1.2 * swimProgress;
            leg_back_right1.rotateAngleZ += 1.2 * swimProgress;
            foot_back_left1.rotateAngleX += 2 * swimProgress;
            foot_back_right1.rotateAngleX += 2 * swimProgress;
            leg_back_left2.rotateAngleX -= 2 * swimProgress;
            leg_back_right2.rotateAngleX -= 2 * swimProgress;

            leg_front_left1.rotateAngleX += 0.8 * 2 * swimProgress;
            leg_front_left1.rotateAngleZ += 0.4 * swimProgress;
            leg_front_left2.rotateAngleY -= 1.8 * 2 * swimProgress;
            foot_front_left1.rotateAngleX += 1 * swimProgress;
            foot_front_left1.rotateAngleY += 1.5 * swimProgress;
            foot_front_left1.rotateAngleZ -= 1.5 * swimProgress;

            leg_front_right1.rotateAngleX += 0.8 * 2 * swimProgress;
            leg_front_right1.rotateAngleZ -= 0.4 * swimProgress;
            leg_front_right2.rotateAngleY += 1.8 * 2 * swimProgress;
            foot_front_right1.rotateAngleX += 1 * swimProgress;
            foot_front_right1.rotateAngleY -= 1.5 * swimProgress;
            foot_front_right1.rotateAngleZ += 1.5 * swimProgress;
        } else {
            //Jumping
            body_base.rotateAngleX += 0.2 * leapingProgress;


            leg_back_left1.rotateAngleX += 2.3 * leapingProgress;
            leg_back_right1.rotateAngleX += 2.3 * leapingProgress;
            leg_back_left1.rotateAngleZ -= 0.2 * leapingProgress;
            leg_back_right1.rotateAngleZ += 0.2 * leapingProgress;
            foot_back_left1.rotateAngleX += 2 * leapingProgress;
            foot_back_right1.rotateAngleX += 2 * leapingProgress;
            leg_back_left2.rotateAngleX -= 2 * leapingProgress;
            leg_back_right2.rotateAngleX -= 2 * leapingProgress;

            leg_front_left1.rotateAngleX -= 0.8 * leapingProgress;
            leg_front_right1.rotateAngleX -= 0.8 * leapingProgress;
            leg_front_left2.rotateAngleZ -= 0.8 * leapingProgress;
            leg_front_right2.rotateAngleZ += 0.8 * leapingProgress;
        }
    }
}
