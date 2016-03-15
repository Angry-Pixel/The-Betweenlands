package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.mobs.EntityFlyingFiend;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFlyingFiend extends ModelBase {
    ModelRenderer body_base;
    ModelRenderer body_buttpart;
    ModelRenderer neck;
    ModelRenderer arm_left1;
    ModelRenderer arm_right1;
    ModelRenderer leg_left1;
    ModelRenderer leg_right1;
    ModelRenderer lil_tail1;
    ModelRenderer leg_left2;
    ModelRenderer leg_right2;
    ModelRenderer lil_tail2;
    ModelRenderer lil_tail3;
    ModelRenderer head_base;
    ModelRenderer head_connection;
    ModelRenderer head_jaw1;
    ModelRenderer fang_left;
    ModelRenderer fang_right;
    ModelRenderer teeth_upperjaw_left;
    ModelRenderer teeth_upperjaw_right;
    ModelRenderer teeth_upperjaw_front;
    ModelRenderer teeth_lowerjaw_left;
    ModelRenderer teeth_lowerjaw_right;
    ModelRenderer teeth_lowerjaw_front;
    ModelRenderer arm_left2;
    ModelRenderer wing_left1;
    ModelRenderer wing_left2;
    ModelRenderer arm_right2;
    ModelRenderer wing_right1;
    ModelRenderer wing_right2;

    public ModelFlyingFiend() {
        textureWidth = 128;
        textureHeight = 64;
        leg_left1 = new ModelRenderer(this, 46, 0);
        leg_left1.setRotationPoint(1.5F, 2.0F, -1.0F);
        leg_left1.addBox(0.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotation(leg_left1, -2.276432943376204F, -0.5009094953223726F, 0.27314402793711257F);
        head_connection = new ModelRenderer(this, 21, 18);
        head_connection.setRotationPoint(0.0F, 2.0F, 0.0F);
        head_connection.addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2, 0.0F);
        neck = new ModelRenderer(this, 21, 0);   //
        neck.setRotationPoint(0.0F, 0.0F, 2.0F);
        neck.addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3, 0.0F);
        setRotation(neck, -0.7285004297824331F, 0.0F, 0.0F);
        teeth_lowerjaw_right = new ModelRenderer(this, 32, 26);
        teeth_lowerjaw_right.setRotationPoint(-2.5F, -1.0F, -3.0F);
        teeth_lowerjaw_right.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
        setRotation(teeth_lowerjaw_right, 0.0F, 0.0F, -0.22759093446006054F);
        head_base = new ModelRenderer(this, 21, 7);
        head_base.setRotationPoint(0.0F, 0.0F, -3.0F);
        head_base.addBox(-3.0F, -2.0F, -6.0F, 6, 4, 6, 0.0F);
        setRotation(head_base, 0.091106186954104F, 0.0F, 0.0F);
        lil_tail2 = new ModelRenderer(this, 0, 24);
        lil_tail2.setRotationPoint(0.0F, 3.0F, 0.0F);
        lil_tail2.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail2, 0.36425021489121656F, 0.0F, 0.0F);
        teeth_upperjaw_left = new ModelRenderer(this, 21, 39);
        teeth_upperjaw_left.setRotationPoint(3.0F, 2.0F, -3.0F);
        teeth_upperjaw_left.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
        setRotation(teeth_upperjaw_left, 0.0F, 0.0F, -0.091106186954104F);
        body_base = new ModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, 12.0F, 0.0F);
        body_base.addBox(-3.0F, 0.0F, -2.0F, 6, 6, 4, 0.0F);
        setRotation(body_base, 0.5462880558742251F, 0.0F, 0.0F);
        lil_tail3 = new ModelRenderer(this, 0, 30);
        lil_tail3.setRotationPoint(0.0F, 3.0F, 0.0F);
        lil_tail3.addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail3, 0.40980330836826856F, 0.0F, 0.0F);
        wing_right2 = new ModelRenderer(this, 57, 44);
        wing_right2.setRotationPoint(-1.0F, 0.0F, 1.0F);
        wing_right2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
        setRotation(wing_right2, 0.0F, 0.27314402793711257F, 0.0F);
        head_jaw1 = new ModelRenderer(this, 21, 23);
        head_jaw1.setRotationPoint(0.0F, 3.0F, -1.0F);
        head_jaw1.addBox(-2.5F, -1.0F, -5.0F, 5, 2, 5, 0.0F);
        setRotation(head_jaw1, 0.9560913642424937F, 0.0F, 0.0F);
        
        lil_tail1 = new ModelRenderer(this, 0, 18);
        lil_tail1.setRotationPoint(0.0F, 3.0F, -2.0F);
        lil_tail1.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F);
        setRotation(lil_tail1, 0.27314402793711257F, 0.0F, 0.0F);
        leg_right2 = new ModelRenderer(this, 57, 9);
        leg_right2.setRotationPoint(-1.0F, 4.0F, 0.0F);
        leg_right2.addBox(-1.01F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
        setRotation(leg_right2, 2.1855012893472994F, 0.0F, 0.0F);
        fang_left = new ModelRenderer(this, 21, 37);
        fang_left.setRotationPoint(3.0F, 2.0F, -6.0F);
        fang_left.addBox(-1.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotation(fang_left, -0.091106186954104F, 0.0F, -0.091106186954104F);
        
        leg_left2 = new ModelRenderer(this, 46, 9);
        leg_left2.setRotationPoint(1.0F, 4.0F, 0.0F);
        leg_left2.addBox(-0.99F, 0.0F, -1.5F, 2, 7, 2, 0.0F);
        setRotation(leg_left2, 2.1855012893472994F, 0.0F, 0.0F);
        wing_left1 = new ModelRenderer(this, 46, 34);
        wing_left1.setRotationPoint(1.0F, 0.0F, 1.0F);
        wing_left1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
        setRotation(wing_left1, 0.0F, -0.27314402793711257F, 0.0F);
       
        teeth_lowerjaw_left = new ModelRenderer(this, 21, 26);
        teeth_lowerjaw_left.setRotationPoint(2.5F, -1.0F, -3.0F);
        teeth_lowerjaw_left.addBox(0.0F, -2.0F, -2.0F, 0, 2, 5, 0.0F);
        setRotation(teeth_lowerjaw_left, 0.0F, 0.0F, 0.22759093446006054F);
        leg_right1 = new ModelRenderer(this, 57, 0);
        leg_right1.setRotationPoint(-1.5F, 2.0F, -1.0F);
        leg_right1.addBox(-2.0F, -1.0F, -1.5F, 2, 5, 3, 0.0F);
        setRotation(leg_right1, -2.276432943376204F, 0.5009094953223726F, -0.27314402793711257F);
        teeth_lowerjaw_front = new ModelRenderer(this, 21, 34);
        teeth_lowerjaw_front.setRotationPoint(0.0F, -1.0F, -5.0F);
        teeth_lowerjaw_front.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 0, 0.0F);
        setRotation(teeth_lowerjaw_front, 0.136659280431156F, 0.0F, 0.0F);
        wing_right1 = new ModelRenderer(this, 46, 44);
        wing_right1.setRotationPoint(-1.0F, 0.0F, 1.0F);
        wing_right1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 5, 0.0F);
        setRotation(wing_right1, 0.0F, 0.27314402793711257F, 0.0F);
        fang_right = new ModelRenderer(this, 26, 37);
        fang_right.setRotationPoint(-3.0F, 2.0F, -6.0F);
        fang_right.addBox(0.0F, -1.0F, 0.0F, 1, 3, 1, 0.0F);
        setRotation(fang_right, -0.091106186954104F, 0.0F, 0.091106186954104F);
        wing_left2 = new ModelRenderer(this, 57, 34);
        wing_left2.setRotationPoint(1.0F, 0.0F, 1.0F);
        wing_left2.addBox(0.0F, -1.0F, 0.0F, 0, 8, 5, 0.0F);
        setRotation(wing_left2, 0.0F, -0.27314402793711257F, 0.0F);
        teeth_upperjaw_right = new ModelRenderer(this, 27, 39);
        teeth_upperjaw_right.setRotationPoint(-3.0F, 2.0F, -3.0F);
        teeth_upperjaw_right.addBox(0.0F, 0.0F, -2.0F, 0, 1, 3, 0.0F);
        setRotation(teeth_upperjaw_right, 0.0F, 0.0F, 0.091106186954104F);
        teeth_upperjaw_front = new ModelRenderer(this, 21, 44);
        teeth_upperjaw_front.setRotationPoint(0.0F, 2.0F, -6.0F);
        teeth_upperjaw_front.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 0, 0.0F);
        setRotation(teeth_upperjaw_front, -0.091106186954104F, 0.0F, 0.0F);
        body_buttpart = new ModelRenderer(this, 0, 11);
        body_buttpart.setRotationPoint(0.0F, 6.0F, 2.0F);
        body_buttpart.addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3, 0.0F);
        setRotation(body_buttpart, -0.22759093446006054F, 0.0F, 0.0F);
        arm_right1 = new ModelRenderer(this, 46, 29);
        arm_right1.setRotationPoint(-3.0F, 0.0F, 1.0F);
        arm_right1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_right1, -0.8196066167365371F, -0.091106186954104F, 0.5462880558742251F);
        arm_right2 = new ModelRenderer(this, 55, 29);
        arm_right2.setRotationPoint(0.0F, 7.0F, 0.0F);
        arm_right2.addBox(-1.01F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_right2, -0.31869712141416456F, 0.0F, 0.0F);
        arm_left1 = new ModelRenderer(this, 46, 19);
        arm_left1.setRotationPoint(3.0F, 0.0F, 1.0F);
        arm_left1.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_left1, -0.8196066167365371F, 0.091106186954104F, -0.5462880558742251F);
        arm_left2 = new ModelRenderer(this, 55, 19);
        arm_left2.setRotationPoint(0.0F, 7.0F, 0.0F);
        arm_left2.addBox(-0.99F, 0.0F, -1.0F, 2, 7, 2, 0.0F);
        setRotation(arm_left2, -0.31869712141416456F, 0.0F, 0.0F);

        body_buttpart.addChild(leg_left1);
        head_base.addChild(head_connection);
        body_base.addChild(neck);
        head_jaw1.addChild(teeth_lowerjaw_right);
        neck.addChild(head_base);
        lil_tail1.addChild(lil_tail2);
        head_base.addChild(teeth_upperjaw_left);
        lil_tail2.addChild(lil_tail3);
        arm_right2.addChild(wing_right2);
        head_base.addChild(head_jaw1);
        arm_right1.addChild(arm_right2);
        body_buttpart.addChild(lil_tail1);
        leg_right1.addChild(leg_right2);
        head_base.addChild(fang_left);
        arm_left1.addChild(arm_left2);
        leg_left1.addChild(leg_left2);
        arm_left1.addChild(wing_left1);
        body_base.addChild(arm_left1);
        head_jaw1.addChild(teeth_lowerjaw_left);
        body_buttpart.addChild(leg_right1);
        head_jaw1.addChild(teeth_lowerjaw_front);
        arm_right1.addChild(wing_right1);
        head_base.addChild(fang_right);
        arm_left2.addChild(wing_left2);
        head_base.addChild(teeth_upperjaw_right);
        head_base.addChild(teeth_upperjaw_front);
        body_base.addChild(arm_right1);
        body_base.addChild(body_buttpart);
    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		 EntityFlyingFiend fiend = (EntityFlyingFiend) entity;
		 GL11.glPushMatrix();
	        if (fiend.getIsHanging()) {
	            GL11.glTranslatef(0.0F, 2.125F, 0.0F);
	        GL11.glRotatef(180, 1F, 0F, 0.0F);
	        }
	        else
	        	GL11.glRotatef(40, 1F, 0F, 0.0F);
	        body_base.render(unitPixel);
	        GL11.glPopMatrix();
    }

    public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityFlyingFiend fiend = (EntityFlyingFiend) entity;
	
		if (fiend.getIsHanging()) {
			arm_right1.rotateAngleZ = 0.5462880558742251F;
			arm_right2.rotateAngleZ = 0F;
			arm_left1.rotateAngleZ = -0.5462880558742251F;
			arm_left2.rotateAngleZ = 0F;
			
			arm_right1.rotateAngleY = 0.0091106186954104F;
			arm_right2.rotateAngleY = 0F;
			arm_left1.rotateAngleY = -0.0091106186954104F;
			arm_left2.rotateAngleY = 0;

			leg_right1.rotateAngleX = -2.276432943376204F;
			leg_left1.rotateAngleX = -2.276432943376204F;

			lil_tail1.rotateAngleX = 0.27314402793711257F;
			lil_tail2.rotateAngleX = 0.36425021489121656F;
			lil_tail3.rotateAngleX = 0.40980330836826856F;
			
			lil_tail1.rotateAngleX = 0.27314402793711257F;
			lil_tail2.rotateAngleX = 0.36425021489121656F;
			lil_tail3.rotateAngleX = 0.40980330836826856F;
			
			head_jaw1.rotateAngleX = 0.9560913642424937F;
			head_base.rotateAngleX = 0.091106186954104F;
		}
		else {
			float flap = MathHelper.sin((fiend.ticksExisted + partialRenderTicks) * 0.5F ) * 0.6F;
			arm_right1.rotateAngleZ = 0.5462880558742251F - flap * 0.5F;
			arm_right2.rotateAngleZ = 0F - flap;
			arm_left1.rotateAngleZ = -0.5462880558742251F + flap * 0.5F;
			arm_left2.rotateAngleZ = 0 + flap;
			
			arm_right1.rotateAngleY = 0.9091106186954104F - flap;
			arm_right2.rotateAngleY = 0F - flap;
			arm_left1.rotateAngleY = -0.9091106186954104F + flap;
			arm_left2.rotateAngleY = 0 + flap;
			
			leg_right1.rotateAngleX = -2.276432943376204F + flap * 0.5F;
			leg_left1.rotateAngleX = -2.276432943376204F + flap * 0.5F;
			
			lil_tail1.rotateAngleX = 0.27314402793711257F + flap * 0.5F;
			lil_tail2.rotateAngleX = 0.36425021489121656F + flap * 0.25F;
			lil_tail3.rotateAngleX = 0.40980330836826856F + flap * 0.125F;
			
			head_jaw1.rotateAngleX = 0.9560913642424937F - flap * 0.5F;
			GL11.glTranslatef(0.0F, 0F - flap * 0.5F, 0.0F);
			head_base.rotateAngleX = -0.698132F;

		}
	}
}
