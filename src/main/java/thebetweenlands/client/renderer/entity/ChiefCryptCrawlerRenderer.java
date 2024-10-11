package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BipedCryptCrawlerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.ChiefCryptCrawler;

public class ChiefCryptCrawlerRenderer extends MobRenderer<ChiefCryptCrawler, BipedCryptCrawlerModel<ChiefCryptCrawler>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/crypt_crawler.png");

	public ChiefCryptCrawlerRenderer(EntityRendererProvider.Context context) {
		super(context, new BipedCryptCrawlerModel<>(context.bakeLayer(BLModelLayers.BIPED_CRYPT_CRAWLER)), 0.5F);
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	protected void scale(ChiefCryptCrawler entity, PoseStack stack, float partialTick) {
		stack.scale(1.35F, 1.35F, 1.35F);
	}

	@Override
	public ResourceLocation getTextureLocation(ChiefCryptCrawler entity) {
		return TEXTURE;
	}
}
