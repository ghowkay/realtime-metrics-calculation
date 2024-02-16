# realtime metrics using kakfa

### About

This application computes and displays near real-time metrics about travelers (pedestrians, bicyclists, and vehicles) moving within a city. The movement events - travelers moving through a public space. The part of the Application that computes and stores `metrics` is written in Java and uses Kafka Streams API .
Kibana is used for visualisation while Elasticsearch is used to store the computed metrics



### Installation and Setup

To get this project up and running on your local machine you need to have `docker` installed


```sh
$ cd project folder
$ docker-compose -f "docker-compose.yml" up -d
```

### Tech

This project uses a number of open source projects to work properly:

* [Elasticsearch](https://www.elastic.co/) - Distributed search engine for fast analytics on schema free JSON documents
* [Kibana](https://www.elastic.co/kibana) - data visualisation tool for Elasticsearch
* [Kafka](https://kafka.apache.org/) - Distrubuted stream processing system


### Visualize

Click link below to visualize data on kibana


<http://localhost:5601/app/kibana#/dashboard/7565d620-ce82-11ea-b77c-715ba60f88bf?_g=(refreshInterval:(pause:!f,value:60000),time:(from:now-15m,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!t,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:7,i:'1',w:48,x:0,y:0),id:'585b7300-ce82-11ea-b77c-715ba60f88bf',panelIndex:'1',type:visualization,version:'6.8.0'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:7),id:cb6b95b0-ce81-11ea-b77c-715ba60f88bf,panelIndex:'2',type:visualization,version:'6.8.0'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:7),id:'2a23c8c0-ce82-11ea-b77c-715ba60f88bf',panelIndex:'3',type:visualization,version:'6.8.0')),query:(language:lucene,query:''),timeRestore:!t,title:'Numina%20traveler%20metrics',viewMode:view)>