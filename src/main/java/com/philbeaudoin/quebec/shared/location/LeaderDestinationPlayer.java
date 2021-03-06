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
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * A leader card destination corresponding to a player zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class LeaderDestinationPlayer implements LeaderDestination {

  private LeaderCard leaderCard;
  private PlayerColor playerColor;

  public LeaderDestinationPlayer(LeaderCard leaderCard, PlayerColor playerColor) {
    this.leaderCard = leaderCard;
    this.playerColor = playerColor;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private LeaderDestinationPlayer() {
  }

  @Override
  public LeaderCard getLeaderCard() {
    return leaderCard;
  }

  @Override
  public void removeFrom(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    assert leaderCard != null;
    assert playerState.getLeaderCard() == leaderCard;
    playerState.setLeaderCard(null);
  }

  @Override
  public void addTo(GameState gameState) {
    PlayerState playerState = gameState.getPlayerState(playerColor);
    assert playerState != null;
    assert leaderCard != null;
    assert playerState.getLeaderCard() == null;
    playerState.setLeaderCard(leaderCard);
  }

  @Override
  public <T> T accept(LeaderDestinationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public <T> T accept(LocationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Access the color of the player zone corresponding to that destination.
   * @return The color of the player zone.
   */
  public PlayerColor getPlayerColor() {
    return playerColor;
  }
}
