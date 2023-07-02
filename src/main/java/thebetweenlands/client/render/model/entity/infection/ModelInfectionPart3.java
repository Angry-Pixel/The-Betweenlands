package thebetweenlands.client.render.model.entity.infection;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelInfectionPart3 extends ModelBase {
	private final ModelRenderer base;

	public ModelInfectionPart3() {
		textureWidth = 32;
		textureHeight = 32;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 13, -2.5F, -7.0F, 0.0F, 5, 7, 0, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, 0.0F, -7.0F, -2.5F, 0, 7, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		base.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}