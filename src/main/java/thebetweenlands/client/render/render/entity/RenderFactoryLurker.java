package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import thebetweenlands.common.entity.mobs.EntityLurker;

public class RenderFactoryLurker implements IRenderFactory<EntityLurker> {
    @Override
    public Render<? super EntityLurker> createRenderFor(RenderManager manager) {
        return new RenderLurker(manager);
    }
}
