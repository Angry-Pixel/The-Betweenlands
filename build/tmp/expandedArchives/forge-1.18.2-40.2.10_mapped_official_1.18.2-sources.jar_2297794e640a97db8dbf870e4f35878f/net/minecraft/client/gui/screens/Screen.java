package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix4f;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends AbstractContainerEventHandler implements Widget {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   private static final Component USAGE_NARRATION = new TranslatableComponent("narrator.screen.usage");
   protected final Component title;
   private final List<GuiEventListener> children = Lists.newArrayList();
   private final List<NarratableEntry> narratables = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   public final List<Widget> renderables = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   @Nullable
   private URI clickedLink;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
   private static final long NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
   private long narrationSuppressTime = Long.MIN_VALUE;
   private long nextNarrationTime = Long.MAX_VALUE;
   @Nullable
   private NarratableEntry lastNarratable;

   protected Screen(Component p_96550_) {
      this.title = p_96550_;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getNarrationMessage() {
      return this.getTitle();
   }

   public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
      for(Widget widget : this.renderables) {
         widget.render(p_96562_, p_96563_, p_96564_, p_96565_);
      }

   }

   public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
      if (p_96552_ == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (p_96552_ == 258) {
         boolean flag = !hasShiftDown();
         if (!this.changeFocus(flag)) {
            this.changeFocus(flag);
         }

         return false;
      } else {
         return super.keyPressed(p_96552_, p_96553_, p_96554_);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.popGuiLayer();
   }

   protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T p_169406_) {
      this.renderables.add(p_169406_);
      return this.addWidget(p_169406_);
   }

   protected <T extends Widget> T addRenderableOnly(T p_169395_) {
      this.renderables.add(p_169395_);
      return p_169395_;
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T p_96625_) {
      this.children.add(p_96625_);
      this.narratables.add(p_96625_);
      return p_96625_;
   }

   protected void removeWidget(GuiEventListener p_169412_) {
      if (p_169412_ instanceof Widget) {
         this.renderables.remove((Widget)p_169412_);
      }

      if (p_169412_ instanceof NarratableEntry) {
         this.narratables.remove((NarratableEntry)p_169412_);
      }

      this.children.remove(p_169412_);
   }

   protected void clearWidgets() {
      this.renderables.clear();
      this.children.clear();
      this.narratables.clear();
   }

   private Font tooltipFont = null;
   private ItemStack tooltipStack = ItemStack.EMPTY;
   protected void renderTooltip(PoseStack p_96566_, ItemStack p_96567_, int p_96568_, int p_96569_) {
      tooltipStack = p_96567_;
      this.renderTooltip(p_96566_, this.getTooltipFromItem(p_96567_), p_96567_.getTooltipImage(), p_96568_, p_96569_);
      tooltipStack = ItemStack.EMPTY;
   }

   public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, ItemStack stack) {
      this.renderTooltip(poseStack, textComponents, tooltipComponent, x, y, null, stack);
   }
   public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, @Nullable Font font) {
      this.renderTooltip(poseStack, textComponents, tooltipComponent, x, y, font, ItemStack.EMPTY);
   }
   public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, @Nullable Font font, ItemStack stack) {
      this.tooltipFont = font;
      this.tooltipStack = stack;
      this.renderTooltip(poseStack, textComponents, tooltipComponent, x, y);
      this.tooltipFont = null;
      this.tooltipStack = ItemStack.EMPTY;
   }
   public void renderTooltip(PoseStack p_169389_, List<Component> p_169390_, Optional<TooltipComponent> p_169391_, int p_169392_, int p_169393_) {
      List<ClientTooltipComponent> list = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_169390_, p_169391_, p_169392_, width, height, this.tooltipFont, this.font);
      this.renderTooltipInternal(p_169389_, list, p_169392_, p_169393_);
   }

   public List<Component> getTooltipFromItem(ItemStack p_96556_) {
      return p_96556_.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   public void renderTooltip(PoseStack p_96603_, Component p_96604_, int p_96605_, int p_96606_) {
      this.renderTooltip(p_96603_, Arrays.asList(p_96604_.getVisualOrderText()), p_96605_, p_96606_);
   }

   public void renderComponentTooltip(PoseStack p_96598_, List<Component> p_96599_, int p_96600_, int p_96601_) {
      List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(this.tooltipStack, p_96599_, p_96600_, width, height, this.tooltipFont, this.font);
      this.renderTooltipInternal(p_96598_, components, p_96600_, p_96601_);
   }
   public void renderComponentTooltip(PoseStack poseStack, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
      this.renderComponentTooltip(poseStack, tooltips, mouseX, mouseY, null, stack);
   }
   public void renderComponentTooltip(PoseStack poseStack, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, @Nullable Font font) {
      this.renderComponentTooltip(poseStack, tooltips, mouseX, mouseY, font, ItemStack.EMPTY);
   }

   public void renderComponentTooltip(PoseStack poseStack, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, @Nullable Font font, ItemStack stack) {
      this.tooltipFont = font;
      this.tooltipStack = stack;
      List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(stack, tooltips, mouseX, width, height, this.tooltipFont, this.font);
      this.renderTooltipInternal(poseStack, components, mouseX, mouseY);
      this.tooltipFont = null;
      this.tooltipStack = ItemStack.EMPTY;
   }

   public void renderTooltip(PoseStack p_96618_, List<? extends FormattedCharSequence> p_96619_, int p_96620_, int p_96621_) {
      this.renderTooltipInternal(p_96618_, p_96619_.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), p_96620_, p_96621_);
   }

   public void renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> lines, int x, int y, Font font) {
      this.tooltipFont = font;
      this.renderTooltip(poseStack, lines, x, y);
      this.tooltipFont = null;
   }

   private void renderTooltipInternal(PoseStack p_169384_, List<ClientTooltipComponent> p_169385_, int p_169386_, int p_169387_) {
      if (!p_169385_.isEmpty()) {
         net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(this.tooltipStack, p_169384_, p_169386_, p_169387_, width, height, p_169385_, this.tooltipFont, this.font);
         if (preEvent.isCanceled()) return;
         int i = 0;
         int j = p_169385_.size() == 1 ? -2 : 0;

         for(ClientTooltipComponent clienttooltipcomponent : p_169385_) {
            int k = clienttooltipcomponent.getWidth(preEvent.getFont());
            if (k > i) {
               i = k;
            }

            j += clienttooltipcomponent.getHeight();
         }

         int j2 = preEvent.getX() + 12;
         int k2 = preEvent.getY() - 12;
         if (j2 + i > this.width) {
            j2 -= 28 + i;
         }

         if (k2 + j + 6 > this.height) {
            k2 = this.height - j - 6;
         }

         p_169384_.pushPose();
         int l = -267386864;
         int i1 = 1347420415;
         int j1 = 1344798847;
         int k1 = 400;
         float f = this.itemRenderer.blitOffset;
         this.itemRenderer.blitOffset = 400.0F;
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tesselator.getBuilder();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         Matrix4f matrix4f = p_169384_.last().pose();
         net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, p_169384_, j2, k2, preEvent.getFont(), p_169385_);
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 400, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd());
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
         fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
         fillGradient(matrix4f, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
         fillGradient(matrix4f, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderStart());
         fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBorderEnd(), colorEvent.getBorderEnd());
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         bufferbuilder.end();
         BufferUploader.end(bufferbuilder);
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         p_169384_.translate(0.0D, 0.0D, 400.0D);
         int l1 = k2;

         for(int i2 = 0; i2 < p_169385_.size(); ++i2) {
            ClientTooltipComponent clienttooltipcomponent1 = p_169385_.get(i2);
            clienttooltipcomponent1.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource);
            l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
         }

         multibuffersource$buffersource.endBatch();
         p_169384_.popPose();
         l1 = k2;

         for(int l2 = 0; l2 < p_169385_.size(); ++l2) {
            ClientTooltipComponent clienttooltipcomponent2 = p_169385_.get(l2);
            clienttooltipcomponent2.renderImage(preEvent.getFont(), j2, l1, p_169384_, this.itemRenderer, 400);
            l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
         }

         this.itemRenderer.blitOffset = f;
      }
   }

   protected void renderComponentHoverEffect(PoseStack p_96571_, @Nullable Style p_96572_, int p_96573_, int p_96574_) {
      if (p_96572_ != null && p_96572_.getHoverEvent() != null) {
         HoverEvent hoverevent = p_96572_.getHoverEvent();
         HoverEvent.ItemStackInfo hoverevent$itemstackinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ITEM);
         if (hoverevent$itemstackinfo != null) {
            this.renderTooltip(p_96571_, hoverevent$itemstackinfo.getItemStack(), p_96573_, p_96574_);
         } else {
            HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = hoverevent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (hoverevent$entitytooltipinfo != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(p_96571_, hoverevent$entitytooltipinfo.getTooltipLines(), p_96573_, p_96574_);
               }
            } else {
               Component component = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
               if (component != null) {
                  this.renderTooltip(p_96571_, this.minecraft.font.split(component, Math.max(this.width / 2, 200)), p_96573_, p_96574_);
               }
            }
         }

      }
   }

   protected void insertText(String p_96587_, boolean p_96588_) {
   }

   public boolean handleComponentClicked(@Nullable Style p_96592_) {
      if (p_96592_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_96592_.getClickEvent();
         if (hasShiftDown()) {
            if (p_96592_.getInsertion() != null) {
               this.insertText(p_96592_.getInsertion(), false);
            }
         } else if (clickevent != null) {
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.options.chatLinks) {
                  return false;
               }

               try {
                  URI uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.options.chatLinksPrompt) {
                     this.clickedLink = uri;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, clickevent.getValue(), false));
                  } else {
                     this.openLink(uri);
                  }
               } catch (URISyntaxException urisyntaxexception) {
                  LOGGER.error("Can't open url for {}", clickevent, urisyntaxexception);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI uri1 = (new File(clickevent.getValue())).toURI();
               this.openLink(uri1);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(clickevent.getValue(), false);
            } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardHandler.setClipboard(clickevent.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", (Object)clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String p_96616_) {
      this.sendMessage(p_96616_, true);
   }

   public void sendMessage(String p_96613_, boolean p_96614_) {
      p_96613_ = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(p_96613_);
      if (p_96613_.isEmpty()) return;
      if (p_96614_) {
         this.minecraft.gui.getChat().addRecentChat(p_96613_);
      }
      if (net.minecraftforge.client.ClientCommandHandler.sendMessage(p_96613_)) return;

      this.minecraft.player.chat(p_96613_);
   }

   public final void init(Minecraft p_96607_, int p_96608_, int p_96609_) {
      this.minecraft = p_96607_;
      this.itemRenderer = p_96607_.getItemRenderer();
      this.font = p_96607_.font;
      this.width = p_96608_;
      this.height = p_96609_;
      java.util.function.Consumer<GuiEventListener> add = (b) -> {
         if (b instanceof Widget w)
            this.renderables.add(w);
         if (b instanceof NarratableEntry ne)
            this.narratables.add(ne);
         children.add(b);
      };
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.InitScreenEvent.Pre(this, this.children, add, this::removeWidget))) {
      this.clearWidgets();
      this.setFocused((GuiEventListener)null);
      this.init();
      this.triggerImmediateNarration(false);
      this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.InitScreenEvent.Post(this, this.children, add, this::removeWidget));
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground(PoseStack p_96557_) {
      this.renderBackground(p_96557_, 0);
   }

   public void renderBackground(PoseStack p_96559_, int p_96560_) {
      if (this.minecraft.level != null) {
         this.fillGradient(p_96559_, 0, 0, this.width, this.height, -1072689136, -804253680);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, p_96559_));
      } else {
         this.renderDirtBackground(p_96560_);
      }

   }

   public void renderDirtBackground(int p_96627_) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / 32.0F + (float)p_96627_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)p_96627_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / 32.0F, (float)p_96627_).color(64, 64, 64, 255).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)p_96627_).color(64, 64, 64, 255).endVertex();
      tesselator.end();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, new PoseStack()));
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean p_96623_) {
      if (p_96623_) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI p_96590_) {
      Util.getPlatform().openUri(p_96590_);
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isCut(int p_96629_) {
      return p_96629_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int p_96631_) {
      return p_96631_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int p_96633_) {
      return p_96633_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int p_96635_) {
      return p_96635_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
      this.init(p_96575_, p_96576_, p_96577_);
   }

   public static void wrapScreenError(Runnable p_96580_, String p_96581_, String p_96582_) {
      try {
         p_96580_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, p_96581_);
         CrashReportCategory crashreportcategory = crashreport.addCategory("Affected screen");
         crashreportcategory.setDetail("Screen name", () -> {
            return p_96582_;
         });
         throw new ReportedException(crashreport);
      }
   }

   protected boolean isValidCharacterForName(String p_96584_, char p_96585_, int p_96586_) {
      int i = p_96584_.indexOf(58);
      int j = p_96584_.indexOf(47);
      if (p_96585_ == ':') {
         return (j == -1 || p_96586_ <= j) && i == -1;
      } else if (p_96585_ == '/') {
         return p_96586_ > i;
      } else {
         return p_96585_ == '_' || p_96585_ == '-' || p_96585_ >= 'a' && p_96585_ <= 'z' || p_96585_ >= '0' && p_96585_ <= '9' || p_96585_ == '.';
      }
   }

   public boolean isMouseOver(double p_96595_, double p_96596_) {
      return true;
   }

   public void onFilesDrop(List<Path> p_96591_) {
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   private void scheduleNarration(long p_169381_, boolean p_169382_) {
      this.nextNarrationTime = Util.getMillis() + p_169381_;
      if (p_169382_) {
         this.narrationSuppressTime = Long.MIN_VALUE;
      }

   }

   private void suppressNarration(long p_169379_) {
      this.narrationSuppressTime = Util.getMillis() + p_169379_;
   }

   public void afterMouseMove() {
      this.scheduleNarration(750L, false);
   }

   public void afterMouseAction() {
      this.scheduleNarration(200L, true);
   }

   public void afterKeyboardAction() {
      this.scheduleNarration(200L, true);
   }

   private boolean shouldRunNarration() {
      return NarratorChatListener.INSTANCE.isActive();
   }

   public void handleDelayedNarration() {
      if (this.shouldRunNarration()) {
         long i = Util.getMillis();
         if (i > this.nextNarrationTime && i > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = Long.MAX_VALUE;
         }
      }

   }

   protected void triggerImmediateNarration(boolean p_169408_) {
      if (this.shouldRunNarration()) {
         this.runNarration(p_169408_);
      }

   }

   private void runNarration(boolean p_169410_) {
      this.narrationState.update(this::updateNarrationState);
      String s = this.narrationState.collectNarrationText(!p_169410_);
      if (!s.isEmpty()) {
         NarratorChatListener.INSTANCE.sayNow(s);
      }

   }

   protected void updateNarrationState(NarrationElementOutput p_169396_) {
      p_169396_.add(NarratedElementType.TITLE, this.getNarrationMessage());
      p_169396_.add(NarratedElementType.USAGE, USAGE_NARRATION);
      this.updateNarratedWidget(p_169396_);
   }

   protected void updateNarratedWidget(NarrationElementOutput p_169403_) {
      ImmutableList<NarratableEntry> immutablelist = this.narratables.stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
      Screen.NarratableSearchResult screen$narratablesearchresult = findNarratableWidget(immutablelist, this.lastNarratable);
      if (screen$narratablesearchresult != null) {
         if (screen$narratablesearchresult.priority.isTerminal()) {
            this.lastNarratable = screen$narratablesearchresult.entry;
         }

         if (immutablelist.size() > 1) {
            p_169403_.add(NarratedElementType.POSITION, new TranslatableComponent("narrator.position.screen", screen$narratablesearchresult.index + 1, immutablelist.size()));
            if (screen$narratablesearchresult.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               p_169403_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.component_list.usage"));
            }
         }

         screen$narratablesearchresult.entry.updateNarration(p_169403_.nest());
      }

   }

   @Nullable
   public static Screen.NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> p_169401_, @Nullable NarratableEntry p_169402_) {
      Screen.NarratableSearchResult screen$narratablesearchresult = null;
      Screen.NarratableSearchResult screen$narratablesearchresult1 = null;
      int i = 0;

      for(int j = p_169401_.size(); i < j; ++i) {
         NarratableEntry narratableentry = p_169401_.get(i);
         NarratableEntry.NarrationPriority narratableentry$narrationpriority = narratableentry.narrationPriority();
         if (narratableentry$narrationpriority.isTerminal()) {
            if (narratableentry != p_169402_) {
               return new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
            }

            screen$narratablesearchresult1 = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
         } else if (narratableentry$narrationpriority.compareTo(screen$narratablesearchresult != null ? screen$narratablesearchresult.priority : NarratableEntry.NarrationPriority.NONE) > 0) {
            screen$narratablesearchresult = new Screen.NarratableSearchResult(narratableentry, i, narratableentry$narrationpriority);
         }
      }

      return screen$narratablesearchresult != null ? screen$narratablesearchresult : screen$narratablesearchresult1;
   }

   public void narrationEnabled() {
      this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
   }

   protected static void hideWidgets(AbstractWidget... p_202377_) {
      for(AbstractWidget abstractwidget : p_202377_) {
         abstractwidget.visible = false;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class NarratableSearchResult {
      public final NarratableEntry entry;
      public final int index;
      public final NarratableEntry.NarrationPriority priority;

      public NarratableSearchResult(NarratableEntry p_169424_, int p_169425_, NarratableEntry.NarrationPriority p_169426_) {
         this.entry = p_169424_;
         this.index = p_169425_;
         this.priority = p_169426_;
      }
   }
}
