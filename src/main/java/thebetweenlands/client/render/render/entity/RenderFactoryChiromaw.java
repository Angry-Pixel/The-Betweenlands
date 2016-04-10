package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromaw;

@SideOnly(Side.CLIENT)
public class RenderFactoryChiromaw implements IRenderFactory<EntityChiromaw> {
    @Override
    public Render<? super EntityChiromaw> createRenderFor(RenderManager manager) {
        return new RenderChiromaw(manager);
    }
}
