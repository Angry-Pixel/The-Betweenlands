package thebetweenlands.manual.widgets;

import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.utils.AspectIconRenderer;

/**
 * Created by Bart on 08/12/2015.
 */
public class AspectWidget extends ManualWidgetsBase {
    public IAspectType aspect;
    public float scale = 1.0f;

    public AspectWidget(int xStart, int yStart, IAspectType aspect, float scale) {
        super(xStart, yStart);
        this.aspect = aspect;
        this.scale = scale;
    }


    @Override
    public void drawForeGround() {
        super.drawForeGround();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        AspectIconRenderer.renderIcon(xStart, yStart, (int) (16 * scale), (int) (16 * scale), aspect.getIconIndex());
        GL11.glPopMatrix();
    }
}
