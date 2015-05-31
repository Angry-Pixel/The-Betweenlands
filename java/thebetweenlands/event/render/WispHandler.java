package thebetweenlands.event.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.tileentity.TileEntityWispRenderer;
import thebetweenlands.tileentities.TileEntityWisp;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Map.Entry;

public class WispHandler {
	public static final WispHandler INSTANCE = new WispHandler();
	
	public ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>> tileList = new ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>>();
	
	//TODO: Temporary solution for the water rendering order, fixed in 1.8
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		GL11.glPushMatrix();
		for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
			Vector3d pos = e.getValue();
			e.getKey().getKey().doRender(e.getKey().getValue(), pos.x, pos.y, pos.z, event.partialTicks);
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		this.tileList.clear();
	}
}
