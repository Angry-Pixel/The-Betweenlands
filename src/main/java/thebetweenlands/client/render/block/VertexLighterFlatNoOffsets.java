package thebetweenlands.client.render.block;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;

/**
 * Renders a block model without ambient occlusion and random offsets
 */
public class VertexLighterFlatNoOffsets extends VertexLighterFlat {
	public VertexLighterFlatNoOffsets(BlockColors colors) {
		super(colors);
	}

	@Override
	public void updateBlockInfo() {
		//Don't set shift
		this.blockInfo.updateFlatLighting();
	}
}