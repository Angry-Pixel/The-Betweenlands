package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityShambler;

@SideOnly(Side.CLIENT)
public class ModelShambler extends ModelBase {
    public ModelRenderer body;
    public ModelRenderer tail_1;
    public ModelRenderer left_leg_top;
    public ModelRenderer right_leg_top;
    public ModelRenderer head;
    public ModelRenderer tail_2;
    public ModelRenderer tail_3;
    public ModelRenderer tail_4;
    public ModelRenderer left_leg_2;
    public ModelRenderer left_leg_top_top;
    public ModelRenderer left_leg_3;
    public ModelRenderer left_foot_1;
    public ModelRenderer left_foot_2;
    public ModelRenderer left_foot_3;
    public ModelRenderer left_foot_4;
    public ModelRenderer right_leg_2;
    public ModelRenderer right_leg_top_top;
    public ModelRenderer right_leg_3;
    public ModelRenderer right_foot_1;
    public ModelRenderer right_foot_2;
    public ModelRenderer right_foot_3;
    public ModelRenderer right_foot_4;
    public ModelRenderer maw_left_1;
    public ModelRenderer maw_right_1;
    public ModelRenderer maw_top_1;
    public ModelRenderer maw_bottom_1;
    public ModelRenderer face;
    public ModelRenderer maw_left_2;
    public ModelRenderer maw_right_2;
    public ModelRenderer maw_top_2;
    public ModelRenderer maw_bottom_2;
    public ModelRenderer tongue_end;

