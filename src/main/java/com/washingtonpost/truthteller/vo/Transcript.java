/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washingtonpost.truthteller.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sathayeg
 */
public class Transcript {
    private String text;
    private List<TranscriptPos> posList;
    
    public Transcript(){
        this.posList=new ArrayList<TranscriptPos>();
    }
    
    public void addToPosList(int start, int end, String text){
        this.posList.add(new TranscriptPos(start, end, text));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TranscriptPos> getPosList() {
        return posList;
    }

    public void setPosList(List<TranscriptPos> posList) {
        this.posList = posList;
    }
    
}
