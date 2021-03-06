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

package com.philbeaudoin.quebec.shared.location;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;

/**
 * A cube destination corresponding to a spot on a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class CubeDestinationTile implements CubeDestination {

  private Tile tile;
  private PlayerColor playerColor;
  private int spot;

  public CubeDestinationTile(Tile tile, PlayerColor playerColor, int spot) {
    this.tile = tile;
    this.playerColor = playerColor;
    this.spot = spot;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private CubeDestinationTile() {
  }

  @Override
  public int getNbCubes(GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    assert tileState != null;
    PlayerColor colorInSpot = tileState.getColorInSpot(spot);
    if (colorInSpot == PlayerColor.NONE) {
      return 0;
    }
    assert colorInSpot == playerColor;
    return tileState.getCubesPerSpot();
  }

  @Override
  public PlayerColor getPlayerColor() {
    return playerColor;
  }

  @Override
  public void removeFrom(int nbCubes, GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    assert tileState != null;
    assert tileState.getColorInSpot(spot) == playerColor;
    assert tileState.getCubesPerSpot() == nbCubes;
    tileState.setColorInSpot(spot, PlayerColor.NONE);
  }

  @Override
  public void addTo(int nbCubes, GameState gameState) {
    TileState tileState = gameState.findTileState(tile);
    assert tileState != null;
    assert tileState.getColorInSpot(spot) == PlayerColor.NONE;
    assert tileState.getCubesPerSpot() == nbCubes;
    tileState.setColorInSpot(spot, playerColor);
  }

  @Override
  public <T> T accept(CubeDestinationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(LocationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Returns the tile onto which cubes are added or removed by this destination.
   * @return The tile.
   */
  public Tile getTile() {
    return tile;
  }

  /**
   * Returns the index of the spot, on the tile, from which the cubes are added or removed by this
   * destination.
   * @return The index of the spot.
   */
  public int getSpot() {
    return spot;
  }
}
