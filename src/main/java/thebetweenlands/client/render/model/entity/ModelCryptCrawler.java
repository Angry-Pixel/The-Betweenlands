package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
@SideOnly(Side.CLIENT)
public class ModelCryptCrawler extends ModelBase {
    ModelRenderer body_main;
    ModelRenderer neck;
    ModelRenderer body_lower;
    ModelRenderer leg_front_left1;
    ModelRenderer leg_front_right1;
    ModelRenderer head1;
    ModelRenderer snout;
    ModelRenderer lowerjaw;
    ModelRenderer ear_left;
    ModelRenderer ear_right;
    ModelRenderer cheekybreeky_left;
    ModelRenderer cheeckybreeky_right;
    ModelRenderer teeth_upper1;
    ModelRenderer teeth_upper2;
    ModelRenderer teethlower;
    ModelRenderer leg_back_left1;
    ModelRenderer leg_back_right1;
    ModelRenderer tail1;
    ModelRenderer tinyurn1;
    ModelRenderer tinyurn2;
    ModelRenderer tinyurn3;
    ModelRenderer leg_back_left2;
    ModelRenderer leg_back_left3;
    ModelRenderer leg_back_right2;
    ModelRenderer leg_back_right3;
    ModelRenderer tail2;
    ModelRenderer tail3;
    ModelRenderer tail4;
    ModelRenderer leg_front_left2;
    ModelRenderer leg_front_left3;
    ModelRenderer leg_front_right2;
    ModelRenderer leg_front_right3;

