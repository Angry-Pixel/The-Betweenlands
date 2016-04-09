package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;

@SideOnly(Side.CLIENT)
public class RenderFactoryMireSnailEgg implements IRenderFactory<EntityMireSnailEgg> {
    @Override
    public Render<? super EntityMireSnailEgg> createRenderFor(RenderManager manager) {
        return new RenderMireSnailEgg(manager);
    }
}
