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

package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of emptying the cubes on a given tile to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionEmptyTileToZone implements GameActionOnInfluenceZone {

  private Tile origin;
  private InfluenceType to;
  private GameStateChange followup;

  public ActionEmptyTileToZone(Tile origin,  InfluenceType to, GameStateChange followup) {
    assert followup != null;
    this.origin = origin;
    this.to = to;
    this.followup = followup;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionEmptyTileToZone() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();

    GameStateChangeComposite result = new GameStateChangeComposite();

    TileState originState = gameState.findTileState(origin);

    int cubesPerSpot = originState.getCubesPerSpot();
    CubeDestinationInfluenceZone destination = new CubeDestinationInfluenceZone(to, activePlayer);
    for (int spot = 0; spot < 3; ++spot) {
      if (originState.getColorInSpot(spot) == activePlayer) {
        result.add(new GameStateChangeMoveCubes(cubesPerSpot,
            new CubeDestinationTile(origin, activePlayer, spot), destination));
      }
    }
    assert result.getNbGameStateChanges() > 0;

    // Execute follow-up move.
    result.add(followup);

    return result;
  }

  @Override
  public boolean isAutomatic() {
    return false;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public InfluenceType getInfluenceZone() {
    return to;
  }

  /**
   * Returns the tile that is being emptied.
   * @return The tile.
   */
  public Tile getOrigin() {
    return origin;
  }
}
