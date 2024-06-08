package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class MappedRegistry<T> extends WritableRegistry<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList<>(256);
   private final Object2IntMap<T> toId = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityStrategy()), (p_194539_) -> {
      p_194539_.defaultReturnValue(-1);
   });
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap<>();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
   private final Map<T, Lifecycle> lifecycles = new IdentityHashMap<>();
   private Lifecycle elementsLifecycle;
   private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
   private boolean frozen;
   @Nullable
   private final Function<T, Holder.Reference<T>> customHolderProvider;
   @Nullable
   private Map<T, Holder.Reference<T>> intrusiveHolderCache;
   @Nullable
   private List<Holder.Reference<T>> holdersInOrder;
   private int nextId;

   public MappedRegistry(ResourceKey<? extends Registry<T>> p_205849_, Lifecycle p_205850_, @Nullable Function<T, Holder.Reference<T>> p_205851_) {
      super(p_205849_, p_205850_);
      this.elementsLifecycle = p_205850_;
      this.customHolderProvider = p_205851_;
      if (p_205851_ != null) {
         this.intrusiveHolderCache = new IdentityHashMap<>();
      }

   }

   private List<Holder.Reference<T>> holdersInOrder() {
      if (this.holdersInOrder == null) {
         this.holdersInOrder = this.byId.stream().filter(Objects::nonNull).toList();
      }

      return this.holdersInOrder;
   }

   private void validateWrite(ResourceKey<T> p_205922_) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + p_205922_ + ")");
      }
   }

   public Holder<T> registerMapping(int p_205853_, ResourceKey<T> p_205854_, T p_205855_, Lifecycle p_205856_) {
      return this.registerMapping(p_205853_, p_205854_, p_205855_, p_205856_, true);
   }

   private Holder<T> registerMapping(int p_205858_, ResourceKey<T> p_205859_, T p_205860_, Lifecycle p_205861_, boolean p_205862_) {
      this.validateWrite(p_205859_);
      Validate.notNull(p_205859_);
      Validate.notNull(p_205860_);
      this.byId.size(Math.max(this.byId.size(), p_205858_ + 1));
      this.toId.put(p_205860_, p_205858_);
      this.holdersInOrder = null;
      // Forge: Fix bug where a key is considered a duplicate if getOrCreateHolder was called before the entry was registered
      if (p_205862_ && this.byKey.containsKey(p_205859_) && this.byKey.get(p_205859_).isBound()) {
         Util.logAndPauseIfInIde("Adding duplicate key '" + p_205859_ + "' to registry");
      }

      if (this.byValue.containsKey(p_205860_)) {
         Util.logAndPauseIfInIde("Adding duplicate value '" + p_205860_ + "' to registry");
      }

      this.lifecycles.put(p_205860_, p_205861_);
      this.elementsLifecycle = this.elementsLifecycle.add(p_205861_);
      if (this.nextId <= p_205858_) {
         this.nextId = p_205858_ + 1;
      }

      Holder.Reference<T> reference;
      if (this.customHolderProvider != null) {
         reference = this.customHolderProvider.apply(p_205860_);
         Holder.Reference<T> reference1 = this.byKey.put(p_205859_, reference);
         if (reference1 != null && reference1 != reference) {
            throw new IllegalStateException("Invalid holder present for key " + p_205859_);
         }
      } else {
         reference = this.byKey.computeIfAbsent(p_205859_, (p_205927_) -> {
            return Holder.Reference.createStandAlone(this, p_205927_);
         });
      }

      this.byLocation.put(p_205859_.location(), reference);
      this.byValue.put(p_205860_, reference);
      reference.bind(p_205859_, p_205860_);
      this.byId.set(p_205858_, reference);
      return reference;
   }

   public Holder<T> register(ResourceKey<T> p_205891_, T p_205892_, Lifecycle p_205893_) {
      return this.registerMapping(this.nextId, p_205891_, p_205892_, p_205893_);
   }

   public Holder<T> registerOrOverride(OptionalInt p_205884_, ResourceKey<T> p_205885_, T p_205886_, Lifecycle p_205887_) {
      this.validateWrite(p_205885_);
      Validate.notNull(p_205885_);
      Validate.notNull(p_205886_);
      Holder<T> holder = this.byKey.get(p_205885_);
      T t = (T)(holder != null && holder.isBound() ? holder.value() : null);
      int i;
      if (t == null) {
         i = p_205884_.orElse(this.nextId);
      } else {
         i = this.toId.getInt(t);
         if (p_205884_.isPresent() && p_205884_.getAsInt() != i) {
            throw new IllegalStateException("ID mismatch");
         }

         this.lifecycles.remove(t);
         this.toId.removeInt(t);
         this.byValue.remove(t);
      }

      return this.registerMapping(i, p_205885_, p_205886_, p_205887_, false);
   }

   @Nullable
   public ResourceLocation getKey(T p_122746_) {
      Holder.Reference<T> reference = this.byValue.get(p_122746_);
      return reference != null ? reference.key().location() : null;
   }

   public Optional<ResourceKey<T>> getResourceKey(T p_122755_) {
      return Optional.ofNullable(this.byValue.get(p_122755_)).map(Holder.Reference::key);
   }

   public int getId(@Nullable T p_122706_) {
      return this.toId.getInt(p_122706_);
   }

   @Nullable
   public T get(@Nullable ResourceKey<T> p_122714_) {
      return getValueFromNullable(this.byKey.get(p_122714_));
   }

   @Nullable
   public T byId(int p_122684_) {
      return (T)(p_122684_ >= 0 && p_122684_ < this.byId.size() ? getValueFromNullable(this.byId.get(p_122684_)) : null);
   }

   public Optional<Holder<T>> getHolder(int p_205907_) {
      return p_205907_ >= 0 && p_205907_ < this.byId.size() ? Optional.ofNullable(this.byId.get(p_205907_)) : Optional.empty();
   }

   public Optional<Holder<T>> getHolder(ResourceKey<T> p_205905_) {
      return Optional.ofNullable(this.byKey.get(p_205905_));
   }

   public Holder<T> getOrCreateHolder(ResourceKey<T> p_205913_) {
      return this.byKey.computeIfAbsent(p_205913_, (p_205924_) -> {
         if (this.customHolderProvider != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(p_205924_);
            return Holder.Reference.createStandAlone(this, p_205924_);
         }
      });
   }

   public int size() {
      return this.byKey.size();
   }

   public Lifecycle lifecycle(T p_122764_) {
      return this.lifecycles.get(p_122764_);
   }

   public Lifecycle elementsLifecycle() {
      return this.elementsLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.holdersInOrder().iterator(), Holder::value);
   }

   @Nullable
   public T get(@Nullable ResourceLocation p_122739_) {
      Holder.Reference<T> reference = this.byLocation.get(p_122739_);
      return getValueFromNullable(reference);
   }

   @Nullable
   private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> p_205866_) {
      return (T)(p_205866_ != null ? p_205866_.value() : null);
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   public Set<Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
   }

   public Stream<Holder.Reference<T>> holders() {
      return this.holdersInOrder().stream();
   }

   public boolean isKnownTagName(TagKey<T> p_205864_) {
      return this.tags.containsKey(p_205864_);
   }

   public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
      return this.tags.entrySet().stream().map((p_211060_) -> {
         return Pair.of(p_211060_.getKey(), p_211060_.getValue());
      });
   }

   public HolderSet.Named<T> getOrCreateTag(TagKey<T> p_205895_) {
      HolderSet.Named<T> named = this.tags.get(p_205895_);
      if (named == null) {
         named = this.createTag(p_205895_);
         Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<>(this.tags);
         map.put(p_205895_, named);
         this.tags = map;
      }

      return named;
   }

   private HolderSet.Named<T> createTag(TagKey<T> p_211068_) {
      return new HolderSet.Named<>(this, p_211068_);
   }

   public Stream<TagKey<T>> getTagNames() {
      return this.tags.keySet().stream();
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional<Holder<T>> getRandom(Random p_205889_) {
      return Util.getRandomSafe(this.holdersInOrder(), p_205889_).map(Holder::hackyErase);
   }

   public boolean containsKey(ResourceLocation p_122761_) {
      return this.byLocation.containsKey(p_122761_);
   }

   public boolean containsKey(ResourceKey<T> p_175392_) {
      return this.byKey.containsKey(p_175392_);
   }

   /** @deprecated Forge: For internal use only. Use the Register events when registering values. */
   @Deprecated
   public void unfreeze() {
      this.frozen = false;
      if (this.customHolderProvider != null && this.intrusiveHolderCache == null)
         this.intrusiveHolderCache = new IdentityHashMap<>();
   }

   public Registry<T> freeze() {
      this.frozen = true;
      List<ResourceLocation> list = this.byKey.entrySet().stream().filter((p_211055_) -> {
         return !p_211055_.getValue().isBound();
      }).map((p_211794_) -> {
         return p_211794_.getKey().location();
      }).sorted().toList();
      if (!list.isEmpty()) {
         throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + list);
      } else {
         if (this.intrusiveHolderCache != null) {
            List<Holder.Reference<T>> list1 = this.intrusiveHolderCache.values().stream().filter((p_211809_) -> {
               return !p_211809_.isBound();
            }).toList();
            if (!list1.isEmpty()) {
               throw new IllegalStateException("Some intrusive holders were not added to registry: " + list1);
            }

            this.intrusiveHolderCache = null;
         }

         return this;
      }
   }

   public Holder.Reference<T> createIntrusiveHolder(T p_205915_) {
      if (this.customHolderProvider == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else if (!this.frozen && this.intrusiveHolderCache != null) {
         return this.intrusiveHolderCache.computeIfAbsent(p_205915_, (p_211813_) -> {
            return Holder.Reference.createIntrusive(this, p_211813_);
         });
      } else {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   public Optional<HolderSet.Named<T>> getTag(TagKey<T> p_205909_) {
      return Optional.ofNullable(this.tags.get(p_205909_));
   }

   public void bindTags(Map<TagKey<T>, List<Holder<T>>> p_205875_) {
      Map<Holder.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap<>();
      this.byKey.values().forEach((p_211801_) -> {
         map.put(p_211801_, new ArrayList<>());
      });
      p_205875_.forEach((p_211806_, p_211807_) -> {
         for(Holder<T> holder : p_211807_) {
            if (!holder.isValidInRegistry(this)) {
               throw new IllegalStateException("Can't create named set " + p_211806_ + " containing value " + holder + " from outside registry " + this);
            }

            if (!(holder instanceof Holder.Reference)) {
               throw new IllegalStateException("Found direct holder " + holder + " value in tag " + p_211806_);
            }

            Holder.Reference<T> reference = (Holder.Reference)holder;
            map.get(reference).add(p_211806_);
         }

      });
      Set<TagKey<T>> set = Sets.difference(this.tags.keySet(), p_205875_.keySet());
      if (!set.isEmpty()) {
         LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), set.stream().map((p_211811_) -> {
            return p_211811_.location().toString();
         }).sorted().collect(Collectors.joining(", ")));
      }

      Map<TagKey<T>, HolderSet.Named<T>> map1 = new IdentityHashMap<>(this.tags);
      p_205875_.forEach((p_211797_, p_211798_) -> {
         map1.computeIfAbsent(p_211797_, this::createTag).bind(p_211798_);
      });
      map.forEach(Holder.Reference::bindTags);
      this.tags = map1;
   }

   public void resetTags() {
      this.tags.values().forEach((p_211792_) -> {
         p_211792_.bind(List.of());
      });
      this.byKey.values().forEach((p_211803_) -> {
         p_211803_.bindTags(Set.of());
      });
   }
}
