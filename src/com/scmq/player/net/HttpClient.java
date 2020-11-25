package com.scmq.player.net;

import com.scmq.player.io.IOUtil;
import com.scmq.player.util.StringUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Objects;

/**
 * HttpClient类的一个对象,是一个微型HTTP协议的客户端,通过这个对象可以建立HTTP连接,并获得响应内容数据. 提供了两种请求方式,分别是“GET”、“POST”.
 *
 * @author SCMQ
 * @see #get(String url)
 * @see #post(String url, String param)
 * @see #postJSON(String url, String json)
 */
public final class HttpClient {
	/** 接受类型 */
	private static final Header ACCEPT = new Header("accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

	/** 接受语言 */
	private static final Header ACCEPT_LANGUAGE = new Header("accept-language", "zh-CN,zh;q=0.9");

	/** 用户代理 PC浏览器标识 */
	private static final Header USER_AGENT = new Header("user-agent",
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

	/** 用户代理 手机浏览器标识 */
	private static final Header USER_AGENT_MOBILE = new Header("user-agent",
			"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36");

	/** get请求 */
	private static final String GET_METHOD = "GET";

	/** post请求 */
	private static final String POST_METHOD = "POST";

	/** 当默认的请求头不能满足需求时,可以追加请求头到这个集合中 */
	private HashSet<Header> headers = new HashSet<>(6);

	/** HTTP连接 */
	private HttpURLConnection connection;

	/** 可关闭的流 */
	private Closeable closeable;

	/**
	 * 指定用户浏览器标识是手机还是PC,然后通过这个标识和Cookie值来创建一个HttpClient对象 <br>
	 *
	 * @param mobile
	 *            是否是手机浏览器.若为true,则创建手机标识的HttpClient;否则创建PC标识的HttpClient对象
	 * @param cookie
	 *            浏览器cookie值,允许为null
	 */
	private HttpClient(boolean mobile, String cookie) {
		headers.add(ACCEPT);
		headers.add(ACCEPT_LANGUAGE);
		headers.add(mobile ? USER_AGENT_MOBILE : USER_AGENT);
		if (!StringUtil.isEmpty(cookie)) {
			headers.add(new Header("cookie", cookie));
		}
	}

	/**
	 * 创建一个默认的具有PC浏览器标识的HttpClient对象
	 *
	 * @param cookie
	 *            浏览器cookie值,可以为null
	 * @return PC浏览器标识的HttpClient对象
	 */
	public static HttpClient createClient(String cookie) {
		return new HttpClient(false, cookie);
	}

	/**
	 * 创建一个具有手机浏览器标识的HttpClient对象
	 *
	 * @param cookie
	 *            浏览器cookie值,可以为null
	 * @return 手机浏览器标识的HttpClient对象
	 */
	public static HttpClient createMobileClient(String cookie) {
		return new HttpClient(true, cookie);
	}

	/**
	 * 设置此次访问请求的目标地址,需要引用的地址(当前请求地址必须依赖的页面地址). <br>
	 * 必须在发送请求之前调用,否则无效.
	 *
	 * @param uri
	 *            引用的页面地址.
	 * @throws IllegalArgumentException
	 *             如果引用地址为空,将抛出此异常
	 * @see HttpClient#get(String uri)
	 * @see HttpClient#post(String uri, String param)
	 * @see HttpClient#postJSON(String uri, String param)
	 */
	public void setReferer(String uri) {
		if (StringUtil.isEmpty(uri)) {
			throw new IllegalArgumentException("页面地址不能为空！");
		}
		String referer = "referer";
		for (Header header : headers) {
			if (referer.equals(header.name)) {
				header.value = uri;
				return;
			}
		}
		headers.add(new Header(referer, uri));
	}

	/**
	 * 设置此次访问请求的目标地址,需要引用的地址(当前请求地址必须依赖的页面地址).
	 *
	 * @param minCapacity
	 *            内部使用StringBuilder拼接多个字符串的最小长度.
	 * @param sequences
	 *            多个字符串(可变参数/数组).
	 * @throws IllegalArgumentException
	 *             若可变参数texts为null或拼接后的字符串是空串,则抛出此异常.
	 * @throws NegativeArraySizeException
	 *             若参数 minCapacity < 0 将抛出此异常.
	 */
	public void setReferer(int minCapacity, String... sequences) {
		StringBuilder builder = new StringBuilder(minCapacity);
		for (String append : sequences) {
			builder.append(append);
		}
		setReferer(builder.toString());
	}

	/**
	 * 添加请求头信息.必须在发送请求之前调用,否则无效.
	 *
	 * @param key
	 *            请求头的key
	 * @param value
	 *            请求头的value
	 * @throws IllegalArgumentException
	 *             如果请求头信息为空(null或空串),将抛出此异常
	 */
	public void addHeader(String key, String value) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			throw new IllegalArgumentException("请求头信息不能为空！");
		}
		headers.add(new Header(key, value));
	}

