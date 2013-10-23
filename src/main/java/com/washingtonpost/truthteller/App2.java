package com.washingtonpost.truthteller;

import com.washingtonpost.truthteller.vo.Fact;
import com.washingtonpost.truthteller.service.FactFinderResponse;
import com.washingtonpost.truthteller.service.PhraseService;
import com.washingtonpost.truthteller.service.QualifyingService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sathayeg
 */
public class App2 {
    
    List<Fact> facts;
    
    public static void main(String[] args){
        new App2().doStuff();
    }
    
    public App2(){
        facts = new ArrayList<Fact>();
        populateFactList();
    }
    
    void doStuff(){
        long start = System.currentTimeMillis();
        try {
            String transcript = getTranscript();
            for(Fact fact:facts){
                tPhraseService(transcript, fact, null);
            }
        } catch (Exception e) {
            p("App2.dostuff error: " + e);
        }
        p("elapsed: " + (System.currentTimeMillis()-start));
    }
    
    void tPhraseService(String transcript, Fact fact, String url){
        try{
            PhraseService ps = new PhraseService(transcript, fact, url);
            List<FactFinderResponse> ffrs = ps.locate();
            p("++++++++++++");
            p("ffrs len: " + ffrs.size());
            for(FactFinderResponse ffr:ffrs){
                if(ffr.isRelevant()){
                    p(ffr.getRelevantText()); 
                    ffr.setCalculatedEquality(new QualifyingService(ffr).inferEquality());
                    p("according to fact: this statement is: " + ffr.getCombinedAssertion());
                    p("---");
                }else{
                    //p("large cluster: " + ffr.getRelevantText());
                }                
            }
        }catch(Exception e){
            p("App2 error: " + e);
        }
    } 

    private void populateFactList() {
        this.facts.add(new Fact(
                "U.S. has the highest corporate tax rate in the world", 
                "us|u.s.|united states|we,corporate|corporation,tax,world,high, rate",
                true,"sample source 1","101"));
        this.facts.add(new Fact(
                "98 percent of American families make less than $250,000",
                "98 percent|98%|98 %,american|us|united states|usa,families|family,make|earn,less,250k|250000|250,000",
                true, "sample source 2","100"));
    }
    
    static String getTranscript() throws Exception{
        String transcriptFile = 
            //"C:\\mydocs\\washingtonpost\\projects\\TruthTeller\\transcript.txt";
            "C:\\mydocs\\projects\\TruthTeller\\transcript.txt";
            //"C:\\mydocs\\washingtonpost\\projects\\TruthTeller\\spacedOutWords.txt";
        BufferedReader br = null;
        FileReader fr = null;
        try{
            fr = new FileReader(transcriptFile);
            br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        }catch(Exception e){
            p("Error unable to get transcript: " + e);
            throw e;
        }finally{
            try{fr.close();}catch(Exception e){}
            try{br.close();}catch(Exception e){}
        }
    }
    
    static void p(Object o){
        System.out.println(o);
    }
}
