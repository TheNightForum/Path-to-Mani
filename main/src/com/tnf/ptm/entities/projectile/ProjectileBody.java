/*
 * Copyright 2017 TheNightForum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnf.ptm.entities.projectile;

import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.game.PtmGame;
import com.tnf.ptm.entities.ship.PtmShip;

public interface ProjectileBody {
    void update(PtmGame game);

    Vector2 getPos();

    Vector2 getSpd();

    void receiveForce(Vector2 force, PtmGame game, boolean acc);

    void onRemove(PtmGame game);

    float getAngle();

    void changeAngle(float diff);

    float getDesiredAngle(PtmShip ne);
}
