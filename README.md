# wjt_netty
学习netty源码

1,

项目内要删除webapp目录,用WebApplicationInitializer的实现类启动web服务;

https://github.com/wjt2015/wjt_netty

https://github.com/waylau/netty-4-user-guide-demos
https://github.com/waylau/netty-4-user-guide
https://github.com/code4craft/netty-learning
https://github.com/jwpttcg66/NettyGameServer


mvn jetty:run -Pfrontend


2,研究SSM的启动流程源码

3,单元测试  

mvn执行单元测试的控制台命令格式:  

####执行一个测试类内的全部测试用例:
mvn test -Dtest=<测试类的完整名字>

####执行一个测试类内的指定的测试方法:
mvn test -Dtest=<测试类的完整名字>#<测试方法>

实例:   
mvn clean test -Dtest=wjt.netty.a.DiscardClientTest#run 

mvn clean test -Dtest=wjt.netty.a.DiscardServerTest#run 



 





