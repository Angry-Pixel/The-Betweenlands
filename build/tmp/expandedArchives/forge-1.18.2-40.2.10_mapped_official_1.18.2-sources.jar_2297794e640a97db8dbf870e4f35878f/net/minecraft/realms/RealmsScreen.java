package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsScreen extends Screen {
   protected static final int TITLE_HEIGHT = 17;
   protected static final int COMPONENT_HEIGHT = 20;
   protected static final int EXPIRATION_NOTIFICATION_DAYS = 7;
   protected static final long SIZE_LIMIT = 5368709120L;
   public static final int COLOR_WHITE = 16777215;
   public static final int COLOR_GRAY = 10526880;
   protected static final int COLOR_DARK_GRAY = 5000268;
   protected static final int COLOR_MEDIUM_GRAY = 7105644;
   protected static final int COLOR_GREEN = 8388479;
   protected static final int COLOR_DARK_GREEN = 6077788;
   protected static final int COLOR_RED = 16711680;
   protected static final int COLOR_RED_FADE = 15553363;
   protected static final int COLOR_BLACK = -1073741824;
   protected static final int COLOR_YELLOW = 13413468;
   protected static final int COLOR_BRIGHT_YELLOW = -256;
   protected static final int COLOR_LINK = 3368635;
   protected static final int COLOR_LINK_HOVER = 7107012;
   protected static final int COLOR_INFO = 8226750;
   protected static final int COLOR_BUTTON_YELLOW = 16777120;
   protected static final String UPDATE_BREAKS_ADVENTURE_URL = "https://www.minecraft.net/realms/adventure-maps-in-1-9";
   protected static final int SKIN_HEAD_U = 8;
   protected static final int SKIN_HEAD_V = 8;
   protected static final int SKIN_HEAD_WIDTH = 8;
   protected static final int SKIN_HEAD_HEIGHT = 8;
   protected static final int SKIN_HAT_U = 40;
   protected static final int SKIN_HAT_V = 8;
   protected static final int SKIN_HAT_WIDTH = 8;
   protected static final int SKIN_HAT_HEIGHT = 8;
   protected static final int SKIN_TEX_WIDTH = 64;
   protected static final int SKIN_TEX_HEIGHT = 64;
   private final List<RealmsLabel> labels = Lists.newArrayList();

   public RealmsScreen(Component p_175072_) {
      super(p_175072_);
   }

   protected static int row(int p_120775_) {
      return 40 + p_120775_ * 13;
   }

   protected RealmsLabel addLabel(RealmsLabel p_175074_) {
      this.labels.add(p_175074_);
      return this.addRenderableOnly(p_175074_);
   }

   public Component createLabelNarration() {
      return CommonComponents.joinLines(this.labels.stream().map(RealmsLabel::getText).collect(Collectors.toList()));
   }
}