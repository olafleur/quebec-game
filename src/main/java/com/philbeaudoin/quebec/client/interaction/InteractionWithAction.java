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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.philbeaudoin.quebec.client.renderer.ChangeRenderer;
import com.philbeaudoin.quebec.client.renderer.ChangeRendererGenerator;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is the basic implementation of an interaction for which a click results in executing an
 * action.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionWithAction implements Interaction {

  protected final Scheduler scheduler;
  protected final RendererFactories rendererFactories;
  protected final GameState gameState;
  protected final GameStateRenderer gameStateRenderer;
  private final ComplexText actionText;
  protected final GameStateChange gameStateChange;
  private final InteractionTarget target;

  private CallbackRegistration animationCompletedRegistration;
  private boolean inside;

  protected InteractionWithAction(Scheduler scheduler, RendererFactories rendererFactories,
      GameState gameState, GameStateRenderer gameStateRenderer, InteractionTarget target,
      GameStateChange gameStateChange) {
    this(scheduler, rendererFactories, gameState, gameStateRenderer, target, null, gameStateChange);
  }

  protected InteractionWithAction(Scheduler scheduler, RendererFactories rendererFactories,
      GameState gameState, GameStateRenderer gameStateRenderer, InteractionTarget target,
      MessageRenderer messageRenderer, GameStateChange gameStateChange) {
    this.scheduler = scheduler;
    this.rendererFactories = rendererFactories;
    this.gameState = gameState;
    this.gameStateRenderer = gameStateRenderer;
    this.gameStateChange = gameStateChange;
    this.target = target;

    if (messageRenderer != null && messageRenderer.getComponents().size() > 0 &&
        !gameState.hasPossibleActionMessage()) {
      this.actionText = new ComplexText(messageRenderer.getComponents(),
          new ConstantTransform(new Vector2d(1.05, 0.1)));
    } else {
      this.actionText = null;
    }
  }

  @Override
  public void onMouseClick(double x, double y, double time) {
    if (getTrigger().triggerAt(x, y)) {
      gameStateRenderer.clearAnimationGraph();
      if (animationCompletedRegistration != null) {
        animationCompletedRegistration.unregister();
        animationCompletedRegistration = null;
      }

      scheduler.scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          gameStateRenderer.clearInteractions();
          gameStateRenderer.removeAllHighlights();
        }
      });

      ChangeRendererGenerator generator = rendererFactories.createChangeRendererGenerator();
      gameStateChange.accept(generator);

      ChangeRenderer changeRenderer = generator.getChangeRenderer();
      changeRenderer.generateAnim(gameStateRenderer, 0.0);
      changeRenderer.undoAdditions(gameStateRenderer);

      if (!gameStateRenderer.isAnimationCompleted(0.0)) {
        animationCompletedRegistration = gameStateRenderer.addAnimationCompletedCallback(
            new Callback() {
              @Override public void execute() {
                renderForNextMove(gameStateChange);
                animationCompletedRegistration.unregister();
              }
            });
      } else {
        scheduler.scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            renderForNextMove(gameStateChange);
          }
        });
      }
    }
  }

  private void renderForNextMove(final GameStateChange change) {
    gameStateRenderer.clearAnimationGraph();
    change.apply(gameState);
    gameStateRenderer.render(gameState);
  }

  @Override
  public void onMouseMove(double x, double y, double time) {
    if (target.getTrigger().triggerAt(x, y)) {
      doMouseMove(x, y, time);
      if (!inside) {
        doMouseEnter(x, y, time);
        if (actionText != null) {
          gameStateRenderer.addToAnimationGraph(actionText);
        }
        inside = true;
      }
    } else if (inside) {
      inside = false;
      doMouseLeave(x, y, time);
      if (actionText != null) {
        actionText.setParent(null);
      }
    }
  }

  @Override
  public void highlight() {
    target.highlight();
  }

  /**
   * Access the trigger of this interaction.
   * @return The trigger of the interaction.
   */
  protected Trigger getTrigger() {
    return target.getTrigger();
  }

  /**
   * Performs any graphical additions that should be performed when the mouse is moving inside the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseMove(double x, double y, double time) {
  }

  /**
   * Performs any graphical additions that should be performed when the mouse enters the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseEnter(double x, double y, double time) {
    target.onMouseEnter(time);
  }

  /**
   * Performs any graphical additions that should be performed when the mouse leaves the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseLeave(double x, double y, double time) {
    target.onMouseLeave(time);
  }
}