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

import com.philbeaudoin.quebec.shared.game.state.Tile;

/**
 * An object that has a destination tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface HasDestinationTile {
  /**
   * Access the destination tile of the object.
   * @return The destination tile.
   */
  Tile getDestinationTile();
}
