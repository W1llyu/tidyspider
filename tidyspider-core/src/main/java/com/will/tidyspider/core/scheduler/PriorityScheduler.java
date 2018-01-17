package com.will.tidyspider.core.scheduler;

import com.will.tidyspider.core.entity.Request;
import com.will.tidyspider.core.utils.NumberUtil;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by shangru_yu.
 */
public class PriorityScheduler implements Scheduler {

    private static final int INITIAL_CAPACITY = 5;

    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<>();

    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            return -NumberUtil.compareLong(o1.getPriority().getValue(), o2.getPriority().getValue());
        }
    });

    private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            return -NumberUtil.compareLong(o1.getPriority().getValue(), o2.getPriority().getValue());
        }
    });

    @Override
    public void push(Request request){
        if (request.getPriority().getValue() == 1) {
            noPriorityQueue.add(request);
        } else if (request.getPriority().getValue() > 1) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    @Override
    public Request poll(){
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }
}
