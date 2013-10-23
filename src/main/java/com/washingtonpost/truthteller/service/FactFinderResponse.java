/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washingtonpost.truthteller.service;

import com.washingtonpost.truthteller.vo.Fact;
import com.washingtonpost.truthteller.vo.PairInt;
import com.washingtonpost.truthteller.vo.TranscriptPos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author sathayeg
 */
public class FactFinderResponse {
    private static Logger log = Logger.getLogger(FactFinderResponse.class);
    
    private static final double PhraseLength_To_PlainEnglishFactLengh_MaxRatio = 6.0;
    private static final double Word_Minimum = 4;
    
    private Fact fact;
    private String transcript;
    private String transcriptSrc;
    private Map<String,PairInt> factPositionsMap;
    private String relevantText;
    private boolean calculatedEquality;
    private boolean combinedAssertion;
    
    private TranscriptPos transcriptPosStart;
    private TranscriptPos transcriptPosEnd;
    private final List<String> errors;
    private String errorMessage;
    
    public FactFinderResponse(){
        this.errors = new ArrayList<String>();
    }
    
    public boolean isPhraseTooLong(){
        if(StringUtils.isBlank(fact.getPlainEnglishFact()) ||
            StringUtils.isBlank(relevantText)){
            return false;
        }
        
        double flen = (double) fact.getPlainEnglishFact().length();
        double rlen = (double) relevantText.length();
        
        if((rlen/flen) > PhraseLength_To_PlainEnglishFactLengh_MaxRatio) return true;
        return false;
    }
    
    public boolean hasMinimumWords(){
        if(StringUtils.isBlank(this.relevantText)) return false;
        return(relevantText.split("\\s").length > Word_Minimum);
    }
    
    public boolean isRelevant(){
        boolean phraseTooLong = isPhraseTooLong();
        boolean hasMinWords = hasMinimumWords();
        
        if(phraseTooLong) errors.add("Phrase is too long compared to plain english text from fact");
        if( (!hasMinWords) && StringUtils.isNotBlank(this.relevantText)) errors.add("Relevant text does not contain at least: " + Word_Minimum + " words");
        
        generateErrorMessageText();
        
        return ( (!phraseTooLong) && hasMinWords && (!hasErrors()));
    }
    
    public boolean hasErrors(){
        return (errors.size() > 0);
    }
    
    public void addErrorMessage(String s){
        errors.add(s);
    }
    
    public String getErrorMessageText(){
        return errorMessage;
    }
    
    private void generateErrorMessageText(){
        StringBuilder sb = new StringBuilder();
         sb.append(this.fact);
        for(String s:errors){
            sb.append(s);
        }
       // sb.append(" \n\t::: transcript: ").append(transcript).append(" \n\t::: relevant text: ").append(
        sb.append(" ::: transcript: ").append(transcript).append("::: relevant text: ").append(relevantText);
        errorMessage = sb.toString();
    }

    public TranscriptPos getTranscriptPosStart() {
        return transcriptPosStart;
    }

    public void setTranscriptPosStart(TranscriptPos start) {
        this.transcriptPosStart = start;
    }

    public TranscriptPos getTranscriptPosEnd() {
        return transcriptPosEnd;
    }

    public void setTranscriptPosEnd(TranscriptPos end) {
        this.transcriptPosEnd = end;
    }   

    public Fact getFact() {
        return fact;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public Map<String, PairInt> getFactPositionsMap() {
        return factPositionsMap;
    }

    public void setFactPositionsMap(Map<String, PairInt> factPositionsMap) {
        this.factPositionsMap = factPositionsMap;
    }

    public String getRelevantText() {
        return relevantText;
    }

    public void setRelevantText(String relevantText) {
        this.relevantText = relevantText;
    }

    public boolean getCalculatedEquality() {
        return calculatedEquality;
    }    

    public void setCalculatedEquality(boolean b) {
        this.calculatedEquality=b;        
    }

    public boolean getCombinedAssertion() {
        return (this.fact.getOriginalAssertion() == this.calculatedEquality);
    }  

    public String getTranscriptSrc() {
        return transcriptSrc;
    }

    public void setTranscriptSrc(String transcriptSrc) {
        this.transcriptSrc = transcriptSrc;
    }
    
    
}
