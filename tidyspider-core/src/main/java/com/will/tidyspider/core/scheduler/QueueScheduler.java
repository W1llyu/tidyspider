package com.will.tidyspider.core.scheduler;

import com.will.tidyspider.core.entity.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by shangru_yu on 2016/9/14.
 */
public class QueueScheduler implements Scheduler {
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    @Override
    public void push(Request request){
        queue.add(request);
    }

    @Override
    public Request poll(){
        return queue.poll();
    }
}
