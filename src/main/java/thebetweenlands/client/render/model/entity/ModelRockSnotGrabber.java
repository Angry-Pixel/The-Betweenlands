package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityRockSnotTendril;

@SideOnly(Side.CLIENT)
public class ModelRockSnotGrabber extends ModelBase {
    ModelRenderer grabbybit_base;
    ModelRenderer grabber1a;
    ModelRenderer grabber2a;
    ModelRenderer grabber3a;
    ModelRenderer grabber4a;
    ModelRenderer grabber1b;
    ModelRenderer grabber2b;
    ModelRenderer grabber3b;
    ModelRenderer grabber4b;

    public ModelRockSnotGrabber() {
        textureWidth = 32;
        textureHeight = 32;
        grabbybit_base = new ModelRenderer(this, 0, 0);
        grabbybit_base.setRotationPoint(0.0F, 0.0F, 0.0F);
        grabbybit_base.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        grabber1b = new ModelRenderer(this, 0, 8);
        grabber1b.setRotationPoint(0.0F, -2.0F, 0.0F);
        grabber1b.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 0, 0.0F);
        setRotateAngle(grabber1b, -0.8196066167365371F, 0.0F, 0.0F);
        grabber3a = new ModelRenderer(this, 0, 17);
        grabber3a.setRotationPoint(-1.0F, -1.0F, 0.0F);
        grabber3a.addBox(0.0F, -2.0F, -1.0F, 0, 2, 2, 0.0F);
        setRotateAngle(grabber3a, 0.0F, 0.0F, -0.5462880558742251F);
        grabber2a = new ModelRenderer(this, 0, 10);
        grabber2a.setRotationPoint(1.0F, -1.0F, 0.0F);
        grabber2a.addBox(0.0F, -2.0F, -1.0F, 0, 2, 2, 0.0F);
        setRotateAngle(grabber2a, 0.0F, 0.0F, 0.5462880558742251F);
        grabber2b = new ModelRenderer(this, 0, 11);
        grabber2b.setRotationPoint(0.0F, -2.0F, 0.0F);
        grabber2b.addBox(0.0F, -3.0F, -2.0F, 0, 3, 4, 0.0F);
        setRotateAngle(grabber2b, 0.0F, 0.0F, -0.8196066167365371F);
        grabber4b = new ModelRenderer(this, 0, 29);
        grabber4b.setRotationPoint(0.0F, -2.0F, 0.0F);
        grabber4b.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 0, 0.0F);
        setRotateAngle(grabber4b, 0.8196066167365371F, 0.0F, 0.0F);
        grabber1a = new ModelRenderer(this, 0, 5);
        grabber1a.setRotationPoint(0.0F, -1.0F, -1.0F);
        grabber1a.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(grabber1a, 0.5462880558742251F, 0.0F, 0.0F);
        grabber3b = new ModelRenderer(this, 0, 18);
        grabber3b.setRotationPoint(0.0F, -2.0F, 0.0F);
        grabber3b.addBox(0.0F, -3.0F, -2.0F, 0, 3, 4, 0.0F);
        setRotateAngle(grabber3b, 0.0F, 0.0F, 0.8196066167365371F);
        grabber4a = new ModelRenderer(this, 0, 26);
        grabber4a.setRotationPoint(0.0F, -1.0F, 1.0F);
        grabber4a.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(grabber4a, -0.5462880558742251F, 0.0F, 0.0F);
        grabber1a.addChild(grabber1b);
        grabbybit_base.addChild(grabber3a);
        grabbybit_base.addChild(grabber2a);
        grabber2a.addChild(grabber2b);
        grabber4a.addChild(grabber4b);
        grabbybit_base.addChild(grabber1a);
        grabber3a.addChild(grabber3b);
        grabbybit_base.addChild(grabber4a);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
    	setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
    	grabbybit_base.render(scale);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
		EntityRockSnotTendril grabber = (EntityRockSnotTendril) entity;

		grabber1a.rotateAngleX = 0.5462880558742251F + (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 4F) : convertDegtoRad(80F));
		grabber2a.rotateAngleZ = 0.5462880558742251F + (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 4F) : convertDegtoRad(80F));
		grabber3a.rotateAngleZ = -0.5462880558742251F - (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 4F) : convertDegtoRad(80F));
		grabber4a.rotateAngleX = -0.5462880558742251F - (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 4F) : convertDegtoRad(80F));

		grabber1b.rotateAngleX = -0.8196066167365371F + (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 2F) : convertDegtoRad(40F));
		grabber2b.rotateAngleZ = -0.8196066167365371F + (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 2F) : convertDegtoRad(40F));
		grabber3b.rotateAngleZ = 0.8196066167365371F - (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 2F) : convertDegtoRad(40F));
		grabber4b.rotateAngleX = 0.8196066167365371F - (grabber.getExtending() ? convertDegtoRad(grabber.ticksExisted * 2F) : convertDegtoRad(40F));
	}

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