    public ModelCryptCrawler() {
        textureWidth = 128;
        textureHeight = 64;
        leg_front_right1 = new ModelRenderer(this, 48, 27);
        leg_front_right1.setRotationPoint(-4.0F, 2.0F, -6.0F);
        leg_front_right1.addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_right1, 0.27314402793711257F, 0.0F, 0.136659280431156F);
        tinyurn1 = new ModelRenderer(this, 80, 0);
        tinyurn1.setRotationPoint(2.0F, 6.0F, 3.0F);
        tinyurn1.addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn1, 0.136659280431156F, 0.0F, 0.0F);
        leg_front_left2 = new ModelRenderer(this, 33, 37);
        leg_front_left2.setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_left2.addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_left2, -0.5918411493512771F, 0.0F, 0.0F);
        leg_back_right1 = new ModelRenderer(this, 48, 0);
        leg_back_right1.setRotationPoint(-4.0F, 0.0F, 7.0F);
        leg_back_right1.addBox(-1.0F, 0.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_right1, 0.27314402793711257F, 0.18203784098300857F, 0.0F);
        body_main = new ModelRenderer(this, 0, 0);
        body_main.setRotationPoint(0.0F, 12.0F, 0.0F);
        body_main.addBox(-4.0F, 0.0F, -8.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_main, 0.091106186954104F, 0.0F, 0.0F);
        lowerjaw = new ModelRenderer(this, 22, 55);
        lowerjaw.setRotationPoint(0.0F, 4.0F, -3.8F);
        lowerjaw.addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        setRotateAngle(lowerjaw, -0.091106186954104F, 0.0F, 0.0F);
        leg_back_left3 = new ModelRenderer(this, 33, 21);
        leg_back_left3.setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_left3.addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_left3, 0.8651597102135892F, 0.0F, 0.0F);
        tinyurn2 = new ModelRenderer(this, 80, 6);
        tinyurn2.setRotationPoint(-1.0F, 6.0F, 3.0F);
        tinyurn2.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        setRotateAngle(tinyurn2, 0.136659280431156F, 0.0F, 0.0F);
        leg_back_right3 = new ModelRenderer(this, 48, 21);
        leg_back_right3.setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_right3.addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_right3, 0.8651597102135892F, 0.0F, 0.0F);
        ear_right = new ModelRenderer(this, 26, 43);
        ear_right.setRotationPoint(-3.0F, 0.0F, 0.0F);
        ear_right.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_right, -0.091106186954104F, 0.5462880558742251F, 0.136659280431156F);
        leg_back_right2 = new ModelRenderer(this, 48, 12);
        leg_back_right2.setRotationPoint(0.0F, 7.0F, 1.0F);
        leg_back_right2.addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_right2, -0.7740535232594852F, 0.0F, 0.0F);
        body_lower = new ModelRenderer(this, 0, 15);
        body_lower.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_lower.addBox(-4.01F, 0.0F, 0.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_lower, -0.22759093446006054F, 0.0F, 0.0F);
        teeth_upper1 = new ModelRenderer(this, 27, 60);
        teeth_upper1.setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper1.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper1, -0.136659280431156F, 0.045553093477052F, 0.0F);
        leg_front_right3 = new ModelRenderer(this, 48, 46);
        leg_front_right3.setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_right3.addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_right3, 0.5918411493512771F, 0.0F, 0.0F);
        tinyurn3 = new ModelRenderer(this, 80, 13);
        tinyurn3.setRotationPoint(-4.0F, 4.0F, 3.0F);
        tinyurn3.addBox(-1.3F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn3, 0.136659280431156F, 0.0F, 0.045553093477052F);
        head1 = new ModelRenderer(this, 0, 39);
        head1.setRotationPoint(0.0F, 0.0F, -3.0F);
        head1.addBox(-3.0F, 0.0F, -4.0F, 6, 5, 4, 0.0F);
        setRotateAngle(head1, 0.22759093446006054F, 0.0F, 0.0F);
        tail3 = new ModelRenderer(this, 64, 12);
        tail3.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail3.addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail3, -0.22759093446006054F, 0.0F, 0.0F);
        teethlower = new ModelRenderer(this, 22, 60);
        teethlower.setRotationPoint(0.0F, 0.0F, -3.0F);
        teethlower.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(teethlower, -0.36425021489121656F, 0.0F, 0.0F);
        tail1 = new ModelRenderer(this, 64, 0);
        tail1.setRotationPoint(0.0F, 0.0F, 8.0F);
        tail1.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail1, -0.40980330836826856F, 0.0F, 0.0F);
        tail4 = new ModelRenderer(this, 64, 18);
        tail4.setRotationPoint(0.0F, 2.0F, 3.0F);
        tail4.addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail4, 0.22759093446006054F, 0.0F, 0.0F);
        leg_front_left3 = new ModelRenderer(this, 33, 46);
        leg_front_left3.setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_left3.addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_left3, 0.5918411493512771F, 0.0F, 0.0F);
        snout = new ModelRenderer(this, 0, 49);
        snout.setRotationPoint(0.0F, 0.0F, -4.0F);
        snout.addBox(-2.0F, 0.0F, -3.0F, 4, 2, 3, 0.0F);
        setRotateAngle(snout, 0.045553093477052F, 0.0F, 0.0F);
        leg_front_right2 = new ModelRenderer(this, 48, 37);
        leg_front_right2.setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_right2.addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_right2, -0.5918411493512771F, 0.0F, 0.0F);
        neck = new ModelRenderer(this, 0, 30);
        neck.setRotationPoint(0.0F, 0.0F, -8.0F);
        neck.addBox(-3.01F, 0.0F, -3.0F, 6, 5, 3, 0.0F);
        setRotateAngle(neck, 0.27314402793711257F, 0.0F, 0.0F);
        leg_front_left1 = new ModelRenderer(this, 33, 27);
        leg_front_left1.setRotationPoint(4.0F, 2.0F, -6.0F);
        leg_front_left1.addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_left1, 0.27314402793711257F, 0.0F, -0.136659280431156F);
        tail2 = new ModelRenderer(this, 64, 6);
        tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        tail2.addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail2, -0.22759093446006054F, 0.0F, 0.0F);
        leg_back_left2 = new ModelRenderer(this, 33, 12);
        leg_back_left2.setRotationPoint(0.0F, 7.0F, 1.0F);
        leg_back_left2.addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_left2, -0.7740535232594852F, 0.0F, 0.0F);
        leg_back_left1 = new ModelRenderer(this, 33, 0);
        leg_back_left1.setRotationPoint(4.0F, 0.0F, 7.0F);
        leg_back_left1.addBox(-2.0F, 0.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_left1, 0.27314402793711257F, -0.18203784098300857F, 0.0F);
        cheeckybreeky_right = new ModelRenderer(this, 11, 55);
        cheeckybreeky_right.setRotationPoint(-2.0F, 0.0F, -3.0F);
        cheeckybreeky_right.addBox(0.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheeckybreeky_right, 0.0F, 0.0F, 0.091106186954104F);
        ear_left = new ModelRenderer(this, 21, 43);
        ear_left.setRotationPoint(3.0F, 0.0F, 0.0F);
        ear_left.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_left, -0.091106186954104F, -0.5462880558742251F, -0.136659280431156F);
        cheekybreeky_left = new ModelRenderer(this, 0, 55);
        cheekybreeky_left.setRotationPoint(2.0F, 0.0F, -3.0F);
        cheekybreeky_left.addBox(-2.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheekybreeky_left, 0.0F, 0.0F, -0.091106186954104F);
        teeth_upper2 = new ModelRenderer(this, 30, 60);
        teeth_upper2.setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper2.addBox(0.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper2, -0.136659280431156F, -0.045553093477052F, 0.0F);
        body_main.addChild(leg_front_right1);
        body_lower.addChild(tinyurn1);
        leg_front_left1.addChild(leg_front_left2);
        body_lower.addChild(leg_back_right1);
        head1.addChild(lowerjaw);
        leg_back_left2.addChild(leg_back_left3);
        body_lower.addChild(tinyurn2);
        leg_back_right2.addChild(leg_back_right3);
        head1.addChild(ear_right);
        leg_back_right1.addChild(leg_back_right2);
        body_main.addChild(body_lower);
        snout.addChild(teeth_upper1);
        leg_front_right2.addChild(leg_front_right3);
        body_lower.addChild(tinyurn3);
        neck.addChild(head1);
        tail2.addChild(tail3);
        lowerjaw.addChild(teethlower);
        body_lower.addChild(tail1);
        tail3.addChild(tail4);
        leg_front_left2.addChild(leg_front_left3);
        head1.addChild(snout);
        leg_front_right1.addChild(leg_front_right2);
        body_main.addChild(neck);
        body_main.addChild(leg_front_left1);
        tail1.addChild(tail2);
        leg_back_left1.addChild(leg_back_left2);
        body_lower.addChild(leg_back_left1);
        snout.addChild(cheeckybreeky_right);
        head1.addChild(ear_left);
        snout.addChild(cheekybreeky_left);
        snout.addChild(teeth_upper2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
        body_main.render(scale);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		float heady = MathHelper.sin((rotationYaw / (180F / (float) Math.PI)) * 0.5F);
		neck.rotateAngleY = heady;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {

		EntityCryptCrawler crypt_crawler = (EntityCryptCrawler) entity;
		float animation = MathHelper.sin((limbSwing * 0.6F + 2) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
		float animation2 = MathHelper.sin((limbSwing * 0.6F) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
		float animation3 = MathHelper.sin((limbSwing * 0.6F + 4) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
		float flap = MathHelper.sin((crypt_crawler.ticksExisted) * 0.3F) * 0.8F;
		float standingAngle = crypt_crawler.smoothedAngle(partialRenderTicks); 

		tail1.rotateAngleX = -0.40980330836826856F + (standingAngle * 0.75F)- animation * 1F;
		tail2.rotateAngleX = -0.22759093446006054F + (standingAngle * 0.75F)- animation * 3F;
		tail3.rotateAngleX = -0.22759093446006054F + (standingAngle * 0.75F)- animation * 4F;
		tail4.rotateAngleX = 0.22759093446006054F + (standingAngle * 0.75F)- animation * 5F;

	    tinyurn1.rotateAngleX = 0.136659280431156F + (standingAngle);
	    tinyurn2.rotateAngleX = 0.136659280431156F + (standingAngle);
	    tinyurn3.rotateAngleX = 0.136659280431156F + (standingAngle);

		if (!(crypt_crawler.standingAngle > 0)) {

			leg_front_right1.rotateAngleX = 0.27314402793711257F + (animation2 * 8F) + flap * 0.05F;
			leg_front_right2.rotateAngleX = -0.5918411493512771F + (animation2 * 6F) - flap * 0.025F;
			leg_front_right3.rotateAngleX = 0.5918411493512771F -(standingAngle*1.25F) -0.17453292519943295F - animation2 * 18F + flap * 0.05F;

			leg_front_left1.rotateAngleX = 0.27314402793711257F + (animation * 8F) + flap * 0.05F;
			leg_front_left2.rotateAngleX = -0.5918411493512771F + (animation * 6F) - flap * 0.025F;
			leg_front_left3.rotateAngleX = 0.5918411493512771F -(standingAngle * 1.25F) -0.17453292519943295F - (animation * 18F) + flap * 0.05F;

			leg_back_right1.rotateAngleX = 0.27314402793711257F -(standingAngle * 0.75F) -0.1F - (animation2 * 6F) - flap * 0.05F;
			leg_back_left1.rotateAngleX = 0.27314402793711257F -(standingAngle * 0.75F) -0.1F - (animation3 * 6F) - flap * 0.05F;

			leg_back_right1.rotateAngleY = 0.18203784098300857F - (standingAngle * 0.0625F);
			leg_back_left1.rotateAngleY = -0.18203784098300857F + (standingAngle * 0.0625F);

			leg_back_right2.rotateAngleX = standingAngle -0.7740535232594852F + (leg_back_right1.rotateAngleX + animation2) + flap * 0.1F;
			leg_back_left2.rotateAngleX = standingAngle -0.7740535232594852F + (leg_back_left1.rotateAngleX + animation3) + flap * 0.1F;

			leg_back_right3.rotateAngleX = 0.8651597102135892F -(standingAngle * 0.5F) - (leg_back_right1.rotateAngleX * 1.25F) - flap * 0.05F;
			leg_back_left3.rotateAngleX = 0.8651597102135892F -(standingAngle * 0.5F) - (leg_back_left1.rotateAngleX * 1.25F) - flap * 0.05F;

			body_main.rotateAngleX = 0.091106186954104F - standingAngle - (animation2 * 3F) - flap * 0.025F;
			body_main.rotateAngleZ = 0F - animation2 * 1.5F;

			body_lower.rotateAngleX = -0.22759093446006054F + standingAngle + (animation2 * 2F) + flap * 0.05F;

			neck.rotateAngleX = 0.27314402793711257F -(standingAngle * 0.5F) -0.17453292519943295F + (animation2 * 2.9F) + flap * 0.025F;
			head1.rotateAngleX = -(standingAngle * 0.5F) + 0.17453292519943295F;
			head1.rotateAngleZ = -(standingAngle * 0.1F * flap * 6F);

		} else {
			leg_front_right1.rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation2 * 6F;
			leg_front_left1.rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation * 6F;
			
			leg_front_right2.rotateAngleX = -0.5918411493512771F + animation2 * 5F;
			leg_front_left2.rotateAngleX = -0.5918411493512771F + animation * 5F;

			leg_front_right3.rotateAngleX = 0.5918411493512771F + (standingAngle * 1.25F) -0.17453292519943295F - animation2 * 12F;
			leg_front_left3.rotateAngleX = 0.5918411493512771F + (standingAngle * 1.25F) -0.17453292519943295F - animation * 12F;

			leg_back_right1.rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F) - animation2 * 5F;
			leg_back_left1.rotateAngleX =  0.27314402793711257F + (standingAngle * 0.5F) - animation3 * 5F;

			leg_back_right1.rotateAngleY = 0.18203784098300857F - (standingAngle * 0.1625F);
			leg_back_left1.rotateAngleY = -0.18203784098300857F + (standingAngle * 0.1625F);

			leg_back_right2.rotateAngleX = -0.7740535232594852F + (standingAngle * 1.25F) + (leg_back_right1.rotateAngleX + animation2);
			leg_back_left2.rotateAngleX = -0.7740535232594852F + (standingAngle * 1.25F) + (leg_back_left1.rotateAngleX + animation3);

			leg_back_right3.rotateAngleX = 0.8651597102135892F +(standingAngle * 0.125F) - leg_back_right1.rotateAngleX * 1.25F;
			leg_back_left3.rotateAngleX = 0.8651597102135892F +(standingAngle * 0.125F) - leg_back_left1.rotateAngleX * 1.25F;

			body_main.rotateAngleX = 0.091106186954104F - (standingAngle) - animation2 * 2F;

			body_main.rotateAngleZ = (standingAngle * 0.125F * flap);

			body_lower.rotateAngleX = -0.22759093446006054F - (standingAngle * 0.5F)+ animation2 * 2F;

			neck.rotateAngleX = 0.27314402793711257F + (standingAngle * 0.25F) -0.17453292519943295F + animation2 * 2.9F;
			head1.rotateAngleX = 0.17453292519943295F + (standingAngle * 0.5F);
			//head1.rotateAngleZ = -(standingAngle * 0.1F * flap * 6F);
		}
		if (!crypt_crawler.onGround)
			lowerjaw.rotateAngleX = -0.091106186954104F;
		else {
			if(standingAngle > 0)
				lowerjaw.rotateAngleX = 0.091106186954104F + flap * 0.5F;
			else
				lowerjaw.rotateAngleX = -0.091106186954104F + flap * 0.3F;
			}
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}