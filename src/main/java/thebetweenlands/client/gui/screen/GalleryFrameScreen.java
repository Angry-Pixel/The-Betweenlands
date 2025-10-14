package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.handler.gallery.GalleryEntry;
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.client.renderer.entity.GalleryFrameRenderer;
import thebetweenlands.common.entity.GalleryFrame;
import thebetweenlands.common.network.serverbound.SetGalleryUrlPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GalleryFrameScreen extends Screen {
	protected static final int WIDTH = 200;
	protected static final int HEIGHT = 200;

	protected int xStart;
	protected int yStart;

	protected final GalleryFrame frame;

	protected EditBox searchBox;

	protected final Component discordName = Component.literal("Discord").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/5RwFZgT57p")));
	protected final Component twitterName = Component.literal("@BetweenlandsDev").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/BetweenlandsDev")));

	@Nullable
	private StylePoint submissionText;
	@Nullable
	private StylePoint sourceUrl;

	public GalleryFrameScreen(GalleryFrame frame) {
		super(Component.empty());
		this.frame = frame;
	}

	@Override
	public void init() {
		this.submissionText = null;
		this.sourceUrl = null;
		this.xStart = (this.width - WIDTH) / 2 - 100;
		this.yStart = (this.height - HEIGHT) / 2;

		this.addRenderableWidget(new Button.Builder(Component.literal("<-"), button -> this.switchPicture(true, false)).bounds(this.xStart - 60, this.yStart + HEIGHT / 2, 30, 20).build());
		this.addRenderableWidget(new Button.Builder(Component.literal("->"), button -> this.switchPicture(false, true)).bounds(this.xStart + WIDTH + 30, this.yStart + HEIGHT / 2, 30, 20).build());

		this.addRenderableWidget(new Button.Builder(Component.translatable("gui.done"), button -> this.minecraft.setScreen(null)).pos(this.xStart + WIDTH + 30, this.yStart + HEIGHT - 40).build());

		Button randomizeButton = new Button.Builder(Component.translatable("gui.thebetweenlands.gallery.random"), button -> {
			Map<String, GalleryEntry> available = GalleryManager.INSTANCE.getEntries();

			if (!available.isEmpty()) {
				PacketDistributor.sendToServer(new SetGalleryUrlPacket(this.frame, available.values().stream().skip(this.frame.getRandom().nextInt(available.size())).findFirst().map(GalleryEntry::getUrl).orElse("")));
				this.sourceUrl = null;
			}
		}).pos(this.xStart + WIDTH + 30, this.yStart + 26 + 14).build();
		this.addRenderableWidget(randomizeButton);

		this.searchBox = new EditBox(this.font, this.xStart + WIDTH + 32, this.yStart + 14, 196, 20, Component.empty());
		this.searchBox.setMaxLength(128);
		this.addRenderableWidget(this.searchBox);

		//Set to random available picture
		if (this.frame.getUrl().isEmpty()) {
			randomizeButton.onPress();
		}
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		String s = this.searchBox.getValue();
		if (this.searchBox.charTyped(codePoint, modifiers)) {
			if (!Objects.equals(s, this.searchBox.getValue())) {
				this.switchPicture(false, false);
			}

			return true;
		}

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean flag1 = InputConstants.getKey(keyCode, scanCode).getNumericKeyValue().isPresent();
		if (!flag1) {
			String s = this.searchBox.getValue();
			if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
				if (!Objects.equals(s, this.searchBox.getValue())) {
					this.switchPicture(false, false);
				}

				return true;
			} else {
				return this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256 || super.keyPressed(keyCode, scanCode, modifiers);
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean ret = super.mouseClicked(mouseX, mouseY, button);

		this.searchBox.mouseClicked(mouseX, mouseY, button);

		if (button == 0) {
			if (this.submissionText != null) {
				var subStyle = this.submissionText.getStyleFromPoint(this.font, mouseX, mouseY);

				if (subStyle != null) {
					this.handleComponentClicked(subStyle);
					return false;
				}
			}

			if (this.sourceUrl != null) {
				var sourceStyle = this.sourceUrl.getStyleFromPoint(this.font, mouseX, mouseY);

				if (sourceStyle != null) {
					this.handleComponentClicked(sourceStyle);
					return false;
				}
			}
		}
		return ret;
	}

	private boolean searchEntryText(GalleryEntry entry, String searchText) {
		return entry.getTitle().toLowerCase().contains(searchText) || entry.getAuthor().toLowerCase().contains(searchText) ||
			(entry.getDescription() != null && entry.getDescription().replaceAll("\n", " ").toLowerCase().contains(searchText)) ||
			(entry.getSourceUrl() != null && entry.getSourceUrl().toLowerCase().contains(searchText)) ||
			entry.getSha256().toLowerCase().contains(searchText);
	}

	private void switchPicture(boolean prev, boolean next) {
		Map<String, GalleryEntry> available = GalleryManager.INSTANCE.getEntries();

		if (!available.isEmpty()) {
			GalleryEntry entry = available.get(this.frame.getUrl());

			GalleryEntry selectedEntry;

			List<GalleryEntry> availableList = new ArrayList<>(available.values());

			final String searchText = this.searchBox.getValue().toLowerCase();

			availableList.sort((e1, e2) -> {
				boolean search1 = !searchText.isEmpty() && this.searchEntryText(e1, searchText);
				boolean search2 = !searchText.isEmpty() && this.searchEntryText(e2, searchText);
				return e1.getTitle().compareTo(e2.getTitle()) + (search1 ? -1000 : 0) + (search2 ? 1000 : 0);
			});

			if (entry == null || (!prev && !next)) {
				selectedEntry = availableList.getFirst();
			} else {
				int currentIndex = availableList.indexOf(entry);

				if (currentIndex >= 0) {
					int newIndex = next ? currentIndex + 1 : currentIndex - 1;
					if (newIndex < 0) {
						newIndex = availableList.size() + newIndex;
					}
					newIndex = newIndex % availableList.size();

					selectedEntry = availableList.get(newIndex);
				} else {
					selectedEntry = availableList.getFirst();
				}
			}

			if (selectedEntry != null) {
				PacketDistributor.sendToServer(new SetGalleryUrlPacket(this.frame, selectedEntry.getUrl()));
			}
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);

		this.searchBox.render(graphics, mouseX, mouseY, partialTick);

		Component submissionText = Component.translatable("gui.thebetweenlands.gallery.submission", this.twitterName, this.discordName);
		if (this.submissionText == null) {
			this.submissionText = StylePoint.createPoint(this.font, this.xStart + 60, this.yStart - 60, 300, submissionText);
		}

		this.submissionText.draw(graphics, this.font, 0xFFFFFFFF);

		Component searchStr = Component.translatable("gui.thebetweenlands.gallery.search");
		graphics.drawString(this.font, searchStr, this.xStart + WIDTH + 30 + 100 - this.font.width(searchStr) / 2, this.yStart, 0xFFFFFFFF, false);

		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(this.frame.getUrl());

		ResourceLocation pictureLocation = entry != null ? entry.loadTextureAndGetLocation(GalleryFrameRenderer.GALLERY_FRAME_EMPTY_BACKGROUND) : GalleryFrameRenderer.GALLERY_FRAME_EMPTY_BACKGROUND;

		float relWidthMargin = 0;
		float relHeightMargin = 0;

		if (entry != null) {
			int maxDim = Math.max(entry.getWidth(), entry.getHeight());
			relWidthMargin = (maxDim - entry.getWidth()) / (float) maxDim / 2.0f;
			relHeightMargin = (maxDim - entry.getHeight()) / (float) maxDim / 2.0f;
		}

		LoreScrapScreen.drawTexture(pictureLocation, graphics, (int) (xStart + (WIDTH * relWidthMargin)), (int) (yStart + (HEIGHT * relHeightMargin)), (int) (WIDTH - (WIDTH * relWidthMargin * 2)), (int) (HEIGHT - (HEIGHT * relHeightMargin * 2)), WIDTH, HEIGHT, 0, WIDTH, 0, HEIGHT);

		if (entry != null) {
			graphics.drawString(this.font, Component.literal(entry.getTitle()).withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE), this.xStart + WIDTH / 2 - this.font.width(entry.getTitle()) / 2, this.yStart - 20, 0xFFFFFFFF, false);

			int maxLineWidth = 0;

			MutableComponent authorLine = Component.translatable("gui.thebetweenlands.gallery.author", entry.getAuthor());
			maxLineWidth = Math.max(maxLineWidth, this.font.width(authorLine));


			MutableComponent descName = Component.translatable("gui.thebetweenlands.gallery.description");
			int descNameWidth = this.font.width(descName);
			String desc = entry.getDescription();
			String[] descLines = null;
			if (desc != null) {
				descLines = desc.split("\\n");
				for (int i = 0; i < descLines.length; i++) {
					if (i == 0) {
						maxLineWidth = Math.max(maxLineWidth, this.font.width(descName.getString() + descLines[0]));
					} else {
						maxLineWidth = Math.max(maxLineWidth, this.font.width(descLines[i]) + descNameWidth);
					}
				}
			}

			Component sourceLine = null;
			if (entry.getSourceUrl() != null) {
				sourceLine = Component.translatable("gui.thebetweenlands.gallery.source_url").append(Component.translatable("gui.thebetweenlands.gallery.source_url_click").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getSourceUrl()))));
				maxLineWidth = Math.max(maxLineWidth, this.font.width(sourceLine));
			}

			graphics.drawString(this.font, authorLine, this.xStart + WIDTH / 2 - maxLineWidth / 2, this.yStart + HEIGHT + 8, 0xFFFFFFFF, false);

			int yOff = 0;

			if (descLines != null) {
				for (int i = 0; i < descLines.length; i++) {
					if (i == 0) {
						graphics.drawString(this.font, descName.append(descLines[i]), this.xStart + WIDTH / 2 - maxLineWidth / 2, this.yStart + HEIGHT + 23 + yOff, 0xFFFFFFFF, false);
					} else {
						graphics.drawString(this.font, descLines[i], this.xStart + descNameWidth + WIDTH / 2 - maxLineWidth / 2, this.yStart + HEIGHT + 23 + yOff, 0xFFFFFFFF, false);
					}
					yOff += this.font.lineHeight;
				}
			}

			if (sourceLine != null) {
				if (this.sourceUrl == null) {
					this.sourceUrl = StylePoint.createPoint(this.font, this.xStart + WIDTH / 2 - maxLineWidth / 2, this.yStart + HEIGHT + 26 + yOff, 200, sourceLine);
				}
				this.sourceUrl.draw(graphics, this.font, 0xFFFFFFFF);
			}
		} else {
			Component notFoundText = Component.translatable("gui.thebetweenlands.gallery.info_not_found");
			graphics.drawWordWrap(this.font, notFoundText, this.xStart + WIDTH / 2 - this.font.width(notFoundText) / 2, this.yStart + HEIGHT + 12, 300, 0xFFFFFFFF);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	static class StylePoint {
		private final int x;
		private final int y;
		private final int width;
		private final FormattedText text;
		private final List<FormattedCharSequence> sequences;

		private StylePoint(int x, int y, int width, FormattedText text, List<FormattedCharSequence> sequences) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.text = text;
			this.sequences = sequences;
		}

		public static StylePoint createPoint(Font font, int x, int y, int width, FormattedText text) {
			var sequence = font.split(text, width);
			return new StylePoint(x, y, width, text, sequence);
		}

		@Nullable
		public Style getStyleFromPoint(Font font, double mouseX, double mouseY) {
			int currentYOffs = this.y;
			for (FormattedCharSequence seq : this.sequences) {
				int width = font.width(seq);
				if (mouseX > this.x && mouseX < this.x + width && mouseY > currentYOffs && mouseY < currentYOffs + font.lineHeight) {
					Style style = font.getSplitter().componentStyleAtWidth(seq, (int) (mouseX - this.x));
					if (style != null)
						return style;
				}
				currentYOffs += font.lineHeight;
			}
			return null;
		}

		public void draw(GuiGraphics graphics, Font font, int color) {
			graphics.drawWordWrap(font, this.text, this.x, this.y, this.width, color);
		}
	}
}
