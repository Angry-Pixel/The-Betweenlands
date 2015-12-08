package thebetweenlands.manual.widgets;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.utils.AspectIconRenderer;

import java.util.ArrayList;
import java.util.List;

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
        AspectIconRenderer.renderIcon(xStart, yStart, (int) (16 * scale), (int) (16 * scale), aspect.getIconIndex());
        if (mouseX >= xStart && mouseX <= xStart + 16 * scale && mouseY >= yStart && mouseY <= yStart + 16 * scale) {
            List<String> tooltipData = new ArrayList<>();
            tooltipData.add(aspect.getName());
            tooltipData.add(EnumChatFormatting.GRAY + aspect.getType());
            renderTooltip(mouseX, mouseY, tooltipData, 0xffffff, 0xf0100010);
        }
    }
}
