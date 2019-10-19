package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityEmberlingWild;

@SideOnly(Side.CLIENT)
public class ModelEmberlingWild extends ModelBase {
    ModelRenderer body3;
    ModelRenderer body4;
    ModelRenderer hindleg_left1;
    ModelRenderer hindleg_right1;
    ModelRenderer body2;
    ModelRenderer body5;
    ModelRenderer tail1;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer tail5;
    ModelRenderer tail6;
    ModelRenderer tail7;
    ModelRenderer tailfin;
    ModelRenderer hindleg_left2;
    ModelRenderer hindleg_right2;
    ModelRenderer body_base;
    ModelRenderer neckpiece;
    ModelRenderer arm_left1;
    ModelRenderer arm_right1;
    ModelRenderer sidefin_left;
    ModelRenderer sidefin_right;
    ModelRenderer head_main;
    ModelRenderer jaw_connection;
    ModelRenderer gillthingy_left_upper1;
    ModelRenderer gillthingy_left_lower1;
    ModelRenderer gillthingy_right_upper1;
    ModelRenderer gillthingy_right_lower1;
    ModelRenderer jaw_lower;
    ModelRenderer gillthingy_left_upper2;
    ModelRenderer gillthingy_left_lower2;
    ModelRenderer gillthingy_right_upper2;
    ModelRenderer gillthingy_right_lower2;
    ModelRenderer arm_left2;
    ModelRenderer arm_right2;

