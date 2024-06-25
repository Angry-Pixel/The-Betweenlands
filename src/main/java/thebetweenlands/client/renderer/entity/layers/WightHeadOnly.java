package thebetweenlands.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.common.entities.EntityWight;

public class WightHeadOnly extends RenderLayer<EntityWight, ModelWight<EntityWight>> {

	public WightHeadOnly(RenderLayerParent<EntityWight, ModelWight<EntityWight>> p_117346_) {
		super(p_117346_);
	}

	@Override
	public void render(PoseStack p_117349_, MultiBufferSource p_117350_, int p_117351_, EntityWight p_117352_,
					   float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {

	}

}
