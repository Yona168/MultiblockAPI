package com.github.yona168.multiblockapi.util;

import com.github.yona168.multiblockapi.pattern.Pattern;
import org.bukkit.Material;

public class ThreeDimensionalArrayCoords {
  private final int x;
  private final int y;
  private final int z;

  public ThreeDimensionalArrayCoords(int level, int row, int column) {
    this.x = row;
    this.y = level;
    this.z = column;
  }

  public int getRow() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getColumn() {
    return z;
  }

  public static <T> T get(T[][][] arr, ThreeDimensionalArrayCoords coords) {
    return arr[coords.y][coords.x][coords.z];
  }

  public static Material get(Pattern pattern, ThreeDimensionalArrayCoords coords) {
    return get(pattern.asArray(), coords);
  }
}
