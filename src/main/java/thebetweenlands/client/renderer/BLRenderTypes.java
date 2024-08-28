package thebetweenlands.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class BLRenderTypes extends RenderType {

	public static final RenderType DRUID_CONE = create(
		"thebetweenlands:druid_cone",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.TRIANGLE_FAN,
		1536,
		false,
		false,
		RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_LIGHTNING_SHADER)
			.setCullState(CULL)
			.setWriteMaskState(COLOR_WRITE)
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.createCompositeState(false)
	);

	public BLRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType druidCone() {
		return DRUID_CONE;
	}
}
