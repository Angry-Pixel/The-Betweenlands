package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityGreebling;

/**
 * Created by Josh on 8/19/2018.
 */
public class ModelGreebling extends MowzieModelBase{
    public MowzieModelRenderer root_0;
    public MowzieModelRenderer body_base_0;
    public MowzieModelRenderer chest_0;
    public MowzieModelRenderer legleft1_0;
    public MowzieModelRenderer legright1_0;
    public MowzieModelRenderer head1_0;
    public MowzieModelRenderer armleft1_0;
    public MowzieModelRenderer armright1_0;
    public MowzieModelRenderer jaw_0;
    public MowzieModelRenderer head2_0;
    public MowzieModelRenderer nose_0;
    public MowzieModelRenderer earleft_0;
    public MowzieModelRenderer earright_0;
    public MowzieModelRenderer armleft2_0;
    public MowzieModelRenderer lutebase_0;
    public MowzieModelRenderer luteneck_0;
    public MowzieModelRenderer endpiece1_0;
    public MowzieModelRenderer armright2_0;
    public MowzieModelRenderer strokestick_0;
    public MowzieModelRenderer legleft2_0;
    public MowzieModelRenderer legright2_0;

    public MowzieModelRenderer cup_1;
    public MowzieModelRenderer root_1;
    public MowzieModelRenderer handle_1;
    public MowzieModelRenderer body_base_1;
    public MowzieModelRenderer chest_1;
    public MowzieModelRenderer legleft1_1;
    public MowzieModelRenderer legright1_1;
    public MowzieModelRenderer head1_1;
    public MowzieModelRenderer armright1_1;
    public MowzieModelRenderer armleft1_1;
    public MowzieModelRenderer jaw_1;
    public MowzieModelRenderer head2_1;
    public MowzieModelRenderer nose_1;
    public MowzieModelRenderer earleft_1;
    public MowzieModelRenderer earright_1;
    public MowzieModelRenderer beard_1;
    public MowzieModelRenderer armright2_1;
    public MowzieModelRenderer panflute1_1;
    public MowzieModelRenderer panflute2_1;
    public MowzieModelRenderer panflute3_1;
    public MowzieModelRenderer panflute4_1;
    public MowzieModelRenderer armleft2_1;
    public MowzieModelRenderer legleft2_1;
    public MowzieModelRenderer legright2_1;

