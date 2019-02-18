package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSprite;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderElixir extends RenderSprite<EntityElixir> {

    public RenderElixir(RenderManager renderManagerIn) {
        super(renderManagerIn, ItemRegistry.ELIXIR, Minecraft.getInstance().getItemRenderer());
    }

    @Override
    public ItemStack getStackToRender(EntityElixir entityIn) {
        return entityIn.getElixirStack();
    }
}
