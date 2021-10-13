<#-- このテンプレートに対応するデータモデルのクラスは org.seasar.doma.extension.gen.EntityDesc です -->
<#import "lib.ftl" as lib>
/*
<#if lib.copyright??>
 * ${lib.copyright}
</#if>
 */
<#if packageName??>
package ${packageName};
</#if>

/**
 * ${comment}サービス
 *
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${simpleName}${entitySuffix}
{

    /**
     * ${comment}リポジトリ
     */
    private final ${simpleName}Repository repository;

    /**
    　* 検索条件・ページングパラメータ・ソート条件を指定して、${comment}リソースの一覧を取得します。
    　*
    　* @param condition 検索条件
    　* @param pagination ページングパラメータ
    　* @param sort ソートパラメータ
    　* @return ${comment}リソースの結果セットが返されます。
    　*/
    @Transactional(readOnly = true)
    public LcResultSet<${simpleName}Resource> getResourceList(
        Condition condition,
        LcPagination pagination,
        Sort sort) {
        // ${comment}の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ${comment}エンティティのリストを${comment}リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
    * ${comment}IDを指定して、${comment}を取得します。
    *
    * @param id ${comment}ID
    * @return ${comment}リソース
    */
    @Transactional(readOnly = true)
    public Optional<${simpleName}Resource> getResource(ID<${simpleName}> id) {
            // BOユーザを取得します。
            return repository.findById(id).map(this::convertEntityToResource);
            }

}
