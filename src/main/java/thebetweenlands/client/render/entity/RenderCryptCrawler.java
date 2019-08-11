package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerAttachedItems;
import thebetweenlands.client.render.model.entity.ModelCryptCrawler;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;

@SideOnly(Side.CLIENT)
public class RenderCryptCrawler extends RenderLiving<EntityCryptCrawler> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/crypt_crawler.png");

	public RenderCryptCrawler(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelCryptCrawler(), 0.5F);
        
        ModelCryptCrawler model = (ModelCryptCrawler) this.getMainModel();
        
        this.addLayer(new LayerAttachedItems<EntityCryptCrawler>(model)
        		.attach(model.leg_front_left3[1], crawler -> crawler.getHeldItemOffhand(), EnumHandSide.LEFT, 0.75F, attachment -> {
        			attachment.rotationPointY = -2F;
        			attachment.rotationPointZ = -3F;
        		})
        		.attach(model.leg_front_right3[1], crawler -> crawler.getHeldItemMainhand(), EnumHandSide.RIGHT, 0.75F, attachment -> {
        			attachment.rotationPointY = -2F;
        			attachment.rotationPointZ = -3F;
        		})
        		);
    }

	@Override
	protected void preRenderCallback(EntityCryptCrawler entity, float partialTickTime) {
		if(!entity.isBiped())
			GlStateManager.translate(0F, 0F - entity.standingAngle * 0.5F, 0F);
		if(entity.isChief())
			GlStateManager.scale(1.35F, 1.35F, 1.35F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCryptCrawler entity) {
		return TEXTURE;
	}
}