package org.cmaaio.httputil;

public interface IResult {
	public void OnResult(String jsonStr);
	public void OnFail(String errorMsg);
	public void OnCacnel();
}
