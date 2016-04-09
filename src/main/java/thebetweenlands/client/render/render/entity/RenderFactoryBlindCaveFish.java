package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;

@SideOnly(Side.CLIENT)
public class RenderFactoryBlindCaveFish implements IRenderFactory<EntityBlindCaveFish> {
    @Override
    public Render<? super EntityBlindCaveFish> createRenderFor(RenderManager manager) {
        return new RenderBlindCaveFish(manager);
    }
}
