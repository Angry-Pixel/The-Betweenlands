package thebetweenlands.event.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.impl.LightSource;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.utils.confighandler.ConfigHandler;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Map.Entry;

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

			if(ConfigHandler.USE_SHADER && ConfigHandler.FIREFLY_LIGHTING) {
				ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
						entity.worldObj.rand.nextFloat() * 0.1f + 6.0f, 
						16.0f / 255.0f + entity.worldObj.rand.nextFloat() * 0.04f, 
						12.0f / 255.0f + entity.worldObj.rand.nextFloat() * 0.01f, 
						8.0f / 255.0f));
			}
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		this.fireflyList.clear();
	}
}
