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

package com.philbeaudoin.quebec.shared.state;

import java.util.List;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.action.ActionSendOneWorker;
import com.philbeaudoin.quebec.shared.action.ActionSendWorkers;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.action.PossibleActionsComposite;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A controller to manipulate the state of a game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameController {

  // Number of cubes per player for a 2, 3, 4, 5 player game.
  private static final int CUBES_FOR_N_PLAYERS[] = { 25, 25, 22, 20 };

  /**
   * Clears the game state and resets the entire game to the initial state.
   * @param gameState The state to reset and initialize.
   */
  public void initGame(GameState gameState, List<Player> players) {
    gameState.setCentury(0);

    int nbPlayers = players.size();
    assert nbPlayers >= 2 && nbPlayers <= 5;
    int cubesPerPlayer = CUBES_FOR_N_PLAYERS[nbPlayers - 2];
    List<PlayerState> playerStates = gameState.getPlayerStates();
    playerStates.clear();
    for (Player player : players) {
      PlayerState playerState = new PlayerState(player);
      playerState.setHoldingArchitect(true);
      playerState.setNbPassiveCubes(cubesPerPlayer - 3);
      playerState.setNbActiveCubes(3);
      playerStates.add(playerState);
    }
    playerStates.get(0).setCurrentPlayer(true);

    List<TileState> tileStates = gameState.getTileStates();
    tileStates.clear();
    TileDeck tileDeck = new TileDeck();
    for (int column = 0; column < 18; ++column) {
      for (int line = 0; line < 8; ++line) {
        BoardAction boardAction = Board.actionForTileLocation(column, line);
        if (boardAction != null) {
          Tile tile = tileDeck.draw(boardAction.getInfluenceType());
          TileState tileState = new TileState(tile, new Vector2d(column, line));
          tileState.setArchitect(PlayerColor.NONE);
          tileStates.add(tileState);
        }
      }
    }

    // TODO: The specific cards available depend on the number of players.
    List<LeaderCard> availableLeaderCards = gameState.getAvailableLeaderCards();
    availableLeaderCards.clear();
    for (InfluenceType influenceType : InfluenceType.values()) {
      availableLeaderCards.add(new LeaderCard(influenceType));
    }

    for (InfluenceType influenceType : InfluenceType.values()) {
      for (PlayerColor playerColor : PlayerColor.values()) {
        if (playerColor.isNormalColor()) {
          gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, 0);
        }
      }
    }

    configuePossibleActions(gameState);
  }

  /**
   * Configure the possible actions given the current game state.
   * @param gameState The state to reset and initialize.
   */
  public void configuePossibleActions(GameState gameState) {
    int century = gameState.getCentury();
    PossibleActionsComposite possibleActions = new PossibleActionsComposite();
    gameState.setPossibleActions(possibleActions);

    PlayerState currentPlayer = gameState.getCurrentPlayer();
    int nbActiveCubes = currentPlayer.getNbActiveCubes();

    // Mark moving architect or sending workers as a possible action.
    for (TileState tileState : gameState.getTileStates()) {
      if (tileState.getTile().getCentury() == century &&
          tileState.getArchitect() == PlayerColor.NONE &&
          !tileState.isBuildingFacing()) {
        possibleActions.add(new ActionMoveArchitect(tileState.getTile(), false));
      } else if (tileState.getArchitect() != PlayerColor.NONE &&
          nbActiveCubes >= tileState.getCubesPerSpot() &&
          tileState.getColorInSpot(2) == PlayerColor.NONE) {
        possibleActions.add(new ActionSendWorkers(tileState.getTile()));
      }
    }

    // Mark moving one cube to influence zones as a possible action.
    if (nbActiveCubes >= 1) {
      for (InfluenceType influenceZone : InfluenceType.values()) {
        possibleActions.add(new ActionSendOneWorker(influenceZone));
      }
    }

    // Mark getting a leader card as a possible action.
    if (currentPlayer.getLeaderCard() == null) {
      for (LeaderCard leaderCard : gameState.getAvailableLeaderCards()) {
        possibleActions.add(new ActionTakeLeaderCard(leaderCard));
      }
    }
  }
}