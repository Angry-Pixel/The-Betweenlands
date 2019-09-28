package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import thebetweenlands.client.render.model.entity.ModeBipedVolarkite;

public class LayerBipedArmorVolarkite extends LayerBipedArmor {
	public LayerBipedArmorVolarkite(RenderLivingBase<?> rendererIn) {
		super(rendererIn);
	}

	@Override
	protected void initArmor() {
		this.modelLeggings = new ModeBipedVolarkite(0.5F);
		this.modelArmor = new ModeBipedVolarkite(1.0F);
	}
}
