package server.queues;

import server.models.Player;

import java.util.Iterator;

public class QueueTimeoutChecker implements Runnable {
    private final GameQueue queue;
    private final GameQueue lowerQueue;
    private final long TIMEOUT_DURATION_MS;


    public QueueTimeoutChecker(GameQueue queue, GameQueue lowerQueue, Integer TIMEOUT_MINUTES) {
        this.queue = queue;
        this.lowerQueue = lowerQueue;
        this.TIMEOUT_DURATION_MS = TIMEOUT_MINUTES * 60 * 1000;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long SLEEP_DURATION_MS = 30 * 1000; // 30 seconds
                Thread.sleep(SLEEP_DURATION_MS);
            } catch (InterruptedException e) {
                //TODO: handle exception
            }
            long currentTime = System.currentTimeMillis();
            synchronized (queue) {
                Iterator<Player> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if (currentTime - player.getUser().getQueueJoinTime() > TIMEOUT_DURATION_MS) {
                        iterator.remove();
                        lowerQueue.add(player, player.getUser().getQueueJoinTime());
                        System.out.println("Moved user " + player.getUser().getUsername() + " to lower queue.");
                    }
                }
            }
        }
    }
}
