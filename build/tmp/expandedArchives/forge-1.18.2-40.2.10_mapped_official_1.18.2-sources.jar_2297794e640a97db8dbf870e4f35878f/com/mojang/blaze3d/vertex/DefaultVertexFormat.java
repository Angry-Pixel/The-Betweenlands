package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultVertexFormat {
   public static final VertexFormatElement ELEMENT_POSITION = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
   public static final VertexFormatElement ELEMENT_COLOR = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
   public static final VertexFormatElement ELEMENT_UV0 = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement ELEMENT_UV1 = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement ELEMENT_UV2 = new VertexFormatElement(2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
   public static final VertexFormatElement ELEMENT_NORMAL = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
   public static final VertexFormatElement ELEMENT_PADDING = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
   public static final VertexFormatElement ELEMENT_UV = ELEMENT_UV0;
   public static final VertexFormat BLIT_SCREEN = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV", ELEMENT_UV).put("Color", ELEMENT_COLOR).build());
   public static final VertexFormat BLOCK = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
   public static final VertexFormat NEW_ENTITY = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV1", ELEMENT_UV1).put("UV2", ELEMENT_UV2).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
   public static final VertexFormat PARTICLE = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).put("UV2", ELEMENT_UV2).build());
   public static final VertexFormat POSITION = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).build());
   public static final VertexFormat POSITION_COLOR = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).build());
   public static final VertexFormat POSITION_COLOR_NORMAL = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
   public static final VertexFormat POSITION_COLOR_LIGHTMAP = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV2", ELEMENT_UV2).build());
   public static final VertexFormat POSITION_TEX = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).build());
   public static final VertexFormat POSITION_COLOR_TEX = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).build());
   public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).build());
   public static final VertexFormat POSITION_COLOR_TEX_LIGHTMAP = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).build());
   public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).put("Color", ELEMENT_COLOR).build());
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
}