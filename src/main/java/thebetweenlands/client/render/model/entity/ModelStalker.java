package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.mobs.EntityStalker;

public class ModelStalker extends MowzieModelBase {
    private final MowzieModelRenderer root;
    private final MowzieModelRenderer body_base;
    private final MowzieModelRenderer body_main;
    private final MowzieModelRenderer body_chest1;
    private final MowzieModelRenderer body_chest2_main;
    private final MowzieModelRenderer body_chest_right;
    private final MowzieModelRenderer arm_leftaJoint;
    private final MowzieModelRenderer arm_lefta;
    private final MowzieModelRenderer arm_leftb;
    private final MowzieModelRenderer sexy_elbowhair_left1a;
    private final MowzieModelRenderer sexy_elbowhair_left1b;
    private final MowzieModelRenderer sexy_shoulderhair_left1a;
    private final MowzieModelRenderer sexy_shoulderhair_left1b;
    private final MowzieModelRenderer sexy_chesthair_right1a;
    private final MowzieModelRenderer sexy_chesthair_right1b;
    private final MowzieModelRenderer body_chest_left;
    private final MowzieModelRenderer arm_rightaJoint;
    private final MowzieModelRenderer arm_righta;
    private final MowzieModelRenderer arm_rightb;
    private final MowzieModelRenderer sexy_elbowhair_right1a;
    private final MowzieModelRenderer sexy_elbowhair_right1b;
    private final MowzieModelRenderer sexy_shoulderhair_right1a;
    private final MowzieModelRenderer sexy_shoulderhair_right1b;
    private final MowzieModelRenderer sexy_chesthair_left1a;
    private final MowzieModelRenderer sexy_chesthair_left1b;
    private final MowzieModelRenderer neckJoint;
    private final MowzieModelRenderer neck1a;
    private final MowzieModelRenderer neck1b;
    private final MowzieModelRenderer head_main;
    private final MowzieModelRenderer head_connection;
    private final MowzieModelRenderer jaw_lower_left1a;
    private final MowzieModelRenderer jaw_lower_left1b;
    private final MowzieModelRenderer teeth_lower_left;
    private final MowzieModelRenderer sexy_muttonchops_left1a;
    private final MowzieModelRenderer jaw_lower_right1a;
    private final MowzieModelRenderer jaw_lower_right1b;
    private final MowzieModelRenderer teeth_lower_right;
    private final MowzieModelRenderer sexy_muttonchops_right1a;
    private final MowzieModelRenderer jaw_lower_main;
    private final MowzieModelRenderer teeth_lower_mid;
    private final MowzieModelRenderer ear_left1a;
    private final MowzieModelRenderer ear_left1b;
    private final MowzieModelRenderer ear_right1a;
    private final MowzieModelRenderer ear_right1b;
    private final MowzieModelRenderer teeth_upper;
    private final MowzieModelRenderer sexy_facialhair_left1a;
    private final MowzieModelRenderer sexy_facialhair_right1a;
    private final MowzieModelRenderer bigeye;
    private final AdvancedModelRenderer bigEyelidTop;
    private final AdvancedModelRenderer bigEyelidBottom;
    private final AdvancedModelRenderer medEyelidTopRight;
    private final AdvancedModelRenderer medEyelidTopLeft;
    private final AdvancedModelRenderer medEyelidBottomRight;
    private final AdvancedModelRenderer medEyelidBottomLeft;
    private final AdvancedModelRenderer smallEyelidRight;
    private final AdvancedModelRenderer smallEyelidLeft;
    private final MowzieModelRenderer midarm_rightaJoint;
    private final MowzieModelRenderer midarm_righta;
    private final MowzieModelRenderer midarm_rightb;
    private final MowzieModelRenderer sexy_elbowhair_righta;
    private final MowzieModelRenderer sexy_elbowhair_rightb;
    private final MowzieModelRenderer midarm_leftaJoint;
    private final MowzieModelRenderer midarm_lefta;
    private final MowzieModelRenderer midarm_leftb;
    private final MowzieModelRenderer sexy_elbowhair_lefta;
    private final MowzieModelRenderer sexy_elbowhair_leftb;
    private final MowzieModelRenderer leg_right1aJoint;
    private final MowzieModelRenderer leg_right1a;
    private final MowzieModelRenderer leg_right1b;
    private final MowzieModelRenderer leg_left1aJoint;
    private final MowzieModelRenderer leg_left1a;
    private final MowzieModelRenderer leg_left1b;

