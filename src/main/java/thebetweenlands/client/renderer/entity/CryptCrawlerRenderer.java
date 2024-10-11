package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.CryptCrawlerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.CryptCrawler;

public class CryptCrawlerRenderer extends MobRenderer<CryptCrawler, CryptCrawlerModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/crypt_crawler.png");

	public CryptCrawlerRenderer(EntityRendererProvider.Context context) {
		super(context, new CryptCrawlerModel(context.bakeLayer(BLModelLayers.CRYPT_CRAWLER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(CryptCrawler entity) {
		return TEXTURE;
	}
}
