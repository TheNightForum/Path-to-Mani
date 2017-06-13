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
package com.tnf.ptm.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by brayden on 13/6/17.
 */
public class Logger {

    public static final String NORMAL =  "       ";
    public static final String WARNING = "WARNING";
    public static final String ERROR =   "!ERROR!";
    public static final String MODULE =  "MODULE ";
    private static final String DEBUG =  "DEBUG";
    private static boolean debugEnabled = false;

    public static void printDebug(String inputString, String inputType){
        if (debugEnabled){
            System.out.println(" " + inputType + " |: " + DEBUG + " - " + inputString);
        }
    }

    public static void printDebug(String inputString){
        if (debugEnabled){
            System.out.println("         |: " + DEBUG + " - " + inputString);
        }
    }

    public static void printLine(String inputString) {
        System.out.println("         | " + inputString);
    }

    public static void printLine(String inputString, String inputType) {
        System.out.println(" " + inputType + " | " + inputString);
    }

    private static void log(String message) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("output.txt", true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.write(message);
        out.close();
    }

    public static void printLine(Exception e){
        System.out.println(" " + ERROR + " | " + e);
    }

    public static void printLine(Throwable t){
        System.out.println(" " + ERROR + " | " + t);
    }

    public static void printLine(int inputInt) {
        printLine(Integer.toString(inputInt));
    }

    public static void printLine(String inputString, int i){
        System.out.println("         | " + inputString + i);
    }

    public static void enabledDebug(){
        debugEnabled = true;
    }

    public static void disableDebug(){
        debugEnabled = false;
    }

    public static boolean isInDebug(){
        return debugEnabled;
    }

    public static void printLine() {
        printLine("");
    }
}