    public ModelStalker() {
        textureWidth = 128;
        textureHeight = 128;

        root = new MowzieModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body_base = new MowzieModelRenderer(this);
        body_base.setRotationPoint(0.0F, -9.0F, 6.0F);
        root.addChild(body_base);
        setRotationAngle(body_base, 1.3659F, 0.0F, 0.0F);
        body_base.cubeList.add(new ModelBox(body_base, 0, 0, -4.5F, -2.5686F, -3.4697F, 9, 5, 6, 0.0F, false));

        body_main = new MowzieModelRenderer(this);
        body_main.setRotationPoint(0.0F, -2.5686F, 0.5303F);
        body_base.addChild(body_main);
        setRotationAngle(body_main, 0.0911F, 0.0F, 0.0F);
        body_main.cubeList.add(new ModelBox(body_main, 0, 12, -4.0F, -7.818F, -4.0083F, 8, 8, 6, 0.0F, false));

        body_chest1 = new MowzieModelRenderer(this);
        body_chest1.setRotationPoint(0.0F, -7.818F, -0.0083F);
        body_main.addChild(body_chest1);
        setRotationAngle(body_chest1, -0.1367F, 0.0F, 0.0F);
        body_chest1.cubeList.add(new ModelBox(body_chest1, 0, 27, -4.5F, -3.4551F, -3.9627F, 9, 4, 6, 0.0F, false));

        body_chest2_main = new MowzieModelRenderer(this);
        body_chest2_main.setRotationPoint(0.0F, -3.4551F, -4.9627F);
        body_chest1.addChild(body_chest2_main);
        setRotationAngle(body_chest2_main, -0.3187F, 0.0F, 0.0F);
        body_chest2_main.cubeList.add(new ModelBox(body_chest2_main, 0, 38, 0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F, false));

        body_chest_right = new MowzieModelRenderer(this);
        body_chest_right.setRotationPoint(2.0F, -3.0F, 6.0F);
        body_chest2_main.addChild(body_chest_right);
        setRotationAngle(body_chest_right, 0.0F, -0.1367F, 0.0F);
        body_chest_right.cubeList.add(new ModelBox(body_chest_right, 0, 38, -2.7988F, -2.0F, -5.6716F, 6, 6, 7, 0.0F, false));

        arm_leftaJoint = new MowzieModelRenderer(this);
        arm_leftaJoint.setRotationPoint(3.2012F, -1.0F, -0.6716F);
        body_chest_right.addChild(arm_leftaJoint);

        arm_lefta = new MowzieModelRenderer(this);
        arm_lefta.setRotationPoint(0, 0, 0);
        arm_leftaJoint.addChild(arm_lefta);
        setRotationAngle(arm_lefta, -0.5463F, 0.0F, -0.6374F);
        arm_lefta.cubeList.add(new ModelBox(arm_lefta, 40, 0, -1.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F, false));

        arm_leftb = new MowzieModelRenderer(this);
        arm_leftb.setRotationPoint(0.0F, 10.0F, 1.0F);
        arm_lefta.addChild(arm_leftb);
        setRotationAngle(arm_leftb, -1.2748F, 0.0F, 0.0F);
        arm_leftb.cubeList.add(new ModelBox(arm_leftb, 40, 13, -1.01F, 0.0F, -2.0F, 2, 10, 2, 0.0F, false));

        sexy_elbowhair_left1a = new MowzieModelRenderer(this);
        sexy_elbowhair_left1a.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_leftb.addChild(sexy_elbowhair_left1a);
        setRotationAngle(sexy_elbowhair_left1a, -0.8652F, 0.0F, 0.0F);
        sexy_elbowhair_left1a.cubeList.add(new ModelBox(sexy_elbowhair_left1a, 105, 0, -1.0F, 0.0F, 0.0F, 2, 0, 1, 0.0F, false));

        sexy_elbowhair_left1b = new MowzieModelRenderer(this);
        sexy_elbowhair_left1b.setRotationPoint(0.0F, 0.0F, 1.0F);
        sexy_elbowhair_left1a.addChild(sexy_elbowhair_left1b);
        setRotationAngle(sexy_elbowhair_left1b, 0.3187F, 0.0F, 0.0F);
        sexy_elbowhair_left1b.cubeList.add(new ModelBox(sexy_elbowhair_left1b, 105, 2, -1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F, false));

        sexy_shoulderhair_left1a = new MowzieModelRenderer(this);
        sexy_shoulderhair_left1a.setRotationPoint(1.0F, 0.0F, 0.0F);
        arm_lefta.addChild(sexy_shoulderhair_left1a);
        setRotationAngle(sexy_shoulderhair_left1a, 0.0F, 0.0F, -0.3187F);
        sexy_shoulderhair_left1a.cubeList.add(new ModelBox(sexy_shoulderhair_left1a, 95, 0, 0.0F, 0.0F, -1.0F, 0, 2, 2, 0.0F, false));

        sexy_shoulderhair_left1b = new MowzieModelRenderer(this);
        sexy_shoulderhair_left1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        sexy_shoulderhair_left1a.addChild(sexy_shoulderhair_left1b);
        setRotationAngle(sexy_shoulderhair_left1b, 0.0F, 0.0F, -0.3187F);
        sexy_shoulderhair_left1b.cubeList.add(new ModelBox(sexy_shoulderhair_left1b, 95, 3, 0.0F, 0.0F, -1.0F, 0, 2, 2, 0.0F, false));

        sexy_chesthair_right1a = new MowzieModelRenderer(this);
        sexy_chesthair_right1a.setRotationPoint(-2.7988F, -1.0F, -5.6716F);
        body_chest_right.addChild(sexy_chesthair_right1a);
        setRotationAngle(sexy_chesthair_right1a, -0.3643F, 0.0F, 0.0F);
        sexy_chesthair_right1a.cubeList.add(new ModelBox(sexy_chesthair_right1a, 0, 52, 0.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F, false));

        sexy_chesthair_right1b = new MowzieModelRenderer(this);
        sexy_chesthair_right1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        sexy_chesthair_right1a.addChild(sexy_chesthair_right1b);
        setRotationAngle(sexy_chesthair_right1b, -0.3643F, 0.0F, 0.0F);
        sexy_chesthair_right1b.cubeList.add(new ModelBox(sexy_chesthair_right1b, 0, 55, 0.0F, 0.0F, 0.0F, 6, 3, 0, 0.0F, false));

        body_chest_left = new MowzieModelRenderer(this);
        body_chest_left.setRotationPoint(-2.0F, -3.0F, 6.0F);
        body_chest2_main.addChild(body_chest_left);
        setRotationAngle(body_chest_left, 0.0F, 0.1367F, 0.0F);
        body_chest_left.cubeList.add(new ModelBox(body_chest_left, 27, 38, -3.2012F, -1.99F, -5.6716F, 6, 6, 7, 0.0F, false));

        arm_rightaJoint = new MowzieModelRenderer(this);
        arm_rightaJoint.setRotationPoint(-3.2012F, -1.0F, -0.6716F);
        body_chest_left.addChild(arm_rightaJoint);

        arm_righta = new MowzieModelRenderer(this);
        arm_righta.setRotationPoint(0, 0, 0);
        arm_rightaJoint.addChild(arm_righta);
        setRotationAngle(arm_righta, -0.5463F, 0.0F, 0.6374F);
        arm_righta.cubeList.add(new ModelBox(arm_righta, 49, 0, -1.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F, false));

        arm_rightb = new MowzieModelRenderer(this);
        arm_rightb.setRotationPoint(0.0F, 10.0F, 1.0F);
        arm_righta.addChild(arm_rightb);
        setRotationAngle(arm_rightb, -1.2748F, 0.0F, 0.0F);
        arm_rightb.cubeList.add(new ModelBox(arm_rightb, 49, 13, -0.99F, 0.0F, -2.0F, 2, 10, 2, 0.0F, false));

        sexy_elbowhair_right1a = new MowzieModelRenderer(this);
        sexy_elbowhair_right1a.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_rightb.addChild(sexy_elbowhair_right1a);
        setRotationAngle(sexy_elbowhair_right1a, -0.8652F, 0.0F, 0.0F);
        sexy_elbowhair_right1a.cubeList.add(new ModelBox(sexy_elbowhair_right1a, 110, 0, -1.0F, 0.0F, 0.0F, 2, 0, 1, 0.0F, false));

        sexy_elbowhair_right1b = new MowzieModelRenderer(this);
        sexy_elbowhair_right1b.setRotationPoint(0.0F, 0.0F, 1.0F);
        sexy_elbowhair_right1a.addChild(sexy_elbowhair_right1b);
        setRotationAngle(sexy_elbowhair_right1b, 0.3187F, 0.0F, 0.0F);
        sexy_elbowhair_right1b.cubeList.add(new ModelBox(sexy_elbowhair_right1b, 110, 2, -1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F, false));

        sexy_shoulderhair_right1a = new MowzieModelRenderer(this);
        sexy_shoulderhair_right1a.setRotationPoint(-1.0F, 0.0F, 0.0F);
        arm_righta.addChild(sexy_shoulderhair_right1a);
        setRotationAngle(sexy_shoulderhair_right1a, 0.0F, 0.0F, 0.3187F);
        sexy_shoulderhair_right1a.cubeList.add(new ModelBox(sexy_shoulderhair_right1a, 100, 0, 0.0F, 0.0F, -1.0F, 0, 2, 2, 0.0F, false));

        sexy_shoulderhair_right1b = new MowzieModelRenderer(this);
        sexy_shoulderhair_right1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        sexy_shoulderhair_right1a.addChild(sexy_shoulderhair_right1b);
        setRotationAngle(sexy_shoulderhair_right1b, 0.0F, 0.0F, 0.3187F);
        sexy_shoulderhair_right1b.cubeList.add(new ModelBox(sexy_shoulderhair_right1b, 100, 3, 0.0F, 0.0F, -1.0F, 0, 2, 2, 0.0F, false));

        sexy_chesthair_left1a = new MowzieModelRenderer(this);
        sexy_chesthair_left1a.setRotationPoint(2.7988F, -1.0F, -5.6716F);
        body_chest_left.addChild(sexy_chesthair_left1a);
        setRotationAngle(sexy_chesthair_left1a, -0.3643F, 0.0F, 0.0F);
        sexy_chesthair_left1a.cubeList.add(new ModelBox(sexy_chesthair_left1a, 27, 52, -6.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F, false));

        sexy_chesthair_left1b = new MowzieModelRenderer(this);
        sexy_chesthair_left1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        sexy_chesthair_left1a.addChild(sexy_chesthair_left1b);
        setRotationAngle(sexy_chesthair_left1b, -0.3643F, 0.0F, 0.0F);
        sexy_chesthair_left1b.cubeList.add(new ModelBox(sexy_chesthair_left1b, 27, 55, -6.0F, 0.0F, 0.0F, 6, 3, 0, 0.0F, false));

        neckJoint = new MowzieModelRenderer(this);
        neckJoint.setRotationPoint(0.0F, -4.0F, 6.0F);
        body_chest2_main.addChild(neckJoint);
        setRotationAngle(neckJoint, 0.6829F, 0.0F, 0.0F);


        neck1a = new MowzieModelRenderer(this);
        neck1a.setRotationPoint(0.0F, 0.0F, 0.0F);
        neckJoint.addChild(neck1a);
        neck1a.cubeList.add(new ModelBox(neck1a, 0, 59, -2.0F, -3.1446F, -2.5932F, 4, 3, 4, 0.0F, false));

        neck1b = new MowzieModelRenderer(this);
        neck1b.setRotationPoint(0.0F, -3.1446F, -0.5932F);
        neck1a.addChild(neck1b);
        setRotationAngle(neck1b, -0.182F, 0.0F, 0.0F);
        neck1b.cubeList.add(new ModelBox(neck1b, 17, 59, -2.0F, -2.6379F, -1.967F, 4, 3, 4, 0.0F, false));

        head_main = new MowzieModelRenderer(this);
        head_main.setRotationPoint(0.0F, -2.6379F, 0.533F);
        neck1b.addChild(head_main);
        setRotationAngle(head_main, -1.5026F, 0.0F, 0.0F);
        head_main.cubeList.add(new ModelBox(head_main, 0, 67, -4.0F, -4.9977F, -6.9318F, 8, 6, 8, 0.0F, false));

        head_connection = new MowzieModelRenderer(this);
        head_connection.setRotationPoint(0.0F, 1.0023F, 1.0682F);
        head_main.addChild(head_connection);
        head_connection.cubeList.add(new ModelBox(head_connection, 0, 82, -4.0F, 0.0F, -2.0F, 8, 2, 2, 0.0F, false));

        jaw_lower_left1a = new MowzieModelRenderer(this);
        jaw_lower_left1a.setRotationPoint(-1.5F, 0.0F, -2.0F);
        head_connection.addChild(jaw_lower_left1a);
        setRotationAngle(jaw_lower_left1a, 0.3643F, 0.2276F, -0.0911F);
        jaw_lower_left1a.cubeList.add(new ModelBox(jaw_lower_left1a, 0, 87, -3.0F, 0.0F, -5.0F, 3, 2, 5, 0.0F, true));

        jaw_lower_left1b = new MowzieModelRenderer(this);
        jaw_lower_left1b.setRotationPoint(0.0F, 2.0F, -5.0F);
        jaw_lower_left1a.addChild(jaw_lower_left1b);
        setRotationAngle(jaw_lower_left1b, -0.3187F, 0.0F, 0.0F);
        jaw_lower_left1b.cubeList.add(new ModelBox(jaw_lower_left1b, 17, 87, -3.0F, -2.0F, -3.0F, 3, 2, 3, 0.0F, true));

        teeth_lower_left = new MowzieModelRenderer(this);
        teeth_lower_left.setRotationPoint(0.0F, -2.0F, -3.0F);
        jaw_lower_left1b.addChild(teeth_lower_left);
        teeth_lower_left.cubeList.add(new ModelBox(teeth_lower_left, 30, 87, -3.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F, false));

        sexy_muttonchops_left1a = new MowzieModelRenderer(this);
        sexy_muttonchops_left1a.setRotationPoint(-3.0F, 2.0F, 0.0F);
        jaw_lower_left1a.addChild(sexy_muttonchops_left1a);
        setRotationAngle(sexy_muttonchops_left1a, 0.0F, 0.0F, 0.2276F);
        sexy_muttonchops_left1a.cubeList.add(new ModelBox(sexy_muttonchops_left1a, 44, 72, 0.0F, 0.0F, -5.0F, 0, 2, 5, 0.0F, false));

        jaw_lower_right1a = new MowzieModelRenderer(this);
        jaw_lower_right1a.setRotationPoint(1.5F, 0.0F, -2.0F);
        head_connection.addChild(jaw_lower_right1a);
        setRotationAngle(jaw_lower_right1a, 0.3643F, -0.2276F, 0.0911F);
        jaw_lower_right1a.cubeList.add(new ModelBox(jaw_lower_right1a, 0, 95, 0.0F, 0.0F, -5.0F, 3, 2, 5, 0.0F, true));

        jaw_lower_right1b = new MowzieModelRenderer(this);
        jaw_lower_right1b.setRotationPoint(0.0F, 2.0F, -5.0F);
        jaw_lower_right1a.addChild(jaw_lower_right1b);
        setRotationAngle(jaw_lower_right1b, -0.3187F, 0.0F, 0.0F);
        jaw_lower_right1b.cubeList.add(new ModelBox(jaw_lower_right1b, 17, 95, 0.0F, -2.0F, -3.0F, 3, 2, 3, 0.0F, true));

        teeth_lower_right = new MowzieModelRenderer(this);
        teeth_lower_right.setRotationPoint(0.0F, -2.0F, -3.0F);
        jaw_lower_right1b.addChild(teeth_lower_right);
        teeth_lower_right.cubeList.add(new ModelBox(teeth_lower_right, 30, 95, 0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F, false));

        sexy_muttonchops_right1a = new MowzieModelRenderer(this);
        sexy_muttonchops_right1a.setRotationPoint(3.0F, 2.0F, 0.0F);
        jaw_lower_right1a.addChild(sexy_muttonchops_right1a);
        setRotationAngle(sexy_muttonchops_right1a, 0.0F, 0.0F, -0.2276F);
        sexy_muttonchops_right1a.cubeList.add(new ModelBox(sexy_muttonchops_right1a, 33, 72, 0.0F, 0.0F, -5.0F, 0, 2, 5, 0.0F, false));

        jaw_lower_main = new MowzieModelRenderer(this);
        jaw_lower_main.setRotationPoint(0.0F, 0.5F, -2.0F);
        head_connection.addChild(jaw_lower_main);
        setRotationAngle(jaw_lower_main, 0.5463F, 0.0F, 0.0F);
        jaw_lower_main.cubeList.add(new ModelBox(jaw_lower_main, 0, 103, -2.0F, 0.0F, -6.0F, 4, 2, 6, 0.0F, false));

        teeth_lower_mid = new MowzieModelRenderer(this);
        teeth_lower_mid.setRotationPoint(0.0F, 0.0F, -6.0F);
        jaw_lower_main.addChild(teeth_lower_mid);
        teeth_lower_mid.cubeList.add(new ModelBox(teeth_lower_mid, 21, 103, -2.0F, -1.0F, 0.0F, 4, 1, 4, 0.0F, false));

        ear_left1a = new MowzieModelRenderer(this);
        ear_left1a.setRotationPoint(-4.0F, -3.9977F, -0.9318F);
        head_main.addChild(ear_left1a);
        setRotationAngle(ear_left1a, 0.0F, -0.4098F, 0.0F);
        ear_left1a.cubeList.add(new ModelBox(ear_left1a, 33, 64, 0.0F, -1.0F, 0.0F, 0, 4, 3, 0.0F, false));

        ear_left1b = new MowzieModelRenderer(this);
        ear_left1b.setRotationPoint(0.0F, -1.0F, 3.0F);
        ear_left1a.addChild(ear_left1b);
        setRotationAngle(ear_left1b, 0.0F, 0.2276F, 0.0F);
        ear_left1b.cubeList.add(new ModelBox(ear_left1b, 40, 63, 0.0F, 0.0F, 0.0F, 0, 4, 4, 0.0F, false));

        ear_right1a = new MowzieModelRenderer(this);
        ear_right1a.setRotationPoint(4.0F, -3.9977F, -0.9318F);
        head_main.addChild(ear_right1a);
        setRotationAngle(ear_right1a, 0.0F, 0.4098F, 0.0F);
        ear_right1a.cubeList.add(new ModelBox(ear_right1a, 33, 69, 0.0F, -1.0F, 0.0F, 0, 4, 3, 0.0F, false));

        ear_right1b = new MowzieModelRenderer(this);
        ear_right1b.setRotationPoint(0.0F, -1.0F, 3.0F);
        ear_right1a.addChild(ear_right1b);
        setRotationAngle(ear_right1b, 0.0F, -0.2276F, 0.0F);
        ear_right1b.cubeList.add(new ModelBox(ear_right1b, 40, 68, 0.0F, 0.0F, 0.0F, 0, 4, 4, 0.0F, false));

        teeth_upper = new MowzieModelRenderer(this);
        teeth_upper.setRotationPoint(0.0F, 1.0023F, -6.9318F);
        head_main.addChild(teeth_upper);
        teeth_upper.cubeList.add(new ModelBox(teeth_upper, 0, 112, -4.0F, 0.0F, 0.0F, 8, 1, 5, 0.0F, false));

        sexy_facialhair_left1a = new MowzieModelRenderer(this);
        sexy_facialhair_left1a.setRotationPoint(-4.0F, -2.4977F, -1.9318F);
        head_main.addChild(sexy_facialhair_left1a);
        setRotationAngle(sexy_facialhair_left1a, -0.4098F, -0.4098F, 0.0F);
        sexy_facialhair_left1a.cubeList.add(new ModelBox(sexy_facialhair_left1a, 49, 65, 0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F, false));

        sexy_facialhair_right1a = new MowzieModelRenderer(this);
        sexy_facialhair_right1a.setRotationPoint(4.0F, -2.4977F, -1.9318F);
        head_main.addChild(sexy_facialhair_right1a);
        setRotationAngle(sexy_facialhair_right1a, -0.4098F, 0.4098F, 0.0F);
        sexy_facialhair_right1a.cubeList.add(new ModelBox(sexy_facialhair_right1a, 49, 70, 0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F, false));

        bigeye = new MowzieModelRenderer(this);
        bigeye.setRotationPoint(0.0F, -2.9977F, -5.9318F);
        head_main.addChild(bigeye);
        setRotationAngle(bigeye, -0.0911F, 0.0F, 0.0F);
        bigeye.cubeList.add(new ModelBox(bigeye, 54, 67, -2.0F, -2.1737F, -2.1903F, 4, 4, 4, 0.0F, false));

        bigEyelidTop = new AdvancedModelRenderer(this);
        bigEyelidTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        bigeye.addChild(bigEyelidTop);
        bigEyelidTop.cubeList.add(new ModelBox(bigEyelidTop, 34, 58, -2.0F, -2.1737F, -2.1902F, 4, 2, 5, 0.0F, false));

        bigEyelidBottom = new AdvancedModelRenderer(this);
        bigEyelidBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        bigeye.addChild(bigEyelidBottom);
        bigEyelidBottom.cubeList.add(new ModelBox(bigEyelidBottom, 58, 58, -2.0F, -0.1737F, -2.1902F, 4, 2, 5, 0.0F, false));

        medEyelidTopRight = new AdvancedModelRenderer(this);
        medEyelidTopRight.setRotationPoint(0.0F, 0.0F, 0.0F);
        head_main.addChild(medEyelidTopRight);
        medEyelidTopRight.cubeList.add(new ModelBox(medEyelidTopRight, 0, 4, -4.0F, -3.9977F, -6.9318F, 1, 1, 1, 0.0F, false));

        medEyelidTopLeft = new AdvancedModelRenderer(this);
        medEyelidTopLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
        head_main.addChild(medEyelidTopLeft);
        medEyelidTopLeft.cubeList.add(new ModelBox(medEyelidTopLeft, 0, 4, 3.0F, -3.9977F, -6.9318F, 1, 1, 1, 0.0F, true));

        medEyelidBottomRight = new AdvancedModelRenderer(this);
        medEyelidBottomRight.setRotationPoint(-1.0F, 1.0F, -1.0F);
        head_main.addChild(medEyelidBottomRight);
        medEyelidBottomRight.cubeList.add(new ModelBox(medEyelidBottomRight, 0, 2, -3.0F, -3.9977F, -5.9318F, 1, 1, 1, 0.0F, false));

        medEyelidBottomLeft = new AdvancedModelRenderer(this);
        medEyelidBottomLeft.setRotationPoint(0.0F, 1.0F, 0.0F);
        head_main.addChild(medEyelidBottomLeft);
        medEyelidBottomLeft.cubeList.add(new ModelBox(medEyelidBottomLeft, 0, 2, 3.0F, -3.9977F, -6.9318F, 1, 1, 1, 0.0F, true));

        smallEyelidRight = new AdvancedModelRenderer(this);
        smallEyelidRight.setRotationPoint(0.0F, 0.0F, 2.0F);
        head_main.addChild(smallEyelidRight);
        smallEyelidRight.cubeList.add(new ModelBox(smallEyelidRight, 0, 11, -4.0F, -3.9977F, -6.9318F, 1, 1, 1, 0.0F, false));

        smallEyelidLeft = new AdvancedModelRenderer(this);
        smallEyelidLeft.setRotationPoint(0.0F, 0.0F, 2.0F);
        head_main.addChild(smallEyelidLeft);
        smallEyelidLeft.cubeList.add(new ModelBox(smallEyelidLeft, 0, 11, 3.0F, -3.9977F, -6.9318F, 1, 1, 1, 0.0F, true));

        midarm_rightaJoint = new MowzieModelRenderer(this);
        midarm_rightaJoint.setRotationPoint(4.5F, -1.4551F, -1.9627F);
        body_chest1.addChild(midarm_rightaJoint);

        midarm_righta = new MowzieModelRenderer(this);
        midarm_righta.setRotationPoint(0, 0, 0);
        midarm_rightaJoint.addChild(midarm_righta);
        setRotationAngle(midarm_righta, -0.0456F, -0.2731F, -0.4098F);
        midarm_righta.cubeList.add(new ModelBox(midarm_righta, 58, 0, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));

        midarm_rightb = new MowzieModelRenderer(this);
        midarm_rightb.setRotationPoint(0.0F, 8.0F, 1.0F);
        midarm_righta.addChild(midarm_rightb);
        setRotationAngle(midarm_rightb, -1.0472F, 0.0F, 0.0F);
        midarm_rightb.cubeList.add(new ModelBox(midarm_rightb, 58, 11, -0.99F, 0.0F, -2.0F, 2, 9, 2, 0.0F, false));

        sexy_elbowhair_righta = new MowzieModelRenderer(this);
        sexy_elbowhair_righta.setRotationPoint(0.0F, 0.0F, 0.0F);
        midarm_rightb.addChild(sexy_elbowhair_righta);
        setRotationAngle(sexy_elbowhair_righta, -1.1383F, 0.0F, 0.0F);
        sexy_elbowhair_righta.cubeList.add(new ModelBox(sexy_elbowhair_righta, 105, 5, -1.0F, 0.0F, 0.0F, 2, 0, 1, 0.0F, false));

        sexy_elbowhair_rightb = new MowzieModelRenderer(this);
        sexy_elbowhair_rightb.setRotationPoint(0.0F, 0.0F, 1.0F);
        sexy_elbowhair_righta.addChild(sexy_elbowhair_rightb);
        setRotationAngle(sexy_elbowhair_rightb, 0.3187F, 0.0F, 0.0F);
        sexy_elbowhair_rightb.cubeList.add(new ModelBox(sexy_elbowhair_rightb, 105, 7, -1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F, false));

        midarm_leftaJoint = new MowzieModelRenderer(this);
        midarm_leftaJoint.setRotationPoint(-4.5F, -1.4551F, -1.9627F);
        body_chest1.addChild(midarm_leftaJoint);

        midarm_lefta = new MowzieModelRenderer(this);
        midarm_lefta.setRotationPoint(0, 0 ,0);
        midarm_leftaJoint.addChild(midarm_lefta);
        setRotationAngle(midarm_lefta, -0.0456F, 0.2731F, 0.4098F);
        midarm_lefta.cubeList.add(new ModelBox(midarm_lefta, 67, 0, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));

        midarm_leftb = new MowzieModelRenderer(this);
        midarm_leftb.setRotationPoint(0.0F, 8.0F, 1.0F);
        midarm_lefta.addChild(midarm_leftb);
        setRotationAngle(midarm_leftb, -1.0472F, 0.0F, 0.0F);
        midarm_leftb.cubeList.add(new ModelBox(midarm_leftb, 67, 11, -1.01F, 0.0F, -2.0F, 2, 9, 2, 0.0F, false));

        sexy_elbowhair_lefta = new MowzieModelRenderer(this);
        sexy_elbowhair_lefta.setRotationPoint(0.0F, 0.0F, 0.0F);
        midarm_leftb.addChild(sexy_elbowhair_lefta);
        setRotationAngle(sexy_elbowhair_lefta, -1.1383F, 0.0F, 0.0F);
        sexy_elbowhair_lefta.cubeList.add(new ModelBox(sexy_elbowhair_lefta, 110, 5, -1.0F, 0.0F, 0.0F, 2, 0, 1, 0.0F, false));

        sexy_elbowhair_leftb = new MowzieModelRenderer(this);
        sexy_elbowhair_leftb.setRotationPoint(0.0F, 0.0F, 1.0F);
        sexy_elbowhair_lefta.addChild(sexy_elbowhair_leftb);
        setRotationAngle(sexy_elbowhair_leftb, 0.3187F, 0.0F, 0.0F);
        sexy_elbowhair_leftb.cubeList.add(new ModelBox(sexy_elbowhair_leftb, 110, 7, -1.0F, 0.0F, 0.0F, 2, 0, 2, 0.0F, false));

        leg_right1aJoint = new MowzieModelRenderer(this);
        leg_right1aJoint.setRotationPoint(3.5F, 1.9314F, 0.5303F);
        body_base.addChild(leg_right1aJoint);

        leg_right1a = new MowzieModelRenderer(this);
        leg_right1a.setRotationPoint(0, 0, 0);
        leg_right1aJoint.addChild(leg_right1a);
        setRotationAngle(leg_right1a, -0.6829F, 0.2731F, -1.0472F);
        leg_right1a.cubeList.add(new ModelBox(leg_right1a, 76, 0, -1.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F, false));

        leg_right1b = new MowzieModelRenderer(this);
        leg_right1b.setRotationPoint(1.0F, 8.0F, 0.0F);
        leg_right1a.addChild(leg_right1b);
        setRotationAngle(leg_right1b, 0.0F, 0.0F, 0.8652F);
        leg_right1b.cubeList.add(new ModelBox(leg_right1b, 76, 12, -2.0F, 0.0F, -0.99F, 2, 9, 2, 0.0F, false));

        leg_left1aJoint = new MowzieModelRenderer(this);
        leg_left1aJoint.setRotationPoint(-3.5F, 1.9314F, 0.5303F);
        body_base.addChild(leg_left1aJoint);

        leg_left1a = new MowzieModelRenderer(this);
        leg_left1a.setRotationPoint(0, 0, 0);
        leg_left1aJoint.addChild(leg_left1a);
        setRotationAngle(leg_left1a, -0.6829F, -0.2731F, 1.0472F);
        leg_left1a.cubeList.add(new ModelBox(leg_left1a, 85, 0, -1.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F, true));

        leg_left1b = new MowzieModelRenderer(this);
        leg_left1b.setRotationPoint(-1.0F, 8.0F, 0.0F);
        leg_left1a.addChild(leg_left1b);
        setRotationAngle(leg_left1b, 0.0F, 0.0F, -0.8652F);
        leg_left1b.cubeList.add(new ModelBox(leg_left1b, 85, 12, 0.0F, 0.0F, -1.0F, 2, 9, 2, 0.0F, true));

        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        root.render(f5);
    }

