package thebetweenlands.client.rendering.vertex;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class BetweenlandsVertexFormats {

	// POSITION_COLOR_TEX_MASK
	public static final VertexFormat BETWEENLANDS_SKY = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().
		put("Position", DefaultVertexFormat.ELEMENT_POSITION).
		put("Color", DefaultVertexFormat.ELEMENT_COLOR).
		put("UV0", DefaultVertexFormat.ELEMENT_UV0).build());

}
