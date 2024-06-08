package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelector {
   public static final int INFINITE = Integer.MAX_VALUE;
   private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
      public Entity tryCast(Entity p_175109_) {
         return p_175109_;
      }

      public Class<? extends Entity> getBaseClass() {
         return Entity.class;
      }
   };
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final Predicate<Entity> predicate;
   private final MinMaxBounds.Doubles range;
   private final Function<Vec3, Vec3> position;
   @Nullable
   private final AABB aabb;
   private final BiConsumer<Vec3, List<? extends Entity>> order;
   private final boolean currentEntity;
   @Nullable
   private final String playerName;
   @Nullable
   private final UUID entityUUID;
   private EntityTypeTest<Entity, ?> type;
   private final boolean usesSelector;

   public EntitySelector(int p_121125_, boolean p_121126_, boolean p_121127_, Predicate<Entity> p_121128_, MinMaxBounds.Doubles p_121129_, Function<Vec3, Vec3> p_121130_, @Nullable AABB p_121131_, BiConsumer<Vec3, List<? extends Entity>> p_121132_, boolean p_121133_, @Nullable String p_121134_, @Nullable UUID p_121135_, @Nullable EntityType<?> p_121136_, boolean p_121137_) {
      this.maxResults = p_121125_;
      this.includesEntities = p_121126_;
      this.worldLimited = p_121127_;
      this.predicate = p_121128_;
      this.range = p_121129_;
      this.position = p_121130_;
      this.aabb = p_121131_;
      this.order = p_121132_;
      this.currentEntity = p_121133_;
      this.playerName = p_121134_;
      this.entityUUID = p_121135_;
      this.type = (EntityTypeTest<Entity, ?>)(p_121136_ == null ? ANY_TYPE : p_121136_);
      this.usesSelector = p_121137_;
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isSelfSelector() {
      return this.currentEntity;
   }

   public boolean isWorldLimited() {
      return this.worldLimited;
   }

   public boolean usesSelector() {
      return this.usesSelector;
   }

   private void checkPermissions(CommandSourceStack p_121169_) throws CommandSyntaxException {
      if (this.usesSelector && !p_121169_.hasPermission(2)) {
         throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
      }
   }

   public Entity findSingleEntity(CommandSourceStack p_121140_) throws CommandSyntaxException {
      this.checkPermissions(p_121140_);
      List<? extends Entity> list = this.findEntities(p_121140_);
      if (list.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else if (list.size() > 1) {
         throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
      } else {
         return list.get(0);
      }
   }

   public List<? extends Entity> findEntities(CommandSourceStack p_121161_) throws CommandSyntaxException {
      this.checkPermissions(p_121161_);
      if (!this.includesEntities) {
         return this.findPlayers(p_121161_);
      } else if (this.playerName != null) {
         ServerPlayer serverplayer = p_121161_.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<? extends Entity>)(serverplayer == null ? Collections.emptyList() : Lists.newArrayList(serverplayer));
      } else if (this.entityUUID != null) {
         for(ServerLevel serverlevel1 : p_121161_.getServer().getAllLevels()) {
            Entity entity = serverlevel1.getEntity(this.entityUUID);
            if (entity != null) {
               return Lists.newArrayList(entity);
            }
         }

         return Collections.emptyList();
      } else {
         Vec3 vec3 = this.position.apply(p_121161_.getPosition());
         Predicate<Entity> predicate = this.getPredicate(vec3);
         if (this.currentEntity) {
            return (List<? extends Entity>)(p_121161_.getEntity() != null && predicate.test(p_121161_.getEntity()) ? Lists.newArrayList(p_121161_.getEntity()) : Collections.emptyList());
         } else {
            List<Entity> list = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.addEntities(list, p_121161_.getLevel(), vec3, predicate);
            } else {
               for(ServerLevel serverlevel : p_121161_.getServer().getAllLevels()) {
                  this.addEntities(list, serverlevel, vec3, predicate);
               }
            }

            return this.sortAndLimit(vec3, list);
         }
      }
   }

   private void addEntities(List<Entity> p_121155_, ServerLevel p_121156_, Vec3 p_121157_, Predicate<Entity> p_121158_) {
      if (this.aabb != null) {
         p_121155_.addAll(p_121156_.getEntities(this.type, this.aabb.move(p_121157_), p_121158_));
      } else {
         p_121155_.addAll(p_121156_.getEntities(this.type, p_121158_));
      }

   }

   public ServerPlayer findSinglePlayer(CommandSourceStack p_121164_) throws CommandSyntaxException {
      this.checkPermissions(p_121164_);
      List<ServerPlayer> list = this.findPlayers(p_121164_);
      if (list.size() != 1) {
         throw EntityArgument.NO_PLAYERS_FOUND.create();
      } else {
         return list.get(0);
      }
   }

   public List<ServerPlayer> findPlayers(CommandSourceStack p_121167_) throws CommandSyntaxException {
      this.checkPermissions(p_121167_);
      if (this.playerName != null) {
         ServerPlayer serverplayer2 = p_121167_.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<ServerPlayer>)(serverplayer2 == null ? Collections.emptyList() : Lists.newArrayList(serverplayer2));
      } else if (this.entityUUID != null) {
         ServerPlayer serverplayer1 = p_121167_.getServer().getPlayerList().getPlayer(this.entityUUID);
         return (List<ServerPlayer>)(serverplayer1 == null ? Collections.emptyList() : Lists.newArrayList(serverplayer1));
      } else {
         Vec3 vec3 = this.position.apply(p_121167_.getPosition());
         Predicate<Entity> predicate = this.getPredicate(vec3);
         if (this.currentEntity) {
            if (p_121167_.getEntity() instanceof ServerPlayer) {
               ServerPlayer serverplayer3 = (ServerPlayer)p_121167_.getEntity();
               if (predicate.test(serverplayer3)) {
                  return Lists.newArrayList(serverplayer3);
               }
            }

            return Collections.emptyList();
         } else {
            List<ServerPlayer> list;
            if (this.isWorldLimited()) {
               list = p_121167_.getLevel().getPlayers(predicate);
            } else {
               list = Lists.newArrayList();

               for(ServerPlayer serverplayer : p_121167_.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(serverplayer)) {
                     list.add(serverplayer);
                  }
               }
            }

            return this.sortAndLimit(vec3, list);
         }
      }
   }

   private Predicate<Entity> getPredicate(Vec3 p_121145_) {
      Predicate<Entity> predicate = this.predicate;
      if (this.aabb != null) {
         AABB aabb = this.aabb.move(p_121145_);
         predicate = predicate.and((p_121143_) -> {
            return aabb.intersects(p_121143_.getBoundingBox());
         });
      }

      if (!this.range.isAny()) {
         predicate = predicate.and((p_121148_) -> {
            return this.range.matchesSqr(p_121148_.distanceToSqr(p_121145_));
         });
      }

      return predicate;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3 p_121150_, List<T> p_121151_) {
      if (p_121151_.size() > 1) {
         this.order.accept(p_121150_, p_121151_);
      }

      return p_121151_.subList(0, Math.min(this.maxResults, p_121151_.size()));
   }

   public static Component joinNames(List<? extends Entity> p_175104_) {
      return ComponentUtils.formatList(p_175104_, Entity::getDisplayName);
   }
}