package com.devilhan.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by hanyanjiao on 17:58 solr java api test
 */
public class SolrTest {

  private static final String SOLR_URL = "http://192.168.93.10:8983/solr/testcore";

  public void addOrUpdate() throws IOException {
    HttpSolrClient solrClient = null;
    try {
      solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
      SolrInputDocument inputDocument = new SolrInputDocument();
      inputDocument.addField("id", "3");
      inputDocument.addField("myfield", "myfield3");
      solrClient.add(inputDocument);
      solrClient.commit();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      solrClient.close();
    }
  }

  public void delete() throws IOException {
    HttpSolrClient solrClient = null;
    try {
      solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
      solrClient.deleteById("3");
      solrClient.commit();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      solrClient.close();
    }
  }

  public void query() throws IOException {

    HttpSolrClient solrClient = null;
    try {
      solrClient = new HttpSolrClient.Builder(SOLR_URL).build();

      //封装了所有查询条件
      SolrQuery params = new SolrQuery();
//      params.setQuery("*:*");
      params.setQuery("name:丰富的");
      //排序
      params.setSort("price", SolrQuery.ORDER.desc);
      //分页
      params.setStart(0); //不是页数，而是起始条数
      params.setRows(1);
      //高亮
      params.setHighlight(true);
      params.addHighlightField("name");
      params.setHighlightSimplePre("<span>");
      params.setHighlightSimplePost("</span>");

      QueryResponse response = solrClient.query(params);
      SolrDocumentList list = response.getResults();
      System.out.println("总条数：" + list.getNumFound());

      //高亮数据
      Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

      for (SolrDocument doc : list) {
        System.out.println(doc.get("id"));
        Map<String, List<String>> map = highlighting.get(doc.get("id"));
        List<String> HLList = map.get("name");
        if (HLList != null && HLList.size() > 0) {//显示高亮数据
          System.out.println(HLList.get(0));
        } else {
          System.out.println(doc.get("name"));
        }
        System.out.println(doc.get("price"));
        System.out.println("===================");
      }
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      solrClient.close();
    }
  }
}