package thebetweenlands.client.render.tile;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.tile.TileEntitySimulacrum;

public class RenderSimulacrum extends TileEntitySpecialRenderer<TileEntitySimulacrum> {
	@Override
	public boolean isGlobalRenderer(TileEntitySimulacrum te) {
		return true;
	}

	@Override
	public void render(TileEntitySimulacrum tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile != null && tile.isRunning()) {
			if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
			}
			WorldRenderHandler.REPELLER_SHIELDS.add(Pair.of(new Vec3d(x + 0.5F, y + 0.5F, z + 0.5F), tile.getRadius(partialTicks)));
		}
	}
}
