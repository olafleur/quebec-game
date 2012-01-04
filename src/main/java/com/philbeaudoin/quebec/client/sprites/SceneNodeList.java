/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.sprites;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.shared.utils.Transformation;

/**
 * This class tracks a list of scene nodes. This class should have only logic, no GWT-specific code,
 * so it's easily testable.
 *
 * @author Philippe Beaudoin
 */
public class SceneNodeList extends SceneNodeImpl {
  private final ArrayList<SceneNode> sceneNodes = new ArrayList<SceneNode>();

  public SceneNodeList() {
    super();
  }

  public SceneNodeList(Transformation transformation) {
    super(transformation);
  }

  /**
   * Adds a scene node to the list.
   * @param sceneNode The scene node to add.
   */
  public void add(SceneNode sceneNode) {
    sceneNode.setParent(this);
  }

  /**
   * Package-private method used by scene nodes to add themselves to the list.
   * @param sceneNodes The scene node to add.
   */
  void addToList(SceneNode sceneNode) {
    sceneNodes.add(sceneNode);
  }

  /**
   * Package-private method used by scene nodes to remove themselves from the list.
   * @param sceneNodes The scene node to remove.
   */
  void removeFromList(SceneNode sceneNode) {
    sceneNodes.remove(sceneNode);
  }

  /**
   * Ensures that the specified scene node is rendered last so it appears on top of all other
   * nodes. Will only work if the provided {@code sceneNode} is in the list, otherwise it has no
   * effect.
   * @param sceneNode The scene node to render in front of all others.
   */
  public void sendToFront(SceneNode sceneNode) {
    if (sceneNodes.remove(sceneNode)) {
      sceneNodes.add(sceneNode);
    }
  }

  /**
   * Renders all the scene nodes contained in the list.
   * @param context The canvas context into which to render.
   */
  @Override
  public void render(Context2d context) {
    context.save();
    try {
      getTransformation().applies(context);
      for (SceneNode sceneNode : sceneNodes) {
        sceneNode.render(context);
      }
    } finally {
      context.restore();
    }
  }
}
