package thebetweenlands.client.rendering.entitys;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.client.rendering.entitys.layers.SwampHagEyesLayer;
import thebetweenlands.client.rendering.model.entity.ModelSwampHag;
import thebetweenlands.common.entitys.EntitySwampHag;

public class RenderSwampHag<T extends EntitySwampHag> extends MobRenderer<T, ModelSwampHag<T>> {

	private static final ResourceLocation SWAMP_HAG_TEXTURE = new ResourceLocation(TheBetweenlands.ID, "textures/entity/swamp_hag.png");
	public static final ModelLayerLocation SWAMP_HAG_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(TheBetweenlands.ID, "main"), "swamp_hag");
	
	public RenderSwampHag(EntityRendererProvider.Context p_174401_) {
		this(p_174401_, SWAMP_HAG_MODEL_LAYER);
		this.shadowRadius = 0.5F;
	}
	
	public RenderSwampHag(EntityRendererProvider.Context p_174403_, ModelLayerLocation p_174404_) {
	      super(p_174403_, new ModelSwampHag<>(p_174403_.bakeLayer(p_174404_)), 0.8F);
	      this.addLayer(new SwampHagEyesLayer<>(this));
	   }
	
	@Override
	public void render(T p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_,
			int p_115460_) {
		super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
	}

	public void scale(T ent, PoseStack p_115984_, float p_115985_){
		p_115984_.scale(0.74F, 0.74F, 0.74F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntitySwampHag p_114482_) {
		return SWAMP_HAG_TEXTURE;
	}
}
