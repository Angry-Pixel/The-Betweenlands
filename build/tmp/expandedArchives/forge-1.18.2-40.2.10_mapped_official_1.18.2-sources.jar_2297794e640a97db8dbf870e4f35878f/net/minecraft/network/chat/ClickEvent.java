package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent(ClickEvent.Action p_130620_, String p_130621_) {
      this.action = p_130620_;
      this.value = p_130621_;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   public boolean equals(Object p_130625_) {
      if (this == p_130625_) {
         return true;
      } else if (p_130625_ != null && this.getClass() == p_130625_.getClass()) {
         ClickEvent clickevent = (ClickEvent)p_130625_;
         if (this.action != clickevent.action) {
            return false;
         } else {
            if (this.value != null) {
               if (!this.value.equals(clickevent.value)) {
                  return false;
               }
            } else if (clickevent.value != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
   }

   public int hashCode() {
      int i = this.action.hashCode();
      return 31 * i + (this.value != null ? this.value.hashCode() : 0);
   }

   public static enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true);

      private static final Map<String, ClickEvent.Action> LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(ClickEvent.Action::getName, (p_130648_) -> {
         return p_130648_;
      }));
      private final boolean allowFromServer;
      private final String name;

      private Action(String p_130642_, boolean p_130643_) {
         this.name = p_130642_;
         this.allowFromServer = p_130643_;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getName() {
         return this.name;
      }

      public static ClickEvent.Action getByName(String p_130646_) {
         return LOOKUP.get(p_130646_);
      }
   }
}