package net.minecraft.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyMapping implements Comparable<KeyMapping>, net.minecraftforge.client.extensions.IForgeKeyMapping {
   private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
   private static final net.minecraftforge.client.settings.KeyBindingMap MAP = new net.minecraftforge.client.settings.KeyBindingMap();
   private static final Set<String> CATEGORIES = Sets.newHashSet();
   public static final String CATEGORY_MOVEMENT = "key.categories.movement";
   public static final String CATEGORY_MISC = "key.categories.misc";
   public static final String CATEGORY_MULTIPLAYER = "key.categories.multiplayer";
   public static final String CATEGORY_GAMEPLAY = "key.categories.gameplay";
   public static final String CATEGORY_INVENTORY = "key.categories.inventory";
   public static final String CATEGORY_INTERFACE = "key.categories.ui";
   public static final String CATEGORY_CREATIVE = "key.categories.creative";
   private static final Map<String, Integer> CATEGORY_SORT_ORDER = Util.make(Maps.newHashMap(), (p_90845_) -> {
      p_90845_.put("key.categories.movement", 1);
      p_90845_.put("key.categories.gameplay", 2);
      p_90845_.put("key.categories.inventory", 3);
      p_90845_.put("key.categories.creative", 4);
      p_90845_.put("key.categories.multiplayer", 5);
      p_90845_.put("key.categories.ui", 6);
      p_90845_.put("key.categories.misc", 7);
   });
   private final String name;
   private final InputConstants.Key defaultKey;
   private final String category;
   private InputConstants.Key key;
   boolean isDown;
   private int clickCount;

   public static void click(InputConstants.Key p_90836_) {
      KeyMapping keymapping = MAP.lookupActive(p_90836_);
      if (keymapping != null) {
         ++keymapping.clickCount;
      }

   }

   public static void set(InputConstants.Key p_90838_, boolean p_90839_) {
      for (KeyMapping keymapping : MAP.lookupAll(p_90838_))
      if (keymapping != null) {
         keymapping.setDown(p_90839_);
      }

   }

   public static void setAll() {
      for(KeyMapping keymapping : ALL.values()) {
         if (keymapping.key.getType() == InputConstants.Type.KEYSYM && keymapping.key.getValue() != InputConstants.UNKNOWN.getValue()) {
            keymapping.setDown(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keymapping.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      for(KeyMapping keymapping : ALL.values()) {
         keymapping.release();
      }

   }

   public static void resetMapping() {
      MAP.clearMap();

      for(KeyMapping keymapping : ALL.values()) {
         MAP.addKey(keymapping.key, keymapping);
      }

   }

   public KeyMapping(String p_90821_, int p_90822_, String p_90823_) {
      this(p_90821_, InputConstants.Type.KEYSYM, p_90822_, p_90823_);
   }

   public KeyMapping(String p_90825_, InputConstants.Type p_90826_, int p_90827_, String p_90828_) {
      this.name = p_90825_;
      this.key = p_90826_.getOrCreate(p_90827_);
      this.defaultKey = this.key;
      this.category = p_90828_;
      ALL.put(p_90825_, this);
      MAP.addKey(this.key, this);
      CATEGORIES.add(p_90828_);
   }

   public boolean isDown() {
      return this.isDown && isConflictContextAndModifierActive();
   }

   public String getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if (this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   private void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   public String getName() {
      return this.name;
   }

   public InputConstants.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputConstants.Key p_90849_) {
      this.key = p_90849_;
   }

   public int compareTo(KeyMapping p_90841_) {
      if (this.category.equals(p_90841_.category)) return I18n.get(this.name).compareTo(I18n.get(p_90841_.name));
      Integer tCat = CATEGORY_SORT_ORDER.get(this.category);
      Integer oCat = CATEGORY_SORT_ORDER.get(p_90841_.category);
      if (tCat == null && oCat != null) return 1;
      if (tCat != null && oCat == null) return -1;
      if (tCat == null && oCat == null) return I18n.get(this.category).compareTo(I18n.get(p_90841_.category));
      return  tCat.compareTo(oCat);
   }

   public static Supplier<Component> createNameSupplier(String p_90843_) {
      KeyMapping keymapping = ALL.get(p_90843_);
      return keymapping == null ? () -> {
         return new TranslatableComponent(p_90843_);
      } : keymapping::getTranslatedKeyMessage;
   }

   public boolean same(KeyMapping p_90851_) {
      if (getKeyConflictContext().conflicts(p_90851_.getKeyConflictContext()) || p_90851_.getKeyConflictContext().conflicts(getKeyConflictContext())) {
         net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
         net.minecraftforge.client.settings.KeyModifier otherKeyModifier = p_90851_.getKeyModifier();
         if (keyModifier.matches(p_90851_.getKey()) || otherKeyModifier.matches(getKey())) {
            return true;
         } else if (getKey().equals(p_90851_.getKey())) {
            // IN_GAME key contexts have a conflict when at least one modifier is NONE.
            // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
            // GUI and other key contexts do not have this limitation.
            return keyModifier == otherKeyModifier ||
               (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
               (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
         }
      }
      return this.key.equals(p_90851_.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputConstants.UNKNOWN);
   }

   public boolean matches(int p_90833_, int p_90834_) {
      if (p_90833_ == InputConstants.UNKNOWN.getValue()) {
         return this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == p_90834_;
      } else {
         return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == p_90833_;
      }
   }

   public boolean matchesMouse(int p_90831_) {
      return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == p_90831_;
   }

   public Component getTranslatedKeyMessage() {
      return getKeyModifier().getCombinedName(key, () -> {
      return this.key.getDisplayName();
      });
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey) && getKeyModifier() == getDefaultKeyModifier();
   }

   public String saveString() {
      return this.key.getName();
   }

   public void setDown(boolean p_90846_) {
      this.isDown = p_90846_;
   }

   /****************** Forge Start *****************************/
   private net.minecraftforge.client.settings.KeyModifier keyModifierDefault = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.KeyModifier keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyMapping(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, final InputConstants.Type inputType, final int keyCode, String category) {
       this(description, keyConflictContext, inputType.getOrCreate(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyMapping(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, InputConstants.Key keyCode, String category) {
       this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyMapping(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, final InputConstants.Type inputType, final int keyCode, String category) {
       this(description, keyConflictContext, keyModifier, inputType.getOrCreate(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyMapping(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, InputConstants.Key keyCode, String category) {
      this.name = description;
      this.key = keyCode;
      this.defaultKey = keyCode;
      this.category = category;
      this.keyConflictContext = keyConflictContext;
      this.keyModifier = keyModifier;
      this.keyModifierDefault = keyModifier;
      if (this.keyModifier.matches(keyCode))
         this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
      ALL.put(description, this);
      MAP.addKey(keyCode, this);
      CATEGORIES.add(category);
   }

   @Override
   public InputConstants.Key getKey() {
       return this.key;
   }

   @Override
   public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext) {
       this.keyConflictContext = keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext() {
       return keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getDefaultKeyModifier() {
       return keyModifierDefault;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getKeyModifier() {
       return keyModifier;
   }

   @Override
   public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, InputConstants.Key keyCode) {
       this.key = keyCode;
       if (keyModifier.matches(keyCode))
           keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
       MAP.removeKey(this);
       this.keyModifier = keyModifier;
       MAP.addKey(keyCode, this);
   }
   /****************** Forge End *****************************/
}
