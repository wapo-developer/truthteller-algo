package com.washingtonpost.truthteller.vo;

/**
 *
 * @author sathaye
 * 
 * For example:
 * plainEnglishFact may be: "U.S. has the highest corporate tax rates in the world";
 * 
 * factKeywords may be: "us|u.s.|united states,corporate,tax,world,high";
 * to look for a word that starts with world (notice space before 'world': "us|u.s.|united states,corporate,tax, world,high";
 * to look for a word that ends with world (notice space after 'world': "us|u.s.|united states,corporate,tax,world ,high";
 * to look for the whole word 'world' (notice spaces before and after 'world': "us|u.s.|united states,corporate,tax, world ,high";
 * 
 * factKeywordSplitRegex may be: ",";
 * factKeywordSynonymSplitRegex may be: "\\|";
 */
public class Fact {
    
    public static final String DefaultKeywordSplitRegex = ",";
    public static final String DefaultKeywordSynonymSplitRegex = "\\|";
    
    private final String plainEnglishFact;
    private final String factKeywords;
    private final String factKeywordSplitRegex;
    private final String factKeywordSynonymSplitRegex;
    private final boolean originalAssertion;
    private final String source;
    private final String id;
    
    public Fact(String plainEnglishFact, String factKeywords, boolean originalAssertion,
            String factKeywordSplitRegex, String factKeywordSynonymSplitRegex, String source,String id){
        this.plainEnglishFact=plainEnglishFact;
        this.factKeywords=factKeywords;
        this.originalAssertion=originalAssertion;
        this.factKeywordSplitRegex=factKeywordSplitRegex;
        this.factKeywordSynonymSplitRegex=factKeywordSynonymSplitRegex;
        this.source=source;
        this.id=id;
    }
    
    public Fact(String plainEnglishFact, String factKeywords, boolean originalAssertion,
            String source,String id){
        this(plainEnglishFact, factKeywords, originalAssertion, 
            DefaultKeywordSplitRegex, DefaultKeywordSynonymSplitRegex,
            source,id);
    }

    public boolean getOriginalAssertion() {
        return originalAssertion;
    }   

    public String getPlainEnglishFact() {
        return plainEnglishFact;
    }
   
    public String getFactKeywords() {
        return factKeywords;
    }
   
    public String getFactKeywordSplitRegex() {
        return factKeywordSplitRegex;
    }

    public String getFactKeywordSynonymSplitRegex() {
        return factKeywordSynonymSplitRegex;
    }

    public String getSource() {
        return source;
    }

     public String getId() {
          return id;
     }
    

    @Override
    public String toString() {
        return "Fact{" + "Id = "+id+ " , plainEnglishFact=" + plainEnglishFact + ", factKeywords=" + factKeywords + ", originalAssertion=" + originalAssertion + ", source=" + source + '}';
    }  

}
