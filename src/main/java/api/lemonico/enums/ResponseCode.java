package api.lemonico.enums;

public enum ResponseCode implements CodeEnum<Integer>{

	// 成功「2XX」
	SUCCESS(200, "成功に実行しました。"),
	// アカウント「6XX」
	ACCOUNT_IS_EXISTED(600, "アカウントも登録されましたので、ログインしてください。"),
	ACCOUNT_IS_NOT_EXIST(601, "アカウントが存在しない。"),
	;

	ResponseCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	private Integer code;

	private String message;

	public Integer getValue() {
		return code;
	}

	public String getLabel() {
		return message;
	}

	public static ResponseCode of(Integer code) {
		return CodeEnum.of(ResponseCode.class, code);
	}
}
