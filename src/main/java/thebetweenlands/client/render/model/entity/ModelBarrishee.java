package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityBarrishee;

@SideOnly(Side.CLIENT)
public class ModelBarrishee extends MowzieModelBase {
    MowzieModelRenderer base_rotation_bit;
    MowzieModelRenderer chest_right;
    MowzieModelRenderer chest_left;
    MowzieModelRenderer neck;
    MowzieModelRenderer belly_1;
    MowzieModelRenderer shoulder_right;
    MowzieModelRenderer arm_right_1;
    MowzieModelRenderer arm_right_2;
    MowzieModelRenderer hand_right_1;
    MowzieModelRenderer finger_right_mid;
    MowzieModelRenderer finger_right_outer;
    MowzieModelRenderer finger_right_inner;
    MowzieModelRenderer finger_right_thumb;
    MowzieModelRenderer finger_right_mid_1;
    MowzieModelRenderer finger_right_mid_2;
    MowzieModelRenderer finger_right_outer_1;
    MowzieModelRenderer finger_right_outer_2;
    MowzieModelRenderer finger_right_inner_1;
    MowzieModelRenderer finger_right_inner_2;
    MowzieModelRenderer finger_right_thumb_1;
    MowzieModelRenderer finger_right_thumb_2;
    MowzieModelRenderer shoulder_left;
    MowzieModelRenderer arm_left_1;
    MowzieModelRenderer arm_left_2;
    MowzieModelRenderer hand_left_1;
    MowzieModelRenderer finger_left_mid;
    MowzieModelRenderer finger_left_outer;
    MowzieModelRenderer finger_left_inner;
    MowzieModelRenderer finger_left_thumb;
    MowzieModelRenderer finger_left_mid_1;
    MowzieModelRenderer finger_left_mid_2;
    MowzieModelRenderer finger_left_outer_1;
    MowzieModelRenderer finger_left_outer_2;
    MowzieModelRenderer finger_left_inner_1;
    MowzieModelRenderer finger_left_inner_2;
    MowzieModelRenderer finger_left_thumb_1;
    MowzieModelRenderer finger_left_thumb_2;
    MowzieModelRenderer head_main;
    MowzieModelRenderer jaw_back;
    MowzieModelRenderer teeth_top;
    MowzieModelRenderer jaw1;
    MowzieModelRenderer teeth_bottom;
    MowzieModelRenderer belly_2;
    MowzieModelRenderer axle;
    MowzieModelRenderer greeble_spike_upper;
    MowzieModelRenderer back_leg_1;
    MowzieModelRenderer greeble_spike_lower;
    MowzieModelRenderer greeble_back;
    MowzieModelRenderer gear_1;
    MowzieModelRenderer gear_2;
    MowzieModelRenderer gear_3;
    MowzieModelRenderer gear_4;
    MowzieModelRenderer greeble_spike_upper_end;
    MowzieModelRenderer back_leg_2;
    MowzieModelRenderer back_leg_3;
    MowzieModelRenderer ski_mid;
    MowzieModelRenderer ski_outer;
    MowzieModelRenderer ski_inner;
    MowzieModelRenderer greeble_spike_lower_end;

