package thebetweenlands.client.render;

import java.lang.reflect.Field;

import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class TessellatorDebug extends Tessellator {
	private Thread currentThread = Thread.currentThread();

	private StackTraceElement currentStartDrawingCaller;

	private Field isDrawingField = ReflectionHelper.findField(Tessellator.class, "isDrawing", "field_78415_z", "x");

	@Override
	public void startDrawing(int mode) {
		checkStart();
		super.startDrawing(mode);
		currentStartDrawingCaller = null;
	}

	@Override
	public void startDrawingQuads() {
		checkStart();
		startDrawing(7);
	}

	private void checkStart() {
		try {
			boolean isDrawing = isDrawingField.getBoolean(this);
			if (isDrawing) {
				System.out.println("Already started drawing from " + currentStartDrawingCaller);
			}
		} catch (Exception e) {
		}
		if (currentStartDrawingCaller == null) {
			currentStartDrawingCaller = currentThread.getStackTrace()[3];
		}
	}
}
