package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgument implements ArgumentType<NbtPathArgument.NbtPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
   public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType(new TranslatableComponent("arguments.nbtpath.node.invalid"));
   public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((p_99502_) -> {
      return new TranslatableComponent("arguments.nbtpath.nothing_found", p_99502_);
   });
   private static final char INDEX_MATCH_START = '[';
   private static final char INDEX_MATCH_END = ']';
   private static final char KEY_MATCH_START = '{';
   private static final char KEY_MATCH_END = '}';
   private static final char QUOTED_KEY_START = '"';

   public static NbtPathArgument nbtPath() {
      return new NbtPathArgument();
   }

   public static NbtPathArgument.NbtPath getPath(CommandContext<CommandSourceStack> p_99499_, String p_99500_) {
      return p_99499_.getArgument(p_99500_, NbtPathArgument.NbtPath.class);
   }

   public NbtPathArgument.NbtPath parse(StringReader p_99491_) throws CommandSyntaxException {
      List<NbtPathArgument.Node> list = Lists.newArrayList();
      int i = p_99491_.getCursor();
      Object2IntMap<NbtPathArgument.Node> object2intmap = new Object2IntOpenHashMap<>();
      boolean flag = true;

      while(p_99491_.canRead() && p_99491_.peek() != ' ') {
         NbtPathArgument.Node nbtpathargument$node = parseNode(p_99491_, flag);
         list.add(nbtpathargument$node);
         object2intmap.put(nbtpathargument$node, p_99491_.getCursor() - i);
         flag = false;
         if (p_99491_.canRead()) {
            char c0 = p_99491_.peek();
            if (c0 != ' ' && c0 != '[' && c0 != '{') {
               p_99491_.expect('.');
            }
         }
      }

      return new NbtPathArgument.NbtPath(p_99491_.getString().substring(i, p_99491_.getCursor()), list.toArray(new NbtPathArgument.Node[0]), object2intmap);
   }

   private static NbtPathArgument.Node parseNode(StringReader p_99496_, boolean p_99497_) throws CommandSyntaxException {
      switch(p_99496_.peek()) {
      case '"':
         String s = p_99496_.readString();
         return readObjectNode(p_99496_, s);
      case '[':
         p_99496_.skip();
         int j = p_99496_.peek();
         if (j == 123) {
            CompoundTag compoundtag1 = (new TagParser(p_99496_)).readStruct();
            p_99496_.expect(']');
            return new NbtPathArgument.MatchElementNode(compoundtag1);
         } else {
            if (j == 93) {
               p_99496_.skip();
               return NbtPathArgument.AllElementsNode.INSTANCE;
            }

            int i = p_99496_.readInt();
            p_99496_.expect(']');
            return new NbtPathArgument.IndexedElementNode(i);
         }
      case '{':
         if (!p_99497_) {
            throw ERROR_INVALID_NODE.createWithContext(p_99496_);
         }

         CompoundTag compoundtag = (new TagParser(p_99496_)).readStruct();
         return new NbtPathArgument.MatchRootObjectNode(compoundtag);
      default:
         String s1 = readUnquotedName(p_99496_);
         return readObjectNode(p_99496_, s1);
      }
   }

   private static NbtPathArgument.Node readObjectNode(StringReader p_99493_, String p_99494_) throws CommandSyntaxException {
      if (p_99493_.canRead() && p_99493_.peek() == '{') {
         CompoundTag compoundtag = (new TagParser(p_99493_)).readStruct();
         return new NbtPathArgument.MatchObjectNode(p_99494_, compoundtag);
      } else {
         return new NbtPathArgument.CompoundChildNode(p_99494_);
      }
   }

   private static String readUnquotedName(StringReader p_99509_) throws CommandSyntaxException {
      int i = p_99509_.getCursor();

      while(p_99509_.canRead() && isAllowedInUnquotedName(p_99509_.peek())) {
         p_99509_.skip();
      }

      if (p_99509_.getCursor() == i) {
         throw ERROR_INVALID_NODE.createWithContext(p_99509_);
      } else {
         return p_99509_.getString().substring(i, p_99509_.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isAllowedInUnquotedName(char p_99489_) {
      return p_99489_ != ' ' && p_99489_ != '"' && p_99489_ != '[' && p_99489_ != ']' && p_99489_ != '.' && p_99489_ != '{' && p_99489_ != '}';
   }

   static Predicate<Tag> createTagPredicate(CompoundTag p_99511_) {
      return (p_99507_) -> {
         return NbtUtils.compareNbt(p_99511_, p_99507_, true);
      };
   }

   static class AllElementsNode implements NbtPathArgument.Node {
      public static final NbtPathArgument.AllElementsNode INSTANCE = new NbtPathArgument.AllElementsNode();

      private AllElementsNode() {
      }

      public void getTag(Tag p_99522_, List<Tag> p_99523_) {
         if (p_99522_ instanceof CollectionTag) {
            p_99523_.addAll((CollectionTag)p_99522_);
         }

      }

      public void getOrCreateTag(Tag p_99528_, Supplier<Tag> p_99529_, List<Tag> p_99530_) {
         if (p_99528_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_99528_;
            if (collectiontag.isEmpty()) {
               Tag tag = p_99529_.get();
               if (collectiontag.addTag(0, tag)) {
                  p_99530_.add(tag);
               }
            } else {
               p_99530_.addAll(collectiontag);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag p_99525_, Supplier<Tag> p_99526_) {
         if (!(p_99525_ instanceof CollectionTag)) {
            return 0;
         } else {
            CollectionTag<?> collectiontag = (CollectionTag)p_99525_;
            int i = collectiontag.size();
            if (i == 0) {
               collectiontag.addTag(0, p_99526_.get());
               return 1;
            } else {
               Tag tag = p_99526_.get();
               int j = i - (int)collectiontag.stream().filter(tag::equals).count();
               if (j == 0) {
                  return 0;
               } else {
                  collectiontag.clear();
                  if (!collectiontag.addTag(0, tag)) {
                     return 0;
                  } else {
                     for(int k = 1; k < i; ++k) {
                        collectiontag.addTag(k, p_99526_.get());
                     }

                     return j;
                  }
               }
            }
         }
      }

      public int removeTag(Tag p_99520_) {
         if (p_99520_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_99520_;
            int i = collectiontag.size();
            if (i > 0) {
               collectiontag.clear();
               return i;
            }
         }

         return 0;
      }
   }

   static class CompoundChildNode implements NbtPathArgument.Node {
      private final String name;

      public CompoundChildNode(String p_99533_) {
         this.name = p_99533_;
      }

      public void getTag(Tag p_99538_, List<Tag> p_99539_) {
         if (p_99538_ instanceof CompoundTag) {
            Tag tag = ((CompoundTag)p_99538_).get(this.name);
            if (tag != null) {
               p_99539_.add(tag);
            }
         }

      }

      public void getOrCreateTag(Tag p_99544_, Supplier<Tag> p_99545_, List<Tag> p_99546_) {
         if (p_99544_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99544_;
            Tag tag;
            if (compoundtag.contains(this.name)) {
               tag = compoundtag.get(this.name);
            } else {
               tag = p_99545_.get();
               compoundtag.put(this.name, tag);
            }

            p_99546_.add(tag);
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag p_99541_, Supplier<Tag> p_99542_) {
         if (p_99541_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99541_;
            Tag tag = p_99542_.get();
            Tag tag1 = compoundtag.put(this.name, tag);
            if (!tag.equals(tag1)) {
               return 1;
            }
         }

         return 0;
      }

      public int removeTag(Tag p_99536_) {
         if (p_99536_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99536_;
            if (compoundtag.contains(this.name)) {
               compoundtag.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class IndexedElementNode implements NbtPathArgument.Node {
      private final int index;

      public IndexedElementNode(int p_99549_) {
         this.index = p_99549_;
      }

      public void getTag(Tag p_99554_, List<Tag> p_99555_) {
         if (p_99554_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_99554_;
            int i = collectiontag.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               p_99555_.add(collectiontag.get(j));
            }
         }

      }

      public void getOrCreateTag(Tag p_99560_, Supplier<Tag> p_99561_, List<Tag> p_99562_) {
         this.getTag(p_99560_, p_99562_);
      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag p_99557_, Supplier<Tag> p_99558_) {
         if (p_99557_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_99557_;
            int i = collectiontag.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               Tag tag = collectiontag.get(j);
               Tag tag1 = p_99558_.get();
               if (!tag1.equals(tag) && collectiontag.setTag(j, tag1)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag p_99552_) {
         if (p_99552_ instanceof CollectionTag) {
            CollectionTag<?> collectiontag = (CollectionTag)p_99552_;
            int i = collectiontag.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               collectiontag.remove(j);
               return 1;
            }
         }

         return 0;
      }
   }

   static class MatchElementNode implements NbtPathArgument.Node {
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchElementNode(CompoundTag p_99566_) {
         this.pattern = p_99566_;
         this.predicate = NbtPathArgument.createTagPredicate(p_99566_);
      }

      public void getTag(Tag p_99575_, List<Tag> p_99576_) {
         if (p_99575_ instanceof ListTag) {
            ListTag listtag = (ListTag)p_99575_;
            listtag.stream().filter(this.predicate).forEach(p_99576_::add);
         }

      }

      public void getOrCreateTag(Tag p_99581_, Supplier<Tag> p_99582_, List<Tag> p_99583_) {
         MutableBoolean mutableboolean = new MutableBoolean();
         if (p_99581_ instanceof ListTag) {
            ListTag listtag = (ListTag)p_99581_;
            listtag.stream().filter(this.predicate).forEach((p_99571_) -> {
               p_99583_.add(p_99571_);
               mutableboolean.setTrue();
            });
            if (mutableboolean.isFalse()) {
               CompoundTag compoundtag = this.pattern.copy();
               listtag.add(compoundtag);
               p_99583_.add(compoundtag);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag p_99578_, Supplier<Tag> p_99579_) {
         int i = 0;
         if (p_99578_ instanceof ListTag) {
            ListTag listtag = (ListTag)p_99578_;
            int j = listtag.size();
            if (j == 0) {
               listtag.add(p_99579_.get());
               ++i;
            } else {
               for(int k = 0; k < j; ++k) {
                  Tag tag = listtag.get(k);
                  if (this.predicate.test(tag)) {
                     Tag tag1 = p_99579_.get();
                     if (!tag1.equals(tag) && listtag.setTag(k, tag1)) {
                        ++i;
                     }
                  }
               }
            }
         }

         return i;
      }

      public int removeTag(Tag p_99573_) {
         int i = 0;
         if (p_99573_ instanceof ListTag) {
            ListTag listtag = (ListTag)p_99573_;

            for(int j = listtag.size() - 1; j >= 0; --j) {
               if (this.predicate.test(listtag.get(j))) {
                  listtag.remove(j);
                  ++i;
               }
            }
         }

         return i;
      }
   }

   static class MatchObjectNode implements NbtPathArgument.Node {
      private final String name;
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchObjectNode(String p_99588_, CompoundTag p_99589_) {
         this.name = p_99588_;
         this.pattern = p_99589_;
         this.predicate = NbtPathArgument.createTagPredicate(p_99589_);
      }

      public void getTag(Tag p_99594_, List<Tag> p_99595_) {
         if (p_99594_ instanceof CompoundTag) {
            Tag tag = ((CompoundTag)p_99594_).get(this.name);
            if (this.predicate.test(tag)) {
               p_99595_.add(tag);
            }
         }

      }

      public void getOrCreateTag(Tag p_99600_, Supplier<Tag> p_99601_, List<Tag> p_99602_) {
         if (p_99600_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99600_;
            Tag tag = compoundtag.get(this.name);
            if (tag == null) {
               Tag compoundtag1 = this.pattern.copy();
               compoundtag.put(this.name, compoundtag1);
               p_99602_.add(compoundtag1);
            } else if (this.predicate.test(tag)) {
               p_99602_.add(tag);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag p_99597_, Supplier<Tag> p_99598_) {
         if (p_99597_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99597_;
            Tag tag = compoundtag.get(this.name);
            if (this.predicate.test(tag)) {
               Tag tag1 = p_99598_.get();
               if (!tag1.equals(tag)) {
                  compoundtag.put(this.name, tag1);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag p_99592_) {
         if (p_99592_ instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)p_99592_;
            Tag tag = compoundtag.get(this.name);
            if (this.predicate.test(tag)) {
               compoundtag.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class MatchRootObjectNode implements NbtPathArgument.Node {
      private final Predicate<Tag> predicate;

      public MatchRootObjectNode(CompoundTag p_99605_) {
         this.predicate = NbtPathArgument.createTagPredicate(p_99605_);
      }

      public void getTag(Tag p_99610_, List<Tag> p_99611_) {
         if (p_99610_ instanceof CompoundTag && this.predicate.test(p_99610_)) {
            p_99611_.add(p_99610_);
         }

      }

      public void getOrCreateTag(Tag p_99616_, Supplier<Tag> p_99617_, List<Tag> p_99618_) {
         this.getTag(p_99616_, p_99618_);
      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag p_99613_, Supplier<Tag> p_99614_) {
         return 0;
      }

      public int removeTag(Tag p_99608_) {
         return 0;
      }
   }

   public static class NbtPath {
      private final String original;
      private final Object2IntMap<NbtPathArgument.Node> nodeToOriginalPosition;
      private final NbtPathArgument.Node[] nodes;

      public NbtPath(String p_99623_, NbtPathArgument.Node[] p_99624_, Object2IntMap<NbtPathArgument.Node> p_99625_) {
         this.original = p_99623_;
         this.nodes = p_99624_;
         this.nodeToOriginalPosition = p_99625_;
      }

      public List<Tag> get(Tag p_99639_) throws CommandSyntaxException {
         List<Tag> list = Collections.singletonList(p_99639_);

         for(NbtPathArgument.Node nbtpathargument$node : this.nodes) {
            list = nbtpathargument$node.get(list);
            if (list.isEmpty()) {
               throw this.createNotFoundException(nbtpathargument$node);
            }
         }

         return list;
      }

      public int countMatching(Tag p_99644_) {
         List<Tag> list = Collections.singletonList(p_99644_);

         for(NbtPathArgument.Node nbtpathargument$node : this.nodes) {
            list = nbtpathargument$node.get(list);
            if (list.isEmpty()) {
               return 0;
            }
         }

         return list.size();
      }

      private List<Tag> getOrCreateParents(Tag p_99651_) throws CommandSyntaxException {
         List<Tag> list = Collections.singletonList(p_99651_);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            NbtPathArgument.Node nbtpathargument$node = this.nodes[i];
            int j = i + 1;
            list = nbtpathargument$node.getOrCreate(list, this.nodes[j]::createPreferredParentTag);
            if (list.isEmpty()) {
               throw this.createNotFoundException(nbtpathargument$node);
            }
         }

         return list;
      }

      public List<Tag> getOrCreate(Tag p_99641_, Supplier<Tag> p_99642_) throws CommandSyntaxException {
         List<Tag> list = this.getOrCreateParents(p_99641_);
         NbtPathArgument.Node nbtpathargument$node = this.nodes[this.nodes.length - 1];
         return nbtpathargument$node.getOrCreate(list, p_99642_);
      }

      private static int apply(List<Tag> p_99636_, Function<Tag, Integer> p_99637_) {
         return p_99636_.stream().map(p_99637_).reduce(0, (p_99633_, p_99634_) -> {
            return p_99633_ + p_99634_;
         });
      }

      public int set(Tag p_169536_, Tag p_169537_) throws CommandSyntaxException {
         return this.set(p_169536_, p_169537_::copy);
      }

      public int set(Tag p_99646_, Supplier<Tag> p_99647_) throws CommandSyntaxException {
         List<Tag> list = this.getOrCreateParents(p_99646_);
         NbtPathArgument.Node nbtpathargument$node = this.nodes[this.nodes.length - 1];
         return apply(list, (p_99631_) -> {
            return nbtpathargument$node.setTag(p_99631_, p_99647_);
         });
      }

      public int remove(Tag p_99649_) {
         List<Tag> list = Collections.singletonList(p_99649_);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            list = this.nodes[i].get(list);
         }

         NbtPathArgument.Node nbtpathargument$node = this.nodes[this.nodes.length - 1];
         return apply(list, nbtpathargument$node::removeTag);
      }

      private CommandSyntaxException createNotFoundException(NbtPathArgument.Node p_99627_) {
         int i = this.nodeToOriginalPosition.getInt(p_99627_);
         return NbtPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, i));
      }

      public String toString() {
         return this.original;
      }
   }

   interface Node {
      void getTag(Tag p_99666_, List<Tag> p_99667_);

      void getOrCreateTag(Tag p_99670_, Supplier<Tag> p_99671_, List<Tag> p_99672_);

      Tag createPreferredParentTag();

      int setTag(Tag p_99668_, Supplier<Tag> p_99669_);

      int removeTag(Tag p_99665_);

      default List<Tag> get(List<Tag> p_99654_) {
         return this.collect(p_99654_, this::getTag);
      }

      default List<Tag> getOrCreate(List<Tag> p_99659_, Supplier<Tag> p_99660_) {
         return this.collect(p_99659_, (p_99663_, p_99664_) -> {
            this.getOrCreateTag(p_99663_, p_99660_, p_99664_);
         });
      }

      default List<Tag> collect(List<Tag> p_99656_, BiConsumer<Tag, List<Tag>> p_99657_) {
         List<Tag> list = Lists.newArrayList();

         for(Tag tag : p_99656_) {
            p_99657_.accept(tag, list);
         }

         return list;
      }
   }
}