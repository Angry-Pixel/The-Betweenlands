package thebetweenlands.forgeevent.client;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.RenderGlobal;

public class PostRenderHandEvent extends Event {
	public final RenderGlobal context;
	public final float partialTicks;
	public final int renderPass;

	public PostRenderHandEvent(RenderGlobal context, float partialTicks, int renderPass) {
		this.context = context;
		this.partialTicks = partialTicks;
		this.renderPass = renderPass;
	}
}