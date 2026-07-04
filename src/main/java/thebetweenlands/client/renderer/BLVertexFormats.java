package thebetweenlands.client.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class BLVertexFormats {

	// Position (rune MASK positions)
	// Color
	// UV0 (rune glow texture UVs)
	// UV1 (rune mask texture UVs)
	// Normal (rune glow normal)
	public static final VertexFormat DUNGEON_DOOR_RUNE = VertexFormat.builder()
	        .add("Position", VertexFormatElement.POSITION)
	        .add("Color", VertexFormatElement.COLOR)
	        .add("UV0", VertexFormatElement.UV0)
	        .add("UV1", VertexFormatElement.UV1)
	        .add("Normal", VertexFormatElement.NORMAL)
	        .padding(1)
	        .build();
	
}
