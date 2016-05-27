package thebetweenlands.client.render.render.entity.renderfactory;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.render.entity.render.RenderLurker;
import thebetweenlands.common.entity.mobs.EntityLurker;

@SideOnly(Side.CLIENT)
public class RenderFactoryLurker implements IRenderFactory<EntityLurker> {
    @Override
    public Render<? super EntityLurker> createRenderFor(RenderManager manager) {
        return new RenderLurker(manager);
    }
}
