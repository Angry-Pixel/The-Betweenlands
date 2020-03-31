package thebetweenlands.client.render.model.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelGreeblingVolarpadFloater extends MowzieModelBase {
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

    public ModelGreeblingVolarpadFloater() {
        textureWidth = 128;
        textureHeight = 64;
        arm_left_upper = new MowzieModelRenderer(this, 96, 0);
        arm_left_upper.setRotationPoint(2.5F, -2.7F, 0.5F);
        arm_left_upper.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arm_left_upper, -0.3565707661824416F, 0.045553093477052F, -0.36425021489121656F);
        jaw_main = new MowzieModelRenderer(this, 71, 25);
        jaw_main.setRotationPoint(0.0F, 0.0F, -1.0F);
        jaw_main.addBox(-1.5F, -0.0F, -2.0F, 3, 1, 2, 0.0F);
        setRotateAngle(jaw_main, 0.5009094953223726F, 0.0F, 0.0F);
        head_main = new MowzieModelRenderer(this, 71, 17);
        head_main.setRotationPoint(0.0F, -3.8F, -0.5F);
        head_main.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        setRotateAngle(head_main, -0.5918411493512771F, 0.0F, 0.0F);
        arm_left_lower = new MowzieModelRenderer(this, 101, 0);
        arm_left_lower.setRotationPoint(0.0F, 3.5F, 0.0F);
        arm_left_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arm_left_lower, -0.6829473363053812F, 0.0F, 0.0F);
        body = new MowzieModelRenderer(this, 71, 9);
        body.setRotationPoint(0.0F, 19.5F, 1.3F);
        body.addBox(-2.0F, -4.0F, -1.5F, 4, 4, 3, 0.0F);
        setRotateAngle(body, 0.0F, 0.0F, 0.33161255787892263F);
        chest = new MowzieModelRenderer(this, 71, 0);
        chest.setRotationPoint(0.0F, -3.4F, 0.0F);
        chest.addBox(-2.5F, -3.5F, -2.0F, 5, 4, 4, 0.0F);
        setRotateAngle(chest, 0.5462880558742251F, 0.0F, 0.0F);
        legleft1 = new MowzieModelRenderer(this, 116, 0);
        legleft1.setRotationPoint(1.7F, -0.4F, 0.0F);
        legleft1.addBox(-0.4F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(legleft1, -0.5462880558742251F, -0.06300638599699529F, -0.40980330836826856F);
        nose = new MowzieModelRenderer(this, 82, 25);
        nose.setRotationPoint(0.0F, 0.0F, -3.0F);
        nose.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        setRotateAngle(nose, -0.22759093446006054F, 0.0F, 0.0F);
        legright1 = new MowzieModelRenderer(this, 116, 5);
        legright1.setRotationPoint(-1.7F, -0.4F, 0.0F);
        legright1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(legright1, -0.1623156204354726F, -0.02426007660272119F, 0.2527236756887789F);
        leg_right_lower = new MowzieModelRenderer(this, 121, 5);
        leg_right_lower.setRotationPoint(0.0F, 2.5F, 0.0F);
        leg_right_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(leg_right_lower, 0.42114794850623166F, 0.0F, 0.0F);
        jaw_back = new MowzieModelRenderer(this, 88, 21);
        jaw_back.setRotationPoint(0.0F, 0.0F, 0.0F);
        jaw_back.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        ear_left = new MowzieModelRenderer(this, 87, 25);
        ear_left.setRotationPoint(2.0F, -1.5F, -0.5F);
        ear_left.addBox(-1.0F, -2.0F, 0.0F, 5, 3, 0, 0.0F);
        setRotateAngle(ear_left, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        ear_right = new MowzieModelRenderer(this, 87, 29);
        ear_right.setRotationPoint(-2.0F, -1.5F, -0.5F);
        ear_right.addBox(-4.0F, -2.0F, 0.0F, 5, 3, 0, 0.0F);
        setRotateAngle(ear_right, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        leg_left_lower = new MowzieModelRenderer(this, 121, 0);
        leg_left_lower.setRotationPoint(0.0F, 2.5F, 0.0F);
        leg_left_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(leg_left_lower, 0.6829473363053812F, -0.045553093477052F, 0.045553093477052F);
        arm_right_upper = new MowzieModelRenderer(this, 106, 0);
        arm_right_upper.setRotationPoint(-2.5F, -2.7F, 0.5F);
        arm_right_upper.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arm_right_upper, 2.882760325519034F, -0.2937389131106456F, -0.6033603224144397F);
        arm_right_lower = new MowzieModelRenderer(this, 111, 0);
        arm_right_lower.setRotationPoint(0.0F, 3.5F, 0.0F);
        arm_right_lower.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        setRotateAngle(arm_right_lower, -0.2284635990860578F, 0.045553093477052F, -0.8134979643545569F);
        chest.addChild(arm_left_upper);
        head_main.addChild(jaw_main);
        chest.addChild(head_main);
        arm_left_upper.addChild(arm_left_lower);
        body.addChild(chest);
        body.addChild(legleft1);
        head_main.addChild(nose);
        body.addChild(legright1);
        legright1.addChild(leg_right_lower);
        head_main.addChild(jaw_back);
        head_main.addChild(ear_left);
        head_main.addChild(ear_right);
        legleft1.addChild(leg_left_lower);
        chest.addChild(arm_right_upper);
        arm_right_upper.addChild(arm_right_lower);
    }

    public void render() {
    	body.render(0.0625F);
    }

    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
