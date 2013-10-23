package com.washingtonpost.truthteller.service;

import com.washingtonpost.truthteller.vo.Fact;
import com.washingtonpost.truthteller.vo.PairInt;
import com.washingtonpost.truthteller.vo.Term;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author sathaye
 */
public class FactFinder {
    private static Logger log = Logger.getLogger(FactFinder.class);
    
    private String transcript;
    private Fact fact;
    
    private boolean initDone = false;
    private List<Term> termList;
    
    private static final double Min_Ratio_ExistingTerms_Over_Total_Terms = 0.68;
    private double numTermsExistInTranscript=0;
    private double ratioExistingTermsOverTotalTerms;
    
    private Map<String,PairInt> factPositionsMap;
    
    private Pattern Pattern_Whitespace_EOL;
    private String relevantText;
    private final String transcriptSource;
    
    public FactFinder(String transcript, Fact fact, String transcriptSource){
        this.transcript=transcript;
        this.fact=fact;
        this.transcriptSource = transcriptSource;
    }
    
    private synchronized void init() throws Exception{
        if(initDone) return;
        
        if(StringUtils.isBlank(transcript)){
            throw new Exception("transcript is blank");
        }
        
        try{
            Pattern_Whitespace_EOL = Pattern.compile("\\s|$");
        }catch(Exception e){
            throw e;
        }
        
        transcript = transcript.toLowerCase().trim();
        
        termList = new ArrayList<Term>();
        factPositionsMap = new HashMap<String, PairInt>();
        
        createTermListFromFact();
        if(termList.size() < 2) {
            throw new NotEnoughTermsInFactException("Number of terms in fact are too low (<2): " + fact.getFactKeywords());
        }
        
        
        try{
            set_positions_of_synonyms_in_transcript();
        }catch(Exception e){
            throw new Exception("Unable to set positions of synonyms in transcript: fact: " + fact.getFactKeywords(), e);
        }
        
        setTermTranscriptRatios();
        if(ratioExistingTermsOverTotalTerms < Min_Ratio_ExistingTerms_Over_Total_Terms){
            p("throw term ratio too small exception");
            throw new TermRatioTooSmallException("Ratio of terms existing in transcript is less than: " + Min_Ratio_ExistingTerms_Over_Total_Terms);
        }
        
        initDone = true;
    }
    
    private class NotEnoughTermsInFactException extends Exception {
        public NotEnoughTermsInFactException(String string) {
            super(string);
        }       
    }
    
    private class TermRatioTooSmallException extends Exception {
        public TermRatioTooSmallException(String string) {
            super(string);
        }       
    }
    
    public FactFinderResponse find() throws Exception{
        FactFinderResponse ffr = new FactFinderResponse();
        ffr.setTranscriptSrc(transcriptSource);
        ffr.setFact(fact);
        ffr.setTranscript(transcript);
        
        try{
            init();
        }catch(NotEnoughTermsInFactException e){
            ffr.addErrorMessage(e.getMessage());
            return ffr;
        }catch(TermRatioTooSmallException e){
            ffr.addErrorMessage(e.getMessage());
            return ffr;
        }
        
        try{
            calcTermDistances();
        }catch(Exception e){
            throw new Exception("Unable to calc term distances, fact: " + fact.getFactKeywords(), e);
        }
        
        try{
            calcFactPositions();
        }catch(Exception e){
            throw new Exception("Unable to calc fact positions, fact: " + fact.getFactKeywords(), e);
        }
        
        try{
            getRelevantText();
        }catch(Exception e){
            throw new Exception("Unable to get relevant text, fact: " + fact.getFactKeywords(), e);
        }        
        
        ffr.setFactPositionsMap(factPositionsMap);
        ffr.setRelevantText(relevantText);        
        return ffr;
    }
    
    private void setTermTranscriptRatios(){
        p("\n-----\n");
        p(this.relevantText);
        for(Term t:termList){
            if(t.termExists()) {
                p("term exists: " + t.getRaw());
                ++numTermsExistInTranscript;
            }
        }   
        this.ratioExistingTermsOverTotalTerms = this.numTermsExistInTranscript/((double)termList.size()); 
        p("num terms in transcript: " + this.numTermsExistInTranscript);
        p("term list size: " + termList.size());
        p("ratio terms: " + this.ratioExistingTermsOverTotalTerms);
    }
    
    static void p(Object o){
        System.out.println(o);
    }
    
    private void getRelevantText() throws Exception {
        PairInt focused = factPositionsMap.get("focus");
        PairInt neutral = factPositionsMap.get("neutral");
        
        int startpos = neutral.getP1();
        int endpos = neutral.getP2();       
        
        //Get the last term after endpos, because most likely, endpos is
        //pointing to the beginning of the last term.
        if(transcript.length() > neutral.getP2()){
            Matcher matcher = Pattern_Whitespace_EOL.matcher(transcript);
            if(matcher.find(neutral.getP2())){
                endpos = matcher.start();
                
                //one of the terms in FACT may have space at beginning,
                //so go beyond that,
                //for example: "us|u.s.|united states|we,corporate|corporation,tax,world,high, rate"
                //notice space before "rate"
                if(endpos==neutral.getP2()){ 
                    if(matcher.find(neutral.getP2()+1)){
                        endpos = matcher.start();
                    }
                }
            }
        }
        
        this.relevantText = transcript.substring(startpos, endpos);
    }
    
