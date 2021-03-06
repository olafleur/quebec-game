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

package com.philbeaudoin.quebec.shared.player;

import java.io.Serializable;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;

/**
 * State of a player during the game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class PlayerState implements Serializable {

  private Player player;
  private int nbPassiveCubes;
  private int nbActiveCubes;
  private boolean currentPlayer;
  private boolean holdingArchitect;
  private boolean holdingNeutralArchitect;
  private LeaderCard leaderCard;
  private int score;

  /**
   * Initializes a player state.
   * @param player Static information about the player.
   */
  public PlayerState(Player player) {
    this.player = player;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private PlayerState() {
  }

  /**
   * Copy constructor performing a deep copy of every mutable object contained in this one.
   * @param other The player state to copy.
   */
  public PlayerState(PlayerState other) {
    this.player = other.player;
    this.nbPassiveCubes = other.nbPassiveCubes;
    this.nbActiveCubes = other.nbActiveCubes;
    this.currentPlayer = other.currentPlayer;
    this.holdingArchitect = other.holdingArchitect;
    this.holdingNeutralArchitect = other.holdingNeutralArchitect;
    this.leaderCard = other.leaderCard;
    this.score = other.score;
  }

  /**
   * @return Static information on the player.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * @return The player color.
   */
  public PlayerColor getColor() {
    return player.getColor();
  }

  /**
   * @param nbPassiveCubes The number of passive cubes held by the player.
   */
  public void setNbPassiveCubes(int nbPassiveCubes) {
    this.nbPassiveCubes = nbPassiveCubes;
  }

  /**
   * @return The number of passive cubes held by the player.
   */
  public int getNbPassiveCubes() {
    return nbPassiveCubes;
  }

  /**
   * @param nbActiveCubes The number of active cubes held by the player.
   */
  public void setNbActiveCubes(int nbActiveCubes) {
    this.nbActiveCubes = nbActiveCubes;
  }

  /**
   * @return The number of active cubes held by the player.
   */
  public int getNbActiveCubes() {
    return nbActiveCubes;
  }

  /**
   * @return The total number of active cubes held by the player, passive and active
   */
  public int getNbTotalCubes() {
    return nbPassiveCubes + nbActiveCubes;
  }

  /**
   * @param currentPlayer True if this is the currently active player.
   */
  public void setCurrentPlayer(boolean currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  /**
   * @return True if this is the currently active player.
   */
  public boolean isCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * @param holdingArchitect True if the player is holding his architect.
   */
  public void setHoldingArchitect(boolean holdingArchitect) {
    this.holdingArchitect = holdingArchitect;
  }

  /**
   * @return True if the player is holding his architect.
   */
  public boolean isHoldingArchitect() {
    return holdingArchitect;
  }

  /**
   * @param holdingNeutralArchitect True if the player is holding the neutral architect.
   */
  public void setHoldingNeutralArchitect(boolean holdingNeutralArchitect) {
    this.holdingNeutralArchitect = holdingNeutralArchitect;
  }

  /**
   * @return True if the player is holding the neutral architect.
   */
  public boolean isHoldingNeutralArchitect() {
    return holdingNeutralArchitect;
  }

  /**
   * @param leaderCard The leader card the player is holding, or {@code null} if none.
   */
  public void setLeaderCard(LeaderCard leaderCard) {
    this.leaderCard = leaderCard;
  }

  /**
   * @return The leader card the player is holding, or {@code null} if none.
   */
  public LeaderCard getLeaderCard() {
    return leaderCard;
  }

  /**
   * @param score The player's score.
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * @return The player's score.
   */
  public int getScore() {
    return score;
  }

  /**
   * Checks whether the current player owns the specified architect.
   * @param architectColor The architect color to check for, can be NEUTRAL.
   * @return True if the player owns the specified architect.
   */
  public boolean ownsArchitect(PlayerColor architectColor) {
    return architectColor == getPlayer().getColor() ||
        architectColor == PlayerColor.NEUTRAL && getLeaderCard() == LeaderCard.ECONOMIC;
  }
}
