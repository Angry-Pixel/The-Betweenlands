package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.entity.ModelFrog;
import thebetweenlands.common.entity.mobs.EntityFrog;

@SideOnly(Side.CLIENT)
public class RenderFrog extends RenderLiving<EntityFrog> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation("thebetweenlands:textures/entity/frog_0.png"),
            new ResourceLocation("thebetweenlands:textures/entity/frog_1.png"),
            new ResourceLocation("thebetweenlands:textures/entity/frog_2.png"),
            new ResourceLocation("thebetweenlands:textures/entity/frog_3.png"),
            new ResourceLocation("thebetweenlands:textures/entity/frog_poison.png")
    };

    public RenderFrog(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelFrog(), 0.2F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFrog entity) {
        EntityFrog frog = entity;
        switch (frog.getSkin()) {
            case 0:
                return TEXTURES[0];
            case 1:
                return TEXTURES[1];
            case 2:
                return TEXTURES[2];
            case 3:
                return TEXTURES[3];
            case 4:
                return TEXTURES[4];
            default:
                return TEXTURES[0];
        }
    }
}
