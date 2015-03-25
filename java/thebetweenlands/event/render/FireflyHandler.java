package thebetweenlands.event.render;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.vecmath.Vector3d;

import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.mobs.EntityFirefly;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FireflyHandler {
	public static final FireflyHandler INSTANCE = new FireflyHandler();

	public ArrayList<Entry<Entry<RenderFirefly, EntityFirefly>, Vector3d>> fireflyList = new ArrayList<Entry<Entry<RenderFirefly, EntityFirefly>, Vector3d>>();

	//TODO: Temporary solution for the water rendering order, fixed in 1.8
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		GL11.glPushMatrix();
		for(Entry<Entry<RenderFirefly, EntityFirefly>, Vector3d> e : this.fireflyList) {
			Vector3d pos = e.getValue();
			RenderFirefly renderer = e.getKey().getKey();
			EntityFirefly entity = e.getKey().getValue();
			renderer.doRenderCallback(entity, pos.x, pos.y, pos.z, event.partialTicks);
			
			ShaderHelper.INSTANCE.lightSources.add(new Vector3d(entity.posX, entity.posY, entity.posZ));
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		this.fireflyList.clear();
	}
}
