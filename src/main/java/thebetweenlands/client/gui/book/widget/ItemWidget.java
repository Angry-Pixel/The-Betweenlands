package thebetweenlands.client.gui.book.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemWidget extends BookWidget {

	public List<ItemStack> stacks = new ArrayList<>();
	public float scale;
	private int currentDisplayItem = 0;
	private long lastCycleTimestamp = System.currentTimeMillis();

	public ItemWidget(int x, int y, ItemStack stack, float scale) {
		super(x, y, (int) (16 * scale), (int) (16 * scale));
		this.stacks.add(stack);
		this.scale = scale;
	}

	public ItemWidget(int x, int y, List<ItemStack> stacks, float scale) {
		super(x, y, (int) (16 * scale), (int) (16 * scale));
		this.stacks = stacks;
		this.scale = scale;
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.pose().pushPose();
		graphics.pose().scale(this.scale, this.scale, 1.0F);
		graphics.renderItem(this.stacks.get(this.currentDisplayItem), (int) (this.getX() / this.scale), (int) (this.getY() / this.scale));
		graphics.renderItemDecorations(Minecraft.getInstance().font, this.stacks.get(this.currentDisplayItem), (int) (this.getX() / this.scale), (int) (this.getY() / this.scale));
		graphics.pose().popPose();
		if (this.isHovered()) {
			graphics.renderTooltip(Minecraft.getInstance().font, this.stacks.get(this.currentDisplayItem), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 2 && this.isHovered()) {
			if (this.currentDisplayItem + 1 < this.stacks.size()) {
				this.currentDisplayItem++;
			} else {
				this.currentDisplayItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		super.tick();
		if (System.currentTimeMillis() > this.lastCycleTimestamp + 2000L) {
			if (this.currentDisplayItem + 1 < this.stacks.size()) {
				this.currentDisplayItem++;
			} else {
				this.currentDisplayItem = 0;
			}
			this.lastCycleTimestamp = System.currentTimeMillis();
		}
	}
}
