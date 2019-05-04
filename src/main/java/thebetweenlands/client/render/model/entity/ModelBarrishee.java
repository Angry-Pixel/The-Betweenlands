package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
@SideOnly(Side.CLIENT)
public class ModelBarrishee extends ModelBase {
    ModelRenderer base_rotation_bit;
    ModelRenderer chest_right;
    ModelRenderer chest_left;
    ModelRenderer neck;
    ModelRenderer belly_1;
    ModelRenderer shoulder_right;
    ModelRenderer arm_right_1;
    ModelRenderer arm_right_2;
    ModelRenderer hand_right_1;
    ModelRenderer finger_right_mid;
    ModelRenderer finger_right_outer;
    ModelRenderer finger_right_inner;
    ModelRenderer finger_right_thumb;
    ModelRenderer finger_right_mid_1;
    ModelRenderer finger_right_mid_2;
    ModelRenderer finger_right_outer_1;
    ModelRenderer finger_right_outer_2;
    ModelRenderer finger_right_inner_1;
    ModelRenderer finger_right_inner_2;
    ModelRenderer finger_right_thumb_1;
    ModelRenderer finger_right_thumb_2;
    ModelRenderer shoulder_left;
    ModelRenderer arm_left_1;
    ModelRenderer arm_left_2;
    ModelRenderer hand_left_1;
    ModelRenderer finger_left_mid;
    ModelRenderer finger_left_outer;
    ModelRenderer finger_left_inner;
    ModelRenderer finger_left_thumb;
    ModelRenderer finger_left_mid_1;
    ModelRenderer finger_left_mid_2;
    ModelRenderer finger_left_outer_1;
    ModelRenderer finger_left_outer_2;
    ModelRenderer finger_left_inner_1;
    ModelRenderer finger_left_inner_2;
    ModelRenderer finger_left_thumb_1;
    ModelRenderer finger_left_thumb_2;
    ModelRenderer head_main;
    ModelRenderer jaw_back;
    ModelRenderer teeth_top;
    ModelRenderer jaw1;
    ModelRenderer teeth_bottom;
    ModelRenderer belly_2;
    ModelRenderer axle;
    ModelRenderer greeble_spike_upper;
    ModelRenderer back_leg_1;
    ModelRenderer greeble_spike_lower;
    ModelRenderer greeble_back;
    ModelRenderer gear_1;
    ModelRenderer gear_2;
    ModelRenderer gear_3;
    ModelRenderer gear_4;
    ModelRenderer greeble_spike_upper_end;
    ModelRenderer back_leg_2;
    ModelRenderer back_leg_3;
    ModelRenderer ski_mid;
    ModelRenderer ski_outer;
    ModelRenderer ski_inner;
    ModelRenderer greeble_spike_lower_end;

