package thebetweenlands.client.render.render.entity.projectile;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;

@SideOnly(Side.CLIENT)
public class RenderFactorySnailPoisonJet implements IRenderFactory<EntitySnailPoisonJet> {
    @Override
    public Render<? super EntitySnailPoisonJet> createRenderFor(RenderManager manager) {
        return new RenderSnailPoisonJet(manager);
    }
}
