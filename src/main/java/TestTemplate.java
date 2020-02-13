import com.zjtt.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 添加一条信息
     */
    @Test
    public void solrAdd(){
        TbItem item = new TbItem();
        // 主键相同，即修改
        item.setId(3L);
        item.setBrand("小米为");
        item.setCategory("手机pluse");
        item.setGoodsId(1L);
        item.setSeller("小米1号专卖店");
        item.setTitle("红米Mate9");
        item.setPrice(new BigDecimal(2200));

        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    /**
     * 按照主键查找
     */
    @Test
    public void testFindOne(){
        TbItem item = solrTemplate.getById(3L, TbItem.class);
        System.out.println(item.getTitle());
    }

    /**
     * 按照主键删除
     */
    @Test
    public void testDelete(){
        solrTemplate.deleteById("3L");
        solrTemplate.commit();
    }

    /**
     * 通过遍历添加100条数据
     */
    @Test
    public void testAddList(){
        List<TbItem> list=new ArrayList();
        for(int i=1;i<101;i++){
            TbItem item=new TbItem();
            item.setId(Long.valueOf(i));
            item.setBrand("华为");
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setSeller("华为"+i+"号专卖店");
            item.setTitle("华为Mate"+i);
            item.setPrice(new BigDecimal(2000+i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 分页查询的测试
     */
    @Test
    public void testPageQuery() {
        Query query = new SimpleQuery("*:*");
        query.setOffset(10);// 开始索引（默认0）
        query.setRows(20);	// 每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> list = page.getContent();

        for (TbItem item : list) {
            System.out.println(item.getTitle() + item.getPrice());
        }

    }

    /**
     * Criteria进行条件封装，条件查询
     */
    @Test
    public void testPageQueryMutil() {
        Query query = new SimpleQuery("*:*");

        Criteria criteria = new Criteria("item_title").contains("2");
        criteria = criteria.and("item_price").greaterThan(2020); //不赋值,不生效
        query.addCriteria(criteria);

        Sort s = new Sort(Sort.Direction.DESC, "item_price");
        query.addSort(s);

        // query.setOffset(10); //开始索引（默认0）
        // query.setRows(100); //每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> list = page.getContent();

        for (TbItem item : list) {
            System.out.println(item.getTitle() + "，" + item.getPrice());
        }
    }

    /**
     * 删除全部数据
     */
    @Test
    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
