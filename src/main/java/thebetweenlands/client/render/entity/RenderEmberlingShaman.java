package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelEmberlingShaman;
import thebetweenlands.common.entity.mobs.EntityEmberlingShaman;

@SideOnly(Side.CLIENT)
public class RenderEmberlingShaman extends RenderLiving<EntityEmberlingShaman> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/emberling.png");

	public RenderEmberlingShaman(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelEmberlingShaman(), 0.5F);
        addLayer(new LayerOverlay<EntityEmberlingShaman>(this, new ResourceLocation("thebetweenlands:textures/entity/emberling_glow.png")).setGlow(true));
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityEmberlingShaman entity) {
		return TEXTURE;
	}
}