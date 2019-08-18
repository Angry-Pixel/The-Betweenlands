package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelEmberling;
import thebetweenlands.common.entity.mobs.EntityEmberling;

@SideOnly(Side.CLIENT)
public class RenderEmberling extends RenderLiving<EntityEmberling> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/emberling.png");

	public RenderEmberling(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEmberling(), 0.5F);
        addLayer(new LayerOverlay<EntityEmberling>(this, new ResourceLocation("thebetweenlands:textures/entity/emberling_glow.png")).setGlow(true));
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityEmberling entity) {
		return TEXTURE;
	}
}