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

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeIncreaseStarToken;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * The action of sending passive worker cubes to a given influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionIncreaseStar implements GameActionOnTile {

  private Tile tileToIncrease;

  public ActionIncreaseStar(Tile tileToIncrease) {
    this.tileToIncrease = tileToIncrease;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionIncreaseStar() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();
    TileState tileState = gameState.findTileState(tileToIncrease);
    int nbStars = tileState.getNbStars();
    assert tileState.getStarTokenColor() == activePlayer;
    assert nbStars == 1 || nbStars == 2;

    GameStateChangeComposite result = new GameStateChangeComposite();
    result.add(new GameStateChangeIncreaseStarToken(tileToIncrease, activePlayer, nbStars + 1));

    // Execute follow-up move.
    result.add(new GameStateChangeNextPlayer());

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
  public Tile getDestinationTile() {
    return tileToIncrease;
  }
}
