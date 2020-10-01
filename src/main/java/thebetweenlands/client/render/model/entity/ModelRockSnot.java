package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityRockSnot;

@SideOnly(Side.CLIENT)
public class ModelRockSnot extends ModelBase {
    public ModelRenderer right_mid;
    public ModelRenderer left_mid;
    public ModelRenderer right_back;
    public ModelRenderer right_front;
    public ModelRenderer right_mid_back;
    public ModelRenderer right_mid_front;
    public ModelRenderer left_back;
    public ModelRenderer left_front;
    public ModelRenderer left_mid_back;
    public ModelRenderer left_mid_front;

    public ModelRockSnot() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.right_mid_front = new ModelRenderer(this, 18, 11);
        this.right_mid_front.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.right_mid_front.addBox(-3.0F, -4.0F, -1.5F, 6, 7, 3, 0.0F);
        this.left_mid_front = new ModelRenderer(this, 34, 24);
        this.left_mid_front.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.left_mid_front.addBox(-3.0F, -4.0F, -1.5F, 6, 7, 3, 0.0F);
        this.left_mid_back = new ModelRenderer(this, 16, 21);
        this.left_mid_back.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.left_mid_back.addBox(-3.0F, -4.0F, -1.5F, 6, 7, 3, 0.0F);
        this.left_front = new ModelRenderer(this, 0, 21);
        this.left_front.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.left_front.addBox(-3.0F, -3.0F, -1.5F, 5, 6, 3, 0.0F);
        this.right_mid_back = new ModelRenderer(this, 0, 11);
        this.right_mid_back.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.right_mid_back.addBox(-3.0F, -4.0F, -1.5F, 6, 7, 3, 0.0F);
        this.right_mid = new ModelRenderer(this, 0, 0);
        this.right_mid.setRotationPoint(-3.0F, 21.0F, 0.0F);
        this.right_mid.addBox(-2.0F, -5.0F, -1.5F, 5, 8, 3, 0.0F);
        this.right_front = new ModelRenderer(this, 45, 6);
        this.right_front.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.right_front.addBox(-2.0F, -3.0F, -1.5F, 5, 6, 3, 0.0F);
        this.left_mid = new ModelRenderer(this, 16, 0);
        this.left_mid.setRotationPoint(3.0F, 21.0F, 0.0F);
        this.left_mid.addBox(-3.0F, -5.0F, -1.5F, 5, 8, 3, 0.0F);
        this.right_back = new ModelRenderer(this, 32, 0);
        this.right_back.setRotationPoint(0.0F, 0.0F, 6.0F);
        this.right_back.addBox(-2.0F, -3.0F, -1.5F, 5, 6, 3, 0.0F);
        this.left_back = new ModelRenderer(this, 36, 15);
        this.left_back.setRotationPoint(0.0F, 0.0F, 6.0F);
        this.left_back.addBox(-3.0F, -3.0F, -1.5F, 5, 6, 3, 0.0F);
        this.right_mid.addChild(this.right_mid_front);
        this.left_mid.addChild(this.left_mid_front);
        this.left_mid.addChild(this.left_mid_back);
        this.left_mid.addChild(this.left_front);
        this.right_mid.addChild(this.right_mid_back);
        this.right_mid.addChild(this.right_front);
        this.right_mid.addChild(this.right_back);
        this.left_mid.addChild(this.left_back);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.right_mid.render(f5);
        this.left_mid.render(f5);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntityRockSnot snot = (EntityRockSnot ) entity;
		right_mid.rotateAngleZ = 0F + -convertDegtoRad(snot.getJawAngle());
		left_mid.rotateAngleZ = 0F + convertDegtoRad(snot.getJawAngle());
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
