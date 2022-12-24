package com.lemonico.core.utils;



import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 接口并发测试类
 *
 * @since 1.0.0
 */
public class ConcurrentTesting
{

    private static final int THREAD_NUM = 200;

    private final CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);

    public static void main(String[] args) {
        ConcurrentTesting concurrentTesting = new ConcurrentTesting();
        concurrentTesting.runThread();
    }

    public void runThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < THREAD_NUM; i++) {
            executorService.submit(buildThread());
        }
    }

    public Thread buildThread() {
        return new Thread(() -> {
            countDownLatch.countDown();
            try {
                System.out.println("线程：" + Thread.currentThread().getName() + "准备");
                countDownLatch.await();
                Object o = executionURL("http://127.0.0.1:8083/api/store/shipment/test", "get");

                System.err.println("线程名" + Thread.currentThread().getName());
                System.err.println("结果=" + o);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public Object executionURL(String url, String method) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000)
            .setConnectTimeout(5000).build();

        CloseableHttpClient client = HttpClients.createDefault();
        String result;
        if (!StringTools.isNullOrEmpty(url)) {
            if ("post".equals(method) || "get".equals(method)) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setConfig(requestConfig);
                try {
                    CloseableHttpResponse response = client.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity, "utf-8");
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("请求失败");
            }
        } else {
            System.err.println("url为null");
        }
        return null;
    }
}
