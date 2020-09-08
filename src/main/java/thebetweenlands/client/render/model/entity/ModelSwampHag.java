package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

@SideOnly(Side.CLIENT)
public class ModelSwampHag extends MowzieModelBase {
    MowzieModelRenderer body_base;
    AdvancedModelRenderer neck;
    AdvancedModelRenderer body_top;
    MowzieModelRenderer toadstool1;
    MowzieModelRenderer mushroomstem;
    AdvancedModelRenderer armright;
    MowzieModelRenderer legright1;
    MowzieModelRenderer legleft1;
    MowzieModelRenderer body_inner;
    MowzieModelRenderer vines2;
    MowzieModelRenderer toadstool1b;
    MowzieModelRenderer toadstool2;
    MowzieModelRenderer toadstool3;
    MowzieModelRenderer vines1;
    MowzieModelRenderer toadstool2b;
    MowzieModelRenderer mushroomhat;
    MowzieModelRenderer mushroomhat2;
    MowzieModelRenderer legright2;
    MowzieModelRenderer legleft2;
    MowzieModelRenderer head1;
    MowzieModelRenderer head2;
    MowzieModelRenderer jaw;
    MowzieModelRenderer toadstool4;
    MowzieModelRenderer brain;
    MowzieModelRenderer modelCore;

