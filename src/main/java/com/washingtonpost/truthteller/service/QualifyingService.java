package com.washingtonpost.truthteller.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author sathayeg
 */
public class QualifyingService {
    private static Logger log = Logger.getLogger(QualifyingService.class);
    
    //n't ever and n't not catch double negatives, and are treated as positive assertions
//    public static final String PositivesRegexExpr = 
//        "( are | were | has | have | had | do | does | did | is | am | was | would |n't ever|n't not)";
    
    public static final String NegativesRegexExpr = "(not |n't|never)";
//    private static Pattern pPattern;
    private static Pattern nPattern;
    private static boolean ValidState=false;
    
    
    private final FactFinderResponse ffr;
    
    static{
        try{
//            pPattern = Pattern.compile(PositivesRegexExpr);
            nPattern = Pattern.compile(NegativesRegexExpr);
            ValidState=true;
        }catch(Exception e){
            log.fatal("Unable to complie +ve and -ve regex expressions", e);
        }
    }
    
    public QualifyingService(FactFinderResponse ffr){
        this.ffr=ffr;
    }
    
    public boolean inferEquality() throws Exception{ 
        if(! ValidState) throw new Exception("Qualifying Service not in valid state, possibly due to regex compilation errors");        
        Matcher nFactMatcher = nPattern.matcher(ffr.getFact().getPlainEnglishFact());
        Matcher nReltxtMatcher = nPattern.matcher(ffr.getRelevantText());
        int nFactCount = 0;       
        int nReltxtCount = 0; 
//        System.out.println("nFactMatcher");
        while(nFactMatcher.find()){
//            System.out.println(nFactMatcher.group());
            nFactCount++;
        }
//        System.out.println("nReltxtMatcher");
        while(nReltxtMatcher.find()){
//            System.out.println(nReltxtMatcher.group());
            nReltxtCount++;
        }        
        return ((((nFactCount+nReltxtCount)%2)==0)?true:false);        
    }
}
