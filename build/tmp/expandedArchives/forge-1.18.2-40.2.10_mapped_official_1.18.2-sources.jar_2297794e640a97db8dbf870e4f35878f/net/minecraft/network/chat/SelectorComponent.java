package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class SelectorComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String pattern;
   @Nullable
   private final EntitySelector selector;
   protected final Optional<Component> separator;

   public SelectorComponent(String p_178516_, Optional<Component> p_178517_) {
      this.pattern = p_178516_;
      this.separator = p_178517_;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_178516_));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         LOGGER.warn("Invalid selector component: {}: {}", p_178516_, commandsyntaxexception.getMessage());
      }

      this.selector = entityselector;
   }

   public String getPattern() {
      return this.pattern;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public Optional<Component> getSeparator() {
      return this.separator;
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_131089_, @Nullable Entity p_131090_, int p_131091_) throws CommandSyntaxException {
      if (p_131089_ != null && this.selector != null) {
         Optional<? extends Component> optional = ComponentUtils.updateForEntity(p_131089_, this.separator, p_131090_, p_131091_);
         return ComponentUtils.formatList(this.selector.findEntities(p_131089_), optional, Entity::getDisplayName);
      } else {
         return new TextComponent("");
      }
   }

   public String getContents() {
      return this.pattern;
   }

   public SelectorComponent plainCopy() {
      return new SelectorComponent(this.pattern, this.separator);
   }

   public boolean equals(Object p_131094_) {
      if (this == p_131094_) {
         return true;
      } else if (!(p_131094_ instanceof SelectorComponent)) {
         return false;
      } else {
         SelectorComponent selectorcomponent = (SelectorComponent)p_131094_;
         return this.pattern.equals(selectorcomponent.pattern) && super.equals(p_131094_);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.pattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }
}