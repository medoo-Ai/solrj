import com.baidu.Item;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther SyntacticSugar
 * @data 2018/11/24 0024下午 11:31
 */
public class SolrJTest {

    @Test
    public void test() throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "4");
        document.addField("title", "五菱宏光，神车");
        document.addField("price", 30000F);
        //
        server.add(document);
        //提交
        server.commit();
    }

    /**
     * pojo中属性没有注解@Field，导致solrJ并不知道哪个属性要对应到索引库中
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void testCreateIndexBean() throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        //
        Item item = new Item();
        item.setId("9");
        item.setPrice(3000F);
        item.setTitle("小米手机");
        // server中添加bean
        server.addBean(item);
        server.commit();
    }

    /**
     * testDeleteIndexById
     * 根据id  删除
     */
    @Test
    public void testDeleteIndexById() throws IOException, SolrServerException {
        //使用solrj的jar
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        server.deleteById("1");
        server.commit();
    }

    /**
     * 根据查询条件删除索引
     */
    @Test
    public void testDeleteIndexByQuery() throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        server.deleteByQuery("title:iphone");
        server.commit();
    }

    /**
     * query返回document 的形式
     */
    @Test
    public void testQueryToDocument() throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        //public class SolrQuery extends ModifiableSolrParams
        SolrQuery query = new SolrQuery("title:手机");
        //public QueryResponse query(SolrParams params)
        QueryResponse response = server.query(query);
        SolrDocumentList list = response.getResults();
        /**
         * 遍历list ，取出document
         */
        for (SolrDocument entries : list) {
            System.out.println(entries.getFieldValue("id"));
            System.out.println(entries.getFieldValue("title"));
            System.out.println(entries.getFieldValue("price"));
        }
        System.out.println("查询的总条数：" + list.size());
    }

    /**
     * query返回javabean 的形式
     */
    @Test
    public void testQueryToJavabean() throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        /**
         * 普通查询
         */
//        SolrQuery query = new SolrQuery("title:iphone");
        /**
         * boolean  查询
         */
//        SolrQuery query = new SolrQuery("title:iphone OR title:小米");
        /**
         *  相似度查询 ，类似于Lucene的编辑距离0-2
         */
//        SolrQuery query = new SolrQuery("title:ipHOne~2");
        /**
         * 范围查询  xx  TO  xx
         * 闭区间
         */
        SolrQuery query = new SolrQuery("price:[10 TO 10000]");

        //
        QueryResponse response = server.query(query);
        List<Item> itemList = response.getBeans(Item.class);
        for (Item item : itemList) {
            System.out.println(item);
        }
        System.out.println("搜多到的条数：" + itemList.size());
    }

    /**
     * 使用 solrj  查询排序
     */
    @Test
    public void testQueryOrder() throws SolrServerException {
        //连接solr服务器、创建query对象、设置查询排序参数 、查询
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        SolrQuery query = new SolrQuery("title:手机");
        /**设置排序源码
         *  public SolrQuery setSort(String field, ORDER order) {
         *     return setSort(new SortClause(field, order));
         *   }
         */
        query.setSort("price", SolrQuery.ORDER.desc);
        QueryResponse response = server.query(query);
        //
        List<Item> itemList = response.getBeans(Item.class);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
        System.out.println("搜索到的条数：" + itemList.size());
    }

    /**
     * 使用 solrj 高亮显示 testHighlightingQuery
     * 1、set高亮标签，add高亮字段
     * 2、高亮工具
     */
    @Test
    public void testHighlightingQuery() throws SolrServerException {
        //连接solr服务器、创建query对象、高亮、查询
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        SolrQuery query = new SolrQuery("title:手机");
        // 标签、字段
        query.setHighlightSimplePre("<em>");
        query.setHighlightSimplePost("</em>");
        query.addHighlightField("title");
        //
        QueryResponse response = server.query(query);
        /**
         *   高亮的响应结果  是一个双层map ：
         *   id  :对应id的高亮字段
         *   高亮字段：高亮值  为list
         */
        Map<String, Map<String, List<String>>> map = response.getHighlighting();
        /**
         * 普通查询结果
         */
        List<Item> itemList = response.getBeans(Item.class);
        for (Item item : itemList) {
            System.out.println("id" + item.getId());
            //  高亮值  为list
            System.out.println("title" + map.get(item.getId()).get("title").get(0));
            System.out.println("price" + item.getPrice());
        }
        System.out.println("搜索到的条数：" + itemList.size());
    }

    /**
     * 分页
     * 1、准备参数
     * 2、设置到query中
     */
    @Test
    public void testPagedQuery() throws SolrServerException {
        //连接solr服务器、创建query对象、设置查询排序参数 、查询
        HttpSolrServer server = new HttpSolrServer("http://localhost:8080/solr/core1");
        SolrQuery query = new SolrQuery("title:手机");
        // 设置分页
        int currentPage = 2;
        final int PAGE_SIZE = 4;
        int start = (currentPage - 1) * PAGE_SIZE;
        // 设置起始页，设置每页显示数目
        query.setStart(start);
        query.setRows(PAGE_SIZE);
        //
        QueryResponse response = server.query(query);
        List<Item> itemList = response.getBeans(Item.class);
        //展示数据
        System.out.println("当前页："+currentPage+"共查询数据："+itemList.size());
        for (Item item : itemList) {
            System.out.println("数据："+item.toString());
        }
    }

}
