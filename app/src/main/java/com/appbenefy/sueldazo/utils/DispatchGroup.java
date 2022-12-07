package com.appbenefy.sueldazo.utils;

public class DispatchGroup {
    private int count;
    private Runnable runnable;

    public DispatchGroup() {
        super();
        count = 0;
    }

    public synchronized void enter(){
        count++;
    }

    public synchronized void leave(){
        count--;
        notifyGroup();
    }

    public void notify(Runnable r) {
        runnable = r;
        notifyGroup();
    }

    private void notifyGroup(){
        if (count <=0 && runnable!=null) {
            runnable.run();
        }
    }
}
