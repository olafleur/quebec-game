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

import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * A local user player.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class PlayerLocalUser extends PlayerBase {
  public PlayerLocalUser(PlayerColor color, String name) {
    super(color, name);
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private PlayerLocalUser() {
  }

  @Override
  public <T> T accept(PlayerVisitor<T> visitor) {
    return visitor.visit(this);
  }

}
