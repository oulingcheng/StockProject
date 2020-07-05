package com.olc.ejdemo.util;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * @author Oulingcheng
 * @Date 2020/7/4
 */
public class JobUtil {

    public static int execute(ShardingContext shardingContext) {
        String jobName = shardingContext.getJobName();
        int shardingTotalCount = shardingContext.getShardingTotalCount();
        int shardingItem = shardingContext.getShardingItem();
        String shardingParameter = shardingContext.getShardingParameter();
        String jobParameter = shardingContext.getJobParameter();
        String taskId = shardingContext.getTaskId();
        System.out.println("任务名称：" + jobName);
        System.out.println("分片总数：" + shardingTotalCount);
        System.out.println("分片项：" + shardingItem);
        System.out.println("分片项参数：" + shardingParameter);
        System.out.println("任务参数：" + jobParameter);
        System.out.println("任务id：" + taskId);
        return shardingItem;
    }

}
