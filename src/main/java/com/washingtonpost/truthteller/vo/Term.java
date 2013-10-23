package com.washingtonpost.truthteller.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author sathayeg
 */
public class Term {
    
    private static Logger log = Logger.getLogger(Term.class);
    
    private String raw;
    private String delim;
    
    private List<String> synonyms;
    private List<Integer> pos;
    
    //These hold the position of words and the score.  The score is defined as how often
    //they appear near other relevant words. key: position, value: score
    private Map<Integer,Integer> posScores;
    private TreeMap<Integer,Integer> posScoresSorted;
    private boolean posScoresAreDescending = false;
    
    public Term(String raw, String delim){
        this.raw=raw;
        this.delim=delim;
        this.synonyms = new ArrayList<String>();
        this.pos = new ArrayList<Integer>();
        this.posScores = new HashMap<Integer, Integer>();
        this.posScoresSorted = new TreeMap<Integer, Integer>(new PosComparator(posScores, this));
        
    }
    
    public boolean init(){
        try{
            synonyms.addAll(Arrays.asList(raw.split(delim)));
            return true;
        }catch(Exception e){
            log.error("unable to init", e);
            return false;
        }
    }
    
    public boolean termExists(){
        return (pos.size()>0) ? true : false;
    }

    public boolean isPosScoresAreDescending() {
        if(posScoresSorted.size()==1) {
            return true;
        }
        return posScoresAreDescending;
    }

    public void setPosScoresAreDescending(boolean posScoresAreDescending) {
        this.posScoresAreDescending = posScoresAreDescending;
    }   
    
    public boolean initSortedPosScores(){
        if(posScores.size()>0 && posScoresSorted.size()<1){
            this.posScoresSorted.putAll(posScores);
            return true;
        }
        return false;
    }

    public TreeMap<Integer, Integer> getPosScoresSorted() {
        return posScoresSorted;
    }  

    public Map<Integer, Integer> getPosScores() {
        return posScores;
    }   
    
    public void incrementPosScore(Integer pos){
        if(posScores.containsKey(pos)){
            posScores.put(pos, posScores.get(pos)+1);
        }else{
            posScores.put(pos, 1);
        }       
    }

    public String getRaw() {
        return raw;
    }

    public String getDelim() {
        return delim;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<Integer> getPos() {
        return pos;
    }
    
    private class PosComparator implements Comparator<Integer>{
        private final Map<Integer, Integer> map;
        private final Term t;
        public PosComparator(Map<Integer,Integer> map, Term t){
            this.map=map;
            this.t = t;
        }
        public int compare(Integer o1, Integer o2) {
            if(map.get(o1) > map.get(o2)){
                t.setPosScoresAreDescending(true);
                return -1;
            }else if(map.get(o1) < map.get(o2)){
                t.setPosScoresAreDescending(true);
                return 1;
            }
            
            //If can't sort on value(score of position), then sort
            //on position, so we can look at the same region, when finding
            //relevant text
            if(o1 > o2){
                return 1;
            }else if(o1 < o2){
                return-1;
            }
            return -1;
        }        
    }   
}
