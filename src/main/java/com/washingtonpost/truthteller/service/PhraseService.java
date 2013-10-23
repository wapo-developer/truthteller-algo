package com.washingtonpost.truthteller.service;

import com.washingtonpost.truthteller.vo.Fact;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author sathayeg
 */
public class PhraseService {
    private static Logger log = Logger.getLogger(PhraseService.class);
    
    private static final int MaxDepth=200;
    
    private final String text;
    private final Fact fact;
    
    private List<FactFinderResponse> ffrs;
    private final String transcriptSource;
    
    public PhraseService(String text, Fact fact, String transcriptSource){
        this.text=text;
        this.fact=fact;
        this.transcriptSource = transcriptSource;
    }
    
    public List<FactFinderResponse> locate(){
        ffrs = new ArrayList<FactFinderResponse>();
        try{
            locate2(new FactFinder(text, fact, transcriptSource), 0); 
        }catch(Exception e){
            log.error("locate(): Unable to locate: " + e);
        }
        return ffrs;
    }
    
//    private void locate(FactFinder ff, int depth) throws Exception{
//        if(true) throw new Exception("This method does not work as well as locate2");
//        if(depth > MaxDepth) return;
//        try{
//            FactFinderResponse ffr = ff.find();
//            ffrs.add(ffr);
//            if(ffr.hasErrors()) return;
//            ++depth;
//            String relevantText = ffr.getRelevantText();
//            if(ffr.isPhraseTooLong()){
//                String[] halfText = halfText(relevantText);
//                for(int i=0;i<halfText.length;i++){
//                    locate(new FactFinder(halfText[i], fact), depth);
//                }
//            }
//            
//            //keep looking for relevant text outside the main focus region
//            //String overlap = relevantText.substring(0, relevantText.lastIndexOf(" "));
//            System.out.println("remove: " + relevantText);
//            String textWithoutPreviouslyRelevantPhrase = ffr.getTranscript().replace(relevantText, "");
//            locate(new FactFinder(textWithoutPreviouslyRelevantPhrase,fact),depth); 
//            
//        }catch(Exception e){
//            log.error("locate(FactFinder,int): Unable to locate: " + e);
//        }
//    }
    
    private void locate2(FactFinder ff, int depth){
        if(depth > MaxDepth) return;
        try{
            FactFinderResponse ffr = ff.find();
            ffrs.add(ffr);
            ++depth;
            
            String relevantText = ffr.getRelevantText();
            
            String[] halfText = halfText(relevantText);            
            String firstHalf = halfText[0];
            String secondHalf = halfText[1];
            
            if(StringUtils.isNotBlank(firstHalf)){
                int endfh = ffr.getTranscript().indexOf(firstHalf) + firstHalf.length();
                String fh = ffr.getTranscript().substring(0, endfh);
                locate2(new FactFinder(fh, fact, transcriptSource), depth);
            }
            
            if(StringUtils.isNotBlank(secondHalf)){
                String sh = ffr.getTranscript().substring(ffr.getTranscript().indexOf(secondHalf));
                locate2(new FactFinder(sh, fact, transcriptSource), depth);
            }
        }catch(Exception e){
            log.error("locate(FactFinder,int): Unable to locate: " + e);
        }
    }
    
    public static String[] halfText(String text){
        String[] arr = new String[2];
        int half = text.length() / 2;
        arr[0]=text.substring(0,half);
        arr[1]=text.substring(half, text.length());
        return arr;
    }
    
}
