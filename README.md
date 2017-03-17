# Mrpc

Mrpc是使用Java实现的RPC框架，主要的特点包括：

* 极易使用,只需要添加一个注释，并在xml文件中做一下配置即可完成服务器的适配工作
* 负载均衡及高可用，当有多个业务实现服务器在线时客户端会自动进行负载均衡，如果一个服务器掉线，客户端也会选择其他的服务器进行调用。

Mrpc框架使用的主要技术包括：

* Spring Framework
* ZooKeeper
* 动态代理
* netty

## Sample

具体的可以运行Mrpc-sample-server和Mrpc-sample-client来查看效果。需要先搭建好ZooKeeper集群。

## 后续改进

后续可以继续学习Dubbo添加如按接口来划分服务器的功能。
