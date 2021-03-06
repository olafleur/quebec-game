/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.server.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Work;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.GameListResult;
import com.philbeaudoin.quebec.shared.game.GameInfo;
import com.philbeaudoin.quebec.shared.game.GameInfoDto;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.AiBrainSimple;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerLocalAi;
import com.philbeaudoin.quebec.shared.player.PlayerLocalUser;
import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * Implementation of {@link GameManager}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@RequestScoped
public class GameManagerImpl implements GameManager, ObjectifyServiceWrapper {

  private final ObjectifyServiceWrapper objectifyServiceWrapper;
  private final ServerSessionManager serverSessionManager;
  private final GameControllerServer gameControllerServer;


  @Inject
  public GameManagerImpl(ObjectifyServiceWrapper objectifyServiceWrapper,
      ServerSessionManager serverSessionManager,
      GameControllerServer gameControllerServer) {
    this.objectifyServiceWrapper = objectifyServiceWrapper;
    this.serverSessionManager = serverSessionManager;
    this.gameControllerServer = gameControllerServer;
  }

  @Override
  public Objectify ofy() {
    return objectifyServiceWrapper.ofy();
  }

  @Override
  public List<GameInfoEntity> listOpenGames() {
    // TODO(beaudoin): Fix, now listing all games, including closed ones.
    return ofy().load().type(GameInfoEntity.class).limit(50).order("creationDate").list();
  }

  @Override
  public GameInfoEntity createNewGame(int nbPlayers) throws ActionException {
    SessionInfoEntity sessionInfoEntity = serverSessionManager.getSessionInfo();
    if (sessionInfoEntity == null || !sessionInfoEntity.isSignedIn()) {
      throw new ActionException("Must be signed in to create a game.");
    }
    if (nbPlayers < 3 || nbPlayers > 5) {
      throw new ActionException("New games must be created with 3, 4 or 5 players.");
    }
    GameInfoEntity gameInfoEntity = new GameInfoEntity(nbPlayers, (new Date()).getTime());
    gameInfoEntity.addPlayer(sessionInfoEntity.getUserInfoEntity());
    ofy().save().entity(gameInfoEntity).now();
    return gameInfoEntity;
  }

  @Override
  public GameInfoEntity joinGame(final long gameId) throws ActionException {
    SessionInfoEntity sessionInfoEntity = serverSessionManager.getSessionInfo();
    if (sessionInfoEntity == null || !sessionInfoEntity.isSignedIn()) {
      throw new ActionException("Must be signed in to create a game.");
    }
    final UserInfoEntity currentUser = sessionInfoEntity.getUserInfoEntity();
    try {
      GameInfoEntity result = ofy().transact(new Work<GameInfoEntity>() {
        @Override
        public GameInfoEntity run() {
          GameInfoEntity game = ofy().load().type(GameInfoEntity.class).id(gameId).get();
          if (game == null) {
            throw new RuntimeException("Cannot join game, game Id not found.");
          }
          // Find the first available seat. Ensure there is room for that player.
          if (game.getNbEmptySeats() <= 0) {
            throw new RuntimeException("Cannot join game, no empty seat.");
          }
          for (int i = 0; i < game.getNbPlayers(); ++i) {
            UserInfo player = game.getPlayerInfo(i);
            if (player != null && player.getId() == currentUser.getId()) {
              throw new RuntimeException("Cannot join game, game Id not found.");
            }
          }
          game.addPlayer(currentUser);
          ofy().save().entity(game).now();
          return game;
        }
      });
      return result;
    } catch (RuntimeException e) {
      throw new ActionException(e.getMessage());
    }
  }

  @Override
  public void ensureListContainsGame(List<GameInfoEntity> games, GameInfoEntity game) {
    for (int i = 0; i < games.size(); ++i) {
      if (games.get(i).getId() == game.getId()) {
        games.set(i, game);
        return;
      }
    }
    games.add(game);
  }

  @Override
  public GameInfo anonymizeGameInfo(final GameInfo gameInfo) {
    return new GameInfo() {
      @Override public long getId() { return gameInfo.getId(); }
      @Override public int getNbPlayers() { return gameInfo.getNbPlayers(); }
      @Override public UserInfo getPlayerInfo(int index) {
        UserInfo result = gameInfo.getPlayerInfo(index);
        return result == null ? null : serverSessionManager.anonymizeUserInfo(result);
      }
      @Override public Date getCreationDate() { return gameInfo.getCreationDate(); }
      @Override public int getCurrentPlayerIndex() { return gameInfo.getCurrentPlayerIndex(); }
    };
  }

  @Override
  public GameListResult GameInfoEntitiesToGameListResult(List<GameInfoEntity> gameInfoEntities) {
    List<GameInfoDto> games = new ArrayList<GameInfoDto>(gameInfoEntities.size());
    for (GameInfoEntity gameInfoEntity : gameInfoEntities) {
      games.add(new GameInfoDto(anonymizeGameInfo(gameInfoEntity)));
    }
    return new GameListResult(games);
  }

  @Override
  public GameState loadGame(final long gameId) {
    return ofy().transact(new Work<GameState>() {
      @Override
      public GameState run() {
        GameInfoEntity gameInfoEntity = ofy().load().type(GameInfoEntity.class).id(gameId).get();
        if (gameInfoEntity == null) {
          throw new RuntimeException("Cannot load game, game Id not found.");
        }
        // Find the first available seat. Ensure there is room for that player.
        if (gameInfoEntity.getNbEmptySeats() != 0) {
          throw new RuntimeException("Cannot load game, game is not complete.");
        }
        Ref<GameEntity> gameRef = gameInfoEntity.getGameRef();
        GameEntity gameEntity = null;
        if (gameRef != null) {
          ofy().load().ref(gameRef);
          gameEntity = gameRef.get();
        }
        if (gameEntity == null) {
          // Create the game.

          // TODO(beaudoin): Just creating a dummy 4 player game here.
          GameState gameState = new GameState();
          ArrayList<Player> players = new ArrayList<Player>(4);
          players.add(new PlayerLocalUser(PlayerColor.BLACK, "You"));
          players.add(new PlayerLocalAi(PlayerColor.PINK, "Johnny 5 Server", new AiBrainSimple()));
          players.add(new PlayerLocalAi(PlayerColor.WHITE, "HAL Server", new AiBrainSimple()));
          players.add(new PlayerLocalAi(PlayerColor.ORANGE, "Skynet Server", new AiBrainSimple()));
          // Client-only game. We can start it right away.
          gameControllerServer.initGame(gameState, players);

          gameEntity = new GameEntity(Key.create(GameInfoEntity.class, gameId), gameState);
          ofy().save().entity(gameEntity).now();
          gameInfoEntity.setGame(gameEntity);
          ofy().save().entity(gameInfoEntity).now();
        }
        return gameEntity.getGameState();
      }
    });
  }

}
