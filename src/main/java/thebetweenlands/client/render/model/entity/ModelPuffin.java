package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.AnimationBlender;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityPuffin;

@SideOnly(Side.CLIENT)
public class ModelPuffin extends MowzieModelBase {
		MowzieModelRenderer body1;
	    MowzieModelRenderer body2;
	    MowzieModelRenderer neck;
	    MowzieModelRenderer wingleft;
	    MowzieModelRenderer wingright;
	    MowzieModelRenderer body3;
	    MowzieModelRenderer leg_left1;
	    MowzieModelRenderer leg_right1;
	    MowzieModelRenderer tail1;
	    MowzieModelRenderer tailfeather_left1;
	    MowzieModelRenderer tailfeather_right1;
	    MowzieModelRenderer tailfeather_left2;
	    MowzieModelRenderer tailfeather_right2;
	    MowzieModelRenderer leg_left2;
	    MowzieModelRenderer leg_right2;
	    MowzieModelRenderer cutehead;
	    MowzieModelRenderer beak1a;
	    MowzieModelRenderer beak2;
	    MowzieModelRenderer beak1b;
	    MowzieModelRenderer wingleft1;
	    MowzieModelRenderer wingleft2;
	    MowzieModelRenderer wingright1;
	    MowzieModelRenderer wingright2;

	    private MowzieModelRenderer[] parts;
	    
