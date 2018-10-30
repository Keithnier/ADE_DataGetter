package PreProcess.DataPreProcess.TFIDF;


import CommonUtil.MongoDB.DataBaseUtil;
import CommonUtil.MongoDB.Read;
import PreProcess.DataPreProcess.InfoExtra.TwitterExtract;
import PreProcess.DataPreProcess.Model.TwitterInfoModel;
import PreProcess.DataPreProcess.Segment.Segment;
import PreProcess.DataPreProcess.Util.FileSystemUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import static PreProcess.DataPreProcess.Util.PropertyUtil.getValueByKey;

public class PreCut {
    public static final int TWITTER = 0;//处理twitter
    public static final int YOUTUBE = 1;
    public static final int FLICKR = 2;
    public static final int TUMBLR = 3;



    public static void preCut(int TypeCode, Segment seg){
        switch (TypeCode){
            case TWITTER:
                __preCutTwitter__(seg);
                break;
            case YOUTUBE:
                break;
            case FLICKR:
                break;
            case TUMBLR:
                break;
            default:
                break;
        }
    }
    public static void preCut(int TypeCode, Segment seg, String Collection){
        switch (TypeCode){
            case TWITTER:
                __preCutTwitter__(seg, Collection);
                break;
            case YOUTUBE:
                break;
            case FLICKR:
                break;
            case TUMBLR:
                break;
            default:
                break;
        }
    }
    /**
     * 预切分
     * @param segment
     */
    private static void __preCutTwitter__(Segment segment){
        String DBnames = getValueByKey("TwitterDataBaseName");
        String stoppath = getValueByKey("stopwordpath");
        List<String> colnames = DataBaseUtil.getColletcionNamesInDB(DBnames);

        PrintStream ps_seg;
        PrintStream ps_local;
        PrintStream ps_time;
        PrintStream ps_id;
        PrintStream ps_bounding;
        String cachePath = PreCut.class.getClassLoader().
                getResource("PreProcess/Cache/").getPath();
        try{
            ps_seg = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterSeg__"));
            ps_local = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterLocal__"));
            ps_time = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterTime__"));
            ps_id = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterId__"));
            ps_bounding = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterBounding__"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("文件打开失败");
            return;
        }
        //处理正常情况
        for(String col : colnames){
            String elem;
            while((elem=Read.nextElem(DBnames,col))!=null){
                TwitterInfoModel tif = new TwitterExtract().jsonInfoExtra(elem);
                if(tif==null) continue;
                String text = tif.getText();
                String timestamp = tif.getTimestamp();
                String id = tif.getId();
                String boundingbox = String.valueOf(tif.getBoundingBox().getX())
                        +":"+String.valueOf(tif.getBoundingBox().getY());
                String localtionName = tif.getLocationName();
                //处理属性为null情况
                if(text == null || timestamp==null||id==null||localtionName==null) continue;
                //将属性对号入座到文件中
                List<String> stringList = segment.doAll(text,FileSystemUtil.getFullDataPath(stoppath));
                if(stringList.size()==0) continue;;
                StringBuilder cur_text = new StringBuilder();
                int count = 0;
                for(String str:stringList){
                    count++;
                    if(count<stringList.size())
                        cur_text.append(str).append(":");
                    else
                        cur_text.append(str);
                }
                ps_seg.println(cur_text.toString());
                ps_time.println(timestamp);
                ps_local.println(localtionName);
                ps_bounding.println(boundingbox);
                ps_id.println(id);

            }
        }
        ps_seg.close();
        ps_local.close();
        ps_time.close();
        ps_id.close();
        ps_bounding.close();
    }


    private static void __preCutTwitter__(Segment segment, String Collections){
        String DBnames = getValueByKey("TwitterDataBaseName");
        String stoppath = getValueByKey("stopwordpath");

        PrintStream ps_seg;
        PrintStream ps_local;
        PrintStream ps_time;
        PrintStream ps_id;
        PrintStream ps_bounding;
        String cachePath = PreCut.class.getClassLoader().
                getResource("PreProcess/Cache/").getPath();
        try{
            ps_local = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterLocal__"));
            ps_seg = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterSeg__"));
            ps_time = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterTime__"));
            ps_id = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterId__"));
            ps_bounding = new PrintStream(new FileOutputStream(cachePath+"__preCutTwitterBounding__"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("文件打开失败");
            return;
        }
        //处理正常情况
        String elem;
        while((elem=Read.nextElem(DBnames,Collections))!=null){
            TwitterInfoModel tif = new TwitterExtract().jsonInfoExtra(elem);
            if(tif==null) continue;
            String text = tif.getText();
            String id = tif.getId();
            String timestamp = tif.getTimestamp();
            String boundingbox = String.valueOf(tif.getBoundingBox().getX())
                    +":"+String.valueOf(tif.getBoundingBox().getY());
            String localtionName = tif.getLocationName();
            //处理属性为null情况
            if(text == null || timestamp==null||id==null||localtionName==null) continue;
            //将属性对号入座到文件中
            List<String> stringList = segment.doAll(text,FileSystemUtil.getFullDataPath(stoppath));
            if(stringList.size()==0) continue;;
            StringBuilder cur_text = new StringBuilder();
            int count = 0;
            for(String str:stringList){
                count++;
                if(count<stringList.size()) {
                    cur_text.append(str);
                    cur_text.append(":");
                }
                else
                    cur_text.append(str);
            }
            ps_seg.println(cur_text.toString());
            ps_time.println(timestamp);
            ps_local.println(localtionName);
            ps_bounding.println(boundingbox);
            ps_id.println(id);
        }
        ps_seg.close();
        ps_local.close();
        ps_time.close();
        ps_id.close();
        ps_bounding.close();
    }
}
