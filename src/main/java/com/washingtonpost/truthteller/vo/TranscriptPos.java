/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washingtonpost.truthteller.vo;

/**
 *
 * @author sathayeg
 */
public class TranscriptPos {
    private final int start;
    private final int end;
    private final String text;
    
    public TranscriptPos(int start, int end, String text){
        this.start=start;
        this.end=end;
        this.text=text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getText() {
        return text;
    }   
}
