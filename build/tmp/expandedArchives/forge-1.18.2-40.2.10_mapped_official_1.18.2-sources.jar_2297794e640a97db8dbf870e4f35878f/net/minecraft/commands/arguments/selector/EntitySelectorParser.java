package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelectorParser {
   public static final char SYNTAX_SELECTOR_START = '@';
   private static final char SYNTAX_OPTIONS_START = '[';
   private static final char SYNTAX_OPTIONS_END = ']';
   public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
   private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
   public static final char SYNTAX_NOT = '!';
   public static final char SYNTAX_TAG = '#';
   private static final char SELECTOR_NEAREST_PLAYER = 'p';
   private static final char SELECTOR_ALL_PLAYERS = 'a';
   private static final char SELECTOR_RANDOM_PLAYERS = 'r';
   private static final char SELECTOR_CURRENT_ENTITY = 's';
   private static final char SELECTOR_ALL_ENTITIES = 'e';
   public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.invalid"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((p_121301_) -> {
      return new TranslatableComponent("argument.entity.selector.unknown", p_121301_);
   });
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed"));
   public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.missing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.unterminated"));
   public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((p_121267_) -> {
      return new TranslatableComponent("argument.entity.options.valueless", p_121267_);
   });
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY = (p_121326_, p_121327_) -> {
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST = (p_121313_, p_121314_) -> {
      p_121314_.sort((p_175140_, p_175141_) -> {
         return Doubles.compare(p_175140_.distanceToSqr(p_121313_), p_175141_.distanceToSqr(p_121313_));
      });
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_FURTHEST = (p_121298_, p_121299_) -> {
      p_121299_.sort((p_175131_, p_175132_) -> {
         return Doubles.compare(p_175132_.distanceToSqr(p_121298_), p_175131_.distanceToSqr(p_121298_));
      });
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM = (p_121264_, p_121265_) -> {
      Collections.shuffle(p_121265_);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_121363_, p_121364_) -> {
      return p_121363_.buildFuture();
   };
   private final StringReader reader;
   private final boolean allowSelectors;
   private int maxResults;
   private boolean includesEntities;
   private boolean worldLimited;
   private MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
   private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private Double z;
   @Nullable
   private Double deltaX;
   @Nullable
   private Double deltaY;
   @Nullable
   private Double deltaZ;
   private WrappedMinMaxBounds rotX = WrappedMinMaxBounds.ANY;
   private WrappedMinMaxBounds rotY = WrappedMinMaxBounds.ANY;
   private Predicate<Entity> predicate = (p_121321_) -> {
      return true;
   };
   private BiConsumer<Vec3, List<? extends Entity>> order = ORDER_ARBITRARY;
   private boolean currentEntity;
   @Nullable
   private String playerName;
   private int startPosition;
   @Nullable
   private UUID entityUUID;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
   private boolean hasNameEquals;
   private boolean hasNameNotEquals;
   private boolean isLimited;
   private boolean isSorted;
   private boolean hasGamemodeEquals;
   private boolean hasGamemodeNotEquals;
   private boolean hasTeamEquals;
   private boolean hasTeamNotEquals;
   @Nullable
   private EntityType<?> type;
   private boolean typeInverse;
   private boolean hasScores;
   private boolean hasAdvancements;
   private boolean usesSelectors;

   public EntitySelectorParser(StringReader p_121218_) {
      this(p_121218_, true);
   }

   public EntitySelectorParser(StringReader p_121220_, boolean p_121221_) {
      this.reader = p_121220_;
      this.allowSelectors = p_121221_;
   }

   public EntitySelector getSelector() {
      AABB aabb;
      if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
         if (this.distance.getMax() != null) {
            double d0 = this.distance.getMax();
            aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
         } else {
            aabb = null;
         }
      } else {
         aabb = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
      }

      Function<Vec3, Vec3> function;
      if (this.x == null && this.y == null && this.z == null) {
         function = (p_121292_) -> {
            return p_121292_;
         };
      } else {
         function = (p_121258_) -> {
            return new Vec3(this.x == null ? p_121258_.x : this.x, this.y == null ? p_121258_.y : this.y, this.z == null ? p_121258_.z : this.z);
         };
      }

      return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, function, aabb, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
   }

   private AABB createAabb(double p_121234_, double p_121235_, double p_121236_) {
      boolean flag = p_121234_ < 0.0D;
      boolean flag1 = p_121235_ < 0.0D;
      boolean flag2 = p_121236_ < 0.0D;
      double d0 = flag ? p_121234_ : 0.0D;
      double d1 = flag1 ? p_121235_ : 0.0D;
      double d2 = flag2 ? p_121236_ : 0.0D;
      double d3 = (flag ? 0.0D : p_121234_) + 1.0D;
      double d4 = (flag1 ? 0.0D : p_121235_) + 1.0D;
      double d5 = (flag2 ? 0.0D : p_121236_) + 1.0D;
      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public void finalizePredicates() {
      if (this.rotX != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, Entity::getXRot));
      }

      if (this.rotY != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, Entity::getYRot));
      }

      if (!this.level.isAny()) {
         this.predicate = this.predicate.and((p_175126_) -> {
            return !(p_175126_ instanceof ServerPlayer) ? false : this.level.matches(((ServerPlayer)p_175126_).experienceLevel);
         });
      }

   }

   private Predicate<Entity> createRotationPredicate(WrappedMinMaxBounds p_121255_, ToDoubleFunction<Entity> p_121256_) {
      double d0 = (double)Mth.wrapDegrees(p_121255_.getMin() == null ? 0.0F : p_121255_.getMin());
      double d1 = (double)Mth.wrapDegrees(p_121255_.getMax() == null ? 359.0F : p_121255_.getMax());
      return (p_175137_) -> {
         double d2 = Mth.wrapDegrees(p_121256_.applyAsDouble(p_175137_));
         if (d0 > d1) {
            return d2 >= d0 || d2 <= d1;
         } else {
            return d2 >= d0 && d2 <= d1;
         }
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.usesSelectors = true;
      this.suggestions = this::suggestSelector;
      if (!this.reader.canRead()) {
         throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
      } else {
         int i = this.reader.getCursor();
         char c0 = this.reader.read();
         if (c0 == 'p') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 'a') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = false;
            this.order = ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 'r') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
         } else if (c0 == 's') {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
         } else {
            if (c0 != 'e') {
               this.reader.setCursor(i);
               throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + String.valueOf(c0));
            }

            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = true;
            this.order = ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
         }

         this.suggestions = this::suggestOpenOptions;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
         }

      }
   }

   protected void parseNameOrUUID() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestions = this::suggestName;
      }

      int i = this.reader.getCursor();
      String s = this.reader.readString();

      try {
         this.entityUUID = UUID.fromString(s);
         this.includesEntities = true;
      } catch (IllegalArgumentException illegalargumentexception) {
         if (s.isEmpty() || s.length() > 16) {
            this.reader.setCursor(i);
            throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includesEntities = false;
         this.playerName = s;
      }

      this.maxResults = 1;
   }

   public void parseOptions() throws CommandSyntaxException {
      this.suggestions = this::suggestOptionsKey;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            EntitySelectorOptions.Modifier entityselectoroptions$modifier = EntitySelectorOptions.get(this, s, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(i);
               throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            entityselectoroptions$modifier.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestOptionsKey;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestions = SUGGEST_NOTHING;
            return;
         }

         throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public boolean isTag() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addPredicate(Predicate<Entity> p_121273_) {
      this.predicate = this.predicate.and(p_121273_);
   }

   public void setWorldLimited() {
      this.worldLimited = true;
   }

   public MinMaxBounds.Doubles getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.Doubles p_175128_) {
      this.distance = p_175128_;
   }

   public MinMaxBounds.Ints getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.Ints p_121246_) {
      this.level = p_121246_;
   }

   public WrappedMinMaxBounds getRotX() {
      return this.rotX;
   }

   public void setRotX(WrappedMinMaxBounds p_121253_) {
      this.rotX = p_121253_;
   }

   public WrappedMinMaxBounds getRotY() {
      return this.rotY;
   }

   public void setRotY(WrappedMinMaxBounds p_121290_) {
      this.rotY = p_121290_;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public Double getZ() {
      return this.z;
   }

   public void setX(double p_121232_) {
      this.x = p_121232_;
   }

   public void setY(double p_121283_) {
      this.y = p_121283_;
   }

   public void setZ(double p_121306_) {
      this.z = p_121306_;
   }

   public void setDeltaX(double p_121319_) {
      this.deltaX = p_121319_;
   }

   public void setDeltaY(double p_121332_) {
      this.deltaY = p_121332_;
   }

   public void setDeltaZ(double p_121340_) {
      this.deltaZ = p_121340_;
   }

   @Nullable
   public Double getDeltaX() {
      return this.deltaX;
   }

   @Nullable
   public Double getDeltaY() {
      return this.deltaY;
   }

   @Nullable
   public Double getDeltaZ() {
      return this.deltaZ;
   }

   public void setMaxResults(int p_121238_) {
      this.maxResults = p_121238_;
   }

   public void setIncludesEntities(boolean p_121280_) {
      this.includesEntities = p_121280_;
   }

   public BiConsumer<Vec3, List<? extends Entity>> getOrder() {
      return this.order;
   }

   public void setOrder(BiConsumer<Vec3, List<? extends Entity>> p_121269_) {
      this.order = p_121269_;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestNameOrSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.allowSelectors) {
            throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         EntitySelector forgeSelector = net.minecraftforge.common.command.EntitySelectorManager.parseSelector(this);
         if (forgeSelector != null)
            return forgeSelector;
         this.parseSelector();
      } else {
         this.parseNameOrUUID();
      }

      this.finalizePredicates();
      return this.getSelector();
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder p_121248_) {
      p_121248_.suggest("@p", new TranslatableComponent("argument.entity.selector.nearestPlayer"));
      p_121248_.suggest("@a", new TranslatableComponent("argument.entity.selector.allPlayers"));
      p_121248_.suggest("@r", new TranslatableComponent("argument.entity.selector.randomPlayer"));
      p_121248_.suggest("@s", new TranslatableComponent("argument.entity.selector.self"));
      p_121248_.suggest("@e", new TranslatableComponent("argument.entity.selector.allEntities"));
      net.minecraftforge.common.command.EntitySelectorManager.fillSelectorSuggestions(p_121248_);
   }

   private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder p_121287_, Consumer<SuggestionsBuilder> p_121288_) {
      p_121288_.accept(p_121287_);
      if (this.allowSelectors) {
         fillSelectorSuggestions(p_121287_);
      }

      return p_121287_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder p_121310_, Consumer<SuggestionsBuilder> p_121311_) {
      SuggestionsBuilder suggestionsbuilder = p_121310_.createOffset(this.startPosition);
      p_121311_.accept(suggestionsbuilder);
      return p_121310_.add(suggestionsbuilder).buildFuture();
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder p_121323_, Consumer<SuggestionsBuilder> p_121324_) {
      SuggestionsBuilder suggestionsbuilder = p_121323_.createOffset(p_121323_.getStart() - 1);
      fillSelectorSuggestions(suggestionsbuilder);
      p_121323_.add(suggestionsbuilder);
      return p_121323_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder p_121334_, Consumer<SuggestionsBuilder> p_121335_) {
      p_121334_.suggest(String.valueOf('['));
      return p_121334_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder p_121342_, Consumer<SuggestionsBuilder> p_121343_) {
      p_121342_.suggest(String.valueOf(']'));
      EntitySelectorOptions.suggestNames(this, p_121342_);
      return p_121342_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder p_121348_, Consumer<SuggestionsBuilder> p_121349_) {
      EntitySelectorOptions.suggestNames(this, p_121348_);
      return p_121348_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder p_121354_, Consumer<SuggestionsBuilder> p_121355_) {
      p_121354_.suggest(String.valueOf(','));
      p_121354_.suggest(String.valueOf(']'));
      return p_121354_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder p_175144_, Consumer<SuggestionsBuilder> p_175145_) {
      p_175144_.suggest(String.valueOf('='));
      return p_175144_.buildFuture();
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }

   public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> p_121271_) {
      this.suggestions = p_121271_;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_121250_, Consumer<SuggestionsBuilder> p_121251_) {
      return this.suggestions.apply(p_121250_.createOffset(this.reader.getCursor()), p_121251_);
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean p_121303_) {
      this.hasNameEquals = p_121303_;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean p_121316_) {
      this.hasNameNotEquals = p_121316_;
   }

   public boolean isLimited() {
      return this.isLimited;
   }

   public void setLimited(boolean p_121329_) {
      this.isLimited = p_121329_;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean p_121337_) {
      this.isSorted = p_121337_;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean p_121345_) {
      this.hasGamemodeEquals = p_121345_;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean p_121351_) {
      this.hasGamemodeNotEquals = p_121351_;
   }

   public boolean hasTeamEquals() {
      return this.hasTeamEquals;
   }

   public void setHasTeamEquals(boolean p_121357_) {
      this.hasTeamEquals = p_121357_;
   }

   public boolean hasTeamNotEquals() {
      return this.hasTeamNotEquals;
   }

   public void setHasTeamNotEquals(boolean p_121360_) {
      this.hasTeamNotEquals = p_121360_;
   }

   public void limitToType(EntityType<?> p_121242_) {
      this.type = p_121242_;
   }

   public void setTypeLimitedInversely() {
      this.typeInverse = true;
   }

   public boolean isTypeLimited() {
      return this.type != null;
   }

   public boolean isTypeLimitedInversely() {
      return this.typeInverse;
   }

   public boolean hasScores() {
      return this.hasScores;
   }

   public void setHasScores(boolean p_121366_) {
      this.hasScores = p_121366_;
   }

   public boolean hasAdvancements() {
      return this.hasAdvancements;
   }

   public void setHasAdvancements(boolean p_121369_) {
      this.hasAdvancements = p_121369_;
   }
}
