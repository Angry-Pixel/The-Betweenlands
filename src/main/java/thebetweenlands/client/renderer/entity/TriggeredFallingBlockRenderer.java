package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.entity.TriggeredFallingBlock;

public class TriggeredFallingBlockRenderer extends EntityRenderer<TriggeredFallingBlock> {

	public TriggeredFallingBlockRenderer(Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(TriggeredFallingBlock entity) {
		return null;
	}
}
