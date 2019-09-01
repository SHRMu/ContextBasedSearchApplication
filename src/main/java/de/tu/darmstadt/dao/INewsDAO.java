package de.tu.darmstadt.dao;

import de.tu.darmstadt.model.NewsDoc;

import java.util.List;

public interface INewsDAO {

    public NewsDoc getNewsDoc(Integer news_id);

    public List<NewsDoc> getWorldNewsDoc();

}
