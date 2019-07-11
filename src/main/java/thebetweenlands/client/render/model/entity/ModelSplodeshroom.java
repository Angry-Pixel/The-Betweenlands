package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSplodeshroom extends ModelBase {
	ModelRenderer stem1;
	ModelRenderer stem2;
	ModelRenderer hat_edge1;
	ModelRenderer hat_edge2;
	ModelRenderer hat_rim1;
	ModelRenderer hat_edge3;
	ModelRenderer hat_edge4;

	public ModelSplodeshroom() {
		textureWidth = 64;
		textureHeight = 32;
		stem2 = new ModelRenderer(this, 0, 0);
		stem2.setRotationPoint(-1.5F, -4.0F, 0.0F);
		stem2.addBox(0.0F, -10.0F, -1.5F, 3, 10, 3, 0.0F);
		setRotateAngle(stem2, 0.0F, 0.0F, 0.136659280431156F);
		hat_rim1 = new ModelRenderer(this, 26, 0);
		hat_rim1.setRotationPoint(0.0F, -7.0F, 0.0F);
		hat_rim1.addBox(-2.0F, 0.0F, -3.5F, 7, 8, 7, 0.0F);
		hat_edge3 = new ModelRenderer(this, 22, 19);
		hat_edge3.setRotationPoint(0.0F, -10.0F, 0.0F);
		hat_edge3.addBox(-1.0F, -1.0F, -2.5F, 2, 4, 5, 0.0F);
		hat_edge2 = new ModelRenderer(this, 15, 22);
		hat_edge2.setRotationPoint(0.0F, -10.0F, 0.0F);
		hat_edge2.addBox(1.0F, -1.0F, -2.5F, 1, 4, 2, 0.0F);
		hat_edge1 = new ModelRenderer(this, 0, 19);
		hat_edge1.setRotationPoint(0.0F, -10.0F, 0.0F);
		hat_edge1.addBox(2.0F, -1.0F, -2.5F, 2, 4, 5, 0.0F);
		hat_edge4 = new ModelRenderer(this, 37, 22);
		hat_edge4.setRotationPoint(0.0F, -10.0F, 0.0F);
		hat_edge4.addBox(1.0F, -1.0F, 0.5F, 1, 4, 2, 0.0F);
		stem1 = new ModelRenderer(this, 13, 0);
		stem1.setRotationPoint(0.0F, 24.0F, 0.0F);
		stem1.addBox(-1.5F, -4.0F, -1.5F, 3, 5, 3, 0.0F);
		setRotateAngle(stem1, 0.0F, 0.0F, -0.136659280431156F);
		stem1.addChild(stem2);
	//	stem2.addChild(hat_rim1);
	//	stem2.addChild(hat_edge3);
	//	stem2.addChild(hat_edge2);
	//	stem2.addChild(hat_edge1);
	//	stem2.addChild(hat_edge4);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		render(scale);
	}
	
    public void render(float scale) {
    	stem1.render(scale);
    	//stem2.render(scale);
    	hat_edge1.render(scale);
    	hat_edge2.render(scale);
    	hat_rim1.render(scale);
    	hat_edge3.render(scale);
    	hat_edge4.render(scale);
   }

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
