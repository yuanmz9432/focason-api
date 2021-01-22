package api.lemonico.model;

import lombok.Data;

@Data
public class FieldErrorInfo {

    /** フィールド名 */
    String field;

    /** エラー種別 */
    String type;
}
