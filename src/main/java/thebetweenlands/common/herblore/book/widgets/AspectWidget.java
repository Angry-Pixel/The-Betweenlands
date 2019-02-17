package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.util.AspectIconRenderer;

@OnlyIn(Dist.CLIENT)
public class AspectWidget extends ManualWidgetBase {
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
        AspectIconRenderer.renderIcon(xStart, yStart, (int) (16 * scale), (int) (16 * scale), aspect.getIcon());
    }

    @Override
    public void drawToolTip() {
        super.drawToolTip();
        if (mouseX >= xStart && mouseX <= xStart + 16 * scale && mouseY >= yStart && mouseY <= yStart + 16 * scale) {
            List<String> tooltipData = new ArrayList<>();
            tooltipData.add(aspect.getName());
            tooltipData.add(TextFormatting.GRAY + aspect.getType());
            renderTooltip(mouseX, mouseY, tooltipData, 0xffffff, 0xf0100010);
        }
    }
}
