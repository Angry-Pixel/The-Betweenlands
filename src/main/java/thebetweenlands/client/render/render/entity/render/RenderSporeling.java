package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.models.entity.ModelSporeling;
import thebetweenlands.client.render.render.entity.layer.LayerSporelingEyes;
import thebetweenlands.common.entity.mobs.EntitySporeling;

public class RenderSporeling extends RenderLiving<EntitySporeling> {
    private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/sporeling.png");


    public RenderSporeling(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelSporeling(), 0.3F);
        addLayer(new LayerSporelingEyes<EntitySporeling>(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySporeling entity) {
        return texture;
    }
}
