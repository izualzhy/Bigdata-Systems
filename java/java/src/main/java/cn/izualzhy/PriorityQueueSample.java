package cn.izualzhy;

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityQueueSample {
    static class TaskPriority implements Comparable<TaskPriority> {
        private int priority;
        private int instanceId;

        TaskPriority(int priority, int instanceId) {
           this.priority = priority;
           this.instanceId = instanceId;
        }

        @Override
        public int compareTo(TaskPriority o) {
            if (this.priority > o.priority) {
                return -1;
            } else if (this.priority < o.priority) {
                return 1;
            }

            if (this.instanceId < o.instanceId) {
                return -1;
            } else if (this.instanceId > o.instanceId) {
                return 1;
            }

            return 0;
        }

        @Override
        public String toString() {
            return "TaskPriority{" +
                    "priority=" + priority +
                    ", instanceId=" + instanceId +
                    '}';
        }
    }

    public static void main(String[] args) {
        PriorityBlockingQueue<TaskPriority> q = new PriorityBlockingQueue<>(10);

        for (int i = 1; i < 10; i++) {
            for (int j = 10; j >= 0; j--) {
                q.offer(new TaskPriority(i, j));
            }
        }

        while (!q.isEmpty()) {
            TaskPriority taskPriority = q.poll();
            System.out.println(taskPriority);
        }

    }
}