    public ModelSwampHag() {
        textureWidth = 128;
        textureHeight = 64;
        toadstool4 = new MowzieModelRenderer(this, 20, 45);
        toadstool4.setRotationPoint(4.0F, -4.0F, -0.4F);
        toadstool4.addBox(-2.7F, 0.0F, -0.9F, 4, 1, 3, 0.0F);
        setRotation(toadstool4, 0.0F, -2.231054382824351F, 0.0F);
        toadstool3 = new MowzieModelRenderer(this, 0, 46);
        toadstool3.setRotationPoint(-3.0F, 2.0F, 0.0F);
        toadstool3.addBox(0.0F, 0.0F, -3.0F, 4, 1, 4, 0.0F);
        setRotation(toadstool3, 0.0F, 0.22759093446006054F, 0.0F);
        head2 = new MowzieModelRenderer(this, 70, 25);
        head2.setRotationPoint(0.0F, 0.0F, 0.0F);
        head2.addBox(-3.5F, 0.0F, -3.5F, 7, 3, 3, 0.0F);
        toadstool2b = new MowzieModelRenderer(this, 19, 41);
        toadstool2b.setRotationPoint(0.0F, 0.0F, 0.0F);
        toadstool2b.addBox(-1.5F, 0.0F, -4.0F, 4, 1, 1, 0.0F);
        vines1 = new MowzieModelRenderer(this, 0, 47);
        vines1.setRotationPoint(1.5F, 1.0F, -1.5F);
        vines1.addBox(0.0F, 0.0F, -2.5F, 0, 10, 5, 0.0F);
        setRotation(vines1, 0.0F, 1.1383037381507017F, 0.0F);
        toadstool1 = new MowzieModelRenderer(this, 0, 34);
        toadstool1.setRotationPoint(4.5F, -4.0F, 0.0F);
        toadstool1.addBox(-4.3F, -1.0F, -3.5F, 8, 2, 4, 0.0F);
        setRotation(toadstool1, 0.0F, -1.0471975511965976F, 0.0F);
        brain = new MowzieModelRenderer(this, 90, 35);
        brain.setRotationPoint(0.0F, 0.0F, 0.0F);
        brain.addBox(-3.5F, -5.5F, -8.0F, 7, 5, 7, 0.0F);
        legright2 = new MowzieModelRenderer(this, 55, 20);
        legright2.setRotationPoint(0.0F, 8.0F, 0.0F);
        legright2.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legright2, 0.18203784098300857F, 0.0F, 0.0F);
        vines2 = new MowzieModelRenderer(this, 69, 42);
        vines2.setRotationPoint(3.0F, -2.0F, 2.0F);
        vines2.addBox(-2.5F, 0.0F, -3.9F, 5, 14, 7, 0.0F);
        mushroomhat2 = new MowzieModelRenderer(this, 16, 52);
        mushroomhat2.setRotationPoint(-0.8F, 1.2F, -2.5F);
        mushroomhat2.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotation(mushroomhat2, 0.045553093477052F, 0.136659280431156F, -0.31869712141416456F);
        body_inner = new MowzieModelRenderer(this, 42, 44);
        body_inner.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_inner.addBox(-4.0F, -1.5F, -0.5F, 8, 9, 5, 0.0F);
        neck = new AdvancedModelRenderer(this, 70, 0);
        neck.setRotationPoint(-0.7F, -7.4F, 0.0F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4, 0.0F);
        setRotation(neck, 0.8196066167365371F, 0.045553093477052F, -0.045553093477052F);
        body_base = new MowzieModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, -15F, 0.0F);
        body_base.addBox(-5.0F, -1.5F, -1.0F, 10, 10, 6, 0.0F);
        toadstool2 = new MowzieModelRenderer(this, 0, 41);
        toadstool2.setRotationPoint(0.0F, -3.0F, 0.0F);
        toadstool2.addBox(-2.5F, 0.0F, -3.0F, 6, 1, 3, 0.0F);
        setRotation(toadstool2, 0.0F, -0.22759093446006054F, 0.0F);
        body_top = new AdvancedModelRenderer(this, 0, 17);
        body_top.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_top.addBox(-6.0F, -8.0F, -2.0F, 12, 8, 8, 0.0F);
        setRotation(body_top, 0.18203784098300857F, 0.136659280431156F, -0.091106186954104F);
        legleft1 = new MowzieModelRenderer(this, 42, 32);
        legleft1.setRotationPoint(3.0F, 8.0F, 2.0F);
        legleft1.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legleft1, -0.18203784098300857F, -0.18203784098300857F, 0.0F);
        mushroomhat = new MowzieModelRenderer(this, 11, 57);
        mushroomhat.setRotationPoint(0.0F, -1.5F, 0.0F);
        mushroomhat.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        setRotation(mushroomhat, 0.045553093477052F, -0.136659280431156F, -0.31869712141416456F);
        jaw = new MowzieModelRenderer(this, 87, 0);
        jaw.setRotationPoint(0.0F, 0.8F, -1.5F);
        jaw.addBox(-3.0F, -1.0F, -7.0F, 6, 2, 7, 0.0F);
        setRotation(jaw, 1.0016444577195458F, 0.0F, 0.0F);
        toadstool1b = new MowzieModelRenderer(this, 25, 34);
        toadstool1b.setRotationPoint(0.0F, 0.0F, 0.0F);
        toadstool1b.addBox(-3.3F, -1.0F, -4.5F, 6, 2, 1, 0.0F);
        legleft2 = new MowzieModelRenderer(this, 55, 32);
        legleft2.setRotationPoint(0.0F, 8.0F, 0.0F);
        legleft2.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legleft2, 0.18203784098300857F, 0.0F, 0.0F);
        head1 = new MowzieModelRenderer(this, 70, 10);
        head1.setRotationPoint(0.0F, -2.0F, 0.5F);
        head1.addBox(-4.0F, -6.0F, -8.5F, 8, 6, 8, 0.0F);
        setRotation(head1, -0.9560913642424937F, 0.045553093477052F, 0.045553093477052F);
        armright = new AdvancedModelRenderer(this, 42, 0);
        armright.setRotationPoint(-7.0F, -6.0F, 2.0F);
        armright.addBox(-0.5F, -1.0F, -1.5F, 2, 16, 3, 0.0F);
        legright1 = new MowzieModelRenderer(this, 42, 20);
        legright1.setRotationPoint(-3.0F, 8.0F, 2.0F);
        legright1.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legright1, -0.18203784098300857F, 0.18203784098300857F, 0.0F);
        mushroomstem = new MowzieModelRenderer(this, 11, 52);
        mushroomstem.setRotationPoint(5.4F, -9.0F, 3.3F);
        mushroomstem.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(mushroomstem, -0.045553093477052F, 0.136659280431156F, 0.31869712141416456F);
        modelCore = new MowzieModelRenderer(this, 0, 0);
        modelCore.setRotationPoint(0F, 15F, 0F);
        modelCore.addBox(0F, 0F, 0F, 0, 0, 0, 0.0F);
        setRotation(modelCore, 0, 0, 0);

        head1.addChild(head2);
        head1.addChild(jaw);
        head1.addChild(toadstool4);
        head1.addChild(brain);

        toadstool1.addChild(toadstool3);
        toadstool2.addChild(toadstool2b);
        toadstool1.addChild(vines1);

        toadstool1.addChild(toadstool1b);
        body_base.addChild(vines2);

        body_base.addChild(body_inner);
        toadstool1.addChild(toadstool2);

        neck.addChild(head1);

        legleft1.addChild(legleft2);
        legright1.addChild(legright2);

        body_base.addChild(body_inner);
        body_inner.addChild(body_top);

        body_top.addChild(armright);
        body_top.addChild(neck);
        body_base.addChild(legleft1);
        body_base.addChild(legright1);

        mushroomstem.addChild(mushroomhat);
        mushroomstem.addChild(mushroomhat2);
        body_top.addChild(mushroomstem);
        body_top.addChild(toadstool1);

        modelCore.addChild(body_base);

        parts = new MowzieModelRenderer[]{body_base, neck, body_top, toadstool1, mushroomstem, armright, legright1, legleft1, body_inner, vines2, toadstool1b, toadstool2, toadstool3, vines1, toadstool2b, mushroomhat, mushroomhat2, legright2, legleft2, head1, head2, jaw, toadstool4, brain, modelCore};
        setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        EntitySwampHag hag = (EntitySwampHag) entity;
        //neck.render(unitPixel);
        //legleft1.render(unitPixel);
        //legright1.render(unitPixel);
