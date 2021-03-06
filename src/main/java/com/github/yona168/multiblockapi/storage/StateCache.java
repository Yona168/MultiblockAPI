package com.github.yona168.multiblockapi.storage;

import com.github.yona168.multiblockapi.state.Backup;
import com.github.yona168.multiblockapi.state.MultiblockState;
import com.github.yona168.multiblockapi.structure.Multiblock;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface StateCache {

  void store(MultiblockState multiblockState);

  MultiblockState getAt(Location location);

  Collection<MultiblockState> getAt(Chunk chunk);

  Collection<MultiblockState> getAt(World world);

  Collection<MultiblockState> getAll();

  void remove(MultiblockState state);

  void clear();

  default CompletableFuture<Void> backup(){
    return CompletableFuture.allOf(
    getAll().stream().filter(it->it instanceof Backup)
            .map(backupable->backupable.getMultiblock().getDataTunnel().storeAwayAsync(((Backup) backupable).snapshot()))
            .toArray(CompletableFuture[]::new));
  }
}
