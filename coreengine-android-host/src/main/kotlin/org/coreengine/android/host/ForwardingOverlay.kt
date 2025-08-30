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

package org.coreengine.android.host

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/** Reenvía eventos al Surface si los hijos del overlay no los consumen. */
class ForwardingOverlay(ctx: Context) : FrameLayout(ctx) {
    var forwardTarget: View? = null
    override fun onInterceptTouchEvent(ev: MotionEvent) = false
    override fun onTouchEvent(ev: MotionEvent) = false
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val handledByChildren = super.dispatchTouchEvent(ev)
        if (handledByChildren) return true
        return forwardTarget?.dispatchTouchEvent(ev) ?: false
    }
}