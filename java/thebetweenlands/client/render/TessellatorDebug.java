package thebetweenlands.client.render;

import java.lang.reflect.Field;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class TessellatorDebug extends Tessellator {
	private Thread currentThread = Thread.currentThread();

	private StackTraceElement currentStartDrawingCaller;

	private boolean isRDown = false;

	private Field isDrawingField = ReflectionHelper.findField(Tessellator.class, "isDrawing");

	@Override
	public void startDrawing(int mode) {
		checkCaller();
		super.startDrawing(mode);
		currentStartDrawingCaller = null;
	}

	@Override
	public void startDrawingQuads() {
		checkCaller();
		startDrawing(7);
	}

	private void checkCaller() {
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
		if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
			if (!isRDown) {
				System.out.println(currentStartDrawingCaller);
				isRDown = true;
			}
		} else {
			isRDown = false;
		}
	}
}
