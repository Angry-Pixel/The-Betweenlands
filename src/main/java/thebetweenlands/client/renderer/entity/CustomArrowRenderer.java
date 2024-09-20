package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class CustomArrowRenderer extends ArrowRenderer<AbstractArrow> {

	private final ResourceLocation texture;

	public CustomArrowRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
		super(context);
		this.texture = texture;
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractArrow entity) {
		return this.texture;
	}
}
