package thebetweenlands.client.render.render.entity.renderfactory;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.render.entity.render.RenderDragonfly;
import thebetweenlands.common.entity.mobs.EntityDragonfly;

@SideOnly(Side.CLIENT)
public class RenderFactoryDragonfly implements IRenderFactory<EntityDragonfly> {
    @Override
    public Render<? super EntityDragonfly> createRenderFor(RenderManager manager) {
        return new RenderDragonfly(manager);
    }
}
