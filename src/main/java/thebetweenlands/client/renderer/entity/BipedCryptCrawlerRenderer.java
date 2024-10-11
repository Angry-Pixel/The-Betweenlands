package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BipedCryptCrawlerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.BipedCryptCrawler;

public class BipedCryptCrawlerRenderer extends MobRenderer<BipedCryptCrawler, BipedCryptCrawlerModel<BipedCryptCrawler>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/crypt_crawler.png");

	public BipedCryptCrawlerRenderer(EntityRendererProvider.Context context) {
		super(context, new BipedCryptCrawlerModel<>(context.bakeLayer(BLModelLayers.BIPED_CRYPT_CRAWLER)), 0.5F);
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(BipedCryptCrawler entity) {
		return TEXTURE;
	}
}
