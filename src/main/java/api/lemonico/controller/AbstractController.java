package api.lemonico.controller;



import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.exception.LcValidationErrorException;
import api.lemonico.repository.StoreRepository;
import api.lemonico.service.StoreService;
import api.lemonico.service.UserService;
import java.util.HashSet;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public abstract class AbstractController
{

    /**
     * ユーザーサービス
     */
    protected final StoreService storeService;
    /**
     * ユーザーサービス
     */
    protected final UserService service;

    protected boolean hasPermission(String storeCode, String silver) {
        if (StringUtils.isNullOrEmpty(storeCode)) {
            throw new RuntimeException();
        }
        storeCode =
            Optional.of(storeCode).orElseThrow(() -> new LcValidationErrorException("Store code can not be found."));
        var storeCodes = new HashSet<String>();
        storeCodes.add(storeCode);
        var stores = storeService.getResourceList(
            StoreRepository.Condition.builder().storeCodes(storeCodes).build(),
            LcPagination.DEFAULT,
            StoreRepository.Sort.DEFAULT);
        if (stores.getCount() > 0) {
            return stores.getData().get(0).getStoreName().equals("STA001");
        }
        return false;
    }
}
