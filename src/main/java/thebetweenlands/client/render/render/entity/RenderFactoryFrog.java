package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityFrog;

@SideOnly(Side.CLIENT)
public class RenderFactoryFrog implements IRenderFactory<EntityFrog> {
    @Override
    public Render<? super EntityFrog> createRenderFor(RenderManager manager) {
        return new RenderFrog(manager);
    }
}
