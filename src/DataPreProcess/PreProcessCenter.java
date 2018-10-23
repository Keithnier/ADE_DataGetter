package DataPreProcess;

import DataPreProcess.Dictionary.GenerateDic;
import DataPreProcess.Segment.EnglishSegment;
import DataPreProcess.Segment.Segment;
import DataPreProcess.TFIDF.PreCut;
import DataPreProcess.TFIDF.TFIDF;

public class PreProcessCenter {
    public static void main(String[]  args){
        Segment seg = new EnglishSegment();
        GenerateDic.generateDict("TwitterData",seg,"twitter_20171212132632_data");
        TFIDF.tfidf(PreCut.TWITTER,seg,"twitter_20171212132632_data");
    }
}
