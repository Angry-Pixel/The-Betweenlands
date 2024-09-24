package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.model.entity.GeckoModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Gecko;

public class GeckoRenderer<T extends Gecko> extends MobRenderer<T, GeckoModel<T>> {

	private static final ResourceLocation GECKO_TEXTURE = TheBetweenlands.prefix("textures/entity/gecko.png");
	public static final ModelLayerLocation GECKO_MODEL_LAYER = new ModelLayerLocation(TheBetweenlands.prefix("main"), "gecko");

	public GeckoRenderer(EntityRendererProvider.Context context) {
		super(context, new GeckoModel<>(context.bakeLayer(GECKO_MODEL_LAYER)), 0.5F);
	}

	@Override
	protected void scale(T entity, PoseStack stack, float partialTicks) {
		stack.scale(0.74F, 0.74F, 0.74F);
	}

	@Override
	public ResourceLocation getTextureLocation(Gecko gecko) {
		return GECKO_TEXTURE;
	}
}
