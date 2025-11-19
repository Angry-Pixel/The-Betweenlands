package thebetweenlands.client.stencil;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.common.util.TriState;
import thebetweenlands.client.renderer.util.StencilType;

public record SimpleStencilConfig(RenderType renderType, StencilType stencilType, TriState colourMaskOverride, TriState depthMaskOverride) {

}
