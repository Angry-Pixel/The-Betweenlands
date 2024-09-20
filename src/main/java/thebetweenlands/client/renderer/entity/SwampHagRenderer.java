package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.client.model.entity.SwampHagModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.SwampHag;

public class SwampHagRenderer<T extends SwampHag> extends MobRenderer<T, SwampHagModel<T>> {

	private static final ResourceLocation SWAMP_HAG_TEXTURE = TheBetweenlands.prefix("textures/entity/swamp_hag.png");
	private static final RenderType SWAMP_HAG_EYES = RenderType.eyes(TheBetweenlands.prefix("textures/entity/swamp_hag_eyes.png"));
	public static final ModelLayerLocation SWAMP_HAG_MODEL_LAYER = new ModelLayerLocation(TheBetweenlands.prefix("main"), "swamp_hag");

	public SwampHagRenderer(EntityRendererProvider.Context context) {
		super(context, new SwampHagModel<>(context.bakeLayer(SWAMP_HAG_MODEL_LAYER)), 0.8F);
		this.addLayer(new GenericEyesLayer<>(this, SWAMP_HAG_EYES));
	}

	@Override
	public void scale(T entity, PoseStack stack, float partialTicks) {
		stack.scale(0.74F, 0.74F, 0.74F);
	}

	@Override
	public ResourceLocation getTextureLocation(SwampHag entity) {
		return SWAMP_HAG_TEXTURE;
	}
}
