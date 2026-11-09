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
	        .add("Position", VertexFormatElement.POSITION) // 3 * 4 = 12
	        .add("Color", VertexFormatElement.COLOR)       // 4 * 1 = 4
	        .add("UV0", VertexFormatElement.UV0)           // 2 * 4 = 8
	        .add("UV1", VertexFormatElement.UV1)           // 2 * 2 = 4
	        .add("Normal", VertexFormatElement.NORMAL)     // 3 * 1 = 3
	        .padding(1)                                    // 1     = 1
	        .build();                                      // Total Size: 32 Bytes / Vertex

	// Position
	// Color (which we actually replace with the four bytes of a float)
	// UV0 (texture UVs)
	// UV2 (lighting) - may get rid of this once I have something working
	// Normal
	public static final VertexFormat BEAM_ORIGIN_GLASS = VertexFormat.builder()
	        .add("Position", VertexFormatElement.POSITION) // 3 * 4 = 12
	        .add("Visibility", VertexFormatElement.COLOR)  // 4 * 1 = 4
	        .add("UV0", VertexFormatElement.UV0)           // 2 * 4 = 8
	        .add("UV2", VertexFormatElement.UV2)           // 2 * 2 = 4
	        .add("Normal", VertexFormatElement.NORMAL)     // 3 * 1 = 3
	        .padding(1)                                    // 1     = 1
	        .build();                                      // Total Size: 32 Bytes / Vertex
	
}
