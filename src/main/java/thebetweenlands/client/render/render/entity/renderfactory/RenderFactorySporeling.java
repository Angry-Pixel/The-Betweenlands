package thebetweenlands.client.render.render.entity.renderfactory;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import thebetweenlands.client.render.render.entity.render.RenderSporeling;
import thebetweenlands.common.entity.mobs.EntitySporeling;

public class RenderFactorySporeling implements IRenderFactory<EntitySporeling> {
    @Override
    public Render<? super EntitySporeling> createRenderFor(RenderManager manager) {
        return new RenderSporeling(manager);
    }
}
