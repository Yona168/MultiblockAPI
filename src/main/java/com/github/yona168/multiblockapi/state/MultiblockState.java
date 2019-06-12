package com.github.yona168.multiblockapi.state;

import com.github.yona168.multiblockapi.structure.Multiblock;
import com.github.yona168.multiblockapi.util.ThreeDimensionalArrayCoords;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface MultiblockState {
  Block getBlockByPattern(int level, int row, int column);

  Location getTriggerBlockLoc();

  Set<Location> getStructureBlocksLocs();

  Multiblock<MultiblockState> getMultiblock();

  Set<Location> getAllBlocksLocs();

  Set<Chunk> getOccupiedChunks();

  Orientation getOrientation();

  UUID getUniqueid();

  default World getWorld(){
    return getTriggerBlockLoc().getWorld();
  }

  enum Orientation {
    NORTH {
      @Override
      Block getBlock(int level, int row, int column, Block bottomLeftCorner) {
        return bottomLeftCorner.getRelative(column, level, row);
      }
    },
    SOUTH {
      @Override
      Block getBlock(int level, int row, int column, Block bottomLeftCorner) {
        return bottomLeftCorner.getRelative(-column, level, -row);
      }
    },
    EAST {
      @Override
      Block getBlock(int level, int row, int column, Block bottomLeftCorner) {
        return bottomLeftCorner.getRelative(-row, level, column);
      }
    },
    WEST {
      @Override
      Block getBlock(int level, int row, int column, Block bottomLeftCorner) {
        return bottomLeftCorner.getRelative(row, level, -column);
      }
    };
    abstract Block getBlock(int level, int row, int column, Block bottomLeftCorner);
  }
}