	/**
	 * 移除accept请求头. 若有accept请求头,下载的资源会存在问题.例如QQ音乐的图片资源不能在Java程序中显示.
	 *
	 * @return HttpClient对象
	 */
	public HttpClient removeAcceptHeader() {
		headers.remove(ACCEPT);
		headers.remove(ACCEPT_LANGUAGE);
		return this;
	}

	/**
	 * 打开HTTP连接(HttpURLConnection)
	 *
	 * @param url
	 *            URL地址
	 * @param method
	 *            请求方式
	 * @throws IOException
	 *             如果发生意外,将抛出IO异常.例如由于网络不畅,将无法连接远程URL.
	 */
	private void openConnection(String url, String method) throws IOException {
		// 获得URL连接
		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		// 设置请求方式
		connection.setRequestMethod(method);
		// 允许URL连接输入,默认为true
		connection.setDoInput(true);
		// 如果是“POST”请求(不需要使用equals)
		if (POST_METHOD.equals(method)) {
			// 允许通过输出流发送数据
			connection.setDoOutput(true);
			// 关闭缓冲
			connection.setUseCaches(false);
		}
		// 设置请求头
		for (Header header : headers) {
			connection.setRequestProperty(header.name, header.value);
		}
	}

	/**
	 * 通过URL发起一个get请求.URL中的参数必须是已经处理好的字符序列,如“user=xiao&password=123”.
	 * 如果参数中包含中文,则需要转换为MIME字符串.可调用{@link HttpClient#encode(String text, String encoding)}方法处理
	 *
	 * @param url
	 *            URL地址
	 * @return 当前这个HttpClient对象
	 */
	public HttpClient get(String url) {
		try {
			// 打开HTTP连接
			openConnection(url, GET_METHOD);
			// 当遇到某些URL被重定向时(301 或 302), 获取重定向后的地址
			if ((url = connection.getHeaderField("Location")) != null) {
				// 重置连接
				reset();
				// 重新打开连接
				openConnection(url, GET_METHOD);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 通过URL和参数发起一个POST请求,这个参数必须是已经处理好的字符序列,如“user=xiao&password=123”.
	 *
	 * @param url
	 *            URL地址
	 * @param param
	 *            需要提交的参数.可为null.如果参数中包含中文,则需要转换为MIME字符串.可调用{@link HttpClient#encode(String text, String encoding)} 方法处理
	 * @return 当前这个HttpClient对象
	 */
	public HttpClient post(String url, String param) {
		PrintWriter writer = null;
		try {
			// 打开HTTP连接
			openConnection(url, POST_METHOD);
			if (param != null) {
				writer = new PrintWriter(connection.getOutputStream());
				// 准备发送到服务器的数据
				writer.write(param);
				writer.flush();
			}
			// 如果需要发送参数到服务器
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(writer);
		}
		return this;
	}

	/**
	 * 通过URL和JSON数据发起一个POST请求
	 *
	 * @param url
	 *            URL地址
	 * @param json
	 *            需要发送的JSON数据,这个字符序列必须是标准的JSON格式,可为null.
	 * @return 当前这个HttpClient对象
	 */
	public HttpClient postJSON(String url, String json) {
		byte[] data = null;
		boolean handle = !StringUtil.isEmpty(json);
		if (handle) {
			data = json.getBytes(StringUtil.UTF_8);
			// 额外设置请求头“内容类型”(发送JSON数据,必须有这个请求头)
			headers.add(new Header("content-type", "application/json;charset=UTF-8"));
			headers.add(new Header("content-length", Integer.toString(data.length)));
		}
		// 通过字节输出流来发送JSONS数据到服务器;也可以用PrintWriter,但还需要重新new String(data);
		OutputStream outputStream = null;
		try {
			// 打开HTTP连接
			openConnection(url, POST_METHOD);
			// 如果需要发送JSON数据到服务器
			if (handle) {
				outputStream = connection.getOutputStream();
				// 准备发送到服务器的JSON数据
				outputStream.write(data);
				outputStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(outputStream);
		}
		return this;
	}

	/**
	 * 打开字节输入流.每次获取的都是同一个输入流
	 *
	 * @return 成功响应后的字节输入流
	 */
	public InputStream openStream() {
		try {
			InputStream stream = connection != null ? connection.getInputStream() : null;
			closeable = stream;
			return stream;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 打开字符输入流.
	 *
	 * @return 字符输入流.默认调用获取响应后的字符编码, 若未获得, 则用默认的“UTF-8”编码
	 * @see #getCharset()
	 */
	public BufferedReader openReader() {
		return openReader(getCharset());
	}

	/**
	 * 指定字符流编码,并打开字符输入流
	 *
	 * @param charset
	 *            字符编码.必须是支持的字符编码,否则将抛出异常
	 * @return 字符输入流
	 */
	public BufferedReader openReader(String charset) {
		if (closeable != null && closeable instanceof BufferedReader) {
			return (BufferedReader) closeable;
		}
		InputStream stream = openStream();
		if (stream == null) {
			return null;
		}
		Charset encoding = Charset.forName(charset);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
		closeable = reader;
		return reader;
	}

	/**
	 * 获取响应状态码.请在发送请求之后调用,否则将返回 “状态码为{@code -1}”
	 *
	 * @return 响应状态码
	 */
	public int getResponseCode() {
		try {
			return connection == null ? -1 : connection.getResponseCode();
		} catch (IOException e) {
			return -1;
		}
	}

	/**
	 * 当前成功连接到URL对应的远程主机时,获取服务器返回的Cookie值
	 *
	 * @return 成功响应后的Cookie值
	 */
	public String getCookie() {
		return connection != null ? connection.getHeaderField("Set-Cookie") : null;
	}

	/**
	 * 获取响应包含的字符编号
	 *
	 * @return 字符编码
	 */
	public String getCharset() {
		if (connection == null) {
			return "UTF-8";
		}
		String charset = connection.getContentType();
		String[] types = charset.split(" ");
		for (String type : types) {
			if (type.startsWith("charset")) {
				types = type.split("=");
				charset = types[1];
				return charset;
			}
		}
		return "UTF-8";
	}

	/**
	 * 关闭IO流和HTTP连接
	 */
	public void close() {
		reset();
		if (headers != null) {
			headers.clear();
		}
		headers = null;
	}

	/**
	 * 关闭连接,重置为可复用的HttpClient对象,保存了之前构造时的请求头信息.
	 */
	public void reset() {
		IOUtil.close(closeable);
		if (connection != null) {
			connection.disconnect();
		}
		closeable = null;
		connection = null;
	}

	/**
	 * 将普通的字符串转换为application/x-www-form-urlencoded MIME字符串
	 *
	 * @param text
	 *            需要转换的普通字符串
	 * @param charset
	 *            字符编码
	 * @return 转换后的MIME字符串, 如果编码不存在则返回原本的普通字符串
	 */
	public static String encode(String text, String charset) {
		try {
			return text == null || text.isEmpty() ? "" : URLEncoder.encode(text, charset);
		} catch (UnsupportedEncodingException e) {
			return text;
		}
	}

	/**
	 * 将 application/x-www-form-urlencoded MIME字符串转换为普通字符串
	 *
	 * @param text
	 *            需要转换的MIME字符串
	 * @param charset
	 *            字符编码
	 * @return 转换后的普通字符串, 如果编码不存在则返回原本的MIME字符串
	 */
	public static String decode(String text, String charset) {
		try {
			return text == null || text.isEmpty() ? "" : URLDecoder.decode(text, charset);
		} catch (UnsupportedEncodingException e) {
			return text;
		}
	}

	/**
	 * 请求头信息
	 *
	 * @author SCMQ
	 */
	private static class Header {
		/** 请求头名称 */
		private String name;
		/** 请求头名称对应的值 */
		private String value;

		Header(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return name == null ? 31 : (31 + name.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return Objects.equals(name, ((Header) obj).name);
		}
	}
}
