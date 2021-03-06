/**
 * Copyright 2012 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.quebec.shared;

/**
 * The various possible types of influence in the game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum InfluenceType {
  RELIGIOUS,
  POLITIC,
  ECONOMIC,
  CULTURAL,
  CITADEL;

  /**
   * Get the i-th scoring zone for the given century.
   * @param century The century.
   * @param index The index of the scoring zone (0 to 4).
   * @return The scoring zone.
   */
  public static InfluenceType getScoringZoneForCentury(int century, int index) {
    if (index == 0) {
      return CITADEL;
    }
    return values()[(century + index - 1) % 4];
  }

  /**
   * Retrieves the number of tiles of a given influence type for a given century.
   * @param influenceType The influence type (color).
   * @param century The century (0, 1, 2 or 3).
   * @return The number of tiles.
   */
  public static int getNbTilesForCentury(InfluenceType influenceType, int century) {
    return tilesPerCentury[influenceType.ordinal()][century];
  }

  private static int tilesPerCentury[][] = {
    {4, 2, 3, 2},
    {2, 4, 2, 3},
    {3, 2, 4, 2},
    {2, 3, 2, 4}
  };
}
