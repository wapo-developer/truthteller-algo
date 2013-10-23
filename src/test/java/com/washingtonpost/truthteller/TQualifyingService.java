/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.washingtonpost.truthteller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sathayeg
 */
public class TQualifyingService {
    
    static String PositivesRegexExpr = "( are | were | has | have | had | do | does | did | is | am | was | would )";
    static String NegativesRegexExpr = "(not |n't)";
    
    public static void main(String[] args){
        q1();
    }
    
    static void q1(){
        
        p("0%2: " + (0%2));
        p("true && true: " + (true && true));
        p("false && false: " + (false && false));
        
        String s = 
                //"return on equity for ko isn't 30% and are the highest in the world";
                "Tax loopholes cost $1.1 trillion";
        
        Pattern pPattern = Pattern.compile(PositivesRegexExpr);
        Matcher pMatcher = pPattern.matcher(s);
        int pCount = 0;
        
        Pattern nPattern = Pattern.compile(NegativesRegexExpr);
        Matcher nMatcher = nPattern.matcher(s);
        int nCount = 0;
        
        while(pMatcher.find()){
            p(pMatcher.group());
            pCount++;
        }
        
        while(nMatcher.find()){
            p(nMatcher.group());
            nCount++;
        }
        
        p("pCount: " + pCount + ", nCount: " + nCount);
        p(false && false);
    }
    
    static void p(Object o){
        System.out.println(o);
    }
}
