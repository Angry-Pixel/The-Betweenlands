package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext<CommandSourceStack> p_96836_, String p_96837_) throws CommandSyntaxException {
      return p_96836_.getArgument(p_96837_, MessageArgument.Message.class).toComponent(p_96836_.getSource(), p_96836_.getSource().hasPermission(2));
   }

   public MessageArgument.Message parse(StringReader p_96834_) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(p_96834_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Message {
      private final String text;
      private final MessageArgument.Part[] parts;

      public Message(String p_96844_, MessageArgument.Part[] p_96845_) {
         this.text = p_96844_;
         this.parts = p_96845_;
      }

      public String getText() {
         return this.text;
      }

      public MessageArgument.Part[] getParts() {
         return this.parts;
      }

      public Component toComponent(CommandSourceStack p_96850_, boolean p_96851_) throws CommandSyntaxException {
         if (this.parts.length != 0 && p_96851_) {
            MutableComponent mutablecomponent = new TextComponent(this.text.substring(0, this.parts[0].getStart()));
            int i = this.parts[0].getStart();

            for(MessageArgument.Part messageargument$part : this.parts) {
               Component component = messageargument$part.toComponent(p_96850_);
               if (i < messageargument$part.getStart()) {
                  mutablecomponent.append(this.text.substring(i, messageargument$part.getStart()));
               }

               if (component != null) {
                  mutablecomponent.append(component);
               }

               i = messageargument$part.getEnd();
            }

            if (i < this.text.length()) {
               mutablecomponent.append(this.text.substring(i));
            }

            return mutablecomponent;
         } else {
            return new TextComponent(this.text);
         }
      }

      public static MessageArgument.Message parseText(StringReader p_96847_, boolean p_96848_) throws CommandSyntaxException {
         String s = p_96847_.getString().substring(p_96847_.getCursor(), p_96847_.getTotalLength());
         if (!p_96848_) {
            p_96847_.setCursor(p_96847_.getTotalLength());
            return new MessageArgument.Message(s, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> list = Lists.newArrayList();
            int i = p_96847_.getCursor();

            while(true) {
               int j;
               EntitySelector entityselector;
               while(true) {
                  if (!p_96847_.canRead()) {
                     return new MessageArgument.Message(s, list.toArray(new MessageArgument.Part[0]));
                  }

                  if (p_96847_.peek() == '@') {
                     j = p_96847_.getCursor();

                     try {
                        EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_96847_);
                        entityselector = entityselectorparser.parse();
                        break;
                     } catch (CommandSyntaxException commandsyntaxexception) {
                        if (commandsyntaxexception.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE && commandsyntaxexception.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                           throw commandsyntaxexception;
                        }

                        p_96847_.setCursor(j + 1);
                     }
                  } else {
                     p_96847_.skip();
                  }
               }

               list.add(new MessageArgument.Part(j - i, p_96847_.getCursor() - i, entityselector));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int p_96856_, int p_96857_, EntitySelector p_96858_) {
         this.start = p_96856_;
         this.end = p_96857_;
         this.selector = p_96858_;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      public EntitySelector getSelector() {
         return this.selector;
      }

      @Nullable
      public Component toComponent(CommandSourceStack p_96861_) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.findEntities(p_96861_));
      }
   }
}