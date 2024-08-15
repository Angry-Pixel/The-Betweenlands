package thebetweenlands.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.Entity;

public class GenericEyesLayer<T extends Entity, M extends EntityModel<T>> extends EyesLayer<T, M> {

	private final RenderType eyes;

	public GenericEyesLayer(RenderLayerParent<T, M> parent, RenderType eyeType) {
		super(parent);
		this.eyes = eyeType;
	}

	@Override
	public RenderType renderType() {
		return this.eyes;
	}
}
