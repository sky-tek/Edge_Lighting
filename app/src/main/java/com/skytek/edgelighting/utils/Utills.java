package com.skytek.edgelighting.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;

import com.skytek.edgelighting.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utills {

    public static void getHeightScreen(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int i = point.y;
        int i2 = point.x;
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.HEIGHT, i, activity);
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.WIDTH, i2, activity);
    }

    public static int compileShaderResourceGLES30(@NonNull Context context, final int shaderType, final int shaderRes) throws RuntimeException {
        final InputStream inputStream = context.getResources().openRawResource(shaderRes);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        final String shaderSource = stringBuilder.toString();
        int shader = GLES30.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Failed to create shader");
        }
        GLES30.glShaderSource(shader, shaderSource);
        GLES30.glCompileShader(shader);
        final int[] status = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES30.glGetShaderInfoLog(shader);
            GLES30.glDeleteShader(shader);
            throw new RuntimeException(log);
        }
        return shader;
    }

    public static int linkProgramGLES30(final int vertShader, final int fragShader) throws RuntimeException {
        int program = GLES30.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Failed to create program");
        }
        GLES30.glAttachShader(program, vertShader);
        GLES30.glAttachShader(program, fragShader);
        GLES30.glLinkProgram(program);
        final int[] status = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES30.glGetProgramInfoLog(program);
            GLES30.glDeleteProgram(program);
            throw new RuntimeException(log);
        }
        return program;
    }

    public static int compileShaderResourceGLES20(@NonNull Context context, final int shaderType, final int shaderRes) throws RuntimeException {
        final InputStream inputStream = context.getResources().openRawResource(shaderRes);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        final String shaderSource = stringBuilder.toString();
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Failed to create shader");
        }
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        final int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException(log);
        }
        return shader;
    }

    public static int linkProgramGLES20(final int vertShader, final int fragShader) throws RuntimeException {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Failed to create program");
        }
        GLES20.glAttachShader(program, vertShader);
        GLES20.glAttachShader(program, fragShader);
        GLES20.glLinkProgram(program);
        final int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES20.glGetProgramInfoLog(program);
            GLES20.glDeleteProgram(program);
            throw new RuntimeException(log);
        }
        return program;
    }

    public static void debug(@NonNull final String tag, @NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

}