    private void calcFactPositions() throws Exception{        
        //Neutral min and max are to get positions of other relevant words,
        //whose scores/counts are all the same (can't be sorted),
        //close to the relevant min and max, but might be outside the min/max range.
        int neutralMin=-1;
        int neutralMax=-1;
        
        int min=-1;
        int max=-1;
        
        List<Term> neutralTermList = new ArrayList<Term>();
        
        for(Term t:termList){
            if(! t.initSortedPosScores()) continue;
            log.debug(t.getRaw() + " : " + t.getPosScoresSorted() + " : " + t.getPosScores());
            Map.Entry<Integer, Integer> firstEntry = t.getPosScoresSorted().firstEntry();
            if(null == firstEntry) continue;
            int position=firstEntry.getKey();
            int count = firstEntry.getValue();
            if(! t.isPosScoresAreDescending()) {
                log.debug("pos scores are not desceding: " + t.getPosScoresSorted());
                neutralTermList.add(t);
                continue;
            }
            if(min==-1){
                min=position; max=position;
            }else{
                if(position < min) min=position;
                if(position > max) max=position;
            }           
        }
        log.debug("min/max pos: " + min + "/" + max);
        
        if((min>-1) && (max>-1)){
            neutralMin=min; neutralMax=max;
            if(neutralTermList.size()>0){
                for(Term t:neutralTermList){
                    Set<Map.Entry<Integer, Integer>> es = t.getPosScoresSorted().entrySet();
                    for(Map.Entry<Integer,Integer> e:es){
                        if(  (e.getKey()<neutralMin) && ((min-e.getKey())<fact.getPlainEnglishFact().length())  ){
                            neutralMin=e.getKey();
                        }else if(  (e.getKey()>neutralMax) && ((e.getKey()-max)<fact.getPlainEnglishFact().length())  ){
                            neutralMax=e.getKey();
                        }
                    }
                }
            }
            log.debug("neutral min max: " + neutralMin + " : " + neutralMax);
            factPositionsMap.put("focus", new PairInt(min, max));
            factPositionsMap.put("neutral", new PairInt(neutralMin, neutralMax));
            return;
        }
        throw new Exception("processPositionScores: Unable to get min max pos");
    }
    
    private void calcTermDistances() throws Exception{
        int shiftt = 0;
        for(Term t:termList){
            //p(t.getRaw() + " : " + t.getPos());
            //p("shiftt: " + shiftt);
            for(int i = shiftt;i<termList.size();i++){
                Term t2 = termList.get(i);
                if(! t.getRaw().equals(t2.getRaw())){
                    log.debug("COMPARE: " + t.getRaw() + ":" + t.getPos() + " to " + t2.getRaw() + ":" + t2.getPos());
                    int min=-1; int minpos1=-1; int minpos2=-1;
                    for(Integer pos:t.getPos()){
                        for(Integer pos2:t2.getPos()){
                            int diff = Math.abs(pos - pos2);
                            log.debug("pos: " + pos + " - pos2: " + pos2 + " :: " + diff);
                            if(min==-1){
                                min = diff; minpos1=pos; minpos2=pos2;
                            }else if(diff < min){
                                min=diff; minpos1=pos; minpos2=pos2;
                            }
                        }
                    }
                    log.debug("min: " + min + ", minpos: " + minpos1 + ", minpos2: " + minpos2);
                    if((minpos1>-1)&&(minpos2>-1)){
                        t.incrementPosScore(minpos1);
                        t2.incrementPosScore(minpos2);
                    }
                }
            }
            ++shiftt;
        }
    }
    
    private void set_positions_of_synonyms_in_transcript(){
        for(Term t:termList){
            List<String> synonyms = t.getSynonyms();        
            for(String synonym:synonyms){
                int startIdx = 0; int idx=0;           
                while( (idx=transcript.indexOf(synonym, startIdx)) > -1 ){
                    log.debug(synonym + " : " + idx);
                    startIdx = synonym.length() + idx;
                    t.getPos().add(idx);
                }
            }
        }
    }
    
    private void createTermListFromFact(){
        String[] split = fact.getFactKeywords().split(fact.getFactKeywordSplitRegex());
        for(int i=0;i<split.length;i++){
            Term term = new Term(split[i], fact.getFactKeywordSynonymSplitRegex());
            if(term.init()){
                termList.add(term);
            }
        }
    }

    public String getTranscript() {
        return transcript;
    }   
}
