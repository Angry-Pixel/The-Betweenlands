package thebetweenlands.client.render.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import thebetweenlands.common.entity.mobs.EntityAngler;

public class RenderFactoryAngler implements IRenderFactory<EntityAngler> {
    @Override
    public Render<? super EntityAngler> createRenderFor(RenderManager manager) {
        return new RenderAngler(manager);
    }
}
