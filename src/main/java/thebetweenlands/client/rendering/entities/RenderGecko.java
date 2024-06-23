package thebetweenlands.client.rendering.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.rendering.model.entity.ModelGecko;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.EntityGecko;

public class RenderGecko<T extends EntityGecko> extends MobRenderer<T, ModelGecko<T>> {

	private static final ResourceLocation GECKO_TEXTURE = ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "textures/entity/gecko.png");
	public static final ModelLayerLocation GECKO_MODEL_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "main"), "gecko");

	public RenderGecko(EntityRendererProvider.Context p_174401_) {
		this(p_174401_, GECKO_MODEL_LAYER);
		this.shadowRadius = 0.5F;
	}

	public RenderGecko(EntityRendererProvider.Context p_174403_, ModelLayerLocation p_174404_) {
		super(p_174403_, new ModelGecko<>(p_174403_.bakeLayer(p_174404_)), 0.5F);
	}

	@Override
	public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_,
					   int p_115460_) {
		//p_115458_.scale(0.74F, 0.74F, 0.74F);
		super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGecko p_114482_) {
		return GECKO_TEXTURE;
	}
}
