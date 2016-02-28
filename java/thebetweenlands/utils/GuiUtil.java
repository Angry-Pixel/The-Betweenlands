package thebetweenlands.utils;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

public class GuiUtil {
	public static float[] getRGBFromHex(int hex) {
		float temp[] = new float[4];
		temp[0] = (hex >> 16 & 0xFF) / 255F;
		temp[1] = (hex >> 8 & 0xFF) / 255F;
		temp[2] = (hex & 0xFF) / 255F;
		temp[3] = (hex >> 24 & 0xFF) / 255F;
		return temp;
	}

	public static void drawLine(float xPos1, float yPos1, float xPos2, float yPos2, int colour) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glPushMatrix();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(getRGBFromHex(colour)[0], getRGBFromHex(colour)[1],
				getRGBFromHex(colour)[2], getRGBFromHex(colour)[3]);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(xPos1, yPos1);
		GL11.glVertex2d(xPos2, yPos2);
		GL11.glEnd();

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawRect(float xPos1, float yPos1, float xPos2, float yPos2, int colour) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glPushMatrix();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(getRGBFromHex(colour)[0], getRGBFromHex(colour)[1],
				getRGBFromHex(colour)[2], getRGBFromHex(colour)[3]);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(xPos1, yPos1);
		GL11.glVertex2d(xPos1, yPos2);
		GL11.glVertex2d(xPos2, yPos2);
		GL11.glVertex2d(xPos2, yPos1);
		GL11.glEnd();

		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawPartialCircle(int x, int y, double radius, int startAngle, int endAngle) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		startAngle -= 90;
		endAngle -= 90;
		for(int angle = startAngle; angle <= endAngle; angle++) {
			float rad = (float) (Math.PI * angle / 180F);
			float x2 = (float) (x + radius * Math.cos(rad));
			float y2 = (float) (y + radius * Math.sin(rad));
			GL11.glVertex3f(x2, y2, 0.0F);
			GL11.glVertex3f(x, y, 0.0F);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircle(int x, int y, double radius) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBegin(6);
		for(int i = 0; i <= 360; i++) {
			double sin = Math.sin(((double)i * Math.PI) / 180D) * radius;
			double cos = Math.cos(((double)i * Math.PI) / 180D) * radius;
			GL11.glVertex2d((double)x + sin, (double)y + cos);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircleOutline(int x, int y, double radius) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		float rotation = (float)(Math.PI * 2D / 360D);
		float moveX = (float)Math.cos(rotation);
		float moveY = (float)Math.sin(rotation);
		float xOffset = (float)radius;
		float yOffset = 0.0F;
		GL11.glBegin(2);
		for(int i = 0; i < 360; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircleOutline(int x, int y, double radius, int corners) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		float rotation = (float)(Math.PI * 2D / (double)corners);
		float moveX = (float)Math.cos(rotation);
		float moveY = (float)Math.sin(rotation);
		float xOffset = (float)radius;
		float yOffset = 0.0F;
		GL11.glBegin(2);
		for(int i = 0; i < corners; i++) {
			GL11.glVertex2f(x + xOffset, y + yOffset);
			float prevXOffset = xOffset;
			xOffset = moveX * xOffset - moveY * yOffset;
			yOffset = moveY * prevXOffset + moveX * yOffset;
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private static final String pad(String s) {
		return (s.length() == 1) ? "0" + s : s;
	}
	public static int toHexInt(Color col) {
		try {
			String as = pad(Integer.toHexString(col.getAlpha()));
			String rs = pad(Integer.toHexString(col.getRed()));
			String gs = pad(Integer.toHexString(col.getGreen()));
			String bs = pad(Integer.toHexString(col.getBlue()));
			String hexString = "0x" + as + rs + gs + bs;
			return (int)(Long.decode(hexString).intValue());
		} catch(Exception ex) {
			return 0xFFFFFFFF;
		}
	}
}