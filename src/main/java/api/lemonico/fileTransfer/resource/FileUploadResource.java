/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.fileTransfer.resource;



import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * フィアルアップロードリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class FileUploadResource
{

    /** アップロードURL */
    private final String uploadUrl;
}