    public ModelShambler() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.right_foot_2 = new ModelRenderer(this, 54, 33);
        this.right_foot_2.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.right_foot_2.addBox(-2.5F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(right_foot_2, -1.0471975511965976F, 0.3490658503988659F, 0.17453292519943295F);
        this.maw_top_2 = new ModelRenderer(this, 40, 53);
        this.maw_top_2.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.maw_top_2.addBox(-1.0F, -4.5F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(maw_top_2, 1.5707963267948966F, 0.0F, 0.0F);
        this.maw_top_1 = new ModelRenderer(this, 38, 44);
        this.maw_top_1.setRotationPoint(0.0F, -2.5F, -5.0F);
        this.maw_top_1.addBox(-1.5F, -4.5F, -1.5F, 3, 6, 3, 0.0F);
        this.setRotateAngle(maw_top_1, 1.0471975511965976F, 0.0F, 0.0F);
        this.left_leg_top = new ModelRenderer(this, 29, 10);
        this.left_leg_top.setRotationPoint(3.5F, 2.0F, 0.5F);
        this.left_leg_top.addBox(0.0F, -2.5F, -2.5F, 3, 5, 5, 0.0F);
        this.setRotateAngle(left_leg_top, -0.17453292519943295F, 0.0F, 0.0F);
        this.right_foot_4 = new ModelRenderer(this, 52, 40);
        this.right_foot_4.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.right_foot_4.addBox(0.5F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(right_foot_4, -1.0471975511965976F, -0.3490658503988659F, -0.17453292519943295F);
        this.right_leg_top_top = new ModelRenderer(this, 7, 35);
        this.right_leg_top_top.setRotationPoint(-3.0F, -2.5F, 0.0F);
        this.right_leg_top_top.addBox(0.0F, -4.0F, -2.4F, 2, 4, 5, 0.0F);
        this.setRotateAngle(right_leg_top_top, 0.0F, 0.0F, 1.0471975511965976F);
        this.left_foot_3 = new ModelRenderer(this, 55, 26);
        this.left_foot_3.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.left_foot_3.addBox(-1.0F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(left_foot_3, -1.0471975511965976F, 0.0F, 0.0F);
        this.left_foot_1 = new ModelRenderer(this, 12, 28);
        this.left_foot_1.setRotationPoint(-0.2F, 5.0F, 1.5F);
        this.left_foot_1.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 3, 0.0F);
        this.setRotateAngle(left_foot_1, -1.2217304763960306F, 0.0F, 0.0F);
        this.face = new ModelRenderer(this, 0, 50);
        this.face.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.face.addBox(-3.0F, -3.0F, -8.0F, 6, 6, 1, 0.0F);
        this.setRotateAngle(face, 0.0F, 0.0F, 0.7853981633974483F);
        this.left_foot_2 = new ModelRenderer(this, 21, 14);
        this.left_foot_2.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.left_foot_2.addBox(-2.5F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(left_foot_2, -1.0471975511965976F, 0.3490658503988659F, 0.17453292519943295F);
        this.right_foot_3 = new ModelRenderer(this, 33, 40);
        this.right_foot_3.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.right_foot_3.addBox(-1.0F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(right_foot_3, -1.0471975511965976F, 0.0F, 0.0F);
        this.maw_right_1 = new ModelRenderer(this, 18, 44);
        this.maw_right_1.setRotationPoint(-2.5F, 0.0F, -5.0F);
        this.maw_right_1.addBox(-4.5F, -1.5F, -1.5F, 6, 3, 3, 0.0F);
        this.setRotateAngle(maw_right_1, 0.0F, -1.0471975511965976F, 0.0F);
        this.tail_3 = new ModelRenderer(this, 26, 0);
        this.tail_3.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.tail_3.addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3, 0.0F);
        this.setRotateAngle(tail_3, -0.08726646259971647F, 0.0F, 0.0F);
        this.left_leg_top_top = new ModelRenderer(this, 41, 26);
        this.left_leg_top_top.setRotationPoint(3.0F, -2.5F, 0.1F);
        this.left_leg_top_top.addBox(-2.0F, -4.0F, -2.4F, 2, 4, 5, 0.0F);
        this.setRotateAngle(left_leg_top_top, 0.0F, 0.0F, -1.0471975511965976F);
        this.tail_2 = new ModelRenderer(this, 28, 20);
        this.tail_2.setRotationPoint(0.0F, 0.0F, 2.5F);
        this.tail_2.addBox(-2.5F, -2.0F, -1.0F, 5, 4, 4, 0.0F);
        this.setRotateAngle(tail_2, -0.08726646259971647F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 12.5F, 1.5F);
        this.body.addBox(-4.5F, -3.0F, -3.5F, 9, 6, 8, 0.0F);
        this.setRotateAngle(body, -0.5235987755982988F, 0.0F, 0.0F);
        this.right_leg_3 = new ModelRenderer(this, 21, 35);
        this.right_leg_3.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.right_leg_3.addBox(-1.3F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(right_leg_3, 0.5235987755982988F, 0.0F, 0.0F);
        this.maw_bottom_2 = new ModelRenderer(this, 14, 54);
        this.maw_bottom_2.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.maw_bottom_2.addBox(-1.0F, -0.5F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(maw_bottom_2, -1.5707963267948966F, 0.0F, 0.0F);
        this.left_leg_2 = new ModelRenderer(this, 46, 20);
        this.left_leg_2.setRotationPoint(1.5F, 2.5F, -2.5F);
        this.left_leg_2.addBox(-1.6F, 0.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(left_leg_2, 0.8726646259971648F, 0.0F, 0.0F);
        this.left_foot_4 = new ModelRenderer(this, 26, 28);
        this.left_foot_4.setRotationPoint(0.0F, 2.5F, 0.0F);
        this.left_foot_4.addBox(0.5F, -1.0F, -1.0F, 2, 5, 2, 0.0F);
        this.setRotateAngle(left_foot_4, -1.0471975511965976F, -0.3490658503988659F, -0.17453292519943295F);
        this.maw_bottom_1 = new ModelRenderer(this, 50, 47);
        this.maw_bottom_1.setRotationPoint(0.0F, 2.5F, -5.0F);
        this.maw_bottom_1.addBox(-1.5F, -1.5F, -1.5F, 3, 6, 3, 0.0F);
        this.setRotateAngle(maw_bottom_1, -1.0471975511965976F, 0.0F, 0.0F);
        this.maw_right_2 = new ModelRenderer(this, 26, 52);
        this.maw_right_2.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.maw_right_2.addBox(-4.5F, -1.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(maw_right_2, 0.0F, -1.5707963267948966F, 0.0F);
        this.head = new ModelRenderer(this, 0, 14);
        this.head.setRotationPoint(0.0F, -3.0F, -1.5F);
        this.head.addBox(-3.5F, -3.5F, -7.0F, 7, 7, 7, 0.0F);
        this.setRotateAngle(head, 0.5235987755982988F, 0.0F, 0.0F);
        this.right_leg_top = new ModelRenderer(this, 45, 10);
        this.right_leg_top.setRotationPoint(-3.5F, 2.0F, 0.5F);
        this.right_leg_top.addBox(-3.0F, -2.5F, -2.5F, 3, 5, 5, 0.0F);
        this.setRotateAngle(right_leg_top, -0.17453292519943295F, 0.0F, 0.0F);
        this.maw_left_2 = new ModelRenderer(this, 14, 50);
        this.maw_left_2.setRotationPoint(4.0F, 0.0F, 0.0F);
        this.maw_left_2.addBox(-0.5F, -1.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(maw_left_2, 0.0F, 1.5707963267948966F, 0.0F);
        this.maw_left_1 = new ModelRenderer(this, 0, 44);
        this.maw_left_1.setRotationPoint(2.5F, 0.0F, -5.0F);
        this.maw_left_1.addBox(-1.5F, -1.5F, -1.5F, 6, 3, 3, 0.0F);
        this.setRotateAngle(maw_left_1, 0.0F, 1.0471975511965976F, 0.0F);
        this.right_foot_1 = new ModelRenderer(this, 40, 35);
        this.right_foot_1.setRotationPoint(0.2F, 5.0F, 1.5F);
        this.right_foot_1.addBox(-2.0F, -1.5F, -1.5F, 4, 4, 3, 0.0F);
        this.setRotateAngle(right_foot_1, -1.2217304763960306F, 0.0F, 0.0F);
        this.tail_1 = new ModelRenderer(this, 34, 0);
        this.tail_1.setRotationPoint(0.0F, 0.0F, 2.5F);
        this.tail_1.addBox(-3.5F, -2.5F, -1.0F, 7, 5, 5, 0.0F);
        this.setRotateAngle(tail_1, -0.3490658503988659F, 0.0F, 0.0F);
        this.left_leg_3 = new ModelRenderer(this, 0, 28);
        this.left_leg_3.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.left_leg_3.addBox(-1.7F, 0.0F, 0.0F, 3, 5, 3, 0.0F);
        this.setRotateAngle(left_leg_3, 0.5235987755982988F, 0.0F, 0.0F);
        this.tail_4 = new ModelRenderer(this, 0, 0);
        this.tail_4.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.tail_4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 3, 0.0F);
        this.setRotateAngle(tail_4, 0.4363323129985824F, 0.0F, 0.0F);
        this.right_leg_2 = new ModelRenderer(this, 31, 32);
        this.right_leg_2.setRotationPoint(-1.5F, 2.5F, -2.5F);
        this.right_leg_2.addBox(-1.4F, 0.0F, 0.0F, 3, 3, 3, 0.0F);
        this.setRotateAngle(right_leg_2, 0.8726646259971648F, 0.0F, 0.0F);
        this.tongue_end = new ModelRenderer(this, 0, 57);
        this.tongue_end.setRotationPoint(0.0F, 0.0F, -8.0F);
        this.tongue_end.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F);

        this.right_foot_1.addChild(this.right_foot_2);
        this.maw_top_1.addChild(this.maw_top_2);
        this.head.addChild(this.maw_top_1);
        this.body.addChild(this.left_leg_top);
        this.right_foot_1.addChild(this.right_foot_4);
        this.right_leg_top.addChild(this.right_leg_top_top);
        this.left_foot_1.addChild(this.left_foot_3);
        this.left_leg_3.addChild(this.left_foot_1);
        this.head.addChild(this.face);
        this.left_foot_1.addChild(this.left_foot_2);
        this.right_foot_1.addChild(this.right_foot_3);
        this.head.addChild(this.maw_right_1);
        this.tail_2.addChild(this.tail_3);
        this.left_leg_top.addChild(this.left_leg_top_top);
        this.tail_1.addChild(this.tail_2);
        this.right_leg_2.addChild(this.right_leg_3);
        this.maw_bottom_1.addChild(this.maw_bottom_2);
        this.left_leg_top.addChild(this.left_leg_2);
        this.left_foot_1.addChild(this.left_foot_4);
        this.head.addChild(this.maw_bottom_1);
        this.maw_right_1.addChild(this.maw_right_2);
        this.body.addChild(this.head);
        this.body.addChild(this.right_leg_top);
        this.maw_left_1.addChild(this.maw_left_2);
        this.head.addChild(this.maw_left_1);
        this.right_leg_3.addChild(this.right_foot_1);
        this.body.addChild(this.tail_1);
        this.left_leg_2.addChild(this.left_leg_3);
        this.tail_3.addChild(this.tail_4);
        this.right_leg_top.addChild(this.right_leg_2);
        this.face.addChild(this.tongue_end);
    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ticksExisted, float rotationYaw, float rotationPitch, float scale) {
        this.body.render(scale);
    }

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime ) {
		EntityShambler shambler = (EntityShambler) entity;
		float animation = MathHelper.cos((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F;
		float animation2 = MathHelper.sin((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F;
		float flap = MathHelper.sin((shambler.ticksExisted) * 0.3F) * 0.8F;
		float smoothedAngle = shambler.smoothedAngle(partialTickTime);
		float headX = 0.5235987755982988F + shambler.rotationPitch / (180F / (float) Math.PI);

		left_leg_top.rotateAngleX = -0.17453292519943295F - (animation2 * 14F) + flap * 0.1F;
		right_leg_top.rotateAngleX = -0.17453292519943295F - (animation * 14F) + flap * 0.1F;

		left_leg_2.rotateAngleX = 0.8726646259971648F + (animation2 * 8F) - flap * 0.05F;
		right_leg_2.rotateAngleX = 0.8726646259971648F + (animation * 8F) - flap * 0.05F;

		left_leg_3.rotateAngleX = 0.5235987755982988F + (animation2 * 4F) + flap * 0.05F;
		right_leg_3.rotateAngleX = 0.5235987755982988F + (animation * 4F) + flap * 0.05F;

		left_foot_1.rotateAngleX = -1.2217304763960306F - (animation2 * 2F) + flap * 0.05F;
		right_foot_1.rotateAngleX = -1.2217304763960306F - (animation * 2F) + flap * 0.05F;

	    left_foot_2.rotateAngleX = -1.2217304763960306F - (animation2 * 2F) - flap * 0.075F/ (180F / (float) Math.PI);
	    left_foot_3.rotateAngleX = -1.0471975511965976F - (animation2 * 2F) - flap * 0.075F/ (180F / (float) Math.PI);
	    left_foot_4.rotateAngleX = -1.0471975511965976F - (animation2 * 2F) - flap * 0.075F/ (180F / (float) Math.PI);

	    right_foot_2.rotateAngleX = -1.0471975511965976F - (animation * 2F) - flap * 0.075F/ (180F / (float) Math.PI);
	    right_foot_3.rotateAngleX = -1.0471975511965976F - (animation * 2F) - flap * 0.075F/ (180F / (float) Math.PI);
	    right_foot_4.rotateAngleX = -1.0471975511965976F - (animation * 2F) - flap * 0.075F/ (180F / (float) Math.PI);

		body.rotateAngleX = -0.5235987755982988F - (animation2 * 3F) - flap * 0.05F;
		head.rotateAngleX = headX + (animation2 * 4F) + flap * 0.1F;

		body.rotateAngleZ = 0F - (animation2 * 2F);
		head.rotateAngleZ = 0F + (animation2 * 4F);

		left_leg_top.rotateAngleZ = 0F + (animation2 * 2F);
		right_leg_top.rotateAngleZ = 0F + (animation * 2F);

		tail_1.rotateAngleY = flap * 0.2F;
		tail_2.rotateAngleY = tail_1.rotateAngleY * 1.2F;
		tail_3.rotateAngleY = tail_2.rotateAngleY * 1.4F;

		tail_1.rotateAngleX = 0.06981317007977318F - animation * 1F;
		tail_2.rotateAngleX = 0.10471975511965977F - animation * 3F;
		tail_3.rotateAngleX = 0.03490658503988659F - animation * 4F;

	    maw_left_1.rotateAngleY = 1.0471975511965976F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);
	    maw_left_2.rotateAngleY = 1.5707963267948966F - smoothedAngle / (180F / (float) Math.PI) * 6F + (!shambler.jawsAreOpen() ? 0F : flap * 0.2F);
	    maw_right_1.rotateAngleY = -1.0471975511965976F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);
	    maw_right_2.rotateAngleY = -1.5707963267948966F + smoothedAngle / (180F / (float) Math.PI) * 6F - (!shambler.jawsAreOpen() ? 0F : flap * 0.2F);
	    maw_top_1.rotateAngleX = 1.0471975511965976F - smoothedAngle / (180F / (float) Math.PI) * 3F + (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);
	    maw_top_2.rotateAngleX = 1.5707963267948966F - smoothedAngle / (180F / (float) Math.PI) * 6F + (!shambler.jawsAreOpen() ? 0F : flap * 0.2F);
	    maw_bottom_1.rotateAngleX = -1.0471975511965976F + smoothedAngle / (180F / (float) Math.PI) * 3F - (!shambler.jawsAreOpen() ? 0F : flap * 0.1F);
	    maw_bottom_2.rotateAngleX = -1.5707963267948966F + smoothedAngle / (180F / (float) Math.PI) * 6F - (!shambler.jawsAreOpen() ? 0F : flap * 0.2F);

	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
