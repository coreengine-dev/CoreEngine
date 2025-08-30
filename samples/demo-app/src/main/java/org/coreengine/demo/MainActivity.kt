
/*
 * Copyright 2025 Juan José Nicolini
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

package org.coreengine_runtime.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import org.coreengine.engine.CoreEngine
//import org.coreengine.scene.SceneManager
//import org.coreengine.scene.DemoScene

class MainActivity : AppCompatActivity() {

//    private lateinit var engine: CoreEngine
//    private lateinit var sceneManager: SceneManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        engine = CoreEngine(targetFps = 60)
//        sceneManager = SceneManager()
//        sceneManager.setScene(DemoScene())

        Thread {
//            engine.start(
//                update = { sceneManager.update() },
//                render = { sceneManager.render() }
//            )
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
//        engine.stop()
    }
}




