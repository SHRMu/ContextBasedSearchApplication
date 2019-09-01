package de.tu.darmstadt.dao;

import de.tu.darmstadt.model.NewsDoc;
import de.tu.darmstadt.model.NewsMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JDBCNews implements INewsDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObj;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObj = new JdbcTemplate(dataSource);
    }

    @Override
    public NewsDoc getNewsDoc(Integer news_id) {
        String SQL = "SELECT * FROM news_data_10000 WHERE news_id = ? ";
        NewsDoc newsDoc = jdbcTemplateObj.queryForObject(SQL, new Object[]{news_id}, new NewsMapper());
        return newsDoc;
    }

    @Override
    /*
    *
    * */
    public  List<NewsDoc>  getWorldNewsDoc(){
        String SQL = "SELECT * FROM news_data_10000 WHERE news_type LIKE 'world%' ";
        List<NewsDoc> query = jdbcTemplateObj.query(SQL, new NewsMapper());
        return query;
    }

}