//		GL11.glPushMatrix();
//		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.15F, 0F);
//		//mushroomstem.render(unitPixel);
//		GL11.glPopMatrix();
//		GL11.glPushMatrix();
//		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.2F, -hag.breatheFloat* 0.35F);
//		//toadstool1.render(unitPixel);
//		GL11.glPopMatrix();
//		GL11.glPushMatrix();
//		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.05F, -hag.breatheFloat* 0.25F);
//		GL11.glScalef(1.F, 1.F + hag.breatheFloat * 0.2F, 1.F + hag.breatheFloat);
//		//body_top.render(unitPixel);
//        GL11.glPopMatrix();
//        body_base.render(unitPixel);
        modelCore.render(unitPixel);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        EntitySwampHag hag = (EntitySwampHag) entity;

        limbSwingAngle = Math.min(limbSwingAngle, 0.25F);
        
        jaw.rotateAngleX = hag.jawFloat;
//		head2.rotateAngleX = hag.jawFloat - 0.8196066167365371F;
        head2.rotateAngleX = -0.8196066167365371F;

        head1.rotateAngleY = rotationYaw / (180F / (float) Math.PI) - 0.045553093477052F;
        head1.rotateAngleX = rotationPitch / (180F / (float) Math.PI) - 0.8196066167365371F;
        head1.rotateAngleZ = rotationPitch / (180F / (float) Math.PI) + 0.045553093477052F;
//        if (hag.getAttackTarget() != null) { // TODO make this work after some zzzzzzzzzzzz
//            armright.rotateAngleX += -((float) Math.PI / 2.5F);
//        }
//		else {
//			armright.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAngle;
//			armright.rotateAngleZ = hag.breatheFloat* 0.5F;
//		}

//		legright1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle -0.18203784098300857F;
//		legleft1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAngle -0.18203784098300857F;
//		legright1.rotateAngleY = 0.0F;
//		legleft1.rotateAngleY = 0.0F;

//        limbSwing = hag.ticksExisted;
//        limbSwingAngle = 0.4F;

        float globalSpeed = 1.7f;
        float globalDegree = 1.8f;
        float legDegree = 1.8f;
        float inverseFrequency = 4 / (globalSpeed);
        float limpSwing = (float) ((-(inverseFrequency * Math.sin((2 * limbSwing - 1) / inverseFrequency) - 4 * limbSwing + Math.PI) / 4) - 0.5 * Math.PI);

        limbSwing = limpSwing;

