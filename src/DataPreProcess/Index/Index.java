package DataPreProcess.Index;

import DataPreProcess.Model.InfoModel;

import java.io.File;
import java.util.List;

import static DataPreProcess.Util.FileSystemUtil.getFullDataPath;

public interface Index <T extends InfoModel>{
    /**创建索引的文件位置**/
    String STORY_PATH = getFullDataPath("LuceneIndexDir/");

    /**
     * 创建索引
     */
    void createIndex();

    /**
     * 从索引文件中根据问题检索答案
     * @param text 搜索关键词
     * @return 搜索结果
     */
    List<T> searchByText(String text) ;

    /**
     * 删除文件夹内所有内容
     * @param file
     */
    void deleteAll(File file);
}
