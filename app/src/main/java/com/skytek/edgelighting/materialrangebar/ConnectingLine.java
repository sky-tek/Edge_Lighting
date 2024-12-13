/*
 * Copyright 2013, Edmodo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.skytek.edgelighting.materialrangebar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.skytek.edgelighting.materialrangebar.PinView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the blue connecting line between the two thumbs.
 */
public class ConnectingLine {

    // Member Variables ////////////////////////////////////////////////////////

    private final int[] colors;
    private final float[] positions;
    private final Paint paint = new Paint();
    private final Paint paint1 = new Paint();
    private final Paint paint2 = new Paint();
    private final float mY;

    // Constructor /////////////////////////////////////////////////////////////

    /**
     * Constructor for connecting line
     *
     * @param y                    the y co-ordinate for the line
     * @param connectingLineWeight the weight of the line
     * @param connectingLineColors the color of the line
     */
    public ConnectingLine(float y, float connectingLineWeight,
                          ArrayList<Integer> connectingLineColors) {

        //Need two colors
        if (connectingLineColors.size() == 1) {
            connectingLineColors.add(connectingLineColors.get(0));
        }

        colors = new int[connectingLineColors.size()];
        positions = new float[connectingLineColors.size()];
        for (int index = 0; index < connectingLineColors.size(); index++) {
            colors[index] = connectingLineColors.get(index);

            positions[index] = (float) index / (connectingLineColors.size() - 1);
        }

        paint.setStrokeWidth(connectingLineWeight);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#FF9800"));
        paint1.setStrokeWidth(connectingLineWeight);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        paint1.setAntiAlias(true);
        paint1.setColor(Color.WHITE);
        paint2.setStrokeWidth(connectingLineWeight);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.RED);
        mY = y;
    }

    private LinearGradient getLinearGradient(float startX, float endX, float height) {

        return new LinearGradient(startX, height, endX, height,
                colors,
                positions,
                Shader.TileMode.REPEAT);
    }


    /**
     * Draw the connecting line between the two thumbs in rangebar.
     *
     * @param canvas     the Canvas to draw to
     * @param leftThumb  the left thumb
     * @param rightThumb the right thumb
     */
    public void draw(Canvas canvas, PinView leftThumb,PinView rightThumb) {


        canvas.drawLine(leftThumb.getX(), mY, rightThumb.getX(), mY, paint);

    }
    public void drawright(Canvas canvas, PinView rightThumb, float total) {



        canvas.drawLine(rightThumb.getX(), mY,total+22 , mY, paint1);

    }
    public void drawleft(Canvas canvas, PinView leftThumb, float total) {


        canvas.drawLine(22, mY, leftThumb.getX(), mY, paint2);

    }

    /**
     * Draw the connecting line between for single slider.
     *
     * @param canvas     the Canvas to draw to
     * @param rightThumb the right thumb
     * @param leftMargin the left margin
     */
    public void draw(Canvas canvas, float leftMargin, PinView rightThumb) {

        canvas.drawLine(leftMargin, mY, rightThumb.getX(), mY, paint);
    }
}
