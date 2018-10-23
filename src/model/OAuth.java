package model;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth接口，目前未用。便于后期管理验证模块
 */
public interface OAuth {
    void RefreshAccessToken() throws InterruptedException, ExecutionException, IOException;
}
