package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;

@SideOnly(Side.CLIENT)
public class RenderFactoryBloodSnail implements IRenderFactory<EntityBloodSnail> {
    @Override
    public Render<? super EntityBloodSnail> createRenderFor(RenderManager manager) {
        return new RenderBloodSnail(manager);
    }
}
