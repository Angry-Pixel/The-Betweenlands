package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityGiantToad;

@SideOnly(Side.CLIENT)
public class RenderFactoryGiantToad implements IRenderFactory<EntityGiantToad> {
    @Override
    public Render<? super EntityGiantToad> createRenderFor(RenderManager manager) {
        return new RenderGiantToad(manager);
    }
}
