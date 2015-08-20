package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.manual.gui.GuiManualBase;

import java.util.ArrayList;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
    public String text;
    public int color = 0x000000;
    public int unchangedColor = 0x000000;

    public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText) {
        super(manual, xStart, yStart);
        text = StatCollector.translateToLocal(unlocalizedText);
    }

    public TextWidget(GuiManualBase manual, int xStart, int yStart, String localizedText, boolean localized) {
        super(manual, xStart, yStart);
        text = localizedText;
    }

    public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText, int color) {
        super(manual, xStart, yStart);
        text = StatCollector.translateToLocal(unlocalizedText);
        this.color = color;
        this.unchangedColor = color;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        int widthLine = 0;
        if (text != null) {
            String[] words = text.split(" ");
            int lineNumber = 0;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

            int tooltipWidth = 0;
            boolean makingTooltip = false;
            boolean makingTooltipWord = false;

            boolean italic = false;
            boolean bold = false;
            boolean underlined = false;
            boolean strikerThrough = false;
            boolean obfuscated = false;

            String toolTipWord = "";
            int tooltipStartX = 0;
            int tooltipStartY = 0;

            float scale = 1f;

            int height = fontRenderer.FONT_HEIGHT;

            for (String word : words) {
                int widthWord = (int)Math.ceil(fontRenderer.getStringWidth(word + " ") * scale);


                word = "" + (italic?EnumChatFormatting.ITALIC:"") + (bold?EnumChatFormatting.BOLD:"") + (underlined?EnumChatFormatting.UNDERLINE:"") + (strikerThrough?EnumChatFormatting.STRIKETHROUGH:"")  + (obfuscated?EnumChatFormatting.OBFUSCATED:"") + word + " ";
                if(word.contains("<end>")) {
                    tooltipWidth = 0;
                    makingTooltip = false;
                    makingTooltipWord = false;

                    italic = false;
                    bold = false;
                    underlined = false;
                    strikerThrough = false;
                    obfuscated = false;
                    word ="";
                    scale = 1f;
                } else if(makingTooltipWord) {
                    if(word.contains(">")){
                        makingTooltipWord = false;
                        toolTipWord += " " + word.replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    } else
                        toolTipWord += " " + word.replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    word = "";
                } else if (word.contains("/n/")) {
                    lineNumber++;
                    widthLine = 0;
                    word = "";
                } else if (word.contains("<color:")) {
                    color = Integer.decode(word.replace("<color:", "").replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, ""));
                    word = "";
                } else if (word.contains("</color>")) {
                    color = unchangedColor;
                    word = "";
                } else if (word.contains("<tooltip:")) {
                    if(!word.contains(">"))
                        makingTooltipWord = true;
                    toolTipWord = word.replace("<tooltip:", "").replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    makingTooltip = true;
                    word = "";
                    tooltipStartX = xStart + widthLine;
                    tooltipStartY = yStart + lineNumber * 10;
                } else if (word.contains("</tooltip>")) {
                    makingTooltip = false;
                    word = "";
                } else  if (word.contains("<italic>")){
                    italic = true;
                    word = "";
                } else  if (word.contains("</italic>")){
                    italic = false;
                    word = "";
                } else  if (word.contains("<bold>")){
                    bold = true;
                    word = "";
                } else  if (word.contains("</bold>")){
                    bold = false;
                    word = "";
                }else  if (word.contains("<underlined>")){
                    underlined = true;
                    word = "";
                } else  if (word.contains("</underlined>")){
                    underlined = false;
                    word = "";
                }else  if (word.contains("<strikerThrough>")){
                    strikerThrough = true;
                    word = "";
                } else  if (word.contains("</strikerThrough>")){
                    strikerThrough = false;
                    word = "";
                } else  if (word.contains("<obfuscated>")){
                    obfuscated = true;
                    word = "";
                } else  if (word.contains("</obfuscated>")){
                    obfuscated = false;
                    word = "";
                } else if (word.contains("<scale:")){
                    scale = Float.parseFloat(word.replace("<scale:", "").replace(">", ""));
                    GL11.glScalef(scale, scale, scale);
                    height *= scale;
                    word = "";
                } else if (word.contains("</scale>")){
                    GL11.glScalef(1f, 1f, 1f);
                    height = fontRenderer.FONT_HEIGHT;
                    scale = 1f;
                    word = "";
                } else if (widthLine + widthWord <= GuiManualBase.WIDTH - unchangedXStart) {
                    widthLine += widthWord;
                    if (makingTooltip) {
                        tooltipWidth += widthWord;
                    }
                } else {
                    lineNumber++;
                    widthLine = widthWord;
                }

                fontRenderer.drawString(word, xStart + widthLine - widthWord, yStart + lineNumber * height, color);
                if (!makingTooltip && tooltipWidth > 0 && mouseX >= tooltipStartX && mouseX <= tooltipStartX + tooltipWidth && mouseY >= tooltipStartY && mouseY <= tooltipStartY + 9) {
                    ArrayList<String> toolTipText = new ArrayList<>();
                    toolTipText.add(toolTipWord);
                    ManualWidgetsBase.renderTooltip(mouseX, mouseY, toolTipText, 0xffffff, 0xf0100010);
                    tooltipWidth = 0;
                    toolTipWord = "";
                    tooltipStartX = 0;
                    tooltipStartY = 0;
                }
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glScalef(1f, 1f, 1f);
            }
        }
    }



}
