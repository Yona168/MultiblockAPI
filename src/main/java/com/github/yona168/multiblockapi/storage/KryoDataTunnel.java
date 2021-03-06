package com.github.yona168.multiblockapi.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import com.github.yona168.multiblockapi.state.MultiblockState;
import com.github.yona168.multiblockapi.storage.kryo.Kryogenic;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import static java.nio.file.Files.*;
import static java.util.stream.Collectors.toSet;

public class KryoDataTunnel extends AbstractDataTunnel {
  private final Path dataFolder;
  private final Pool<Kryo> kryoPool;

  public KryoDataTunnel(Pool<Kryo> kryoPool, Path dataFolder, Plugin plugin) {
    super(plugin);
    this.dataFolder = dataFolder;
    this.kryoPool = kryoPool;
    createDirIfNotExists(dataFolder);
  }

  public KryoDataTunnel(Path dataFolder, Plugin plugin){
    this(Kryogenic.getKryoPool(), dataFolder,plugin);
  }
  @Override
  public void initStoreAway(MultiblockState state) {
    if (state.isEnabled()) {
      throw new IllegalStateException("Enabled state is tryna be stored!");
    }
    final Chunk targetChunk = state.getTriggerChunk();
    final Path targetChunkFolder = getFilePathFor(targetChunk);
    createDirIfNotExists(targetChunkFolder);
    final Path targetFile = targetChunkFolder.resolve(state.getUniqueid().toString() + "-" + state.getMultiblock().getId().getNamespacedKey());
    try {
      Kryogenic.freeze(kryoPool.obtain(), targetFile, state);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public Collection<MultiblockState> initGetFromAfar(Chunk chunk) {
    try {
      Path targetDir = getFilePathFor(chunk);
      if (notExists(targetDir)) {
        return new HashSet<>();
      }
      return list(targetDir).map(path -> {
        try {
          MultiblockState state = Kryogenic.thaw(kryoPool.obtain(), path);
          return state;
        } catch (IOException e) {
          throw new RuntimeException("Error with Kryo parsing!");
        }
      }).collect(toSet());
    } catch (IOException e) {
      e.printStackTrace();
      return new HashSet<>();
    }
  }

  @Override
  public void initRemoveFromAfar(MultiblockState state) {
    final Path targetFile = getFilePathFor(state.getTriggerChunk()).resolve(state.getUniqueid().toString() + "-" + state.getMultiblock().getId().getNamespacedKey());
    try {
      Files.deleteIfExists(targetFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Path getFilePathFor(Chunk chunk) {
    return getFilePathFor(chunk.getWorld(), chunk.getX(), chunk.getZ());
  }

  private Path getFilePathFor(World world, int x, int z) {
    return dataFolder.resolve(world.getUID().toString()).resolve(x + "-" + z);
  }

  private void createDirIfNotExists(Path dir) {
    if (!exists(dir)) {
      try {
        createDirectories(dir);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
