package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityGreebling;

/**
 * Created by Josh on 8/19/2018.
 */
public class ModelGreebling extends MowzieModelBase{
    public MowzieModelRenderer cup;
    public MowzieModelRenderer root;
    public MowzieModelRenderer handle;
    public MowzieModelRenderer body_base;
    public MowzieModelRenderer chest;
    public MowzieModelRenderer legleft1;
    public MowzieModelRenderer legright1;
    public MowzieModelRenderer head1;
    public MowzieModelRenderer armright1;
    public MowzieModelRenderer armleft1;
    public MowzieModelRenderer jaw;
    public MowzieModelRenderer head2;
    public MowzieModelRenderer nose;
    public MowzieModelRenderer earleft;
    public MowzieModelRenderer earright;
    public MowzieModelRenderer beard;
    public MowzieModelRenderer armright2;
    public MowzieModelRenderer panflute1;
    public MowzieModelRenderer panflute2;
    public MowzieModelRenderer panflute3;
    public MowzieModelRenderer panflute4;
    public MowzieModelRenderer armleft2;
    public MowzieModelRenderer legleft2;
    public MowzieModelRenderer legright2;

    public ModelGreebling() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.earright = new MowzieModelRenderer(this, 16, 29);
        this.earright.setRotationPoint(-1.0F, -2.48F, -0.32F);
        this.earright.addBox(-4.237956694918856F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earright, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F);
        this.beard = new MowzieModelRenderer(this, 0, 29);
        this.beard.setRotationPoint(0.0F, 1.0F, -2.0F);
        this.beard.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0, 0.0F);
        this.setRotateAngle(beard, -0.36425021489121656F, 0.0F, 0.0F);
        this.panflute4 = new MowzieModelRenderer(this, 40, 10);
        this.panflute4.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.panflute4.addBox(0.0F, 0.0F, -1.0F, 1, 5, 1, 0.0F);
        this.setRotateAngle(panflute4, 0.0F, -0.045553093477052F, 0.0F);
        this.legleft1 = new MowzieModelRenderer(this, 45, 0);
        this.legleft1.mirror = true;
        this.legleft1.setRotationPoint(1.3F, 0.5777565139606047F, 0.16185614160758632F);
        this.legleft1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legleft1, -1.4175939675619955F, -0.8297069961766927F, 0.24760389207524505F);
        this.cup = new MowzieModelRenderer(this, 25, 15);
        this.cup.setRotationPoint(5.5F, 24.0F, -2.0F);
        this.cup.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(cup, 0.0F, -0.5009094953223726F, 0.0F);
        this.nose = new MowzieModelRenderer(this, 11, 25);
        this.nose.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.nose.addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(nose, -0.22759093446006054F, 0.0F, 0.0F);
        this.armright2 = new MowzieModelRenderer(this, 40, 0);
        this.armright2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armright2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright2, -1.9123572614101867F, 0.0F, 0.0F);
        this.legright1 = new MowzieModelRenderer(this, 45, 5);
        this.legright1.setRotationPoint(-1.3F, 0.5777565139606047F, 0.16185614160758632F);
        this.legright1.addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legright1, -1.2343262671693576F, 0.6958921313115842F, 0.2361931785788314F);
        this.panflute3 = new MowzieModelRenderer(this, 35, 10);
        this.panflute3.setRotationPoint(1.0F, 0.0F, 0.0F);
        this.panflute3.addBox(0.0F, 0.0F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(panflute3, 0.0F, -0.045553093477052F, 0.0F);
        this.earleft = new MowzieModelRenderer(this, 16, 25);
        this.earleft.setRotationPoint(1.0F, -2.48F, -0.32F);
        this.earleft.addBox(-0.7620433050811432F, -0.6272468752590973F, -0.2536307258254739F, 5, 3, 0, 0.0F);
        this.setRotateAngle(earleft, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F);
        this.panflute1 = new MowzieModelRenderer(this, 25, 10);
        this.panflute1.setRotationPoint(0.3091571662794066F, 4.451192104343287F, -0.5676794810700461F);
        this.panflute1.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(panflute1, 2.3818404332756264F, 0.07033489531877639F, 0.40103946021775155F);
        this.armleft2 = new MowzieModelRenderer(this, 30, 0);
        this.armleft2.setRotationPoint(0.0F, 3.5F, 0.0F);
        this.armleft2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft2, -1.9123572614101867F, 0.0F, 0.0F);
        this.jaw = new MowzieModelRenderer(this, 0, 25);
        this.jaw.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.jaw.addBox(-1.5F, 0.0F, -2.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(jaw, 0.22759093446006054F, 0.0F, 0.0F);
        this.handle = new MowzieModelRenderer(this, 34, 15);
        this.handle.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.handle.addBox(0.0F, -3.0F, 0.0F, 0, 3, 2, 0.0F);
        this.armleft1 = new MowzieModelRenderer(this, 25, 0);
        this.armleft1.setRotationPoint(2.5F, -1.6999999999999957F, -0.5000000000000008F);
        this.armleft1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armleft1, -0.5918411493512772F, 0.09110618695410401F, -0.36425021489121645F);
        this.head2 = new MowzieModelRenderer(this, 17, 21);
        this.head2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head2.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F);
        this.panflute2 = new MowzieModelRenderer(this, 30, 10);
        this.panflute2.setRotationPoint(0.5F, 0.0F, 0.5F);
        this.panflute2.addBox(0.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(panflute2, 0.0F, -0.045553093477052F, 0.0F);
        this.chest = new MowzieModelRenderer(this, 0, 0);
        this.chest.setRotationPoint(0.0F, -3.7748866854558005F, 0.5002304588572539F);
        this.chest.addBox(-2.5F, -3.0F, -2.0F, 5, 4, 4, 0.0F);
        this.setRotateAngle(chest, 0.27314402793711257F, 0.0F, 0.0F);
        this.root = new MowzieModelRenderer(this, 0, 0);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.root.addBox(0.0F, -24.0F, 0.0F, 0, 0, 0, 0.0F);
        this.head1 = new MowzieModelRenderer(this, 0, 17);
        this.head1.setRotationPoint(0.0F, -3.8F, -1.0F);
        this.head1.addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4, 0.0F);
        this.setRotateAngle(head1, 0.18203784098300857F, 0.0F, 0.0F);
        this.legright2 = new MowzieModelRenderer(this, 50, 5);
        this.legright2.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legright2.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legright2, 2.41309222380736F, 1.593485607070823F, 0.0F);
        this.legleft2 = new MowzieModelRenderer(this, 50, 0);
        this.legleft2.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.legleft2.addBox(-0.5F, -0.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(legleft2, 1.5707963267948966F, -1.593485607070823F, 0.7740535232594852F);
        this.body_base = new MowzieModelRenderer(this, 0, 9);
        this.body_base.setRotationPoint(0.0F, -1.1F, -1.5F);
        this.body_base.addBox(-2.0F, -3.5F, -0.76F, 4, 4, 3, 0.0F);
        this.setRotateAngle(body_base, -0.27314402793711257F, 0.0F, 0.0F);
        this.armright1 = new MowzieModelRenderer(this, 35, 0);
        this.armright1.setRotationPoint(-2.5F, -1.6999999999999957F, -0.5000000000000008F);
        this.armright1.addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(armright1, -0.5918411493512772F, -0.09110618695410401F, 0.36425021489121645F);
        this.head1.addChild(this.earright);
        this.jaw.addChild(this.beard);
        this.panflute3.addChild(this.panflute4);
        this.body_base.addChild(this.legleft1);
        this.head1.addChild(this.nose);
        this.armright1.addChild(this.armright2);
        this.body_base.addChild(this.legright1);
        this.panflute2.addChild(this.panflute3);
        this.head1.addChild(this.earleft);
        this.armright2.addChild(this.panflute1);
        this.armleft1.addChild(this.armleft2);
        this.head1.addChild(this.jaw);
        this.cup.addChild(this.handle);
        this.chest.addChild(this.armleft1);
        this.head1.addChild(this.head2);
        this.panflute1.addChild(this.panflute2);
        this.body_base.addChild(this.chest);
        this.chest.addChild(this.head1);
        this.legright1.addChild(this.legright2);
        this.legleft1.addChild(this.legleft2);
        this.root.addChild(this.body_base);
        this.chest.addChild(this.armright1);

        parts = new MowzieModelRenderer[] {cup,
                root,
                handle,
                body_base,
                chest,
                legleft1,
                legright1,
                head1,
                armright1,
                armleft1,
                jaw,
                head2,
                nose,
                earleft,
                earright,
                beard,
                armright2,
                panflute1,
                panflute2,
                panflute3,
                panflute4,
                armleft2,
                legleft2,
                legright2
        };
        setInitPose();
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.cup.render(f5);
        this.root.render(f5);
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

        float swaySpeed = 0.12f;

        flap(body_base, swaySpeed, 0.15f, false, 0, 0, frame, 1f);
        flap(legleft1, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        flap(legright1, swaySpeed, 0.15f, true, 0, 0, frame, 1f);
        chest.rotationPointY += Math.sin((frame - 3) * swaySpeed * 2) * 0.25;
        flap(head1, swaySpeed * 4, 0.075f, false, 0, 0, frame, 1f);
    }
}