    public ModelGreebling() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.jaw_0 = new MowzieModelRenderer(this, 0, 25);
        this.jaw_0.setRotationPoint(0.0F, 0.04549010195178495F, -1.4979263506025928F);
        this.jaw_0.addBox(-1.5F, -0.02276867034659224F, -1.5005186813797255F, 3, 1, 2, 0.0F);
        this.setRotateAngle(jaw_0, 0.045553093477052F, 0.0F, 0.0F);
        this.earleft_0 = new MowzieModelRenderer(this, 16, 25);
        this.earleft_0.setRotationPoint(1.0F, -2.5F, -0.59F);
        this.earleft_0.addBox(-0.6220433050811432F, -0.6372468752590972F, -0.023630725825473986F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earleft_0, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        this.earright_0 = new MowzieModelRenderer(this, 16, 29);
        this.earright_0.setRotationPoint(-1.0F, -2.5F, -0.59F);
        this.earright_0.addBox(-4.377956694918857F, -0.6372468752590972F, -0.023630725825473986F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earright_0, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        this.legright1_0 = new MowzieModelRenderer(this, 45, 5);
        this.legright1_0.setRotationPoint(-1.3F, 0.867466694522502F, -0.9149770530750317F);
        this.legright1_0.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legright1_0, -1.3087095159565685F, 0.3101876267227021F, 0.07436440490714699F);
        this.strokestick_0 = new MowzieModelRenderer(this, 37, 20);
        this.strokestick_0.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.strokestick_0.addBox(0.0F, -1.5F, -1.0F, 0, 7, 1, 0.0F);
        this.setRotateAngle(strokestick_0, 0.091106186954104F, 0.0F, -1.0471975511965976F);
        this.armleft1_0 = new MowzieModelRenderer(this, 25, 0);
        this.armleft1_0.setRotationPoint(2.5F, -2.122938627842288F, -0.784733536128615F);
        this.armleft1_0.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft1_0, -0.3881606553461388F, -0.04707782911457041F, -1.413927420973236F);
        this.armleft2_0 = new MowzieModelRenderer(this, 30, 0);
        this.armleft2_0.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armleft2_0.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft2_0, -1.5025539530419183F, 0.0F, 0.0F);
        this.endpiece1_0 = new MowzieModelRenderer(this, 37, 17);
        this.endpiece1_0.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.endpiece1_0.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(endpiece1_0, -0.27314402793711257F, 0.0F, 0.0F);
        this.head2_0 = new MowzieModelRenderer(this, 17, 21);
        this.head2_0.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head2_0.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        this.legleft1_0 = new MowzieModelRenderer(this, 45, 0);
        this.legleft1_0.mirror = true;
        this.legleft1_0.setRotationPoint(1.3F, 0.867466694522502F, -0.9149770530750317F);
        this.legleft1_0.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legleft1_0, -1.3087095159565685F, -0.3101876267227021F, -0.07436440490714699F);
        this.head1_0 = new MowzieModelRenderer(this, 0, 17);
        this.head1_0.setRotationPoint(0.0F, -3.8311339463882743F, -0.9674967227287707F);
        this.head1_0.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(head1_0, 0.045553093477052F, 0.0F, 0.0F);
        this.luteneck_0 = new MowzieModelRenderer(this, 30, 17);
        this.luteneck_0.setRotationPoint(0.017376167048784863F, 0.29556824894909506F, -0.330517345185515F);
        this.luteneck_0.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1, 0.0F);
        this.setRotateAngle(luteneck_0, -0.091106186954104F, 0.0F, 0.0F);
        this.armright1_0 = new MowzieModelRenderer(this, 35, 0);
        this.armright1_0.setRotationPoint(-2.5F, -2.122938627842288F, -0.784733536128615F);
        this.armright1_0.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright1_0, -0.7451176564534014F, -0.06546967612617839F, 0.4969738117393972F);
        this.lutebase_0 = new MowzieModelRenderer(this, 30, 8);
        this.lutebase_0.setRotationPoint(-0.7622866342321437F, 3.1200327646462362F, -1.21631341944674F);
        this.lutebase_0.addBox(-1.9902175722236533F, 0.2917097165214597F, -0.8443796388811212F, 4, 5, 3, 0.0F);
        this.setRotateAngle(lutebase_0, -0.9139542082504476F, -1.3997957633427642F, 2.535364913532174F);
        this.legleft2_0 = new MowzieModelRenderer(this, 50, 0);
        this.legleft2_0.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legleft2_0.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft2_0, 1.5707963267948966F, 0.0F, 0.0F);
        this.chest_0 = new MowzieModelRenderer(this, 0, 0);
        this.chest_0.setRotationPoint(0.0F, -2.965801266869104F, -0.27582661303015055F);
        this.chest_0.addBox(-2.5F, -3.3237656918367984F, -2.4506766441575647F, 5, 4, 4, 0.0F);
        this.setRotateAngle(chest_0, 0.091106186954104F, 0.0F, 0.0F);
        this.root_0 = new MowzieModelRenderer(this, 0, 0);
        this.root_0.setRotationPoint(0.0F, 24.0F, -5.5F);
        this.root_0.addBox(0.0F, -24.0F, 5.5F, 0, 0, 0, 0.0F);
        this.body_base_0 = new MowzieModelRenderer(this, 0, 9);
        this.body_base_0.setRotationPoint(0.0F, -1.1384599901460248F, 0.5872213733837306F);
        this.body_base_0.addBox(-2.0F, -3.182596199392689F, -2.0625201609095485F, 4, 4, 3, 0.0F);
        this.setRotateAngle(body_base_0, -0.22776546738526F, 0.0F, 0.0F);
        this.armright2_0 = new MowzieModelRenderer(this, 40, 0);
        this.armright2_0.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armright2_0.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright2_0, -0.6829473363053812F, 0.0F, -0.7285004297824331F);
        this.nose_0 = new MowzieModelRenderer(this, 11, 25);
        this.nose_0.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.nose_0.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(nose_0, -0.22759093446006054F, 0.0F, 0.0F);
        this.legright2_0 = new MowzieModelRenderer(this, 50, 5);
        this.legright2_0.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legright2_0.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright2_0, 1.4994643202709739F, 0.0F, 0.0F);
        this.head1_0.addChild(this.jaw_0);
        this.head1_0.addChild(this.earleft_0);
        this.head1_0.addChild(this.earright_0);
        this.body_base_0.addChild(this.legright1_0);
        this.armright2_0.addChild(this.strokestick_0);
        this.chest_0.addChild(this.armleft1_0);
        this.armleft1_0.addChild(this.armleft2_0);
        this.luteneck_0.addChild(this.endpiece1_0);
        this.head1_0.addChild(this.head2_0);
        this.body_base_0.addChild(this.legleft1_0);
        this.chest_0.addChild(this.head1_0);
        this.lutebase_0.addChild(this.luteneck_0);
        this.chest_0.addChild(this.armright1_0);
        this.armleft2_0.addChild(this.lutebase_0);
        this.legleft1_0.addChild(this.legleft2_0);
        this.body_base_0.addChild(this.chest_0);
        this.root_0.addChild(this.body_base_0);
        this.armright1_0.addChild(this.armright2_0);
        this.head1_0.addChild(this.nose_0);
        this.legright1_0.addChild(this.legright2_0);

        this.earright_1 = new MowzieModelRenderer(this, 16, 29);
        this.earright_1.setRotationPoint(-1.0F, -2.48F, -0.32F);
        this.earright_1.addBox(-4.237956694918856F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earright_1, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        this.beard_1 = new MowzieModelRenderer(this, 0, 29);
        this.beard_1.setRotationPoint(0.0F, 1.0F, -2.0F);
        this.beard_1.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        this.setRotateAngle(beard_1, -0.36425021489121656F, 0.0F, 0.0F);
        this.panflute4_1 = new MowzieModelRenderer(this, 40, 10);
        this.panflute4_1.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.panflute4_1.addBox(0.0F, 0.0F, -1.0F, 1, 5, 1, 0.0F);
        this.setRotateAngle(panflute4_1, 0.0F, -0.045553093477052F, 0.0F);
        this.legleft1_1 = new MowzieModelRenderer(this, 45, 0);
        this.legleft1_1.mirror = true;
        this.legleft1_1.setRotationPoint(1.3F, 0.5777565139606047F, 0.16185614160758632F);
        this.legleft1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legleft1_1, -1.4175939675619955F, -0.8297069961766927F, 0.24760389207524505F);
        this.cup_1 = new MowzieModelRenderer(this, 25, 15);
        this.cup_1.setRotationPoint(5.5F, 24.0F, -2.0F);
        this.cup_1.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(cup_1, 0.0F, -0.5009094953223726F, 0.0F);
        this.nose_1 = new MowzieModelRenderer(this, 11, 25);
        this.nose_1.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.nose_1.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(nose_1, -0.22759093446006054F, 0.0F, 0.0F);
        this.armright2_1 = new MowzieModelRenderer(this, 40, 0);
        this.armright2_1.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armright2_1.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright2_1, -1.9123572614101867F, 0.0F, 0.0F);
        this.legright1_1 = new MowzieModelRenderer(this, 45, 5);
        this.legright1_1.setRotationPoint(-1.3F, 0.5777565139606047F, 0.16185614160758632F);
        this.legright1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legright1_1, -1.2343262671693576F, 0.6958921313115842F, 0.2361931785788314F);
        this.panflute3_1 = new MowzieModelRenderer(this, 35, 10);
        this.panflute3_1.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.panflute3_1.addBox(0.0F, 0.0F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(panflute3_1, 0.0F, -0.045553093477052F, 0.0F);
        this.earleft_1 = new MowzieModelRenderer(this, 16, 25);
        this.earleft_1.setRotationPoint(1.0F, -2.48F, -0.32F);
        this.earleft_1.addBox(-0.7620433050811432F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earleft_1, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        this.panflute1_1 = new MowzieModelRenderer(this, 25, 10);
        this.panflute1_1.setRotationPoint(0.3091571662794066F, 4.451192104343287F, -0.5676794810700461F);
        this.panflute1_1.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(panflute1_1, 2.3818404332756264F, 0.07033489531877639F, 0.40103946021775155F);
        this.armleft2_1 = new MowzieModelRenderer(this, 30, 0);
        this.armleft2_1.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armleft2_1.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft2_1, -1.9123572614101867F, 0.0F, 0.0F);
        this.jaw_1 = new MowzieModelRenderer(this, 0, 25);
        this.jaw_1.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.jaw_1.addBox(-1.5F, 0.0F, -2.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(jaw_1, 0.22759093446006054F, 0.0F, 0.0F);
        this.handle_1 = new MowzieModelRenderer(this, 34, 15);
        this.handle_1.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.handle_1.addBox(0.0F, -3.0F, 0.0F, 0, 3, 2, 0.0F);
        this.armleft1_1 = new MowzieModelRenderer(this, 25, 0);
        this.armleft1_1.setRotationPoint(2.5F, -1.6999999999999957F, -0.5000000000000008F);
        this.armleft1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft1_1, -0.5918411493512772F, 0.09110618695410401F, -0.36425021489121645F);
        this.head2_1 = new MowzieModelRenderer(this, 17, 21);
        this.head2_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head2_1.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        this.panflute2_1 = new MowzieModelRenderer(this, 30, 10);
        this.panflute2_1.setRotationPoint(0.5F, 0.0F, 0.5F);
        this.panflute2_1.addBox(0.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(panflute2_1, 0.0F, -0.045553093477052F, 0.0F);
        this.chest_1 = new MowzieModelRenderer(this, 0, 0);
        this.chest_1.setRotationPoint(0.0F, -3.7748866854558005F, 0.5002304588572539F);
        this.chest_1.addBox(-2.5F, -3.0F, -2.0F, 5, 4, 4, 0.0F);
        this.setRotateAngle(chest_1, 0.27314402793711257F, 0.0F, 0.0F);
        this.root_1 = new MowzieModelRenderer(this, 0, 0);
        this.root_1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.root_1.addBox(0.0F, -24.0F, 0.0F, 0, 0, 0, 0.0F);
        this.head1_1 = new MowzieModelRenderer(this, 0, 17);
        this.head1_1.setRotationPoint(0.0F, -3.8F, -1.0F);
        this.head1_1.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(head1_1, 0.18203784098300857F, 0.0F, 0.0F);
        this.legright2_1 = new MowzieModelRenderer(this, 50, 5);
        this.legright2_1.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legright2_1.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright2_1, 2.41309222380736F, 1.593485607070823F, 0.0F);
        this.legleft2_1 = new MowzieModelRenderer(this, 50, 0);
        this.legleft2_1.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legleft2_1.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft2_1, 1.5707963267948966F, -1.593485607070823F, 0.7740535232594852F);
        this.body_base_1 = new MowzieModelRenderer(this, 0, 9);
        this.body_base_1.setRotationPoint(0.0F, -1.1F, -1.5F);
        this.body_base_1.addBox(-2.0F, -3.5F, -0.76F, 4, 4, 3, 0.0F);
        this.setRotateAngle(body_base_1, -0.27314402793711257F, 0.0F, 0.0F);
        this.armright1_1 = new MowzieModelRenderer(this, 35, 0);
        this.armright1_1.setRotationPoint(-2.5F, -1.6999999999999957F, -0.5000000000000008F);
        this.armright1_1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright1_1, -0.5918411493512772F, -0.09110618695410401F, 0.36425021489121645F);
        this.head1_1.addChild(this.earright_1);
        this.jaw_1.addChild(this.beard_1);
        this.panflute3_1.addChild(this.panflute4_1);
        this.body_base_1.addChild(this.legleft1_1);
        this.head1_1.addChild(this.nose_1);
        this.armright1_1.addChild(this.armright2_1);
        this.body_base_1.addChild(this.legright1_1);
        this.panflute2_1.addChild(this.panflute3_1);
        this.head1_1.addChild(this.earleft_1);
        this.armright2_1.addChild(this.panflute1_1);
        this.armleft1_1.addChild(this.armleft2_1);
        this.head1_1.addChild(this.jaw_1);
        this.cup_1.addChild(this.handle_1);
        this.chest_1.addChild(this.armleft1_1);
        this.head1_1.addChild(this.head2_1);
        this.panflute1_1.addChild(this.panflute2_1);
        this.body_base_1.addChild(this.chest_1);
        this.chest_1.addChild(this.head1_1);
        this.legright1_1.addChild(this.legright2_1);
        this.legleft1_1.addChild(this.legleft2_1);
        this.root_1.addChild(this.body_base_1);
        this.chest_1.addChild(this.armright1_1);

        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        EntityGreebling greebling = (EntityGreebling) entity;

        if (greebling.getType() == 0) {
            float disappearFrame = greebling.disappearTimer > 0 ? (float) Math.pow(greebling.disappearTimer / 8f, 4) : 0;
            float scaleXZ = 1 - disappearFrame;
            float scaleY = 1 + 0.1f * disappearFrame;
            GL11.glScalef(scaleXZ, scaleY, scaleXZ);
            this.root_0.render(f5);
            GL11.glScalef(1 / scaleXZ, 1 / scaleY, 1 / scaleXZ);
        }
        else {
            this.cup_1.render(f5);
            float disappearFrame = greebling.disappearTimer > 0 ? (float) Math.pow(greebling.disappearTimer / 8f, 4) : 0;
            float scaleXZ = 1 - disappearFrame;
            float scaleY = 1 + 0.1f * disappearFrame;
            GL11.glScalef(scaleXZ, scaleY, scaleXZ);
            this.root_1.render(f5);
            GL11.glScalef(1 / scaleXZ, 1 / scaleY, 1 / scaleXZ);
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        EntityGreebling greebling = (EntityGreebling) entity;
        setToInitPose();
        float frame = greebling.ticksExisted + partialRenderTicks;

        float swaySpeed = 0.06f;

        float strokeSpeed = swaySpeed * 0.33f;
        
        flap(body_base_1, swaySpeed, 0.15f, false, 0, 0, frame, 1f);
        flap(legleft1_1, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        flap(legright1_1, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        chest_1.rotationPointY += Math.sin((frame - 3) * swaySpeed * 2) * 0.25;
        flap(head1_1, swaySpeed * 4, 0.075f, false, 0, 0, frame, 1f);

        flap(body_base_0, swaySpeed, 0.15f, false, 0, 0, frame, 1f);
        flap(legleft1_0, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        flap(legright1_0, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        chest_0.rotationPointY += Math.sin((frame - 3) * swaySpeed * 2) * 0.25;
        flap(head1_0, swaySpeed * 4, 0.075f, false, 0, 0, frame, 1f);
        swing(armright1_0, strokeSpeed * 4, 0.3f, false, 0, 0, frame, 1f);
        walk(strokestick_0, strokeSpeed * 4, 0.2f, false, 0, 0, frame, 1f);
        flap(strokestick_0, strokeSpeed * 4, 0.4f, true, 0, 0, frame, 1f);

        float disappearFrame = greebling.disappearTimer > 0 ? greebling.disappearTimer + partialRenderTicks : 0;

        body_base_1.rotationPointY -= 16 * Math.pow(disappearFrame/8f, 1.5);
        body_base_1.rotateAngleY += Math.pow(5* disappearFrame/8f, 1.4);

        body_base_0.rotationPointY -= 16 * Math.pow(disappearFrame/8f, 1.5);
        body_base_0.rotateAngleY += Math.pow(5* disappearFrame/8f, 1.4);
    }
}
