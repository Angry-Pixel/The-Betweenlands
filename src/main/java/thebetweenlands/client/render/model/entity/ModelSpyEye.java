package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntitySpyEye;

@SideOnly(Side.CLIENT)
public class ModelSpyEye extends ModelBase {
    public ModelRenderer eye;
    public ModelRenderer eyeLidBottom;
    public ModelRenderer eyeLidTop;

    public ModelSpyEye() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.eye = new ModelRenderer(this, 0, 11);
        this.eye.setRotationPoint(0.0F, 12.0F, -6.0F);
        this.eye.addBox(-3.5F, 0.0F, 0.1F, 7, 4, 4, 0.0F);
        this.eyeLidBottom = new ModelRenderer(this, 0, 5);
        this.eyeLidBottom.mirror = true;
        this.eyeLidBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.eyeLidBottom.addBox(-4.5F, 0.0F, -0.9F, 9, 5, 1, 0.0F);
        this.eyeLidTop = new ModelRenderer(this, 0, 0);
        this.eyeLidTop.mirror = true;
        this.eyeLidTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.eyeLidTop.addBox(-3.5F, 0.0F, -1.9F, 7, 4, 1, 0.0F);
        this.eye.addChild(this.eyeLidBottom);
        this.eyeLidBottom.addChild(this.eyeLidTop);
    }

    @Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		eye.render(scale);
	}
    
    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntitySpyEye spy_eye = (EntitySpyEye) entity;
		float open = spy_eye.getOpenCount() * 0.1571F;
		eye.rotateAngleX = 0F - open;
		eye.rotateAngleY = 0F;
		eye.rotateAngleZ = 0F;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
