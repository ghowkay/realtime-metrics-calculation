package co.numina.traveler.streams.libraries;

import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkProcessor;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;

import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class Elasticsearch {
    private final Logger logger = LoggerFactory.getLogger(Elasticsearch.class);
    
    

  private  RestHighLevelClient esClient;
    
    
    
  private BulkProcessor bulkProcessor;
    
	
    
    public Elasticsearch(RestHighLevelClient esClient, BulkProcessor bulkProcessor){

        this.esClient = esClient;
        this.bulkProcessor = bulkProcessor;

    }
    
    public RestHighLevelClient getClient() {
    	
    	return esClient;
    	
    }
   
    public void delete(String id, String index , String type) {
        bulkProcessor.add(new DeleteRequest(index, type, id));
    }
    
    
    
    
    public SearchResponse searchScroll(String scrollId) throws IOException {
    	
    	
    return	esClient.scroll( new SearchScrollRequest()
    		.scrollId(scrollId)  
    		.scroll(new TimeValue(600000))
    		, RequestOptions.DEFAULT);
    }
    

    public SearchResponse search(QueryBuilder query, Integer size,String index,String type) throws IOException {
        logger.debug("elasticsearch query: {}", query.toString());
        SearchResponse response = esClient.search(new SearchRequest(index)

        		.types(type)
        		.searchType(SearchType.DEFAULT)
        		.scroll(new TimeValue(60000))
                .source(new SearchSourceBuilder()
                        .query(query)
                         .size(size)
    
                ), RequestOptions.DEFAULT);

        logger.debug("elasticsearch response: {} hits", response.getHits().getTotalHits());
        logger.trace("elasticsearch response: {} hits", response.toString());

        return response;
    }
    
    public boolean checkIfIndexExists(String indexName) throws IOException {
        Response response = esClient.getLowLevelClient().performRequest("HEAD", "/" + indexName);
        int statusCode = response.getStatusLine().getStatusCode(); 
        return (statusCode != 404);
}

    
public boolean createMapping(String indexName) throws IOException {
        
    boolean acknowledged = false;
    	try{
    PutMappingRequest request = new PutMappingRequest(indexName);

    request.type("count");
    

        request.source(
    "{\n" +
    "  \"properties\": {\n" +
    "    \"sensorId\": {\n" +
    "      \"type\": \"integer\"\n" +
    "    },\n" +
    "    \"timestamp\": {\n" +
    "      \"type\": \"date\"\n" +
    "    },\n" +
    "    \"count\": {\n" +
    "      \"type\": \"integer\"\n" +
    "    },\n" +
    "    \"path\": {\n" +
    "      \"type\": \"text\"\n" +
    "    }\n" +
    "  }\n" +
    "}", 
    XContentType.JSON);

    
    
    
    AcknowledgedResponse putMappingResponse = esClient.indices().putMapping(request, RequestOptions.DEFAULT);
    
    acknowledged = putMappingResponse.isAcknowledged();


}catch(Exception e ){


}
    
    return acknowledged;
}

    public boolean createIndex(String indexName) throws IOException {
    	
        boolean acknowledged =false;
        try{
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        
    	CreateIndexResponse	createIndexResponse = esClient.indices().create(request, RequestOptions.DEFAULT);
    	
         acknowledged = createIndexResponse.isAcknowledged();
        
    }catch(Exception e ){


    }
    	
    	return acknowledged;
    }
    
    
    public void indexDocument(String index, String type, String id, String source) throws IOException{
    	
    	System.out.println("source"+ source);
    	System.out.println("index"+ index);
    	System.out.println("type"+ type);
    	System.out.println("id"+ id);
    	esClient.index(new IndexRequest(index,type,id).source(source,XContentType.JSON),RequestOptions.DEFAULT);
    	
	}
   
    

	public GetResponse getResponse(String index, String type, String id) throws InterruptedException, ExecutionException, IOException{
		GetRequest request = new GetRequest();
		
		request.id(id);
		request.index(index);
		request.type(type);
				
		GetResponse response	= esClient.get(request,  RequestOptions.DEFAULT);
		return response;
	}
	public boolean exists(String index, String type, String id) throws InterruptedException, ExecutionException, IOException{
		GetRequest request = new GetRequest();
		request.id(id);
		request.index(index);
		request.type(type);
				
		GetResponse response = esClient.get(request,  RequestOptions.DEFAULT);
		return response.isExists();
	}
	
	public void bulkIndexDocument(String index, String type, String id, String source){
		
		
		bulkProcessor.add(new IndexRequest(index,type,id).source(source,XContentType.JSON));
	}
	
 	
	public void bulkUpdateDocument(String index, String type, String id, String source){
		
		
		bulkProcessor.add(new UpdateRequest(index,type,id).doc(source,XContentType.JSON).upsert(new IndexRequest(index,type,id).source(source,XContentType.JSON)));
	}
	 
	

public  void updateDocBulk(String index, String type, String id, String source) throws IOException{
    	
	
	bulkProcessor.add(new UpdateRequest(index, type, id).doc(source,XContentType.JSON), RequestOptions.DEFAULT);
	
   
	}
    
    
    
}