    public void setRotationAngle(MowzieModelRenderer MowzieModelRenderer, float x, float y, float z) {
        MowzieModelRenderer.rotateAngleX = x;
        MowzieModelRenderer.rotateAngleY = y;
        MowzieModelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        neck1a.rotateAngleZ -= (f3 / (180f / (float) Math.PI)) / 2;
        head_main.rotateAngleZ -= (f3 / (180f / (float) Math.PI)) / 2;
        neck1a.rotateAngleX += (f4 / (180f / (float) Math.PI)) / 2;
        head_main.rotateAngleX += (f4 / (180f / (float) Math.PI)) / 2;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        super.setLivingAnimations(entity, swing, speed, partialRenderTicks);
        setToInitPose();

        EntityStalker stalker = (EntityStalker) entity;
        float frame = entity.ticksExisted + partialRenderTicks;

        float scale = 1.02f;
        bigEyelidBottom.setScale(scale);
        bigEyelidTop.setScale(scale);
        medEyelidBottomLeft.setScale(scale);
        medEyelidBottomRight.setScale(scale);
        medEyelidTopLeft.setScale(scale);
        medEyelidTopRight.setScale(scale);
        smallEyelidLeft.setScale(scale);
        smallEyelidRight.setScale(scale);

        medEyelidTopRight.rotationPointX += 0.07;
        medEyelidTopRight.rotationPointZ += 0.13;
        medEyelidTopRight.rotationPointY += 0.085;
        medEyelidTopLeft.rotationPointX -= 0.07;
        medEyelidTopLeft.rotationPointZ += 0.13;
        medEyelidTopLeft.rotationPointY += 0.085;
        medEyelidBottomRight.rotationPointX += 0.05;
        medEyelidBottomRight.rotationPointZ += 0.1;
        medEyelidBottomLeft.rotationPointX -= 0.07;
        medEyelidBottomLeft.rotationPointZ += 0.13;
        medEyelidBottomLeft.rotationPointY += 0.06;
        medEyelidBottomRight.rotationPointY += 0.06;
        smallEyelidLeft.rotationPointX -= 0.07;
        smallEyelidLeft.rotationPointZ += 0.12;
        smallEyelidLeft.rotationPointY += 0.08;
        smallEyelidRight.rotationPointX += 0.07;
        smallEyelidRight.rotationPointZ += 0.12;
        smallEyelidRight.rotationPointY += 0.08;

        float blinkFrame = (float) (frame * Math.PI * 2);
        float blinkSmallRight = 0;
        float blinkSmallLeft = 0;
        float blinkMedRight = 0;
        float blinkMedLeft = 0;
        float blinkBig = 0;
        if ((int)(frame * 0.04) % 5 == 0) {
            blinkSmallRight = (float) Math.pow(Math.sin(blinkFrame * 0.02 + 0.5), 10);
            blinkSmallLeft = (float) Math.pow(Math.sin(blinkFrame * 0.02 + 0.7), 10);
            blinkMedRight = (float) Math.pow(Math.sin(blinkFrame * 0.02 - 0.2), 10);
            blinkMedLeft = (float) Math.pow(Math.sin(blinkFrame * 0.02 + 0.1), 10);
            blinkBig = (float) Math.pow(Math.sin(blinkFrame * 0.02 + 0.35), 20);
        }
        smallEyelidLeft.rotationPointY -= 1 * (blinkSmallLeft);
        smallEyelidRight.rotationPointY -= 1 * (blinkSmallRight);
        medEyelidTopRight.rotationPointY -= 1 * (blinkMedRight);
        medEyelidTopLeft.rotationPointY -= 1 * (blinkMedLeft);
        medEyelidBottomRight.rotationPointY += 1 * (blinkMedRight);
        medEyelidBottomLeft.rotationPointY += 1 * (blinkMedLeft);

        float blinkAnim1 = (float) Math.pow(2 * (1 - blinkBig) - 1, 2);
        if (1 - blinkBig < 0.5) blinkAnim1 *= -1;
        float blinkAnim2 = 1 - (float) Math.pow(2 * (1 - blinkBig) - 1, 6);
        float blinkAnim3 = (float) Math.pow(Math.sin((1 - blinkBig) - 1.8), 10) - 1;
        float blinkAnim4 = (float) Math.pow(1 - blinkBig, 20);
        bigEyelidTop.rotateAngleX -= 0.5 * (blinkAnim1 + 1) + 0.4 * blinkAnim4;
        bigEyelidTop.rotationPointY -= 0.9 * blinkAnim2 - 0.3 * blinkAnim4;
        bigEyelidTop.rotationPointZ -= 2 * blinkAnim3 + 0.6;
        bigEyelidTop.scaleZ -= 0.4 * blinkAnim4;
        bigEyelidBottom.rotateAngleX += 0.5 * (blinkAnim1 + 1) + 0.4 * blinkAnim4;
        bigEyelidBottom.rotationPointY += 0.9 * blinkAnim2 - 0.2 * blinkAnim4;
        bigEyelidBottom.rotationPointZ -= 1 * blinkAnim3 + 0.6;
        bigEyelidBottom.scaleZ -= 0.45 * blinkAnim4;

        root.rotationPointY -= 2.2;

//        swing = frame;
//        speed = 1f;

        float newf1 = speed;
        if (newf1 > 0.4) newf1 = 0.4f;
        float newf12 = speed;
        if (newf12 > 0.7) newf12 = 0.7f;

        float globalDegree = 1.4f;
        float wiggleDegree = 2f;
        float globalSpeed = 0.8f;
        float globalHeight = 1f;

        body_base.rotationPointX -= wiggleDegree * globalDegree * newf1 * 3f * Math.cos(globalSpeed * swing);
        swing(body_base, globalSpeed, 0.2f * globalDegree * wiggleDegree, false, 1.6f, 0, swing, newf1);
        flap(body_main, globalSpeed, 0.2f * globalDegree * wiggleDegree, false, 0.8f, 0, swing, newf1);
        flap(body_chest1, globalSpeed, 0.3f * globalDegree * wiggleDegree, false, 0, 0, swing, newf1);
        flap(neck1a, globalSpeed, 0.7f * globalDegree * wiggleDegree, true, -0.5f, 0, swing, newf1);
        flap(head_main, globalSpeed, 0.35f * globalDegree * wiggleDegree, false, -1f, 0, swing, newf1);
        swing(head_main, globalSpeed, 0.2f * globalDegree * wiggleDegree, true, 0, 0, swing, newf1);
        head_main.rotationPointZ -= 1 * Math.cos(2 * swing * globalSpeed - 0.5f) * newf1 * globalDegree;

        walk(body_main, 2 * globalSpeed, 0.1f * globalHeight, true, -1.5f, 0.1f, swing, speed);
        walk(neck1a, 2 * globalSpeed, 0.1f * globalHeight, false, -1.5f, -0.1f, swing, speed);
        walk(jaw_lower_main, 2 * globalSpeed, 0.15f * globalHeight, false, -0.7f, -0.1f, swing, speed);
        walk(jaw_lower_left1a, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, swing, speed);
        walk(jaw_lower_right1a, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, swing, speed);
        bob(body_base, 2 * globalSpeed, 0.5f * globalHeight, false, swing, speed);

        float legOffset = -0.4f;
        flap(leg_left1aJoint, 1 * globalSpeed, 0.6f * globalDegree, false, 0- 0.8f + legOffset, 0.6f, swing, newf12);
        walk(leg_left1a, 1 * globalSpeed, 0.2f * globalDegree, true, 0- 2f + legOffset, 0.5f, swing, newf12);
        walk(leg_left1b, 1 * globalSpeed, 0.3f * globalDegree, true, -1.5f +0.2f + legOffset, -0.6f, swing, newf12);
        flap(leg_left1b, 1 * globalSpeed, 0.5f * globalDegree, true, -1.5f+0.7f + legOffset, 0f, swing, newf12);

        flap(leg_right1aJoint, 1 * globalSpeed, 0.6f * globalDegree, false, 0- 0.8f + legOffset, -0.6f, swing, newf12);
        walk(leg_right1a, 1 * globalSpeed, 0.2f * globalDegree, false, 0- 2f + legOffset, 0.5f, swing, newf12);
        walk(leg_right1b, 1 * globalSpeed, 0.3f * globalDegree, false, -1.5f +0.2f + legOffset, -0.6f, swing, newf12);
        flap(leg_right1b, 1 * globalSpeed, 0.5f * globalDegree, true, -1.5f+0.7f + legOffset, 0f, swing, newf12);

        walk(arm_leftaJoint, 1 * globalSpeed, 0.5f * globalDegree, true, -1.6f - 0.4f, -0.3f, swing, newf12);
        walk(arm_leftb, 1 * globalSpeed, 0.5f * globalDegree, true, -0.1f - 0.4f, -0.2f, swing, newf12);
        swing(arm_leftb, 1 * globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, -0f, swing, newf12);

        walk(arm_rightaJoint, 1 * globalSpeed, 0.5f * globalDegree, false, -1.6f - 0.4f, -0.3f, swing, newf12);
        walk(arm_rightb, 1 * globalSpeed, 0.5f * globalDegree, false, -0.1f - 0.4f, -0.2f, swing, newf12);
        swing(arm_rightb, 1 * globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, 0f, swing, newf12);

        float midLegOffset = 3.1415926f - 3f;
        float midLegDegree = 0.5f;
        walk(midarm_leftaJoint, 1 * globalSpeed, 0.7f * globalDegree * midLegDegree, true, -1.6f - 0.4f + midLegOffset, 0.1f, swing, newf12);
        swing(midarm_leftaJoint, 1 * globalSpeed, 0.2f * globalDegree * midLegDegree, false, -1.6f + 2.4f + midLegOffset, 0.3f, swing, newf12);
        flap(midarm_leftaJoint, 1 * globalSpeed, 0.3f * globalDegree * midLegDegree, false, -1.6f + 0.4f + midLegOffset, 0.5f, swing, newf12);
        walk(midarm_leftb, 1 * globalSpeed, 0.5f * globalDegree * midLegDegree, true, -0.1f - 0.4f + midLegOffset, 0.2f, swing, newf12);
        swing(midarm_leftb, 1 * globalSpeed, 0.3f * globalDegree * midLegDegree, false, -0.1f - 0.4f + midLegOffset, -0f, swing, newf12);

        walk(midarm_rightaJoint, 1 * globalSpeed, 0.7f * globalDegree * midLegDegree, false, -1.6f - 0.4f + midLegOffset, 0.1f, swing, newf12);
        swing(midarm_rightaJoint, 1 * globalSpeed, 0.2f * globalDegree * midLegDegree, false, -1.6f + 2.4f + midLegOffset, -0.3f, swing, newf12);
        flap(midarm_rightaJoint, 1 * globalSpeed, 0.3f * globalDegree * midLegDegree, false, -1.6f + 0.4f + midLegOffset, -0.5f, swing, newf12);
        walk(midarm_rightb, 1 * globalSpeed, 0.5f * globalDegree * midLegDegree, false, -0.1f - 0.4f + midLegOffset, 0.2f, swing, newf12);
        swing(midarm_rightb, 1 * globalSpeed, 0.3f * globalDegree * midLegDegree, false, -0.1f - 0.4f + midLegOffset, -0f, swing, newf12);

        Vec3d eyeRot = stalker.prevEyeRotation.add(stalker.eyeRotation.subtract(stalker.prevEyeRotation).scale(partialRenderTicks));
        eyeRot = eyeRot.scale(0.5);
        bigeye.rotateAngleX += eyeRot.x * 0.5;
        bigeye.rotateAngleY += eyeRot.y;
        bigeye.rotateAngleZ += eyeRot.z;

        walk(head_main, 4, 0.01f, false, 0, 0, frame, 1);
        swing(head_main, 3, 0.01f, false, 0, 0, frame, 1);
        flap(head_main, 5, 0.01f, false, 0, 0, frame, 1);

        walk(jaw_lower_main, 0.4f, 0.2f, false, 0, 0, frame, 1);
        walk(jaw_lower_right1a, 0.4f, 0.1f, false, -0.35f, 0, frame, 1);
        walk(jaw_lower_left1a, 0.4f, 0.1f, false, -0.35f, 0, frame, 1);
        swing(jaw_lower_right1a, 0.4f, 0.1f, true, -0.7f, 0, frame, 1);
        swing(jaw_lower_left1a, 0.4f, 0.1f, false, -0.7f, 0, frame, 1);
        
        float screechTicks = stalker.prevScreechingTicks + (stalker.screechingTicks - stalker.prevScreechingTicks) * partialRenderTicks;
        
        float upright = easeInOutBack(Math.min(screechTicks / 40.0f, 1));
        body_main.rotateAngleX -= upright * 0.5f;
        head_main.rotateAngleX -= upright;

        float screeching = easeInOutBack(MathHelper.clamp((screechTicks - 20) / 30.0f, 0, 1));
        float screechingHeadRotationStrength = MathHelper.sin(frame * 0.4f) * screeching;
        head_main.rotateAngleY += screechingHeadRotationStrength * 0.15f;
        head_main.rotateAngleZ += screechingHeadRotationStrength * 0.15f;
    }
    
    private static float easeInOutBack(float x) {
    	float c1 = 1.70158f;
    	float c2 = c1 * 1.525f;

    	return x < 0.5
    	  ? ((float)Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
    	  : ((float)Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    	}
}
