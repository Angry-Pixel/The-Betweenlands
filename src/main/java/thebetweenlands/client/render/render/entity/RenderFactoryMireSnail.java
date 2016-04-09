package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityMireSnail;

@SideOnly(Side.CLIENT)
public class RenderFactoryMireSnail implements IRenderFactory<EntityMireSnail> {
    @Override
    public Render<? super EntityMireSnail> createRenderFor(RenderManager manager) {
        return new RenderMireSnail(manager);
    }
}
