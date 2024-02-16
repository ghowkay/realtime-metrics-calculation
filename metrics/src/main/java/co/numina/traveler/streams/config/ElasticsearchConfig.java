package co.numina.traveler.streams.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticsearchConfig {
	
	  private final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

      private String elasticsearchHost = System.getenv("ELASTICSEARCH_HOST");
    
      private int port = Integer.parseInt(System.getenv("ELASTICSEARCH_PORT"));
      
      private String type = "http";


      private static RestHighLevelClient client;
  
  

    public RestHighLevelClient esClient() {

    	RestClientBuilder builder = RestClient.builder(
    		    new HttpHost(elasticsearchHost, port,type))
    		    .setRequestConfigCallback(
    		        new RestClientBuilder.RequestConfigCallback() {
    		            @Override
    		            public RequestConfig.Builder customizeRequestConfig(
    		                    RequestConfig.Builder requestConfigBuilder) {
    		                return requestConfigBuilder
    		                    .setConnectTimeout(5000)
    		                    .setSocketTimeout(60000);
    		            }
    		        }).setHttpClientConfigCallback(new HttpClientConfigCallback() {
    		            @Override
    		            public HttpAsyncClientBuilder customizeHttpClient(
    		                    HttpAsyncClientBuilder httpClientBuilder) {
    		                return httpClientBuilder.setDefaultIOReactorConfig(
    		                    IOReactorConfig.custom()
    		                        .setIoThreadCount(100)
    		                        .build());
    		            }
    		        })
    		    .setMaxRetryTimeoutMillis(60000);
    	
                if (client == null) {
         client =   new RestHighLevelClient(builder);
         System.out.println("=====Elasticsearch new connection =====");
                }else{
                    System.out.println("=====Elasticsearch connection exists=====");
                }
                
		
		System.out.println(client);
    	
    	
    	
    	
    	return client;

    }


private static synchronized void closeConnection() throws IOException {
    // Closing the client connection with .close()
    client.close();
    client = null;
    }
    
    
    
    public BulkProcessor bulkProcessor() {
    	
    	
    return BulkProcessor.builder(
                 (request, bulkListener) -> esClient().bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                 new BulkProcessor.Listener() {
                     @Override
                     public void beforeBulk(long executionId, BulkRequest request) {
                     	
                     	request.timeout(TimeValue.timeValueMinutes(2)); 
                     	request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            
                     	
                         logger.info("going to execute bulk of {} requests "+request.timeout(), request.numberOfActions());
                     }

                     @Override
                     public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                   
                         logger.info("bulk executed {} failures", response.hasFailures() ? "with" : "without");
                         
                         if (response.hasFailures()) {
                             logger.error(response.buildFailureMessage());
                            
                             
                             for (BulkItemResponse bulkItemResponse : response) {
                             	 logger.error( bulkItemResponse.getId() + " ：" + bulkItemResponse.getFailureMessage());
                               
                             }
                         }
                     }

                     @Override
                     public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                     	
                     	failure.printStackTrace();
                         logger.info("error while executing bulk" + failure.getMessage());
                     }
                 })
    		   .setBulkActions(10000)
    	        .setFlushInterval(TimeValue.timeValueSeconds(30)) 
		        .setBulkSize(new ByteSizeValue(50, ByteSizeUnit.MB))
		        .setConcurrentRequests(1) 
		        .build();
         
    	
    	
    	
    	
    }
    


}