package thebetweenlands.client.rendering.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class BetweenlandsVertexFormats {

	// POSITION_COLOR_TEX_MASK
	public static final VertexFormat BETWEENLANDS_SKY = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV0", VertexFormatElement.UV0)
		.build();
}
