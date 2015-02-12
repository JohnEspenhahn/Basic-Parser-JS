package com.hahn.basic.viewer;

class ViewerUpdateThread implements Runnable {
    private static final int FPS = 10;
    
    Viewer viewer;
    
    public ViewerUpdateThread(Viewer view) {
        this.viewer = view;
    }

    @Override
    public void run() {
        while (true) {
            long start = System.currentTimeMillis();
            
            if (viewer.needsUpdate()) {
                viewer.update();
            }
            
            try {
                long end = System.currentTimeMillis(),
                        sleepTime = (long) ((1000.0 / FPS) - (end - start));

                if (sleepTime > 0) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