    public ModelBarrishee() {
        textureWidth = 128;
        textureHeight = 256;
        finger_right_outer = new MowzieModelRenderer(this, 34, 0);
        finger_right_outer.setRotationPoint(3.0F, 0.0F, -6.5F);
        finger_right_outer.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_outer, -0.17453292519943295F, -0.3490658503988659F, 0.0F);
        back_leg_2 = new MowzieModelRenderer(this, 72, 135);
        back_leg_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        back_leg_2.addBox(-10.0F, -4.0F, -3.0F, 6, 8, 7, 0.0F);
        back_leg_1 = new MowzieModelRenderer(this, 41, 98);
        back_leg_1.setRotationPoint(-6.5F, 0.4F, 5.0F);
        back_leg_1.addBox(-4.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(back_leg_1, 1.3962634015954636F, 0.5235987755982988F, 0.0F);
        head_main = new MowzieModelRenderer(this, 0, 65);
        head_main.setRotationPoint(0.0F, -4.0F, -6.0F);
        head_main.addBox(-7.0F, -8.0F, -14.0F, 14, 10, 16, 0.0F);
        setRotateAngle(head_main, 0.0F, 0.0F, 0.0F);
        gear_2 = new MowzieModelRenderer(this, 93, 107);
        gear_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_2.addBox(1.52F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_2, 0.7853981633974483F, 0.0F, -0.08726646259971647F);
        back_leg_3 = new MowzieModelRenderer(this, 79, 107);
        back_leg_3.setRotationPoint(-8.0F, 0.7F, 0.4F);
        back_leg_3.addBox(-2.9F, -1.5F, -2.5F, 6, 7, 5, 0.0F);
        setRotateAngle(back_leg_3, 0.0F, 0.0F, 0.5235987755982988F);
        finger_left_outer_2 = new MowzieModelRenderer(this, 32, 60);
        finger_left_outer_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_outer_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_outer_2, -0.5235987755982988F, 0.0F, 0.0F);
        gear_1 = new MowzieModelRenderer(this, 59, 107);
        gear_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_1.addBox(1.53F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_1, 0.39269908169872414F, 0.0F, -0.08726646259971647F);
        finger_left_outer_1 = new MowzieModelRenderer(this, 110, 46);
        finger_left_outer_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_outer_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_outer_1, 0.7853981633974483F, 0.0F, 0.0F);
        arm_right_2 = new MowzieModelRenderer(this, 72, 33);
        arm_right_2.setRotationPoint(-8.0F, 0.0F, 0.0F);
        arm_right_2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
        setRotateAngle(arm_right_2, 0.0F, 0.2617993877991494F, 0.3490658503988659F);
        finger_right_thumb_2 = new MowzieModelRenderer(this, 63, 26);
        finger_right_thumb_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_thumb_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_thumb_2, -0.5235987755982988F, 0.0F, 0.0F);
        belly_2 = new MowzieModelRenderer(this, 0, 105);
        belly_2.setRotationPoint(0.0F, 0.0F, 6.0F);
        belly_2.addBox(-7.5F, -5.0F, -1.5F, 15, 10, 11, 0.0F);
        setRotateAngle(belly_2, 0.5235987755982988F, 0.0F, -0.08726646259971647F);
        chest_right = new MowzieModelRenderer(this, 0, 0);
        chest_right.setRotationPoint(-4.0F, 0.0F, 0.0F);
        chest_right.addBox(-5.0F, -6.0F, -3.5F, 10, 12, 14, 0.0F);
        setRotateAngle(chest_right, -0.3490658503988659F, 0.06981317007977318F, 0.17453292519943295F);
        jaw1 = new MowzieModelRenderer(this, 70, 77);
        jaw1.setRotationPoint(0.0F, 6.0F, -3.0F);
        jaw1.addBox(-5.0F, 0.0F, -8.0F, 10, 4, 12, 0.0F);
        setRotateAngle(jaw1, 0.7853981633974483F, 0.0F, 0.0F);
        jaw_back = new MowzieModelRenderer(this, 78, 65);
        jaw_back.setRotationPoint(0.0F, 1.0F, -2.0F);
        jaw_back.addBox(-6.0F, 0.0F, -3.0F, 12, 6, 6, 0.0F);
        arm_right_1 = new MowzieModelRenderer(this, 96, 0);
        arm_right_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_right_1.addBox(-10.0F, -4.0F, -4.0F, 6, 8, 8, 0.0F);
        arm_left_2 = new MowzieModelRenderer(this, 60, 51);
        arm_left_2.setRotationPoint(8.0F, 0.0F, 0.0F);
        arm_left_2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
        setRotateAngle(arm_left_2, 0.0F, -0.2617993877991494F, -0.3490658503988659F);
        finger_left_thumb_1 = new MowzieModelRenderer(this, 98, 60);
        finger_left_thumb_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_thumb_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_thumb_1, 0.7853981633974483F, 0.0F, 0.0F);
        ski_mid = new MowzieModelRenderer(this, 11, 149);
        ski_mid.mirror = true;
        ski_mid.setRotationPoint(0.0F, 5.5F, -2.5F);
        ski_mid.addBox(-2.5F, -2.5F, -16.5F, 5, 3, 23, 0.0F);
        setRotateAngle(ski_mid, -1.3089969389957472F, 0.08726646259971647F, 0.0F);
        greeble_back = new MowzieModelRenderer(this, 100, 93);
        greeble_back.setRotationPoint(0.0F, -5.0F, 7.0F);
        greeble_back.addBox(-3.0F, 1.0F, -1.7F, 6, 10, 4, 0.0F);
        setRotateAngle(greeble_back, 0.36425021489121656F, 0.0F, 0.0F);
        finger_left_thumb = new MowzieModelRenderer(this, 110, 39);
        finger_left_thumb.setRotationPoint(-3.0F, 0.0F, -1.5F);
        finger_left_thumb.addBox(-1.5F, -1.5F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(finger_left_thumb, -0.17453292519943295F, 1.0471975511965976F, 0.0F);
        chest_left = new MowzieModelRenderer(this, 48, 0);
        chest_left.setRotationPoint(4.0F, 0.0F, 0.0F);
        chest_left.addBox(-5.0F, -6.0F, -3.5F, 10, 12, 14, 0.0F);
        setRotateAngle(chest_left, -0.3490658503988659F, -0.06981317007977318F, -0.17453292519943295F);
        finger_right_thumb_1 = new MowzieModelRenderer(this, 31, 26);
        finger_right_thumb_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_thumb_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_thumb_1, 0.7853981633974483F, 0.0F, 0.0F);
        finger_right_inner_2 = new MowzieModelRenderer(this, 112, 21);
        finger_right_inner_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_inner_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_inner_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_right_mid_2 = new MowzieModelRenderer(this, 34, 6);
        finger_right_mid_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_mid_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_mid_2, -0.5235987755982988F, 0.0F, 0.0F);
        neck = new MowzieModelRenderer(this, 88, 18);
        neck.setRotationPoint(0.0F, -1.0F, -1.0F);
        neck.addBox(-4.0F, -4.0F, -6.5F, 8, 7, 8, 0.0F);
        setRotateAngle(neck, 0.0F, 0.0F, 0.0F);
        hand_left_1 = new MowzieModelRenderer(this, 0, 54);
        hand_left_1.setRotationPoint(0.0F, 12.0F, 0.0F);
        hand_left_1.addBox(-4.0F, -1.5F, -8.0F, 8, 3, 8, 0.0F);
        setRotateAngle(hand_left_1, 0.4363323129985824F, 0.0F, 0.05235987755982988F);
        ski_outer = new MowzieModelRenderer(this, 77, 135);
        ski_outer.mirror = true;
        ski_outer.setRotationPoint(0.0F, 0.0F, 0.0F);
        ski_outer.addBox(-5.5F, -1.5F, -15.8F, 3, 2, 21, 0.0F);
        setRotateAngle(ski_outer, 0.03490658503988659F, 0.03490658503988659F, 0.03490658503988659F);
        finger_right_outer_2 = new MowzieModelRenderer(this, 82, 7);
        finger_right_outer_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_outer_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_outer_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_left_mid_1 = new MowzieModelRenderer(this, 24, 43);
        finger_left_mid_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_mid_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_mid_1, 0.7853981633974483F, 0.0F, 0.0F);
        greeble_spike_upper_end = new MowzieModelRenderer(this, 108, 65);
        greeble_spike_upper_end.setRotationPoint(0.0F, 0.0F, 0.0F);
        greeble_spike_upper_end.addBox(-1.5F, -7.0F, -1.5F, 3, 2, 4, 0.0F);
        finger_left_inner_2 = new MowzieModelRenderer(this, 84, 60);
        finger_left_inner_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_inner_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_inner_2, -0.5235987755982988F, 0.0F, 0.0F);
        gear_3 = new MowzieModelRenderer(this, 38, 121);
        gear_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_3.addBox(1.51F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_3, 1.1780972450961724F, 0.0F, -0.08726646259971647F);
        belly_1 = new MowzieModelRenderer(this, 0, 26);
        belly_1.setRotationPoint(0.0F, 2.5F, 8.5F);
        belly_1.addBox(-5.5F, -4.0F, -0.5F, 11, 8, 9, 0.0F);
        setRotateAngle(belly_1, -0.6829473363053812F, 0.0F, -0.08726646259971647F);
        arm_left_1 = new MowzieModelRenderer(this, 32, 44);
        arm_left_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_left_1.addBox(4.0F, -4.0F, -4.0F, 6, 8, 8, 0.0F);
        gear_4 = new MowzieModelRenderer(this, 0, 126);
        gear_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_4.addBox(1.5F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_4, 0.0F, 0.0F, -0.08726646259971647F);
        finger_left_mid_2 = new MowzieModelRenderer(this, 52, 44);
        finger_left_mid_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_mid_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_mid_2, -0.5235987755982988F, 0.0F, 0.0F);
        teeth_bottom = new MowzieModelRenderer(this, 0, 91);
        teeth_bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        teeth_bottom.addBox(-4.5F, -2.0F, -7.5F, 9, 2, 12, 0.0F);
        axle = new MowzieModelRenderer(this, 102, 77);
        axle.setRotationPoint(7.0F, -1.0F, 6.0F);
        axle.addBox(0.0F, -1.5F, -1.5F, 7, 3, 3, 0.0F);
        setRotateAngle(axle, 0.0F, 0.0F, 0.17453292519943295F);
        finger_left_outer = new MowzieModelRenderer(this, 90, 33);
        finger_left_outer.setRotationPoint(3.0F, 0.0F, -6.0F);
        finger_left_outer.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_outer, -0.17453292519943295F, -0.3490658503988659F, 0.0F);
        base_rotation_bit = new MowzieModelRenderer(this, 0, 0);
        base_rotation_bit.setRotationPoint(0.0F, 9.5F, -3.5F);
        base_rotation_bit.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        shoulder_right = new MowzieModelRenderer(this, 40, 26);
        shoulder_right.setRotationPoint(-3.5F, 0.0F, 2.0F);
        shoulder_right.addBox(-4.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(shoulder_right, 0.0F, -0.06981317007977318F, -0.41887902047863906F);
        finger_left_inner = new MowzieModelRenderer(this, 104, 33);
        finger_left_inner.setRotationPoint(-3.0F, 0.0F, -6.5F);
        finger_left_inner.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_inner, -0.17453292519943295F, 0.3490658503988659F, 0.0F);
        greeble_spike_lower_end = new MowzieModelRenderer(this, 68, 79);
        greeble_spike_lower_end.setRotationPoint(0.0F, 0.0F, 0.0F);
        greeble_spike_lower_end.addBox(-1.5F, -6.0F, -1.5F, 3, 2, 4, 0.0F);
        finger_right_thumb = new MowzieModelRenderer(this, 82, 0);
        finger_right_thumb.setRotationPoint(3.0F, 0.0F, -1.5F);
        finger_right_thumb.addBox(-1.5F, -1.5F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(finger_right_thumb, -0.17453292519943295F, -1.0471975511965976F, 0.0F);
        finger_right_inner = new MowzieModelRenderer(this, 48, 0);
        finger_right_inner.setRotationPoint(-3.0F, 0.0F, -6.0F);
        finger_right_inner.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_inner, -0.17453292519943295F, 0.3490658503988659F, 0.0F);
        ski_inner = new MowzieModelRenderer(this, 44, 149);
        ski_inner.mirror = true;
        ski_inner.setRotationPoint(0.0F, 0.0F, 0.0F);
        ski_inner.addBox(2.5F, -1.5F, -17.5F, 3, 2, 19, 0.0F);
        setRotateAngle(ski_inner, 0.03490658503988659F, -0.03490658503988659F, -0.03490658503988659F);
        finger_right_mid = new MowzieModelRenderer(this, 0, 0);
        finger_right_mid.setRotationPoint(0.0F, 0.0F, -7.5F);
        finger_right_mid.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_mid, -0.17453292519943295F, 0.0F, 0.0F);
        finger_left_thumb_2 = new MowzieModelRenderer(this, 110, 60);
        finger_left_thumb_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_thumb_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_thumb_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_left_inner_1 = new MowzieModelRenderer(this, 46, 60);
        finger_left_inner_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_inner_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_inner_1, 0.7853981633974483F, 0.0F, 0.0F);
        shoulder_left = new MowzieModelRenderer(this, 87, 42);
        shoulder_left.setRotationPoint(3.5F, 0.0F, 2.0F);
        shoulder_left.addBox(-3.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(shoulder_left, 0.0F, 0.06981317007977318F, 0.41887902047863906F);
        finger_right_mid_1 = new MowzieModelRenderer(this, 0, 6);
        finger_right_mid_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_mid_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_mid_1, 0.7853981633974483F, 0.0F, 0.0F);
        greeble_spike_lower = new MowzieModelRenderer(this, 0, 73);
        greeble_spike_lower.setRotationPoint(5.8F, -3.5F, 7.3F);
        greeble_spike_lower.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 4, 0.0F);
        setRotateAngle(greeble_spike_lower, -1.1383037381507017F, -0.36425021489121656F, 0.045553093477052F);
        finger_right_inner_1 = new MowzieModelRenderer(this, 112, 16);
        finger_right_inner_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_inner_1.addBox(-1.4F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_inner_1, 0.7853981633974483F, 0.0F, 0.0F);
        teeth_top = new MowzieModelRenderer(this, 44, 69);
        teeth_top.setRotationPoint(0.0F, 0.0F, 0.0F);
        teeth_top.addBox(-5.5F, 1.9F, -13.0F, 11, 2, 8, 0.0F);
        finger_right_outer_1 = new MowzieModelRenderer(this, 48, 6);
        finger_right_outer_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_outer_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_outer_1, 0.7853981633974483F, 0.0F, 0.0F);
        finger_left_mid = new MowzieModelRenderer(this, 73, 27);
        finger_left_mid.setRotationPoint(0.0F, 0.0F, -7.5F);
        finger_left_mid.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_mid, -0.17453292519943295F, 0.0F, 0.0F);
        greeble_spike_upper = new MowzieModelRenderer(this, 0, 65);
        greeble_spike_upper.setRotationPoint(-5.2F, -3.0F, 4.2F);
        greeble_spike_upper.addBox(-1.5F, -5.0F, -1.5F, 3, 4, 4, 0.0F);
        setRotateAngle(greeble_spike_upper, -0.5462880558742251F, 0.27314402793711257F, -0.36425021489121656F);
        hand_right_1 = new MowzieModelRenderer(this, 0, 43);
        hand_right_1.setRotationPoint(0.0F, 12.0F, 0.0F);
        hand_right_1.addBox(-4.0F, -1.5F, -8.0F, 8, 3, 8, 0.0F);
        setRotateAngle(hand_right_1, 0.4363323129985824F, 0.0F, -0.05235987755982988F);
        hand_right_1.addChild(finger_right_outer);
        back_leg_1.addChild(back_leg_2);
        belly_2.addChild(back_leg_1);
        neck.addChild(head_main);
        axle.addChild(gear_2);
        back_leg_2.addChild(back_leg_3);
        finger_left_outer_1.addChild(finger_left_outer_2);
        axle.addChild(gear_1);
        finger_left_outer.addChild(finger_left_outer_1);
        arm_right_1.addChild(arm_right_2);
        finger_right_thumb_1.addChild(finger_right_thumb_2);
        belly_1.addChild(belly_2);
        base_rotation_bit.addChild(chest_right);
        jaw_back.addChild(jaw1);
        head_main.addChild(jaw_back);
        shoulder_right.addChild(arm_right_1);
        arm_left_1.addChild(arm_left_2);
        finger_left_thumb.addChild(finger_left_thumb_1);
        back_leg_3.addChild(ski_mid);
        belly_2.addChild(greeble_back);
        hand_left_1.addChild(finger_left_thumb);
        base_rotation_bit.addChild(chest_left);
        finger_right_thumb.addChild(finger_right_thumb_1);
        finger_right_inner_1.addChild(finger_right_inner_2);
        finger_right_mid_1.addChild(finger_right_mid_2);
        base_rotation_bit.addChild(neck);
        arm_left_2.addChild(hand_left_1);
        ski_mid.addChild(ski_outer);
        finger_right_outer_1.addChild(finger_right_outer_2);
        finger_left_mid.addChild(finger_left_mid_1);
        greeble_spike_upper.addChild(greeble_spike_upper_end);
        finger_left_inner_1.addChild(finger_left_inner_2);
        axle.addChild(gear_3);
        base_rotation_bit.addChild(belly_1);
        shoulder_left.addChild(arm_left_1);
        axle.addChild(gear_4);
        finger_left_mid_1.addChild(finger_left_mid_2);
        jaw1.addChild(teeth_bottom);
        belly_2.addChild(axle);
        hand_left_1.addChild(finger_left_outer);
        chest_right.addChild(shoulder_right);
        hand_left_1.addChild(finger_left_inner);
        greeble_spike_lower.addChild(greeble_spike_lower_end);
        hand_right_1.addChild(finger_right_thumb);
        hand_right_1.addChild(finger_right_inner);
        ski_mid.addChild(ski_inner);
        hand_right_1.addChild(finger_right_mid);
        finger_left_thumb_1.addChild(finger_left_thumb_2);
        finger_left_inner.addChild(finger_left_inner_1);
        chest_left.addChild(shoulder_left);
        finger_right_mid.addChild(finger_right_mid_1);
        belly_2.addChild(greeble_spike_lower);
        finger_right_inner.addChild(finger_right_inner_1);
        head_main.addChild(teeth_top);
        finger_right_outer.addChild(finger_right_outer_1);
        hand_left_1.addChild(finger_left_mid);
        belly_2.addChild(greeble_spike_upper);
        arm_right_2.addChild(hand_right_1);
        
		parts = new MowzieModelRenderer[] { base_rotation_bit, chest_right, chest_left, neck, belly_1, shoulder_right,
				arm_right_1, arm_right_2, hand_right_1, finger_right_mid, finger_right_outer, finger_right_inner,
				finger_right_thumb, finger_right_mid_1, finger_right_mid_2, finger_right_outer_1, finger_right_outer_2,
				finger_right_inner_1, finger_right_inner_2, finger_right_thumb_1, finger_right_thumb_2, shoulder_left,
				arm_left_1, arm_left_2, hand_left_1, finger_left_mid, finger_left_outer, finger_left_inner,
				finger_left_thumb, finger_left_mid_1, finger_left_mid_2, finger_left_outer_1, finger_left_outer_2,
				finger_left_inner_1, finger_left_inner_2, finger_left_thumb_1, finger_left_thumb_2, head_main, jaw_back,
				teeth_top, jaw1, teeth_bottom, belly_2, axle, greeble_spike_upper, back_leg_1, greeble_spike_lower,
				greeble_back, greeble_spike_upper_end, back_leg_2, back_leg_3, ski_mid,
				ski_outer, ski_inner, greeble_spike_lower_end };//gear_1, gear_2, gear_3, gear_4, 

        setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
        base_rotation_bit.render(scale);
    }

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {
		EntityBarrishee barrishee = (EntityBarrishee) entity;
		setToInitPose();
		float animation = limbSwing * 0.1F;
		float animation2 = MathHelper.sin((limbSwing) * 0.5F) * 0.4F * limbSwingAngle * 2.5F;

		float standingAngle = barrishee.smoothedAngle(partialRenderTicks);
		float flap = MathHelper.sin((barrishee.ticksExisted) * 0.6F) * 0.8F;
		float flap2 = MathHelper.sin((barrishee.ticksExisted) * 0.3F) * 0.8F;

		if ((barrishee.standingAngle > 0)) {
			base_rotation_bit.rotateAngleX = convertDegtoRad(-65F) + (convertDegtoRad(65F) * standingAngle);
			neck.rotateAngleX = convertDegtoRad(60F) - (convertDegtoRad(60F) * standingAngle);
			head_main.rotateAngleX = convertDegtoRad(5F) - (convertDegtoRad(5F) * standingAngle);
			belly_1.rotateAngleX = convertDegtoRad(30F) - (convertDegtoRad(70F) * standingAngle);
		}

		if (barrishee.isScreaming() && barrishee.getScreamTimer() >= 20 && barrishee.getScreamTimer() <= 30) {
			int fudge = barrishee.getScreamTimer() - 20;
			base_rotation_bit.rotateAngleX = convertDegtoRad(0F) - (convertDegtoRad(10F) * fudge * 0.1F);
			belly_1.rotateAngleX = convertDegtoRad(-40F) + (convertDegtoRad(10F) * fudge * 0.1F);
			neck.rotateAngleX = convertDegtoRad(0F) - (convertDegtoRad(45F) * fudge * 0.1F);
			head_main.rotateAngleX = convertDegtoRad(0F) + (convertDegtoRad(25F) * fudge * 0.1F);

			jaw_back.rotateAngleX = convertDegtoRad(0F) + (convertDegtoRad(10F) * fudge * 0.1F);
			jaw1.rotateAngleX = convertDegtoRad(20F) + (convertDegtoRad(45F) * fudge * 0.1F);

			shoulder_right.rotateAngleX = convertDegtoRad(0F) + (convertDegtoRad(30F) * fudge * 0.1F);
			shoulder_right.rotateAngleZ = convertDegtoRad(-25F) + (convertDegtoRad(-5F) * fudge * 0.1F);

			shoulder_left.rotateAngleX = convertDegtoRad(0F) + (convertDegtoRad(30F) * fudge * 0.1F);
			shoulder_left.rotateAngleZ = convertDegtoRad(25F) + (convertDegtoRad(5F) * fudge * 0.1F);

			hand_right_1.rotateAngleX = convertDegtoRad(25F) - (convertDegtoRad(20F) * fudge * 0.1F);
			hand_left_1.rotateAngleX = convertDegtoRad(25F) - (convertDegtoRad(20F) * fudge * 0.1F);
		}
		
		if (barrishee.isScreaming() && barrishee.getScreamTimer() > 30 && barrishee.getScreamTimer() < 40) {
			base_rotation_bit.rotateAngleX = convertDegtoRad(-10F);
			belly_1.rotateAngleX = convertDegtoRad(-30F);
			neck.rotateAngleX = convertDegtoRad(-45F);

			jaw_back.rotateAngleX = convertDegtoRad(10F);
			jaw1.rotateAngleX = convertDegtoRad(65F);

			shoulder_right.rotateAngleX = convertDegtoRad(30F);
			shoulder_right.rotateAngleZ = convertDegtoRad(-30F);

			shoulder_left.rotateAngleX = convertDegtoRad(30F);
			shoulder_left.rotateAngleZ = convertDegtoRad(30F);

			hand_right_1.rotateAngleX = convertDegtoRad(5F);
			hand_left_1.rotateAngleX = convertDegtoRad(5F);
			
			head_main.rotateAngleZ = 0F + animation2 * 0.25F + flap * 0.25F;
		}

		if (barrishee.isScreaming() && barrishee.getScreamTimer() >= 40) {
			int fudge = barrishee.getScreamTimer() - 40;
			base_rotation_bit.rotateAngleX = convertDegtoRad(-10F) + (convertDegtoRad(10F) * fudge * 0.1F);
			belly_1.rotateAngleX = convertDegtoRad(-30F) - (convertDegtoRad(10F) * fudge * 0.1F);
			neck.rotateAngleX = convertDegtoRad(-45F) + (convertDegtoRad(45F) * fudge * 0.1F);
			head_main.rotateAngleX = convertDegtoRad(25F) - (convertDegtoRad(25F) * fudge * 0.1F);

			jaw_back.rotateAngleX = convertDegtoRad(10F) - (convertDegtoRad(10F) * fudge * 0.1F);
			jaw1.rotateAngleX = convertDegtoRad(65F) - (convertDegtoRad(45F) * fudge * 0.1F);

			shoulder_right.rotateAngleX = convertDegtoRad(30F) - (convertDegtoRad(30F) * fudge * 0.1F);
			shoulder_right.rotateAngleZ = convertDegtoRad(-30F) + (convertDegtoRad(5F) * fudge * 0.1F);

			shoulder_left.rotateAngleX = convertDegtoRad(30F) - (convertDegtoRad(30F) * fudge * 0.1F);
			shoulder_left.rotateAngleZ = convertDegtoRad(30F) - (convertDegtoRad(5F) * fudge * 0.1F);

			hand_right_1.rotateAngleX = convertDegtoRad(5F) + (convertDegtoRad(20F) * fudge * 0.1F);
			hand_left_1.rotateAngleX = convertDegtoRad(5F) + (convertDegtoRad(20F) * fudge * 0.1F);
		}

		if (!barrishee.isAmbushSpawn() && !barrishee.isScreaming() || barrishee.isAmbushSpawn() && barrishee.standingAngle == 1 && !barrishee.isScreaming()) {
			gear_1.rotateAngleX = 0.39269908169872414F + animation;
			gear_2.rotateAngleX = 0.7853981633974483F + animation;
			gear_3.rotateAngleX = 1.1780972450961724F + animation;
			gear_4.rotateAngleX = 0F + animation;

			neck.rotateAngleX = 0F - flap2 * 0.0625F;
			head_main.rotateAngleX = 0F + flap2 * 0.0625F;
			head_main.rotateAngleZ = 0F + animation2 * 0.25F;
			jaw_back.rotateAngleX = 0F + - flap2 * 0.125F;
			jaw1.rotateAngleX = convertDegtoRad(20F) - flap2 * 0.0625F;

			shoulder_left.rotateAngleX = 0F + animation2 * 0.5F;
			shoulder_right.rotateAngleX = 0F - animation2 * 0.5F;
			arm_left_2.rotateAngleX = 0F + animation2;
			arm_right_2.rotateAngleX = 0F - animation2;

			hand_left_1.rotateAngleX = 0.4363323129985824F - shoulder_left.rotateAngleX - arm_left_2.rotateAngleX - animation2 * 0.5F;
			hand_right_1.rotateAngleX = 0.4363323129985824F - shoulder_right.rotateAngleX - arm_right_2.rotateAngleX + animation2 * 0.5F;

			finger_left_inner.rotateAngleX = -0.17453292519943295F + animation2;
			finger_left_inner_1.rotateAngleX = 0.7853981633974483F + animation2;
			finger_left_inner_2.rotateAngleX = -0.5235987755982988F - animation2;

			finger_left_mid.rotateAngleX = -0.17453292519943295F + animation2;
			finger_left_mid_1.rotateAngleX = 0.7853981633974483F + animation2;
			finger_left_mid_2.rotateAngleX = -0.5235987755982988F - animation2;

			finger_left_outer.rotateAngleX = -0.17453292519943295F + animation2;
			finger_left_outer_1.rotateAngleX = 0.7853981633974483F + animation2;
			finger_left_outer_2.rotateAngleX = -0.5235987755982988F - animation2;

			finger_right_inner.rotateAngleX = -0.17453292519943295F - animation2;
			finger_right_inner_1.rotateAngleX = 0.7853981633974483F - animation2;
			finger_right_inner_2.rotateAngleX = -0.5235987755982988F + animation2;

			finger_right_mid.rotateAngleX = -0.17453292519943295F - animation2;
			finger_right_mid_1.rotateAngleX = 0.7853981633974483F - animation2;
			finger_right_mid_2.rotateAngleX = -0.5235987755982988F + animation2;

			finger_right_outer.rotateAngleX = -0.17453292519943295F - animation2;
			finger_right_outer_1.rotateAngleX = 0.7853981633974483F - animation2;
			finger_right_outer_2.rotateAngleX = -0.5235987755982988F + animation2;

			base_rotation_bit.rotateAngleX = 0F - animation2 * 0.125F;
			base_rotation_bit.rotateAngleZ = 0F - animation2 * 0.125F;

			belly_1.rotateAngleX = -0.6829473363053812F + animation2 * 0.125F;
			belly_1.rotateAngleZ = -0.08726646259971647F + animation2 * 0.125F;
		}
	}

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
