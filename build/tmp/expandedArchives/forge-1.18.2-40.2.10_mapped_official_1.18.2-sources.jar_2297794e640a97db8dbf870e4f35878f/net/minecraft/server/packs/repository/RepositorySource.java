package net.minecraft.server.packs.repository;

import java.util.function.Consumer;

public interface RepositorySource {
   void loadPacks(Consumer<Pack> p_10542_, Pack.PackConstructor p_10543_);
}