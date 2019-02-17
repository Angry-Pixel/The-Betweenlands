package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderElixir extends RenderSnowball<EntityElixir> {

    public RenderElixir(RenderManager renderManagerIn) {
        super(renderManagerIn, ItemRegistry.ELIXIR, Minecraft.getInstance().getRenderItem());
    }

    @Override
    public ItemStack getStackToRender(EntityElixir entityIn) {
        return entityIn.getElixirStack();
    }
}
