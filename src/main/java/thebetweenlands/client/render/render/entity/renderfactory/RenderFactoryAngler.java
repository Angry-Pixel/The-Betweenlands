package thebetweenlands.client.render.render.entity.renderfactory;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.render.entity.render.RenderAngler;
import thebetweenlands.common.entity.mobs.EntityAngler;

@SideOnly(Side.CLIENT)
public class RenderFactoryAngler implements IRenderFactory<EntityAngler> {
    @Override
    public Render<? super EntityAngler> createRenderFor(RenderManager manager) {
        return new RenderAngler(manager);
    }
}
