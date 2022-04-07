package com.devilhan.solr;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hanyanjiao
 * @Date: 2022/4/7
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MyApplication.class)
public class SpringDataSolrTest {
    @Autowired
    private SolrTemplate solrTemplate;

    public void testInsert(){
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id","002");
        doc.setField("item_title","这是一个手机3");
        UpdateResponse ur = solrTemplate.saveBean("collection1", doc);
        if(ur.getStatus()==0){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
        solrTemplate.commit("collection1");
    }

    public void testDelete(){
        UpdateResponse ur = solrTemplate.deleteByIds("collection1", "change.me");
        if(ur.getStatus()==0){
            System.out.println("成功");
        }else{
            System.out.println("失败");
        }
        solrTemplate.commit("collection1");
    }


    public void query(){
        SimpleQuery query = new SimpleQuery();
        Criteria c = new Criteria("item_keywords");
        c.is("手机");
        query.addCriteria(c);
        query.setOffset(1L);
        query.setRows(1);
        ScoredPage<DemoPojo> sp = solrTemplate.queryForPage("collection1", query, DemoPojo.class);
        System.out.println(sp.getContent());
    }

    @Test
    public void queryHL(){

        List<DemoPojo> listResult = new ArrayList<>();
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置查询条件
        Criteria c = new Criteria("item_keywords");
        c.is("手机");
        query.addCriteria(c);
        //分页
        query.setOffset(0L);
        query.setRows(10);
        //排序
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        query.addSort(sort);
        //高亮设置
        HighlightOptions hlo = new HighlightOptions();
        hlo.addField("item_title item_sell_point");
        hlo.setSimplePrefix("<span style='color:red;'>");
        hlo.setSimplePostfix("</span>");
        query.setHighlightOptions(hlo);
        HighlightPage<DemoPojo> hl = solrTemplate.queryForHighlightPage("collection1", query, DemoPojo.class);
//        System.out.println(hl.getContent());
        List<HighlightEntry<DemoPojo>> highlighted = hl.getHighlighted();
        for(HighlightEntry<DemoPojo> hle : highlighted){
            List<HighlightEntry.Highlight> list = hle.getHighlights();
            DemoPojo dp = hle.getEntity();
            for (HighlightEntry.Highlight h : list){//一个对象里面可能多个属性是高亮属性
                if(h.getField().getName().equals("item_title")){
                    dp.setItem_title( h.getSnipplets().get(0));
                }
            }
            listResult.add(dp);
        }
        System.out.println(listResult);
    }
}
