package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.mobs.EntitySwampHag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSwampHag extends ModelBase {
    ModelRenderer body_base;
    ModelRenderer neck;
    ModelRenderer body_top;
    ModelRenderer toadstool1;
    ModelRenderer mushroomstem;
    ModelRenderer armright;
    ModelRenderer legright1;
    ModelRenderer legleft1;
    ModelRenderer body_inner;
    ModelRenderer vines2;
    ModelRenderer toadstool1b;
    ModelRenderer toadstool2;
    ModelRenderer toadstool3;
    ModelRenderer vines1;
    ModelRenderer toadstool2b;
    ModelRenderer mushroomhat;
    ModelRenderer mushroomhat2;
    ModelRenderer legright2;
    ModelRenderer legleft2;
    ModelRenderer head1;
    ModelRenderer head2;
    ModelRenderer jaw;
    ModelRenderer toadstool4;
    ModelRenderer brain;

    public ModelSwampHag() {
        textureWidth = 128;
        textureHeight = 64;
        toadstool4 = new ModelRenderer(this, 20, 45);
        toadstool4.setRotationPoint(4.0F, -4.0F, -0.4F);
        toadstool4.addBox(-2.7F, 0.0F, -0.9F, 4, 1, 3, 0.0F);
        setRotation(toadstool4, 0.0F, -2.231054382824351F, 0.0F);
        toadstool3 = new ModelRenderer(this, 0, 46);
        toadstool3.setRotationPoint(-3.0F, 2.0F, 0.0F);
        toadstool3.addBox(0.0F, 0.0F, -3.0F, 4, 1, 4, 0.0F);
        setRotation(toadstool3, 0.0F, 0.22759093446006054F, 0.0F);
        head2 = new ModelRenderer(this, 70, 25);
        head2.setRotationPoint(0.0F, 0.0F, 0.0F);
        head2.addBox(-3.5F, 0.0F, -3.5F, 7, 3, 3, 0.0F);
        toadstool2b = new ModelRenderer(this, 19, 41);
        toadstool2b.setRotationPoint(0.0F, 0.0F, 0.0F);
        toadstool2b.addBox(-1.5F, 0.0F, -4.0F, 4, 1, 1, 0.0F);
        vines1 = new ModelRenderer(this, 0, 47);
        vines1.setRotationPoint(1.5F, 1.0F, -1.5F);
        vines1.addBox(0.0F, 0.0F, -2.5F, 0, 10, 5, 0.0F);
        setRotation(vines1, 0.0F, 1.1383037381507017F, 0.0F);
        toadstool1 = new ModelRenderer(this, 0, 34);
        toadstool1.setRotationPoint(4.5F, -4.0F, 0.0F);
        toadstool1.addBox(-4.3F, -1.0F, -3.5F, 8, 2, 4, 0.0F);
        setRotation(toadstool1, 0.0F, -1.0471975511965976F, 0.0F);
        brain = new ModelRenderer(this, 90, 35);
        brain.setRotationPoint(0.0F, 0.0F, 0.0F);
        brain.addBox(-3.5F, -5.5F, -8.0F, 7, 5, 7, 0.0F);
        legright2 = new ModelRenderer(this, 55, 20);
        legright2.setRotationPoint(0.0F, 8.0F, 0.0F);
        legright2.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legright2, 0.18203784098300857F, 0.0F, 0.0F);
        vines2 = new ModelRenderer(this, 69, 42);
        vines2.setRotationPoint(3.0F, -2.0F, 2.0F);
        vines2.addBox(-2.5F, 0.0F, -3.9F, 5, 14, 7, 0.0F);
        mushroomhat2 = new ModelRenderer(this, 16, 52);
        mushroomhat2.setRotationPoint(-0.8F, 1.2F, -2.5F);
        mushroomhat2.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        setRotation(mushroomhat2, 0.045553093477052F, 0.136659280431156F, -0.31869712141416456F);
        body_inner = new ModelRenderer(this, 42, 44);
        body_inner.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_inner.addBox(-4.0F, -1.5F, -0.5F, 8, 9, 5, 0.0F);
        neck = new ModelRenderer(this, 70, 0);
        neck.setRotationPoint(-0.7F, -7.4F, 0.0F);
        neck.addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4, 0.0F);
        setRotation(neck, 0.8196066167365371F, 0.045553093477052F, -0.045553093477052F);
        body_base = new ModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_base.addBox(-5.0F, -1.5F, -1.0F, 10, 10, 6, 0.0F);
        toadstool2 = new ModelRenderer(this, 0, 41);
        toadstool2.setRotationPoint(0.0F, -3.0F, 0.0F);
        toadstool2.addBox(-2.5F, 0.0F, -3.0F, 6, 1, 3, 0.0F);
        setRotation(toadstool2, 0.0F, -0.22759093446006054F, 0.0F);
        body_top = new ModelRenderer(this, 0, 17);
        body_top.setRotationPoint(0.0F, 0.0F, 0.0F);
        body_top.addBox(-6.0F, -8.0F, -2.0F, 12, 8, 8, 0.0F);
        setRotation(body_top, 0.18203784098300857F, 0.136659280431156F, -0.091106186954104F);
        legleft1 = new ModelRenderer(this, 42, 32);
        legleft1.setRotationPoint(3.0F, 8.0F, 2.0F);
        legleft1.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legleft1, -0.18203784098300857F, -0.18203784098300857F, 0.0F);
        mushroomhat = new ModelRenderer(this, 11, 57);
        mushroomhat.setRotationPoint(0.0F, -1.5F, 0.0F);
        mushroomhat.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        setRotation(mushroomhat, 0.045553093477052F, -0.136659280431156F, -0.31869712141416456F);
        jaw = new ModelRenderer(this, 87, 0);
        jaw.setRotationPoint(0.0F, 0.8F, -1.5F);
        jaw.addBox(-3.0F, -1.0F, -7.0F, 6, 2, 7, 0.0F);
        setRotation(jaw, 1.0016444577195458F, 0.0F, 0.0F);
        toadstool1b = new ModelRenderer(this, 25, 34);
        toadstool1b.setRotationPoint(0.0F, 0.0F, 0.0F);
        toadstool1b.addBox(-3.3F, -1.0F, -4.5F, 6, 2, 1, 0.0F);
        legleft2 = new ModelRenderer(this, 55, 32);
        legleft2.setRotationPoint(0.0F, 8.0F, 0.0F);
        legleft2.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legleft2, 0.18203784098300857F, 0.0F, 0.0F);
        head1 = new ModelRenderer(this, 70, 10);
        head1.setRotationPoint(0.0F, -2.0F, 0.5F);
        head1.addBox(-4.0F, -6.0F, -8.5F, 8, 6, 8, 0.0F);
        setRotation(head1, -0.9560913642424937F, 0.045553093477052F, 0.045553093477052F);
        armright = new ModelRenderer(this, 42, 0);
        armright.setRotationPoint(-7.0F, -6.0F, 2.0F);
        armright.addBox(-0.5F, -1.0F, -1.5F, 2, 16, 3, 0.0F);
        legright1 = new ModelRenderer(this, 42, 20);
        legright1.setRotationPoint(-3.0F, 8.0F, 2.0F);
        legright1.addBox(-1.5F, 0.0F, -1.5F, 3, 8, 3, 0.0F);
        setRotation(legright1, -0.18203784098300857F, 0.18203784098300857F, 0.0F);
        mushroomstem = new ModelRenderer(this, 11, 52);
        mushroomstem.setRotationPoint(5.4F, -9.0F, 3.3F);
        mushroomstem.addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(mushroomstem, -0.045553093477052F, 0.136659280431156F, 0.31869712141416456F);
        
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
        
        body_base.addChild(armright);
        
        mushroomstem.addChild(mushroomhat);
        mushroomstem.addChild(mushroomhat2);

    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntitySwampHag hag = (EntitySwampHag) entity;
		neck.render(unitPixel);
		legleft1.render(unitPixel);
		legright1.render(unitPixel);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.15F, 0F);
		mushroomstem.render(unitPixel);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.2F, -hag.breatheFloat* 0.35F);
		toadstool1.render(unitPixel);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0.F, -hag.breatheFloat * 0.05F, -hag.breatheFloat* 0.25F);
		GL11.glScalef(1.F, 1.F + hag.breatheFloat * 0.2F, 1.F + hag.breatheFloat);
		body_top.render(unitPixel);
        GL11.glPopMatrix();
        body_base.render(unitPixel);
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
		
		jaw.rotateAngleX = hag.jawFloat;
		head2.rotateAngleX = hag.jawFloat - 0.8196066167365371F;

		head1.rotateAngleY = rotationYaw / (180F / (float)Math.PI) - 0.045553093477052F;
		head1.rotateAngleX = rotationPitch / (180F / (float)Math.PI) - 0.8196066167365371F;
		head1.rotateAngleZ = rotationPitch / (180F / (float)Math.PI)  + 0.045553093477052F;
		if (hag.getEntityToAttack() != null) { // TODO make this work after some zzzzzzzzzzzz
			armright.rotateAngleX = -((float) Math.PI / 2F);
			armright.rotateAngleZ = hag.breatheFloat* 0.5F;
		}
		else {
			armright.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAngle;
			armright.rotateAngleZ = hag.breatheFloat* 0.5F;
		}

		legright1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
		legleft1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAngle;
		legright1.rotateAngleY = 0.0F;
		legleft1.rotateAngleY = 0.0F;
	}
}
