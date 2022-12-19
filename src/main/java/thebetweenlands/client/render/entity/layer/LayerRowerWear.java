package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower.BipedTextureUVs;
import thebetweenlands.client.render.model.entity.rowboat.ModelPlayerRower;

public class LayerRowerWear implements LayerRenderer<AbstractClientPlayer> {
    private ModelBipedRower model;

    public LayerRowerWear(boolean slimArms) {
        model = new ModelPlayerRower(0.25F, true, slimArms, new BipedTextureUVs(48, 48, 40, 32, 0, 48, 0, 32, 16, 32));
        model.bipedHead.showModel = model.bipedHeadwear.showModel = false;
    }

    public ModelBipedRower getModel() {
        return model;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float swing, float speed, float delta, float age, float yaw, float pitch, float scale) {
        model.render(player, swing, speed, age, yaw, pitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
