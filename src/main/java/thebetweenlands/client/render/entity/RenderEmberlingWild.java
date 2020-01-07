package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelEmberlingWild;
import thebetweenlands.common.entity.mobs.EntityEmberlingWild;

@SideOnly(Side.CLIENT)
public class RenderEmberlingWild extends RenderLiving<EntityEmberlingWild> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/emberling.png");

	public RenderEmberlingWild(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEmberlingWild(), 0.5F);
        addLayer(new LayerOverlay<EntityEmberlingWild>(this, new ResourceLocation("thebetweenlands:textures/entity/emberling_glow.png")).setGlow(true));
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityEmberlingWild entity) {
		return TEXTURE;
	}
}