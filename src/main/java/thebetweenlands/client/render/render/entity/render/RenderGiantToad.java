package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.entity.ModelGiantToad;
import thebetweenlands.common.entity.mobs.EntityGiantToad;

@SideOnly(Side.CLIENT)
public class RenderGiantToad extends RenderLiving<EntityGiantToad> {
    private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/giantToad.png");

    public RenderGiantToad(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelGiantToad(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGiantToad entity) {
        return texture;
    }
}
