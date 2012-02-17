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

package com.philbeaudoin.quebec.shared.action;

/**
 * Interface for a class that can visit a {@link GameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameActionVisitor {
  /**
   * Visits a {@link ActionSendWorkers}.
   * @param host The visited class.
   */
  void visit(ActionSendWorkers host);

  /**
   * Visits a {@link ActionTakeLeaderCard}.
   * @param host The visited class.
   */
  void visit(ActionTakeLeaderCard host);

  /**
   * Visits a {@link ActionSendCubesToZone}.
   * @param host The visited class.
   */
  void visit(ActionSendCubesToZone host);

  /**
   * Visits a {@link ActionSelectBoadAction}.
   * @param host The visited class.
   */
  void visit(ActionSelectBoadAction host);

  /**
   * Visits a {@link ActionScorePoints}.
   * @param host The visited class.
   */
  void visit(ActionScorePoints host);

  /**
   * Visits a {@link ActionMoveArchitect}.
   * @param host The visited class.
   */
  void visit(ActionMoveArchitect host);

  /**
   * Visits a {@link ActionSkip}.
   * @param host The visited class.
   */
  void visit(ActionSkip host);
}
