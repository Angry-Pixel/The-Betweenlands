package thebetweenlands.client.render.model.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;

@SideOnly(Side.CLIENT)
public class ModelChiromaw extends MowzieModelBase {
    MowzieModelRenderer body_base;
    MowzieModelRenderer body_buttpart;
    MowzieModelRenderer neck;
    MowzieModelRenderer arm_left1;
    MowzieModelRenderer arm_right1;
    MowzieModelRenderer leg_left1;
    MowzieModelRenderer leg_right1;
    MowzieModelRenderer lil_tail1;
    MowzieModelRenderer leg_left2;
    MowzieModelRenderer leg_right2;
    MowzieModelRenderer lil_tail2;
    MowzieModelRenderer lil_tail3;
    MowzieModelRenderer head_base;
    MowzieModelRenderer head_connection;
    MowzieModelRenderer head_jaw1;
    MowzieModelRenderer fang_left;
    MowzieModelRenderer fang_right;
    MowzieModelRenderer teeth_upperjaw_left;
    MowzieModelRenderer teeth_upperjaw_right;
    MowzieModelRenderer teeth_upperjaw_front;
    MowzieModelRenderer teeth_lowerjaw_left;
    MowzieModelRenderer teeth_lowerjaw_right;
    MowzieModelRenderer teeth_lowerjaw_front;
    MowzieModelRenderer arm_left2;
    MowzieModelRenderer wing_left1;
    MowzieModelRenderer wing_left2;
    MowzieModelRenderer arm_right2;
    MowzieModelRenderer wing_right1;
    MowzieModelRenderer wing_right2;
    

    MowzieModelRenderer body;
    MowzieModelRenderer chest;
    MowzieModelRenderer legleft1;
    MowzieModelRenderer legright1;
    MowzieModelRenderer head_main;
    MowzieModelRenderer arm_left_upper;
    MowzieModelRenderer arm_right_upper;
    MowzieModelRenderer nose;
    MowzieModelRenderer jaw_back;
    MowzieModelRenderer ear_right;
    MowzieModelRenderer ear_left;
    MowzieModelRenderer jaw_main;
    MowzieModelRenderer arm_left_lower;
    MowzieModelRenderer arm_right_lower;
    MowzieModelRenderer leg_left_lower;
    MowzieModelRenderer leg_right_lower;
  

