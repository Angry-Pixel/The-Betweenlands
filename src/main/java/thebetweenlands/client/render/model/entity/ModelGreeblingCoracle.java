package thebetweenlands.client.render.model.entity;

import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

/**
 * BLGreeblingCoracle - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelGreeblingCoracle extends MowzieModelBase {
    public MowzieModelRenderer body_base;
    public MowzieModelRenderer coracle_base;
    public MowzieModelRenderer chest;
    public MowzieModelRenderer leg_left1a;
    public MowzieModelRenderer leg_right1a;
    public MowzieModelRenderer head_connect;
    public MowzieModelRenderer arm_left1a;
    public MowzieModelRenderer arm_right1a;
    public MowzieModelRenderer head_main;
    public MowzieModelRenderer jaw;
    public MowzieModelRenderer nose;
    public MowzieModelRenderer ear_left;
    public MowzieModelRenderer ear_right;
    public MowzieModelRenderer cloth1a;
    public MowzieModelRenderer cloth1b;
    public MowzieModelRenderer arm_left1b;
    public MowzieModelRenderer paddle_main;
    public MowzieModelRenderer paddle_blade1a;
    public MowzieModelRenderer paddle_knob;
    public MowzieModelRenderer paddle_blade1b;
    public MowzieModelRenderer arm_right1b;
    public MowzieModelRenderer leg_left1b;
    public MowzieModelRenderer leg_right1b;
    public MowzieModelRenderer side_left;
    public MowzieModelRenderer side_front;
    public MowzieModelRenderer side_right;
    public MowzieModelRenderer side_back;
    public MowzieModelRenderer corner_front_left1a;
    public MowzieModelRenderer corner_front_right1a;
    public MowzieModelRenderer corner_back_left1a;
    public MowzieModelRenderer corner_back_right1a;
    public MowzieModelRenderer bench;
    public MowzieModelRenderer gourdbag_base;
    public MowzieModelRenderer netrope1a;
    public MowzieModelRenderer top_left;
    public MowzieModelRenderer side_rope1;
    public MowzieModelRenderer top_front;
    public MowzieModelRenderer top_right;
    public MowzieModelRenderer top_back;
    public MowzieModelRenderer side_rope2;
    public MowzieModelRenderer corner_front_left1b;
    public MowzieModelRenderer rope_fl1;
    public MowzieModelRenderer hook_front;
    public MowzieModelRenderer corner_front_right1b;
    public MowzieModelRenderer rope_fr1;
    public MowzieModelRenderer corner_back_left1b;
    public MowzieModelRenderer rope_br1;
    public MowzieModelRenderer hook_left;
    public MowzieModelRenderer corner_back_right1b;
    public MowzieModelRenderer rope_br1_1;
    public MowzieModelRenderer bench_conn_left;
    public MowzieModelRenderer bench_conn_right;
    public MowzieModelRenderer gourdbag_mid;
    public MowzieModelRenderer gourdbag_top;
    public MowzieModelRenderer netrope1b;
    public MowzieModelRenderer net_ring1;
    public MowzieModelRenderer net_netting1;
    public MowzieModelRenderer net_weightline;
    public MowzieModelRenderer net_netting2;
    public MowzieModelRenderer net_netting3;
    public MowzieModelRenderer net_weight;

    public ModelGreeblingCoracle() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.corner_front_right1b = new MowzieModelRenderer(this, 9, 59);
        this.corner_front_right1b.setRotationPoint(1.5F, -3.0F, 1.5F);
        this.corner_front_right1b.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(corner_front_right1b, 0.27314402793711257F, 0.027401669256310976F, -0.27314402793711257F);
        this.side_front = new MowzieModelRenderer(this, 54, 14);
        this.side_front.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.side_front.addBox(-6.0F, -3.0F, -1.0F, 12, 3, 1, 0.0F);
        this.setRotateAngle(side_front, 0.18203784098300857F, 0.0F, 0.0F);
        this.corner_back_right1a = new MowzieModelRenderer(this, 27, 52);
        this.corner_back_right1a.setRotationPoint(-6.0F, 0.0F, 6.0F);
        this.corner_back_right1a.addBox(-0.5F, -3.0F, -1.5F, 2, 4, 2, 0.0F);
        this.setRotateAngle(corner_back_right1a, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F);
        this.leg_left1a = new MowzieModelRenderer(this, 90, 51);
        this.leg_left1a.setRotationPoint(1.3F, 0.0F, -1.0F);
        this.leg_left1a.addBox(-0.5F, -0.5F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(leg_left1a, -1.1383037381507017F, -0.36425021489121656F, 0.045553093477052F);
        this.net_ring1 = new MowzieModelRenderer(this, 6, 98);
        this.net_ring1.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.net_ring1.addBox(-2.5F, -0.5F, -5.0F, 5, 1, 5, 0.0F);
        this.setRotateAngle(net_ring1, 0.8651597102135892F, 0.0F, 0.0F);
        this.arm_left1a = new MowzieModelRenderer(this, 90, 39);
        this.arm_left1a.setRotationPoint(2.5F, -2.0F, -1.0F);
        this.arm_left1a.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(arm_left1a, -1.4114477660878142F, 0.0F, -0.40980330836826856F);
        this.nose = new MowzieModelRenderer(this, 90, 29);
        this.nose.setRotationPoint(0.0F, 0.0F, -3.5F);
        this.nose.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(nose, -0.22759093446006054F, 0.0F, 0.0F);
        this.paddle_blade1a = new MowzieModelRenderer(this, 118, 19);
        this.paddle_blade1a.setRotationPoint(0.0F, 14.0F, -0.5F);
        this.paddle_blade1a.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.setRotateAngle(paddle_blade1a, 0.091106186954104F, 0.0F, 0.0F);
        this.corner_back_left1b = new MowzieModelRenderer(this, 18, 59);
        this.corner_back_left1b.setRotationPoint(-1.5F, -3.0F, -1.5F);
        this.corner_back_left1b.addBox(0.0F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(corner_back_left1b, -0.27314402793711257F, 0.027401669256310976F, 0.27314402793711257F);
        this.bench_conn_right = new MowzieModelRenderer(this, 29, 78);
        this.bench_conn_right.setRotationPoint(-5.5F, 0.0F, 0.0F);
        this.bench_conn_right.addBox(-2.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
        this.net_netting1 = new MowzieModelRenderer(this, 27, 98);
        this.net_netting1.setRotationPoint(0.0F, 0.5F, -0.5F);
        this.net_netting1.addBox(-2.0F, 0.0F, -4.0F, 4, 5, 4, 0.0F);
        this.setRotateAngle(net_netting1, -0.18203784098300857F, 0.0F, 0.0F);
        this.side_rope2 = new MowzieModelRenderer(this, 54, 32);
        this.side_rope2.setRotationPoint(0.0F, -1.5F, 1.0F);
        this.side_rope2.addBox(-5.0F, 0.0F, 0.0F, 10, 4, 0, 0.0F);
        this.setRotateAngle(side_rope2, 0.4553564018453205F, 0.0F, 0.0F);
        this.side_back = new MowzieModelRenderer(this, 54, 23);
        this.side_back.setRotationPoint(0.0F, 0.0F, 5.0F);
        this.side_back.addBox(-6.0F, -3.0F, 0.0F, 12, 3, 1, 0.0F);
        this.setRotateAngle(side_back, -0.18203784098300857F, 0.0F, 0.0F);
        this.side_left = new MowzieModelRenderer(this, 0, 14);
        this.side_left.setRotationPoint(5.0F, 0.0F, 0.0F);
        this.side_left.addBox(0.0F, -3.0F, -6.0F, 1, 3, 12, 0.0F);
        this.setRotateAngle(side_left, 0.0F, 0.0F, 0.18203784098300857F);
        this.corner_front_left1b = new MowzieModelRenderer(this, 0, 59);
        this.corner_front_left1b.setRotationPoint(-1.5F, -3.0F, 1.5F);
        this.corner_front_left1b.addBox(0.0F, -3.0F, -2.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(corner_front_left1b, 0.27314402793711257F, -0.027401669256310976F, 0.27314402793711257F);
        this.rope_fr1 = new MowzieModelRenderer(this, 13, 65);
        this.rope_fr1.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope_fr1.addBox(-1.0F, -1.0F, -1.0F, 3, 2, 3, 0.0F);
        this.setRotateAngle(rope_fr1, 0.136659280431156F, 0.01361356816555577F, -0.136659280431156F);
        this.corner_front_left1a = new MowzieModelRenderer(this, 0, 52);
        this.corner_front_left1a.setRotationPoint(6.0F, 0.0F, -6.0F);
        this.corner_front_left1a.addBox(-1.5F, -3.0F, -0.5F, 2, 4, 2, 0.0F);
        this.setRotateAngle(corner_front_left1a, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F);
        this.head_connect = new MowzieModelRenderer(this, 90, 17);
        this.head_connect.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.head_connect.addBox(-2.0F, -1.0F, -1.5F, 4, 1, 2, 0.0F);
        this.side_rope1 = new MowzieModelRenderer(this, 0, 37);
        this.side_rope1.setRotationPoint(1.0F, -1.5F, 0.0F);
        this.side_rope1.addBox(0.0F, 0.0F, -5.0F, 0, 4, 10, 0.0F);
        this.setRotateAngle(side_rope1, 0.0F, 0.0F, -0.4553564018453205F);
        this.body_base = new MowzieModelRenderer(this, 90, 0);
        this.body_base.setRotationPoint(-3.0F, 19.2F, 2.0F);
        this.body_base.addBox(-2.0F, -3.5F, -2.0F, 4, 4, 3, 0.0F);
        this.setRotateAngle(body_base, -0.18203784098300857F, 0.0F, -0.091106186954104F);
        this.cloth1b = new MowzieModelRenderer(this, 90, 36);
        this.cloth1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.cloth1b.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F);
        this.setRotateAngle(cloth1b, -0.36425021489121656F, 0.0F, 0.0F);
        this.head_main = new MowzieModelRenderer(this, 90, 21);
        this.head_main.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.head_main.addBox(-2.0F, -3.0F, -3.5F, 4, 3, 4, 0.0F);
        this.netrope1b = new MowzieModelRenderer(this, 3, 98);
        this.netrope1b.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.netrope1b.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        this.setRotateAngle(netrope1b, 0.31869712141416456F, 0.0F, 0.0F);
        this.ear_right = new MowzieModelRenderer(this, 106, 29);
        this.ear_right.setRotationPoint(-1.0F, -2.5F, -1.5F);
        this.ear_right.addBox(-4.0F, -0.5F, 0.0F, 5, 3, 0, 0.0F);
        this.setRotateAngle(ear_right, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        this.leg_right1a = new MowzieModelRenderer(this, 95, 51);
        this.leg_right1a.setRotationPoint(-1.3F, 0.0F, -1.0F);
        this.leg_right1a.addBox(-0.5F, -0.5F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(leg_right1a, -1.2747884856566583F, 0.5462880558742251F, 0.0F);
        this.hook_front = new MowzieModelRenderer(this, 0, 68);
        this.hook_front.setRotationPoint(0.5F, 0.0F, -1.0F);
        this.hook_front.addBox(0.0F, 0.0F, -2.0F, 0, 3, 3, 0.0F);
        this.setRotateAngle(hook_front, -0.27314402793711257F, 0.9560913642424937F, -0.6373942428283291F);
        this.corner_back_right1b = new MowzieModelRenderer(this, 27, 59);
        this.corner_back_right1b.setRotationPoint(1.5F, -3.0F, -1.5F);
        this.corner_back_right1b.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(corner_back_right1b, -0.27314402793711257F, -0.027401669256310976F, -0.27314402793711257F);
        this.gourdbag_base = new MowzieModelRenderer(this, 0, 80);
        this.gourdbag_base.setRotationPoint(3.0F, 0.0F, -0.5F);
        this.gourdbag_base.addBox(0.0F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(gourdbag_base, -0.18203784098300857F, -0.5462880558742251F, 0.18203784098300857F);
        this.coracle_base = new MowzieModelRenderer(this, 0, 0);
        this.coracle_base.setRotationPoint(0.0F, 23.5F, 0.0F);
        this.coracle_base.addBox(-6.0F, 0.0F, -6.0F, 12, 1, 12, 0.0F);
        this.setRotateAngle(coracle_base, 0.0F, 0.7853981633974483F, 0.0F);
        this.arm_right1b = new MowzieModelRenderer(this, 95, 45);
        this.arm_right1b.setRotationPoint(0.0F, 3.5F, 0.5F);
        this.arm_right1b.addBox(-0.51F, 0.0F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(arm_right1b, -0.6829473363053812F, 0.0F, 0.0F);
        this.hook_left = new MowzieModelRenderer(this, 7, 68);
        this.hook_left.setRotationPoint(1.0F, 0.0F, 0.5F);
        this.hook_left.addBox(0.0F, 0.0F, -2.0F, 0, 3, 3, 0.0F);
        this.setRotateAngle(hook_left, 0.31869712141416456F, -0.31869712141416456F, -0.31869712141416456F);
        this.chest = new MowzieModelRenderer(this, 90, 8);
        this.chest.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.chest.addBox(-2.5F, -3.0F, -2.5F, 5, 4, 4, 0.0F);
        this.setRotateAngle(chest, 0.136659280431156F, 0.0F, 0.091106186954104F);
        this.corner_back_left1a = new MowzieModelRenderer(this, 18, 52);
        this.corner_back_left1a.setRotationPoint(6.0F, 0.0F, 6.0F);
        this.corner_back_left1a.addBox(-1.5F, -3.0F, -1.5F, 2, 4, 2, 0.0F);
        this.setRotateAngle(corner_back_left1a, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F);
        this.corner_front_right1a = new MowzieModelRenderer(this, 9, 52);
        this.corner_front_right1a.setRotationPoint(-6.0F, 0.0F, -6.0F);
        this.corner_front_right1a.addBox(-0.5F, -3.0F, -0.5F, 2, 4, 2, 0.0F);
        this.setRotateAngle(corner_front_right1a, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F);
        this.paddle_knob = new MowzieModelRenderer(this, 118, 0);
        this.paddle_knob.setRotationPoint(0.0F, 0.0F, 0.5F);
        this.paddle_knob.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(paddle_knob, -0.36425021489121656F, 0.0F, 0.0F);
        this.paddle_main = new MowzieModelRenderer(this, 118, 3);
        this.paddle_main.setRotationPoint(-0.5F, 4.0F, 0.0F);
        this.paddle_main.addBox(-0.5F, 0.0F, -0.5F, 1, 14, 1, 0.0F);
        this.setRotateAngle(paddle_main, 1.8212510744560826F, -1.2292353921796064F, 0.18203784098300857F);
        this.arm_left1b = new MowzieModelRenderer(this, 90, 45);
        this.arm_left1b.setRotationPoint(0.0F, 3.5F, 0.5F);
        this.arm_left1b.addBox(-0.49F, 0.0F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(arm_left1b, -1.0471975511965976F, 0.0F, 0.0F);
        this.bench_conn_left = new MowzieModelRenderer(this, 29, 75);
        this.bench_conn_left.setRotationPoint(5.4F, 0.0F, 0.0F);
        this.bench_conn_left.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
        this.rope_fl1 = new MowzieModelRenderer(this, 0, 65);
        this.rope_fl1.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope_fl1.addBox(-2.0F, -1.0F, -1.0F, 3, 2, 3, 0.0F);
        this.setRotateAngle(rope_fl1, 0.136659280431156F, -0.01361356816555577F, 0.136659280431156F);
        this.top_back = new MowzieModelRenderer(this, 54, 28);
        this.top_back.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.top_back.addBox(-7.0F, -2.0F, 0.0F, 14, 2, 1, 0.0F);
        this.setRotateAngle(top_back, -0.27314402793711257F, 0.0F, 0.0F);
        this.bench = new MowzieModelRenderer(this, 0, 75);
        this.bench.setRotationPoint(-0.5F, -2.5F, 0.5F);
        this.bench.addBox(-5.5F, -1.0F, 0.0F, 11, 1, 3, 0.0F);
        this.setRotateAngle(bench, 0.0F, -0.7853981633974483F, 0.0F);
        this.arm_right1a = new MowzieModelRenderer(this, 95, 39);
        this.arm_right1a.setRotationPoint(-2.5F, -2.0F, -1.0F);
        this.arm_right1a.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(arm_right1a, -0.6829473363053812F, -0.18203784098300857F, 0.40980330836826856F);
        this.top_left = new MowzieModelRenderer(this, 0, 30);
        this.top_left.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.top_left.addBox(0.0F, -2.0F, -7.0F, 1, 2, 14, 0.0F);
        this.setRotateAngle(top_left, 0.0F, 0.0F, 0.27314402793711257F);
        this.rope_br1_1 = new MowzieModelRenderer(this, 39, 65);
        this.rope_br1_1.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope_br1_1.addBox(-1.0F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        this.setRotateAngle(rope_br1_1, -0.136659280431156F, -0.01361356816555577F, -0.136659280431156F);
        this.net_weightline = new MowzieModelRenderer(this, 60, 98);
        this.net_weightline.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.net_weightline.addBox(-0.5F, 0.0F, -6.0F, 1, 0, 6, 0.0F);
        this.setRotateAngle(net_weightline, -0.091106186954104F, 0.0F, 0.0F);
        this.leg_right1b = new MowzieModelRenderer(this, 95, 56);
        this.leg_right1b.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.leg_right1b.addBox(-0.51F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(leg_right1b, 1.4114477660878142F, 0.0F, 0.0F);
        this.paddle_blade1b = new MowzieModelRenderer(this, 118, 24);
        this.paddle_blade1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.paddle_blade1b.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1, 0.0F);
        this.setRotateAngle(paddle_blade1b, 0.27314402793711257F, 0.0F, 0.0F);
        this.net_netting2 = new MowzieModelRenderer(this, 44, 98);
        this.net_netting2.setRotationPoint(0.0F, 5.0F, -0.5F);
        this.net_netting2.addBox(-1.5F, -0.5F, -3.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(net_netting2, -0.045553093477052F, 0.091106186954104F, 0.0F);
        this.jaw = new MowzieModelRenderer(this, 107, 21);
        this.jaw.setRotationPoint(0.0F, -1.0F, -1.5F);
        this.jaw.addBox(-1.5F, 0.0F, -2.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(jaw, 0.22759093446006054F, 0.0F, 0.0F);
        this.gourdbag_top = new MowzieModelRenderer(this, 0, 94);
        this.gourdbag_top.setRotationPoint(0.5F, -3.0F, 0.5F);
        this.gourdbag_top.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(gourdbag_top, -0.091106186954104F, 0.0F, 0.0F);
        this.side_right = new MowzieModelRenderer(this, 27, 14);
        this.side_right.setRotationPoint(-5.0F, 0.0F, 0.0F);
        this.side_right.addBox(-1.0F, -3.0F, -6.0F, 1, 3, 12, 0.0F);
        this.setRotateAngle(side_right, 0.0F, 0.0F, -0.18203784098300857F);
        this.ear_left = new MowzieModelRenderer(this, 95, 29);
        this.ear_left.setRotationPoint(1.0F, -2.5F, -1.5F);
        this.ear_left.addBox(-1.0F, -0.5F, 0.0F, 5, 3, 0, 0.0F);
        this.setRotateAngle(ear_left, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        this.net_netting3 = new MowzieModelRenderer(this, 57, 98);
        this.net_netting3.setRotationPoint(0.0F, 5.0F, -0.5F);
        this.net_netting3.addBox(-1.0F, -0.5F, -2.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(net_netting3, 0.045553093477052F, 0.136659280431156F, 0.0F);
        this.rope_br1 = new MowzieModelRenderer(this, 26, 65);
        this.rope_br1.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope_br1.addBox(-2.0F, -1.0F, -2.0F, 3, 2, 3, 0.0F);
        this.setRotateAngle(rope_br1, -0.136659280431156F, 0.01361356816555577F, 0.136659280431156F);
        this.netrope1a = new MowzieModelRenderer(this, 0, 98);
        this.netrope1a.setRotationPoint(-6.5F, -2.0F, 6.5F);
        this.netrope1a.addBox(-0.5F, -1.0F, 0.0F, 1, 6, 0, 0.0F);
        this.setRotateAngle(netrope1a, 0.5918411493512771F, -0.7740535232594852F, 0.0F);
        this.top_right = new MowzieModelRenderer(this, 31, 30);
        this.top_right.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.top_right.addBox(-1.0F, -2.0F, -7.0F, 1, 2, 14, 0.0F);
        this.setRotateAngle(top_right, 0.0F, 0.0F, -0.27314402793711257F);
        this.gourdbag_mid = new MowzieModelRenderer(this, 0, 87);
        this.gourdbag_mid.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.gourdbag_mid.addBox(-0.01F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(gourdbag_mid, -0.22759093446006054F, 0.0F, 0.0F);
        this.top_front = new MowzieModelRenderer(this, 54, 19);
        this.top_front.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.top_front.addBox(-7.0F, -2.0F, -1.0F, 14, 2, 1, 0.0F);
        this.setRotateAngle(top_front, 0.27314402793711257F, 0.0F, 0.0F);
        this.cloth1a = new MowzieModelRenderer(this, 90, 33);
        this.cloth1a.setRotationPoint(0.0F, -1.0F, 0.5F);
        this.cloth1a.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0, 0.0F);
        this.setRotateAngle(cloth1a, 0.5462880558742251F, 0.0F, 0.0F);
        this.net_weight = new MowzieModelRenderer(this, 69, 98);
        this.net_weight.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.net_weight.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(net_weight, -0.091106186954104F, 0.0F, 0.0F);
        this.leg_left1b = new MowzieModelRenderer(this, 90, 56);
        this.leg_left1b.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.leg_left1b.addBox(-0.49F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(leg_left1b, 1.1838568316277536F, 0.0F, 0.0F);
        this.corner_front_right1a.addChild(this.corner_front_right1b);
        this.coracle_base.addChild(this.side_front);
        this.coracle_base.addChild(this.corner_back_right1a);
        this.body_base.addChild(this.leg_left1a);
        this.netrope1b.addChild(this.net_ring1);
        this.chest.addChild(this.arm_left1a);
        this.head_main.addChild(this.nose);
        this.paddle_main.addChild(this.paddle_blade1a);
        this.corner_back_left1a.addChild(this.corner_back_left1b);
        this.bench.addChild(this.bench_conn_right);
        this.net_ring1.addChild(this.net_netting1);
        this.top_back.addChild(this.side_rope2);
        this.coracle_base.addChild(this.side_back);
        this.coracle_base.addChild(this.side_left);
        this.corner_front_left1a.addChild(this.corner_front_left1b);
        this.corner_front_right1a.addChild(this.rope_fr1);
        this.coracle_base.addChild(this.corner_front_left1a);
        this.chest.addChild(this.head_connect);
        this.top_left.addChild(this.side_rope1);
        this.cloth1a.addChild(this.cloth1b);
        this.head_connect.addChild(this.head_main);
        this.netrope1a.addChild(this.netrope1b);
        this.head_main.addChild(this.ear_right);
        this.body_base.addChild(this.leg_right1a);
        this.rope_fl1.addChild(this.hook_front);
        this.corner_back_right1a.addChild(this.corner_back_right1b);
        this.coracle_base.addChild(this.gourdbag_base);
        this.arm_right1a.addChild(this.arm_right1b);
        this.rope_br1.addChild(this.hook_left);
        this.body_base.addChild(this.chest);
        this.coracle_base.addChild(this.corner_back_left1a);
        this.coracle_base.addChild(this.corner_front_right1a);
        this.paddle_main.addChild(this.paddle_knob);
        this.arm_left1b.addChild(this.paddle_main);
        this.arm_left1a.addChild(this.arm_left1b);
        this.bench.addChild(this.bench_conn_left);
        this.corner_front_left1a.addChild(this.rope_fl1);
        this.side_back.addChild(this.top_back);
        this.coracle_base.addChild(this.bench);
        this.chest.addChild(this.arm_right1a);
        this.side_left.addChild(this.top_left);
        this.corner_back_right1a.addChild(this.rope_br1_1);
        this.net_ring1.addChild(this.net_weightline);
        this.leg_right1a.addChild(this.leg_right1b);
        this.paddle_blade1a.addChild(this.paddle_blade1b);
        this.net_netting1.addChild(this.net_netting2);
        this.head_connect.addChild(this.jaw);
        this.gourdbag_mid.addChild(this.gourdbag_top);
        this.coracle_base.addChild(this.side_right);
        this.head_main.addChild(this.ear_left);
        this.net_netting2.addChild(this.net_netting3);
        this.corner_back_left1a.addChild(this.rope_br1);
        this.coracle_base.addChild(this.netrope1a);
        this.side_right.addChild(this.top_right);
        this.gourdbag_base.addChild(this.gourdbag_mid);
        this.side_front.addChild(this.top_front);
        this.head_main.addChild(this.cloth1a);
        this.net_weightline.addChild(this.net_weight);
        this.leg_left1a.addChild(this.leg_left1b);
        this.coracle_base.addChild(body_base);

        body_base.rotationPointY -= 23;
        body_base.rotateAngleY -= Math.PI / 4f;
        body_base.rotationPointZ -= 2.8;
        body_base.rotationPointX -= 0.3;

        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.coracle_base.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(MowzieModelRenderer MowzieModelRenderer, float x, float y, float z) {
        MowzieModelRenderer.rotateAngleX = x;
        MowzieModelRenderer.rotateAngleY = y;
        MowzieModelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        faceTarget(head_connect, 1, f3, f4);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        super.setLivingAnimations(entity, swing, speed, partialRenderTicks);
        setToInitPose();
        float frame = entity.ticksExisted + partialRenderTicks;

        if (entity.isInWater()) {
            // Boat idle
            bob(coracle_base, 0.1f, 0.2f, false, frame, 1f);
            walk(coracle_base, 0.06f, 0.05f, false, 0, 0, frame, 1f);
            swing(coracle_base, 0.08f, 0.05f, false, 0.3f, 0, frame, 1f);
            flap(coracle_base, 0.04f, 0.05f, false, 1.2f, 0, frame, 1f);
            walk(netrope1a, 0.06f, 0.05f, true, 0, -0.025f, frame, 1f);
            swing(netrope1a, 0.08f, 0.05f, true, 0.3f, 0, frame, 1f);
            flap(netrope1a, 0.04f, 0.05f, true, 1.2f, 0, frame, 1f);
            walk(body_base, 0.06f, 0.05f, true, 0, 0, frame, 1f);
            swing(body_base, 0.08f, 0.05f, true, 0.3f, 0, frame, 1f);
            flap(body_base, 0.04f, 0.05f, true, 1.2f, 0, frame, 1f);

            MowzieModelRenderer netting[] = {netrope1a, netrope1b, net_netting1, net_netting2, net_netting3};
            float offsetAmount = 1.2f;
            for (int i = 0; i < netting.length; i++) {
                walk(netting[i], 0.08f, 0.05f, false, 2 - offsetAmount * i, 0, frame, 1f);
                swing(netting[i], 0.06f, 0.05f, false, 1.8f - offsetAmount * i, 0, frame, 1f);
                flap(netting[i], 0.05f, 0.05f, false, 1.3f - offsetAmount * i, 0, frame, 1f);
            }
            walk(net_weightline, 0.08f, 0.15f, false, 2 - offsetAmount * 2, 0, frame, 1f);
            swing(net_weightline, 0.06f, 0.15f, false, 1.8f - offsetAmount * 2, 0, frame, 1f);
            flap(net_weightline, 0.05f, 0.15f, false, 1.3f - offsetAmount * 2, 0, frame, 1f);

            walk(hook_front, 0.16f, 0.1f + (float) Math.sin(frame * 0.04f) * 0.1f, false, 0, 0, frame, 1f);
            walk(hook_left, 0.16f, 0.1f + (float) Math.sin(frame * 0.04f + 0.6) * 0.1f, false, 1.7f, 0, frame, 1f);
            flap(side_rope1, 0.08f, 0.15f, false, 2.3f, 0, frame, 1f);
            // Greebling idle
            walk(chest, 0.1f, 0.05f, false, 1, 0, frame, 1f);
            walk(head_connect, 0.1f, 0.05f, true, 1, 0, frame, 1f);
            walk(jaw, 0.1f, 0.05f, true, 1, -0.2f, frame, 1f);
            walk(cloth1a, 0.1f, 0.05f, false, 1.5f, 0.025f, frame, 1f);
            walk(cloth1b, 0.1f, 0.05f, false, 1f, 0.025f, frame, 1f);

            // Paddling
//            swing = frame;
//            speed = 1f;

            float globalDegree = 0.6f;
            float globalSpeed = 1.4f;
            walk(body_base, 0.15f * globalSpeed, 0.3f * globalDegree, false, 0, 0.9f, swing, speed);
            walk(leg_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, swing, speed);
            walk(leg_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, swing, speed);
            walk(arm_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, swing, speed);
            walk(arm_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, swing, speed);
            walk(head_connect, 0.15f * globalSpeed, 0.25f * globalDegree, true, 0, -0.2f, swing, speed);
            walk(chest, 0.15f * globalSpeed, 0.3f * globalDegree, false, -1f, 0, swing, speed);
            walk(arm_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, -1f, 0, swing, speed);
            walk(arm_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, -1f, 0, swing, speed);
            walk(head_connect, 0.15f * globalSpeed, 0.25f * globalDegree, true, -1f, 0, swing, speed);

            walk(arm_left1a, 0.15f * globalSpeed, 0.8f * globalDegree, false, 2.5f, 0.4f, swing, speed);
            walk(arm_left1b, 0.15f * globalSpeed, 0.8f * globalDegree, true, 2.5f - 0.8f, 0.2f, swing, speed);

            float paddleDelay = -0.4f;
            flap(paddle_main, 0.15f * globalSpeed, 0.45f * globalDegree, false, 2.5f - 0.8f + paddleDelay, -0.1f, swing, speed);
            flap(arm_left1a, 0.15f * globalSpeed, 0.4f * globalDegree, false, 2.5f - 0.8f + paddleDelay, 0.3f, swing, speed);
            flap(chest, 0.15f * globalSpeed, 0.4f * globalDegree, false, 2.5f - 1.3f + paddleDelay, 0f, swing, speed);
            flap(head_connect, 0.15f * globalSpeed, 0.4f * globalDegree, true, 2.5f - 1.3f + paddleDelay, 0f, swing, speed);
            swing(paddle_main, 0.15f * globalSpeed, 0.2f * globalDegree, true, -0.5f + paddleDelay, -0.2f, swing, speed);

            swing(chest, 0.15f * globalSpeed, 0.6f * globalDegree, false, 2.5f + paddleDelay, -0.2f, swing, speed);
            swing(head_connect, 0.15f * globalSpeed, 0.6f * globalDegree, true, 2.5f + paddleDelay, -0.2f, swing, speed);

            flap(body_base, 0.15f * globalSpeed, 0.1f * globalDegree, false, 2.5f + paddleDelay, -0.7f, swing, speed);
            flap(chest, 0.15f * globalSpeed, 0.1f * globalDegree, false, 2.5f + paddleDelay, 0.4f, swing, speed);
            flap(leg_left1a, 0.15f * globalSpeed, 0.1f * globalDegree, true, 2.5f + paddleDelay, 0.4f, swing, speed);
            flap(leg_right1a, 0.15f * globalSpeed, 0.1f * globalDegree, true, 2.5f + paddleDelay, 0.4f, swing, speed);

            walk(arm_right1a, 0.15f * globalSpeed, 0.7f * globalDegree, false, 2.5f + paddleDelay, 0.25f, swing, speed);
            walk(arm_right1b, 0.15f * globalSpeed, 1.0f * globalDegree, true, 2.5f - 0.9f + paddleDelay, -0.2f, swing, speed);
            walk(arm_right1b, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f + paddleDelay, 0f, swing, speed);
            swing(arm_right1a, 0.15f * globalSpeed, 0.5f * globalDegree, true, 2.5f + paddleDelay, 0.4f, swing, speed);
            flap(arm_right1a, 0.15f * globalSpeed, 0.6f * globalDegree, false, 2.5f - 0.8f + paddleDelay, 0f, swing, speed);
            flap(arm_right1b, 0.15f * globalSpeed, 0.4f * globalDegree, true, 2.5f + paddleDelay, -0.2f, swing, speed);
            arm_right1a.rotationPointZ += (float) Math.cos((swing + 2.5f + 0.8f + paddleDelay) * 0.15 * globalSpeed) * globalDegree * speed + 0.2f;

            walk(cloth1a, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f + paddleDelay, 0.3f, swing, speed);
            walk(cloth1b, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f - 1f + paddleDelay, 0.25f, swing, speed);

            flap(ear_left, 0.3f * globalSpeed, 0.1f * globalDegree, false, 2.5f - 1f + paddleDelay, 0f, swing, speed);
            flap(ear_right, 0.3f * globalSpeed, 0.1f * globalDegree, true, 2.5f - 1f + paddleDelay, 0f, swing, speed);

            body_base.rotationPointZ -= 1 * speed;
        }
    }
}
