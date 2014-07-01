package com.example.huanyingxiangji1.gif;

public interface GifAction {

	/**
	 * gif解码观察�?
	 * @param parseStatus 解码是否成功，成功会为true
	 * @param frameIndex 当前解码的第几帧，当全部解码成功后，这里�?1
	 */
	public void parseOk(boolean parseStatus, int frameIndex);
}
