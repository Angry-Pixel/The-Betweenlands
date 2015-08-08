package thebetweenlands.client.model.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSpawnerCrystal extends ModelBase {
	private ModelRenderer crystal;

	public ModelSpawnerCrystal() {
		this.textureWidth = 64;
		this.textureHeight = 32;

		this.crystal = new ModelRenderer(this, 0, 0);
		this.crystal.addBox(-16.0F, -16.0F, 0.0F, 16, 16, 16);
		this.crystal.setRotationPoint(0.0F, 32.0F, 0.0F);
		this.crystal.setTextureSize(64, 32);
		this.crystal.mirror = true;
		
		this.setRotation(this.crystal, 0.7071F, 0.0F, 0.7071F);
	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glScaled(0.1D, 0.3D, 0.1D);
		this.crystal.render(0.0625F);
		GL11.glPopMatrix();
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.crystal.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