    public ModelBarrishee() {
        textureWidth = 128;
        textureHeight = 256;
        finger_right_outer = new ModelRenderer(this, 34, 0);
        finger_right_outer.setRotationPoint(3.0F, 0.0F, -6.5F);
        finger_right_outer.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_outer, -0.17453292519943295F, -0.3490658503988659F, 0.0F);
        back_leg_2 = new ModelRenderer(this, 72, 135);
        back_leg_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        back_leg_2.addBox(-10.0F, -4.0F, -3.0F, 6, 8, 7, 0.0F);
        back_leg_1 = new ModelRenderer(this, 41, 98);
        back_leg_1.setRotationPoint(-6.5F, 0.4F, 5.0F);
        back_leg_1.addBox(-4.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(back_leg_1, 1.3962634015954636F, 0.5235987755982988F, 0.0F);
        head_main = new ModelRenderer(this, 0, 65);
        head_main.setRotationPoint(0.0F, -4.0F, -6.0F);
        head_main.addBox(-7.0F, -8.0F, -14.0F, 14, 10, 16, 0.0F);
        setRotateAngle(head_main, 0.0F, 0.0F, 0.0F);
        gear_2 = new ModelRenderer(this, 93, 107);
        gear_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_2.addBox(1.52F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_2, 0.7853981633974483F, 0.0F, -0.08726646259971647F);
        back_leg_3 = new ModelRenderer(this, 79, 107);
        back_leg_3.setRotationPoint(-8.0F, 0.7F, 0.4F);
        back_leg_3.addBox(-2.9F, -1.5F, -2.5F, 6, 7, 5, 0.0F);
        setRotateAngle(back_leg_3, 0.0F, 0.0F, 0.5235987755982988F);
        finger_left_outer_2 = new ModelRenderer(this, 32, 60);
        finger_left_outer_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_outer_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_outer_2, -0.5235987755982988F, 0.0F, 0.0F);
        gear_1 = new ModelRenderer(this, 59, 107);
        gear_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_1.addBox(1.53F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_1, 0.39269908169872414F, 0.0F, -0.08726646259971647F);
        finger_left_outer_1 = new ModelRenderer(this, 110, 46);
        finger_left_outer_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_outer_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_outer_1, 0.7853981633974483F, 0.0F, 0.0F);
        arm_right_2 = new ModelRenderer(this, 72, 33);
        arm_right_2.setRotationPoint(-8.0F, 0.0F, 0.0F);
        arm_right_2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
        setRotateAngle(arm_right_2, 0.0F, 0.2617993877991494F, 0.3490658503988659F);
        finger_right_thumb_2 = new ModelRenderer(this, 63, 26);
        finger_right_thumb_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_thumb_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_thumb_2, -0.5235987755982988F, 0.0F, 0.0F);
        belly_2 = new ModelRenderer(this, 0, 105);
        belly_2.setRotationPoint(0.0F, 0.0F, 6.0F);
        belly_2.addBox(-7.5F, -5.0F, -1.5F, 15, 10, 11, 0.0F);
        setRotateAngle(belly_2, 0.5235987755982988F, 0.0F, -0.08726646259971647F);
        chest_right = new ModelRenderer(this, 0, 0);
        chest_right.setRotationPoint(-4.0F, 0.0F, 0.0F);
        chest_right.addBox(-5.0F, -6.0F, -3.5F, 10, 12, 14, 0.0F);
        setRotateAngle(chest_right, -0.3490658503988659F, 0.06981317007977318F, 0.17453292519943295F);
        jaw1 = new ModelRenderer(this, 70, 77);
        jaw1.setRotationPoint(0.0F, 6.0F, -3.0F);
        jaw1.addBox(-5.0F, 0.0F, -8.0F, 10, 4, 12, 0.0F);
        setRotateAngle(jaw1, 0.7853981633974483F, 0.0F, 0.0F);
        jaw_back = new ModelRenderer(this, 78, 65);
        jaw_back.setRotationPoint(0.0F, 1.0F, -2.0F);
        jaw_back.addBox(-6.0F, 0.0F, -3.0F, 12, 6, 6, 0.0F);
        arm_right_1 = new ModelRenderer(this, 96, 0);
        arm_right_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_right_1.addBox(-10.0F, -4.0F, -4.0F, 6, 8, 8, 0.0F);
        arm_left_2 = new ModelRenderer(this, 60, 51);
        arm_left_2.setRotationPoint(8.0F, 0.0F, 0.0F);
        arm_left_2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
        setRotateAngle(arm_left_2, 0.0F, -0.2617993877991494F, -0.3490658503988659F);
        finger_left_thumb_1 = new ModelRenderer(this, 98, 60);
        finger_left_thumb_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_thumb_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_thumb_1, 0.7853981633974483F, 0.0F, 0.0F);
        ski_mid = new ModelRenderer(this, 11, 149);
        ski_mid.mirror = true;
        ski_mid.setRotationPoint(0.0F, 5.5F, -2.5F);
        ski_mid.addBox(-2.5F, -2.5F, -16.5F, 5, 3, 23, 0.0F);
        setRotateAngle(ski_mid, -1.3089969389957472F, 0.08726646259971647F, 0.0F);
        greeble_back = new ModelRenderer(this, 100, 93);
        greeble_back.setRotationPoint(0.0F, -5.0F, 7.0F);
        greeble_back.addBox(-3.0F, 1.0F, -1.7F, 6, 10, 4, 0.0F);
        setRotateAngle(greeble_back, 0.36425021489121656F, 0.0F, 0.0F);
        finger_left_thumb = new ModelRenderer(this, 110, 39);
        finger_left_thumb.setRotationPoint(-3.0F, 0.0F, -1.5F);
        finger_left_thumb.addBox(-1.5F, -1.5F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(finger_left_thumb, -0.17453292519943295F, 1.0471975511965976F, 0.0F);
        chest_left = new ModelRenderer(this, 48, 0);
        chest_left.setRotationPoint(4.0F, 0.0F, 0.0F);
        chest_left.addBox(-5.0F, -6.0F, -3.5F, 10, 12, 14, 0.0F);
        setRotateAngle(chest_left, -0.3490658503988659F, -0.06981317007977318F, -0.17453292519943295F);
        finger_right_thumb_1 = new ModelRenderer(this, 31, 26);
        finger_right_thumb_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_thumb_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_thumb_1, 0.7853981633974483F, 0.0F, 0.0F);
        finger_right_inner_2 = new ModelRenderer(this, 112, 21);
        finger_right_inner_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_inner_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_inner_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_right_mid_2 = new ModelRenderer(this, 34, 6);
        finger_right_mid_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_mid_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_mid_2, -0.5235987755982988F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 88, 18);
        neck.setRotationPoint(0.0F, -1.0F, -1.0F);
        neck.addBox(-4.0F, -4.0F, -6.5F, 8, 7, 8, 0.0F);
        setRotateAngle(neck, 0.0F, 0.0F, 0.0F);
        hand_left_1 = new ModelRenderer(this, 0, 54);
        hand_left_1.setRotationPoint(0.0F, 12.0F, 0.0F);
        hand_left_1.addBox(-4.0F, -1.5F, -8.0F, 8, 3, 8, 0.0F);
        setRotateAngle(hand_left_1, 0.4363323129985824F, 0.0F, 0.05235987755982988F);
        ski_outer = new ModelRenderer(this, 77, 135);
        ski_outer.mirror = true;
        ski_outer.setRotationPoint(0.0F, 0.0F, 0.0F);
        ski_outer.addBox(-5.5F, -1.5F, -15.8F, 3, 2, 21, 0.0F);
        setRotateAngle(ski_outer, 0.03490658503988659F, 0.03490658503988659F, 0.03490658503988659F);
        finger_right_outer_2 = new ModelRenderer(this, 82, 7);
        finger_right_outer_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_right_outer_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_right_outer_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_left_mid_1 = new ModelRenderer(this, 24, 43);
        finger_left_mid_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_mid_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_mid_1, 0.7853981633974483F, 0.0F, 0.0F);
        greeble_spike_upper_end = new ModelRenderer(this, 108, 65);
        greeble_spike_upper_end.setRotationPoint(0.0F, 0.0F, 0.0F);
        greeble_spike_upper_end.addBox(-1.5F, -7.0F, -1.5F, 3, 2, 4, 0.0F);
        finger_left_inner_2 = new ModelRenderer(this, 84, 60);
        finger_left_inner_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_inner_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_inner_2, -0.5235987755982988F, 0.0F, 0.0F);
        gear_3 = new ModelRenderer(this, 38, 121);
        gear_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_3.addBox(1.51F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_3, 1.1780972450961724F, 0.0F, -0.08726646259971647F);
        belly_1 = new ModelRenderer(this, 0, 26);
        belly_1.setRotationPoint(0.0F, 2.5F, 8.5F);
        belly_1.addBox(-5.5F, -4.0F, -0.5F, 11, 8, 9, 0.0F);
        setRotateAngle(belly_1, -0.6829473363053812F, 0.0F, -0.08726646259971647F);
        arm_left_1 = new ModelRenderer(this, 32, 44);
        arm_left_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        arm_left_1.addBox(4.0F, -4.0F, -4.0F, 6, 8, 8, 0.0F);
        gear_4 = new ModelRenderer(this, 0, 126);
        gear_4.setRotationPoint(0.0F, 0.0F, 0.0F);
        gear_4.addBox(1.5F, -7.0F, -7.0F, 3, 14, 14, 0.0F);
        setRotateAngle(gear_4, 0.0F, 0.0F, -0.08726646259971647F);
        finger_left_mid_2 = new ModelRenderer(this, 52, 44);
        finger_left_mid_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_mid_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_mid_2, -0.5235987755982988F, 0.0F, 0.0F);
        teeth_bottom = new ModelRenderer(this, 0, 91);
        teeth_bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        teeth_bottom.addBox(-4.5F, -2.0F, -7.5F, 9, 2, 12, 0.0F);
        axle = new ModelRenderer(this, 102, 77);
        axle.setRotationPoint(7.0F, -1.0F, 6.0F);
        axle.addBox(0.0F, -1.5F, -1.5F, 7, 3, 3, 0.0F);
        setRotateAngle(axle, 0.0F, 0.0F, 0.17453292519943295F);
        finger_left_outer = new ModelRenderer(this, 90, 33);
        finger_left_outer.setRotationPoint(3.0F, 0.0F, -6.0F);
        finger_left_outer.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_outer, -0.17453292519943295F, -0.3490658503988659F, 0.0F);
        base_rotation_bit = new ModelRenderer(this, 0, 0);
        base_rotation_bit.setRotationPoint(0.0F, 9.5F, -3.5F);
        base_rotation_bit.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        shoulder_right = new ModelRenderer(this, 40, 26);
        shoulder_right.setRotationPoint(-3.5F, 0.0F, 2.0F);
        shoulder_right.addBox(-4.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(shoulder_right, 0.0F, -0.06981317007977318F, -0.41887902047863906F);
        finger_left_inner = new ModelRenderer(this, 104, 33);
        finger_left_inner.setRotationPoint(-3.0F, 0.0F, -6.5F);
        finger_left_inner.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_inner, -0.17453292519943295F, 0.3490658503988659F, 0.0F);
        greeble_spike_lower_end = new ModelRenderer(this, 68, 79);
        greeble_spike_lower_end.setRotationPoint(0.0F, 0.0F, 0.0F);
        greeble_spike_lower_end.addBox(-1.5F, -6.0F, -1.5F, 3, 2, 4, 0.0F);
        finger_right_thumb = new ModelRenderer(this, 82, 0);
        finger_right_thumb.setRotationPoint(3.0F, 0.0F, -1.5F);
        finger_right_thumb.addBox(-1.5F, -1.5F, -4.0F, 3, 3, 4, 0.0F);
        setRotateAngle(finger_right_thumb, -0.17453292519943295F, -1.0471975511965976F, 0.0F);
        finger_right_inner = new ModelRenderer(this, 48, 0);
        finger_right_inner.setRotationPoint(-3.0F, 0.0F, -6.0F);
        finger_right_inner.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_inner, -0.17453292519943295F, 0.3490658503988659F, 0.0F);
        ski_inner = new ModelRenderer(this, 44, 149);
        ski_inner.mirror = true;
        ski_inner.setRotationPoint(0.0F, 0.0F, 0.0F);
        ski_inner.addBox(2.5F, -1.5F, -17.5F, 3, 2, 19, 0.0F);
        setRotateAngle(ski_inner, 0.03490658503988659F, -0.03490658503988659F, -0.03490658503988659F);
        finger_right_mid = new ModelRenderer(this, 0, 0);
        finger_right_mid.setRotationPoint(0.0F, 0.0F, -7.5F);
        finger_right_mid.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_right_mid, -0.17453292519943295F, 0.0F, 0.0F);
        finger_left_thumb_2 = new ModelRenderer(this, 110, 60);
        finger_left_thumb_2.setRotationPoint(0.0F, 0.0F, -2.5F);
        finger_left_thumb_2.addBox(-2.0F, -1.5F, -2.0F, 4, 2, 3, 0.0F);
        setRotateAngle(finger_left_thumb_2, -0.5235987755982988F, 0.0F, 0.0F);
        finger_left_inner_1 = new ModelRenderer(this, 46, 60);
        finger_left_inner_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_left_inner_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_left_inner_1, 0.7853981633974483F, 0.0F, 0.0F);
        shoulder_left = new ModelRenderer(this, 87, 42);
        shoulder_left.setRotationPoint(3.5F, 0.0F, 2.0F);
        shoulder_left.addBox(-3.0F, -4.5F, -4.5F, 7, 9, 9, 0.0F);
        setRotateAngle(shoulder_left, 0.0F, 0.06981317007977318F, 0.41887902047863906F);
        finger_right_mid_1 = new ModelRenderer(this, 0, 6);
        finger_right_mid_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_mid_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_mid_1, 0.7853981633974483F, 0.0F, 0.0F);
        greeble_spike_lower = new ModelRenderer(this, 0, 73);
        greeble_spike_lower.setRotationPoint(5.8F, -3.5F, 7.3F);
        greeble_spike_lower.addBox(-1.5F, -4.0F, -1.5F, 3, 4, 4, 0.0F);
        setRotateAngle(greeble_spike_lower, -1.1383037381507017F, -0.36425021489121656F, 0.045553093477052F);
        finger_right_inner_1 = new ModelRenderer(this, 112, 16);
        finger_right_inner_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_inner_1.addBox(-1.4F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_inner_1, 0.7853981633974483F, 0.0F, 0.0F);
        teeth_top = new ModelRenderer(this, 44, 69);
        teeth_top.setRotationPoint(0.0F, 0.0F, 0.0F);
        teeth_top.addBox(-5.5F, 1.9F, -13.0F, 11, 2, 8, 0.0F);
        finger_right_outer_1 = new ModelRenderer(this, 48, 6);
        finger_right_outer_1.setRotationPoint(0.0F, 0.0F, -4.0F);
        finger_right_outer_1.addBox(-1.5F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        setRotateAngle(finger_right_outer_1, 0.7853981633974483F, 0.0F, 0.0F);
        finger_left_mid = new ModelRenderer(this, 73, 27);
        finger_left_mid.setRotationPoint(0.0F, 0.0F, -7.5F);
        finger_left_mid.addBox(-1.5F, -1.5F, -4.0F, 3, 2, 4, 0.0F);
        setRotateAngle(finger_left_mid, -0.17453292519943295F, 0.0F, 0.0F);
        greeble_spike_upper = new ModelRenderer(this, 0, 65);
        greeble_spike_upper.setRotationPoint(-5.2F, -3.0F, 4.2F);
        greeble_spike_upper.addBox(-1.5F, -5.0F, -1.5F, 3, 4, 4, 0.0F);
        setRotateAngle(greeble_spike_upper, -0.5462880558742251F, 0.27314402793711257F, -0.36425021489121656F);
        hand_right_1 = new ModelRenderer(this, 0, 43);
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
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
        base_rotation_bit.render(scale);
    }

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {

		EntityBarrishee barrishee = (EntityBarrishee) entity;
		float animation = limbSwing * 0.1F;
		float animation2 = MathHelper.sin((limbSwing) * 0.5F) * 0.4F * limbSwingAngle * 2.5F;

		float standingAngle = barrishee.smoothedAngle(partialRenderTicks);
		float flap = MathHelper.sin((barrishee.ticksExisted) * 0.6F) * 0.8F;

		if ((barrishee.standingAngle > 0)) {
			base_rotation_bit.rotateAngleX = convertDegtoRad(-65) + (convertDegtoRad(65) * standingAngle);
			neck.rotateAngleX = convertDegtoRad(60) - (convertDegtoRad(60) * standingAngle);
			head_main.rotateAngleX = convertDegtoRad(5) - (convertDegtoRad(5) * standingAngle);
			belly_1.rotateAngleX = convertDegtoRad(30) - (convertDegtoRad(70) * standingAngle);
		}

		if (barrishee.isScreaming() && barrishee.getScreamTimer() >= 20 && barrishee.getScreamTimer() <= 30) {
			int fudge = barrishee.getScreamTimer() - 20;
			base_rotation_bit.rotateAngleX = convertDegtoRad(0) - (convertDegtoRad(10) * fudge * 0.1F);
			belly_1.rotateAngleX = convertDegtoRad(-40) + (convertDegtoRad(10) * fudge * 0.1F);
			neck.rotateAngleX = convertDegtoRad(0) - (convertDegtoRad(45) * fudge * 0.1F);
			head_main.rotateAngleX = convertDegtoRad(0) + (convertDegtoRad(25) * fudge * 0.1F);

			jaw_back.rotateAngleX = convertDegtoRad(0) + (convertDegtoRad(10) * fudge * 0.1F);
			jaw1.rotateAngleX = convertDegtoRad(20) + (convertDegtoRad(45) * fudge * 0.1F);

			shoulder_right.rotateAngleX = convertDegtoRad(0) + (convertDegtoRad(30) * fudge * 0.1F);
			shoulder_right.rotateAngleZ = convertDegtoRad(-25) + (convertDegtoRad(-5) * fudge * 0.1F);

			shoulder_left.rotateAngleX = convertDegtoRad(0) + (convertDegtoRad(30) * fudge * 0.1F);
			shoulder_left.rotateAngleZ = convertDegtoRad(25) + (convertDegtoRad(5) * fudge * 0.1F);

			hand_right_1.rotateAngleX = convertDegtoRad(25) - (convertDegtoRad(20) * fudge * 0.1F);
			hand_left_1.rotateAngleX = convertDegtoRad(25) - (convertDegtoRad(20) * fudge * 0.1F);
		}
		
		if (barrishee.isScreaming() && barrishee.getScreamTimer() > 30 && barrishee.getScreamTimer() < 40) {
			base_rotation_bit.rotateAngleX = convertDegtoRad(-10);
			belly_1.rotateAngleX = convertDegtoRad(-30);
			neck.rotateAngleX = convertDegtoRad(-45);

			jaw_back.rotateAngleX = convertDegtoRad(10);
			jaw1.rotateAngleX = convertDegtoRad(65);

			shoulder_right.rotateAngleX = convertDegtoRad(30);
			shoulder_right.rotateAngleZ = convertDegtoRad(-30);

			shoulder_left.rotateAngleX = convertDegtoRad(30);
			shoulder_left.rotateAngleZ = convertDegtoRad(30);

			hand_right_1.rotateAngleX = convertDegtoRad(5);
			hand_left_1.rotateAngleX = convertDegtoRad(5);
			
			head_main.rotateAngleZ = 0F + animation2 * 0.25F + flap * 0.25F;
		}

		if (barrishee.isScreaming() && barrishee.getScreamTimer() >= 40) {
			int fudge = barrishee.getScreamTimer() - 40;
			base_rotation_bit.rotateAngleX = convertDegtoRad(-10) + (convertDegtoRad(10) * fudge * 0.1F);
			belly_1.rotateAngleX = convertDegtoRad(-30) - (convertDegtoRad(10) * fudge * 0.1F);
			neck.rotateAngleX = convertDegtoRad(-45) + (convertDegtoRad(45) * fudge * 0.1F);
			head_main.rotateAngleX = convertDegtoRad(25) - (convertDegtoRad(25) * fudge * 0.1F);

			jaw_back.rotateAngleX = convertDegtoRad(10) - (convertDegtoRad(10) * fudge * 0.1F);
			jaw1.rotateAngleX = convertDegtoRad(65) - (convertDegtoRad(45) * fudge * 0.1F);

			shoulder_right.rotateAngleX = convertDegtoRad(30) - (convertDegtoRad(30) * fudge * 0.1F);
			shoulder_right.rotateAngleZ = convertDegtoRad(-30) + (convertDegtoRad(5) * fudge * 0.1F);

			shoulder_left.rotateAngleX = convertDegtoRad(30) - (convertDegtoRad(30) * fudge * 0.1F);
			shoulder_left.rotateAngleZ = convertDegtoRad(30) - (convertDegtoRad(5) * fudge * 0.1F);

			hand_right_1.rotateAngleX = convertDegtoRad(5) + (convertDegtoRad(20) * fudge * 0.1F);
			hand_left_1.rotateAngleX = convertDegtoRad(5) + (convertDegtoRad(20) * fudge * 0.1F);
		}

		if (!barrishee.isAmbushSpawn() && !barrishee.isScreaming() || barrishee.isAmbushSpawn() && barrishee.standingAngle == 1 && !barrishee.isScreaming()) {
			gear_1.rotateAngleX = 0.39269908169872414F + animation;
			gear_2.rotateAngleX = 0.7853981633974483F + animation;
			gear_3.rotateAngleX = 1.1780972450961724F + animation;
			gear_4.rotateAngleX = 0F + animation;

			// head_main.rotateAngleX = 0F - animation2 * 0.25F;
			head_main.rotateAngleZ = 0F + animation2 * 0.25F;
			jaw1.rotateAngleX = convertDegtoRad(20);

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

	public float convertDegtoRad(int angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