    public ModelEmberlingWild() {
        textureWidth = 64;
        textureHeight = 64;
        neckpiece = new ModelRenderer(this, 44, 0);
        neckpiece.setRotationPoint(0.0F, 0.0F, -1.0F);
        neckpiece.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2, 0.0F);
        setRotateAngle(neckpiece, 0.18203784098300857F, 0.0F, 0.0F);
        arm_left1 = new ModelRenderer(this, 20, 14);
        arm_left1.setRotationPoint(4.0F, 5.0F, 2.0F);
        arm_left1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(arm_left1, 0.22759093446006054F, -0.091106186954104F, -0.18203784098300857F);
        body3 = new ModelRenderer(this, 0, 25);
        body3.setRotationPoint(0.0F, 15.5F, 3.0F);
        body3.addBox(-3.51F, -3.5F, 0.0F, 7, 7, 3, 0.0F);
        setRotateAngle(body3, -0.22759093446006054F, 0.0F, 0.0F);
        body5 = new ModelRenderer(this, 0, 46);
        body5.setRotationPoint(0.0F, 0.0F, 3.0F);
        body5.addBox(-3.01F, 0.0F, 0.0F, 6, 5, 3, 0.0F);
        setRotateAngle(body5, -0.22759093446006054F, 0.0F, 0.0F);
        jaw_connection = new ModelRenderer(this, 44, 9);
        jaw_connection.setRotationPoint(0.0F, 4.0F, 0.0F);
        jaw_connection.addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2, 0.0F);
        arm_right1 = new ModelRenderer(this, 20, 28);
        arm_right1.setRotationPoint(-4.0F, 5.0F, 2.0F);
        arm_right1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(arm_right1, 0.22759093446006054F, 0.091106186954104F, 0.18203784098300857F);
        tail7 = new ModelRenderer(this, 29, 47);
        tail7.setRotationPoint(0.0F, 0.0F, 4.0F);
        tail7.addBox(-1.02F, -3.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tail7, 0.40980330836826856F, 0.0F, 0.0F);
        arm_left2 = new ModelRenderer(this, 20, 21);
        arm_left2.setRotationPoint(0.0F, 4.0F, 1.0F);
        arm_left2.addBox(-1.01F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        setRotateAngle(arm_left2, -0.31869712141416456F, 0.0F, 0.0F);
        head_main = new ModelRenderer(this, 0, 54);
        head_main.setRotationPoint(0.0F, 0.0F, -2.0F);
        head_main.addBox(-3.0F, 0.0F, -6.0F, 6, 4, 6, 0.0F);
        setRotateAngle(head_main, 0.045553093477052F, 0.0F, 0.0F);
        gillthingy_right_lower1 = new ModelRenderer(this, 44, 33);
        gillthingy_right_lower1.setRotationPoint(-3.0F, 5.0F, -1.0F);
        gillthingy_right_lower1.addBox(0.0F, -3.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(gillthingy_right_lower1, -0.22759093446006054F, -0.8651597102135892F, 0.18203784098300857F);
        tail3 = new ModelRenderer(this, 29, 16);
        tail3.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail3.addBox(-1.5F, -4.0F, 0.0F, 3, 4, 3, 0.0F);
        setRotateAngle(tail3, 0.136659280431156F, 0.0F, 0.0F);
        sidefin_right = new ModelRenderer(this, 49, 50);
        sidefin_right.setRotationPoint(-4.0F, 1.0F, 5.0F);
        sidefin_right.addBox(0.0F, 0.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(sidefin_right, 0.0F, -0.18203784098300857F, 0.0F);
        gillthingy_left_lower1 = new ModelRenderer(this, 44, 29);
        gillthingy_left_lower1.setRotationPoint(3.0F, 5.0F, -1.0F);
        gillthingy_left_lower1.addBox(0.0F, -3.0F, 0.0F, 0, 3, 2, 0.0F);
        setRotateAngle(gillthingy_left_lower1, -0.22759093446006054F, 0.8651597102135892F, -0.18203784098300857F);
        tail1 = new ModelRenderer(this, 29, 0);
        tail1.setRotationPoint(0.0F, 4.0F, 3.0F);
        tail1.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
        setRotateAngle(tail1, 0.136659280431156F, 0.0F, 0.0F);
        hindleg_right1 = new ModelRenderer(this, 42, 46);
        hindleg_right1.setRotationPoint(-3.0F, 1.5F, 2.0F);
        hindleg_right1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(hindleg_right1, -0.2617993877991494F, 0.3490658503988659F, 0.17453292519943295F);
        tailfin = new ModelRenderer(this, 25, 45);
        tailfin.setRotationPoint(0.0F, 0.0F, 0.0F);
        tailfin.addBox(0.0F, -5.0F, -1.0F, 0, 5, 8, 0.0F);
        body4 = new ModelRenderer(this, 0, 36);
        body4.setRotationPoint(0.0F, -3.5F, 3.0F);
        body4.addBox(-3.0F, 0.0F, 0.0F, 6, 6, 3, 0.0F);
        setRotateAngle(body4, -0.18203784098300857F, 0.0F, 0.0F);
        tail2 = new ModelRenderer(this, 29, 8);
        tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail2.addBox(-2.01F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
        setRotateAngle(tail2, 0.136659280431156F, 0.0F, 0.0F);
        gillthingy_left_lower2 = new ModelRenderer(this, 49, 28);
        gillthingy_left_lower2.setRotationPoint(0.0F, 0.0F, 2.0F);
        gillthingy_left_lower2.addBox(0.0F, -3.0F, 0.0F, 0, 3, 3, 0.0F);
        setRotateAngle(gillthingy_left_lower2, 0.0F, 0.31869712141416456F, 0.0F);
        gillthingy_right_upper1 = new ModelRenderer(this, 44, 23);
        gillthingy_right_upper1.setRotationPoint(-3.0F, 2.0F, -1.0F);
        gillthingy_right_upper1.addBox(0.0F, -4.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(gillthingy_right_upper1, 0.27314402793711257F, -0.5918411493512771F, -0.091106186954104F);
        gillthingy_right_upper2 = new ModelRenderer(this, 51, 23);
        gillthingy_right_upper2.setRotationPoint(0.0F, 0.0F, 3.0F);
        gillthingy_right_upper2.addBox(0.0F, -4.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(gillthingy_right_upper2, 0.0F, -0.22759093446006054F, 0.0F);
        sidefin_left = new ModelRenderer(this, 42, 50);
        sidefin_left.setRotationPoint(4.0F, 1.0F, 5.0F);
        sidefin_left.addBox(0.0F, 0.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(sidefin_left, 0.0F, 0.18203784098300857F, 0.0F);
        body_base = new ModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, 1.1F, -7.9F);
        body_base.addBox(-4.0F, 0.0F, -1.0F, 8, 7, 6, 0.0F);
        setRotateAngle(body_base, 0.22759093446006054F, 0.0F, 0.0F);
        gillthingy_left_upper2 = new ModelRenderer(this, 51, 18);
        gillthingy_left_upper2.setRotationPoint(0.0F, 0.0F, 3.0F);
        gillthingy_left_upper2.addBox(0.0F, -4.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(gillthingy_left_upper2, 0.0F, 0.22759093446006054F, 0.0F);
        jaw_lower = new ModelRenderer(this, 44, 14);
        jaw_lower.setRotationPoint(0.0F, 0.0F, -2.0F);
        jaw_lower.addBox(-3.01F, 0.0F, -4.0F, 6, 2, 4, 0.0F);
        setRotateAngle(jaw_lower, 0.40980330836826856F, 0.0F, 0.0F);
        hindleg_right2 = new ModelRenderer(this, 51, 46);
        hindleg_right2.setRotationPoint(0.0F, 4.0F, -1.0F);
        hindleg_right2.addBox(-0.99F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        setRotateAngle(hindleg_right2, 0.7740535232594852F, 0.0F, 0.0F);
        gillthingy_right_lower2 = new ModelRenderer(this, 49, 32);
        gillthingy_right_lower2.setRotationPoint(0.0F, 0.0F, 2.0F);
        gillthingy_right_lower2.addBox(0.0F, -3.0F, 0.0F, 0, 3, 3, 0.0F);
        setRotateAngle(gillthingy_right_lower2, 0.0F, -0.31869712141416456F, 0.0F);
        arm_right2 = new ModelRenderer(this, 20, 35);
        arm_right2.setRotationPoint(0.0F, 4.0F, 1.0F);
        arm_right2.addBox(-0.99F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        setRotateAngle(arm_right2, -0.31869712141416456F, 0.0F, 0.0F);
        body2 = new ModelRenderer(this, 0, 14);
        body2.setRotationPoint(0.0F, -3.5F, 0.0F);
        body2.addBox(-3.5F, 0.0F, -3.0F, 7, 7, 3, 0.0F);
        tail4 = new ModelRenderer(this, 29, 24);
        tail4.setRotationPoint(0.0F, -1.0F, 3.0F);
        tail4.addBox(-1.51F, -3.0F, 0.0F, 3, 3, 3, 0.0F);
        setRotateAngle(tail4, 0.40980330836826856F, 0.0F, 0.0F);
        tail6 = new ModelRenderer(this, 29, 39);
        tail6.setRotationPoint(0.0F, 0.0F, 4.0F);
        tail6.addBox(-1.01F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        tail5 = new ModelRenderer(this, 29, 31);
        tail5.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail5.addBox(-1.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        gillthingy_left_upper1 = new ModelRenderer(this, 44, 18);
        gillthingy_left_upper1.setRotationPoint(3.0F, 2.0F, -1.0F);
        gillthingy_left_upper1.addBox(0.0F, -4.0F, 0.0F, 0, 4, 3, 0.0F);
        setRotateAngle(gillthingy_left_upper1, 0.27314402793711257F, 0.5918411493512771F, 0.091106186954104F);
        hindleg_left1 = new ModelRenderer(this, 42, 39);
        hindleg_left1.setRotationPoint(3.0F, 1.5F, 2.0F);
        hindleg_left1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        setRotateAngle(hindleg_left1, -0.2617993877991494F, -0.3490658503988659F, -0.17453292519943295F);
        hindleg_left2 = new ModelRenderer(this, 51, 39);
        hindleg_left2.setRotationPoint(0.0F, 4.0F, -1.0F);
        hindleg_left2.addBox(-1.01F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
        setRotateAngle(hindleg_left2, 0.7740535232594852F, 0.0F, 0.0F);
        body_base.addChild(neckpiece);
        body_base.addChild(arm_left1);
        body4.addChild(body5);
        head_main.addChild(jaw_connection);
        body_base.addChild(arm_right1);
        tail6.addChild(tail7);
        arm_left1.addChild(arm_left2);
        neckpiece.addChild(head_main);
        head_main.addChild(gillthingy_right_lower1);
        tail2.addChild(tail3);
        body_base.addChild(sidefin_right);
        head_main.addChild(gillthingy_left_lower1);
        body5.addChild(tail1);
        body3.addChild(hindleg_right1);
        tail7.addChild(tailfin);
        body3.addChild(body4);
        tail1.addChild(tail2);
        gillthingy_left_lower1.addChild(gillthingy_left_lower2);
        head_main.addChild(gillthingy_right_upper1);
        gillthingy_right_upper1.addChild(gillthingy_right_upper2);
        body_base.addChild(sidefin_left);
        body2.addChild(body_base);
        gillthingy_left_upper1.addChild(gillthingy_left_upper2);
        jaw_connection.addChild(jaw_lower);
        hindleg_right1.addChild(hindleg_right2);
        gillthingy_right_lower1.addChild(gillthingy_right_lower2);
        arm_right1.addChild(arm_right2);
        body3.addChild(body2);
        tail3.addChild(tail4);
        tail5.addChild(tail6);
        tail4.addChild(tail5);
        head_main.addChild(gillthingy_left_upper1);
        body3.addChild(hindleg_left1);
        hindleg_left1.addChild(hindleg_left2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
    	body3.render(scale);
    }
    
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntityEmberlingWild emberling = (EntityEmberlingWild) entity;
		float heady = MathHelper.sin((rotationYaw / (180F / (float) Math.PI)) * 0.5F);
		float headx = MathHelper.sin((rotationPitch / (180F / (float) Math.PI)) * 0.5F);
		if(emberling.isSitting()) {
			head_main.rotateAngleY =  0F;
			head_main.rotateAngleX = 0F;
		}
		else {
			head_main.rotateAngleY = heady;
			head_main.rotateAngleX = 0.045553093477052F + headx + emberling.animationTicks;
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {
		EntityEmberlingWild emberling = (EntityEmberlingWild) entity;
		float animation = MathHelper.sin(limbSwing * 0.6F) * limbSwingAngle * 0.4F;
		float flap = MathHelper.sin((emberling.ticksExisted + partialRenderTicks) * 0.2F) * 0.8F;
		float headFlap = MathHelper.sin((emberling.ticksExisted + partialRenderTicks) * 0.6F) * 0.7F;
		gillthingy_left_upper1.rotateAngleY = 0.5918411493512771F - flap * 0.125F;
		gillthingy_left_upper2.rotateAngleY = 0.22759093446006054F - flap * 0.25F;

		gillthingy_right_upper1.rotateAngleY = -0.5918411493512771F + flap * 0.125F;
		gillthingy_right_upper2.rotateAngleY = -0.22759093446006054F + flap * 0.25F;

		gillthingy_left_lower1.rotateAngleY = 0.8651597102135892F - flap * 0.125F;
		gillthingy_left_lower2.rotateAngleY = 0.31869712141416456F - flap * 0.25F;

		gillthingy_right_lower1.rotateAngleY = -0.8651597102135892F + flap * 0.125F;
		gillthingy_right_lower2.rotateAngleY = -0.31869712141416456F + flap * 0.25F;
		
		if(emberling.getIsFlameAttacking()) {
			jaw_lower.rotateAngleX = 1F;
			head_main.rotateAngleZ = 0F + headFlap;
		}
		else {
			jaw_lower.rotateAngleX = (!emberling.isSitting() ? 0.40980330836826856F : 0.2F ) + flap * (!emberling.isSitting() ? 0.5F : 0.125F);
			head_main.rotateAngleZ = 0F;
		}
			
        if (emberling.isSitting()) {
        	body3.rotationPointY= 20.5F;
			hindleg_left1.rotateAngleX = -1.4F;
			hindleg_right1.rotateAngleX = -1.4F;
	
			hindleg_left2.rotateAngleX = 0.7740535232594852F;
			hindleg_right2.rotateAngleX = 0.7740535232594852F;
	
			arm_right1.rotateAngleX = -1.1F;
			arm_right1.rotateAngleY = 0.091106186954104F;
			arm_right1.rotateAngleZ = 0.18203784098300857F;
	
			arm_left1.rotateAngleX = -1.1F;
			arm_left1.rotateAngleY = -0.091106186954104F;
			arm_left1.rotateAngleZ = -0.18203784098300857F;
	
			body2.rotateAngleY = 0F;
			body4.rotateAngleY = 0F ;
			
			body2.rotateAngleX = 0F + flap * 0.0125F;
			body4.rotateAngleX = -0.18203784098300857F - flap * 0.025F;
	
		    tail1.rotateAngleY = 0.4F;
		    tail2.rotateAngleY = 0.4F;
		    tail3.rotateAngleY = 0.4F;
		    tail4.rotateAngleY = 0.6F;
		    tail5.rotateAngleY = 0.8F;
		    tail6.rotateAngleY = 0.4F;
		    tail7.rotateAngleY = 0.2F;
		    
		    tail2.rotateAngleZ = 0.3F;
		    tail3.rotateAngleZ = 0.3F;
		    tail4.rotateAngleZ = 0.3F;
        }
        else
        {
        	body3.rotationPointY= 15.5F;
			hindleg_left1.rotateAngleX = -0.2617993877991494F + animation * 2F;
			hindleg_right1.rotateAngleX = -0.2617993877991494F - animation * 2F;
	
			hindleg_left2.rotateAngleX = 0.7740535232594852F;
			hindleg_right2.rotateAngleX = 0.7740535232594852F;
	
			arm_right1.rotateAngleX = 0.22759093446006054F + animation * 2F;
			arm_right1.rotateAngleY = 0.091106186954104F + animation;
			arm_right1.rotateAngleZ = 0.18203784098300857F + emberling.smoothedAngle(partialRenderTicks);
	
			arm_left1.rotateAngleX = 0.22759093446006054F - animation * 2F;
			arm_left1.rotateAngleY = -0.091106186954104F + animation;
			arm_left1.rotateAngleZ = -0.18203784098300857F - emberling.smoothedAngle(partialRenderTicks);
	
			body2.rotateAngleY = 0F + animation * 0.8F + emberling.smoothedAngle(partialRenderTicks) * 0.125F;
			body4.rotateAngleY = 0F - animation * 0.8F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
	
		    tail1.rotateAngleY = 0F + animation * 0.5F - flap * 0.25F + emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail2.rotateAngleY = 0F + animation * 0.5F - flap * 0.25F + emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail3.rotateAngleY = 0F - animation * 0.5F + flap * 0.25F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail4.rotateAngleY = 0F - animation * 0.5F + flap * 0.25F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail5.rotateAngleY = 0F - animation * 0.5F + flap * 0.25F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail6.rotateAngleY = 0F - animation * 0.5F + flap * 0.25F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    tail7.rotateAngleY = 0F - animation * 0.5F + flap * 0.25F - emberling.smoothedAngle(partialRenderTicks) * 0.125F;
		    
		    tail2.rotateAngleZ = 0F;
		    tail3.rotateAngleZ = 0F;
		    tail4.rotateAngleZ = 0F;
        }
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
