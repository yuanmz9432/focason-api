package api.lemonico.service;

import api.lemonico.attribute.LcPagination;
import api.lemonico.attribute.LcResultSet;
import api.lemonico.entity.Customer;
import api.lemonico.repository.CustomerRepository;
import api.lemonico.resource.CustomerResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * BOユーザサービス
 *
 * @since 1.1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerService {
    /**
     * BOユーザリポジトリ
     */
    private final CustomerRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、BOユーザリソースの一覧を取得します。
     *
     * @param condition  検索条件
     * @param pagination ページングパラメータ
     * @param sort       ソートパラメータ
     * @return BOユーザリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<CustomerResource> getResourceList(
            CustomerRepository.Condition condition,
            LcPagination pagination,
            CustomerRepository.Sort sort) {
        // BOユーザの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // BOユーザエンティティのリストをBOユーザリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

//    /**
//     * BOユーザIDを指定して、BOユーザを取得します。
//     *
//     * @param id BOユーザID
//     * @return BOユーザリソース
//     */
//    @Transactional(readOnly = true)
//    public Optional<CustomerResource> getResource(ID<Customer> id) {
//        // BOユーザを取得します。
//        return repository.findById(id).map(this::convertEntityToResource);
//    }
//
//    /**
//     * BOユーザを作成します。
//     *
//     * @param resource BOユーザリソース
//     * @return 作成されたBOユーザリソース
//     */
//    @Transactional
//    public CustomerResource createResource(CustomerResource resource) {
//        // TODO Send email function
//        // メールにおいて重複したデータが存在していることを示す。
//        if (StringUtils.hasText(resource.getEmail()) && repository.existsEmail(null, resource.getEmail())) {
//            throw new MitsuifudosanMutexLockFailedException(resource.getEmail() + " already exists");
//        }
//
//        // BOユーザを作成します。
//        var id = repository.create(resource.toEntity());
//
//        // BOユーザを取得します。
//        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
//    }
//
//    /**
//     * BOユーザIDを指定して、BOユーザを更新します。
//     *
//     * @param id BOユーザID
//     * @param resource BOユーザリソース
//     * @return 更新後のBOユーザリソース
//     */
//    @Transactional
//    public CustomerResource updateResource(ID<Customer> id, CustomerResource resource) {
//        // TODO Waiting for finalization of basic design according to Q&A
//        // BOユーザIDにおいて重複したデータが存在していることを示す。
//        if (!repository.exists(id)) {
//            throw new LcResourceNotFoundException(Customer.class, id);
//        }
//
//        // メールにおいて重複したデータが存在していることを示す。
//        if (StringUtils.hasText(resource.getEmail()) && repository.existsEmail(id, resource.getEmail())) {
//            throw new MitsuifudosanMutexLockFailedException(resource.getEmail() + " already exists");
//        }
//
//        // BOユーザを更新します。
//        repository.update(id, resource.toEntity());
//
//        // BOユーザを取得します。
//        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
//    }
//
//    /**
//     * BOユーザIDを指定して、BOユーザを削除します。
//     *
//     * @param id BOユーザID
//     */
//    @Transactional
//    public void deleteResource(ID<Customer> id) {
//        // TODO Waiting for finalization of basic design according to Q&A
//        // BOユーザIDにおいて重複したデータが存在していることを示す。
//        if (!repository.exists(id)) {
//            throw new LcResourceNotFoundException(Customer.class, id);
//        }
//
//        // BOユーザを削除します。
//        repository.deleteLogicById(id);
//    }
//
    /**
     * BOユーザエンティティをBOユーザリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public CustomerResource convertEntityToResource(Customer entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * BOユーザエンティティのリストをBOユーザリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<CustomerResource> convertEntitiesToResources(List<Customer> entities) {
        return entities.stream()
                .map(CustomerResource::new)
                .collect(toList());
    }
}