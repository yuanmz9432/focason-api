package api.lemonico.controller;



import api.lemonico.resource.Base64Resource;
import api.lemonico.service.Base64Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ツールコントローラー
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Base64Controller
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/base64";

    /**
     * エンコードURI
     */
    private static final String ENCODER_UTI = COLLECTION_RESOURCE_URI + "/encoder";

    /**
     * ディコードURI
     */
    private static final String DECODER_UTI = COLLECTION_RESOURCE_URI + "/decoder";

    /**
     * ツールサービス
     */
    private final Base64Service service;

    /**
     * エンコードハンドラー
     *
     * @return ユーザーリソース一覧取得APIレスポンス
     */
    @PostMapping(ENCODER_UTI)
    public ResponseEntity<Base64Resource> encodeHandler(@RequestBody Base64Resource resource) {
        return ResponseEntity.ok(service.encode(resource));
    }

    /**
     * ディコードハンドラー
     *
     * @return ユーザーリソース一覧取得APIレスポンス
     */
    @PostMapping(DECODER_UTI)
    public ResponseEntity<Base64Resource> decodeHandler(@RequestBody Base64Resource resource) {
        return ResponseEntity.ok(service.decode(resource));
    }
}
