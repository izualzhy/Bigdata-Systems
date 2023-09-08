package cn.izualzhy;

public class FinalizeEscapeGC {
    static FinalizeEscapeGC SAVE_HOOK = null;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize");

        // IMPORTANT
        FinalizeEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeEscapeGC();

        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(2000);
        System.out.println("after 1st gc, SAVE_HOOK:" + SAVE_HOOK);

        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(2000);
        System.out.println("after 2nd gc, SAVE_HOOK:" + SAVE_HOOK);
    }
}