//        bob(modelCore, 1 * globalSpeed, 1 * globalDegree, false, limbSwing, limbSwingAngle);
        walk(body_base, 1F * globalSpeed, 0.1F * globalDegree, true, 0, 0.05F * globalDegree, limbSwing, limbSwingAngle);
        walk(legleft1, 1F * globalSpeed, 0.1F * globalDegree, false, 0, 0F, limbSwing, limbSwingAngle);
        walk(legright1, 1F * globalSpeed, 0.1F * globalDegree, false, 0, 0F, limbSwing, limbSwingAngle);
        walk(body_top, 1F * globalSpeed, 0.1F * globalDegree, true, -1f, 0.05F * globalDegree, limbSwing, limbSwingAngle);
        walk(neck, 1F * globalSpeed, 0.1F * globalDegree, true, -2f, 0.05F * globalDegree, limbSwing, limbSwingAngle);
        walk(head1, 1F * globalSpeed, 0.1F * globalDegree, true, -3f, -0.15F * globalDegree, limbSwing, limbSwingAngle);

        walk(legleft1, 0.5F * globalSpeed, 0.7F * legDegree, false, 0, 0F, limbSwing, limbSwingAngle);
        walk(legleft2, 0.5F * globalSpeed, 0.8F * legDegree, false, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);

        walk(legright1, 0.5F * globalSpeed, 0.7F * legDegree, true, 0, 0F, limbSwing, limbSwingAngle);
        walk(legright2, 0.5F * globalSpeed, 0.8F * legDegree, true, -2f, 0.6F * legDegree, limbSwing, limbSwingAngle);

        walk(armright, 1 * globalSpeed, 0.2f * globalDegree, true, -1f, -0.3f * globalDegree, limbSwing, limbSwingAngle);
        flap(modelCore, 0.5f * globalSpeed, 0.2f * globalDegree, false, 3f, 0.4f, limbSwing, limbSwingAngle);
        flap(body_top, 0.5f * globalSpeed, 0.1f * globalDegree, false, 1f, 0.1f, limbSwing, limbSwingAngle);
        flap(armright, 0.5f * globalSpeed, 0.2f * globalDegree, true, 0.5f, 0.3f, limbSwing, limbSwingAngle);
        flap(neck, 0.5f * globalSpeed, 0.1f * globalDegree, false, 0.5f, 0.1f, limbSwing, limbSwingAngle);
        flap(head1, 0.5f * globalSpeed, 0.1f * globalDegree, false, 0f, 0.1f, limbSwing, limbSwingAngle);
        body_base.rotationPointX -= Math.cos((limbSwing - 3) * 0.5 * globalSpeed) * limbSwingAngle;
        
        if (this.isRiding) {
        	armright.rotateAngleX += -((float)Math.PI / 5F);
            legright1.rotateAngleX = -1.4137167F;
            legright1.rotateAngleY = ((float)Math.PI / 10F);
            legright1.rotateAngleZ = 0.07853982F;
            legleft1.rotateAngleX = -1.4137167F;
            legleft1.rotateAngleY = -((float)Math.PI / 10F);
            legleft1.rotateAngleZ = -0.07853982F;
        }
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float p_78086_2_, float p_78086_3_, float partialRenderTicks) {
        super.setLivingAnimations(entity, p_78086_2_, p_78086_3_, partialRenderTicks);
        setToInitPose();

        float frame = entity.ticksExisted + partialRenderTicks;
        body_top.setScaleX((float) (1 + 0.08 * Math.sin(frame * 0.07)));
        body_top.setScaleY((float) (1 + 0.08 * Math.sin(frame * 0.07)));
        body_top.setScaleZ((float) (1 + 0.08 * Math.sin(frame * 0.07)));

        neck.setScaleX(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
        neck.setScaleY(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
        neck.setScaleZ(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));

        armright.setScaleX(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
        armright.setScaleY(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));
        armright.setScaleZ(1 / (float) (1 + 0.08 * Math.sin(frame * 0.07)));

        walk(body_top, 0.07f, 0.05f, false, 1, 0, frame, 1);
        walk(neck, 0.07f, 0.05f, false, 0.5f, 0, frame, 1);
        walk(head1, 0.07f, 0.05f, false, 0f, 0, frame, 1);
        walk(armright, 0.07f, 0.1f, false, 0.5f, -0.1f, frame, 1);
        flap(armright, 0.07f, 0.1f, true, 0.5f, 0.15f, frame, 1);
    }
}
