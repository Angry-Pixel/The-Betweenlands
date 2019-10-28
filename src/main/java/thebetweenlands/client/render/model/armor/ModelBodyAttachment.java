package thebetweenlands.client.render.model.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelBodyAttachment extends ModelBiped {
	public ModelBodyAttachment() {
		clear(
			bipedHead,
			bipedHeadwear,
			bipedBody,
			bipedRightArm,
			bipedLeftArm,
			bipedRightLeg,
			bipedLeftLeg
		);
	}

	private void clear(ModelRenderer... renderers) {
		for (ModelRenderer renderer : renderers) {
			renderer.cubeList.clear();
		}
	}
}
