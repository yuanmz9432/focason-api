package api.lemonico.file.controller;



import api.lemonico.core.attribute.ID;
import api.lemonico.file.resource.FileDownloadResource;
import api.lemonico.file.service.FileDownloadService;
import api.lemonico.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * フィアルエクスポートコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileDownloadController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/files";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    private final FileDownloadService service;

    /**
     * ファイルIDを指定して、ファイルダウンロード用の証明付きURL取得
     *
     * @param id ファイルID
     * @return ファイルダウンロード用の証明付きURL
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<FileDownloadResource> getFileDownloadUrl(
        @PathVariable("id") ID<User> id) {

        return ResponseEntity.ok(service.getDownloadUrl());
    }

}
