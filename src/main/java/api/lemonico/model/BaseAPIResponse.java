package api.lemonico.model;

import api.lemonico.enums.ResponseCode;
import lombok.Data;

@Data
public class BaseAPIResponse {

	private static final long serialVersionUID = -1832122505105038646L;

	private Integer code;

	private String message;

	private Object data;

	public BaseAPIResponse() {
	}

	/**
	 * 正常処理
	 * @return
	 */
	public static BaseAPIResponse success() {
		BaseAPIResponse baseResponse = new BaseAPIResponse();
		baseResponse.setResultCode(ResponseCode.SUCCESS);
		return baseResponse;
	}

	public static BaseAPIResponse success(Object data) {
		BaseAPIResponse BaseResponse = new BaseAPIResponse();
		BaseResponse.setResultCode(ResponseCode.SUCCESS);
		BaseResponse.setData(data);
		return BaseResponse;
	}

	public static BaseAPIResponse success(ResponseCode responseCode, Object data) {
		BaseAPIResponse BaseResponse = new BaseAPIResponse();
		BaseResponse.setResultCode(responseCode);
		BaseResponse.setData(data);
		return BaseResponse;
	}

	public static BaseAPIResponse failure(ResponseCode responseCode) {
		BaseAPIResponse BaseResponse = new BaseAPIResponse();
		BaseResponse.setResultCode(responseCode);
		return BaseResponse;
	}

	public void setResultCode(ResponseCode resultCode) {
		this.code = resultCode.getValue();
		this.message = resultCode.getLabel();
	}
}