    public ModelChiromaw() {
        textureWidth = 128;
        textureHeight = 64;
        leg_left1 = new MowzieModelRenderer(this, 46, 0);
        leg_left1.setRotationPoint(1.5F, 2.0F, -1.0F);
        leg_left1.addBox(0.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotation(leg_left1, -2.276432943376204F, -0.5009094953223726F, 0.27314402793711257F);
        head_connection = new MowzieModelRenderer(this, 21, 18);
        head_connection.setRotationPoint(0.0F, 2.0F, 0.0F);
        head_connection.addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2, 0.0F);
        neck = new MowzieModelRenderer(this, 21, 0);   //
        neck.setRotationPoint(0.0F, 0.0F, 2.0F);
        neck.addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3, 0.0F);
        setRotation(neck, -0.7285004297824331F, 0.0F, 0.0F);
        teeth_lowerjaw_right = new MowzieModelRenderer(this, 32, 26);
        teeth_lowerjaw_right.setRotationPoint(-2.5F, -1.0F, -3.0F);
        teeth_lowerjaw_right.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
        setRotation(teeth_lowerjaw_right, 0.0F, 0.0F, -0.22759093446006054F);
        head_base = new MowzieModelRenderer(this, 21, 7);
        head_base.setRotationPoint(0.0F, 0.0F, -3.0F);
        head_base.addBox(-3.0F, -2.0F, -6.0F, 6, 4, 6, 0.0F);
        setRotation(head_base, 0.091106186954104F, 0.0F, 0.0F);
        lil_tail2 = new MowzieModelRenderer(this, 0, 24);
        lil_tail2.setRotationPoint(0.0F, 3.0F, 0.0F);
        lil_tail2.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail2, 0.36425021489121656F, 0.0F, 0.0F);
        teeth_upperjaw_left = new MowzieModelRenderer(this, 21, 39);
        teeth_upperjaw_left.setRotationPoint(3.0F, 2.0F, -3.0F);
        teeth_upperjaw_left.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
        setRotation(teeth_upperjaw_left, 0.0F, 0.0F, -0.091106186954104F);
        body_base = new MowzieModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, 12.0F, 0.0F);
        body_base.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 4, 0.0F);
        setRotation(body_base, 0.5462880558742251F, 0.0F, 0.0F);
        lil_tail3 = new MowzieModelRenderer(this, 0, 30);
        lil_tail3.setRotationPoint(0.0F, 3.0F, 0.0F);
        lil_tail3.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail3, 0.40980330836826856F, 0.0F, 0.0F);
        wing_right2 = new MowzieModelRenderer(this, 57, 44);
        wing_right2.setRotationPoint(-1.0F, 0.0F, 1.0F);
        wing_right2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
        setRotation(wing_right2, 0.0F, 0.27314402793711257F, 0.0F);
        head_jaw1 = new MowzieModelRenderer(this, 21, 23);
        head_jaw1.setRotationPoint(0.0F, 3.0F, -1.0F);
        head_jaw1.addBox(-2.5F, -1.0F, -5.0F, 5, 2, 5, 0.0F);
        setRotation(head_jaw1, 0.9560913642424937F, 0.0F, 0.0F);

        lil_tail1 = new MowzieModelRenderer(this, 0, 18);
        lil_tail1.setRotationPoint(0.0F, 3.0F, -2.0F);
        lil_tail1.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail1, 0.27314402793711257F, 0.0F, 0.0F);
        leg_right2 = new MowzieModelRenderer(this, 57, 9);
        leg_right2.setRotationPoint(-1.0F, 4.0F, 0.0F);
        leg_right2.addBox(-1.01F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
        setRotation(leg_right2, 2.1855012893472994F, 0.0F, 0.0F);
        fang_left = new MowzieModelRenderer(this, 21, 37);
        fang_left.setRotationPoint(3.0F, 2.0F, -6.0F);
        fang_left.addBox(-1.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotation(fang_left, -0.091106186954104F, 0.0F, -0.091106186954104F);

        leg_left2 = new MowzieModelRenderer(this, 46, 9);
        leg_left2.setRotationPoint(1.0F, 4.0F, 0.0F);
        leg_left2.addBox(-0.99F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
        setRotation(leg_left2, 2.1855012893472994F, 0.0F, 0.0F);
        wing_left1 = new MowzieModelRenderer(this, 46, 34);
        wing_left1.setRotationPoint(1.0F, 0.0F, 1.0F);
        wing_left1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
        setRotation(wing_left1, 0.0F, -0.27314402793711257F, 0.0F);

        teeth_lowerjaw_left = new MowzieModelRenderer(this, 21, 26);
        teeth_lowerjaw_left.setRotationPoint(2.5F, -1.0F, -3.0F);
        teeth_lowerjaw_left.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
        setRotation(teeth_lowerjaw_left, 0.0F, 0.0F, 0.22759093446006054F);
        leg_right1 = new MowzieModelRenderer(this, 57, 0);
        leg_right1.setRotationPoint(-1.5F, 2.0F, -1.0F);
        leg_right1.addBox(-2.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotation(leg_right1, -2.276432943376204F, 0.5009094953223726F, -0.27314402793711257F);
        teeth_lowerjaw_front = new MowzieModelRenderer(this, 21, 34);
        teeth_lowerjaw_front.setRotationPoint(0.0F, -1.0F, -5.0F);
        teeth_lowerjaw_front.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 0, 0.0F);
        setRotation(teeth_lowerjaw_front, 0.136659280431156F, 0.0F, 0.0F);
        wing_right1 = new MowzieModelRenderer(this, 46, 44);
        wing_right1.setRotationPoint(-1.0F, 0.0F, 1.0F);
        wing_right1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
        setRotation(wing_right1, 0.0F, 0.27314402793711257F, 0.0F);
        fang_right = new MowzieModelRenderer(this, 26, 37);
        fang_right.setRotationPoint(-3.0F, 2.0F, -6.0F);
        fang_right.addBox(0.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotation(fang_right, -0.091106186954104F, 0.0F, 0.091106186954104F);
        wing_left2 = new MowzieModelRenderer(this, 57, 34);
        wing_left2.setRotationPoint(1.0F, 0.0F, 1.0F);
        wing_left2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
        setRotation(wing_left2, 0.0F, -0.27314402793711257F, 0.0F);
        teeth_upperjaw_right = new MowzieModelRenderer(this, 27, 39);
        teeth_upperjaw_right.setRotationPoint(-3.0F, 2.0F, -3.0F);
        teeth_upperjaw_right.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
        setRotation(teeth_upperjaw_right, 0.0F, 0.0F, 0.091106186954104F);
        teeth_upperjaw_front = new MowzieModelRenderer(this, 21, 44);
        teeth_upperjaw_front.setRotationPoint(0.0F, 2.0F, -6.0F);
        teeth_upperjaw_front.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 0, 0.0F);
        setRotation(teeth_upperjaw_front, -0.091106186954104F, 0.0F, 0.0F);
        body_buttpart = new MowzieModelRenderer(this, 0, 11);
        body_buttpart.setRotationPoint(0.0F, 6.0F, 2.0F);
        body_buttpart.addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3, 0.0F);
        setRotation(body_buttpart, -0.22759093446006054F, 0.0F, 0.0F);
        arm_right1 = new MowzieModelRenderer(this, 46, 29);
        arm_right1.setRotationPoint(-3.0F, 0.0F, 1.0F);
        arm_right1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_right1, -0.8196066167365371F, -0.091106186954104F, 0.5462880558742251F);
        arm_right2 = new MowzieModelRenderer(this, 55, 29);
        arm_right2.setRotationPoint(0.0F, 7.0F, 0.0F);
        arm_right2.addBox(-1.01F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_right2, -0.31869712141416456F, 0.0F, 0.0F);
        arm_left1 = new MowzieModelRenderer(this, 46, 19);
        arm_left1.setRotationPoint(3.0F, 0.0F, 1.0F);
        arm_left1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_left1, -0.8196066167365371F, 0.091106186954104F, -0.5462880558742251F);
        arm_left2 = new MowzieModelRenderer(this, 55, 19);
        arm_left2.setRotationPoint(0.0F, 7.0F, 0.0F);
        arm_left2.addBox(-0.99F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_left2, -0.31869712141416456F, 0.0F, 0.0F);

        body_buttpart.addChild(leg_left1);
        head_base.addChild(head_connection);
        body_base.addChild(neck);
        head_jaw1.addChild(teeth_lowerjaw_right);
        neck.addChild(head_base);
        lil_tail1.addChild(lil_tail2);
        head_base.addChild(teeth_upperjaw_left);
        lil_tail2.addChild(lil_tail3);
        arm_right2.addChild(wing_right2);
        head_base.addChild(head_jaw1);
        arm_right1.addChild(arm_right2);
        body_buttpart.addChild(lil_tail1);
        leg_right1.addChild(leg_right2);
        head_base.addChild(fang_left);
        arm_left1.addChild(arm_left2);
        leg_left1.addChild(leg_left2);
        arm_left1.addChild(wing_left1);
        body_base.addChild(arm_left1);
        head_jaw1.addChild(teeth_lowerjaw_left);
        body_buttpart.addChild(leg_right1);
        head_jaw1.addChild(teeth_lowerjaw_front);
        arm_right1.addChild(wing_right1);
        head_base.addChild(fang_right);
        arm_left2.addChild(wing_left2);
        head_base.addChild(teeth_upperjaw_right);
        head_base.addChild(teeth_upperjaw_front);
        body_base.addChild(arm_right1);
        body_base.addChild(body_buttpart);
        
        arm_left_lower = new MowzieModelRenderer(this, 101, 0);
        arm_left_lower.setRotationPoint(0.0F, 3.5F, 0.0F);
        arm_left_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotation(arm_left_lower, -0.6829473363053812F, 0.0F, 0.0F);
        nose = new MowzieModelRenderer(this, 82, 25);
        nose.setRotationPoint(0.0F, 0.0F, -3.0F);
        nose.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        setRotation(nose, -0.22759093446006054F, 0.0F, 0.0F);
        body = new MowzieModelRenderer(this, 71, 9);
        body.setRotationPoint(0.0F, 19.5F, 1.3F);
        body.addBox(-2.0F, -4.0F, -1.5F, 4, 4, 3, 0.0F);
        arm_left_upper = new MowzieModelRenderer(this, 96, 0);
        arm_left_upper.setRotationPoint(2.5F, -2.7F, 0.5F);
        arm_left_upper.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotation(arm_left_upper, -1.2292353921796064F, 0.045553093477052F, -0.36425021489121656F);
        head_main = new MowzieModelRenderer(this, 71, 17);
        head_main.setRotationPoint(0.0F, -3.8F, -0.5F);
        head_main.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        setRotation(head_main, -0.5918411493512771F, 0.0F, 0.0F);
        chest = new MowzieModelRenderer(this, 71, 0);
        chest.setRotationPoint(0.0F, -3.4F, 0.0F);
        chest.addBox(-2.5F, -3.5F, -2.0F, 5, 4, 4, 0.0F);
        setRotation(chest, 0.5462880558742251F, 0.0F, 0.0F);
        legright1 = new MowzieModelRenderer(this, 116, 5);
        legright1.setRotationPoint(-1.7F, -0.4F, 0.0F);
        legright1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(legright1, -0.5462880558742251F, 1.0927506446736497F, 0.40980330836826856F);
        ear_left = new MowzieModelRenderer(this, 87, 25);
        ear_left.setRotationPoint(2.0F, -1.5F, -0.5F);
        ear_left.addBox(-1.0F, -2.0F, 0.0F, 5, 3, 0, 0.0F);
        setRotation(ear_left, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        leg_left_lower = new MowzieModelRenderer(this, 121, 0);
        leg_left_lower.setRotationPoint(0.0F, 2.5F, 0.0F);
        leg_left_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(leg_left_lower, 0.6829473363053812F, -0.045553093477052F, 0.045553093477052F);
        ear_right = new MowzieModelRenderer(this, 87, 29);
        ear_right.setRotationPoint(-2.0F, -1.5F, -0.5F);
        ear_right.addBox(-4.0F, -2.0F, 0.0F, 5, 3, 0, 0.0F);
        setRotation(ear_right, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        jaw_main = new MowzieModelRenderer(this, 71, 25);
        jaw_main.setRotationPoint(0.0F, 0.0F, -1.0F);
        jaw_main.addBox(-1.5F, -0.0F, -2.0F, 3, 1, 2, 0.0F);
        setRotation(jaw_main, 0.5009094953223726F, 0.0F, 0.0F);
        legleft1 = new MowzieModelRenderer(this, 116, 0);
        legleft1.setRotationPoint(1.7F, -0.4F, 0.0F);
        legleft1.addBox(-0.4F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(legleft1, -0.5462880558742251F, -1.0927506446736497F, -0.40980330836826856F);
        leg_right_lower = new MowzieModelRenderer(this, 121, 5);
        leg_right_lower.setRotationPoint(0.0F, 2.5F, 0.0F);
        leg_right_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(leg_right_lower, 0.6829473363053812F, 0.0F, 0.0F);
        arm_right_lower = new MowzieModelRenderer(this, 111, 0);
        arm_right_lower.setRotationPoint(0.0F, 3.5F, 0.0F);
        arm_right_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotation(arm_right_lower, -2.5497515042385164F, 0.045553093477052F, -0.045553093477052F);
        jaw_back = new MowzieModelRenderer(this, 88, 21);
        jaw_back.setRotationPoint(0.0F, 0.0F, 0.0F);
        jaw_back.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        arm_right_upper = new MowzieModelRenderer(this, 106, 0);
        arm_right_upper.setRotationPoint(-2.5F, -2.7F, 0.5F);
        arm_right_upper.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotation(arm_right_upper, 0.9105382707654417F, -0.136659280431156F, 0.18203784098300857F);
        arm_left_upper.addChild(arm_left_lower);
        head_main.addChild(nose);
        chest.addChild(arm_left_upper);
        chest.addChild(head_main);
        body.addChild(chest);
        body.addChild(legright1);
        head_main.addChild(ear_left);
        legleft1.addChild(leg_left_lower);
        head_main.addChild(ear_right);
        head_main.addChild(jaw_main);
        body.addChild(legleft1);
        legright1.addChild(leg_right_lower);
        arm_right_upper.addChild(arm_right_lower);
        head_main.addChild(jaw_back);
        chest.addChild(arm_right_upper);


        parts = new MowzieModelRenderer[] {
            body_base,
            body_buttpart,
            neck,
            arm_left1,
            arm_right1,
            leg_left1,
            leg_right1,
            lil_tail1,
            leg_left2,
            leg_right2,
            lil_tail2,
            lil_tail3,
            head_base,
            head_connection,
            head_jaw1,
            fang_left,
            fang_right,
            teeth_upperjaw_left,
            teeth_upperjaw_right,
            teeth_upperjaw_front,
            teeth_lowerjaw_left,
            teeth_lowerjaw_right,
            teeth_lowerjaw_front,
            arm_left2,
            wing_left1,
            wing_left2,
            arm_right2,
            wing_right1,
            wing_right2,

            body,
			chest,
			legleft1,
			legright1,
			head_main,
			arm_left_upper,
			arm_right_upper,
			nose,
			jaw_back,
			ear_right,
			ear_left,
			jaw_main,
			arm_left_lower,
			arm_right_lower,
			leg_left_lower,
			leg_right_lower
        };
        setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        EntityChiromaw chiromaw = (EntityChiromaw) entity;
        GlStateManager.pushMatrix();
        if (chiromaw.getIsHanging()) {
            GlStateManager.translate(0.0F, 2.125F, 0.0F);
            GlStateManager.rotate(180, 1F, 0F, 0.0F);
        } else {
            GlStateManager.rotate(40, 1F, 0F, 0.0F);
            GlStateManager.translate(0.0F, 0F, -0.8F);
        }
        
        GlStateManager.enableCull();
        GlStateManager.cullFace(CullFace.FRONT);
        wing_left1.showModel = false;
        wing_left2.showModel = false;
        wing_right1.showModel = false;
        wing_right2.showModel = false;
        body_base.render(unitPixel);
        wing_left1.showModel = true;
        wing_left2.showModel = true;
        wing_right1.showModel = true;
        wing_right2.showModel = true;
        GlStateManager.cullFace(CullFace.BACK);
        body_base.render(unitPixel);
        GlStateManager.disableCull();

        GlStateManager.popMatrix();

        boolean showRider = chiromaw instanceof EntityChiromawGreeblingRider;
        body.showModel = showRider;
		chest.showModel = showRider;
		legleft1.showModel = showRider;
		legright1.showModel = showRider;
		head_main.showModel = showRider;
		arm_left_upper.showModel = showRider;
		arm_right_upper.showModel = showRider;
		nose.showModel = showRider;
		jaw_back.showModel = showRider;
		ear_right.showModel = showRider;
		ear_left.showModel = showRider;
		jaw_main.showModel = showRider;
		arm_left_lower.showModel = showRider;
		arm_right_lower.showModel = showRider;
		leg_left_lower.showModel = showRider;
		leg_right_lower.showModel = showRider;
		body.rotationPointY = 15.5F;
		body.rotationPointZ = -2F;
		body.render(unitPixel);
    }

    public void setRotation(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        EntityChiromaw chiromaw = (EntityChiromaw) entity;
        setToInitPose();

        if (chiromaw.getIsHanging()) {
            arm_right1.rotateAngleZ = 0.5462880558742251F;
            arm_right2.rotateAngleZ = 0F;
            arm_left1.rotateAngleZ = -0.5462880558742251F;
            arm_left2.rotateAngleZ = 0F;

            arm_right1.rotateAngleY = 0.0091106186954104F;
            arm_right2.rotateAngleY = 0F;
            arm_left1.rotateAngleY = -0.0091106186954104F;
            arm_left2.rotateAngleY = 0;

            leg_right1.rotateAngleX = -2.276432943376204F;
            leg_left1.rotateAngleX = -2.276432943376204F;

            lil_tail1.rotateAngleX = 0.27314402793711257F;
            lil_tail2.rotateAngleX = 0.36425021489121656F;
            lil_tail3.rotateAngleX = 0.40980330836826856F;

            lil_tail1.rotateAngleX = 0.27314402793711257F;
            lil_tail2.rotateAngleX = 0.36425021489121656F;
            lil_tail3.rotateAngleX = 0.40980330836826856F;

            head_jaw1.rotateAngleX = 0.9560913642424937F;
            head_base.rotateAngleX = 0.091106186954104F;
        } else {
            float flap = MathHelper.sin((chiromaw.ticksExisted + partialRenderTicks) * 0.5F) * 0.6F;
            arm_right1.rotateAngleZ = 0.5462880558742251F;
            arm_right2.rotateAngleZ = 0F;
            arm_left1.rotateAngleZ = -0.5462880558742251F;
            arm_left2.rotateAngleZ = 0;

            arm_right1.rotateAngleY = 0.9091106186954104F;
            arm_right2.rotateAngleY = 0F;
            arm_left1.rotateAngleY = -0.9091106186954104F;
            arm_left2.rotateAngleY = 0;

            float globalSpeed = 1;
            float globalDegree = 1;
            float frame = chiromaw.ticksExisted + partialRenderTicks;

            swing(arm_right1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, frame, 1F);
            flap(arm_right2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, frame, 1F);
            swing(arm_left1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, frame, 1F);
            flap(arm_left2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, frame, 1F);

            walk(arm_right1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
            walk(arm_right2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);
            walk(arm_left1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
            walk(arm_left2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);

            leg_right1.rotateAngleX = -2.276432943376204F + flap * 0.5F;
            leg_left1.rotateAngleX = -2.276432943376204F + flap * 0.5F;

            lil_tail1.rotateAngleX = 0.27314402793711257F + flap * 0.5F;
            lil_tail2.rotateAngleX = 0.36425021489121656F + flap * 0.25F;
            lil_tail3.rotateAngleX = 0.40980330836826856F + flap * 0.125F;

            head_jaw1.rotateAngleX = 0.9560913642424937F - flap * 0.5F;
            head_base.rotateAngleX = -0.698132F;
            
            // WIP shooting animation
            
            if(chiromaw instanceof EntityChiromawGreeblingRider) {
            	chest.rotateAngleY = 0F + ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F);
            	head_main.rotateAngleY = 0F - ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F);
            	head_main.rotateAngleX = -0.5918411493512771F + ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F) / (float)Math.PI;

            	jaw_main.rotateAngleX = 0.5009094953223726F - ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F) / (float)Math.PI;
            	arm_left_upper.rotateAngleY = 0.045553093477052F - ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F);
            	arm_left_lower.rotateAngleX = -0.6829473363053812F + ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F) / (float)Math.PI;
            	
            	arm_right_upper.rotateAngleX = 0.9105382707654417F - ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F);
            	arm_right_upper.rotateAngleZ = 0.18203784098300857F + ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F) / (float)Math.PI;
            	arm_right_lower.rotateAngleX = -2.5497515042385164F + ((EntityChiromawGreeblingRider) chiromaw).getReloadTimer() * ((float) Math.PI / 180F);
            }
        }
    }
}
