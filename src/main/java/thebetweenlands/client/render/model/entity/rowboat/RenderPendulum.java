package thebetweenlands.client.render.model.entity.rowboat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import thebetweenlands.util.phys.DiffEqSolver;
import thebetweenlands.util.phys.PendulumSimulation;
import thebetweenlands.util.phys.RungeKuttaSolver;

//@Mod.EventBusSubscriber(modid = ModInfo.ID)
public class RenderPendulum {
	static PendulumSimulation pendulum = new PendulumSimulation(1.0F, 0.4F);
	static DiffEqSolver solver = new RungeKuttaSolver(pendulum);

	@SubscribeEvent
	public static void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
			final float step = 1.0F / 20.0F;
			pendulum.move(Mouse.getDX(),  -Mouse.getDY());
			solver.step(step);
		}
	}

	@SubscribeEvent
	public static void onRender(final RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			final ScaledResolution res = event.getResolution();
			GlStateManager.disableTexture2D();
			GlStateManager.pushMatrix();
			GlStateManager.translate((double) Mouse.getX() / res.getScaleFactor(), (double) (Display.getHeight() - Mouse.getY()) / res.getScaleFactor(), 0.0F);
			GlStateManager.pushMatrix();
			GlStateManager.rotate((float) Math.toDegrees(pendulum.state[0]), 0.0F, 0.0F, 1.0F);
			Gui.drawRect(-50 / 5, 0, 50 / 5, 50, 0xFFFFFFFF);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			GlStateManager.enableTexture2D();
		}
	}
}
