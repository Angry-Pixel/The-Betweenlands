package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.entity.ModelMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnail;

@SideOnly(Side.CLIENT)
public class RenderMireSnail extends RenderLiving<EntityMireSnail> {
    private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/mireSnail.png");

    public RenderMireSnail(RenderManager manager) {
        super(manager, new ModelMireSnail(), 0.5F);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityMireSnail entity) {
        return texture;
    }
}