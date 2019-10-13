package de.tu.darmstadt.dao;

import de.tu.darmstadt.domain.NewsDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface NewsDao{

    @Select("select news_id, news_type, news_title, news_fulltext from washingtonpost_21855")
    List<NewsDoc> selectAll();

    List<NewsDoc> selectByType(String type);

}
