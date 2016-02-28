package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelSludgeBall;

/**
 * Created by jnad325 on 2/28/16.
 */
public class RenderSludgeBall extends Render {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
    ModelSludgeBall model;

    public RenderSludgeBall() {
        super();
        model = new ModelSludgeBall();
    }

    @Override
    public void doRender(Entity entity, double f, double f1, double f2, float f3, float f4) {
        model.render(entity, (float)f, (float)f1, (float)f2, f3, f4, 0.0625f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return texture;
    }
}
