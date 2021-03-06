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

package com.philbeaudoin.quebec.client.renderer;

import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A valid place to take or send leader cards within a scene graph. The destination implies a leader
 * card. The render-side equivalent of
 * {@link com.philbeaudoin.quebec.shared.location.LeaderDestination LeaderDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface SceneLeaderDestination {

  /**
   * @return The leader card associated with that destination.
   */
  LeaderCard getLeaderCard();

  /**
   * Remove a leader card from that specific destination in the provided scene graph.
   * @param renderer The renderer containing the scene graph from which to remove a leader card.
   * @return The global transform of the leader card removed from the scene graph.
   */
  Transform removeFrom(GameStateRenderer renderer);

  /**
   * Add cubes to this specific destination in the provided scene graph.
   * @param renderer The renderer containing the scene graph onto which to add a leader card.
   * @return The global transform of the leader card added to the scene graph.
   */
  Transform addTo(GameStateRenderer renderer);
}
