package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityOlm;

@SideOnly(Side.CLIENT)
public class ModelOlm extends MowzieModelBase {
    MowzieModelRenderer body_main;
    MowzieModelRenderer body2;
    MowzieModelRenderer tail1;
    MowzieModelRenderer leg_back_left1a;
    MowzieModelRenderer leg_back_right1a;
    MowzieModelRenderer body3;
    MowzieModelRenderer body4;
    MowzieModelRenderer head1;
    MowzieModelRenderer leg_front_left1a;
    MowzieModelRenderer leg_front_right1a;
    MowzieModelRenderer head2;
    MowzieModelRenderer gills_left1a;
    MowzieModelRenderer gills_right1a;
    MowzieModelRenderer snout;
    MowzieModelRenderer gills_top;
    MowzieModelRenderer gills_left1b;
    MowzieModelRenderer gills_right1b;
    MowzieModelRenderer leg_front_left1b;
    MowzieModelRenderer leg_front_right1b;
    MowzieModelRenderer tail2;
    MowzieModelRenderer tail3;
    MowzieModelRenderer tail2_fin;
    MowzieModelRenderer tail4;
    MowzieModelRenderer tail3_fin;
    MowzieModelRenderer tail4_fin;
    MowzieModelRenderer leg_back_left1b;
    MowzieModelRenderer leg_back_right1b;

