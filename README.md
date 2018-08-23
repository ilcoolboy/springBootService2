#利用activeMQ做分布式事务处理
###主要思路：把本地事务和消息发送绑定起来：即本地事务成功，消息发送成功；本地事务失败 消息发送失败。
###实现：
* 1消息生产者即发送端设置消息发送为setSessionTransacted(true) 启用事务， 即本地事务和消息发送绑定
* 2消息发送成功会出现 无人消费 或者 消费失败两种情况: 
    * 1）消息消费失败：消息服务器会根据配置的最大重试次数，多次投递消息给队列监听者。如果到达最大重试次数后消息还是消费失败，那么此条消息会进入死信队列。（本文最后介绍了进入死信队列机制。默认所有的队列都会进入同一个死信队列 比如队列1 和 队列2 如果消费失败了都会默认进入ActiveMQ.DLQ队列，但是这个不利于消息管理，所以activeMQ 提供了设置死信队列的配置 （本文最后介绍了配置死信队列的方法）
    * 2）无人消费消息: 如果无人消费消息那么也会造成数据一致性问题， activeMQ提供了配置解决：
		打开服务质量的开关
		jmsTemplate.setExplicitQosEnabled(true);
		// 设置消息有效时间为10秒  （设置消息有效时间为10秒之后，如果10秒之后还无人消费消息 消息自动进入死信队列）
		jmsTemplate.setTimeToLive(1000*10);
* 3 死信队列处理：消息消费失败或者无人消费都会进入死信队列,那么对死信队列进行处理就可以保证一致性，由人工处理重新发送消息或者监听死信队列（监听死信队列一旦消费失败 那么死信队列是不会再次进入死信队列的 即这条消息完全丢失 所以这里不推荐这种方式） 

###优势 ：可以将串行的事务变成并行执行，即本地事务执行，把待处理的数据发送到消息队列就返回，不必像以前要等待远程调用结束。可以极大降低方法执行时间、提高响应，增强用户体验。
###劣势：
* 1 在不做编程补偿的情况下，一旦消息丢失， 那么会造成数据不一致。
* 2 因为消息有可能会被重复投递，所以消费端接口要具有幂等性、即一次调用跟多次调用结果相同。
* 3 在没有发生异常的情况下 消费者生产者的数据会找短时间内不一致，因为生产者的数据已经提交了，消费者消费数据需要时间（这取决于消息服务器的性能和消息堆积情况和消费者的业务复杂度）

###步骤
* 1 开启本地事务，操作DB 
* 2 发送消息
* 3 结束本地事务，发送消息成功，本地事务结束
* 4 消息服务器投递消息给消费者
* 5 消费者消费落地消息（存入消费者端的数据库中）

###数据不一致的若干种情况 （这里消费者要提供一个查询功能用于生产者来校验消息是否被成功消费）
* 1 生产者本地事务结束发送消息的时候 消息服务器接受消息失败 （网络中断或者消息服务器宕机） 此时本地事务成功 消息服务器没有接收到消息 
* 2 生产者本地事务结束发送消息成功 消费者消费消息失败
* 3 生产者本地事务结束发送消息成功 消费者消费成功 告知生产者时失败

###解决不一致的方案：
* 1 在生产端增加消息表，即生产者进行本地业务数据保存，发送消息，消息存在本地数据库中 （消息要有若干种状态 ）
* 2 增加一个定时任务轮询消息表（定时周期取决于业务允许的数据不一致的时间）
* 3 如果是第（1）种数据不一致的情况， 那么将消息发送到对应队列中；如果是第（2）种不一致，要么回滚生产者的数据 ，要么通知运维人员查询为什么消费失败；如果是第（3）种不一致，那么更新生产者数据状态 改成操作完成。

#名次解释及配置说明：

####什么是死信队列：
DLQ-死信队列(Dead Letter Queue)用来保存处理失败或者过期的消息。
出现以下情况时，消息会被redelivered
 A transacted session is used and rollback() is called.
 A transacted session is closed before commit is called.
 A session is using CLIENT_ACKNOWLEDGE and Session.recover() is called.
当一个消息被redelivered超过maximumRedeliveries(缺省为6次，具体设置请参考后面的链接)次数时，会给broker发送一个"Poison ack"，这个消息被认为是a poison pill，这时broker会将这个消息发送到DLQ，以便后续处理。
缺省的死信队列是ActiveMQ.DLQ，如果没有特别指定，死信都会被发送到这个队列。
缺省持久消息过期，会被送到DLQ，非持久消息不会送到DLQ

####什么是应答机制（ack机制）：
ABC

####修改默认的死信队列配置方法：
activeMQ 提供了设置死信队列的配置 找到activeMQ的配置文件， 路径  apache-activemq-5.15.3/conf/activemq.xml  打开修改activemq.xml 找到destinationPolicy 在policyEntries下加入以下配置） 

	 <destinationPolicy>
            <policyMap>
              <policyEntries>
                <policyEntry topic=">" >
                    <!-- The constantPendingMessageLimitStrategy is used to prevent
                         slow topic consumers to block producers and affect other consumers
                         by limiting the number of messages that are retained
                         For more information, see:

                         http://activemq.apache.org/slow-consumer-handling.html
						主题死信队列配置 这里主要用队列 所以不处理
                    -->
                  <pendingMessageLimitStrategy>
                    <constantPendingMessageLimitStrategy limit="1000"/>
                  </pendingMessageLimitStrategy>
                  <!--
                  <deadLetterStrategy>  
				          
				            <individualDeadLetterStrategy  
				              queuePrefix="DLQ." useQueueForQueueMessages="true" />  
				          </deadLetterStrategy>  
				          -->
                </policyEntry>
                
                
                <!--此处为配置队列的死信队列  如果不配置 那么所有队列消费失败都会进入ActiveMQ.DLQ， 下面这段配置的意思是消费失败会进入DLQ.队列名    即如果队列名为 queue1、queue2 的队列消费失败并且设置了此队列带事务 那么会分别进入 名称为“DLQ.queue1、DLQ.queue2”的死信队列   --->
                <policyEntry queue=">" >
                    <!-- The constantPendingMessageLimitStrategy is used to prevent
                         slow topic consumers to block producers and affect other consumers
                         by limiting the number of messages that are retained
                         For more information, see:

                         http://activemq.apache.org/slow-consumer-handling.html

                    -->
				            <!--  
				              Use the prefix 'DLQ.' for the destination name, and make  
				              the DLQ a queue rather than a topic  
				            -->  
				            <deadLetterStrategy>  
					            <individualDeadLetterStrategy  
					              queuePrefix="DLQ." useQueueForQueueMessages="true" />  
					          </deadLetterStrategy>  
                </policyEntry>
              </policyEntries>
            </policyMap>
        </destinationPolicy>


