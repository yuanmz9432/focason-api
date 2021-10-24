package api.lemonico.file.controller;



import api.lemonico.core.attribute.ID;
import api.lemonico.user.entity.User;
import api.lemonico.user.resource.UserResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ファイルインポートコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileImportController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/files/import";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ファイル名を指定して、ファイルダウンロード用の証明付きURL取得
     *
     * @return ユーザーリソース取得APIレスポンス
     */
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<UserResource> getFileUploadUrl() {
        return null;
    }

    /**
     * ファイル名を指定して、ファイルダウンロード用の証明付きURL取得
     *
     * @param fileName ユーザーID
     * @return ユーザーリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserResource> getFileDownloadUrl(
        @PathVariable("id") ID<User> fileName) {
        return null;
    }

}