    public ModelOlm() {
        textureWidth = 32;
        textureHeight = 32;
        leg_back_right1a = new MowzieModelRenderer(this, 20, 21);
        leg_back_right1a.setRotationPoint(-1.0F, 1.0F, -0.5F);
        leg_back_right1a.addBox(-2.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_back_right1a, 0.0F, -0.22759093446006054F, -0.4553564018453205F);
        head2 = new MowzieModelRenderer(this, 11, 28);
        head2.setRotationPoint(0.0F, 0.0F, -2.0F);
        head2.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
        tail2_fin = new MowzieModelRenderer(this, 22, -3);
        tail2_fin.setRotationPoint(0.0F, 0.0F, 0.0F);
        tail2_fin.addBox(0.0F, -0.5F, 0.0F, 0, 1, 3, 0.0F);
        tail3_fin = new MowzieModelRenderer(this, 22, -1);
        tail3_fin.setRotationPoint(0.0F, 0.0F, 0.0F);
        tail3_fin.addBox(0.0F, -1.5F, 0.0F, 0, 2, 3, 0.0F);
        leg_front_left1b = new MowzieModelRenderer(this, 13, 24);
        leg_front_left1b.setRotationPoint(2.0F, 0.0F, 1.0F);
        leg_front_left1b.addBox(0.0F, 0.01F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_front_left1b, 0.0F, 0.9105382707654417F, 0.0F);
        tail3 = new MowzieModelRenderer(this, 13, 12);
        tail3.setRotationPoint(0.0F, 1.0F, 3.0F);
        tail3.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
        setRotateAngle(tail3, -0.045553093477052F, 0.0F, 0.0F);
        snout = new MowzieModelRenderer(this, 20, 29);
        snout.setRotationPoint(0.01F, 0.0F, -2.0F);
        snout.addBox(-1.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(snout, 0.8196066167365371F, 0.0F, 0.0F);
        tail2 = new MowzieModelRenderer(this, 13, 6);
        tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail2.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 3, 0.0F);
        setRotateAngle(tail2, -0.045553093477052F, 0.0F, 0.0F);
        tail4_fin = new MowzieModelRenderer(this, 22, 2);
        tail4_fin.setRotationPoint(0.0F, 0.0F, 0.0F);
        tail4_fin.addBox(0.0F, -1.5F, 0.0F, 0, 3, 3, 0.0F);
        leg_back_right1b = new MowzieModelRenderer(this, 20, 24);
        leg_back_right1b.setRotationPoint(-2.0F, 0.0F, 0.0F);
        leg_back_right1b.addBox(-2.0F, 0.01F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_back_right1b, 0.0F, -0.36425021489121656F, 0.0F);
        body2 = new MowzieModelRenderer(this, 0, 7);
        body2.setRotationPoint(0.0F, 0.0F, -4.0F);
        body2.addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F);
        body4 = new MowzieModelRenderer(this, 0, 21);
        body4.setRotationPoint(0.0F, 0.0F, -4.0F);
        body4.addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F);
        gills_left1a = new MowzieModelRenderer(this, 22, 10);
        gills_left1a.setRotationPoint(1.5F, 2.0F, 0.0F);
        gills_left1a.addBox(0.0F, -2.0F, 0.0F, 0, 2, 1, 0.0F);
        setRotateAngle(gills_left1a, 0.22759093446006054F, 0.18203784098300857F, 0.0F);
        leg_back_left1a = new MowzieModelRenderer(this, 20, 21);
        leg_back_left1a.setRotationPoint(1.0F, 1.0F, -0.5F);
        leg_back_left1a.addBox(0.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_back_left1a, 0.0F, 0.22759093446006054F, 0.4553564018453205F);
        body3 = new MowzieModelRenderer(this, 0, 14);
        body3.setRotationPoint(0.0F, 0.0F, -4.0F);
        body3.addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F);
        gills_top = new MowzieModelRenderer(this, 21, 9);
        gills_top.setRotationPoint(0.0F, 0.0F, 2.0F);
        gills_top.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 1, 0.0F);
        setRotateAngle(gills_top, 0.4553564018453205F, 0.0F, 0.0F);
        gills_left1b = new MowzieModelRenderer(this, 25, 10);
        gills_left1b.setRotationPoint(0.0F, 0.0F, 1.0F);
        gills_left1b.addBox(0.0F, -2.0F, 0.0F, 0, 2, 1, 0.0F);
        setRotateAngle(gills_left1b, 0.0F, 0.136659280431156F, 0.0F);
        leg_front_right1b = new MowzieModelRenderer(this, 13, 24);
        leg_front_right1b.setRotationPoint(-2.0F, 0.0F, 1.0F);
        leg_front_right1b.addBox(-2.0F, 0.01F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_front_right1b, 0.0F, -0.9105382707654417F, 0.0F);
        leg_front_right1a = new MowzieModelRenderer(this, 13, 21);
        leg_front_right1a.setRotationPoint(-1.0F, 1.0F, -3.0F);
        leg_front_right1a.addBox(-2.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_front_right1a, 0.0F, 0.40980330836826856F, -0.5462880558742251F);
        tail4 = new MowzieModelRenderer(this, 13, 17);
        tail4.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail4.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(tail4, -0.136659280431156F, 0.0F, 0.0F);
        body_main = new MowzieModelRenderer(this, 0, 0);
        body_main.setRotationPoint(0.0F, 21.0F, 8.0F);
        body_main.addBox(-1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F);
        tail1 = new MowzieModelRenderer(this, 13, 0);
        tail1.setRotationPoint(0.0F, 0.0F, 0.0F);
        tail1.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 3, 0.0F);
        setRotateAngle(tail1, -0.045553093477052F, 0.0F, 0.0F);
        leg_front_left1a = new MowzieModelRenderer(this, 13, 21);
        leg_front_left1a.setRotationPoint(1.0F, 1.0F, -3.0F);
        leg_front_left1a.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_front_left1a, 0.0F, -0.40980330836826856F, 0.5462880558742251F);
        gills_right1b = new MowzieModelRenderer(this, 25, 13);
        gills_right1b.setRotationPoint(0.0F, 0.0F, 1.0F);
        gills_right1b.addBox(0.0F, -2.0F, 0.0F, 0, 2, 1, 0.0F);
        setRotateAngle(gills_right1b, 0.0F, -0.136659280431156F, 0.0F);
        leg_back_left1b = new MowzieModelRenderer(this, 20, 24);
        leg_back_left1b.setRotationPoint(2.0F, 0.0F, 0.0F);
        leg_back_left1b.addBox(0.0F, 0.01F, -1.0F, 2, 1, 1, 0.0F);
        setRotateAngle(leg_back_left1b, 0.0F, 0.36425021489121656F, 0.0F);
        head1 = new MowzieModelRenderer(this, 0, 28);
        head1.setRotationPoint(0.0F, 0.0F, -4.0F);
        head1.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(head1, 0.18203784098300857F, 0.0F, 0.0F);
        gills_right1a = new MowzieModelRenderer(this, 22, 13);
        gills_right1a.setRotationPoint(-1.5F, 2.0F, 0.0F);
        gills_right1a.addBox(0.0F, -2.0F, 0.0F, 0, 2, 1, 0.0F);
        setRotateAngle(gills_right1a, 0.22759093446006054F, -0.18203784098300857F, 0.0F);
        body_main.addChild(leg_back_right1a);
        head1.addChild(head2);
        tail2.addChild(tail2_fin);
        tail3.addChild(tail3_fin);
        leg_front_left1a.addChild(leg_front_left1b);
        tail2.addChild(tail3);
        head2.addChild(snout);
        tail1.addChild(tail2);
        tail4.addChild(tail4_fin);
        leg_back_right1a.addChild(leg_back_right1b);
        body_main.addChild(body2);
        body3.addChild(body4);
        head1.addChild(gills_left1a);
        body_main.addChild(leg_back_left1a);
        body2.addChild(body3);
        head2.addChild(gills_top);
        gills_left1a.addChild(gills_left1b);
        leg_front_right1a.addChild(leg_front_right1b);
        body4.addChild(leg_front_right1a);
        tail3.addChild(tail4);
        body_main.addChild(tail1);
        body4.addChild(leg_front_left1a);
        gills_right1a.addChild(gills_right1b);
        leg_back_left1a.addChild(leg_back_left1b);
        body4.addChild(head1);
        head1.addChild(gills_right1a);

		parts = new MowzieModelRenderer[] {
			    body_main,
			    body2,
			    tail1,
			    leg_back_left1a,
			    leg_back_right1a,
			    body3,
			    body4,
			    head1,
			    leg_front_left1a,
			    leg_front_right1a,
			    head2,
			    gills_left1a,
			    gills_right1a,
			    snout,
			    gills_top,
			    gills_left1b,
			    gills_right1b,
			    leg_front_left1b,
			    leg_front_right1b,
			    tail2,
			    tail3,
			    tail2_fin,
			    tail4,
			    tail3_fin,
			    tail4_fin,
			    leg_back_left1b,
			    leg_back_right1b
		        };

			setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        body_main.render(scale);
    }

	@Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityOlm olm = (EntityOlm) entity;
        float globalDegree = -0.5F;
        float rippleSpeed = -1F;
        float frame = olm.ticksExisted + partialRenderTicks;
		setToInitPose();
		swing(gills_left1a, rippleSpeed * 0.125F, globalDegree * 0.5F, true, 0F, 0F, frame, 1F);
		swing(gills_right1a, rippleSpeed * 0.125F, globalDegree *0.5F, false, 0F, 0F, frame, 1F);
		walk(gills_top, rippleSpeed * 0.125F, globalDegree, false, 0F, 0F, frame, 1F);
		chainSwing(parts, rippleSpeed * 0.5F, globalDegree, 1F, swing, speed);
		chainFlap(parts, rippleSpeed, -globalDegree * 0.25F, 1F,  swing, speed);
		swing(leg_back_left1a, rippleSpeed, globalDegree * 3F, true, 1F, 0F, swing, speed);
		swing(leg_back_right1a, rippleSpeed, globalDegree * 3F, true, 1F, 0F, swing, speed);
		swing(leg_front_left1a, rippleSpeed, globalDegree * 3F, false, 1F, 0F, swing, speed);
		swing(leg_front_right1a, rippleSpeed, globalDegree * 3F, false, 1F, 0F, swing, speed);
		
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