	    public ModelPuffin() {
	        textureWidth = 64;
	        textureHeight = 32;
	        leg_left1 = new MowzieModelRenderer(this, 21, 20);
	        leg_left1.setRotationPoint(1.5F, 3.5F, 0.5F);
	        leg_left1.addBox(-0.5F, -0.3F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_left1, -0.7740535232594852F, -0.18203784098300857F, 0.18203784098300857F);
	        leg_right2 = new MowzieModelRenderer(this, 23, 25);
	        leg_right2.setRotationPoint(0.0F, 2.65F, 0.5F);
	        leg_right2.addBox(-1.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
	        leg_right1 = new MowzieModelRenderer(this, 26, 20);
	        leg_right1.setRotationPoint(-1.5F, 3.5F, 0.5F);
	        leg_right1.addBox(-0.5F, -0.3F, -0.5F, 1, 3, 1, 0.0F);
	        setRotateAngle(leg_right1, -0.7740535232594852F, 0.18203784098300857F, -0.18203784098300857F);
	        tailfeather_right1 = new MowzieModelRenderer(this, 28, 10);
	        tailfeather_right1.setRotationPoint(0.0F, 4.0F, 4.0F);
	        tailfeather_right1.addBox(-3.0F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
	        setRotateAngle(tailfeather_right1, 0.045553093477052F, 0.0F, 0.091106186954104F);
	        beak1a = new MowzieModelRenderer(this, 51, 17);
	        beak1a.setRotationPoint(0.0F, -4.0F, -4.0F);
	        beak1a.addBox(-1.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
	        setRotateAngle(beak1a, 0.22759093446006054F, 0.0F, 0.0F);
	        wingleft = new MowzieModelRenderer(this, 38, 0);
	        wingleft.setRotationPoint(2.5F, 2.5F, 2.5F);
	        wingleft.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
	        setRotateAngle(wingleft, -0.46949356878647464F, 0.0F, 0.0F);
	        wingright = new MowzieModelRenderer(this, 49, 0);
	        wingright.setRotationPoint(-2.5F, 2.5F, 2.5F);
	        wingright.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
	        setRotateAngle(wingright, -0.46949356878647464F, 0.0F, 0.0F);
	        cutehead = new MowzieModelRenderer(this, 31, 24);
	        cutehead.setRotationPoint(0.0F, -3.0F, 3.0F);
	        cutehead.addBox(-2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F);
	        setRotateAngle(cutehead, 0.40980330836826856F, 0.0F, 0.0F);
	        body3 = new MowzieModelRenderer(this, 0, 20);
	        body3.setRotationPoint(0.0F, 4.0F, 0.0F);
	        body3.addBox(-2.5F, 0.0F, 0.0F, 5, 4, 4, 0.0F);
	        setRotateAngle(body3, 0.6373942428283291F, 0.0F, 0.0F);
	        tail1 = new MowzieModelRenderer(this, 21, 0);
	        tail1.setRotationPoint(0.0F, 4.0F, 0.0F);
	        tail1.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
	        leg_left2 = new MowzieModelRenderer(this, 18, 25);
	        leg_left2.setRotationPoint(0.0F, 2.65F, 0.5F);
	        leg_left2.addBox(-1.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
	        tailfeather_left2 = new MowzieModelRenderer(this, 21, 15);
	        tailfeather_left2.setRotationPoint(0.0F, 4.0F, 0.0F);
	        tailfeather_left2.addBox(0.0F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
	        setRotateAngle(tailfeather_left2, 0.136659280431156F, 0.0F, 0.0F);
	        body2 = new MowzieModelRenderer(this, 0, 10);
	        body2.setRotationPoint(0.0F, 4.0F, 0.0F);
	        body2.addBox(-2.51F, 0.0F, 0.0F, 5, 4, 5, 0.0F);
	        setRotateAngle(body2, 0.31869712141416456F, 0.0F, 0.0F);
	        wingright1 = new MowzieModelRenderer(this, 49, 0);
	        wingright1.setRotationPoint(0.0F, 0.0F, 0.0F);
	        wingright1.addBox(-1.0F, -0.5F, -2.0F, 1, 6, 4, 0.0F);
	        setRotateAngle(wingright1, 0.8726646259971648F, 0.13962634015954636F, 0.0F);
	        beak2 = new MowzieModelRenderer(this, 51, 28);
	        beak2.setRotationPoint(0.0F, -1.3F, -4.0F);
	        beak2.addBox(-1.0F, 0.0F, -2.8F, 2, 1, 3, 0.0F);
	        setRotateAngle(beak2, 0.045553093477052F, 0.0F, 0.0F);
	        body1 = new MowzieModelRenderer(this, 0, 0);
	        body1.setRotationPoint(0.0F, 15.5F, -5.5F);
	        body1.addBox(-2.5F, 0.0F, 0.0F, 5, 4, 5, 0.0F);
	        setRotateAngle(body1, 0.4553564018453205F, 0.0F, 0.0F);
	        neck = new MowzieModelRenderer(this, 38, 17);
	        neck.setRotationPoint(0.0F, 0.0F, 0.0F);
	        neck.addBox(-1.5F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
	        setRotateAngle(neck, -0.8651597102135892F, 0.0F, 0.0F);
	        beak1b = new MowzieModelRenderer(this, 51, 23);
	        beak1b.setRotationPoint(0.0F, 0.0F, -3.0F);
	        beak1b.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
	        setRotateAngle(beak1b, -0.22759093446006054F, 0.0F, 0.0F);
	        wingleft1 = new MowzieModelRenderer(this, 38, 0);
	        wingleft1.setRotationPoint(0.0F, 0.0F, 0.0F);
	        wingleft1.addBox(0.0F, -0.5F, -2.0F, 1, 6, 4, 0.0F);
	        setRotateAngle(wingleft1, 0.8726646259971648F, -0.13962634015954636F, 0.0F);
	        wingright2 = new MowzieModelRenderer(this, 49, 11);
	        wingright2.setRotationPoint(0.0F, 5.5F, 2.0F);
	        wingright2.addBox(-1.0F, 0.0F, -4.0F, 1, 2, 3, 0.0F);
	        setRotateAngle(wingright2, -0.136659280431156F, 0.0F, 0.0F);
	        tailfeather_left1 = new MowzieModelRenderer(this, 21, 10);
	        tailfeather_left1.setRotationPoint(0.0F, 4.0F, 4.0F);
	        tailfeather_left1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
	        setRotateAngle(tailfeather_left1, 0.045553093477052F, 0.0F, -0.091106186954104F);
	        wingleft2 = new MowzieModelRenderer(this, 38, 11);
	        wingleft2.setRotationPoint(0.0F, 5.5F, 2.0F);
	        wingleft2.addBox(0.0F, 0.0F, -4.0F, 1, 2, 3, 0.0F);
	        setRotateAngle(wingleft2, -0.136659280431156F, 0.0F, 0.0F);
	        tailfeather_right2 = new MowzieModelRenderer(this, 28, 15);
	        tailfeather_right2.setRotationPoint(0.0F, 4.0F, 0.0F);
	        tailfeather_right2.addBox(-3.0F, 0.0F, 0.0F, 3, 4, 0, 0.0F);
	        setRotateAngle(tailfeather_right2, 0.136659280431156F, 0.0F, 0.0F);
	        body2.addChild(leg_left1);
	        leg_right1.addChild(leg_right2);
	        body2.addChild(leg_right1);
	        body3.addChild(tailfeather_right1);
	        cutehead.addChild(beak1a);
	        body1.addChild(wingleft);
	        body1.addChild(wingright);
	        neck.addChild(cutehead);
	        body2.addChild(body3);
	        body3.addChild(tail1);
	        leg_left1.addChild(leg_left2);
	        tailfeather_left1.addChild(tailfeather_left2);
	        body1.addChild(body2);
	        wingright.addChild(wingright1);
	        cutehead.addChild(beak2);
	        body1.addChild(neck);
	        beak1a.addChild(beak1b);
	        wingleft.addChild(wingleft1);
	        wingright1.addChild(wingright2);
	        body3.addChild(tailfeather_left1);
	        wingleft1.addChild(wingleft2);
	        tailfeather_right1.addChild(tailfeather_right2);

        parts = new MowzieModelRenderer[] {
		body1,
		body2,
		wingleft1,
		wingright1,
		neck,
		body3,
		leg_left1,
		leg_right1,
		tail1,
		tailfeather_left1,
		tailfeather_right1,
		tailfeather_left2,
		tailfeather_right2,
		leg_left2,
		leg_right2,
		wingleft2,
		wingright2,
		cutehead,
		beak1a,
		beak2,
		beak1b,
	    wingleft,
	    wingright
        };

		setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
        body1.render(scale);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {
		EntityPuffin puffin = (EntityPuffin) entity;

        float globalSpeed = 1F;
        float globalDegree = 0.5F;
        float rippleSpeed = 1F;

        float frame = puffin.ticksExisted + partialRenderTicks;
        float flapFrame = puffin.flapTicks + partialRenderTicks;

        AnimationBlender<ModelPuffin> blender = new AnimationBlender<>(this); //TODO Make this a field once animations are done
        
        float landingPercent = puffin.landingTimer.getAnimationProgressSmooth(partialRenderTicks);
        float divingPercent = puffin.divingTimer.getAnimationProgressSmooth(partialRenderTicks);
        float flyingPercent = Math.max(0, 1.0f - divingPercent- landingPercent);
        
        class FlyingPose {
        	public void apply(ModelPuffin model, float globalSpeed, float globalDegree, float rippleSpeed) {
            	model.setToInitPose();
        		//add angles and shit
                 setRotateAngle(body1, 1.0471975511965976F, 0.0F, 0.0F);
                 setRotateAngle(body2, 0.31869712141416456F, 0.0F, 0.0F);
                 setRotateAngle(body3, 0F, 0.0F, 0.0F);
                 setRotateAngle(tailfeather_right1, 0.045553093477052F, 0.0F, 0.08726646259971647F);
                 setRotateAngle(leg_right2, 1.0471975511965976F, 0.0F, 0.0F);
                 setRotateAngle(wingright2, -0.136659280431156F, 0.0F, 0.0F);
                 setRotateAngle(tailfeather_right2, 0.136659280431156F, 0.0F, 0.0F);
                 setRotateAngle(beak1b, -0.22759093446006054F, 0.0F, 0.0F);
                 setRotateAngle(neck, -0.8651597102135892F, 0.0F, 0.0F);
                 setRotateAngle(wingleft2, -0.136659280431156F, 0.0F, 0.0F);
                 setRotateAngle(leg_left2, 1.0471975511965976F, 0.0F, 0.0F);
                 setRotateAngle(beak1a, 0.22759093446006054F, 0.0F, 0.0F);
                 setRotateAngle(beak2, 0.045553093477052F, 0.0F, 0.0F);
                 setRotateAngle(leg_right1, -0.5235987755982988F, 0.18203784098300857F, -0.18203784098300857F);
                 setRotateAngle(wingleft1, -1.2217304763960306F, -1.5707963267948966F, -0.3490658503988659F);
                 setRotateAngle(wingright1, -1.2217304763960306F, 1.5707963267948966F, 0.3490658503988659F);
                 setRotateAngle(leg_left1, -0.5235987755982988F, -0.18203784098300857F, 0.18203784098300857F);
                 setRotateAngle(tailfeather_left2, 0.136659280431156F, 0.0F, 0.0F);
                 setRotateAngle(cutehead, 0.40980330836826856F, 0.0F, 0.0F);
                 setRotateAngle(tailfeather_left1, 0.045553093477052F, 0.0F, -0.08726646259971647F);

                swing(model.wingright1, puffin.flapSpeed, globalDegree * 1.2f, false, 2.0f, 0f, flapFrame, 1F);
                swing(model.wingleft1, puffin.flapSpeed, globalDegree * 1.2f, true, 2.0f, 0f, flapFrame, 1F);

              //  flap(model.wingright1, puffin.flapSpeed, globalDegree * 1.2f, false, 2.0f, 0f, flapFrame, 1F);
              //  flap(model.wingleft1, puffin.flapSpeed, globalDegree * 1.2f, true, 2.0f, 0f, flapFrame, 1F);

              //  walk(model.wingright1, puffin.flapSpeed, globalDegree * 1f, false, 1f, 0f, flapFrame, 1F);
              //  walk(model.wingleft1, puffin.flapSpeed, globalDegree * 1f, false, 1f, 0f, flapFrame, 1F);
        	}
        }
        
        class DivingPose {
        	public void apply(ModelPuffin model, float globalSpeed, float globalDegree, float rippleSpeed) {
            	model.setToInitPose();
            	//add angles and shit
            	setRotateAngle(wingleft2, -0.136659280431156F, 0.0F, 0.0F);
            	setRotateAngle(tailfeather_right2, 0.136659280431156F, 0.0F, 0.0F);
				setRotateAngle(leg_right1, -0.5235987755982988F, 0.18203784098300857F, -0.18203784098300857F);
				setRotateAngle(body2, 0.31869712141416456F, 0.0F, 0.0F);
				setRotateAngle(tailfeather_right1, 0.045553093477052F, 0.0F, 0.091106186954104F);
				setRotateAngle(beak1b, -0.22759093446006054F, 0.0F, 0.0F);
				setRotateAngle(cutehead, 0.091106186954104F, 0.0F, 0.0F);
				setRotateAngle(leg_left1, -0.5235987755982988F, -0.18203784098300857F, 0.18203784098300857F);
				setRotateAngle(tailfeather_left2, 0.136659280431156F, 0.0F, 0.0F);
				setRotateAngle(beak1a, 0.22759093446006054F, 0.0F, 0.0F);
				setRotateAngle(beak2, 0.045553093477052F, 0.0F, 0.0F);
				setRotateAngle(body1, 1.2292353921796064F, 0.0F, 0.0F);
				setRotateAngle(tailfeather_left1, 0.045553093477052F, 0.0F, -0.091106186954104F);
				setRotateAngle(leg_right2, 1.0471975511965976F, 0.0F, 0.0F);
				setRotateAngle(leg_left2, 1.0471975511965976F, 0.0F, 0.0F);
				setRotateAngle(wingleft1, 0.4363323129985824F, -0.13962634015954636F, 0.0F);
				setRotateAngle(neck, -1.0471975511965976F, 0.0F, 0.0F);
				setRotateAngle(wingright1, 0.4363323129985824F, 0.13962634015954636F, 0.0F);
				setRotateAngle(wingright2, -0.136659280431156F, 0.0F, 0.0F);
        	}
        }
        
        FlyingPose flyingPose = new FlyingPose();
        DivingPose divingPose = new DivingPose();
        
        blender.addState(model -> {
        	model.setToInitPose();
        }, () -> landingPercent);


        // Flying animation state
        blender.addState(model -> {
        	model.setToInitPose();
        	flyingPose.apply(model, globalSpeed, globalDegree, rippleSpeed);
        }, () -> flyingPercent);

        //Diving animation state
        blender.addState(model -> {
        	model.setToInitPose();
        	flyingPose.apply(model, 1.5f, 0.25f, 0);
        }, () -> divingPercent);

        blender.setAngles(false);

	}

    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
