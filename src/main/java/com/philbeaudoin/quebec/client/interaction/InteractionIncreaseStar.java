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

package com.philbeaudoin.quebec.client.interaction;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.shared.action.ActionIncreaseStar;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * This is an interaction with the game board for the action of increasing the number of stars on
 * a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionIncreaseStar extends InteractionWithAction {

  @Inject
  public InteractionIncreaseStar(Scheduler scheduler, RendererFactories rendererFactories,
      InteractionFactories interactionFactories, MessageRenderer messageRenderer,
      @Assisted GameState gameState, @Assisted GameStateRenderer gameStateRenderer,
      @Assisted ActionIncreaseStar action) {
    super(scheduler, rendererFactories, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetTile(gameStateRenderer, action),
        action.execute(gameState));
  }

}