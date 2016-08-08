package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnail;

@SideOnly(Side.CLIENT)
public class RenderMireSnail extends RenderLiving<EntityMireSnail> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/mire_snail.png");

    public RenderMireSnail(RenderManager manager) {
        super(manager, new ModelMireSnail(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMireSnail entity) {
        return TEXTURE;
    }
}