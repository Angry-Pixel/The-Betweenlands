package net.minecraft.network.chat;

public class TextComponent extends BaseComponent {
   public static final Component EMPTY = new TextComponent("");
   private final String text;

   public TextComponent(String p_131286_) {
      this.text = p_131286_;
   }

   public String getText() {
      return this.text;
   }

   public String getContents() {
      return this.text;
   }

   public TextComponent plainCopy() {
      return new TextComponent(this.text);
   }

   public boolean equals(Object p_131290_) {
      if (this == p_131290_) {
         return true;
      } else if (!(p_131290_ instanceof TextComponent)) {
         return false;
      } else {
         TextComponent textcomponent = (TextComponent)p_131290_;
         return this.text.equals(textcomponent.getText()) && super.equals(p_131290_);
      }
   }

   public String toString() {
      return "TextComponent{text='" + this.text + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }
}